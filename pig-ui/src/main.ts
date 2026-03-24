import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { setupStore } from './store'
import { setupElementPlus } from './plugins/element-plus'
import { setupPermission } from './directives/permission'
import './styles/index.scss'

const app = createApp(App)

// 配置状态管理
setupStore(app)

// 配置 Element Plus
setupElementPlus(app)

// 配置权限指令
setupPermission(app)

// 配置路由
app.use(router)

// 挂载应用
app.mount('#app')
