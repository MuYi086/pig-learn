# Pig 开源免费版 vs 商业版 详细对比

## 📊 版本对比总览

| 维度 | 开源免费版 (3.9.2) | 商业版 (2026) |
|------|-------------------|---------------|
| **定位** | 基础 RBAC 快速开发平台 | 全栈企业级解决方案 |
| **前端** | Vue 3.5 + Element Plus | Vue 3 + ElementPlus + **Vite + Pinia + TailwindCSS + TS** |
| **跨端** | ❌ 不支持 | ✅ **Uniapp (H5/小程序/App)** |
| **信创适配** | ❌ 不支持 | ✅ **全面国产化支持** |

---

## 🔍 详细差异分析

### 1. 前端技术栈升级

**开源版：**
- Vue 3.5 + Element Plus 基础组合

**商业版新增：**
| 技术 | 用途 |
|-----|------|
| **Vite** | 极速构建工具，替代 Webpack |
| **Pinia** | Vue 官方推荐状态管理（替代 Vuex）|
| **TailwindCSS** | 原子化 CSS 框架 |
| **TypeScript** | 类型安全开发 |
| **Uniapp** | 一套代码编译到 H5、小程序、App |

### 2. 后端功能扩展

**开源版核心：**
- Spring Boot 3.5.7 + Spring Cloud 2025
- Spring Authorization Server (OAuth2)
- MyBatis Plus 3.5.14 + Redis + Nacos

**商业版额外功能：**

```diff
+ Druid 数据库连接池（带监控）
+ SpringCloudLoadbalancer（替代 Ribbon）
+ Seata 分布式事务（AT/TCC/Saga/XA）
+ Flowable 工作流引擎（BPMN 2.0）
+ xxl-job 分布式任务调度
+ Quartz 定时任务
+ knife4j 接口文档美化
+ 报表引擎（UReport2/积木报表）
+ S3 OSS 对象存储（兼容 Minio）
+ wxjava 微信开发 SDK
+ IJPay 聚合支付（微信/支付宝/银联）
+ WebSocket 实时通信
+ Hutool 工具类库
```

### 3. 数据库支持

| 类型 | 开源版 | 商业版 |
|-----|-------|-------|
| MySQL | ✅ | ✅ |
| Oracle | ❌ | ✅ |
| SQL Server | ❌ | ✅ |
| PostgreSQL | ❌ | ✅ |
| Redis | ✅ | ✅ |
| **达梦 (DM)** | ❌ | ✅ |
| **人大金仓** | ❌ | ✅ |
| **瀚高** | ❌ | ✅ |
| **南大通用** | ❌ | ✅ |

### 4. 信创国产化支持（商业版独有）

**操作系统迁移方案：**
- CentOS → **银河麒麟 V10**
- Windows Server → **统信 UOS V20A**
- Ubuntu → **统信 UOS V20A**

### 5. 企业级特性（商业版独有）

1. **支付能力** - 微信/支付宝/银联聚合支付
2. **工作流** - Flowable BPMN 2.0 标准工作流
3. **分布式事务** - Seata 多模式事务管理
4. **报表系统** - 低代码报表/积木报表
5. **微信生态** - 公众号/小程序/支付完整 SDK
6. **多端统一** - Uniapp 跨端开发

---

## 🎯 选型建议

**选择开源版如果你：**
- 预算有限，需要基础 RBAC 权限管理
- 技术团队能自主维护和扩展
- 仅需标准 MySQL + 常规微服务架构

**选择商业版如果你：**
- 需要**信创国产化适配**（政府/国企/金融）
- 需要**工作流引擎、支付、报表**等企业级功能
- 需要**跨端应用**（小程序、App）
- 需要**分布式事务**保障数据一致性
- 需要**Oracle/SQL Server** 等企业数据库支持
