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

// 快捷提示词示例 - 5条
const quickPrompts = [
  '帮我创建一个个人博客网站，包含首页、文章列表页和文章详情页',
  '生成一个企业内部OA管理系统，包含员工管理、部门管理、考勤打卡',
  '创建一个在线商城的商品展示页面，包括轮播图、商品列表、购物车',
  '制作一个数据可视化大屏页面，展示实时数据统计、图表分析',
  '开发一个在线教育平台，包含课程列表、视屏播放、作业提交',
]

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

const handleQuickPrompt = (prompt: string) => {
  createForm.initPrompt = prompt
  handleMainAction()
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
      router.push({ path: '/app/chat', query: { appId: String(res.data.data) } })
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

const handleView = (appId: string | number) => {
  router.push({ path: '/app/chat', query: { appId: String(appId), view: '1' } })
}

const handleViewWork = (app: AppVO) => {
  if (app.deployKey) {
    window.open(`http://localhost/${app.deployKey}`, '_blank')
  }
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
        <h1 class="hero-title">AI 应用生成平台</h1>
        <p class="hero-desc">一句话轻松创建网站应用</p>

        <!-- 主要操作区 -->
        <div class="main-action">
          <a-input-search
            v-model:value="createForm.initPrompt"
            placeholder="帮我创建个人博客网站"
            size="large"
            style="max-width: 560px"
            @search="handleMainAction"
          >
            <template #enterButton>
              <a-button type="primary" size="large">
                创建应用
              </a-button>
            </template>
          </a-input-search>
        </div>

        <!-- 快捷提示词 - 弹幕风格 2行 -->
        <div class="quick-prompts">
          <div class="quick-row">
            <a-tooltip :title="quickPrompts[0]" placement="top">
              <button class="quick-item" @click="handleQuickPrompt(quickPrompts[0])">
                {{ quickPrompts[0].length > 15 ? quickPrompts[0].substring(0, 15) + '...' : quickPrompts[0] }}
              </button>
            </a-tooltip>
            <a-tooltip :title="quickPrompts[1]" placement="top">
              <button class="quick-item" @click="handleQuickPrompt(quickPrompts[1])">
                {{ quickPrompts[1].length > 15 ? quickPrompts[1].substring(0, 15) + '...' : quickPrompts[1] }}
              </button>
            </a-tooltip>
          </div>
          <div class="quick-row">
            <a-tooltip :title="quickPrompts[2]" placement="top">
              <button class="quick-item" @click="handleQuickPrompt(quickPrompts[2])">
                {{ quickPrompts[2].length > 15 ? quickPrompts[2].substring(0, 15) + '...' : quickPrompts[2] }}
              </button>
            </a-tooltip>
            <a-tooltip :title="quickPrompts[3]" placement="top">
              <button class="quick-item" @click="handleQuickPrompt(quickPrompts[3])">
                {{ quickPrompts[3].length > 15 ? quickPrompts[3].substring(0, 15) + '...' : quickPrompts[3] }}
              </button>
            </a-tooltip>
            <a-tooltip :title="quickPrompts[4]" placement="top">
              <button class="quick-item" @click="handleQuickPrompt(quickPrompts[4])">
                {{ quickPrompts[4].length > 15 ? quickPrompts[4].substring(0, 15) + '...' : quickPrompts[4] }}
              </button>
            </a-tooltip>
          </div>
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
            >
              <div class="app-cover" @click="handleChat(app.id!)">
                <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
                <div v-else class="app-cover-placeholder">
                  <span>{{ app.appName?.[0] || 'A' }}</span>
                </div>
                <div class="app-cover-overlay">
                  <a-button type="primary" @click.stop="handleChat(app.id!)">
                    查看对话
                  </a-button>
                  <a-button v-if="app.deployKey" @click.stop="handleViewWork(app)">
                    查看作品
                  </a-button>
                  <a-button @click.stop="handleEdit(app.id!)">
                    编辑
                  </a-button>
                </div>
              </div>
              <div class="app-info">
                <div class="app-info-left">
                  <a-avatar v-if="app.user?.userAvatar" :src="app.user.userAvatar" :size="40" />
                  <a-avatar v-else :size="40">{{ app.user?.userName?.[0] || 'A' }}</a-avatar>
                </div>
                <div class="app-info-right">
                  <h3 class="app-name">{{ app.appName }}</h3>
                  <p class="app-creator">{{ app.user?.userName || app.user?.userAccount || '未知' }}</p>
                </div>
                <span v-if="app.deployKey" class="deploy-tag">已部署</span>
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
            >
              <div class="app-cover" @click="handleView(app.id!)">
                <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
                <div v-else class="app-cover-placeholder">
                  <span>{{ app.appName?.[0] || 'A' }}</span>
                </div>
                <div class="app-cover-overlay">
                  <a-button type="primary" @click.stop="handleView(app.id!)">
                    查看对话
                  </a-button>
                  <a-button v-if="app.deployKey" @click.stop="handleViewWork(app)">
                    查看作品
                  </a-button>
                </div>
              </div>
              <div class="app-info">
                <div class="app-info-left">
                  <a-avatar v-if="app.user?.userAvatar" :src="app.user.userAvatar" :size="40" />
                  <a-avatar v-else :size="40">{{ app.user?.userName?.[0] || 'A' }}</a-avatar>
                </div>
                <div class="app-info-right">
                  <h3 class="app-name">{{ app.appName }}</h3>
                  <p class="app-creator">{{ app.user?.userName || app.user?.userAccount || '未知' }}</p>
                </div>
                <span v-if="app.deployKey" class="deploy-tag">已部署</span>
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
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 50%, #e8f5e9 100%);
}

.hero-section {
  padding: 60px 24px 50px;
  text-align: center;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.8) 0%, transparent 100%);
}

.hero-content {
  max-width: 900px;
  margin: 0 auto;
}

.hero-title {
  font-size: 44px;
  font-weight: 700;
  color: #00838f;
  margin: 0 0 14px 0;
  text-shadow: 0 2px 4px rgba(0, 131, 143, 0.1);
}

.hero-desc {
  font-size: 18px;
  color: #4db6ac;
  margin: 0 0 36px 0;
}

.main-action {
  display: flex;
  justify-content: center;
}

.main-action :deep(.ant-input-search) {
  border-radius: 24px;
}

.main-action :deep(.ant-input-search .ant-input) {
  border-radius: 24px 0 0 24px;
  padding: 12px 20px;
  font-size: 15px;
}

.main-action :deep(.ant-input-search .ant-input-group-addon) {
  border-radius: 0 24px 24px 0;
  background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
  border: none;
}

.main-action :deep(.ant-input-search .ant-btn-primary) {
  border-radius: 0 24px 24px 0;
  height: 48px;
  padding: 0 32px;
  font-size: 15px;
  background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
  border: none;
}

/* 快捷提示词 - 弹幕风格 2行 */
.quick-prompts {
  margin-top: 32px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}

.quick-row {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
  max-width: 600px;
}

.quick-item {
  display: inline-block;
  padding: 8px 14px;
  background: rgba(0, 131, 143, 0.1);
  border: 1px solid rgba(0, 131, 143, 0.25);
  border-radius: 16px;
  font-size: 13px;
  color: #00838f;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-item:hover {
  background: rgba(0, 131, 143, 0.2);
  border-color: rgba(0, 131, 143, 0.5);
  transform: translateY(-2px);
}

.main-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
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
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.app-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.app-cover {
  height: 130px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.app-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.app-card:hover .app-cover img {
  transform: scale(1.05);
}

.app-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
  color: #fff;
  font-size: 36px;
  font-weight: 600;
}

.app-cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.app-card:hover .app-cover-overlay {
  opacity: 1;
}

.app-cover-overlay :deep(.ant-btn) {
  padding: 4px 12px;
  font-size: 12px;
  height: 28px;
  background: rgba(255, 255, 255, 0.95);
  border-color: transparent;
  color: #333;
}

.app-cover-overlay :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, #00bcd4 0%, #00838f 100%);
  color: #fff;
}

.app-info {
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
}

.app-info-left {
  flex-shrink: 0;
}

.app-info-right {
  flex: 1;
  min-width: 0;
}

.app-name {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.app-creator {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.deploy-tag {
  position: absolute;
  bottom: 12px;
  right: 12px;
  font-size: 11px;
  color: #52c41a;
  background: #f6ffed;
  padding: 2px 8px;
  border-radius: 4px;
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