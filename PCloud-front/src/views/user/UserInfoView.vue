<script setup lang="ts">
import { ref } from 'vue'
import { Form, Input, Button, Avatar, Card } from 'ant-design-vue'
import { LockOutlined, MobileOutlined, IdcardOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useLoginStore } from '@/stores/LoginUserStore'
const userStore = useLoginStore()
const formState = ref({
  userName: userStore.loginUser.userName,
  userEmail: '2232706035@qq.com',
  userProfile: userStore.loginUser.userProfile ?? '该用户还没有填写个人简介',
  organization: '等风来，不如追风去——禅小和尚',
})

const handleUpdateAvatar = () => {
  // TODO 更新头像
  message.success('更新头像')
}
</script>

<template>
  <div class="user-info-container">
    <div class="user-info-left">
      <Card class="avatar-card">
        <div class="avatar-wrapper">
          <Avatar :size="120" :src="userStore.loginUser.userAvatar" />
          <Button class="update-avatar-btn" @click="handleUpdateAvatar">更新头像</Button>
        </div>
      </Card>
    </div>
    <div class="user-info-right">
      <Card title="个人信息" :bordered="false">
        <Form :model="formState" layout="vertical">
          <Form.Item label="用户名：">
            <Input v-model:value="formState.userName" placeholder="请输入用户名" />
          </Form.Item>
          <Form.Item label="邮件地址：">
            <Input v-model:value="formState.userEmail" placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item label="个人简介：">
            <Input.TextArea v-model:value="formState.userProfile" placeholder="请输入个人简介" />
          </Form.Item>
          <Form.Item label="组织：">
            <Input v-model:value="formState.organization" placeholder="请输入组织" />
          </Form.Item>
        </Form>
      </Card>

      <Card title="账号安全" :bordered="false" class="security-card">
        <div class="security-item">
          <div class="security-info">
            <LockOutlined class="security-icon" />
            <span>密码</span>
          </div>
          <div class="security-status">
            <span class="status">已设置</span>
            <a class="action-link">修改密码</a>
          </div>
        </div>
        <div class="security-item">
          <div class="security-info">
            <MobileOutlined class="security-icon" />
            <span>手机</span>
          </div>
          <div class="security-status">
            <span class="status">198*****780</span>
            <a class="action-link">更换手机</a>
          </div>
        </div>
        <div class="security-item">
          <div class="security-info">
            <IdcardOutlined class="security-icon" />
            <span>实名认证</span>
          </div>
          <div class="security-status">
            <span class="status">已实名认证</span>
            <a class="action-link">查看详情</a>
          </div>
        </div>
      </Card>
    </div>
  </div>
</template>

<style scoped>
.user-info-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
  display: flex;
  gap: 20px;
}

.user-info-left {
  width: 280px;
}

.user-info-right {
  flex: 1;
}

.avatar-card {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%);
  border: none;
}

.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.user-info-right :deep(.ant-card) {
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%);
}

:deep(.ant-card) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: none;
}

:deep(.ant-card-head) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  background: transparent;
}

:deep(.ant-card-head-title) {
  color: #333;
}

.security-card {
  margin-top: 20px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.security-item:last-child {
  border-bottom: none;
}

.security-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
}

.security-icon {
  font-size: 20px;
  color: #333;
}

.security-status {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status {
  color: #333;
}

.action-link {
  color: #333;
  cursor: pointer;
  text-decoration: underline;
}

.action-link:hover {
  color: white;
}

:deep(.ant-form-item-label > label) {
  color: #333;
  font-weight: 500;
}

:deep(.ant-input),
:deep(.ant-input-textarea) {
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.9);
}

:deep(.ant-input:hover),
:deep(.ant-input-textarea:hover),
:deep(.ant-input:focus),
:deep(.ant-input-textarea:focus) {
  border-color: white;
  box-shadow: none;
}

:deep(.ant-input-focused) {
  box-shadow: none;
}

.update-avatar-btn {
  width: 100%;
  background: rgba(255, 255, 255, 0.9);
  border: none;
}

.update-avatar-btn:hover {
  background: white !important;
  color: rgb(178, 163, 255) !important;
}
</style>
