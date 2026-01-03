import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
    appId: 'com.carsales.app',
    appName: '汽车销售系统',
    webDir: 'dist',
    server: {
        // 开发时使用内网穿透地址，记得替换成你的实际地址
        // url: 'https://your-tunnel-url.com',
        // cleartext: true
    },
    android: {
        allowMixedContent: true // 允许 HTTP 请求（如果用 HTTP 内网穿透）
    }
};

export default config;
