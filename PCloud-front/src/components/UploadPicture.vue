<script setup lang="ts">
import { ref, watch } from 'vue'
import { message, Upload, Button, Modal } from 'ant-design-vue'
import type { UploadChangeParam, UploadProps, UploadFile } from 'ant-design-vue'
import { InboxOutlined } from '@ant-design/icons-vue'
import { uploadPictureUsingPost } from '@/api/pictureController'
import type { API } from '@/api/typings'

const props = defineProps<{
  picture?: API.PictureVO
  onSuccess?: (picture: API.PictureVO | undefined) => void
}>()

const fileList = ref<UploadFile[]>([])
const loading = ref(false)

const previewVisible = ref(false)
const previewImage = ref('')

// 监听 picture 的变化，当 picture 为 undefined 时清空文件列表
watch(
  () => props.picture,
  (newVal) => {
    if (!newVal) {
      fileList.value = []
    }
  },
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
</script>

<template>
  <div class="upload-container">
    <div v-if="picture?.url" class="preview-container" @click="handlePreviewImage(picture.url)">
      <img :src="picture.url" class="preview-image" />
      <div class="preview-actions">
        <Button type="primary" @click.stop="handleReupload">重新上传</Button>
      </div>
    </div>
    <Upload
      v-else
      v-model:file-list="fileList"
      name="file"
      :show-upload-list="false"
      :custom-request="customRequest"
      :before-upload="beforeUpload"
      @change="handleChange"
    >
      <div class="upload-area">
        <p class="ant-upload-drag-icon">
          <InboxOutlined />
        </p>
        <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
        <p class="ant-upload-hint">支持单个图片上传，可点击或拖拽</p>
      </div>
    </Upload>

    <Modal v-model:visible="previewVisible" :footer="null" @cancel="handleCancel">
      <img :src="previewImage" style="width: 100%" />
    </Modal>
  </div>
</template>

<style scoped>
.preview-container {
  position: relative;
  width: 300px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 10px;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.preview-container:hover .preview-actions {
  opacity: 1;
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

.upload-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px; /* 设置一个最小高度 */
  width: 100%;
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
