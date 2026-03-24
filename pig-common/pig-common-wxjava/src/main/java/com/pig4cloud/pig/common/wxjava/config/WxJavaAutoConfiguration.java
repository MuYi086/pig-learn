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

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.message.WxMaMessageHandler;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import cn.binarywang.wx.mp.api.WxMpService;
import cn.binarywang.wx.mp.api.impl.WxMpServiceImpl;
import cn.binarywang.wx.mp.config.impl.WxMpDefaultConfigImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WxJava 自动配置类
 *
 * @author pig4cloud
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WxJavaProperties.class)
@ConditionalOnProperty(prefix = "wx", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WxJavaAutoConfiguration {

	private final WxJavaProperties wxJavaProperties;

	@PostConstruct
	public void init() {
		log.info("WxJava 微信模块初始化完成");
	}

	/**
	 * 微信公众号服务
	 * @return WxMpService
	 */
	@Bean
	@ConditionalOnProperty(prefix = "wx.mp", name = "configs")
	public WxMpService wxMpService() {
		WxMpService service = new WxMpServiceImpl();
		List<WxJavaProperties.MpConfig> mpConfigs = wxJavaProperties.getMp().getConfigs();

		if (mpConfigs != null) {
			for (WxJavaProperties.MpConfig config : mpConfigs) {
				WxMpDefaultConfigImpl mpConfig = new WxMpDefaultConfigImpl();
				mpConfig.setAppId(config.getAppId());
				mpConfig.setSecret(config.getSecret());
				mpConfig.setToken(config.getToken());
				mpConfig.setAesKey(config.getAesKey());
				service.addConfigStorage(config.getAppId(), mpConfig);
				log.info("微信公众号配置初始化完成: appId={}", config.getAppId());
			}
		}

		return service;
	}

	/**
	 * 微信小程序服务
	 * @return WxMaService
	 */
	@Bean
	@ConditionalOnProperty(prefix = "wx.miniapp", name = "configs")
	public WxMaService wxMaService() {
		WxMaService service = new WxMaServiceImpl();
		List<WxJavaProperties.MiniappConfig> miniappConfigs = wxJavaProperties.getMiniapp().getConfigs();

		if (miniappConfigs != null) {
			for (WxJavaProperties.MiniappConfig config : miniappConfigs) {
				WxMaDefaultConfigImpl maConfig = new WxMaDefaultConfigImpl();
				maConfig.setAppid(config.getAppid());
				maConfig.setSecret(config.getSecret());
				maConfig.setToken(config.getToken());
				maConfig.setAesKey(config.getAesKey());
				service.addConfig(config.getAppid(), maConfig);
				log.info("微信小程序配置初始化完成: appid={}", config.getAppid());
			}
		}

		return service;
	}

}
