<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import type { TableColumnsType } from 'ant-design-vue'
import { listUserVoByPage, deleteUser } from '@/api/userController'
import type { Dayjs } from 'dayjs'

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
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    key: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    key: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    key: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    key: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
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
        params.createTimeStart = (filterValue.value[0] as Dayjs).format('YYYY-MM-DD HH:mm:ss')
        params.createTimeEnd = (filterValue.value[1] as Dayjs).format('YYYY-MM-DD HH:mm:ss')
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

const handleDelete = async (id: number) => {
  try {
    const res = await deleteUser({ id })
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
  <div class="user-manage-container">
    <div class="table-header">
      <h2>用户管理</h2>
    </div>
    <div class="search-container">
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
        <a-button type="primary" @click="doSearch">搜索</a-button>
        <a-button @click="filterField = null; filterValue = null; doSearch()">重置</a-button>
      </a-space>
    </div>
    <a-divider />
    <a-table
      :columns="columns"
      :data-source="data"
      :loading="loading.table"
      :pagination="false"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'userAvatar'">
          <a-avatar v-if="record.userAvatar" :src="record.userAvatar" />
          <a-avatar v-else>{{ record.userName?.[0] || '无' }}</a-avatar>
        </template>
        <template v-else-if="column.key === 'userRole'">
          <a-tag :color="record.userRole === 1 ? 'blue' : 'green'">
            {{ record.userRole === 1 ? '管理员' : '用户' }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small">编辑</a-button>
            <a-button type="link" danger size="small" @click="handleDelete(record.id!)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
    <div class="pagination-container">
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
</template>

<style scoped>
.user-manage-container {
  padding: 24px;
  min-height: calc(100vh - 64px - 50px);
}

.table-header {
  margin-bottom: 16px;
}

.table-header h2 {
  margin: 0;
}

.search-container {
  margin-bottom: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding-bottom: 24px;
}
</style>
