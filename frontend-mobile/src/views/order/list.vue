<template>
  <div class="order-list-page">
    <van-nav-bar title="订单管理" fixed placeholder>
      <template #right>
        <van-icon name="search" size="18" @click="showSearch = true" />
      </template>
    </van-nav-bar>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <van-card
          v-for="order in list"
          :key="order.id"
          :title="`订单号: ${order.orderNo}`"
          :desc="`客户: ${order.customerName} | 销售: ${order.salesUserName}`"
          @click="goToDetail(order.id)"
        >
          <template #tags>
            <van-tag :type="getStatusType(order.status)" size="medium">
              {{ formatOrderStatus(order.status) }}
            </van-tag>
          </template>
          <template #price>
            <span class="price">{{ formatMoney(order.actualPrice) }}</span>
          </template>
          <template #footer>
            <div class="order-info">
              <span>车辆: {{ order.carBrand }} {{ order.carModel }}</span>
              <span>日期: {{ formatDate(order.orderDate, 'YYYY-MM-DD') }}</span>
            </div>
          </template>
        </van-card>

        <!-- 空状态 -->
        <EmptyState v-if="!loading && list.length === 0" description="暂无订单数据" />
      </van-list>
    </van-pull-refresh>

    <!-- 创建订单浮动按钮 -->
    <van-floating-bubble
      icon="plus"
      @click="goToCreate"
      axis="xy"
      v-model:offset="bubbleOffset"
    />

    <!-- 搜索弹窗 -->
    <van-popup v-model:show="showSearch" position="top" :style="{ height: '70%' }">
      <van-nav-bar title="搜索订单" left-arrow @click-left="showSearch = false" />
      <van-form @submit="onSearch" class="search-form">
        <van-cell-group inset>
          <van-field
            v-model="searchParams.startDate"
            name="startDate"
            label="开始日期"
            placeholder="请选择开始日期"
            readonly
            clickable
            @click="showStartDatePicker = true"
          />
          <van-field
            v-model="searchParams.endDate"
            name="endDate"
            label="结束日期"
            placeholder="请选择结束日期"
            readonly
            clickable
            @click="showEndDatePicker = true"
          />
          <van-field
            v-model="searchParams.salesUserName"
            name="salesUser"
            label="销售员"
            placeholder="请选择销售员"
            readonly
            clickable
            @click="showSalesUserPicker = true"
          />
          <van-field
            v-model="searchParams.statusText"
            name="status"
            label="状态"
            placeholder="请选择状态"
            readonly
            clickable
            @click="showStatusPicker = true"
          />
        </van-cell-group>

        <div class="search-buttons">
          <van-button block type="primary" native-type="submit">搜索</van-button>
          <van-button block plain type="default" @click="resetSearch">重置</van-button>
        </div>
      </van-form>
    </van-popup>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showStartDatePicker" position="bottom">
      <van-date-picker
        @confirm="onStartDateConfirm"
        @cancel="showStartDatePicker = false"
      />
    </van-popup>
    <van-popup v-model:show="showEndDatePicker" position="bottom">
      <van-date-picker
        @confirm="onEndDateConfirm"
        @cancel="showEndDatePicker = false"
      />
    </van-popup>

    <!-- 销售员选择器 -->
    <van-popup v-model:show="showSalesUserPicker" position="bottom">
      <van-picker
        :columns="salesUserColumns"
        @confirm="onSalesUserConfirm"
        @cancel="showSalesUserPicker = false"
      />
    </van-popup>

    <!-- 状态选择器 -->
    <van-popup v-model:show="showStatusPicker" position="bottom">
      <van-picker
        :columns="statusColumns"
        @confirm="onStatusConfirm"
        @cancel="showStatusPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast, closeToast } from 'vant'
import { orderApi } from '@/api/order'
import { authApi } from '@/api/auth'
import { formatMoney, formatOrderStatus, formatDate } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'
import type { OrderDetail, User } from '@/types'

const router = useRouter()

const list = ref<OrderDetail[]>([])
const loading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const showSearch = ref(false)
const showStartDatePicker = ref(false)
const showEndDatePicker = ref(false)
const showSalesUserPicker = ref(false)
const showStatusPicker = ref(false)

// 浮动按钮位置（从右下角的偏移）
const bubbleOffset = ref({ x: window.innerWidth - 72, y: window.innerHeight - 116 })

const page = ref(1)
const pageSize = 20

const searchParams = reactive({
  startDate: '',
  endDate: '',
  salesUserId: '',
  salesUserName: '',
  status: '',
  statusText: ''
})

const salesUsers = ref<User[]>([])
const salesUserColumns = ref<any[]>([])

const statusColumns = [
  { text: '全部', value: '' },
  { text: '已完成', value: '1' },
  { text: '已取消', value: '2' }
]

// 获取状态标签类型
function getStatusType(status: 1 | 2) {
  return status === 1 ? 'success' : 'default'
}

// 加载销售员列表
async function loadSalesUsers() {
  try {
    const res = await authApi.getSalespersons()
    salesUsers.value = res.data
    salesUserColumns.value = [
      { text: '全部', value: '' },
      ...res.data.map(user => ({
        text: user.realName,
        value: user.id
      }))
    ]
  } catch (error: any) {
    console.error('加载销售员列表失败:', error)
  }
}

// 加载数据
async function loadData(isRefresh = false) {
  if (isRefresh) {
    page.value = 1
    list.value = []
    finished.value = false
  }

  try {
    const params: any = {
      page: page.value,
      size: pageSize
    }

    if (searchParams.startDate) params.startDate = searchParams.startDate
    if (searchParams.endDate) params.endDate = searchParams.endDate
    if (searchParams.salesUserId) params.salesUserId = searchParams.salesUserId
    if (searchParams.status) params.status = Number(searchParams.status)

    const res = await orderApi.getOrderList(params)
    
    if (isRefresh) {
      list.value = res.data.records
    } else {
      list.value.push(...res.data.records)
    }

    // 判断是否还有更多数据
    if (list.value.length >= res.data.total) {
      finished.value = true
    }

    page.value++
  } catch (error: any) {
    console.error('加载订单列表失败:', error)
    showFailToast('加载失败')
  }
}

// 上拉加载
async function onLoad() {
  await loadData()
  loading.value = false
}

// 下拉刷新
async function onRefresh() {
  await loadData(true)
  refreshing.value = false
  closeToast()
  showSuccessToast('刷新成功')
}

// 搜索
async function onSearch() {
  showSearch.value = false
  refreshing.value = true
  await loadData(true)
  refreshing.value = false
}

// 重置搜索
function resetSearch() {
  searchParams.startDate = ''
  searchParams.endDate = ''
  searchParams.salesUserId = ''
  searchParams.salesUserName = ''
  searchParams.status = ''
  searchParams.statusText = ''
}

// 日期选择确认
function onStartDateConfirm(value: any) {
  const date = new Date(value.selectedValues.join('-'))
  searchParams.startDate = formatDate(date, 'YYYY-MM-DD')
  showStartDatePicker.value = false
}

function onEndDateConfirm(value: any) {
  const date = new Date(value.selectedValues.join('-'))
  searchParams.endDate = formatDate(date, 'YYYY-MM-DD')
  showEndDatePicker.value = false
}

// 销售员选择确认
function onSalesUserConfirm(value: any) {
  searchParams.salesUserId = value.selectedValues[0]
  searchParams.salesUserName = value.selectedOptions[0].text
  showSalesUserPicker.value = false
}

// 状态选择确认
function onStatusConfirm(value: any) {
  searchParams.status = value.selectedValues[0]
  searchParams.statusText = value.selectedOptions[0].text
  showStatusPicker.value = false
}

// 跳转到详情页
function goToDetail(id: string) {
  router.push(`/order/detail/${id}`)
}

// 跳转到创建页
function goToCreate() {
  router.push('/order/create')
}

onMounted(() => {
  loadSalesUsers()
})
</script>

<style scoped>
.order-list-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 50px;
}

.van-card {
  margin: 8px;
  cursor: pointer;
}

.price {
  color: #ee0a24;
  font-size: 16px;
  font-weight: bold;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #969799;
}

.search-form {
  padding: 16px;
}

.search-buttons {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
