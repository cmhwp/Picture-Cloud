<script setup lang="ts">
import { ref, computed } from 'vue'
import { Menu, MenuItem, Avatar, Card, Button } from 'ant-design-vue'
import {
  CheckCircleFilled,
  LikeOutlined,
  CommentOutlined,
  StarOutlined,
} from '@ant-design/icons-vue'
import { useLoginStore } from '@/stores/LoginUserStore'
const loginUserStore = useLoginStore()
const selectedKeys = ref(['dynamic'])
const userInfo = computed(() => loginUserStore.loginUser)
</script>

<template>
  <div class="user-info-container">
    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="user-avatar">
        <Avatar :size="120" :src="userInfo.userAvatar ?? ''" />
        <div class="user-name">{{ userInfo.userName }}</div>
        <div class="user-signature">{{ userInfo.userProfile ?? '这个人很懒，什么都没留下' }}</div>
      </div>
      <!-- TODO: 用户统计 -->
      <div class="user-stats">
        <div class="stat-item">
          <div class="stat-value">51</div>
          <div class="stat-label">关注数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">37</div>
          <div class="stat-label">粉丝数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">8900</div>
          <div class="stat-label">阅读量</div>
        </div>
      </div>
      <div class="online-status">
        <CheckCircleFilled class="status-icon" />
        <span>在线</span>
      </div>
      <!-- TODO: 来访用户头像展示 -->
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- TODO: 用户菜单 -->
      <Menu mode="horizontal" v-model:selectedKeys="selectedKeys">
        <MenuItem key="dynamic">动态</MenuItem>
        <MenuItem key="course">课程</MenuItem>
        <MenuItem key="video">视频</MenuItem>
        <MenuItem key="more">更多</MenuItem>
        <MenuItem key="collection">收藏</MenuItem>
        <MenuItem key="notification">通知</MenuItem>
      </Menu>

      <!-- 动态列表 -->
      <div class="activity-list">
        <Card class="activity-item" v-for="i in 2" :key="i">
          <div class="activity-header">
            <div class="activity-title">活动打卡代码</div>
            <div class="activity-time">2024-01-08 17:56</div>
          </div>
          <div class="activity-content">
            <div class="code-block">
              class Solution { public: string longestPalindrome(string s) { // 代码内容 } };
            </div>
            <div class="activity-footer">
              <div class="action-buttons">
                <Button type="text">
                  <template #icon><LikeOutlined /></template>
                  赞
                </Button>
                <Button type="text">
                  <template #icon><CommentOutlined /></template>
                  评论
                </Button>
                <Button type="text">
                  <template #icon><StarOutlined /></template>
                  收藏
                </Button>
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-info-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
  display: flex;
  gap: 20px;
}

.user-card {
  width: 300px;
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%);
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 20px;
  color: white;
}

.user-avatar {
  text-align: center;
  margin-bottom: 20px;
}

.user-name {
  font-size: 24px;
  font-weight: bold;
  margin: 12px 0;
  color: #333;
}

.user-signature {
  color: #333;
  font-size: 14px;
  line-height: 1.5;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.online-status {
  display: flex;
  align-items: center;
  margin-top: 20px;
  color: #333;
}

.status-icon {
  margin-right: 8px;
}

.main-content {
  flex: 1;
  background: linear-gradient(135deg, rgb(178, 163, 255) 0%, rgb(232, 255, 199) 100%);
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.ant-menu) {
  background: transparent;
}

:deep(.ant-menu-item) {
  color: #333 !important;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.activity-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.activity-time {
  color: #999;
  font-size: 14px;
}

.code-block {
  background: #f6f8fa;
  padding: 16px;
  border-radius: 6px;
  font-family: monospace;
  margin-bottom: 16px;
  white-space: pre;
  overflow-x: auto;
}

.activity-footer {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
  margin-top: 16px;
}

.action-buttons {
  display: flex;
  gap: 16px;
}

:deep(.ant-btn-text) {
  color: #666;
}

:deep(.ant-btn-text:hover) {
  color: rgb(178, 163, 255);
}

:deep(.ant-menu-item-selected) {
  color: rgb(178, 163, 255) !important;
}

:deep(.ant-menu-item:hover) {
  color: rgb(178, 163, 255) !important;
}

:deep(.ant-menu-item::after) {
  display: none !important;
}

.action-buttons :deep(.ant-btn:hover) {
  color: rgb(178, 163, 255) !important;
}

:deep(.ant-card-bordered) {
  border-color: #f0f0f0;
}

:deep(.ant-card-body) {
  padding: 20px;
  background: transparent;
}
</style>
