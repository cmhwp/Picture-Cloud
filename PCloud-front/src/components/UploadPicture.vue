<script setup lang="ts">
import { ref, watch } from 'vue'
import { message, Upload, Button, Modal, Tabs, Input } from 'ant-design-vue'
import type { UploadChangeParam, UploadProps, UploadFile } from 'ant-design-vue'
import { InboxOutlined } from '@ant-design/icons-vue'
import { uploadPictureUsingPost } from '@/api/pictureController'
import type { API } from '@/api/typings'

const TabPane = Tabs.TabPane

const props = defineProps<{
  picture?: API.PictureVO
  onSuccess?: (picture: API.PictureVO | undefined) => void
}>()

const fileList = ref<UploadFile[]>([])
const loading = ref(false)
const urlLoading = ref(false)
const activeTab = ref('file')
const imageUrl = ref('')

const previewVisible = ref(false)
const previewImage = ref('')

// 监听 picture 的变化，当 picture 为 undefined 时清空文件列表
watch(
  () => props.picture,
  (newVal) => {
    if (!newVal) {
      fileList.value = []
      imageUrl.value = ''
    } else {
      // 当有图片数据时，初始化 fileList
      fileList.value = [{
        uid: '-1',
        name: newVal.name || 'image',
        status: 'done',
        url: newVal.url,
      }]
    }
  },
  // 添加 immediate: true 使得组件挂载时就执行一次
  { immediate: true }
)

const handleChange = async (info: UploadChangeParam) => {
  if (info.file.status === 'uploading') {
    loading.value = true
    return
  }
  if (info.file.status === 'done') {
    loading.value = false
  }
  if (info.file.status === 'error') {
    loading.value = false
    message.error('上传失败')
  }
  fileList.value = info.fileList
}

/**
 * 上传图片
 * @param options 上传选项
 */
const customRequest: UploadProps['customRequest'] = async (options) => {
  const { file, onSuccess, onError } = options
  try {
    // 如果props.picture存在，则上传图片
    const params = props.picture ? { id: props.picture.id } : {}
    const res = await uploadPictureUsingPost(params, {}, file as File)
    if (res.data?.code === 0 && res.data.data) {
      onSuccess?.(res.data.data)
      props.onSuccess?.(res.data.data)
      message.success('上传成功')
    } else {
      throw new Error(res.data?.message || '上传失败')
    }
  } catch (error) {
    onError?.(error as Error)
    message.error('上传失败: ' + (error as Error).message)
  }
}
/**
 * 上传图片前检查文件类型和大小
 * @param file 文件
 * @returns 是否上传
 */
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    message.error('图片必须小于 10MB!')
  }
  return isImage && isLt10M
}

const handleCancel = () => {
  previewVisible.value = false
}

const handleReupload = () => {
  fileList.value = []
  props.onSuccess?.(undefined)
}

const handlePreviewImage = (url: string) => {
  previewImage.value = url
  previewVisible.value = true
}

// URL上传处理
const handleUrlUpload = async () => {
  if (!imageUrl.value) {
    message.error('请输入图片URL')
    return
  }

  // if (!isValidImageUrl(imageUrl.value)) {
  //   message.error('请输入有效的图片URL')
  //   return
  // }

  urlLoading.value = true
  try {
    // 创建一个Blob对象
    const response = await fetch(imageUrl.value)
    const blob = await response.blob()
    const file = new File([blob], 'image.jpg', { type: blob.type })

    // 使用相同的上传逻辑
    const params = props.picture ? { id: props.picture.id } : {}
    const res = await uploadPictureUsingPost(params, {}, file)

    if (res.data?.code === 0 && res.data.data) {
      props.onSuccess?.(res.data.data)
      message.success('上传成功')
      // 更新预览
      fileList.value = [{
        uid: '-1',
        name: 'image.jpg',
        status: 'done',
        url: imageUrl.value,
      }]
    } else {
      throw new Error(res.data?.message || '上传失败')
    }
  } catch (error) {
    message.error('上传失败: ' + (error instanceof Error ? error.message : '未知错误'))
  } finally {
    urlLoading.value = false
  }
}

// 验证图片URL
const isValidImageUrl = (url: string) => {
  try {
    const urlObj = new URL(url)
    const ext = urlObj.pathname.split('.').pop()?.toLowerCase()
    const validExts = ['jpg', 'jpeg', 'png', 'gif', 'webp']
    return validExts.includes(ext || '')
  } catch {
    return false
  }
}
</script>

<template>
  <div class="upload-container">
    <div v-if="!fileList.length" class="upload-options">
      <Tabs v-model:activeKey="activeTab">
        <TabPane key="file" tab="本地上传">
          <Upload
            v-model:fileList="fileList"
            :customRequest="customRequest"
            :beforeUpload="beforeUpload"
            @change="handleChange"
            :showUploadList="false"
          >
            <div class="upload-area">
              <p class="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
              <p class="ant-upload-hint">支持单次上传一张图片，文件大小不超过10MB</p>
            </div>
          </Upload>
        </TabPane>
        <TabPane key="url" tab="URL上传">
          <div class="url-upload">
            <Input.TextArea
              v-model:value="imageUrl"
              placeholder="请输入图片URL"
              :rows="3"
            />
            <Button
              type="primary"
              :loading="urlLoading"
              @click="handleUrlUpload"
              style="margin-top: 16px"
            >
              上传
            </Button>
          </div>
        </TabPane>
      </Tabs>
    </div>

    <div v-else class="preview-container">
      <div class="preview-wrapper">
        <img :src="fileList[0]?.url || fileList[0]?.preview" alt="预览图" @click="handlePreview" />
      </div>
      <div class="preview-actions">
        <Button type="primary" @click="handleReupload">重新上传</Button>
      </div>
    </div>

    <Modal
      :visible="previewVisible"
      :footer="null"
      @cancel="handleCancel"
      :width="800"
    >
      <img style="width: 100%" :src="previewImage" />
    </Modal>
  </div>
</template>

<style scoped>
.upload-container {
  width: 100%;
  min-height: 200px;
}

.upload-options {
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
}

.url-upload {
  padding: 20px;
  text-align: center;
}

.preview-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.preview-wrapper {
  width: 100%;
  max-width: 500px;
  aspect-ratio: 16/9;
  overflow: hidden;
  border-radius: 8px;
  cursor: pointer;
}

.preview-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-actions {
  display: flex;
  gap: 8px;
}

/* :deep(.ant-upload-list-item-container) {
  width: 100% !important;
  height: 100% !important;
  min-width: 152px !important;
  min-height: 152px !important;
  margin-right: 8px;
  margin-bottom: 8px;
} */

:deep(.ant-upload.ant-upload-select) {
  width: 100% !important;
  height: 100% !important;
  min-width: 152px !important;
  min-height: 152px !important;
}

:deep(.ant-upload-list) {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
}

.upload-area {
  margin-top: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  width: 100%;
  height: 100%;
}

:deep(.ant-modal-content) {
  background: rgba(255, 255, 255, 0.95);
}

:deep(.ant-modal-header) {
  background: transparent;
  border-bottom: 1px solid rgba(178, 163, 255, 0.2);
}

:deep(.ant-modal-title) {
  color: rgb(178, 163, 255);
}

:deep(.ant-modal-close) {
  color: rgb(178, 163, 255);
}

:deep(.ant-modal-body) {
  padding: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
}

:deep(.ant-modal-body img) {
  max-height: 80vh;
  object-fit: contain;
}
</style>
