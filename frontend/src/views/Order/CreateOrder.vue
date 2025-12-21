<template>
  <div class="create-order">
    <h2 class="page-title">创建订单</h2>

    <el-card>
      <el-steps :active="currentStep" finish-status="success" align-center>
        <el-step title="选择客户" />
        <el-step title="选择车辆" />
        <el-step title="确认订单" />
      </el-steps>

      <div class="step-content">
        <!-- 步骤1：选择客户 -->
        <div v-show="currentStep === 0" class="step-panel">
          <el-form :model="searchCustomer" :inline="true">
            <el-form-item label="客户姓名">
              <el-input v-model="searchCustomer.name" placeholder="请输入客户姓名" clearable />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="searchCustomer.phone" placeholder="请输入手机号" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadCustomers">查询</el-button>
            </el-form-item>
          </el-form>

          <el-table
            :data="customerList"
            border
            highlight-current-row
            @current-change="handleCustomerSelect"
            v-loading="loadingCustomers"
          >
            <el-table-column type="index" width="50" />
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="phone" label="手机号" width="150" />
            <el-table-column prop="idCard" label="身份证号" width="200" />
            <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="customerPagination.current"
              v-model:page-size="customerPagination.size"
              :total="customerPagination.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadCustomers"
              @current-change="loadCustomers"
            />
          </div>
        </div>

        <!-- 步骤2：选择车辆 -->
        <div v-show="currentStep === 1" class="step-panel">
          <el-form :model="searchCar" :inline="true">
            <el-form-item label="品牌">
              <el-input v-model="searchCar.brand" placeholder="请输入品牌" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadCars">查询</el-button>
            </el-form-item>
          </el-form>

          <el-table
            :data="carList"
            border
            highlight-current-row
            @current-change="handleCarSelect"
            v-loading="loadingCars"
          >
            <el-table-column type="index" width="50" />
            <el-table-column prop="vin" label="VIN码" width="180" />
            <el-table-column prop="brand" label="品牌" width="120" />
            <el-table-column prop="model" label="型号" width="150" />
            <el-table-column prop="color" label="颜色" width="100" />
            <el-table-column prop="year" label="年份" width="100" />
            <el-table-column prop="price" label="价格（元）" width="120">
              <template #default="{ row }">
                {{ row.price.toLocaleString() }}
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="carPagination.current"
              v-model:page-size="carPagination.size"
              :total="carPagination.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadCars"
              @current-change="loadCars"
            />
          </div>
        </div>

        <!-- 步骤3：确认订单 -->
        <div v-show="currentStep === 2" class="step-panel">
          <el-form
            ref="orderFormRef"
            :model="orderForm"
            :rules="orderRules"
            label-width="120px"
            style="max-width: 600px; margin: 0 auto"
          >
            <el-divider content-position="left">客户信息</el-divider>
            <el-form-item label="客户姓名">
              <el-input :value="selectedCustomer?.name" disabled />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input :value="selectedCustomer?.phone" disabled />
            </el-form-item>

            <el-divider content-position="left">车辆信息</el-divider>
            <el-form-item label="车辆">
              <el-input :value="`${selectedCar?.brand} ${selectedCar?.model}`" disabled />
            </el-form-item>
            <el-form-item label="VIN码">
              <el-input :value="selectedCar?.vin" disabled />
            </el-form-item>
            <el-form-item label="原价">
              <el-input :value="selectedCar?.price.toLocaleString() + ' 元'" disabled />
            </el-form-item>

            <el-divider content-position="left">订单信息</el-divider>
            <el-form-item label="优惠金额" prop="discountAmount">
              <el-input-number
                v-model="orderForm.discountAmount"
                :min="0"
                :max="selectedCar?.price || 0"
                :precision="2"
                @change="calculateActualPrice"
              />
            </el-form-item>
            <el-form-item label="成交价">
              <el-input :value="orderForm.actualPrice.toLocaleString() + ' 元'" disabled />
            </el-form-item>
            <el-form-item label="订单日期" prop="orderDate">
              <el-date-picker
                v-model="orderForm.orderDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item label="备注">
              <el-input
                v-model="orderForm.remark"
                type="textarea"
                :rows="3"
                placeholder="请输入备注信息"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="step-actions">
        <el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
        <el-button v-if="currentStep < 2" type="primary" @click="nextStep" :disabled="!canNext">
          下一步
        </el-button>
        <el-button v-if="currentStep === 2" type="success" @click="handleSubmit" :loading="submitting">
          提交订单
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import request from '@/utils/request'
import type { Customer, Car } from '@/types'

const router = useRouter()
const currentStep = ref(0)
const loadingCustomers = ref(false)
const loadingCars = ref(false)
const submitting = ref(false)
const orderFormRef = ref<FormInstance>()

const searchCustomer = reactive({
  name: '',
  phone: ''
})

const searchCar = reactive({
  brand: ''
})

const customerList = ref<Customer[]>([])
const carList = ref<Car[]>([])
const selectedCustomer = ref<Customer | null>(null)
const selectedCar = ref<Car | null>(null)

const customerPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const carPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const orderForm = reactive({
  customerId: '',
  carId: '',
  originalPrice: 0,
  discountAmount: 0,
  actualPrice: 0,
  orderDate: new Date().toISOString().split('T')[0],
  remark: ''
})

const orderRules: FormRules = {
  discountAmount: [{ required: true, message: '请输入优惠金额', trigger: 'blur' }],
  orderDate: [{ required: true, message: '请选择订单日期', trigger: 'change' }]
}

const canNext = computed(() => {
  if (currentStep.value === 0) return selectedCustomer.value !== null
  if (currentStep.value === 1) return selectedCar.value !== null
  return false
})

const loadCustomers = async () => {
  loadingCustomers.value = true
  try {
    const params = {
      current: customerPagination.current,
      size: customerPagination.size,
      ...searchCustomer
    }
    const res = await request.get('/customers', { params })
    customerList.value = res.data.records
    customerPagination.total = Number(res.data.total)
  } catch (error) {
    ElMessage.error('加载客户列表失败')
  } finally {
    loadingCustomers.value = false
  }
}

const loadCars = async () => {
  loadingCars.value = true
  try {
    const params = {
      current: carPagination.current,
      size: carPagination.size,
      status: 0, // 只显示在库车辆
      ...searchCar
    }
    const res = await request.get('/cars', { params })
    carList.value = res.data.records
    carPagination.total = Number(res.data.total)
  } catch (error) {
    ElMessage.error('加载车辆列表失败')
  } finally {
    loadingCars.value = false
  }
}

const handleCustomerSelect = (row: Customer | null) => {
  selectedCustomer.value = row
}

const handleCarSelect = (row: Car | null) => {
  selectedCar.value = row
}

const calculateActualPrice = () => {
  if (selectedCar.value) {
    orderForm.actualPrice = selectedCar.value.price - orderForm.discountAmount
  }
}

const nextStep = () => {
  if (currentStep.value === 0 && selectedCustomer.value) {
    currentStep.value++
    loadCars()
  } else if (currentStep.value === 1 && selectedCar.value) {
    orderForm.customerId = selectedCustomer.value!.id
    orderForm.carId = selectedCar.value.id
    orderForm.originalPrice = selectedCar.value.price
    orderForm.actualPrice = selectedCar.value.price
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const handleSubmit = async () => {
  if (!orderFormRef.value) return

  await orderFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      await request.post('/orders', orderForm)
      ElMessage.success('订单创建成功')
      router.push('/orders')
    } catch (error) {
      ElMessage.error('订单创建失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleCancel = () => {
  router.back()
}

// 初始加载客户列表
loadCustomers()
</script>

<style scoped>
.create-order {
  padding: 10px;
}

.page-title {
  margin-bottom: 20px;
  color: #303133;
  font-size: 24px;
  font-weight: 500;
}

.step-content {
  margin: 40px 0;
  min-height: 400px;
}

.step-panel {
  padding: 20px;
}

.step-actions {
  text-align: center;
  padding: 20px;
  border-top: 1px solid #ebeef5;
}

.step-actions .el-button {
  margin: 0 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
