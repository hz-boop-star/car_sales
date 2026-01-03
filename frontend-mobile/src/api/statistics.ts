import request from '@/utils/request'
import type { DashboardData } from '@/types'

/**
 * 统计相关 API
 */
export const statisticsApi = {
    /**
     * 获取仪表盘统计数据
     */
    getDashboard() {
        return request.get<DashboardData>('/statistics/dashboard')
    }
}
