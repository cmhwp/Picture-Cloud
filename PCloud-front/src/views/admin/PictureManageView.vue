<script setup lang="ts">
import { ref, reactive, h, onMounted } from 'vue'
import {
  Card,
  Form,
  Input,
  Button,
  Table,
  Tag,
  Space,
  message,
  Popconfirm,
  Image,
  Modal,
  Radio,
  Typography,
  Select,
} from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  SettingOutlined,
} from '@ant-design/icons-vue'
import type { API } from '@/api/typings'
import {
  deletePictureUsingPost,
  listPictureByPageUsingPost,
  reviewPictureUsingPost,
} from '@/api/pictureController'
import { PictureReviewStatusOptions } from '@/constant/PictureConstant'
import type { TablePaginationConfig } from 'ant-design-vue'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'
import type { TableColumnType } from 'ant-design-vue'

const router = useRouter()
const formState = reactive({
  name: '',
  category: '',
  reviewStatus: undefined as number | undefined,
})

const loading = ref(false)
const tableData = ref<API.Picture[]>([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`,
})

const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

const columns = [
  {
    title: '预览图',
    dataIndex: 'url',
    width: 100,
    customRender: ({ text }: { text: string }) => {
      return text
        ? h(Image, {
            src: text,
            style: {
              height: '50px',
              width: '50px',
              objectFit: 'cover',
            },
          })
        : '-'
    },
  },
  {
    title: '名称',
    dataIndex: 'name',
    width: 100,
  },
  {
    title: '分类',
    dataIndex: 'category',
    width: 100,
  },
  {
    title: '标签',
    dataIndex: 'tags',
    width: 150,
    customRender: ({ text }: { text: string | string[] }) => {
      if (!text) return '-'
      const tags = Array.isArray(text)
        ? text
        : text
            .replace(/[\[\]"]/g, '')
            .split(',')
            .filter(Boolean)
      return tags.length
        ? h(Space, { wrap: true }, () => tags.map((tag) => h(Tag, { color: 'blue' }, () => tag)))
        : '-'
    },
  },
  {
    title: '图片信息',
    dataIndex: 'pictureInfo',
    width: 150,
    customRender: ({ record }: { record: API.Picture }) => {
      const info = []
      if (record.picWidth && record.picHeight) {
        info.push(`${record.picWidth}x${record.picHeight}`)
      }
      if (record.picFormat) {
        info.push(record.picFormat.toUpperCase())
      }
      if (record.picSize) {
        const size = (record.picSize / 1024 / 1024).toFixed(2)
        info.push(`${size}MB`)
      }
      if (record.picScale) {
        info.push(`${record.picScale}%`)
      }
      return info.length ? info.join(' | ') : '-'
    },
  },
  {
    title: '简介',
    dataIndex: 'introduction',
    ellipsis: true,
    width: 150,
  },
  {
    title: '审核状态',
    dataIndex: 'reviewStatus',
    width: 100,
    customRender: ({ text }: { text: number }) => {
      const status = PictureReviewStatusOptions[text].label || '未知'
      const color = text === 1 ? 'success' : text === 2 ? 'error' : 'warning'
      return h(Tag, { color }, () => status)
    },
  },
  {
    title: '审核信息',
    dataIndex: 'reviewInfo',
    width: 150,
    customRender: ({ record }: { record: API.Picture }) => {
      return h(Text, { color: 'blue' }, () => record.reviewMessage)
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
    customRender: ({ text }: { text: string }) => {
      return text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-'
    },
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right',
    customRender: ({ record }: { record: API.Picture }) => {
      return h(Space, { wrap: true, size: [4, 0] }, () => [
        h(
          Button,
          {
            type: 'link',
            onClick: () => handleReview(record),
            style: { color: '#52c41a' },
            icon: h(SettingOutlined),
          },
          () => '审核',
        ),
        h(
          Button,
          {
            type: 'link',
            onClick: () => handleEdit(record),
            icon: h(EditOutlined),
          },
          () => '编辑',
        ),
        h(
          Popconfirm,
          {
            title: '确定要删除此图片吗？',
            okText: '确定',
            cancelText: '取消',
            onConfirm: () => handleDelete(record),
          },
          () => [
            h(
              Button,
              {
                type: 'link',
                danger: true,
                icon: h(DeleteOutlined),
              },
              () => '删除',
            ),
          ],
        ),
      ])
    },
  },
] as TableColumnType<API.Picture>[]

const reviewVisible = ref(false)
const reviewForm = reactive({
  id: 0,
  reviewStatus: 1,
  reviewMessage: '',
})

const { Text } = Typography

const handleSearch = async () => {
  loading.value = true
  try {
    const res = await listPictureByPageUsingPost({
      name: formState.name,
      category: formState.category,
      reviewStatus: formState.reviewStatus,
      current: pagination.current,
      pageSize: pagination.pageSize,
      sortField: 'createTime',
      sortOrder: 'descend',
    })
    if (res.data?.data) {
      tableData.value = res.data.data.records ?? []
      pagination.total = res.data.data.total ?? 0
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (error) {
    message.error('获取图片列表失败: ' + (error instanceof Error ? error.message : String(error)))
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  formState.name = ''
  formState.category = ''
  formState.reviewStatus = undefined
  pagination.current = 1
  handleSearch()
}

const handleTableChange = (pag: TablePaginationConfig) => {
  pagination.current = pag.current ?? 1
  pagination.pageSize = pag.pageSize ?? 10
  searchParams.current = pagination.current
  searchParams.pageSize = pagination.pageSize
  handleSearch()
}

const handleEdit = (record: API.Picture) => {
  router.push(`/addPicture?id=${record.id}`)
}

const handleDelete = async (record: API.Picture) => {
  try {
    await deletePictureUsingPost({ id: record.id })
    message.success('删除成功')
    handleSearch()
  } catch (error) {
    message.error('删除失败: ' + error)
  }
}

const handleReview = (record: API.Picture) => {
  reviewForm.id = record.id ?? 0
  reviewVisible.value = true
}

const handleReviewSubmit = async () => {
  try {
    await reviewPictureUsingPost(reviewForm)
    message.success('审核成功')
    reviewVisible.value = false
    handleSearch()
  } catch (error) {
    message.error('审核失败: ' + error)
  }
}

const FormItem = Form.Item

onMounted(() => {
  handleSearch()
})
</script>

<template>
  <div class="picture-manage-container">
    <Card class="search-card" :bordered="false">
      <Form layout="inline" :model="formState">
        <FormItem label="图片名称" name="name">
          <Input v-model:value="formState.name" placeholder="请输入图片名称" allowClear />
        </FormItem>
        <FormItem label="分类" name="category">
          <Input v-model:value="formState.category" placeholder="请输入分类" allowClear />
        </FormItem>
        <FormItem label="审核状态" name="reviewStatus">
          <Select
            v-model:value="formState.reviewStatus"
            :options="PictureReviewStatusOptions"
            placeholder="请选择审核状态"
            allowClear
            style="width: 200px"
          >
          </Select>
        </FormItem>
        <FormItem>
          <Space :size="10">
            <Button type="primary" @click="handleSearch">
              <template #icon><SearchOutlined /></template>
              搜索
            </Button>
            <Button @click="handleReset">
              <template #icon><ReloadOutlined /></template>
              重置
            </Button>
          </Space>
        </FormItem>
      </Form>
    </Card>

    <Card class="table-card" :bordered="false">
      <Table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        row-key="id"
        :scroll="{ x: 1200 }"
      />
    </Card>

    <Modal v-model:visible="reviewVisible" title="审核图片" @ok="handleReviewSubmit">
      <Form :model="reviewForm">
        <FormItem label="审核结果">
          <Radio.Group v-model:value="reviewForm.reviewStatus">
            <Radio :value="1">通过</Radio>
            <Radio :value="2">拒绝</Radio>
          </Radio.Group>
        </FormItem>
        <FormItem label="审核备注">
          <Input.TextArea v-model:value="reviewForm.reviewMessage" placeholder="请输入审核备注" />
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<style scoped>
.picture-manage-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%);
}

.table-card {
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%);
}

:deep(.ant-form-item-label > label) {
  color: #333;
}

:deep(.ant-input) {
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.9);
}

:deep(.ant-input:hover),
:deep(.ant-input:focus) {
  border-color: white;
  box-shadow: none;
}

:deep(.ant-btn-primary) {
  background: rgba(255, 255, 255, 0.9);
  border: none;
  color: rgb(178, 163, 255);
}

:deep(.ant-btn-primary:hover) {
  background: white !important;
  color: rgb(178, 163, 255) !important;
}

:deep(.ant-table) {
  background: transparent;
}

:deep(.ant-table-thead > tr > th) {
  background: rgba(255, 255, 255, 0.5);
  color: #333;
}

:deep(.ant-table-tbody > tr > td) {
  background: rgba(255, 255, 255, 0.3);
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: rgba(255, 255, 255, 0.5) !important;
}

:deep(.ant-pagination-item-active) {
  border-color: rgb(178, 163, 255);
}

:deep(.ant-pagination-item-active a) {
  color: rgb(178, 163, 255);
}

:deep(.ant-select-selection-item) {
  color: rgb(158, 143, 235);
}

:deep(.ant-pagination-options) {
  border-color: rgb(178, 163, 255);
}

:deep(.ant-image) {
  border-radius: 5px;
  overflow: hidden;
}

/* Modal 样式 */
:deep(.ant-modal-content) {
  background: linear-gradient(135deg, rgb(178, 163, 255, 0.1) 0%, rgb(232, 255, 199, 0.1) 100%);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.ant-modal-header) {
  background: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.ant-modal-title) {
  color: #333;
}

:deep(.ant-modal-footer) {
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.ant-radio-wrapper) {
  color: #333;
}

:deep(.ant-input) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

:deep(.ant-input:hover),
:deep(.ant-input:focus) {
  border-color: rgb(178, 163, 255);
  box-shadow: 0 0 0 2px rgba(178, 163, 255, 0.2);
}

:deep(.ant-form-item-label > label) {
  color: #333;
}
</style>
