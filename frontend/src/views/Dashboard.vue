<template>
  <div class="dashboard">
    <h2 class="page-title">数据概览</h2>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">总车辆数</div>
            <div class="stat-value">{{ dashboardData.totalCars }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card available">
          <div class="stat-content">
            <div class="stat-label">可售车辆</div>
            <div class="stat-value">{{ dashboardData.availableCars }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card sold">
          <div class="stat-content">
            <div class="stat-label">已售车辆</div>
            <div class="stat-value">{{ dashboardData.soldCars }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card revenue">
          <div class="stat-content">
            <div class="stat-label">总收入（万元）</div>
            <div class="stat-value">{{ (dashboardData.totalRevenue / 10000).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>品牌销量占比</span>
          </template>
          <div ref="brandChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>月度销售趋势</span>
          </template>
          <div ref="trendChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

interface BrandDistribution {
  brand: string
  count: string
}

interface MonthlyTrend {
  month: string
  sales: string
}

interface DashboardData {
  totalCars: string
  availableCars: string
  soldCars: string
  totalOrders: string
  totalRevenue: number
  brandDistribution: BrandDistribution[]
  monthlyTrend: MonthlyTrend[]
}

const dashboardData = ref<DashboardData>({
  totalCars: '0',
  availableCars: '0',
  soldCars: '0',
  totalOrders: '0',
  totalRevenue: 0,
  brandDistribution: [],
  monthlyTrend: []
})

const brandChartRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()
let brandChart: ECharts | null = null
let trendChart: ECharts | null = null

const loadDashboard = async () => {
  try {
    const res = await request.get<DashboardData>('/statistics/dashboard')
    dashboardData.value = res.data
    
    // 渲染品牌销量图表
    if (brandChartRef.value && res.data.brandDistribution?.length > 0) {
      brandChart = echarts.init(brandChartRef.value)
      brandChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center'
        },
        series: [
          {
            type: 'pie',
            radius: '60%',
            data: res.data.brandDistribution.map(item => ({
              name: item.brand,
              value: Number(item.count)
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      })
    }
    
    // 渲染月度销售趋势图表
    if (trendChartRef.value && res.data.monthlyTrend?.length > 0) {
      // 按月份排序（从早到晚）
      const sortedTrend = [...res.data.monthlyTrend].sort((a, b) => 
        a.month.localeCompare(b.month)
      )
      
      trendChart = echarts.init(trendChartRef.value)
      trendChart.setOption({
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['销量']
        },
        xAxis: {
          type: 'category',
          data: sortedTrend.map(item => item.month)
        },
        yAxis: {
          type: 'value',
          name: '销量'
        },
        series: [
          {
            name: '销量',
            type: 'bar',
            data: sortedTrend.map(item => Number(item.sales)),
            itemStyle: { color: '#409eff' }
          }
        ]
      })
    }
  } catch (error) {
    ElMessage.error('加载仪表盘数据失败')
  }
}

onMounted(() => {
  loadDashboard()

  window.addEventListener('resize', () => {
    brandChart?.resize()
    trendChart?.resize()
  })
})

onUnmounted(() => {
  brandChart?.dispose()
  trendChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 10px;
}

.page-title {
  margin-bottom: 20px;
  color: #303133;
  font-size: 24px;
  font-weight: 500;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-card.available {
  border-top: 3px solid #67c23a;
}

.stat-card.sold {
  border-top: 3px solid #409eff;
}

.stat-card.revenue {
  border-top: 3px solid #e6a23c;
}

.stat-content {
  padding: 20px 0;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.charts-row {
  margin-top: 20px;
}
</style>
