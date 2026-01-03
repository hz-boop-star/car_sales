<template>
  <div class="customer-list-page">
    <van-nav-bar title="客户管理" fixed placeholder>
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
        <van-cell
          v-for="customer in list"
          :key="customer.id"
          :title="customer.name"
          :label="`身份证: ${customer.idCard}`"
          is-link
          @click="goToDetail(customer.id)"
        >
          <template #icon>
            <img :src="customerDefaultImg" class="customer-avatar" />
          </template>
          <template #value>
            <a :href="`tel:${customer.phone}`" class="phone-link" @click.stop>
              {{ customer.phone }}
            </a>
          </template>
        </van-cell>

        <!-- 空状态 -->
        <EmptyState v-if="!loading && list.length === 0" description="暂无客户数据" />
      </van-list>
    </van-pull-refresh>

    <!-- 搜索弹窗 -->
    <van-popup v-model:show="showSearch" position="top" :style="{ height: '50%' }">
      <van-nav-bar title="搜索客户" left-arrow @click-left="showSearch = false" />
      <van-form @submit="onSearch" class="search-form">
        <van-cell-group inset>
          <van-field
            v-model="searchParams.name"
            name="name"
            label="姓名"
            placeholder="请输入姓名（模糊搜索）"
            clearable
          />
          <van-field
            v-model="searchParams.phone"
            name="phone"
            label="手机号"
            placeholder="请输入手机号（精确搜索）"
            clearable
          />
        </van-cell-group>

        <div class="search-buttons">
          <van-button block type="primary" native-type="submit">搜索</van-button>
          <van-button block plain type="default" @click="resetSearch">重置</van-button>
        </div>
      </van-form>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast, closeToast } from 'vant'
import { customerApi } from '@/api/customer'
import EmptyState from '@/components/EmptyState.vue'
import type { Customer } from '@/types'
import customerDefaultImg from '@/assets/images/客户头像.avif'

const router = useRouter()

const list = ref<Customer[]>([])
const loading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const showSearch = ref(false)

const page = ref(1)
const pageSize = 20

const searchParams = reactive({
  name: '',
  phone: ''
})

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

    if (searchParams.name) params.name = searchParams.name
    if (searchParams.phone) params.phone = searchParams.phone

    const res = await customerApi.getCustomerList(params)
    
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
    console.error('加载客户列表失败:', error)
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
  searchParams.name = ''
  searchParams.phone = ''
}

// 跳转到详情页
function goToDetail(id: string) {
  router.push(`/customer/detail/${id}`)
}
</script>

<style scoped>
.customer-list-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 50px;
}

.van-cell {
  margin: 8px;
  border-radius: 8px;
}

.phone-link {
  color: #1989fa;
  text-decoration: none;
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

.customer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 12px;
  object-fit: cover;
}
</style>
