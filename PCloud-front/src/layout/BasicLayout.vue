<template>
  <div id="basic-layout">
    <Layout style="min-height: 100vh;">
      <LayoutHeader class="header">
        <GlobalHeader/>
      </LayoutHeader>
      <LayoutContent>
      <RouterView></RouterView>
      </LayoutContent>
      <LayoutFooter class="fixed-footer">
        <CopyRight/>
      </LayoutFooter>
    </Layout>
  </div>
</template>

<script setup lang="ts">
import CopyRight from '@/components/CopyRight.vue';
import { Layout,LayoutHeader,LayoutFooter,LayoutContent } from 'ant-design-vue';
import { RouterView } from 'vue-router';
import GlobalHeader from '@/components/GlobalHeader.vue';
</script>
<style scoped>
#basic-layout {
  display: flex;
  flex-direction: column;
}
#basic-layout .header {
  background: #ffffff;
  color: unset;
  padding-inline: 30px;
  box-shadow: 0 2px 8px #f0f1f2;
  margin-bottom: 20px;
}
.fixed-footer {
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
}
</style>
