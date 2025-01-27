<template>
  <div class="picture-detail">
    <div class="detail-container">
      <!-- 左侧图片区域 -->
      <div class="image-section">
        <img :src="pictureDetail?.url" :alt="pictureDetail?.name" />

        <!-- 操作按钮组 -->
        <div class="action-bar">
          <Space size="large">
            <Button type="primary" @click="handleCollect"> <HeartOutlined /> 收藏 </Button>
            <Button type="primary" @click="handleDownload"> <DownloadOutlined /> 下载 </Button>
            <Button @click="handleShare"> <ShareAltOutlined /> 分享 </Button>
          </Space>
        </div>
      </div>

      <!-- 右侧信息区域 -->
      <div class="info-section">
        <h1>{{ pictureDetail?.name }}</h1>

        <!-- 作者信息 -->
        <div class="author-info">
          <Avatar :src="pictureDetail?.user?.userAvatar" :size="48" />
          <div class="author-detail">
            <h3>{{ pictureDetail?.user?.userName || '未知作者' }}</h3>
            <Button type="link" @click="handleFollowAuthor">关注作者</Button>
          </div>
        </div>

        <!-- 图片信息 -->
        <div class="info-block">
          <h4>📁 分类</h4>
          <Tag color="#ff4d4f">{{ pictureDetail?.category || '未分类' }}</Tag>
        </div>

        <div class="info-block">
          <h4>🏷️ 标签</h4>
          <Space wrap>
            <Tag v-for="tag in parsedTags" :key="tag" :color="getRandomColor()">
              {{ tag }}
            </Tag>
          </Space>
        </div>
        <div class="info-block">
          <h4>🖼️ 图片信息</h4>
          <div class="info-item">
            <label>分辨率：</label>
            <span>{{ pictureDetail?.picWidth || 0 }} × {{ pictureDetail?.picHeight || 0 }} px</span>
          </div>
          <div class="info-item">
            <label>文件大小：</label>
            <span>{{ formatFileSize(pictureDetail?.picSize) }}</span>
          </div>
          <div class="info-item">
            <label>文件格式：</label>
            <span>{{ getFileFormat(pictureDetail?.url) }}</span>
          </div>
        </div>
        <div class="info-block">
          <h4>📅 上传时间</h4>
          <span>{{ dayjs(pictureDetail?.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
        </div>

        <div class="info-block">
          <h4>📊 统计信息</h4>
          <!-- TODO: 统计信息 -->
          <Space size="large">
            <span>👁️ 浏览: {{ pictureDetail?.viewCount || 0 }}</span>
            <span>❤️ 收藏: {{ pictureDetail?.favourCount || 0 }}</span>
            <span>⬇️ 下载: {{ pictureDetail?.downloadCount || 0 }}</span>
          </Space>
        </div>

        <!-- 图片描述 -->
        <div class="info-block description">
          <h4>📝 图片描述</h4>
          <p>{{ pictureDetail?.introduction || '暂无描述' }}</p>
        </div>
      </div>
    </div>

    <!-- 相关推荐 -->
    <div class="related-section">
      <h2>相关推荐</h2>
      <div class="related-grid">
        <Card
          v-for="item in relatedPictures"
          :key="item.id"
          hoverable
          class="related-card"
          @click="handleRelatedClick(item)"
        >
          <template #cover>
            <img :src="item.url" :alt="item.name" />
          </template>
          <Card.Meta :title="item.name" />
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Card, Button, Space, Tag, Avatar, message } from 'ant-design-vue'
import { HeartOutlined, DownloadOutlined, ShareAltOutlined } from '@ant-design/icons-vue'
import type { API } from '@/api/typings'
import { getPictureVoByIdUsingGet } from '@/api/pictureController'
import { colorList } from '@/config/OptionConfig'
import { downloadImage } from '@/utils'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const pictureDetail = ref<API.PictureVO>()
const relatedPictures = ref<API.PictureVO[]>([])

const getRandomColor = () => {
  return colorList.value?.[Math.floor(Math.random() * colorList.value.length)] || '#b2a3ff'
}

// 获取图片详情
const loadPictureDetail = async () => {
  try {
    const id = route.params.id as string
    //@ts-expect-error: id 类型不匹配，但API实际可以接受字符串ID
    const res = await getPictureVoByIdUsingGet({ id })
    if (res.data?.data) {
      pictureDetail.value = res.data.data
    }
  } catch (error) {
    console.error('获取图片详情失败:', error)
    message.error('获取图片详情失败')
  }
}

// 处理收藏
const handleCollect = () => {
  message.success('收藏成功')
}

// 处理下载
const handleDownload = () => {
  downloadImage(pictureDetail.value?.url, pictureDetail.value?.name)
}

// 处理分享
const handleShare = () => {
  message.success('分享链接已复制')
}

// 处理关注作者
const handleFollowAuthor = () => {
  message.success('关注成功')
}

// 处理相关图片点击
const handleRelatedClick = (picture: API.Picture) => {
  router.push(`/pictureDetail/${picture.id}`)
}

// 格式化文件大小
const formatFileSize = (size?: number) => {
  if (!size) return '未知'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  return (size / (1024 * 1024)).toFixed(2) + ' MB'
}

// 从URL获取文件格式
const getFileFormat = (url?: string) => {
  if (!url) return '未知'
  const extension = url.split('.').pop()?.toLowerCase()
  return extension ? extension.toUpperCase() : '未知'
}

// 处理标签
const parsedTags = computed(() => {
  if (!pictureDetail.value?.tags) return ['默认']
  try {
    // 如果已经是数组就直接返回
    if (Array.isArray(pictureDetail.value.tags)) {
      return pictureDetail.value.tags
    }
    // 尝试解析 JSON 字符串
    return JSON.parse(pictureDetail.value.tags as string)
  } catch {
    // 如果解析失败，返回默认值
    return ['默认']
  }
})

onMounted(() => {
  loadPictureDetail()
})
</script>

<style scoped>
.picture-detail {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.detail-container {
  display: flex;
  gap: 32px;
  margin-bottom: 48px;
}

.image-section {
  flex: 1;
  min-width: 0;
}

.image-section img {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-bar {
  margin-top: 24px;
  text-align: center;
}

.info-section {
  width: 360px;
  padding: 24px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.info-section h1 {
  margin-bottom: 24px;
  color: #333;
  font-size: 24px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.author-detail {
  flex: 1;
}

.author-detail h3 {
  margin: 0;
  color: #333;
}

.info-block {
  margin-bottom: 24px;
}

.info-block h4 {
  margin-bottom: 12px;
  color: #666;
}

.description p {
  color: #666;
  line-height: 1.6;
}

.related-section {
  margin-top: 48px;
}

.related-section h2 {
  margin-bottom: 24px;
  color: #333;
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 24px;
}

.related-card {
  transition: transform 0.3s;
}

.related-card:hover {
  transform: translateY(-5px);
}

.related-card img {
  height: 180px;
  object-fit: cover;
}

:deep(.ant-btn-primary) {
  background: rgb(178, 163, 255);
  border-color: rgb(178, 163, 255);
}

:deep(.ant-btn-primary:hover) {
  background: rgb(158, 143, 235);
  border-color: rgb(158, 143, 235);
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.info-item label {
  color: #666;
  width: 80px;
  flex-shrink: 0;
}

.info-item span {
  color: #333;
}
</style>
