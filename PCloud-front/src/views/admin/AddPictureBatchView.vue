<template>
  <div class="batch-upload-container">
    <Card :bordered="false" title="批量上传图片">
      <Form :model="formState" layout="vertical">
        <Form.Item
          label="搜索关键词"
          name="searchText"
          :rules="[{ required: true, message: '请输入搜索关键词' }]"
        >
          <Input
            v-model:value="formState.searchText"
            placeholder="请输入要搜索的图片关键词"
            allow-clear
          />
        </Form.Item>

        <Form.Item
          label="抓取数量"
          name="fetchCount"
          :rules="[{ required: true, message: '请选择抓取数量' }]"
        >
          <InputNumber
            v-model:value="formState.fetchCount"
            :min="1"
            :max="30"
            style="width: 200px"
          />
          <span class="ant-form-text" style="margin-left: 8px">最多30张</span>
        </Form.Item>

        <Form.Item
          label="图片名称前缀"
          name="prefixName"
          extra="不填写则使用搜索关键词作为前缀"
        >
          <Input
            v-model:value="formState.prefixName"
            placeholder="请输入图片名称前缀"
            allow-clear
          />
        </Form.Item>

        <Form.Item>
          <Space>
            <Button
              type="primary"
              :loading="loading"
              @click="handleSubmit"
            >
              开始抓取
            </Button>
            <Button @click="handleReset">重置</Button>
          </Space>
        </Form.Item>
      </Form>

      <!-- 上传结果展示 -->
      <div v-if="uploadResult" class="upload-result">
        <Alert
          :message="`成功上传 ${uploadResult} 张图片`"
          type="success"
          show-icon
        />
        <div class="result-actions">
          <Space>
            <Button type="primary" @click="goToManage">查看管理</Button>
            <Button @click="handleReset">继续上传</Button>
          </Space>
        </div>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Card, Form, Input, InputNumber, Button, Space, message, Alert } from 'ant-design-vue'
import { uploadPictureByBatchUsingPost } from '@/api/pictureController'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const uploadResult = ref<number>()

const formState = reactive({
  searchText: '',
  fetchCount: 10,
  prefixName: '',
})

const handleSubmit = async () => {
  if (!formState.searchText) {
    message.error('请输入搜索关键词')
    return
  }

  loading.value = true
  try {
    const res = await uploadPictureByBatchUsingPost(formState)
    if (res.data?.code === 0) {
      uploadResult.value = res.data.data
      message.success(`成功上传 ${res.data.data} 张图片`)
    } else {
      message.error(res.data?.message || '上传失败')
    }
  } catch (error) {
    message.error('上传失败: ' + error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  formState.searchText = ''
  formState.fetchCount = 10
  formState.prefixName = ''
  uploadResult.value = undefined
}

const goToManage = () => {
  router.push('/admin/pictureManage')
}
</script>

<style scoped>
.batch-upload-container {
  padding: 24px;
  min-height: 100vh;
}

:deep(.ant-card) {
  max-width: 800px;
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
:deep(.ant-input-number) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

:deep(.ant-input:hover),
:deep(.ant-input:focus),
:deep(.ant-input-number:hover),
:deep(.ant-input-number:focus) {
  border-color: white;
  box-shadow: none;
}

.upload-result {
  margin-top: 24px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
}

.result-actions {
  margin-top: 16px;
  text-align: center;
}

:deep(.ant-alert-success) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(82, 196, 26, 0.3);
}
</style>
