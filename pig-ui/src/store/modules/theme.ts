import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useThemeStore = defineStore(
  'theme',
  () => {
    // State
    const sidebarCollapsed = ref(false)
    const isDark = ref(false)
    const themeColor = ref('#409EFF')
    const showSettings = ref(false)
    const tagsView = ref(true)
    const fixedHeader = ref(true)
    const sidebarLogo = ref(true)

    // Actions
    const toggleSidebar = () => {
      sidebarCollapsed.value = !sidebarCollapsed.value
    }

    const setSidebarCollapsed = (val: boolean) => {
      sidebarCollapsed.value = val
    }

    const toggleTheme = () => {
      isDark.value = !isDark.value
      updateHtmlClass()
    }

    const setThemeColor = (val: string) => {
      themeColor.value = val
    }

    const initTheme = () => {
      updateHtmlClass()
    }

    const updateHtmlClass = () => {
      const html = document.documentElement
      if (isDark.value) {
        html.classList.add('dark')
      } else {
        html.classList.remove('dark')
      }
    }

    return {
      sidebarCollapsed,
      isDark,
      themeColor,
      showSettings,
      tagsView,
      fixedHeader,
      sidebarLogo,
      toggleSidebar,
      setSidebarCollapsed,
      toggleTheme,
      setThemeColor,
      initTheme
    }
  },
  {
    persist: {
      key: 'pig-theme',
      paths: ['sidebarCollapsed', 'isDark', 'themeColor', 'tagsView', 'fixedHeader', 'sidebarLogo']
    }
  }
)
