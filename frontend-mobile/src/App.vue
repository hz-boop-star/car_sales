<template>
  <div id="app">
    <router-view />
    <TabBar v-if="showTabBar" />
    <!-- 全局 Toast 容器 -->
    <van-toast />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import TabBar from '@/components/TabBar.vue'

const route = useRoute()

// 根据路由 meta 信息决定是否显示底部导航栏
const showTabBar = computed(() => route.meta.showTabBar === true)
</script>

<style>
/* 全局样式已在 main.ts 中引入 */
</style>
