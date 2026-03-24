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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Druid 连接池配置属性
 *
 * @author pig4cloud
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource.druid")
public class DruidProperties {

	/**
	 * 是否启用 Druid 连接池
	 */
	private boolean enabled = true;

	/**
	 * 初始化连接数
	 */
	private int initialSize = 5;

	/**
	 * 最小连接数
	 */
	private int minIdle = 5;

	/**
	 * 最大连接数
	 */
	private int maxActive = 20;

	/**
	 * 获取连接等待超时时间（毫秒）
	 */
	private int maxWait = 60000;

	/**
	 * 检测间隔时间（毫秒）
	 */
	private int timeBetweenEvictionRunsMillis = 60000;

	/**
	 * 连接在池中最小生存时间（毫秒）
	 */
	private int minEvictableIdleTimeMillis = 300000;

	/**
	 * 连接在池中最大生存时间（毫秒）
	 */
	private int maxEvictableIdleTimeMillis = 900000;

	/**
	 * 检测连接是否有效的 SQL
	 */
	private String validationQuery = "SELECT 1";

	/**
	 * 申请连接时检测
	 */
	private boolean testWhileIdle = true;

	/**
	 * 申请连接时检测
	 */
	private boolean testOnBorrow = false;

	/**
	 * 归还连接时检测
	 */
	private boolean testOnReturn = false;

	/**
	 * 是否开启 PSCache
	 */
	private boolean poolPreparedStatements = true;

	/**
	 * PSCache 大小
	 */
	private int maxPoolPreparedStatementPerConnectionSize = 20;

	/**
	 * 过滤器配置
	 */
	private String filters = "stat,wall,slf4j";

	/**
	 * 合并 SQL
	 */
	private boolean mergeSql = true;

	/**
	 * 慢 SQL 阈值（毫秒）
	 */
	private int slowSqlMillis = 1000;

	/**
	 * 是否记录慢 SQL
	 */
	private boolean logSlowSql = true;

	/**
	 * 监控配置
	 */
	private StatViewServlet statViewServlet = new StatViewServlet();

	/**
	 * Web 监控配置
	 */
	private WebStatFilter webStatFilter = new WebStatFilter();

	@Data
	public static class StatViewServlet {

		/**
		 * 是否启用监控页面
		 */
		private boolean enabled = true;

		/**
		 * 访问路径
		 */
		private String urlPattern = "/druid/*";

		/**
		 * 登录用户名
		 */
		private String loginUsername = "admin";

		/**
		 * 登录密码
		 */
		private String loginPassword = "admin";

		/**
		 * 允许访问的 IP（空表示允许所有）
		 */
		private String allow = "";

		/**
		 * 拒绝访问的 IP
		 */
		private String deny = "";

		/**
		 * 是否允许重置数据
		 */
		private boolean resetEnable = false;

	}

	@Data
	public static class WebStatFilter {

		/**
		 * 是否启用 Web 监控
		 */
		private boolean enabled = true;

		/**
		 * 拦截路径
		 */
		private String urlPattern = "/*";

		/**
		 * 排除路径
		 */
		private String exclusions = "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*";

	}

}
