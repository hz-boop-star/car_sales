import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { Lazyload, setToastDefaultOptions } from 'vant'
import App from './App.vue'
import router from './router'

// Vant 样式
import 'vant/lib/index.css'

// 全局样式
import './styles/variables.css'
import './styles/common.css'

// 配置 Toast 默认选项
setToastDefaultOptions({ duration: 2000 })

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Lazyload)

app.mount('#app')
