<template>
  <div class="home-container">
    <!-- 顶部背景图片区域 -->
    <div class="hero-section">
      <h1 class="hero-title">精彩的免版税图片和免版税库存</h1>

      <!-- 搜索框 -->
      <div class="search-container">
        <Input.Search
          v-model:value="searchText"
          placeholder="搜索免费图片、视频、音乐等内容"
          enter-button
          size="large"
          @search="handleSearch"
        />
      </div>

      <!-- 热门标签 -->
      <div class="popular-tags">
        <Tag
          v-for="tag in popularTags"
          :key="tag"
          :color="getRandomColor()"
          @click="handleTagClick(tag)"
        >
          {{ tag }}
        </Tag>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-section">
      <!-- 分类标签页 -->
      <Tabs v-model:activeKey="activeTab">
        <Tab-Pane v-for="item in categoryList" :key="item.value" :tab="item.label" />
      </Tabs>

      <!-- 排序选项 -->
      <div class="filter-section">
        <Space>
          <Button
            :type="sortType === 'featured' ? 'primary' : 'text'"
            @click="handleSort('featured')"
            >编辑精选</Button
          >
          <Button :type="sortType === 'newest' ? 'primary' : 'text'" @click="handleSort('newest')"
            >最新的</Button
          >
          <Button :type="sortType === 'popular' ? 'primary' : 'text'" @click="handleSort('popular')"
            >热门</Button
          >
        </Space>
      </div>

      <!-- 图片网格 -->
      <div class="image-grid">
        <Card v-for="image in filteredImages" :key="image.id" hoverable class="image-card">
          <template #cover>
            <div class="image-wrapper">
              <img :src="image.url" :alt="image.name" />
              <div class="image-overlay">
                <Space>
                  <Button type="text" class="overlay-icon" @click="handleCollect(image)">
                    <HeartOutlined />
                  </Button>
                  <Button type="text" class="overlay-icon" @click="handlePreview(image)">
                    <EyeOutlined />
                  </Button>
                  <Button type="text" class="overlay-icon" @click="handleDownload(image)">
                    <DownloadOutlined />
                  </Button>
                </Space>
              </div>
            </div>
          </template>
          <Card.Meta :title="image.name">
            <template #description>
              <Space>
                <Tag color="#ff4d4f">📁 {{ image.category || '未分类' }}</Tag>
                <Space wrap>
                  <Tag
                    v-for="tag in image.tags?.length ? image.tags : ['默认']"
                    :key="tag"
                    :color="getRandomColor()"
                  >
                    🏷️ {{ tag }}
                  </Tag>
                </Space>
              </Space>
            </template>
          </Card.Meta>
        </Card>
      </div>

      <!-- 加载更多 -->
      <div class="load-more">
        <Button v-if="hasMore" type="primary" :loading="loading" @click="loadMore">
          加载更多
        </Button>
        <div v-else class="no-more">没有更多数据了</div>
      </div>
    </div>

    <!-- 图片预览 Modal -->
    <Modal
      v-model:visible="previewVisible"
      :footer="null"
      @cancel="handleCancel"
      width="1000px"
      :centered="true"
      wrapClassName="preview-modal"
    >
      <div class="preview-content">
        <div class="preview-image">
          <img :src="previewImage" />
        </div>
        <div class="preview-info">
          <h2>{{ currentImage?.name }}</h2>
          <div class="info-item">
            <label>👤 作者：</label>
            <span>{{ currentImage?.user?.userName || '未知' }}</span>
          </div>
          <div class="info-item">
            <label>📁 分类：</label>
            <Tag color="#ff4d4f">{{ currentImage?.category || '未分类' }}</Tag>
          </div>
          <div class="info-item">
            <label>🏷️ 标签：</label>
            <Space wrap>
              <Tag
                v-for="tag in currentImage?.tags?.length ? currentImage.tags : ['默认']"
                :key="tag"
                :color="getRandomColor()"
              >
                {{ tag }}
              </Tag>
            </Space>
          </div>
          <div class="info-item">
            <label>📅 上传时间：</label>
            <span>{{ dayjs(currentImage?.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
          </div>

          <!-- 添加操作按钮组 -->
          <div class="action-buttons">
            <Button type="primary" block @click="handleViewDetail(currentImage)">
              <EyeOutlined /> 查看图片详情
            </Button>
          </div>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed, watch, nextTick } from 'vue'
import { Input, Tag, Tabs, Card, Button, Space, Modal } from 'ant-design-vue'
import type { API } from '@/api/typings'
import { listPictureVoByPageUsingPost } from '@/api/pictureController'
import { tagList, colorList, categoryList } from '@/config/OptionConfig'
import { HeartOutlined, EyeOutlined, DownloadOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'

const searchText = ref('')
const activeTab = ref('')
const loading = ref(false)
const images = ref<API.PictureVO[]>([])
const sortType = ref('featured')
const previewVisible = ref(false)
const previewImage = ref('')
const currentImage = ref<API.PictureVO | null>(null)

const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})

const hasMore = ref(true)

const popularTags = computed(() => {
  return tagList.value?.map((item) => item.label) || []
})

const getRandomColor = () => {
  return colorList.value?.[Math.floor(Math.random() * colorList.value.length)] || '#b2a3ff'
}

const handleSearch = (value: string) => {
  console.log('搜索:', value)
}

const handleTagClick = (tag: string) => {
  searchText.value = tag
  handleSearch(tag)
}

const handleSort = (type: string) => {
  sortType.value = type
  // TODO: 实现排序逻辑
}

// 添加计算属性来过滤图片
const filteredImages = computed(() => {
  if (!activeTab.value || activeTab.value === 'all') {
    return images.value
  }
  return images.value.filter((image) => image.category === activeTab.value)
})

// 修改加载数据的函数
const loadData = async (loadMore = false) => {
  try {
    const res = await listPictureVoByPageUsingPost({
      ...searchParams,
      category: activeTab.value === 'all' ? undefined : activeTab.value,
    })

    if (res.data?.data?.records) {
      if (loadMore) {
        images.value = [...images.value, ...res.data.data.records]
      } else {
        images.value = res.data.data.records
      }
      hasMore.value = (res.data?.data?.total ?? 0) > images.value.length
    }
  } catch (error) {
    console.error('加载图片失败:', error)
  }
}

const loadMore = async () => {
  if (!hasMore.value) return
  loading.value = true
  searchParams.current! += 1 // 使用非空断言
  try {
    await loadData(true)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await nextTick() // 等待下一个 DOM 更新周期
  if (categoryList.value?.length) {
    activeTab.value = categoryList.value[0].value
  }
  loadData()
})

// 修改分类切换的监听
watch(activeTab, () => {
  searchParams.current = 1 // 重置页码
  hasMore.value = true // 重置加载更多状态
  loadData()
})

const handleCollect = (image: API.PictureVO) => {
  // 处理收藏逻辑
  console.log('收藏图片:', image)
}

const handlePreview = (image: API.PictureVO) => {
  currentImage.value = image
  previewImage.value = image.url ?? ''
  previewVisible.value = true
}

const handleDownload = (image: API.PictureVO) => {
  // 处理下载逻辑
  console.log('下载图片:', image)
}

const handleCancel = () => {
  previewVisible.value = false
  currentImage.value = null
}

const router = useRouter()

const handleViewDetail = (image: API.PictureVO | null) => {
  if (image?.id) {
    router.push(`/pictureDetail/${image.id}`)
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
}

.hero-section {
  height: 400px;
  background-image: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('@/assets/bg.jpg');
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: white;
  margin-bottom: 20px;
  width: 100%;
  border-radius: 10px;
}

.hero-title {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  text-align: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.search-container {
  width: 100%;
  max-width: 800px;
  margin-bottom: 1.5rem;
}

.popular-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.content-section {
  border-radius: 10px;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, rgba(178, 163, 255, 0.1) 0%, rgba(232, 255, 199, 0.1) 100%);
}

.filter-section {
  margin: 16px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.image-card {
  width: 100%;
  transition: transform 0.3s;
  background: rgba(255, 255, 255, 0.9);
}

.image-card:hover {
  transform: translateY(-5px);
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 8px 8px 0 0;
}

.image-wrapper:hover .image-overlay {
  opacity: 1;
}

.overlay-icon {
  color: white !important;
  font-size: 20px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.overlay-icon:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.load-more {
  text-align: center;
  margin-top: 40px;
  margin-bottom: 40px;
}

:deep(.ant-input-search-button) {
  background-color: rgb(178, 163, 255);
  border-color: rgb(178, 163, 255);
}

:deep(.ant-input-search-button:hover) {
  background-color: rgb(158, 143, 235);
  border-color: rgb(158, 143, 235);
}

:deep(.ant-tag) {
  cursor: pointer;
  transition: transform 0.2s;
}

:deep(.ant-tag:hover) {
  transform: scale(1.05);
}

:deep(.ant-tabs-nav) {
  margin-bottom: 0;
}

:deep(.ant-tabs-ink-bar) {
  background: rgb(178, 163, 255);
}

:deep(.ant-tabs-tab.ant-tabs-tab-active .ant-tabs-tab-btn) {
  color: rgb(178, 163, 255);
}

:deep(.ant-btn-primary) {
  background: rgb(178, 163, 255);
  border-color: rgb(178, 163, 255);
}

:deep(.ant-btn-primary:hover) {
  background: rgb(158, 143, 235);
  border-color: rgb(158, 143, 235);
}

:deep(.ant-card-meta-title) {
  color: #333;
}
:deep(.ant-tabs-tab-btn):hover {
  color: rgb(178, 163, 255);
}
:deep(.ant-tabs-tab):hover {
  color: rgb(178, 163, 255);
}

:deep(.ant-space) {
  flex-wrap: wrap;
  gap: 8px;
}

.no-more {
  color: #999;
  text-align: center;
  padding: 16px 0;
}

:deep(.preview-modal .ant-modal-content) {
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
}

.preview-content {
  display: flex;
  gap: 24px;
}

.preview-image {
  flex: 1;
  min-width: 0;
}

.preview-image img {
  width: 100%;
  border-radius: 8px;
}

.preview-info {
  width: 300px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
}

.preview-info h2 {
  margin-bottom: 20px;
  color: #333;
}

.info-item {
  margin-bottom: 16px;
}

.info-item label {
  display: block;
  color: #666;
  margin-bottom: 8px;
}

.action-buttons {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.action-buttons .ant-btn {
  height: 40px;
  font-size: 16px;
}
</style>
