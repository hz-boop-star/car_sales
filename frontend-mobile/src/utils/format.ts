/**
 * 格式化日期
 */
export function formatDate(date: string | Date, format = 'YYYY-MM-DD HH:mm:ss'): string {
    const d = typeof date === 'string' ? new Date(date) : date

    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hour = String(d.getHours()).padStart(2, '0')
    const minute = String(d.getMinutes()).padStart(2, '0')
    const second = String(d.getSeconds()).padStart(2, '0')

    return format
        .replace('YYYY', String(year))
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hour)
        .replace('mm', minute)
        .replace('ss', second)
}

/**
 * 格式化金额
 */
export function formatMoney(amount: number): string {
    return `¥${amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

/**
 * 格式化车辆状态
 */
export function formatCarStatus(status: 0 | 1 | 2): string {
    const statusMap = {
        0: '在库',
        1: '锁定',
        2: '已售'
    }
    return statusMap[status]
}

/**
 * 格式化订单状态
 */
export function formatOrderStatus(status: 1 | 2): string {
    const statusMap = {
        1: '已完成',
        2: '已取消'
    }
    return statusMap[status]
}

/**
 * 格式化性别
 */
export function formatGender(gender?: 'M' | 'F'): string {
    if (!gender) return '未知'
    return gender === 'M' ? '男' : '女'
}
