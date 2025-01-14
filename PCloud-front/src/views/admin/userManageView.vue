<script setup lang="ts">
import { ref, reactive, h, onMounted } from 'vue'
import { Card, Form, Input, Button, Table, Tag, Space, message, Popconfirm } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import type { API } from '@/api/typings'
import {
  deleteUserUsingPost,
  listUserVoByPageUsingPost,
  updateUserUsingPost,
} from '@/api/userController'
import type { TablePaginationConfig } from 'ant-design-vue'
import dayjs from 'dayjs'

const formState = reactive({
  userAccount: '',
  userName: '',
})

const loading = ref(false)
const tableData = ref<API.UserVo[]>([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => {
    return `共 ${total} 条`
  },
})
const searchParams = reactive({
  ...formState,
  currentPageNum: 1,
  pageSize: 2,
  sortField: 'createTime',
  sortOrder: 'scend',
})
const editingKey = ref('')
const editableData = reactive<Record<string, API.UserVo>>({})

const isEditing = (record: API.UserVo) => record.id?.toString() === editingKey.value

const edit = (record: API.UserVo) => {
  editableData[Number(record.id)] = { ...record }
  editingKey.value = record.id?.toString() || ''
}

const cancel = () => {
  editingKey.value = ''
}

const save = async (record: API.UserVo) => {
  try {
    const row = editableData[Number(record.id)]
    const res = await updateUserUsingPost(row)
    if (res.data?.code === 0) {
      message.success('保存成功')
      editingKey.value = ''
      delete editableData[Number(record.id)]
      handleSearch()
    } else {
      message.error('保存失败' + res.data?.message)
    }
  } catch (errInfo) {
    message.error('保存失败' + errInfo)
  }
}

const columns = [
  {
    title: '用户名',
    dataIndex: 'userAccount',
    key: 'userAccount',
    customRender: ({ text, record }: { text: string; record: API.UserVo }) => {
      const editable = isEditing(record)
      return editable
        ? h(Input, {
            value: editableData[Number(record.id)]?.userAccount,
            'onUpdate:value': (val: string) => {
              editableData[Number(record.id)] = {
                ...editableData[Number(record.id)],
                userAccount: val,
              }
            },
          })
        : text
    },
  },
  {
    title: '昵称',
    dataIndex: 'userName',
    key: 'userName',
    customRender: ({ text, record }: { text: string; record: API.UserVo }) => {
      const editable = isEditing(record)
      return editable
        ? h(Input, {
            value: editableData[Number(record.id)]?.userName,
            'onUpdate:value': (val: string) => {
              editableData[Number(record.id)] = {
                ...editableData[Number(record.id)],
                userName: val,
              }
            },
          })
        : text
    },
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    key: 'userAvatar',
    customRender: ({ text, record }: { text: string; record: API.UserVo }) => {
      const editable = isEditing(record)
      return editable
        ? h(Input, {
            value: editableData[Number(record.id)]?.userAvatar,
            'onUpdate:value': (val: string) => {
              editableData[Number(record.id)] = {
                ...editableData[Number(record.id)],
                userAvatar: val,
              }
            },
            placeholder: '请输入头像URL',
          })
        : text
          ? h('img', { src: text, style: 'width: 32px; height: 32px; border-radius: 50%;' })
          : '-'
    },
  },
  {
    title: '角色',
    dataIndex: 'userRole',
    key: 'userRole',
    customRender: ({ text }: { text: string }) => {
      const color = text === 'admin' ? 'purple' : 'green'
      return h(Tag, { color }, () => text)
    },
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    key: 'userProfile',
    customRender: ({ text, record }: { text: string; record: API.UserVo }) => {
      const editable = isEditing(record)
      return editable
        ? h(Input, {
            value: editableData[Number(record.id)]?.userProfile,
            'onUpdate:value': (val: string) => {
              editableData[Number(record.id)] = {
                ...editableData[Number(record.id)],
                userProfile: val,
              }
            },
          })
        : text || '该用户比较神秘，没有简介'
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    customRender: ({ text }: { text: string }) => {
      return text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-'
    },
  },
  {
    title: '操作',
    key: 'action',
    customRender: ({ record }: { record: API.UserVo }) => {
      const editable = isEditing(record)
      return editable
        ? h(Space, {}, () => [
            h(
              Button,
              {
                type: 'link',
                onClick: () => save(record),
              },
              () => '保存',
            ),
            h(
              Button,
              {
                type: 'link',
                onClick: cancel,
              },
              () => '取消',
            ),
          ])
        : h(Space, {}, () => [
            h(
              Button,
              {
                type: 'link',
                disabled: editingKey.value !== '',
                onClick: () => edit(record),
              },
              () => '编辑',
            ),
            h(
              Popconfirm,
              {
                title: '确定要删除此用户吗？',
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
                    disabled: editingKey.value !== '',
                  },
                  () => '删除',
                ),
              ],
            ),
          ])
    },
  },
]

const handleSearch = async () => {
  loading.value = true
  try {
    Object.assign(searchParams, {
      ...formState,
      currentPageNum: pagination.current,
      pageSize: pagination.pageSize,
    })
    const res = await listUserVoByPageUsingPost(searchParams)
    if (res.data?.data) {
      tableData.value = res.data.data.records || []
      pagination.total = Number(res.data.data.total) || 0
    }
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  formState.userAccount = ''
  formState.userName = ''
  pagination.current = 1
  handleSearch()
}

const handleTableChange = (pag: TablePaginationConfig) => {
  if (pag.current && pag.pageSize) {
    pagination.current = pag.current
    pagination.pageSize = pag.pageSize
    handleSearch()
  }
}

const handleDelete = async (record: API.UserVo) => {
  try {
    await deleteUserUsingPost({ id: record.id })
    message.success('删除成功')
    handleSearch()
  } catch (error) {
    message.error('删除失败' + error)
  }
}

onMounted(() => {
  handleSearch()
})
</script>

<template>
  <div class="user-manage-container">
    <Card class="search-card" :bordered="false">
      <Form layout="inline" :model="formState">
        <Form.Item label="用户名" name="userAccount">
          <Input v-model:value="formState.userAccount" placeholder="请输入用户名" allowClear />
        </Form.Item>
        <Form.Item label="昵称" name="userName">
          <Input v-model:value="formState.userName" placeholder="请输入昵称" allowClear />
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
        :scroll="{ x: 1000 }"
      />
    </Card>
  </div>
</template>

<style scoped>
@media screen and (max-width: 768px) {
  .user-manage-container {
    padding: 10px;
  }
}
.user-manage-container {
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

:deep(.ant-popover-buttons .ant-btn-primary) {
  background: rgb(178, 163, 255);
  color: white;
}

:deep(.ant-popover-buttons .ant-btn-primary:hover) {
  background: rgb(158, 143, 235);
  color: white !important;
}

:deep(.ant-popover-buttons .ant-btn-default:hover) {
  color: rgb(178, 163, 255);
  border-color: rgb(178, 163, 255);
}

:deep(.ant-popover-message-title) {
  color: #333;
}

:deep(.ant-popover-inner) {
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%);
}

:deep(.ant-popover-arrow-content::before) {
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%);
}
</style>
