<template>
  <div class="order-create-page">
    <van-nav-bar title="创建订单" left-arrow fixed placeholder @click-left="goBack" />

    <div class="form-content">
      <van-form @submit="onSubmit">
        <van-cell-group inset>
          <!-- 客户选择 -->
          <van-field
            v-model="formData.customerName"
            name="customer"
            label="客户"
            placeholder="请选择客户"
            readonly
            required
            clickable
            :rules="[{ required: true, message: '请选择客户' }]"
            @click="showCustomerPicker = true"
          >
            <template #button>
              <van-button size="small" type="primary">选择</van-button>
            </template>
          </van-field>

          <!-- 车辆选择 -->
          <van-field
            v-model="formData.carInfo"
            name="car"
            label="车辆"
            placeholder="请选择车辆"
            readonly
            required
            clickable
            :rules="[{ required: true, message: '请选择车辆' }]"
            @click="showCarPicker = true"
          >
            <template #button>
              <van-button size="small" type="primary">选择</van-button>
            </template>
          </van-field>

          <!-- 原价（自动填充） -->
          <van-field
            v-model="formData.originalPrice"
            name="originalPrice"
            type="number"
            label="原价"
            placeholder="选择车辆后自动填充"
            readonly
            required
            :rules="[{ required: true, message: '请选择车辆' }]"
          />

          <!-- 优惠金额 -->
          <van-field
            v-model="formData.discountAmount"
            name="discountAmount"
            type="number"
            label="优惠金额"
            placeholder="请输入优惠金额"
            required
            :rules="[{ required: true, message: '请输入优惠金额' }]"
            @input="calculateActualPrice"
          />

          <!-- 成交价（自动计算） -->
          <van-field
            v-model="formData.actualPrice"
            name="actualPrice"
            type="number"
            label="成交价"
            placeholder="自动计算"
            readonly
            required
          />

          <!-- 订单日期 -->
          <van-field
            v-model="formData.orderDate"
            name="orderDate"
            label="订单日期"
            placeholder="请选择订单日期"
            readonly
            required
            clickable
            :rules="[{ required: true, message: '请选择订单日期' }]"
            @click="showDatePicker = true"
          />

          <!-- 备注 -->
          <van-field
            v-model="formData.remark"
            name="remark"
            label="备注"
            type="textarea"
            placeholder="请输入备注信息（选填）"
            rows="3"
            maxlength="200"
            show-word-limit
          />
        </van-cell-group>

        <div class="submit-button">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            :loading="submitting"
          >
            提交订单
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 客户选择弹窗 -->
    <van-popup v-model:show="showCustomerPicker" position="bottom" :style="{ height: '70%' }">
      <van-nav-bar title="选择客户" left-arrow @click-left="showCustomerPicker = false" />
      <van-search
        v-model="customerSearchKeyword"
        placeholder="搜索客户姓名或手机号"
        @search="searchCustomers"
      />
      <van-list
        v-model:loading="customerLoading"
        :finished="customerFinished"
        finished-text="没有更多了"
        @load="loadCustomers"
      >
        <van-cell
          v-for="customer in customerList"
          :key="customer.id"
          :title="customer.name"
          :label="customer.phone"
          clickable
          @click="selectCustomer(customer)"
        />
        <EmptyState v-if="!customerLoading && customerList.length === 0" description="暂无客户" />
      </van-list>
    </van-popup>

    <!-- 车辆选择弹窗 -->
    <van-popup v-model:show="showCarPicker" position="bottom" :style="{ height: '70%' }">
      <van-nav-bar title="选择车辆（仅在库）" left-arrow @click-left="showCarPicker = false" />
      <van-search
        v-model="carSearchKeyword"
        placeholder="搜索车辆品牌或型号"
        @search="searchCars"
      />
      <van-list
        v-model:loading="carLoading"
        :finished="carFinished"
        finished-text="没有更多了"
        @load="loadCars"
      >
        <van-cell
          v-for="car in carList"
          :key="car.id"
          :title="`${car.brand} ${car.model}`"
          :label="`VIN: ${car.vin} | 价格: ${formatMoney(car.price)}`"
          clickable
          @click="selectCar(car)"
        >
          <template #value>
            <van-tag type="success">在库</van-tag>
          </template>
        </van-cell>
        <EmptyState v-if="!carLoading && carList.length === 0" description="暂无在库车辆" />
      </van-list>
    </van-popup>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        v-model="currentDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showFailToast, showDialog } from 'vant'
import { orderApi } from '@/api/order'
import { customerApi } from '@/api/customer'
import { carApi } from '@/api/car'
import { formatMoney, formatDate } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'
import type { Customer, Car } from '@/types'

const router = useRouter()

const submitting = ref(false)
const showCustomerPicker = ref(false)
const showCarPicker = ref(false)
const showDatePicker = ref(false)

const formData = reactive({
  customerId: '',
  customerName: '',
  carId: '',
  carInfo: '',
  originalPrice: '',
  discountAmount: '0',
  actualPrice: '',
  orderDate: formatDate(new Date(), 'YYYY-MM-DD'),
  remark: ''
})

// 客户相关
const customerList = ref<Customer[]>([])
const customerSearchKeyword = ref('')
const customerLoading = ref(false)
const customerFinished = ref(false)
const customerPage = ref(1)

// 车辆相关
const carList = ref<Car[]>([])
const carSearchKeyword = ref('')
const carLoading = ref(false)
const carFinished = ref(false)
const carPage = ref(1)

// 日期选择
const currentDate = ref(['2024', '01', '01'])

// 加载客户列表
async function loadCustomers() {
  try {
    const params: any = {
      page: customerPage.value,
      size: 20
    }
    if (customerSearchKeyword.value) {
      params.name = customerSearchKeyword.value
    }

    const res = await customerApi.getCustomerList(params)
    customerList.value.push(...res.data.records)

    if (customerList.value.length >= res.data.total) {
      customerFinished.value = true
    }

    customerPage.value++
  } catch (error: any) {
    console.error('加载客户列表失败:', error)
  } finally {
    customerLoading.value = false
  }
}

// 搜索客户
function searchCustomers() {
  customerList.value = []
  customerPage.value = 1
  customerFinished.value = false
  loadCustomers()
}

// 选择客户
function selectCustomer(customer: Customer) {
  formData.customerId = customer.id
  formData.customerName = `${customer.name} (${customer.phone})`
  showCustomerPicker.value = false
}

// 加载车辆列表（仅在库）
async function loadCars() {
  try {
    const params: any = {
      page: carPage.value,
      size: 20,
      status: 0 // 仅在库状态
    }
    if (carSearchKeyword.value) {
      params.brand = carSearchKeyword.value
    }

    const res = await carApi.getCarList(params)
    carList.value.push(...res.data.records)

    if (carList.value.length >= res.data.total) {
      carFinished.value = true
    }

    carPage.value++
  } catch (error: any) {
    console.error('加载车辆列表失败:', error)
  } finally {
    carLoading.value = false
  }
}

// 搜索车辆
function searchCars() {
  carList.value = []
  carPage.value = 1
  carFinished.value = false
  loadCars()
}

// 选择车辆
function selectCar(car: Car) {
  formData.carId = car.id
  formData.carInfo = `${car.brand} ${car.model} (${car.vin})`
  formData.originalPrice = car.price.toString()
  calculateActualPrice()
  showCarPicker.value = false
}

// 计算成交价
function calculateActualPrice() {
  const original = Number(formData.originalPrice) || 0
  const discount = Number(formData.discountAmount) || 0
  formData.actualPrice = (original - discount).toString()
}

// 日期选择确认
function onDateConfirm(value: any) {
  const date = new Date(value.selectedValues.join('-'))
  formData.orderDate = formatDate(date, 'YYYY-MM-DD')
  showDatePicker.value = false
}

// 提交订单
async function onSubmit() {
  // 验证成交价不能为负数
  if (Number(formData.actualPrice) < 0) {
    showFailToast('成交价不能为负数')
    return
  }

  submitting.value = true

  try {
    const data = {
      customerId: formData.customerId,
      carId: formData.carId,
      originalPrice: Number(formData.originalPrice),
      discountAmount: Number(formData.discountAmount),
      actualPrice: Number(formData.actualPrice),
      orderDate: formData.orderDate,
      remark: formData.remark || undefined
    }

    await orderApi.createOrder(data)

    showDialog({
      title: '成功',
      message: '订单创建成功',
      confirmButtonText: '查看订单列表'
    }).then(() => {
      router.replace('/order/list')
    })
  } catch (error: any) {
    console.error('创建订单失败:', error)
    showFailToast(error.message || '创建订单失败')
  } finally {
    submitting.value = false
  }
}

// 返回
function goBack() {
  router.back()
}

onMounted(() => {
  // 初始化日期选择器为今天
  const today = new Date()
  currentDate.value = [
    today.getFullYear().toString(),
    (today.getMonth() + 1).toString().padStart(2, '0'),
    today.getDate().toString().padStart(2, '0')
  ]
})
</script>

<style scoped>
.order-create-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 130px; /* 为底部按钮和TabBar留出空间 (50px TabBar + 80px 按钮区域) */
}

.form-content {
  padding: 16px 0;
}

.van-cell-group {
  margin-bottom: 16px;
}

.submit-button {
  position: fixed;
  bottom: 50px; /* TabBar高度 */
  left: 0;
  right: 0;
  padding: 16px;
  background-color: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  z-index: 999;
}
</style>
