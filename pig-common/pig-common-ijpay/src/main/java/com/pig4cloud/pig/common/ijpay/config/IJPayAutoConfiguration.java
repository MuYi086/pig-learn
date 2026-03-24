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

import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * IJPay 自动配置类
 *
 * @author pig4cloud
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(PayProperties.class)
@ConditionalOnProperty(prefix = "pay", name = "enabled", havingValue = "true", matchIfMissing = true)
public class IJPayAutoConfiguration {

	private final PayProperties payProperties;

	@PostConstruct
	public void init() {
		// 初始化微信支付配置
		initWxPay();

		// 初始化支付宝配置
		initAliPay();

		log.info("IJPay 支付模块初始化完成");
	}

	/**
	 * 初始化微信支付
	 */
	private void initWxPay() {
		List<PayProperties.WxPayConfig> wxpayList = payProperties.getWxpay();
		if (wxpayList != null) {
			for (PayProperties.WxPayConfig config : wxpayList) {
				if (!config.isEnabled()) {
					continue;
				}

				WxPayApiConfig wxPayApiConfig = WxPayApiConfig.builder()
					.appId(config.getAppId())
					.mchId(config.getMchId())
					.partnerKey(config.getApiKey())
					.certPath(config.getCertPath())
					.build();

				WxPayApiConfigKit.putApiConfig(wxPayApiConfig);
				log.info("微信支付配置初始化完成: appId={}", config.getAppId());
			}
		}
	}

	/**
	 * 初始化支付宝
	 */
	private void initAliPay() {
		List<PayProperties.AliPayConfig> alipayList = payProperties.getAlipay();
		if (alipayList != null) {
			for (PayProperties.AliPayConfig config : alipayList) {
				if (!config.isEnabled()) {
					continue;
				}

				AliPayApiConfig aliPayApiConfig = AliPayApiConfig.builder()
					.setAppId(config.getAppId())
					.setPrivateKey(config.getPrivateKey())
					.setAliPayPublicKey(config.getAlipayPublicKey())
					.setCharset(config.getCharset())
					.setServerUrl(config.getServerUrl())
					.setSignType(config.getSignType())
					.setCertModel(false)
					.build();

				AliPayApiConfigKit.putApiConfig(aliPayApiConfig);
				log.info("支付宝配置初始化完成: appId={}", config.getAppId());
			}
		}
	}

}
