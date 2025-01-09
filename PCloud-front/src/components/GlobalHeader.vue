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
      <Col flex="90px">
        <RouterLink to="/login">
          <div class="user-login">
            <span>登录</span>
            <span>或</span>
            <span>注册</span>
          </div>
        </RouterLink>
      </Col>
    </Row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Menu, Row, Col } from 'ant-design-vue'

import type { ItemType } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { menuItems } from '@/config/menuConfig'
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
router.afterEach((to) => {
  current.value = [to.path]
})
</script>
<style scoped>
.logo {
  display: flex;
  align-items: center;
}
.logo img {
  height: 40px;
  width: 40px;
  margin-right: 10px;
}
.logo .title {
  font-size: 18px;
  font-weight: bold;
  color: black;
}
#global-header .user-login {
  margin-left: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  color: grey;
}
</style>
