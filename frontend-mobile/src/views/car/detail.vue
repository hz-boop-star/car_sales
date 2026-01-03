<template>
  <div class="car-detail-page">
    <van-nav-bar title="车辆详情" left-arrow fixed placeholder @click-left="goBack" />

    <div class="detail-content">
      <!-- 加载状态 -->
      <van-loading v-if="loading" size="24px" vertical>加载中...</van-loading>

      <!-- 车辆详情 -->
      <div v-else-if="carDetail">
        <!-- 车辆图片 -->
        <div class="car-image">
          <van-image
            width="100%"
            height="200"
            fit="cover"
            src="https://via.placeholder.com/400x200"
          />
        </div>

        <!-- 基本信息 -->
        <van-cell-group title="基本信息" inset>
          <van-cell title="品牌" :value="carDetail.brand" />
          <van-cell title="型号" :value="carDetail.model" />
          <van-cell title="VIN码" :value="carDetail.vin" />
          <van-cell title="颜色" :value="carDetail.color || '未知'" />
          <van-cell title="年份" :value="carDetail.year?.toString() || '未知'" />
        </van-cell-group>

        <!-- 价格信息 -->
        <van-cell-group title="价格信息" inset>
          <van-cell title="售价">
            <template #value>
              <span class="price">{{ formatMoney(carDetail.price) }}</span>
            </template>
          </van-cell>
        </van-cell-group>

        <!-- 状态信息 -->
        <van-cell-group title="状态信息" inset>
          <van-cell title="当前状态">
            <template #value>
              <van-tag :type="getStatusType(carDetail.status)" size="medium">
                {{ formatCarStatus(carDetail.status) }}
              </van-tag>
            </template>
          </van-cell>
          <van-cell
            v-if="carDetail.purchaseDate"
            title="采购日期"
            :value="formatDate(carDetail.purchaseDate, 'YYYY-MM-DD')"
          />
          <van-cell
            title="创建时间"
            :value="formatDate(carDetail.createTime)"
          />
          <van-cell
            title="更新时间"
            :value="formatDate(carDetail.updateTime)"
          />
        </van-cell-group>
      </div>

      <!-- 错误状态 -->
      <div v-else class="error-state">
        <van-empty description="加载失败">
          <van-button round type="primary" @click="loadDetail">重试</van-button>
        </van-empty>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showFailToast } from 'vant'
import { carApi } from '@/api/car'
import { formatMoney, formatCarStatus, formatDate } from '@/utils/format'
import type { Car } from '@/types'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const carDetail = ref<Car | null>(null)

// 获取状态标签类型
function getStatusType(status: 0 | 1 | 2) {
  const typeMap = {
    0: 'success',
    1: 'warning',
    2: 'danger'
  }
  return typeMap[status] as 'success' | 'warning' | 'danger'
}

// 加载详情
async function loadDetail() {
  loading.value = true

  try {
    const id = route.params.id as string
    const res = await carApi.getCarDetail(id)
    carDetail.value = res.data
  } catch (error: any) {
    console.error('加载车辆详情失败:', error)
    showFailToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 返回
function goBack() {
  router.back()
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.car-detail-page {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.detail-content {
  padding: 16px 0;
  min-height: calc(100vh - 46px);
}

.car-image {
  margin-bottom: 16px;
}

.van-cell-group {
  margin-bottom: 16px;
}

.price {
  color: #ee0a24;
  font-size: 18px;
  font-weight: bold;
}

.error-state {
  margin-top: 60px;
}
</style>
