<template>
  <van-empty :image="image" :description="description">
    <slot></slot>
  </van-empty>
</template>

<script setup lang="ts">
interface Props {
  image?: string
  description?: string
}

withDefaults(defineProps<Props>(), {
  image: 'default',
  description: '暂无数据'
})
</script>

<style scoped>
/* 空状态组件样式 */
</style>
