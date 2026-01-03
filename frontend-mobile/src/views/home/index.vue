<template>
  <div class="home-page">
    <van-nav-bar title="首页" fixed placeholder />

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div class="dashboard-content">
        <!-- 加载状态 -->
        <van-loading v-if="loading" size="24px" vertical>加载中...</van-loading>

        <!-- 统计数据 -->
        <div v-else-if="!error" class="dashboard-grid">
          <div class="stat-row">
            <div class="stat-card">
              <div class="stat-value">{{ dashboardData.totalCars }}</div>
              <div class="stat-label">总车辆数</div>
            </div>
            <div class="stat-card">
              <div class="stat-value success">{{ dashboardData.availableCars }}</div>
              <div class="stat-label">可售车辆</div>
            </div>
          </div>
          <div class="stat-row">
            <div class="stat-card">
              <div class="stat-value warning">{{ dashboardData.soldCars }}</div>
              <div class="stat-label">已售车辆</div>
            </div>
            <div class="stat-card">
              <div class="stat-value primary">{{ formatMoney(dashboardData.totalRevenue) }}</div>
              <div class="stat-label">总收入</div>
            </div>
          </div>
        </div>

        <!-- 错误状态 -->
        <div v-if="error" class="error-state">
          <van-empty description="加载失败">
            <van-button round type="primary" @click="loadData">重试</van-button>
          </van-empty>
        </div>
      </div>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showSuccessToast, closeToast } from 'vant'
import { statisticsApi } from '@/api/statistics'
import { formatMoney } from '@/utils/format'
import type { DashboardData } from '@/types'

const loading = ref(false)
const refreshing = ref(false)
const error = ref(false)

const dashboardData = ref<DashboardData>({
  totalCars: 0,
  availableCars: 0,
  soldCars: 0,
  totalRevenue: 0
})

async function loadData() {
  loading.value = true
  error.value = false

  try {
    const res = await statisticsApi.getDashboard()
    dashboardData.value = res.data
  } catch (err: any) {
    console.error('加载统计数据失败:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

async function onRefresh() {
  refreshing.value = true
  error.value = false

  try {
    const res = await statisticsApi.getDashboard()
    dashboardData.value = res.data
    closeToast()
    showSuccessToast('刷新成功')
  } catch (err: any) {
    console.error('刷新失败:', err)
    error.value = true
  } finally {
    refreshing.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.dashboard-content {
  padding: 16px;
  min-height: calc(100vh - 46px);
}

.dashboard-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-row {
  display: flex;
  gap: 12px;
}

.stat-card {
  flex: 1;
  padding: 24px 16px;
  text-align: center;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #323233;
  margin-bottom: 8px;
}

.stat-value.success {
  color: #07c160;
}

.stat-value.warning {
  color: #ff976a;
}

.stat-value.primary {
  color: #1989fa;
  font-size: 20px;
}

.stat-label {
  font-size: 14px;
  color: #969799;
}

.error-state {
  margin-top: 60px;
}
</style>
