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

package com.pig4cloud.pig.common.wxjava.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import com.pig4cloud.pig.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信小程序 Controller
 *
 * @author pig4cloud
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wx/ma")
@Tag(name = "微信小程序", description = "微信小程序接口")
public class WxMaController {

	private final WxMaService wxMaService;

	/**
	 * 小程序登录
	 * @param appid 小程序 appid
	 * @param code 登录凭证
	 * @return R
	 */
	@GetMapping("/login/{appid}")
	@Operation(summary = "小程序登录", description = "微信小程序登录获取 session_key 和 openid")
	public R login(@PathVariable String appid, @RequestParam String code) {
		try {
			if (!wxMaService.switchover(appid)) {
				return R.failed(String.format("未找到对应 appid=[%s] 的配置", appid));
			}

			WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
			log.info("小程序登录成功: appid={}, openid={}", appid, session.getOpenid());

			return R.ok(session);
		}
		catch (WxErrorException e) {
			log.error("小程序登录失败: {}", e.getMessage(), e);
			return R.failed("登录失败: " + e.getMessage());
		}
		finally {
			WxMaConfigHolder.remove();
		}
	}

	/**
	 * 获取用户信息
	 * @param appid 小程序 appid
	 * @param sessionKey 会话密钥
	 * @param signature 签名
	 * @param rawData 原始数据
	 * @param encryptedData 加密数据
	 * @param iv 加密算法的初始向量
	 * @return R
	 */
	@GetMapping("/user/info/{appid}")
	@Operation(summary = "获取用户信息", description = "获取微信小程序用户信息")
	public R getUserInfo(@PathVariable String appid,
			@RequestParam String sessionKey,
			@RequestParam String signature,
			@RequestParam String rawData,
			@RequestParam String encryptedData,
			@RequestParam String iv) {
		try {
			if (!wxMaService.switchover(appid)) {
				return R.failed(String.format("未找到对应 appid=[%s] 的配置", appid));
			}

			// 验证签名
			if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
				return R.failed("用户信息签名验证失败");
			}

			WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
			return R.ok(userInfo);
		}
		catch (Exception e) {
			log.error("获取用户信息失败: {}", e.getMessage(), e);
			return R.failed("获取用户信息失败: " + e.getMessage());
		}
		finally {
			WxMaConfigHolder.remove();
		}
	}

	/**
	 * 获取手机号
	 * @param appid 小程序 appid
	 * @param sessionKey 会话密钥
	 * @param encryptedData 加密数据
	 * @param iv 加密算法的初始向量
	 * @return R
	 */
	@GetMapping("/phone/{appid}")
	@Operation(summary = "获取手机号", description = "获取微信小程序用户手机号")
	public R getPhone(@PathVariable String appid,
			@RequestParam String sessionKey,
			@RequestParam String encryptedData,
			@RequestParam String iv) {
		try {
			if (!wxMaService.switchover(appid)) {
				return R.failed(String.format("未找到对应 appid=[%s] 的配置", appid));
			}

			WxMaPhoneNumberInfo phoneInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
			return R.ok(phoneInfo);
		}
		catch (Exception e) {
			log.error("获取手机号失败: {}", e.getMessage(), e);
			return R.failed("获取手机号失败: " + e.getMessage());
		}
		finally {
			WxMaConfigHolder.remove();
		}
	}

}
