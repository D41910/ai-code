<script setup lang="ts">
import { onMounted, reactive, ref, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getAppVOById, chatToGenCode, deployApp } from '@/api/appController'
import type { AppVO } from '@/api/typings'

const route = useRoute()
const router = useRouter()

const appId = computed(() => route.query.appId as string)
const app = ref<AppVO | null>(null)
const loading = reactive({
  app: false,
  deploy: false,
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
      if (app.value.deployKey) {
        deployUrl.value = `${window.location.origin}/${app.value.deployKey}/`
        iframeSrc.value = deployUrl.value
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

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isGenerating.value) return

  const userMessage = inputMessage.value.trim()
  chatMessages.value.push({ role: 'user', content: userMessage })
  inputMessage.value = ''
  isGenerating.value = true

  // 添加一个空的 assistant 消息用于流式填充
  chatMessages.value.push({ role: 'assistant', content: '' })

  try {
    // 使用 SSE 获取流式响应
    const url = `/api/app/chat/gen/code?appId=${appId.value}&message=${encodeURIComponent(userMessage)}`
    eventSource = new EventSource(url)

    eventSource.onmessage = (event) => {
      const lastMsg = chatMessages.value[chatMessages.value.length - 1]
      if (event.data === 'done' || event.data === '') {
        // 完成
      } else {
        try {
          const data = JSON.parse(event.data)
          if (data.d) {
            lastMsg.content += data.d
          }
        } catch {
          lastMsg.content += event.data
        }
      }
    }

    eventSource.onerror = () => {
      message.error('连接失败，请重试')
      isGenerating.value = false
      closeEventSource()
    }

    eventSource.onopen = () => {
      // 连接成功
    }
  } catch (err) {
    message.error('发送消息失败')
    isGenerating.value = false
    // 移除最后添加的空消息
    chatMessages.value.pop()
    if (chatMessages.value[chatMessages.value.length - 1]?.role === 'user') {
      chatMessages.value.pop()
    }
  }
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
        <a-button
          type="primary"
          :loading="loading.deploy"
          :disabled="!app"
          @click="handleDeploy"
        >
          部署
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
          <a-input
            v-model:value="inputMessage"
            placeholder="输入你的需求，如：帮我生成一个用户管理页面"
            :disabled="isGenerating"
            @pressEnter="sendMessage"
          />
          <a-button
            type="primary"
            :disabled="!inputMessage.trim() || isGenerating"
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
          <iframe
            v-if="iframeSrc"
            :src="iframeSrc"
            frameborder="0"
            class="preview-iframe"
          ></iframe>
          <div v-else class="preview-placeholder">
            <p>点击「部署」按钮后即可预览网站</p>
          </div>
        </div>
      </div>
    </div>
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

.typing-text {
  margin-left: 8px;
  color: #999;
}
</style>
