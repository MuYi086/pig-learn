import type { App } from 'vue'
import { hasPermission, hasRole } from '@/utils/permission'

export function setupPermission(app: App) {
  // v-permission 指令
  app.directive('permission', {
    mounted(el, binding) {
      const { value } = binding
      if (value && value.length > 0) {
        const has = hasPermission(value)
        if (!has) {
          el.parentNode?.removeChild(el)
        }
      }
    }
  })

  // v-role 指令
  app.directive('role', {
    mounted(el, binding) {
      const { value } = binding
      if (value && value.length > 0) {
        const has = hasRole(value)
        if (!has) {
          el.parentNode?.removeChild(el)
        }
      }
    }
  })
}
