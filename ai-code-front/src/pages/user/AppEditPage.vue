<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getAppVOById, updateOneself, adminUpdate } from '@/api/appController'
import { useLoginUserStore } from '@/stores/loginUser'
import { copyToClipboard } from '@/utils/copy'
import type { AppVO } from '@/api/typings'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

const appId = computed(() => route.query.id as string)
const isAdmin = computed(() => loginUserStore.loginUser?.userRole === 1)
const app = ref<AppVO | null>(null)
const loading = reactive({
  page: false,
  save: false,
})

const formState = reactive({
  appName: '',
  cover: '',
  priority: 0 as number,
})
const originalForm = ref({
  appName: '',
  cover: '',
  priority: 0 as number,
})

const isEditMode = ref(false)

// 访问链接
const deployUrl = computed(() => {
  if (app.value?.deployKey) {
    return `http://localhost/${app.value.deployKey}`
  }
  return ''
})

const handleCopyUrl = () => {
  if (deployUrl.value) {
    copyToClipboard(deployUrl.value)
    message.success('链接已复制')
  }
}

const fetchAppDetail = async () => {
  if (!appId.value) {
    // 新建模式
    isEditMode.value = false
    return
  }
  isEditMode.value = true
  loading.page = true
  try {
    const res = isAdmin.value
      ? await getAppVOById({ id: appId.value })
      : await getAppVOById({ id: appId.value })
    if (res.data.code === 20000 && res.data.data) {
      app.value = res.data.data
      formState.appName = app.value.appName || ''
      formState.cover = app.value.cover || ''
      formState.priority = app.value.priority ?? 0
      originalForm.value = {
        appName: formState.appName,
        cover: formState.cover,
        priority: formState.priority,
      }
    } else {
      message.error('获取应用详情失败')
    }
  } finally {
    loading.page = false
  }
}

const handleSubmit = async () => {
  loading.save = true
  try {
    let res
    if (isAdmin.value) {
      res = await adminUpdate({
        id: appId.value,
        appName: formState.appName === originalForm.value.appName ? null : formState.appName,
        cover: formState.cover === originalForm.value.cover ? null : formState.cover,
        priority: formState.priority === originalForm.value.priority ? null : formState.priority,
      })
    } else {
      res = await updateOneself({
        id: appId.value,
        appName: formState.appName === originalForm.value.appName ? null : formState.appName,
      })
    }
    if (res.data.code === 20000) {
      message.success('保存成功')
      router.back()
    } else {
      message.error('保存失败：' + res.data.message)
    }
  } catch {
    message.error('保存失败')
  } finally {
    loading.save = false
  }
}

const handleCancel = () => {
  router.back()
}

const handleEnterChat = () => {
  router.push({ path: '/app/chat', query: { appId: String(appId.value) } })
}

onMounted(() => {
  fetchAppDetail()
})
</script>

<template>
  <div class="app-edit-container">
    <div class="edit-header">
      <h2>{{ isEditMode ? '编辑应用' : '新建应用' }}</h2>
    </div>
    <div class="edit-content">
      <a-spin :spinning="loading.page">
        <a-form :model="formState" layout="vertical" class="edit-form">
          <a-form-item label="应用名称" name="appName">
            <a-input
              v-model:value="formState.appName"
              placeholder="请输入应用名称"
              :maxlength="50"
              show-count
            />
          </a-form-item>

          <a-form-item v-if="isAdmin" label="封面" name="cover">
            <a-input
              v-model:value="formState.cover"
              placeholder="请输入封面图片URL"
            />
          </a-form-item>

          <a-form-item v-if="isAdmin" label="优先级" name="priority">
            <a-input-number
              v-model:value="formState.priority"
              :min="0"
              :max="999"
              style="width: 100%"
            />
          </a-form-item>

          <a-form-item v-if="app?.initPrompt" label="初始化Prompt">
            <a-textarea
              :value="app.initPrompt"
              :rows="4"
              disabled
            />
          </a-form-item>

          <a-form-item v-if="app?.codeGenType" label="代码生成类型">
            <a-input :value="app.codeGenType" disabled />
          </a-form-item>

          <a-form-item v-if="app?.deployKey" label="部署密钥">
            <a-input :value="app.deployKey" disabled />
          </a-form-item>
        </a-form>
      </a-spin>
    </div>

    <!-- 应用信息 -->
    <div v-if="app" class="app-info-card">
      <div class="app-info-header">
        <span class="app-info-title">应用信息</span>
      </div>
      <div class="app-info-grid">
        <div class="app-info-item">
          <span class="item-label">应用ID</span>
          <span class="item-value">{{ app.id || '-' }}</span>
        </div>
        <div class="app-info-item">
          <span class="item-label">创建者</span>
          <span class="item-value">
            <a-avatar v-if="app.user?.userAvatar" :src="app.user.userAvatar" :size="20" />
            <a-avatar v-else :size="20">{{ app.user?.userName?.[0] || '无' }}</a-avatar>
            <span style="margin-left: 6px">{{ app.user?.userName || app.user?.userAccount || '未知' }}</span>
          </span>
        </div>
        <div class="app-info-item">
          <span class="item-label">创建时间</span>
          <span class="item-value">{{ app.createTime ? app.createTime.replace('T', ' ') : '-' }}</span>
        </div>
        <div class="app-info-item">
          <span class="item-label">更新时间</span>
          <span class="item-value">{{ app.updateTime ? app.updateTime.replace('T', ' ') : '-' }}</span>
        </div>
        <div class="app-info-item">
          <span class="item-label">部署时间</span>
          <span class="item-value">{{ app.deployedTime ? app.deployedTime.replace('T', ' ') : '-' }}</span>
        </div>
        <div class="app-info-item">
          <span class="item-label">访问链接</span>
          <span class="item-value">
            <a v-if="deployUrl" :href="deployUrl" target="_blank" class="deploy-link">
              {{ deployUrl }}
            </a>
            <span v-else class="no-deploy">未部署</span>
          </span>
        </div>
      </div>
    </div>

    <div class="edit-footer">
      <a-space :size="12">
        <a-button @click="handleCancel">取消</a-button>
        <a-button @click="handleEnterChat">进入对话</a-button>
        <a-button type="primary" :loading="loading.save" @click="handleSubmit">
          保存
        </a-button>
      </a-space>
    </div>
  </div>
</template>

<style scoped>
.app-edit-container {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.edit-header {
  margin-bottom: 24px;
}

.edit-header h2 {
  margin: 0;
}

.edit-content {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
}

.edit-form {
  max-width: 500px;
}

.app-info-card {
  margin-top: 16px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.app-info-header {
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.app-info-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.app-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  border-top: 1px solid #f0f0f0;
}

.app-info-item {
  padding: 16px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  border-right: 1px solid #f0f0f0;
}

.app-info-item:nth-child(2n) {
  border-right: none;
}

.app-info-item:nth-last-child(-n+2) {
  border-bottom: none;
}

.item-label {
  color: #666;
  font-size: 14px;
  min-width: 80px;
}

.item-value {
  color: #333;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.deploy-link {
  color: #1890ff;
  text-decoration: none;
}

.deploy-link:hover {
  color: #40a9ff;
}

.no-deploy {
  color: #999;
}

.edit-footer {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e8e8e8;
  display: flex;
  justify-content: flex-end;
}
</style>
