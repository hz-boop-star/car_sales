<template>
  <div class="car-list">
    <h2 class="page-title">车辆管理</h2>

    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="品牌">
          <el-input v-model="searchForm.brand" placeholder="请输入品牌" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="在库" :value="0" />
            <el-option label="锁定" :value="1" />
            <el-option label="已售" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格区间">
          <el-input-number v-model="searchForm.minPrice" :min="0" placeholder="最低价" :controls="false" style="width: 120px" />
          <span style="margin: 0 10px">-</span>
          <el-input-number v-model="searchForm.maxPrice" :min="0" placeholder="最高价" :controls="false" style="width: 120px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 显示当前筛选条件 -->
      <div v-if="hasActiveFilters" style="margin-top: 10px; display: flex; align-items: center; gap: 8px">
        <span style="color: #606266; font-size: 14px">当前查询条件：</span>
        <el-tag v-if="searchForm.brand" closable @close="searchForm.brand = ''; handleSearch()">
          品牌: {{ searchForm.brand }}
        </el-tag>
        <el-tag v-if="searchForm.status !== undefined" closable @close="searchForm.status = undefined; handleSearch()" type="success">
          状态: {{ searchForm.status === 0 ? '在库' : searchForm.status === 1 ? '锁定' : '已售' }}
        </el-tag>
        <el-tag v-if="searchForm.minPrice !== undefined || searchForm.maxPrice !== undefined" closable @close="searchForm.minPrice = undefined; searchForm.maxPrice = undefined; handleSearch()" type="warning">
          价格: {{ searchForm.minPrice || 0 }} - {{ searchForm.maxPrice || '不限' }}
        </el-tag>
      </div>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>车辆列表</span>
          <div>
            <el-button type="info" @click="handleDownloadTemplate">下载模板</el-button>
            <el-button type="success" @click="handleImport">导入 Excel</el-button>
            <el-button type="warning" @click="handleExport">导出</el-button>
            <el-button type="primary" @click="handleAdd">新增车辆</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
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
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="success">在库</el-tag>
            <el-tag v-else-if="row.status === 1" type="warning">锁定</el-tag>
            <el-tag v-else type="info">已售</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="采购日期" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="VIN码" prop="vin">
          <el-input v-model="form.vin" placeholder="请输入VIN码" />
        </el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入型号" />
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <el-input v-model="form.color" placeholder="请输入颜色" />
        </el-form-item>
        <el-form-item label="年份" prop="year">
          <el-input-number v-model="form.year" :min="1900" :max="new Date().getFullYear()" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="在库" :value="0" />
            <el-option label="锁定" :value="1" />
            <el-option label="已售" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="采购日期" prop="purchaseDate">
          <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入车辆数据"
      width="500px"
    >
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :limit="1"
        :on-change="handleFileChange"
        :on-exceed="handleExceed"
        accept=".xlsx,.xls"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx/xls 文件，且不超过 10MB
          </div>
        </template>
      </el-upload>

      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit" :loading="importing">确定导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadInstance, type UploadFile } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'
import type { Car, PageResult } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const importing = ref(false)
const dialogVisible = ref(false)
const importDialogVisible = ref(false)
const dialogTitle = ref('新增车辆')
const formRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()
const uploadFile = ref<File | null>(null)

const searchForm = reactive({
  brand: '',
  status: undefined as number | undefined,
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref<Car[]>([])

// 判断是否有激活的筛选条件
const hasActiveFilters = computed(() => {
  return searchForm.brand !== '' || 
         searchForm.status !== undefined || 
         searchForm.minPrice !== undefined || 
         searchForm.maxPrice !== undefined
})

const form = reactive<Partial<Car>>({
  vin: '',
  brand: '',
  model: '',
  color: '',
  year: new Date().getFullYear(),
  price: 0,
  status: 0,
  purchaseDate: ''
})

const rules: FormRules = {
  vin: [{ required: true, message: '请输入VIN码', trigger: 'blur' }],
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  model: [{ required: true, message: '请输入型号', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const res = await request.get<PageResult<Car>>('/cars', { params })
    tableData.value = res.data.records
    pagination.total = Number(res.data.total)  // 转换为数字
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.brand = ''
  searchForm.status = undefined
  searchForm.minPrice = undefined
  searchForm.maxPrice = undefined
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增车辆'
  Object.assign(form, {
    id: undefined,
    vin: '',
    brand: '',
    model: '',
    color: '',
    year: new Date().getFullYear(),
    price: 0,
    status: 0,
    purchaseDate: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: Car) => {
  dialogTitle.value = '编辑车辆'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      if (form.id) {
        await request.put(`/cars/${form.id}`, form)
        ElMessage.success('更新成功')
      } else {
        await request.post('/cars', form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) {
      ElMessage.error('操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleDelete = async (row: Car) => {
  try {
    await ElMessageBox.confirm('确定要删除该车辆吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await request.delete(`/cars/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleImport = () => {
  uploadFile.value = null
  importDialogVisible.value = true
}

const handleFileChange = (file: UploadFile) => {
  uploadFile.value = file.raw as File
}

const handleExceed = () => {
  ElMessage.warning('只能上传一个文件')
}

const handleImportSubmit = async () => {
  if (!uploadFile.value) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  importing.value = true
  try {
    const formData = new FormData()
    formData.append('file', uploadFile.value)

    const res = await request.post<{
      successCount: number
      failCount: number
      errors: string[]
    }>('/cars/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.data.failCount > 0) {
      ElMessageBox.alert(
        `成功导入 ${res.data.successCount} 条，失败 ${res.data.failCount} 条。\n错误信息：\n${res.data.errors.join('\n')}`,
        '导入结果',
        { type: 'warning' }
      )
    } else {
      ElMessage.success(`成功导入 ${res.data.successCount} 条数据`)
    }

    importDialogVisible.value = false
    uploadRef.value?.clearFiles()
    loadData()
  } catch (error) {
    ElMessage.error('导入失败')
  } finally {
    importing.value = false
  }
}

const handleDownloadTemplate = async () => {
  try {
    loading.value = true
    const response = await request.get('/cars/template', {
      responseType: 'blob'
    })

    // 创建下载链接
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '车辆导入模板.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('模板下载成功')
  } catch (error) {
    ElMessage.error('模板下载失败')
  } finally {
    loading.value = false
  }
}

const handleExport = async () => {
  try {
    loading.value = true
    const params = {
      ...searchForm
    }

    const response = await request.get('/cars/export', {
      params,
      responseType: 'blob'
    })

    // 创建下载链接
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `车辆列表_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.car-list {
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

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.el-icon--upload {
  font-size: 67px;
  color: #c0c4cc;
  margin: 40px 0 16px;
  line-height: 50px;
}

.el-upload__text {
  color: #606266;
  font-size: 14px;
  text-align: center;
}

.el-upload__text em {
  color: #409eff;
  font-style: normal;
}
</style>
