# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

pig-learn 是一个基于 Spring Cloud 微服务架构的 RBAC 企业快速开发平台（版本 3.9.2），同时支持微服务架构和单体架构。使用 Spring Authorization Server 实现 OAuth2 认证授权。

**技术栈**: Java 17, Spring Boot 3.5.7, Spring Cloud 2025, Spring Cloud Alibaba 2025, MyBatis Plus 3.5.14, Vue 3.5

## 构建命令

```bash
# 微服务模式构建（默认）
mvn clean install

# 单体模式构建
mvn clean install -Pboot

# 代码格式化（提交前必须执行）
mvn spring-javaformat:apply

# 代码格式验证
mvn spring-javaformat:validate

# 指定模块构建（如只构建 pig-auth）
mvn clean install -pl pig-auth -am

# 跳过测试构建
mvn clean install -DskipTests
```

## 运行模式

### 微服务模式
启动顺序：pig-register (Nacos) [8848] → pig-gateway [9999] → pig-auth [3000] → pig-upms-biz [4000]

```bash
# Docker 快速启动（需要 4G+ 内存）
docker compose up

# 手动启动各服务
# 1. 先启动 Nacos (pig-register)
# 2. 再启动网关 (pig-gateway)
# 3. 最后启动业务服务
```

### 单体模式
直接运行 pig-boot 模块，访问地址: http://localhost:9999/admin

```bash
# 单体模式打包
mvn clean install -Pboot
# 运行 pig-boot/target/pig-boot-*.jar
```

### 前端开发
前端项目已集成到 pig-ui 目录：

```bash
cd pig-ui
npm install
npm run dev      # 开发模式，端口 80
npm run build    # 生产构建
```

## 模块架构

```
pig
├── pig-boot           # 单体模式启动器 [9999]
├── pig-auth           # OAuth2 授权服务 [3000]
├── pig-gateway        # Spring Cloud Gateway 网关 [9999]
├── pig-register       # Nacos 注册中心 [8848]
├── pig-common         # 公共模块
│   ├── pig-common-bom         # 全局依赖版本管理
│   ├── pig-common-core        # 核心工具类、常量、异常
│   ├── pig-common-security    # OAuth2 资源服务器配置
│   ├── pig-common-feign       # Feign 扩展、Sentinel 集成
│   ├── pig-common-mybatis     # MyBatis Plus 扩展
│   ├── pig-common-datasource  # 动态数据源
│   ├── pig-common-log         # 操作日志注解
│   ├── pig-common-oss         # 文件上传
│   ├── pig-common-excel       # Excel 导入导出
│   ├── pig-common-xss         # XSS 安全过滤
│   ├── pig-common-druid       # Druid 连接池监控
│   ├── pig-common-ijpay       # 支付模块（微信、支付宝）
│   └── pig-common-wxjava      # 微信开发 SDK（公众号、小程序）
├── pig-upms           # 用户权限管理
│   ├── pig-upms-api           # 公共 API：Entity、DTO、Feign Client
│   └── pig-upms-biz           # 业务实现：Controller、Service [4000]
├── pig-visual         # 可视化工具
│   ├── pig-monitor            # 服务监控 [5001]
│   ├── pig-codegen            # 代码生成器 [5002]
│   ├── pig-quartz             # 定时任务 [5007]
│   ├── pig-flowable           # 工作流引擎 [5003]
│   ├── pig-xxl-job            # 分布式调度中心 [5004]
│   └── pig-report             # 积木报表 [5005]
└── pig-ui             # PC 前端 (Vite + Vue3 + TypeScript + Pinia)
```

## 关键架构说明

### OAuth2 认证架构
- `pig-auth` 使用 Spring Authorization Server 实现授权服务器
- 支持密码模式(password)、短信登录(sms)、授权码模式(authorization_code)、客户端凭证(client_credentials)
- Token 存储在 Redis，通过 `PigRedisOAuth2AuthorizationService` 实现
- 资源服务通过 `@EnablePigResourceServer` 注解启用，在 `pig-common-security` 中配置
- 默认客户端: `pig:pig` (client_id:client_secret)，Base64 编码: `cGlnOnBpZw==`

### 服务间调用与权限控制
- **内部调用**: 使用 `@Inner` 注解标记内部接口，跳过权限校验。注解在 `pig-common-core` 中定义，`pig-gateway` 的 `InnerFilter` 拦截处理
- **Feign 调用**: Feign Client 定义在 `pig-upms-api` 模块，服务间调用时自动传递 Token
- **Sentinel**: 熔断限流配置在 `pig-common-feign`，默认关闭，可通过配置开启

### 数据访问
- MyBatis Plus 作为 ORM 框架，分页插件在 `pig-common-mybatis` 配置
- 多数据源切换：使用 `@EnableDynamicDataSource` 开启，配合 `@DS("slave")` 切换数据源
- Entity 基类: `BaseEntity`（自动填充 createTime、updateTime），位于 `pig-common-core`
- Mapper XML 位于各模块的 `resources/mapper/` 目录

### 统一响应与异常
- 响应包装类: `com.pig4cloud.pig.common.core.util.R<T>`（code=0 为成功）
- 错误码定义: `com.pig4cloud.pig.common.core.exception.ErrorCodes`
- 全局异常处理: `GlobalExceptionHandler` 在 `pig-common-core`

### 配置管理
- 配置中心: Nacos，配置文件从 Nacos 动态加载
- 本地配置: `application.yml` 定义端口、Nacos 连接等基础配置
- 远程配置: `application-dev.yml` 和 `{service-name}-dev.yml` 存储在 Nacos
- 配置加密: 使用 jasypt-spring-boot-starter，加密值格式: `ENC(encrypted_value)`

### pig-boot 单体模式聚合
- `pig-boot` 通过 Maven 依赖聚合所有模块，形成单体应用
- 依赖顺序: pig-auth → pig-upms-biz → pig-codegen (可选) → pig-quartz (可选)
- 所有 Controller 和 Service 通过组件扫描自动加载
- 单体模式访问统一网关: http://localhost:9999/admin

## 代码规范

使用 spring-javaformat 强制代码格式化：
- **提交前必须执行**: `mvn spring-javaformat:apply`
- IDEA 插件: https://repo1.maven.org/maven2/io/spring/javaformat/spring-javaformat-intellij-idea-plugin/
- 格式验证: `mvn spring-javaformat:validate`

## 数据库与缓存

- 数据库脚本: `db/pig.sql`（业务数据）和 `db/pig_config.sql`（Nacos 配置）
- 默认端口: MySQL 33306, Redis 36379
- 多数据库支持: MySQL、PostgreSQL、Oracle、SQL Server、达梦、人大金仓

## 新增商业版模块（2026扩展）

### pig-common-druid
- Druid 连接池自动配置，监控页面: `/druid/index.html`
- 配置项: `spring.datasource.druid.*`

### pig-common-ijpay
- 微信支付: 统一下单、回调、查询、退款
- 支付宝支付: PC 网站支付、回调、查询、退款
- 配置项: `pay.wxpay[]` 和 `pay.alipay[]` 支持多商户

### pig-common-wxjava
- 微信小程序登录、用户信息获取、手机号获取
- 微信公众号消息处理
- 配置项: `wx.mp.configs[]` 和 `wx.miniapp.configs[]`

### pig-flowable
- Flowable 6.8.x 工作流引擎
- API: 流程定义管理、流程实例管理、任务管理（认领、委派、转办、驳回）
- 数据库表: ACT_* 前缀，自动创建

### pig-xxl-job
- xxl-job 2.4.x 分布式调度中心
- 端口: 5004，管理页面: `/xxl-job-admin`

### pig-report
- 积木报表 1.7.x 集成
- 端口: 5005，设计器: `/jmreport/list`

### pig-ui
- Vite + Vue 3.5 + TypeScript + Pinia + Element Plus 2.8 + TailwindCSS
- 代理配置: vite.config.ts 中 `/api` 代理到 `localhost:9999`
