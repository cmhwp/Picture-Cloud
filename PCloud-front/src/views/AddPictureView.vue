<template>
  <div class="add-picture-container">
    <Card title="上传图片" :bordered="false">
      <Form :model="formState" layout="vertical">
        <Form.Item label="图片名称" name="name">
          <Input v-model:value="formState.name" placeholder="请输入图片名称" />
        </Form.Item>

        <Form.Item label="分类" name="category">
          <Select v-model:value="formState.category" placeholder="请选择分类">
            <Select.Option value="风景">风景</Select.Option>
            <Select.Option value="人物">人物</Select.Option>
            <Select.Option value="动物">动物</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item label="简介" name="introduction">
          <Input.TextArea
            v-model:value="formState.introduction"
            placeholder="请输入图片简介"
            :rows="4"
          />
        </Form.Item>

        <Form.Item label="标签" name="tags">
          <Select
            v-model:value="formState.tags"
            mode="tags"
            placeholder="请输入标签"
            :token-separators="[',']"
          >
            <Select.Option v-for="tag in formState.tags" :key="tag" :value="tag">
              <Tag :color="getRandomColor()">{{ tag }}</Tag>
            </Select.Option>
          </Select>
        </Form.Item>

        <Form.Item label="上传图片">
          <UploadPicture :picture="picture" @success="handleUploadSuccess" />
        </Form.Item>

        <Form.Item>
          <div class="form-buttons">
            <Button type="primary" :loading="loading" @click="handleSubmit"> 创建图片 </Button>
            <Button @click="handleReset">重置</Button>
          </div>
        </Form.Item>
      </Form>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Card, Form, Input, Select, Tag, Button, message } from 'ant-design-vue'
import UploadPicture from '@/components/UploadPicture.vue'
import type { API } from '@/api/typings'
import { updatePictureUsingPost } from '@/api/pictureController'

const formState = ref({
  name: '',
  category: '',
  introduction: '',
  tags: [] as string[],
})

const picture = ref<API.PictureVO>()

// 添加标签颜色数组
const tagColors = [
  'pink',
  'red',
  'orange',
  'green',
  'cyan',
  'blue',
  'purple',
  'geekblue',
  'magenta',
  'volcano',
  'gold',
  'lime',
]

// 获取随机颜色的函数
const getRandomColor = () => {
  const index = Math.floor(Math.random() * tagColors.length)
  return tagColors[index]
}

const handleUploadSuccess = (uploadedPicture: API.PictureVO) => {
  picture.value = uploadedPicture
  formState.value.name = uploadedPicture.name || ''
  formState.value.category = uploadedPicture.category || ''
  formState.value.introduction = uploadedPicture.introduction || ''
  formState.value.tags = uploadedPicture.tags || []
}

const loading = ref(false)

const handleSubmit = async () => {
  if (!picture.value?.id) {
    message.error('请先上传图片')
    return
  }

  loading.value = true
  try {
    const res = await updatePictureUsingPost({
      id: picture.value.id,
      name: formState.value.name,
      category: formState.value.category,
      introduction: formState.value.introduction,
      tags: formState.value.tags,
    })

    if (res.data?.code === 0) {
      message.success('创建成功')
      handleReset()
    } else {
      message.error(res.data?.message || '创建失败')
    }
  } catch (error) {
    message.error('创建失败: ' + error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  formState.value = {
    name: '',
    category: '',
    introduction: '',
    tags: [],
  }
  picture.value = undefined
}
</script>

<style scoped>
.add-picture-container {
  padding: 24px;
  /* background: #f0f2f5; */
  min-height: 100vh;
}

:deep(.ant-card) {
  max-width: 900px;
  margin: 0 auto;
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%);
}

:deep(.ant-card-head) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

:deep(.ant-card-head-title) {
  color: #333;
}

:deep(.ant-form-item-label > label) {
  color: #333;
}

:deep(.ant-input),
:deep(.ant-select-selector) {
  background: rgba(255, 255, 255, 0.9) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
}

:deep(.ant-input:hover),
:deep(.ant-input:focus),
:deep(.ant-select-selector:hover),
:deep(.ant-select-selector:focus) {
  border-color: white !important;
  box-shadow: none !important;
}

:deep(.ant-select-selection-item-content) {
  display: inline-block;
}

:deep(.ant-select-selection-item .ant-tag) {
  margin-right: 0;
  background: transparent;
}

.form-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

:deep(.ant-btn-primary) {
  background: rgba(178, 163, 255, 0.8);
  border: none;
}

:deep(.ant-btn-primary:hover) {
  background: rgb(178, 163, 255);
}

:deep(.ant-btn-default) {
  border: 1px solid rgba(178, 163, 255, 0.8);
  color: rgb(178, 163, 255);
}

:deep(.ant-btn-default:hover) {
  border-color: rgb(178, 163, 255);
  color: rgb(178, 163, 255);
}
</style>
