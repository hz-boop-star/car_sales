<template>
  <div class="order-list">
    <h2 class="page-title">订单管理</h2>

    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单日期">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 280px"
          />
        </el-form-item>
        <el-form-item label="销售员">
          <el-select v-model="searchForm.salesUserId" placeholder="全部销售员" clearable filterable style="width: 150px">
            <el-option
              v-for="user in salesUserList"
              :key="user.id"
              :label="user.realName"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="已完成" :value="1" />
            <el-option label="已取消" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 显示当前筛选条件 -->
      <div v-if="hasActiveFilters" style="margin-top: 10px; display: flex; align-items: center; gap: 8px">
        <span style="color: #606266; font-size: 14px">当前查询条件：</span>
        <el-tag v-if="searchForm.dateRange && searchForm.dateRange.length === 2" closable @close="searchForm.dateRange = []; handleSearch()">
          日期: {{ searchForm.dateRange[0] }} ~ {{ searchForm.dateRange[1] }}
        </el-tag>
        <el-tag v-if="searchForm.salesUserId" closable @close="searchForm.salesUserId = undefined; handleSearch()" type="success">
          销售员: {{ getSalesUserName(searchForm.salesUserId) }}
        </el-tag>
        <el-tag v-if="searchForm.status !== undefined" closable @close="searchForm.status = undefined; handleSearch()" type="warning">
          状态: {{ searchForm.status === 1 ? '已完成' : '已取消' }}
        </el-tag>
      </div>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
          <el-button type="primary" @click="handleCreate">创建订单</el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="customerPhone" label="客户电话" width="130" />
        <el-table-column label="车辆信息" width="200">
          <template #default="{ row }">
            {{ row.carBrand }} {{ row.carModel }}
          </template>
        </el-table-column>
        <el-table-column prop="carVin" label="VIN码" width="180" />
        <el-table-column prop="actualPrice" label="成交价（元）" width="120">
          <template #default="{ row }">
            {{ row.actualPrice.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="salesUserName" label="销售员" width="100" />
        <el-table-column prop="orderDate" label="订单日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">已完成</el-tag>
            <el-tag v-else type="info">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="700px"
    >
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单日期">{{ currentOrder.orderDate }}</el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ currentOrder.customerName }}</el-descriptions-item>
        <el-descriptions-item label="客户电话">{{ currentOrder.customerPhone }}</el-descriptions-item>
        <el-descriptions-item label="车辆品牌">{{ currentOrder.carBrand }}</el-descriptions-item>
        <el-descriptions-item label="车辆型号">{{ currentOrder.carModel }}</el-descriptions-item>
        <el-descriptions-item label="VIN码" :span="2">{{ currentOrder.carVin }}</el-descriptions-item>
        <el-descriptions-item label="原价">{{ currentOrder.originalPrice.toLocaleString() }} 元</el-descriptions-item>
        <el-descriptions-item label="优惠金额">{{ currentOrder.discountAmount.toLocaleString() }} 元</el-descriptions-item>
        <el-descriptions-item label="成交价">{{ currentOrder.actualPrice.toLocaleString() }} 元</el-descriptions-item>
        <el-descriptions-item label="销售员">{{ currentOrder.salesUserName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentOrder.status === 1" type="success">已完成</el-tag>
          <el-tag v-else type="info">已取消</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import type { OrderDetail, PageResult } from '@/types'

const router = useRouter()
const loading = ref(false)
const detailVisible = ref(false)
const currentOrder = ref<OrderDetail | null>(null)
const salesUserList = ref<any[]>([])

const searchForm = reactive({
  salesUserId: undefined as string | undefined,
  status: undefined as number | undefined,
  dateRange: [] as string[]
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref<OrderDetail[]>([])

// 判断是否有激活的筛选条件
const hasActiveFilters = computed(() => {
  return (searchForm.dateRange && searchForm.dateRange.length === 2) || 
         searchForm.salesUserId !== undefined || 
         searchForm.status !== undefined
})

// 获取销售员姓名
const getSalesUserName = (userId: string | undefined) => {
  if (!userId) return ''
  const user = salesUserList.value.find(u => u.id === userId)
  return user?.realName || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size,
      salesUserId: searchForm.salesUserId,
      status: searchForm.status
    }

    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }

    const res = await request.get<PageResult<OrderDetail>>('/orders', { params })
    tableData.value = res.data.records
    pagination.total = Number(res.data.total)  // 转换为数字
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadSalesUsers = async () => {
  try {
    const res = await request.get('/auth/salespersons')
    salesUserList.value = res.data
  } catch (error) {
    console.error('加载销售员列表失败', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.salesUserId = undefined
  searchForm.status = undefined
  searchForm.dateRange = []
  handleSearch()
}

const handleCreate = () => {
  router.push('/orders/create')
}

const handleView = (row: OrderDetail) => {
  currentOrder.value = row
  detailVisible.value = true
}

onMounted(() => {
  loadSalesUsers()
  loadData()
})
</script>

<style scoped>
.order-list {
  padding: 10px;
}

.page-title {
  margin-bottom: 20px;
  color: #303133;
  font-size: 24px;
  font-weight: 500;
}

.search-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
