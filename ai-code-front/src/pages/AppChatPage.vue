<script setup lang="ts">
import { onMounted, reactive, ref, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { getAppVOById, chatToGenCode, deployApp, deleteOneself, adminDelete, updateOneself, adminUpdate } from '@/api/appController'
import { getStaticPreviewUrl } from '@/config/env'
import { useLoginUserStore } from '@/stores/loginUser'
import type { AppVO } from '@/api/typings'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

const appId = computed(() => route.query.appId as string)
const isViewMode = computed(() => route.query.view === '1')
const app = ref<AppVO | null>(null)
const loading = reactive({
  app: false,
  deploy: false,
})

// 判断是否为所有者
const isOwner = computed(() => {
  if (!app.value?.userId || !loginUserStore.loginUser.id) return false
  return String(app.value.userId) === String(loginUserStore.loginUser.id)
})

// 判断是否为管理员
const isAdmin = computed(() => loginUserStore.loginUser.userRole === 1)

// 操作权限（本人或管理员）
const canOperate = computed(() => isOwner.value || isAdmin.value)

// 输入框禁用状态
const inputDisabled = computed(() => !isOwner.value)

// 是否已部署
const isDeployed = computed(() => !!app.value?.deployKey)

// 详情弹窗
const detailVisible = ref(false)
const editVisible = ref(false)
const editLoading = ref(false)
const editForm = reactive({
  id: '',
  appName: '',
})
const originalForm = ref({
  appName: '',
})
const chatMessages = ref<Array<{ role: 'user' | 'assistant'; content: string }>>([])
const inputMessage = ref('')
const isGenerating = ref(false)
const deployUrl = ref('')
const iframeSrc = ref('')
let eventSource: EventSource | null = null

const fetchAppDetail = async () => {
  if (!appId.value) return
  loading.app = true
  try {
    const res = await getAppVOById({ id: appId.value })
    if (res.data.code === 20000 && res.data.data) {
      app.value = res.data.data
      // 用生成目录预览
      if (app.value.codeGenType) {
        iframeSrc.value = getStaticPreviewUrl(app.value.codeGenType, appId.value)
      }
      // 非查看模式，自动发送初始消息
      if (!isViewMode.value && app.value.initPrompt) {
        inputMessage.value = app.value.initPrompt
        sendMessage()
      }
    } else {
      message.error('获取应用详情失败')
    }
  } finally {
    loading.app = false
  }
}

const handleDeploy = async () => {
  if (!appId.value) return
  loading.deploy = true
  try {
    const res = await deployApp({ appId: appId.value })
    if (res.data.code === 20000 && res.data.data) {
      deployUrl.value = res.data.data
      iframeSrc.value = deployUrl.value
      message.success('部署成功')
    } else {
      message.error('部署失败：' + res.data.message)
    }
  } finally {
    loading.deploy = false
  }
}

const handleEdit = () => {
  if (!app.value) return
  editForm.id = String(app.value.id)
  editForm.appName = app.value.appName || ''
  originalForm.value.appName = app.value.appName || ''
  editVisible.value = true
  detailVisible.value = false
}

const handleEditSubmit = async () => {
  editLoading.value = true
  try {
    const res = isAdmin.value
      ? await adminUpdate({ id: editForm.id, appName: editForm.appName })
      : await updateOneself({ id: editForm.id, appName: editForm.appName === originalForm.value.appName ? undefined : editForm.appName })
    if (res.data.code === 20000) {
      message.success('修改成功')
      editVisible.value = false
      fetchAppDetail()
    } else {
      message.error('修改失败：' + res.data.message)
    }
  } catch {
    message.error('修改失败')
  } finally {
    editLoading.value = false
  }
}

const handleDelete = () => {
  if (!app.value) return
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这个应用吗？删除后无法恢复。',
    okText: '确认',
    cancelText: '取消',
    okType: 'danger',
    async onOk() {
      try {
        const res = isAdmin.value
          ? await adminDelete({ deleteRequest: { id: String(app.value!.id) } })
          : await deleteOneself({ deleteRequest: { id: String(app.value!.id) } })
        if (res.data.code === 20000) {
          message.success('删除成功')
          detailVisible.value = false
          router.push('/')
        } else {
          message.error('删除失败：' + res.data.message)
        }
      } catch {
        message.error('删除失败')
      }
    },
  })
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isGenerating.value) return

  const userMessage = inputMessage.value.trim()
  chatMessages.value.push({ role: 'user', content: userMessage })
  inputMessage.value = ''
  isGenerating.value = true

  // 添加一个空的 assistant 消息用于流式填充
  chatMessages.value.push({ role: 'assistant', content: '' })
  const aiMessageIndex = chatMessages.value.length - 1

  generateCode(userMessage, aiMessageIndex)
}

// 生成代码 - 使用 EventSource 处理流式响应
const generateCode = async (userMessage: string, aiMessageIndex: number) => {
  let streamCompleted = false

  try {
    // 构建URL参数
    const params = new URLSearchParams({
      appId: appId.value || '',
      message: userMessage,
    })

    const url = `/api/app/chat/gen/code?${params}`

    // 创建 EventSource 连接
    eventSource = new EventSource(url)
    let fullContent = ''

    // 处理接收到的消息
    eventSource.onmessage = function (event) {
      if (streamCompleted) return

      try {
        // 解析JSON包装的数据
        const parsed = JSON.parse(event.data)
        const content = parsed.d

        // 拼接内容
        if (content !== undefined && content !== null) {
          fullContent += content
          chatMessages.value[aiMessageIndex].content = fullContent
        }
      } catch (error) {
        console.error('解析消息失败:', error)
        handleError(error, aiMessageIndex)
      }
    }

    // 处理done事件
    eventSource.addEventListener('done', function () {
      if (streamCompleted) return

      streamCompleted = true
      isGenerating.value = false
      closeEventSource()

      // 延迟更新预览，确保后端已完成处理
      setTimeout(async () => {
        await fetchAppDetail()
      }, 1000)
    })

    // 处理错误
    eventSource.onerror = function () {
      if (streamCompleted || !isGenerating.value) return
      // 检查是否是正常的连接关闭
      if (eventSource?.readyState === EventSource.CONNECTING) {
        streamCompleted = true
        isGenerating.value = false
        closeEventSource()

        setTimeout(async () => {
          await fetchAppDetail()
        }, 1000)
      } else {
        handleError(new Error('SSE连接错误'), aiMessageIndex)
      }
    }
  } catch (err) {
    handleError(err, aiMessageIndex)
  }
}

// 错误处理函数
const handleError = (error: unknown, aiMessageIndex: number) => {
  console.error('生成代码失败：', error)
  chatMessages.value[aiMessageIndex].content = '抱歉，生成过程中出现了错误，请重试。'
  isGenerating.value = false
  closeEventSource()
  message.error('生成失败，请重试')
}

const closeEventSource = () => {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchAppDetail()
})

onUnmounted(() => {
  closeEventSource()
})
</script>

<template>
  <div class="app-chat-container">
    <!-- 顶部导航 -->
    <div class="top-bar">
      <div class="top-bar-left">
        <a-button type="text" @click="goBack">
          <template #icon>
            <span>&lt;</span>
          </template>
          返回
        </a-button>
        <span class="app-name">{{ app?.appName || '加载中...' }}</span>
      </div>
      <div class="top-bar-right">
        <a-button @click="detailVisible = true">
          应用详情
        </a-button>
        <a-button
          type="primary"
          :loading="loading.deploy"
          :disabled="!app || isDeployed"
          @click="handleDeploy"
        >
          {{ isDeployed ? '已部署' : '部署' }}
        </a-button>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 左侧聊天区域 -->
      <div class="chat-area">
        <div class="chat-messages" id="chatMessages">
          <div
            v-for="(msg, index) in chatMessages"
            :key="index"
            :class="['message', msg.role]"
          >
            <div class="message-avatar">
              <span v-if="msg.role === 'user'">👤</span>
              <span v-else>🤖</span>
            </div>
            <div class="message-content">{{ msg.content }}</div>
          </div>
          <div v-if="isGenerating && chatMessages[chatMessages.length - 1]?.role === 'assistant'" class="message assistant">
            <div class="message-avatar">🤖</div>
            <div class="message-content">
              <a-spin size="small" />
              <span class="typing-text">正在生成代码...</span>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input-area">
          <a-tooltip :title="inputDisabled ? '无法在别人的作品下对话哦~' : ''">
            <a-input
              v-model:value="inputMessage"
              placeholder="输入你的需求，如：帮我生成一个用户管理页面"
              :disabled="isGenerating || inputDisabled"
              @pressEnter="sendMessage"
            />
          </a-tooltip>
          <a-button
            type="primary"
            :disabled="!inputMessage.trim() || isGenerating || inputDisabled"
            @click="sendMessage"
          >
            发送
          </a-button>
        </div>
      </div>

      <!-- 右侧预览区域 -->
      <div class="preview-area">
        <div class="preview-header">
          <span>网站预览</span>
          <a-button v-if="deployUrl" type="link" :href="deployUrl" target="_blank">
            新窗口打开
          </a-button>
        </div>
        <div class="preview-content">
          <div v-if="isGenerating" class="preview-loading">
            <a-spin size="large" />
            <p>正在生成网站...</p>
          </div>
          <iframe
            v-else-if="iframeSrc"
            :src="iframeSrc"
            frameborder="0"
            class="preview-iframe"
          ></iframe>
          <div v-else class="preview-placeholder">
            <p>输入需求，让 AI 为你生成网站</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 应用详情弹窗 -->
    <a-drawer
      v-model:open="detailVisible"
      title="应用详情"
      :width="360"
      :closable="true"
    >
      <div v-if="app" class="app-detail">
        <!-- 应用基础信息 -->
        <div class="detail-section">
          <h4 class="detail-title">基础信息</h4>
          <div class="detail-item">
            <span class="detail-label">创建者：</span>
            <span class="detail-value">
              <a-avatar v-if="app.user?.userAvatar" :src="app.user.userAvatar" :size="24" />
              <a-avatar v-else :size="24">{{ app.user?.userName?.[0] || '无' }}</a-avatar>
              <span style="margin-left: 8px">{{ app.user?.userName || app.user?.userAccount || '未知' }}</span>
            </span>
          </div>
          <div class="detail-item">
            <span class="detail-label">创建时间：</span>
            <span class="detail-value">{{ app.createTime ? app.createTime.replace('T', ' ') : '-' }}</span>
          </div>
        </div>

        <!-- 操作栏 -->
        <div v-if="canOperate" class="detail-section">
          <h4 class="detail-title">操作</h4>
          <div class="detail-actions">
            <a-button type="primary" @click="handleEdit">
              修改
            </a-button>
            <a-button danger @click="handleDelete">
              删除
            </a-button>
          </div>
        </div>
      </div>
    </a-drawer>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:open="editVisible"
      title="修改应用"
      :confirm-loading="editLoading"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleEditSubmit"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="应用名称" name="appName">
          <a-input v-model:value="editForm.appName" placeholder="请输入应用名称" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.app-chat-container {
  height: calc(100vh - 64px - 50px);
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
}

.top-bar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.top-bar-left .app-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.chat-area {
  width: 40%;
  min-width: 400px;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-right: 1px solid #e8e8e8;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 85%;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message.assistant {
  align-self: flex-start;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
  color: #fff;
}

.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.message.user .message-content {
  background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
  color: #fff;
}

.message.assistant .message-content {
  background: #f5f5f5;
  color: #333;
}

.chat-input-area {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid #e8e8e8;
  background: #fff;
}

.chat-input-area .ant-input {
  flex: 1;
}

.preview-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  font-weight: 500;
}

.preview-content {
  flex: 1;
  padding: 16px;
  background: #f0f0f0;
  overflow: hidden;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.preview-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 8px;
  color: #999;
}

.preview-loading {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 8px;
  color: #666;
}

.preview-loading p {
  margin-top: 16px;
}

.typing-text {
  margin-left: 8px;
  color: #999;
}

.app-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 16px;
}

.detail-section:last-child {
  border-bottom: none;
}

.detail-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.detail-label {
  color: #666;
  font-size: 14px;
  min-width: 70px;
}

.detail-value {
  color: #333;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.detail-actions {
  display: flex;
  gap: 12px;
}
</style>
