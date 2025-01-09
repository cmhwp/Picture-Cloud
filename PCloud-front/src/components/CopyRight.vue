<template>
  <div class="copyright">
    <span>© {{ year }} {{ author }}</span>
    <a v-if="record" :href="recordLink" target="_blank" rel="noopener noreferrer">{{ record }}</a>
  </div>
</template>

<script setup lang="ts">
interface Props {
  author?: string
  record?: string
  recordLink?: string
}

const { author, record, recordLink } = withDefaults(defineProps<Props>(), {
  author: '我不是干脆面',
  record: '豫ICP备2022029049号-1',
  recordLink: 'http://beian.miit.gov.cn/',
})

const year = new Date().getFullYear()
</script>

<style scoped>
.copyright {
  position: absolute;
  bottom: -10px;
  right: 0;
  margin: 10px 0;
  width: 100%;
  padding: 8px 16px;
  text-align: center;
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
  line-height: 1.5;
  z-index: 1000;
  transition: color 0.3s ease;
}

.copyright span {
  margin-right: 8px;
}

.copyright a {
  color: rgba(0, 0, 0, 0.45);
  text-decoration: none;
  transition: color 0.3s ease;
}

.copyright a:hover {
  color: #1890ff;
}

@media (max-width: 768px) {
  .copyright {
    font-size: 12px;
  }
}
</style>
