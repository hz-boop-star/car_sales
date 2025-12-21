import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { ElMessage, ElMessageBox } from 'element-plus'
import CustomerList from './CustomerList.vue'

// Mock request module
vi.mock('@/utils/request', () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
        put: vi.fn(),
        delete: vi.fn()
    }
}))

// Mock router
vi.mock('vue-router', () => ({
    useRouter: () => ({
        push: vi.fn()
    }),
    useRoute: () => ({
        path: '/customers'
    })
}))

describe('CustomerList.vue', () => {
    beforeEach(() => {
        vi.clearAllMocks()
    })

    it('应该正确渲染客户列表组件', () => {
        const wrapper = mount(CustomerList, {
            global: {
                stubs: {
                    'el-card': true,
                    'el-form': true,
                    'el-form-item': true,
                    'el-input': true,
                    'el-button': true,
                    'el-table': true,
                    'el-table-column': true,
                    'el-pagination': true,
                    'el-dialog': true,
                    'el-radio-group': true,
                    'el-radio': true
                }
            }
        })

        expect(wrapper.find('.customer-list').exists()).toBe(true)
        expect(wrapper.find('.page-title').text()).toBe('客户管理')
    })

    it('应该包含姓名和手机号搜索表单', () => {
        const wrapper = mount(CustomerList, {
            global: {
                stubs: {
                    'el-card': true,
                    'el-form': true,
                    'el-form-item': true,
                    'el-input': true,
                    'el-button': true,
                    'el-table': true,
                    'el-table-column': true,
                    'el-pagination': true,
                    'el-dialog': true
                }
            }
        })

        // 验证搜索表单存在
        expect(wrapper.find('.search-card').exists()).toBe(true)
    })

    it('应该包含新增客户按钮', () => {
        const wrapper = mount(CustomerList, {
            global: {
                stubs: {
                    'el-card': true,
                    'el-form': true,
                    'el-form-item': true,
                    'el-input': true,
                    'el-button': true,
                    'el-table': true,
                    'el-table-column': true,
                    'el-pagination': true,
                    'el-dialog': true
                }
            }
        })

        expect(wrapper.find('.card-header').exists()).toBe(true)
    })

    it('应该包含客户列表表格', () => {
        const wrapper = mount(CustomerList, {
            global: {
                stubs: {
                    'el-card': true,
                    'el-form': true,
                    'el-form-item': true,
                    'el-input': true,
                    'el-button': true,
                    'el-table': true,
                    'el-table-column': true,
                    'el-pagination': true,
                    'el-dialog': true
                }
            }
        })

        expect(wrapper.find('.table-card').exists()).toBe(true)
    })

    it('应该包含分页组件', () => {
        const wrapper = mount(CustomerList, {
            global: {
                stubs: {
                    'el-card': true,
                    'el-form': true,
                    'el-form-item': true,
                    'el-input': true,
                    'el-button': true,
                    'el-table': true,
                    'el-table-column': true,
                    'el-pagination': true,
                    'el-dialog': true
                }
            }
        })

        // 验证分页组件存在
        const pagination = wrapper.findComponent({ name: 'el-pagination' })
        expect(pagination.exists()).toBe(true)
    })
})
