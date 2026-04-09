<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { getLoginUserVo, updateUser } from '@/api/userController'

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
  editForm.value = {
    userName: userInfo.value.userName,
    userAvatar: userInfo.value.userAvatar,
    userProfile: userInfo.value.userProfile,
  }
  editVisible.value = true
}

const handleEditSubmit = async () => {
  editLoading.value = true
  try {
    const res = await updateUser({
      id: userInfo.value.id,
      userName: editForm.value.userName,
      userAvatar: editForm.value.userAvatar,
      userProfile: editForm.value.userProfile,
    })
    if (res.data.code === 20000) {
      message.success('修改成功')
      editVisible.value = false
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
  <div class="user-info-container">
    <div class="user-info-card">
      <div class="card-header">
        <h2>个人信息</h2>
      </div>

      <div class="info-content">
        <div class="avatar-section">
          <a-avatar :size="100" :src="userInfo.userAvatar">
            {{ userInfo.userName?.[0] || '无' }}
          </a-avatar>
        </div>

        <a-descriptions :column="1" bordered class="info-list">
          <a-descriptions-item label="账号">
            {{ userInfo.userAccount || '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="用户名">
            {{ userInfo.userName || '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="头像">
            {{ userInfo.userAvatar || '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="简介">
            {{ userInfo.userProfile || '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="角色">
            <a-tag :color="userInfo.userRole === 1 ? 'blue' : 'green'">
              {{ userInfo.userRole === 1 ? '管理员' : '用户' }}
            </a-tag>
          </a-descriptions-item>
        </a-descriptions>

        <div class="action-section">
          <a-button type="primary" @click="handleEdit">编辑资料</a-button>
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
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.user-info-container {
  padding: 24px;
  display: flex;
  justify-content: center;
  min-height: calc(100vh - 64px - 50px);
}

.user-info-card {
  width: 500px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.card-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.info-content {
  padding: 24px;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.info-list {
  margin-bottom: 24px;
}

.action-section {
  display: flex;
  justify-content: center;
}
</style>
