<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { TableColumnsType } from 'ant-design-vue'
import { adminListByPage, adminDelete, adminUpdate } from '@/api/appController'
import type { AppVO } from '@/api/typings'
import { copyToClipboard } from '@/utils/copy'
import { formatCodeGenType } from '@/utils/codeGenTypes'

const router = useRouter()

const columns: TableColumnsType<AppVO> = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 60,
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    key: 'appName',
    width: 120,
  },
  {
    title: '封面',
    dataIndex: 'cover',
    key: 'cover',
    width: 60,
  },
  {
    title: '初始化Prompt',
    dataIndex: 'initPrompt',
    key: 'initPrompt',
    width: 150,
  },
  {
    title: '代码生成类型',
    dataIndex: 'codeGenType',
    key: 'codeGenType',
    width: 100,
  },
  {
    title: '部署标识',
    dataIndex: 'deployKey',
    key: 'deployKey',
    width: 80,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    key: 'priority',
    width: 60,
  },
  {
    title: '创建用户',
    key: 'user',
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
    width: 160,
    fixed: 'right',
  },
]

const data = ref<AppVO[]>([])
const total = ref(0)
const current = ref(1)
const pageSize = ref(10)

const filterAppName = ref<string | null>(null)

const loading = reactive({
  table: false,
})

const fetchData = async () => {
  loading.table = true
  try {
    const params: Record<string, any> = {
      pageNum: current.value,
      paseSize: pageSize.value,
    }
    if (filterAppName.value) {
      params.appName = filterAppName.value
    }
    const res = await adminListByPage(params)
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

const handleReset = () => {
  filterAppName.value = null
  doSearch()
}

// 编辑弹窗
const editVisible = ref(false)
const editLoading = ref(false)
const editForm = reactive({
  id: '',
  appName: '',
  cover: '',
  priority: 0 as number,
})
const originalForm = ref({
  appName: '',
  cover: '',
  priority: 0 as number,
})

const handleEdit = (record: AppVO) => {
  originalForm.value = {
    appName: record.appName || '',
    cover: record.cover || '',
    priority: record.priority ?? 0,
  }
  editForm.id = String(record.id!)
  editForm.appName = record.appName || ''
  editForm.cover = record.cover || ''
  editForm.priority = record.priority ?? 0
  editVisible.value = true
}

const handleEditSubmit = async () => {
  editLoading.value = true
  try {
    const res = await adminUpdate({
      id: editForm.id,
      appName: editForm.appName === originalForm.value.appName ? null : editForm.appName,
      cover: editForm.cover === originalForm.value.cover ? null : editForm.cover,
      priority: editForm.priority === originalForm.value.priority ? null : editForm.priority,
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

const handleDelete = async (id: string) => {
  try {
    const res = await adminDelete({ deleteRequest: { id } })
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

const handleChat = (record: AppVO) => {
  router.push({ path: '/app/chat', query: { appId: String(record.id) } })
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
        <h1 class="page-title">应用管理</h1>
        <p class="page-desc">管理系统应用信息</p>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-card">
      <a-space :size="12">
        <a-input
          v-model:value="filterAppName"
          placeholder="应用名称..."
          style="width: 200px"
          allow-clear
          @pressEnter="doSearch"
        />
        <a-button class="btn-primary" @click="doSearch">搜索</a-button>
        <a-button class="btn-default" @click="handleReset">重置</a-button>
      </a-space>
    </div>

    <!-- 表格区域 -->
    <div class="table-card">
      <a-table
        :columns="columns"
        :data-source="data"
        :loading="loading.table"
        :pagination="false"
        :scroll="{ x: 900 }"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'id'">
            <a-tooltip :title="record.id" @click="copyToClipboard(String(record.id) || '')">
              <span class="cell-text">{{ record.id }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'appName'">
            <a-tooltip :title="record.appName" @click="copyToClipboard(record.appName || '')">
              <span class="cell-text">{{ record.appName }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'cover'">
            <a-avatar v-if="record.cover" :src="record.cover" shape="square" />
            <a-avatar v-else shape="square" class="default-avatar">无</a-avatar>
          </template>
          <template v-else-if="column.key === 'initPrompt'">
            <a-tooltip :title="record.initPrompt" @click="copyToClipboard(record.initPrompt || '')">
              <span class="cell-text">{{ record.initPrompt }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'codeGenType'">
            <span class="cell-text">{{ formatCodeGenType(record.codeGenType) }}</span>
          </template>
          <template v-else-if="column.key === 'deployKey'">
            <a-tooltip :title="record.deployKey" @click="copyToClipboard(record.deployKey || '')">
              <span class="cell-text">{{ record.deployKey }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'priority'">
            <span class="cell-text">{{ record.priority }}</span>
          </template>
          <template v-else-if="column.key === 'user'">
            <span class="cell-text">{{ record.user?.userName || record.user?.userAccount || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button class="action-btn" @click="handleChat(record)">聊天</a-button>
              <a-button class="action-btn" @click="handleEdit(record)">编辑</a-button>
              <a-button class="action-btn danger" @click="handleDelete(String(record.id))">删除</a-button>
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
      title="编辑应用"
      :confirm-loading="editLoading"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleEditSubmit"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="应用名称" name="appName">
          <a-input v-model:value="editForm.appName" placeholder="请输入应用名称" />
        </a-form-item>
        <a-form-item label="封面" name="cover">
          <a-input v-model:value="editForm.cover" placeholder="请输入封面URL" />
        </a-form-item>
        <a-form-item label="优先级" name="priority">
          <a-input-number v-model:value="editForm.priority" :min="0" style="width: 100%" />
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