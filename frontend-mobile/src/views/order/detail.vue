<template>
  <div class="order-detail-page">
    <van-nav-bar title="订单详情" left-arrow fixed placeholder @click-left="goBack" />

    <div class="detail-content">
      <!-- 加载状态 -->
      <van-loading v-if="loading" size="24px" vertical>加载中...</van-loading>

      <!-- 订单详情 -->
      <div v-else-if="orderDetail">
        <!-- 订单状态 -->
        <div class="status-banner">
          <van-tag :type="getStatusType(orderDetail.status)" size="large">
            {{ formatOrderStatus(orderDetail.status) }}
          </van-tag>
        </div>

        <!-- 订单信息 -->
        <van-cell-group title="订单信息" inset>
          <van-cell title="订单号" :value="orderDetail.orderNo" />
          <van-cell title="订单日期" :value="formatDate(orderDetail.orderDate, 'YYYY-MM-DD')" />
          <van-cell title="销售员" :value="orderDetail.salesUserName" />
        </van-cell-group>

        <!-- 客户信息 -->
        <van-cell-group title="客户信息" inset>
          <van-cell title="客户姓名" :value="orderDetail.customerName" />
          <van-cell title="联系电话">
            <template #value>
              <a :href="`tel:${orderDetail.customerPhone}`" class="phone-link">
                {{ orderDetail.customerPhone }}
              </a>
            </template>
          </van-cell>
        </van-cell-group>

        <!-- 车辆信息 -->
        <van-cell-group title="车辆信息" inset>
          <van-cell title="品牌型号" :value="`${orderDetail.carBrand} ${orderDetail.carModel}`" />
          <van-cell title="VIN码" :value="orderDetail.carVin" />
        </van-cell-group>

        <!-- 价格信息 -->
        <van-cell-group title="价格信息" inset>
          <van-cell title="原价" :value="formatMoney(orderDetail.originalPrice)" />
          <van-cell title="优惠金额" :value="formatMoney(orderDetail.discountAmount)" />
          <van-cell title="成交价">
            <template #value>
              <span class="price">{{ formatMoney(orderDetail.actualPrice) }}</span>
            </template>
          </van-cell>
        </van-cell-group>

        <!-- 备注信息 -->
        <van-cell-group v-if="orderDetail.remark" title="备注信息" inset>
          <van-cell :value="orderDetail.remark" />
        </van-cell-group>

        <!-- 系统信息 -->
        <van-cell-group title="系统信息" inset>
          <van-cell title="创建时间" :value="formatDate(orderDetail.createTime)" />
          <van-cell title="更新时间" :value="formatDate(orderDetail.updateTime)" />
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
import { orderApi } from '@/api/order'
import { formatMoney, formatOrderStatus, formatDate } from '@/utils/format'
import type { OrderDetail } from '@/types'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const orderDetail = ref<OrderDetail | null>(null)

// 获取状态标签类型
function getStatusType(status: 1 | 2) {
  return status === 1 ? 'success' : 'default'
}

// 加载详情
async function loadDetail() {
  loading.value = true

  try {
    const id = route.params.id as string
    const res = await orderApi.getOrderDetail(id)
    orderDetail.value = res.data
  } catch (error: any) {
    console.error('加载订单详情失败:', error)
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
.order-detail-page {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.detail-content {
  padding: 16px 0;
  min-height: calc(100vh - 46px);
}

.status-banner {
  text-align: center;
  padding: 20px;
  background: white;
  margin-bottom: 16px;
}

.van-cell-group {
  margin-bottom: 16px;
}

.phone-link {
  color: #1989fa;
  text-decoration: none;
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
