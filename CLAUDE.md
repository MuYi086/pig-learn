# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

pig-learn 是一个基于 Spring Cloud 微服务架构的 RBAC 企业快速开发平台（版本 3.9.2），同时支持微服务架构和单体架构。使用 Spring Authorization Server 实现 OAuth2 认证授权。

**技术栈**: Java 17, Spring Boot 3.5.7, Spring Cloud 2025, Spring Cloud Alibaba 2025, MyBatis Plus 3.5.14

**前端仓库**: https://gitee.com/log4j/pig-ui (Vue 3.5 + Element Plus)

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

# 指定 Java 版本编译
mvn clean install -Dmaven.compiler.release=17
```

## 运行模式

### 微服务模式
启动顺序：pig-register (Nacos) → pig-gateway → pig-auth → pig-upms-biz

### 单体模式
直接运行 pig-boot 模块，访问地址: http://localhost:9999/admin

### Docker 快速启动
```bash
docker compose up
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
│   └── pig-common-xss         # XSS 安全过滤
├── pig-upms           # 用户权限管理
│   ├── pig-upms-api   # 公共 API：Entity、DTO、Feign Client
│   └── pig-upms-biz   # 业务实现：Controller、Service [4000]
└── pig-visual         # 可视化工具
    ├── pig-monitor    # 服务监控 [5001]
    ├── pig-codegen    # 代码生成器 [5002]
    └── pig-quartz     # 定时任务 [5007]
```

## 关键架构说明

### OAuth2 认证架构
- `pig-auth` 使用 Spring Authorization Server 实现授权服务器
- 支持密码模式(password)、短信登录(sms)、授权码模式(authorization_code)、客户端凭证(client_credentials)
- Token 存储在 Redis，通过 `PigRedisOAuth2AuthorizationService` 实现
- 资源服务通过 `@EnablePigResourceServer` 注解启用，在 `pig-common-security` 中配置

### 服务间调用
- 使用 OpenFeign 进行服务间调用
- Feign Client 定义在 `pig-upms-api` 模块
- 通过 `@Inner` 注解标记内部接口，跳过权限校验
- 使用 Sentinel 进行服务熔断限流

### 数据访问
- MyBatis Plus 作为 ORM 框架
- 支持多数据源切换（`@EnableDynamicDataSource`）
- Entity 基类: `BaseEntity`，自动填充创建时间、更新时间
- Mapper XML 位于各模块的 `resources/mapper/` 目录

### 统一响应格式
- 响应包装类: `com.pig4cloud.pig.common.core.util.R`
- 错误码定义: `com.pig4cloud.pig.common.core.exception.ErrorCodes`

## 代码规范

使用 spring-javaformat 强制代码格式化，提交代码前必须执行格式化命令。IntelliJ IDEA 可安装插件: https://repo1.maven.org/maven2/io/spring/javaformat/spring-javaformat-intellij-idea-plugin/

## 配置管理

- 配置中心: Nacos，配置文件从 Nacos 动态加载
- 配置加密: 使用 jasypt-spring-boot-starter
- 本地配置文件: `application.yml` 定义基本配置和 Nacos 连接信息

## 数据库

- 数据库脚本位于 `db/pig.sql` 和 `db/pig_config.sql`
- 支持 MySQL，默认端口映射 33306