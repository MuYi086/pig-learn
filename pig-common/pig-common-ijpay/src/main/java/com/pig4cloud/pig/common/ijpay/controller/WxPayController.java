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

package com.pig4cloud.pig.common.ijpay.controller;

import com.ijpay.core.enums.SignType;
import com.ijpay.core.kit.HttpKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.model.UnifiedOrderModel;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信支付 Controller
 *
 * @author pig4cloud
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay/wx")
@Tag(name = "微信支付", description = "微信支付接口")
public class WxPayController {

	/**
	 * 统一下单
	 * @param request 请求
	 * @return R
	 */
	@SysLog("微信支付统一下单")
	@PostMapping("/unified-order")
	@Operation(summary = "统一下单", description = "微信统一下单接口")
	public R unifiedOrder(HttpServletRequest request, @RequestBody Map<String, Object> params) {
		try {
			String appId = params.get("appId").toString();
			String mchId = params.get("mchId").toString();
			String body = params.get("body").toString();
			String outTradeNo = params.get("outTradeNo").toString();
			String totalFee = params.get("totalFee").toString();
			String notifyUrl = params.get("notifyUrl").toString();
			String tradeType = params.getOrDefault("tradeType", "NATIVE").toString();
			String productId = params.getOrDefault("productId", "").toString();
			String openId = params.getOrDefault("openId", "").toString();

			String ip = HttpKit.getRealIp(request);

			Map<String, String> result = WxPayApi.unifiedOrder(UnifiedOrderModel.builder()
				.appid(appId)
				.mch_id(mchId)
				.nonce_str(WxPayKit.generateStr())
				.body(body)
				.out_trade_no(outTradeNo)
				.total_fee(totalFee)
				.spbill_create_ip(ip)
				.notify_url(notifyUrl)
				.trade_type(tradeType)
				.product_id(productId)
				.openid(openId)
				.build(),
				WxPayApiConfigKit.getWxPayApiConfig().getPartnerKey(),
				SignType.HMACSHA256);

			return R.ok(result);
		}
		catch (Exception e) {
			log.error("微信支付统一下单失败: {}", e.getMessage(), e);
			return R.failed("支付失败: " + e.getMessage());
		}
	}

	/**
	 * 支付回调
	 * @param request 请求
	 * @return 回调响应
	 */
	@PostMapping("/callback")
	@Operation(summary = "支付回调", description = "微信支付结果通知")
	public String callback(HttpServletRequest request) {
		try {
			String xmlMsg = HttpKit.readData(request);
			log.info("微信支付回调: {}", xmlMsg);

			Map<String, String> params = WxPayKit.xmlToMap(xmlMsg);
			String returnCode = params.get("return_code");

			if (WxPayKit.codeVerify(params, WxPayApiConfigKit.getWxPayApiConfig().getPartnerKey(), returnCode)) {
				// 处理支付成功逻辑
				String outTradeNo = params.get("out_trade_no");
				String transactionId = params.get("transaction_id");
				log.info("支付成功: outTradeNo={}, transactionId={}", outTradeNo, transactionId);

				return WxPayKit.toXml(params);
			}
			else {
				log.error("支付回调验签失败");
				return WxPayKit.toXml(params);
			}
		}
		catch (Exception e) {
			log.error("支付回调处理失败: {}", e.getMessage(), e);
			return "fail";
		}
	}

	/**
	 * 查询订单
	 * @param outTradeNo 商户订单号
	 * @return R
	 */
	@GetMapping("/order/{outTradeNo}")
	@Operation(summary = "查询订单", description = "查询微信支付订单状态")
	public R queryOrder(@PathVariable String outTradeNo) {
		try {
			Map<String, String> result = WxPayApi.orderQuery(outTradeNo, null,
					WxPayApiConfigKit.getWxPayApiConfig().getMchId(),
					WxPayApiConfigKit.getWxPayApiConfig().getAppId(),
					WxPayKit.generateStr(),
					WxPayApiConfigKit.getWxPayApiConfig().getPartnerKey(),
					SignType.HMACSHA256);
			return R.ok(result);
		}
		catch (Exception e) {
			log.error("订单查询失败: {}", e.getMessage(), e);
			return R.failed("订单查询失败: " + e.getMessage());
		}
	}

	/**
	 * 申请退款
	 * @param params 参数
	 * @return R
	 */
	@SysLog("申请退款")
	@PostMapping("/refund")
	@Operation(summary = "申请退款", description = "微信申请退款")
	public R refund(@RequestBody Map<String, Object> params) {
		try {
			String outTradeNo = params.get("outTradeNo").toString();
			String outRefundNo = params.get("outRefundNo").toString();
			String totalFee = params.get("totalFee").toString();
			String refundFee = params.get("refundFee").toString();
			String refundDesc = params.getOrDefault("refundDesc", "").toString();

			Map<String, String> result = WxPayApi.refund(false, null, outTradeNo, outRefundNo, totalFee,
					refundFee, null, refundDesc, null,
					WxPayApiConfigKit.getWxPayApiConfig().getMchId(),
					WxPayApiConfigKit.getWxPayApiConfig().getAppId(),
					WxPayKit.generateStr(),
					WxPayApiConfigKit.getWxPayApiConfig().getPartnerKey(),
					SignType.HMACSHA256, null);

			return R.ok(result);
		}
		catch (Exception e) {
			log.error("退款申请失败: {}", e.getMessage(), e);
			return R.failed("退款申请失败: " + e.getMessage());
		}
	}

}
