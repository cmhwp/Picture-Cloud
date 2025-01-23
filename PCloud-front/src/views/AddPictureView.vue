<template>
  <div class="add-picture-container">
    <Card :title="isEdit ? '修改图片' : '创建图片'" :bordered="false">
      <Form :model="formState" layout="vertical">
        <Form.Item label="图片名称" name="name">
          <Input v-model:value="formState.name" placeholder="请上传图片后编辑" />
        </Form.Item>

        <Form.Item label="分类" name="category">
          <Select v-model:value="formState.category" placeholder="请选择分类">
            <Select.Option v-for="item in categoryList" :key="item.value" :value="item.value">
              {{ item.label }}
            </Select.Option>
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
            <Select.Option v-for="tag in tagList" :key="tag.value" :value="tag.value">
              <Tag :color="tag.color">{{ tag.label }}</Tag>
            </Select.Option>
          </Select>
        </Form.Item>

        <Form.Item label="上传图片">
          <UploadPicture :picture="picture" @success="handleUploadSuccess" />
        </Form.Item>

        <Form.Item>
          <div class="form-buttons">
            <Button type="primary" :loading="loading" @click="handleSubmit">
              {{ isEdit ? '保存修改' : '创建图片' }}
            </Button>
            <Button @click="handleReset">重置</Button>
          </div>
        </Form.Item>
      </Form>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Card, Form, Input, Select, Tag, Button, message } from 'ant-design-vue'
import UploadPicture from '@/components/UploadPicture.vue'
import type { API } from '@/api/typings'
import { updatePictureUsingPost, getPictureVoByIdUsingGet } from '@/api/pictureController'
import { categoryList, tagList } from '@/config/OptionConfig'
import { useRouter, useRoute } from 'vue-router'

const formState = ref<API.PictureEditRequest>({})
const picture = ref<API.PictureVO>()
const router = useRouter()
const route = useRoute()
const loading = ref(false)
const isEdit = ref(false)

onMounted(async () => {
  const id = route.query.id
  console.log('查询ID:', Number(id))
  if (id) {
    isEdit.value = true
    try {
      // @ts-expect-error: id 类型不匹配，但API实际可以接受字符串ID
      const res = await getPictureVoByIdUsingGet({ id: id })
      console.log('API响应:', res)
      if (res.data?.code === 0 && res.data.data) {
        const pictureData = res.data.data
        picture.value = {
          ...pictureData,
          tags: pictureData.tags || [],
        }
        formState.value = {
          name: pictureData.name,
          category: pictureData.category,
          introduction: pictureData.introduction,
          tags: pictureData.tags || [],
        }
      } else {
        message.error(`获取图片失败: ${res.data?.message || '未知错误'}`)
      }
    } catch (error) {
      message.error('获取图片信息失败: ' + error)
    }
  }
})

const handleUploadSuccess = (uploadedPicture: API.PictureVO | undefined) => {
  if (uploadedPicture) {
    picture.value = uploadedPicture
    formState.value.name = uploadedPicture.name || ''
    formState.value.category = uploadedPicture.category || ''
    formState.value.introduction = uploadedPicture.introduction || ''
    formState.value.tags = uploadedPicture.tags || []
  } else {
    picture.value = undefined
    formState.value = {}
  }
}

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
      message.success(isEdit.value ? '修改成功' : '创建成功')
      router.push(`/pictureDetail/${picture.value?.id}`)
    } else {
      message.error(res.data?.message || (isEdit.value ? '修改失败' : '创建失败'))
    }
  } catch (error) {
    message.error((isEdit.value ? '修改' : '创建') + '失败: ' + error)
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
