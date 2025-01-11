<template>
  <div id="basic-layout">
    <Layout>
      <LayoutHeader>
        <GlobalHeader />
      </LayoutHeader>
      <LayoutContent class="main-content">
        <RouterView></RouterView>
      </LayoutContent>
      <LayoutFooter class="fixed-footer">
        <CopyRight />
      </LayoutFooter>
    </Layout>
  </div>
</template>

<script setup lang="ts">
import CopyRight from '@/components/CopyRight.vue'
import { Layout, LayoutHeader, LayoutFooter, LayoutContent } from 'ant-design-vue'
import { RouterView } from 'vue-router'
import GlobalHeader from '@/components/GlobalHeader.vue'
</script>

<style scoped>
#basic-layout {
  min-height: 100vh;
}

:deep(.ant-layout) {
  min-height: 100vh;
  background: transparent;
}

:deep(.ant-layout-header) {
  padding: 0;
  height: auto;
  line-height: normal;
  background: transparent;
  width: 100%;
}

.main-content {
  padding: 0 50px;
  margin: 20px auto;
  max-width: 1400px;
  width: 100%;
  background: transparent;
}

.fixed-footer {
  position: fixed;
  bottom: 0;
  width: 100%;
  background: transparent;
  padding: 0;
}
</style>
