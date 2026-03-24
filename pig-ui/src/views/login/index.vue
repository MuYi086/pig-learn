<template>
  <div class="login-container">
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      class="login-form"
    >
      <h3 class="title">Pig 快速开发平台</h3>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="用户名"
          prefix-icon="User"
          size="large"
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="密码"
          prefix-icon="Lock"
          size="large"
          show-password
          @keyup.enter="handleLogin"
        />
      </el-form-item>
      <el-form-item prop="code">
        <div class="code-input">
          <el-input
            v-model="loginForm.code"
            placeholder="验证码"
            prefix-icon="Grid"
            size="large"
            @keyup.enter="handleLogin"
          />
          <img
            v-if="codeUrl"
            :src="codeUrl"
            class="code-img"
            @click="getCode"
          />
        </div>
      </el-form-item>
      <el-form-item>
        <el-button
          :loading="loading"
          type="primary"
          size="large"
          class="login-btn"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getCode as getCodeApi } from '@/api/login'
import type { LoginForm } from '@/types/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const codeUrl = ref('')
const randomStr = ref('')

const loginForm = reactive<LoginForm>({
  username: 'admin',
  password: '123456',
  code: '',
  randomStr: ''
})

const loginRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const getCode = async () => {
  try {
    randomStr.value = Math.random().toString(36).substring(2)
    const res = await getCodeApi()
    codeUrl.value = res.data?.image || ''
    loginForm.randomStr = res.data?.randomStr || randomStr.value
  } catch (error) {
    console.error('获取验证码失败:', error)
  }
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        await userStore.getUserInfo()
        ElMessage.success('登录成功')
        const redirect = route.query.redirect as string
        router.push(redirect || '/')
      } catch (error: any) {
        ElMessage.error(error.message || '登录失败')
        getCode()
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  getCode()
})
</script>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .login-form {
    width: 400px;
    padding: 40px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

    .title {
      text-align: center;
      margin-bottom: 30px;
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }

    .code-input {
      display: flex;
      gap: 10px;

      .el-input {
        flex: 1;
      }

      .code-img {
        width: 100px;
        height: 40px;
        border-radius: 4px;
        cursor: pointer;
      }
    }

    .login-btn {
      width: 100%;
    }
  }
}
</style>
