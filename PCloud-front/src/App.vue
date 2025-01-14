<script setup lang="ts">
import BasicLayout from '@/layout/BasicLayout.vue'
</script>

<template>
  <div id="app">
    <BasicLayout>
      <router-view />
    </BasicLayout>
  </div>
</template>

<style>
html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
}

#app {
  min-height: 100vh;
  width: 100%;
}
/* Popconfirm 全局样式 */
.ant-popover .ant-btn {
  border-radius: 4px;
  height: 32px;
  padding: 4px 15px;
  font-size: 14px;
}
.ant-popconfirm-buttons {
  margin-left: 8px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.ant-popover .ant-btn-primary {
  background: rgb(178, 163, 255) !important;
  color: white !important;
  border: none !important;
}

.ant-popover .ant-btn-primary:hover {
  background: rgb(158, 143, 235) !important;
}

.ant-popover .ant-btn-default {
  background: rgba(255, 255, 255, 0.9) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: #333 !important;
}

.ant-popover .ant-btn-default:hover {
  background: white !important;
  color: rgb(178, 163, 255) !important;
  border-color: rgb(178, 163, 255) !important;
}

.ant-popover-message {
  color: #333 !important;
}
.ant-popover-message-title {
  color: #333 !important;
}
.ant-popconfirm-message-title {
  color: #333 !important;
}

.ant-popover-inner {
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%) !important;
}

.ant-popover-arrow-content::before {
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%) !important;
}
</style>
