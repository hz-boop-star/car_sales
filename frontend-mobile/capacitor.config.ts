import { CapacitorConfig } from '@capacitor/cli'

const config: CapacitorConfig = {
    appId: 'com.carsales.mobile',
    appName: '汽车销售系统',
    webDir: 'dist',
    server: {
        // 开发时配置
        // url: 'http://your-dev-server-ip:5174',
        // cleartext: true
    },
    android: {
        allowMixedContent: true
    }
}

export default config
