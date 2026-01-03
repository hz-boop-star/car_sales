<template>
  <van-tabbar v-model="active" @change="onChange" fixed placeholder>
    <van-tabbar-item name="home" icon="home-o" to="/home">首页</van-tabbar-item>
    <van-tabbar-item name="car" icon="logistics" to="/car/list">车辆</van-tabbar-item>
    <van-tabbar-item name="customer" icon="friends-o" to="/customer/list">客户</van-tabbar-item>
    <van-tabbar-item name="order" icon="orders-o" to="/order/list">订单</van-tabbar-item>
    <van-tabbar-item name="profile" icon="user-o" to="/profile">我的</van-tabbar-item>
  </van-tabbar>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const active = ref('home')

// 根据当前路由设置激活的标签
watch(
  () => route.path,
  (path) => {
    if (path.startsWith('/home')) {
      active.value = 'home'
    } else if (path.startsWith('/car')) {
      active.value = 'car'
    } else if (path.startsWith('/customer')) {
      active.value = 'customer'
    } else if (path.startsWith('/order')) {
      active.value = 'order'
    } else if (path.startsWith('/profile')) {
      active.value = 'profile'
    }
  },
  { immediate: true }
)

function onChange(name: string) {
  active.value = name
}
</script>

<style scoped>
/* 底部导航栏样式可以在这里自定义 */
</style>
