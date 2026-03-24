<template>
  <div class="error-page">
    <div class="error-content">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.error-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;

  .error-content {
    text-align: center;

    h1 {
      font-size: 120px;
      font-weight: 700;
      color: #409EFF;
      margin: 0;
    }

    p {
      font-size: 18px;
      color: #606266;
      margin: 20px 0 30px;
    }
  }
}
</style>
