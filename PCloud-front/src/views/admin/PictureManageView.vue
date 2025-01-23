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
} from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import type { API } from '@/api/typings'
import { deletePictureUsingPost, listPictureVoByPageUsingPost } from '@/api/pictureController'
import type { TablePaginationConfig } from 'ant-design-vue'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'
import type { TableColumnType } from 'ant-design-vue'

const router = useRouter()
const formState = reactive({
  name: '',
  category: '',
})

const loading = ref(false)
const tableData = ref<API.PictureVO[]>([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`,
})

const searchParams = reactive<API.PictureQueryRequest>({
  ...formState,
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

const columns: TableColumnType[] = [
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
    customRender: ({ text }: { text: string[] }) => {
      return text?.length
        ? h(Space, { wrap: true }, () => text.map((tag) => h(Tag, { color: 'blue' }, () => tag)))
        : '-'
    },
  },
  {
    title: '图片信息',
    dataIndex: 'pictureInfo',
    width: 150,
    customRender: ({ record }: { record: API.PictureVO }) => {
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
    title: '上传者',
    dataIndex: ['user', 'userName'],
    width: 100,
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
    customRender: ({ record }: { record: API.PictureVO }) => {
      return h(Space, {}, () => [
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
] as TableColumnType[]

const handleSearch = async () => {
  loading.value = true
  try {
    const res = await listPictureVoByPageUsingPost({
      ...searchParams,
      current: pagination.current,
      pageSize: pagination.pageSize,
    })
    if (res.data?.data) {
      tableData.value = res.data.data.records || []
      pagination.total = res.data.data.total || 0
    }
  } catch (error) {
    message.error('获取图片列表失败: ' + error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  formState.name = ''
  formState.category = ''
  pagination.current = 1
  handleSearch()
}

const handleTableChange = (pag: TablePaginationConfig) => {
  pagination.current = pag.current || 1
  pagination.pageSize = pag.pageSize || 10
  searchParams.current = pagination.current
  searchParams.pageSize = pagination.pageSize
  handleSearch()
}

const handleEdit = (record: API.PictureVO) => {
  router.push(`/addPicture?id=${record.id}`)
}

const handleDelete = async (record: API.PictureVO) => {
  try {
    await deletePictureUsingPost({ id: record.id })
    message.success('删除成功')
    handleSearch()
  } catch (error) {
    message.error('删除失败: ' + error)
  }
}

onMounted(() => {
  handleSearch()
})
</script>

<template>
  <div class="picture-manage-container">
    <Card class="search-card" :bordered="false">
      <Form layout="inline" :model="formState">
        <Form.Item label="图片名称" name="name">
          <Input v-model:value="formState.name" placeholder="请输入图片名称" allowClear />
        </Form.Item>
        <Form.Item label="分类" name="category">
          <Input v-model:value="formState.category" placeholder="请输入分类" allowClear />
        </Form.Item>
        <Form.Item>
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
        </Form.Item>
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
</style>
