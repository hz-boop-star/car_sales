<template>
  <div class="customer-detail-page">
    <van-nav-bar title="客户详情" left-arrow fixed placeholder @click-left="goBack" />

    <div class="detail-content">
      <!-- 加载状态 -->
      <van-loading v-if="loading" size="24px" vertical>加载中...</van-loading>

      <!-- 客户详情 -->
      <div v-else-if="customerDetail">
        <!-- 基本信息 -->
        <van-cell-group title="基本信息" inset>
          <van-cell title="姓名" :value="customerDetail.name" />
          <van-cell title="手机号">
            <template #value>
              <a :href="`tel:${customerDetail.phone}`" class="phone-link">
                {{ customerDetail.phone }}
              </a>
            </template>
          </van-cell>
          <van-cell title="身份证号" :value="customerDetail.idCard" />
          <van-cell title="性别" :value="formatGender(customerDetail.gender)" />
          <van-cell
            v-if="customerDetail.address"
            title="地址"
            :value="customerDetail.address"
          />
        </van-cell-group>

        <!-- 系统信息 -->
        <van-cell-group title="系统信息" inset>
          <van-cell
            title="创建时间"
            :value="formatDate(customerDetail.createTime)"
          />
          <van-cell
            title="更新时间"
            :value="formatDate(customerDetail.updateTime)"
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
import { customerApi } from '@/api/customer'
import { formatDate, formatGender } from '@/utils/format'
import type { Customer } from '@/types'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const customerDetail = ref<Customer | null>(null)

// 加载详情
async function loadDetail() {
  loading.value = true

  try {
    const id = route.params.id as string
    const res = await customerApi.getCustomerDetail(id)
    customerDetail.value = res.data
  } catch (error: any) {
    console.error('加载客户详情失败:', error)
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
.customer-detail-page {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.detail-content {
  padding: 16px 0;
  min-height: calc(100vh - 46px);
}

.van-cell-group {
  margin-bottom: 16px;
}

.phone-link {
  color: #1989fa;
  text-decoration: none;
}

.error-state {
  margin-top: 60px;
}
</style>
