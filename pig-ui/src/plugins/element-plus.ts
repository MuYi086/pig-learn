import type { App } from 'vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'

export function setupElementPlus(app: App) {
  // 注册所有图标
  for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }

  // 使用 Element Plus
  app.use(ElementPlus, {
    locale: zhCn,
    size: 'default',
    zIndex: 3000
  })
}
