import request from '@/utils/request'
import type { Customer, PageResult } from '@/types'

/**
 * 客户查询参数
 */
export interface CustomerQueryParams {
    page: number
    size: number
    name?: string
    phone?: string
}

/**
 * 客户相关 API
 */
export const customerApi = {
    /**
     * 查询客户列表（分页）
     */
    getCustomerList(params: CustomerQueryParams) {
        return request.get<PageResult<Customer>>('/customers', { params })
    },

    /**
     * 获取客户详情
     */
    getCustomerDetail(id: string) {
        return request.get<Customer>(`/customers/${id}`)
    }
}
