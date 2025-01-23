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
        <Card v-for="image in images" :key="image.id" hoverable class="image-card">
          <template #cover>
            <img :src="image.url" :alt="image.name" />
          </template>
          <Card.Meta :title="image.name">
            <template #description>
              <Space>
                <Tag v-for="tag in image.tags" :key="tag" :color="getRandomColor()">{{ tag }}</Tag>
              </Space>
            </template>
          </Card.Meta>
        </Card>
      </div>

      <!-- 加载更多 -->
      <div class="load-more">
        <Button type="primary" :loading="loading" @click="loadMore">加载更多</Button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed, watch, nextTick } from 'vue'
import { Input, Tag, Tabs, Card, Button, Space } from 'ant-design-vue'
import type { API } from '@/api/typings'
import { listPictureVoByPageUsingPost } from '@/api/pictureController'
import { tagList, colorList, categoryList } from '@/config/OptionConfig'

const searchText = ref('')
const activeTab = ref('')
const loading = ref(false)
const images = ref<API.PictureVO[]>([])
const sortType = ref('featured')

const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

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
const loadData = async () => {
  try {
    const res = await listPictureVoByPageUsingPost({
      ...searchParams,
    })
    if (res.data?.data?.records) {
      images.value = res.data.data.records
    }
  } catch (error) {
    console.error('加载图片失败:', error)
  }
}
const loadMore = () => {
  loading.value = true
  // TODO: 实现加载更多逻辑
  setTimeout(() => {
    loading.value = false
  }, 1000)
}

onMounted(async () => {
  await nextTick() // 等待下一个 DOM 更新周期
  if (categoryList.value?.length) {
    activeTab.value = categoryList.value[0].value
  }
  loadData()
})

watch([categoryList], () => {
  if (categoryList.value?.length && !activeTab.value) {
    activeTab.value = categoryList.value[0].value
  }
})
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

.image-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
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
</style>
