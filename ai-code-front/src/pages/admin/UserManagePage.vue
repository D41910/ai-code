<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import type { TableColumnsType } from 'ant-design-vue'
import { listUserVoByPage, deleteUser, updateUser } from '@/api/userController'
import type { Dayjs } from 'dayjs'
import { copyToClipboard } from '@/utils/copy'

interface UserVO {
  id?: number
  userAccount?: string
  userName?: string
  userAvatar?: string
  userProfile?: string
  userRole?: number
  createTime?: string
  updateTime?: string
}

type FilterField = 'userAccount' | 'userName' | 'userProfile' | 'userRole' | 'createTime'

const columns: TableColumnsType<UserVO> = [
  {
    title: '账号',
    dataIndex: 'userAccount',
    key: 'userAccount',
    width: 100,
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    key: 'userName',
    width: 100,
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    key: 'userAvatar',
    width: 60,
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    key: 'userProfile',
    width: 150,
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    key: 'userRole',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 130,
    customRender: ({ text }: { text: string }) => {
      if (!text) return '-'
      return text.replace('T', ' ')
    },
  },
  {
    title: '操作',
    key: 'action',
    width: 140,
    fixed: 'right',
  },
]

const data = ref<UserVO[]>([])
const total = ref(0)
const current = ref(1)
const pageSize = ref(10)

const filterField = ref<FilterField | null>('userAccount')
const filterValue = ref<string | number | [Dayjs, Dayjs] | null>(null)

const filterFieldOptions = [
  { label: '账号', value: 'userAccount' },
  { label: '用户名', value: 'userName' },
  { label: '简介', value: 'userProfile' },
  { label: '用户角色', value: 'userRole' },
  { label: '创建时间', value: 'createTime' },
]

const roleOptions = [
  { label: '管理员', value: 1 },
  { label: '用户', value: 0 },
]

const loading = reactive({
  table: false,
})

// 编辑弹窗
const editVisible = ref(false)
const editLoading = ref(false)
const editForm = reactive({
  id: 0,
  userName: '',
  userAvatar: '',
  userProfile: '',
  userRole: 0 as number,
  userPassword: '',
})
const originalForm = ref({
  userName: '',
  userAvatar: '',
  userProfile: '',
  userRole: 0 as number,
})

const fetchData = async () => {
  loading.table = true
  try {
    const params: Record<string, any> = {
      pageNum: current.value,
      paseSize: pageSize.value,
    }

    if (filterField.value && filterValue.value !== null && filterValue.value !== '') {
      if (filterField.value === 'userRole') {
        params.userRole = filterValue.value
      } else if (filterField.value === 'createTime' && Array.isArray(filterValue.value)) {
        params.startTime = (filterValue.value[0] as Dayjs).format('YYYY-MM-DDTHH:mm:ss')
        params.endTime = (filterValue.value[1] as Dayjs).format('YYYY-MM-DDTHH:mm:ss')
      } else {
        params[filterField.value] = filterValue.value
      }
    }

    const res = await listUserVoByPage(params)
    if (res.data.code === 20000 && res.data.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.totalRow ?? 0
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } finally {
    loading.table = false
  }
}

const doSearch = () => {
  current.value = 1
  fetchData()
}

const handleFilterFieldChange = () => {
  filterValue.value = null
}

const handleEdit = (record: UserVO) => {
  originalForm.value = {
    userName: record.userName || '',
    userAvatar: record.userAvatar || '',
    userProfile: record.userProfile || '',
    userRole: record.userRole ?? 0,
  }
  editForm.id = record.id!
  editForm.userName = record.userName || ''
  editForm.userAvatar = record.userAvatar || ''
  editForm.userProfile = record.userProfile || ''
  editForm.userRole = record.userRole ?? 0
  editForm.userPassword = ''
  editVisible.value = true
}

const handleEditSubmit = async () => {
  editLoading.value = true
  try {
    const res = await updateUser({
      id: editForm.id,
      userName: editForm.userName === originalForm.value.userName ? null : editForm.userName,
      userAvatar: editForm.userAvatar === originalForm.value.userAvatar ? null : editForm.userAvatar,
      userProfile: editForm.userProfile === originalForm.value.userProfile ? null : editForm.userProfile,
      userRole: editForm.userRole === originalForm.value.userRole ? null : editForm.userRole,
      userPassword: editForm.userPassword === '' ? null : editForm.userPassword,
    })
    if (res.data.code === 20000) {
      message.success('修改成功')
      editVisible.value = false
      fetchData()
    } else {
      message.error('修改失败，' + res.data.message)
    }
  } catch {
    message.error('修改失败')
  } finally {
    editLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    const res = await deleteUser({ deleteRequest: { id } })
    if (res.data.code === 20000) {
      message.success('删除成功')
      fetchData()
    } else {
      message.error('删除失败，' + res.data.message)
    }
  } catch {
    message.error('删除失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="manage-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">用户管理</h1>
        <p class="page-desc">管理系统用户信息</p>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-card">
      <a-space :size="12">
        <a-select
          v-model:value="filterField"
          placeholder="选择筛选字段"
          style="width: 140px"
          :options="filterFieldOptions"
          allow-clear
          @change="handleFilterFieldChange"
        />
        <a-input
          v-if="filterField && filterField !== 'userRole' && filterField !== 'createTime'"
          v-model:value="filterValue"
          :placeholder="filterFieldOptions.find(f => f.value === filterField)?.label + '...'"
          style="width: 160px"
          allow-clear
          @pressEnter="doSearch"
        />
        <a-select
          v-if="filterField === 'userRole'"
          v-model:value="filterValue"
          placeholder="选择角色"
          style="width: 140px"
          :options="roleOptions"
          allow-clear
        />
        <a-range-picker
          v-if="filterField === 'createTime'"
          v-model:value="filterValue"
          style="width: 280px"
          show-time
          format="YYYY-MM-DD HH:mm:ss"
          :placeholder="['开始时间', '结束时间']"
        />
        <a-button class="btn-primary" @click="doSearch">搜索</a-button>
        <a-button class="btn-default" @click="filterField = null; filterValue = null; doSearch()">重置</a-button>
      </a-space>
    </div>

    <!-- 表格区域 -->
    <div class="table-card">
      <a-table
        :columns="columns"
        :data-source="data"
        :loading="loading.table"
        :pagination="false"
        :scroll="{ x: 760 }"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'userAccount'">
            <a-tooltip :title="record.userAccount" @click="copyToClipboard(record.userAccount || '')">
              <span class="cell-text">{{ record.userAccount }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'userName'">
            <a-tooltip :title="record.userName" @click="copyToClipboard(record.userName || '')">
              <span class="cell-text">{{ record.userName }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'userAvatar'">
            <a-avatar v-if="record.userAvatar" :src="record.userAvatar" />
            <a-avatar v-else class="default-avatar">{{ record.userName?.[0] || '无' }}</a-avatar>
          </template>
          <template v-else-if="column.key === 'userProfile'">
            <a-tooltip :title="record.userProfile" @click="copyToClipboard(record.userProfile || '')">
              <span class="cell-text">{{ record.userProfile }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'userRole'">
            <a-tag :color="record.userRole === 1 ? 'blue' : 'green'">
              {{ record.userRole === 1 ? '管理员' : '用户' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button class="action-btn" @click="handleEdit(record)">编辑</a-button>
              <a-button class="action-btn danger" @click="handleDelete(record.id!)">删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
      <div class="pagination-wrapper">
        <a-pagination
          v-model:current="current"
          v-model:page-size="pageSize"
          :total="total"
          :show-size-changer="true"
          :show-quick-jumper="true"
          :page-size-options="['10', '20', '50', '100']"
          :show-total="(total: number) => `共 ${total} 条`"
          @change="fetchData"
        />
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:open="editVisible"
      title="编辑用户"
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
        <a-form-item label="用户角色" name="userRole">
          <a-select v-model:value="editForm.userRole" :options="roleOptions" />
        </a-form-item>
        <a-form-item label="密码（不修改请留空）" name="userPassword"
          :rules="[{ min: 8, message: '密码不能小于 8 位' }]">
          <a-input-password v-model:value="editForm.userPassword" placeholder="请输入新密码" autocomplete="new-password" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.manage-page {
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

/* 搜索区域 */
.search-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

:deep(.ant-select:not(.ant-select-disabled):hover .ant-select-selector) {
  border-color: #00bcd4;
}

:deep(.ant-select-focused .ant-select-selector) {
  border-color: #00bcd4 !important;
  box-shadow: 0 0 0 2px rgba(0, 188, 212, 0.1) !important;
}

:deep(.ant-input:hover) {
  border-color: #00bcd4;
}

:deep(.ant-input:focus) {
  border-color: #00bcd4;
  box-shadow: 0 0 0 2px rgba(0, 188, 212, 0.1);
}

.btn-primary {
  color: #fff;
  background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
  border: none;
  border-radius: 6px;
}

.btn-primary:hover {
  opacity: 0.9;
}

.btn-default {
  border-radius: 6px;
}

.btn-default:hover {
  color: #00838f;
  border-color: #00838f;
}

/* 表格区域 */
.table-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.cell-text {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: #333;
}

.cell-text:hover {
  color: #00bcd4;
}

.default-avatar {
  background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
  color: #fff;
}

.action-btn {
  color: #00838f;
  font-size: 13px;
  padding: 4px 8px;
  height: auto;
}

.action-btn:hover {
  color: #006064;
  background: rgba(0, 131, 143, 0.1);
}

.action-btn.danger {
  color: #ff4d4f;
}

.action-btn.danger:hover {
  color: #ff7875;
  background: rgba(255, 77, 79, 0.1);
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-bottom: 8px;
}

:deep(.ant-pagination-item-active) {
  border-color: #00bcd4;
}

:deep(.ant-pagination-item-active a) {
  color: #00bcd4;
}
</style>