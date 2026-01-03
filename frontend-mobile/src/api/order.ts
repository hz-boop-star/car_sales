import request from '@/utils/request'
import type { Order, OrderDetail, PageResult } from '@/types'

/**
 * 订单查询参数
 */
export interface OrderQueryParams {
    page: number
    size: number
    startDate?: string
    endDate?: string
    salesUserId?: string
    status?: 1 | 2
}

/**
 * 订单创建请求
 */
export interface OrderCreateRequest {
    customerId: string
    carId: string
    originalPrice: number
    discountAmount: number
    actualPrice: number
    orderDate: string
    remark?: string
}

/**
 * 订单相关 API
 */
export const orderApi = {
    /**
     * 查询订单列表（分页）
     */
    getOrderList(params: OrderQueryParams) {
        return request.get<PageResult<OrderDetail>>('/orders', { params })
    },

    /**
     * 获取订单详情
     */
    getOrderDetail(id: string) {
        return request.get<OrderDetail>(`/orders/${id}`)
    },

    /**
     * 创建订单
     */
    createOrder(data: OrderCreateRequest) {
        return request.post<Order>('/orders', data)
    }
}
