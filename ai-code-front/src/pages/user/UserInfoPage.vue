<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { getLoginUserVo, updateUserOneself } from '@/api/userController'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const userInfo = ref({
  id: 0,
  userAccount: '',
  userName: '',
  userAvatar: '',
  userProfile: '',
  userRole: 0 as number,
})

// 编辑弹窗
const editVisible = ref(false)
const editLoading = ref(false)
const editForm = ref({
  userName: '',
  userAvatar: '',
  userProfile: '',
  userPassword: '',
})
// 用于记录打开编辑弹窗时的原始值，不修改则传null
const originalForm = ref({
  userName: '',
  userAvatar: '',
  userProfile: '',
})

const roleOptions = [
  { label: '管理员', value: 1 },
  { label: '用户', value: 0 },
]

const fetchUserInfo = async () => {
  try {
    const res = await getLoginUserVo()
    if (res.data.code === 20000 && res.data.data) {
      const data = res.data.data
      userInfo.value = {
        id: data.id || 0,
        userAccount: data.userAccount || '',
        userName: data.userName || '',
        userAvatar: data.userAvatar || '',
        userProfile: data.userProfile || '',
        userRole: data.userRole ?? 0,
      }
    }
  } catch {
    message.error('获取用户信息失败')
  }
}

const handleEdit = () => {
  originalForm.value = {
    userName: userInfo.value.userName,
    userAvatar: userInfo.value.userAvatar,
    userProfile: userInfo.value.userProfile,
  }
  editForm.value = {
    userName: userInfo.value.userName,
    userAvatar: userInfo.value.userAvatar,
    userProfile: userInfo.value.userProfile,
    userPassword: '',
  }
  editVisible.value = true
}

const handleEditSubmit = async () => {
  editLoading.value = true
  try {
    // 不修改传null，修改为空字符串传空字符串
    const res = await updateUserOneself({
      id: userInfo.value.id,
      userName: editForm.value.userName === originalForm.value.userName ? null : editForm.value.userName,
      userAvatar: editForm.value.userAvatar === originalForm.value.userAvatar ? null : editForm.value.userAvatar,
      userProfile: editForm.value.userProfile === originalForm.value.userProfile ? null : editForm.value.userProfile,
      userPassword: editForm.value.userPassword === '' ? null : editForm.value.userPassword,
    })
    if (res.data.code === 20000) {
      message.success('修改成功')
      editVisible.value = false
      editForm.value.userPassword = ''
      await loginUserStore.fetchLoginUser()
      await fetchUserInfo()
    } else {
      message.error('修改失败，' + res.data.message)
    }
  } catch {
    message.error('修改失败')
  } finally {
    editLoading.value = false
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<template>
  <div class="user-info-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">个人中心</h1>
        <p class="page-desc">查看和修改个人信息</p>
      </div>
    </div>

    <!-- 用户信息卡片 -->
    <div class="info-card">
      <div class="card-header">
        <h2>个人信息</h2>
        <a-button class="edit-btn" @click="handleEdit">编辑资料</a-button>
      </div>

      <div class="info-content">
        <div class="avatar-section">
          <a-avatar v-if="userInfo.userAvatar" :src="userInfo.userAvatar" :size="100" class="user-avatar" />
          <a-avatar v-else :size="100" class="user-avatar default">
            {{ userInfo.userName?.[0] || '无' }}
          </a-avatar>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">账号</span>
            <span class="info-value">{{ userInfo.userAccount || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">用户名</span>
            <span class="info-value">{{ userInfo.userName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">简介</span>
            <span class="info-value">{{ userInfo.userProfile || '暂无简介' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">角色</span>
            <a-tag :color="userInfo.userRole === 1 ? 'blue' : 'green'">
              {{ userInfo.userRole === 1 ? '管理员' : '用户' }}
            </a-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:open="editVisible"
      title="编辑资料"
      :confirm-loading="editLoading"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleEditSubmit"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="用户名" name="userName">
          <a-input v-model:value="editForm.userName" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item label="头像" name="userAvatar">
          <a-input v-model:value="editForm.userAvatar" placeholder="请输入头像URL" />
        </a-form-item>
        <a-form-item label="简介" name="userProfile">
          <a-textarea v-model:value="editForm.userProfile" placeholder="请输入简介" :rows="3" />
        </a-form-item>
        <a-form-item label="新密码（不修改请留空）" name="userPassword"
          :rules="[{ min: 8, message: '密码不能小于 8 位' }]">
          <a-input-password v-model:value="editForm.userPassword" placeholder="请输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.user-info-page {
  padding: 24px;
  min-height: calc(100vh - 64px - 50px);
  background: #f0f2f5;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}

.header-content {
  background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
  padding: 24px 32px;
  border-radius: 12px;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #fff;
}

.page-desc {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
}

/* 用户信息卡片 */
.info-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.edit-btn {
  color: #fff;
  background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
  border: none;
  border-radius: 6px;
}

.edit-btn:hover {
  opacity: 0.9;
}

.info-content {
  padding: 32px 24px;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
}

.user-avatar {
  border: 4px solid rgba(0, 188, 212, 0.1);
}

.user-avatar.default {
  background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
  color: #fff;
  font-size: 36px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 13px;
  color: #999;
}

.info-value {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

@media (max-width: 600px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>