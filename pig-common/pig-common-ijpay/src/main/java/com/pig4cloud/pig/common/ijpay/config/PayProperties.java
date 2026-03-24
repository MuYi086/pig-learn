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

package com.pig4cloud.pig.common.ijpay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 支付配置属性
 *
 * @author pig4cloud
 */
@Data
@ConfigurationProperties(prefix = "pay")
public class PayProperties {

	/**
	 * 微信支付配置列表
	 */
	private List<WxPayConfig> wxpay;

	/**
	 * 支付宝配置列表
	 */
	private List<AliPayConfig> alipay;

	/**
	 * 微信支付配置
	 */
	@Data
	public static class WxPayConfig {

		/**
		 * 应用 ID
		 */
		private String appId;

		/**
		 * 商户号
		 */
		private String mchId;

		/**
		 * API 密钥
		 */
		private String apiKey;

		/**
		 * 证书路径
		 */
		private String certPath;

		/**
		 * 证书密码
		 */
		private String certPassword;

		/**
		 * 回调地址
		 */
		private String notifyUrl;

		/**
		 * 是否启用
		 */
		private boolean enabled = true;

	}

	/**
	 * 支付宝配置
	 */
	@Data
	public static class AliPayConfig {

		/**
		 * 应用 ID
		 */
		private String appId;

		/**
		 * 私钥
		 */
		private String privateKey;

		/**
		 * 公钥
		 */
		private String publicKey;

		/**
		 * 支付宝公钥
		 */
		private String alipayPublicKey;

		/**
		 * 网关地址
		 */
		private String serverUrl = "https://openapi.alipay.com/gateway.do";

		/**
		 * 回调地址
		 */
		private String notifyUrl;

		/**
		 * 返回地址
		 */
		private String returnUrl;

		/**
		 * 签名类型
		 */
		private String signType = "RSA2";

		/**
		 * 字符编码
		 */
		private String charset = "UTF-8";

		/**
		 * 格式
		 */
		private String format = "json";

		/**
		 * 是否启用
		 */
		private boolean enabled = true;

	}

}
