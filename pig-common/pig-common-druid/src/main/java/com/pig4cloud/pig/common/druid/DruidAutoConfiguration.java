/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pig4cloud.pig.common.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.spring.boot3.autoconfigure.stat.DruidFilterConfiguration;
import com.alibaba.druid.spring.boot3.autoconfigure.stat.DruidStatViewServletConfiguration;
import com.alibaba.druid.spring.boot3.autoconfigure.stat.DruidWebStatFilterConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Druid 连接池自动配置
 *
 * @author pig4cloud
 */
@Slf4j
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnProperty(prefix = "spring.datasource.druid", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DruidProperties.class)
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
@Import({ DruidStatViewServletConfiguration.class, DruidWebStatFilterConfiguration.class, DruidFilterConfiguration.class })
@RequiredArgsConstructor
public class DruidAutoConfiguration {

	private final DruidProperties druidProperties;

	/**
	 * 配置 Druid 数据源
	 * @param dataSourceProperties Spring 数据源属性
	 * @return DruidDataSource
	 */
	@Bean
	@ConditionalOnProperty(prefix = "spring.datasource", name = "type", havingValue = "com.alibaba.druid.pool.DruidDataSource", matchIfMissing = true)
	public DataSource dataSource(DataSourceProperties dataSourceProperties) {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(dataSourceProperties.getUrl());
		dataSource.setUsername(dataSourceProperties.getUsername());
		dataSource.setPassword(dataSourceProperties.getPassword());
		dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());

		// 基础连接池配置
		dataSource.setInitialSize(druidProperties.getInitialSize());
		dataSource.setMinIdle(druidProperties.getMinIdle());
		dataSource.setMaxActive(druidProperties.getMaxActive());
		dataSource.setMaxWait(druidProperties.getMaxWait());

		// 连接检测配置
		dataSource.setTimeBetweenEvictionRunsMillis(druidProperties.getTimeBetweenEvictionRunsMillis());
		dataSource.setMinEvictableIdleTimeMillis(druidProperties.getMinEvictableIdleTimeMillis());
		dataSource.setMaxEvictableIdleTimeMillis(druidProperties.getMaxEvictableIdleTimeMillis());
		dataSource.setValidationQuery(druidProperties.getValidationQuery());
		dataSource.setTestWhileIdle(druidProperties.isTestWhileIdle());
		dataSource.setTestOnBorrow(druidProperties.isTestOnBorrow());
		dataSource.setTestOnReturn(druidProperties.isTestOnReturn());

		// PSCache 配置
		dataSource.setPoolPreparedStatements(druidProperties.isPoolPreparedStatements());
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(
				druidProperties.getMaxPoolPreparedStatementPerConnectionSize());

		// 过滤器配置
		try {
			dataSource.setFilters(druidProperties.getFilters());
		}
		catch (SQLException e) {
			log.error("Druid 过滤器配置失败: {}", e.getMessage(), e);
		}

		// 合并 SQL、慢 SQL 监控
		System.setProperty("druid.stat.mergeSql", String.valueOf(druidProperties.isMergeSql()));
		System.setProperty("druid.stat.slowSqlMillis", String.valueOf(druidProperties.getSlowSqlMillis()));
		System.setProperty("druid.stat.logSlowSql", String.valueOf(druidProperties.isLogSlowSql()));

		log.info("Druid 数据源初始化完成, 初始连接数: {}, 最大连接数: {}", druidProperties.getInitialSize(),
				druidProperties.getMaxActive());

		return dataSource;
	}

}
