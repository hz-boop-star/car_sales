<template>
  <div class="car-list-page">
    <van-nav-bar title="车辆管理" fixed placeholder>
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
          v-for="car in list"
          :key="car.id"
          :title="`${car.brand} ${car.model}`"
          :desc="`VIN: ${car.vin}`"
          :thumb="carDefaultImg"
          @click="goToDetail(car.id)"
        >
          <template #tags>
            <van-tag :type="getStatusType(car.status)" size="medium">
              {{ formatCarStatus(car.status) }}
            </van-tag>
          </template>
          <template #price>
            <span class="price">{{ formatMoney(car.price) }}</span>
          </template>
          <template #footer>
            <div class="car-info">
              <span v-if="car.color">颜色: {{ car.color }}</span>
              <span v-if="car.year">年份: {{ car.year }}</span>
            </div>
          </template>
        </van-card>

        <!-- 空状态 -->
        <EmptyState v-if="!loading && list.length === 0" description="暂无车辆数据" />
      </van-list>
    </van-pull-refresh>

    <!-- 搜索弹窗 -->
    <van-popup v-model:show="showSearch" position="top" :style="{ height: '60%' }">
      <van-nav-bar title="搜索车辆" left-arrow @click-left="showSearch = false" />
      <van-form @submit="onSearch" class="search-form">
        <van-cell-group inset>
          <van-field
            v-model="searchParams.brand"
            name="brand"
            label="品牌"
            placeholder="请输入品牌"
            clearable
          />
          <van-field
            v-model="searchParams.status"
            name="status"
            label="状态"
            placeholder="请选择状态"
            readonly
            clickable
            @click="showStatusPicker = true"
          />
          <van-field
            v-model="searchParams.minPrice"
            name="minPrice"
            type="number"
            label="最低价格"
            placeholder="请输入最低价格"
            clearable
          />
          <van-field
            v-model="searchParams.maxPrice"
            name="maxPrice"
            type="number"
            label="最高价格"
            placeholder="请输入最高价格"
            clearable
          />
        </van-cell-group>

        <div class="search-buttons">
          <van-button block type="primary" native-type="submit">搜索</van-button>
          <van-button block plain type="default" @click="resetSearch">重置</van-button>
        </div>
      </van-form>
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast, closeToast } from 'vant'
import { carApi } from '@/api/car'
import { formatMoney, formatCarStatus } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'
import type { Car } from '@/types'
import carDefaultImg from '@/assets/images/车图.jpg'

const router = useRouter()

const list = ref<Car[]>([])
const loading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const showSearch = ref(false)
const showStatusPicker = ref(false)

const page = ref(1)
const pageSize = 20

const searchParams = reactive({
  brand: '',
  status: '',
  minPrice: '',
  maxPrice: ''
})

const statusColumns = [
  { text: '全部', value: '' },
  { text: '在库', value: '0' },
  { text: '锁定', value: '1' },
  { text: '已售', value: '2' }
]

// 获取状态标签类型
function getStatusType(status: 0 | 1 | 2) {
  const typeMap = {
    0: 'success',
    1: 'warning',
    2: 'danger'
  }
  return typeMap[status] as 'success' | 'warning' | 'danger'
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

    if (searchParams.brand) params.brand = searchParams.brand
    if (searchParams.status) params.status = Number(searchParams.status)
    if (searchParams.minPrice) params.minPrice = Number(searchParams.minPrice)
    if (searchParams.maxPrice) params.maxPrice = Number(searchParams.maxPrice)

    const res = await carApi.getCarList(params)
    
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
    console.error('加载车辆列表失败:', error)
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
  searchParams.brand = ''
  searchParams.status = ''
  searchParams.minPrice = ''
  searchParams.maxPrice = ''
}

// 状态选择确认
function onStatusConfirm(value: any) {
  searchParams.status = value.selectedValues[0]
  showStatusPicker.value = false
}

// 跳转到详情页
function goToDetail(id: string) {
  router.push(`/car/detail/${id}`)
}
</script>

<style scoped>
.car-list-page {
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

.car-info {
  display: flex;
  gap: 16px;
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
