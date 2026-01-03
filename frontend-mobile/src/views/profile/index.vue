<template>
  <div class="profile-page">
    <van-nav-bar title="个人中心" fixed placeholder />

    <div class="profile-content">
      <!-- 加载状态 -->
      <van-loading v-if="loading" size="24px" vertical>加载中...</van-loading>

      <!-- 用户信息 -->
      <div v-else-if="userInfo">
        <!-- 用户头像和基本信息 -->
        <div class="user-header">
          <van-image
            round
            width="80"
            height="80"
            :src="userDefaultImg"
          />
          <div class="user-name">{{ userInfo.realName }}</div>
          <van-tag :type="userInfo.role === 'ADMIN' ? 'primary' : 'success'" size="medium">
            {{ userInfo.role === 'ADMIN' ? '管理员' : '销售员' }}
          </van-tag>
        </div>

        <!-- 用户详细信息 -->
        <van-cell-group title="账户信息" inset>
          <van-cell title="用户名" :value="userInfo.username" />
          <van-cell title="真实姓名" :value="userInfo.realName" />
          <van-cell title="角色" :value="userInfo.role === 'ADMIN' ? '管理员' : '销售员'" />
          <van-cell v-if="userInfo.phone" title="手机号">
            <template #value>
              <a :href="`tel:${userInfo.phone}`" class="phone-link">
                {{ userInfo.phone }}
              </a>
            </template>
          </van-cell>
          <van-cell v-if="userInfo.email" title="邮箱" :value="userInfo.email" />
        </van-cell-group>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <van-button
            block
            type="danger"
            round
            @click="handleLogout"
          >
            退出登录
          </van-button>
        </div>
      </div>

      <!-- 错误状态 -->
      <div v-else class="error-state">
        <van-empty description="加载失败">
          <van-button round type="primary" @click="loadUserInfo">重试</van-button>
        </van-empty>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast, showDialog } from 'vant'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import type { User } from '@/types'
import userDefaultImg from '@/assets/images/个人头像.jpg'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const userInfo = ref<User | null>(null)

// 加载用户信息
async function loadUserInfo() {
  loading.value = true

  try {
    const res = await authApi.getUserInfo()
    userInfo.value = res.data.userInfo
    // 更新 store 中的用户信息
    userStore.setUserInfo(res.data.userInfo)
  } catch (error: any) {
    console.error('加载用户信息失败:', error)
    showFailToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 退出登录
function handleLogout() {
  showDialog({
    title: '确认退出',
    message: '确定要退出登录吗？',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      // 调用退出登录接口
      await authApi.logout(userStore.userInfo?.username)
      
      // 清除本地存储
      userStore.logout()
      
      showSuccessToast('已退出登录')
      
      // 跳转到登录页
      router.replace('/login')
    } catch (error: any) {
      console.error('退出登录失败:', error)
      // 即使接口失败，也清除本地存储并跳转
      userStore.logout()
      router.replace('/login')
    }
  }).catch(() => {
    // 用户取消
  })
}

onMounted(() => {
  // 优先使用 store 中的用户信息
  if (userStore.userInfo) {
    userInfo.value = userStore.userInfo
  }
  // 重新加载最新的用户信息
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.profile-content {
  padding: 16px 0;
  min-height: calc(100vh - 46px);
}

.user-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  background: white;
  margin-bottom: 16px;
}

.user-name {
  font-size: 20px;
  font-weight: bold;
  margin: 16px 0 8px;
}

.van-cell-group {
  margin-bottom: 16px;
}

.phone-link {
  color: #1989fa;
  text-decoration: none;
}

.action-buttons {
  padding: 16px;
}

.error-state {
  margin-top: 60px;
}
</style>
