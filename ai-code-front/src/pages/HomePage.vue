<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listOneselfByPage, listFeaturedByPage, addApp } from '@/api/appController'
import { useLoginUserStore } from '@/stores/loginUser'
import type { AppVO, AppAddDTO } from '@/api/typings'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const isLogin = computed(() => loginUserStore.loginUser?.userRole !== undefined)

// 发起应用创建
const createAppModalVisible = ref(false)
const createLoading = ref(false)
const createForm = reactive<AppAddDTO>({
  initPrompt: '',
})

const handleMainAction = () => {
  if (!isLogin.value) {
    router.push('/user/login')
    return
  }
  if (!createForm.initPrompt?.trim()) {
    message.warning('请输入应用描述')
    return
  }
  createAppModalVisible.value = true
}

const handleCreateApp = async () => {
  if (!createForm.initPrompt?.trim()) {
    message.warning('请输入应用描述')
    return
  }
  createLoading.value = true
  try {
    const res = await addApp(createForm)
    if (res.data.code === 20000 && res.data.data) {
      message.success('创建成功')
      createAppModalVisible.value = false
      createForm.initPrompt = ''
      // 跳转到聊天页面
      router.push({ path: '/app/chat', query: { appId: String(res.data.data) } })
      // 刷新我的应用列表
      fetchMyApps()
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch {
    message.error('创建失败')
  } finally {
    createLoading.value = false
  }
}

// 我的应用列表
const myApps = ref<AppVO[]>([])
const myAppsTotal = ref(0)
const myAppsLoading = reactive({ table: false })
const myAppsCurrent = ref(1)
const myAppsPageSize = ref(4)

const fetchMyApps = async () => {
  if (!isLogin.value) return
  myAppsLoading.table = true
  try {
    const res = await listOneselfByPage({
      pageNum: myAppsCurrent.value,
      paseSize: myAppsPageSize.value,
    })
    if (res.data.code === 20000 && res.data.data) {
      myApps.value = res.data.data.records ?? []
      myAppsTotal.value = res.data.data.totalRow ?? 0
    }
  } finally {
    myAppsLoading.table = false
  }
}

// 精选应用列表
const featuredAppsPage = reactive({
  current: 1,
  pageSize: 8,
  total: 0,
})
const featuredApps = ref<AppVO[]>([])
const featuredLoading = reactive({ table: false })

const fetchFeaturedApps = async () => {
  featuredLoading.table = true
  try {
    const res = await listFeaturedByPage({
      pageNum: featuredAppsPage.current,
      pageSize: featuredAppsPage.pageSize,
    })
    if (res.data.code === 20000 && res.data.data) {
      featuredApps.value = res.data.data.records ?? []
      featuredAppsPage.total = res.data.data.totalRow ?? 0
    }
  } catch (error) {
    console.error('加载精选应用失败：', error)
  } finally {
    featuredLoading.table = false
  }
}

const handleChat = (appId: string | number) => {
  router.push({ path: '/app/chat', query: { appId: String(appId) } })
}

const handleEdit = (appId: string | number) => {
  router.push({ path: '/user/app/edit', query: { id: String(appId) } })
}

onMounted(() => {
  fetchFeaturedApps()
  if (isLogin.value) {
    fetchMyApps()
  }
})
</script>

<template>
  <div class="home-page">
    <!-- Hero区域 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">不写一行代码，生成完整应用</h1>
        <p class="hero-desc">基于 AI 智能代码生成技术，让开发变得更简单</p>

        <!-- 主要操作区 -->
        <div class="main-action">
          <a-input-search
            v-model:value="createForm.initPrompt"
            placeholder="输入你想要的应用描述，如：帮我生成一个用户管理页面"
            size="large"
            style="max-width: 600px"
            @search="handleMainAction"
          >
            <template #enterButton>
              <a-button type="primary" size="large">
                创建应用
              </a-button>
            </template>
          </a-input-search>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 我的应用 -->
      <div v-if="isLogin" class="section my-apps-section">
        <div class="section-header">
          <h2>我的应用</h2>
          <div class="section-actions">
            <a-button type="link" @click="createAppModalVisible = true">创建应用</a-button>
          </div>
        </div>
        <a-spin :spinning="myAppsLoading.table">
          <div v-if="myApps.length === 0" class="empty-state">
            <p>还没有应用，点击「创建应用」开始吧</p>
          </div>
          <div v-else class="app-grid">
            <div
              v-for="app in myApps"
              :key="app.id"
              class="app-card"
              @click="handleChat(app.id!)"
            >
              <div class="app-cover">
                <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
                <div v-else class="app-cover-placeholder">
                  <span>{{ app.appName?.[0] || 'A' }}</span>
                </div>
              </div>
              <div class="app-info">
                <h3 class="app-name">{{ app.appName }}</h3>
                <p class="app-desc">{{ app.initPrompt || '无描述' }}</p>
                <div class="app-footer">
                  <span v-if="app.deployKey" class="deploy-tag">已部署</span>
                  <a-button type="link" size="small" @click.stop="handleEdit(app.id!)">
                    编辑
                  </a-button>
                </div>
              </div>
            </div>
          </div>
        </a-spin>
        <div v-if="myAppsTotal > myAppsPageSize" class="pagination-wrapper">
          <a-pagination
            v-model:current="myAppsCurrent"
            v-model:page-size="myAppsPageSize"
            :total="myAppsTotal"
            :show-size-changer="false"
            @change="fetchMyApps"
          />
        </div>
      </div>

      <!-- 精选应用 -->
      <div class="section featured-apps-section">
        <div class="section-header">
          <h2>精选应用</h2>
        </div>
        <a-spin :spinning="featuredLoading.table">
          <div v-if="featuredApps.length === 0" class="empty-state">
            <p>暂无精选应用</p>
          </div>
          <div v-else class="app-grid">
            <div
              v-for="app in featuredApps"
              :key="app.id"
              class="app-card"
              @click="handleChat(app.id!)"
            >
              <div class="app-cover">
                <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
                <div v-else class="app-cover-placeholder">
                  <span>{{ app.appName?.[0] || 'A' }}</span>
                </div>
              </div>
              <div class="app-info">
                <h3 class="app-name">{{ app.appName }}</h3>
                <p class="app-desc">{{ app.initPrompt || '无描述' }}</p>
                <div class="app-footer">
                  <span v-if="app.user?.userName" class="creator">
                    by {{ app.user.userName }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </a-spin>
        <div v-if="featuredAppsPage.total > featuredAppsPage.pageSize" class="pagination-wrapper">
          <a-pagination
            v-model:current="featuredAppsPage.current"
            v-model:page-size="featuredAppsPage.pageSize"
            :total="featuredAppsPage.total"
            :show-size-changer="false"
            @change="fetchFeaturedApps"
          />
        </div>
      </div>
    </div>

    <!-- 创建应用弹窗 -->
    <a-modal
      v-model:open="createAppModalVisible"
      title="创建应用"
      :confirm-loading="createLoading"
      ok-text="创建"
      cancel-text="取消"
      @ok="handleCreateApp"
    >
      <div class="create-modal-content">
        <p>描述你想要的应用，AI 将为你生成完整的代码</p>
        <a-form :model="createForm" layout="vertical">
          <a-form-item label="应用描述" name="initPrompt">
            <a-textarea
              v-model:value="createForm.initPrompt"
              placeholder="例如：帮我生成一个用户管理页面，包含用户的增删改查功能"
              :rows="4"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
.home-page {
  min-height: calc(100vh - 64px - 50px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.hero-section {
  padding: 60px 24px;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 16px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.hero-desc {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 32px 0;
}

.main-action {
  display: flex;
  justify-content: center;
}

.main-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.app-card {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.app-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.app-cover {
  height: 120px;
  background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.app-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.app-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 36px;
  font-weight: 600;
}

.app-info {
  padding: 12px;
}

.app-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.app-desc {
  margin: 0 0 12px 0;
  font-size: 12px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  height: 36px;
}

.app-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.deploy-tag {
  font-size: 12px;
  color: #52c41a;
  background: #f6ffed;
  padding: 2px 8px;
  border-radius: 4px;
}

.creator {
  font-size: 12px;
  color: #999;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.create-modal-content p {
  margin-bottom: 16px;
  color: #666;
}
</style>
