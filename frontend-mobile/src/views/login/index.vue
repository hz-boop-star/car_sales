<template>
  <div class="login-page">
    <div class="login-header">
      <h1>汽车销售系统</h1>
      <p>移动端管理平台</p>
    </div>

    <van-form @submit="onSubmit" class="login-form">
      <van-cell-group inset>
        <van-field
          v-model="formData.username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
          clearable
        />
        <van-field
          v-model="formData.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
          clearable
        />
      </van-cell-group>

      <div class="login-button">
        <van-button
          round
          block
          type="primary"
          native-type="submit"
          :loading="loading"
        >
          登录
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast } from 'vant'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formData = reactive({
  username: '',
  password: ''
})

const loading = ref(false)

async function onSubmit() {
  loading.value = true

  try {
    const res = await authApi.login(formData)
    
    // 存储 token 和用户信息
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.userInfo)

    showSuccessToast('登录成功')

    // 跳转到首页
    router.push('/home')
  } catch (error: any) {
    console.error('登录失败:', error)
    // 错误提示已在 request.ts 中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 20px;
}

.login-header {
  text-align: center;
  margin-bottom: 60px;
  color: white;
}

.login-header h1 {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px;
}

.login-header p {
  font-size: 16px;
  opacity: 0.9;
}

.login-form {
  background: transparent;
}

.login-button {
  margin-top: 30px;
  padding: 0 16px;
}
</style>
