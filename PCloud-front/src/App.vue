<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { RouterView } from 'vue-router'
import confetti from 'canvas-confetti'
import BasicLayout from '@/layout/BasicLayout.vue'

// 添加鼠标点击特效
const handleClick = (e: MouseEvent) => {
  confetti({
    particleCount: 100,
    spread: 70,
    origin: {
      x: e.clientX / window.innerWidth,
      y: e.clientY / window.innerHeight,
    },
    colors: ['#b2a3ff', '#e8ffc7', '#ffd6e7', '#c7f0ff', '#ffe4c7'],
    disableForReducedMotion: true,
    // 添加更多配置使特效更生动
    ticks: 200,
    gravity: 0.8,
    scalar: 1.2,
    shapes: ['circle', 'square'],
    zIndex: 9999,
  })
}

onMounted(() => {
  document.addEventListener('click', handleClick)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClick)
})
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
  cursor: pointer;
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

/* 防止特效影响交互元素 */
button,
input,
select,
.ant-table,
.ant-pagination,
.ant-modal {
  cursor: default;
}

/* 保持链接和可点击元素的指针样式 */
a,
.ant-btn,
.clickable {
  cursor: pointer !important;
}
</style>
