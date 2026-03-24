import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/login'
import type { LoginForm, UserInfo } from '@/types/user'

export const useUserStore = defineStore(
  'user',
  () => {
    // State
    const token = ref<string>('')
    const userInfo = ref<UserInfo | null>(null)
    const roles = ref<string[]>([])
    const permissions = ref<string[]>([])

    // Getters
    const isLoggedIn = computed(() => !!token.value)
    const username = computed(() => userInfo.value?.username || '')
    const nickname = computed(() => userInfo.value?.nickname || '')
    const avatar = computed(() => userInfo.value?.avatar || '')

    // Actions
    const setToken = (val: string) => {
      token.value = val
    }

    const login = async (loginForm: LoginForm) => {
      const res = await loginApi(loginForm)
      setToken(res.access_token)
      return res
    }

    const getUserInfo = async () => {
      const res = await getUserInfoApi()
      userInfo.value = res.data
      roles.value = res.data.roles || []
      permissions.value = res.data.permissions || []
      return res.data
    }

    const logout = async () => {
      try {
        await logoutApi()
      } finally {
        token.value = ''
        userInfo.value = null
        roles.value = []
        permissions.value = []
      }
    }

    return {
      token,
      userInfo,
      roles,
      permissions,
      isLoggedIn,
      username,
      nickname,
      avatar,
      setToken,
      login,
      getUserInfo,
      logout
    }
  },
  {
    persist: {
      key: 'pig-user',
      paths: ['token']
    }
  }
)
