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

package com.pig4cloud.pig.common.wxjava.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * WxJava 配置属性
 *
 * @author pig4cloud
 */
@Data
@ConfigurationProperties(prefix = "wx")
public class WxJavaProperties {

	/**
	 * 是否启用
	 */
	private boolean enabled = true;

	/**
	 * 公众号配置
	 */
	private Mp mp;

	/**
	 * 小程序配置
	 */
	private Miniapp miniapp;

	/**
	 * 公众号配置
	 */
	@Data
	public static class Mp {

		/**
		 * 配置列表
		 */
		private List<MpConfig> configs;

	}

	/**
	 * 公众号配置项
	 */
	@Data
	public static class MpConfig {

		/**
		 * 应用 ID
		 */
		private String appId;

		/**
		 * 应用密钥
		 */
		private String secret;

		/**
		 * 令牌
		 */
		private String token;

		/**
		 * 消息加解密密钥
		 */
		private String aesKey;

	}

	/**
	 * 小程序配置
	 */
	@Data
	public static class Miniapp {

		/**
		 * 配置列表
		 */
		private List<MiniappConfig> configs;

	}

	/**
	 * 小程序配置项
	 */
	@Data
	public static class MiniappConfig {

		/**
		 * 应用 ID
		 */
		private String appid;

		/**
		 * 应用密钥
		 */
		private String secret;

		/**
		 * 令牌
		 */
		private String token;

		/**
		 * 消息加解密密钥
		 */
		private String aesKey;

	}

}
