import request from '@/utils/request'
import type { Car, PageResult } from '@/types'

/**
 * 车辆查询参数
 */
export interface CarQueryParams {
    page: number
    size: number
    brand?: string
    status?: 0 | 1 | 2
    minPrice?: number
    maxPrice?: number
}

/**
 * 车辆相关 API
 */
export const carApi = {
    /**
     * 查询车辆列表（分页）
     */
    getCarList(params: CarQueryParams) {
        return request.get<PageResult<Car>>('/cars', { params })
    },

    /**
     * 获取车辆详情
     */
    getCarDetail(id: string) {
        return request.get<Car>(`/cars/${id}`)
    }
}
