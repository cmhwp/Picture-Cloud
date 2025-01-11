<template>
  <div id="global-header">
    <Row :wrap="false" align="middle">
      <Col flex="200px">
        <RouterLink to="/">
          <div class="logo">
            <img alt="logo" src="@/assets/logo.svg" />
            <span class="title">Picture Cloud</span>
          </div>
        </RouterLink>
      </Col>
      <Col flex="auto">
        <Menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        ></Menu>
      </Col>
      <Col flex="200px">
        <div class="user-login" v-if="loginStore.loginUser.userName === '未登录'">
          <span @click="doLogin" class="login-btn">登录</span>
          <span>或</span>
          <span @click="doRegister" class="login-btn">注册</span>
        </div>
        <div class="user-login" v-else>
          <Dropdown menu-item-icon="logout" @click.prevent class="user-avatar">
            <template #overlay>
              <Menu>
                <MenuItem>
                  <span @click="doUserInfo" class="logout-btn">
                    <UserOutlined />
                    用户信息
                  </span>
                </MenuItem>
                <MenuItem>
                  <span @click="doLogout" class="logout-btn">
                    <LogoutOutlined />
                    退出
                  </span>
                </MenuItem>
              </Menu>
            </template>
            <Avatar :src="userAvatar" v-if="userAvatar" />
            <Avatar v-else :size="32" shape="circle" class="default-avatar">
              <template #icon>
                <UserOutlined />
              </template>
            </Avatar>
          </Dropdown>
          <span class="login-btn">{{ loginStore.loginUser.userName }}</span>
        </div>
      </Col>
    </Row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Menu, Row, Col, Avatar, message, MenuItem, Dropdown } from 'ant-design-vue'
import { UserOutlined, LogoutOutlined } from '@ant-design/icons-vue'

import type { ItemType } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { menuItems } from '@/config/menuConfig'
import { useLoginStore } from '@/stores'
const current = ref<string[]>(['home'])
const items = menuItems
const router = useRouter()
const doMenuClick = (item: ItemType) => {
  if (item) {
    console.log(item.key)
    router.push({
      path: item.key?.toString() || '/',
    })
  }
}
const doLogin = () => {
  router.push({
    path: 'login',
  })
}
const doRegister = () => {
  router.push({
    path: 'register',
  })
}
const doUserInfo = () => {
  router.push({
    path: 'info',
  })
}
const loginStore = useLoginStore()
//获取用户头像
const userAvatar = computed(() => {
  console.log(loginStore.loginUser.userAvatar)

  return loginStore.loginUser.userAvatar
})
const doLogout = () => {
  loginStore.clearLoginUser()
  router.push({
    path: '/',
    replace: true,
  })
  message.success('退出成功')
}
router.afterEach((to) => {
  current.value = [to.path]
})
</script>
<style scoped>
#global-header {
  background-image: linear-gradient(to right, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

#global-header .ant-row {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 50px;
  height: 64px;
}

.logo {
  display: flex;
  align-items: center;
  height: 64px;
}

.logo img {
  height: 32px;
  width: 32px;
  margin-right: 12px;
}

.logo .title {
  font-size: 18px;
  font-weight: bold;
  color: #666;
}

#global-header .user-login {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 100%;
  color: #666;
}

.user-login .login-btn {
  cursor: pointer;
  padding: 0 8px;
}

.user-login span:hover {
  color: rgb(178, 163, 255);
}

:deep(.ant-menu) {
  background: transparent;
  border-bottom: none;
  display: flex;
  justify-content: flex-start;
}

:deep(.ant-menu-item) {
  color: #666 !important;
  font-size: 14px;
}

:deep(.ant-menu-item::after) {
  display: none !important;
}

:deep(.ant-menu-item-selected) {
  color: rgb(178, 163, 255) !important;
}

:deep(.ant-menu-item:hover) {
  color: rgb(178, 163, 255) !important;
}

:deep(.ant-dropdown-menu) {
  min-width: 120px;
}

:deep(.ant-dropdown-menu-item) {
  padding: 8px 16px;
}

.logout-btn {
  color: #666;
  display: block;
  width: 100%;
}

.logout-btn:hover {
  color: rgb(178, 163, 255);
}

.user-login :deep(.ant-avatar) {
  margin-right: 0;
  flex-shrink: 0;
  cursor: pointer;
}

.user-login .default-avatar {
  background-color: rgb(178, 163, 255);
}

.user-login .default-avatar .anticon {
  font-size: 18px;
  color: white;
}
</style>
