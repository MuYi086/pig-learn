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

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfigKit;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝支付 Controller
 *
 * @author pig4cloud
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay/ali")
@Tag(name = "支付宝支付", description = "支付宝支付接口")
public class AliPayController {

	/**
	 * PC 网站支付
	 * @param params 参数
	 * @return R
	 */
	@SysLog("支付宝 PC 网站支付")
	@PostMapping("/page-pay")
	@Operation(summary = "PC 网站支付", description = "支付宝 PC 网站支付")
	public R pagePay(@RequestBody Map<String, Object> params) {
		try {
			String outTradeNo = params.get("outTradeNo").toString();
			String totalAmount = params.get("totalAmount").toString();
			String subject = params.get("subject").toString();
			String body = params.getOrDefault("body", "").toString();
			String returnUrl = params.get("returnUrl").toString();
			String notifyUrl = params.get("notifyUrl").toString();

			AlipayTradePagePayModel model = new AlipayTradePagePayModel();
			model.setOutTradeNo(outTradeNo);
			model.setTotalAmount(totalAmount);
			model.setSubject(subject);
			model.setBody(body);
			model.setProductCode("FAST_INSTANT_TRADE_PAY");

			AlipayTradePagePayResponse response = AliPayApi.tradePagePay(model, returnUrl, notifyUrl);

			return R.ok(response.getBody());
		}
		catch (AlipayApiException e) {
			log.error("支付宝支付失败: {}", e.getMessage(), e);
			return R.failed("支付失败: " + e.getMessage());
		}
	}

	/**
	 * 支付回调
	 * @param request 请求
	 * @return 回调响应
	 */
	@PostMapping("/callback")
	@Operation(summary = "支付回调", description = "支付宝支付结果通知")
	public String callback(HttpServletRequest request) {
		try {
			Map<String, String> params = AliPayApi.toMap(request);
			log.info("支付宝回调参数: {}", params);

			boolean verifyResult = AlipaySignature.rsaCheckV1(params,
					AliPayApiConfigKit.getAliPayApiConfig().getAliPayPublicKey(),
					AliPayApiConfigKit.getAliPayApiConfig().getCharset(),
					AliPayApiConfigKit.getAliPayApiConfig().getSignType());

			if (verifyResult) {
				String tradeStatus = params.get("trade_status");
				String outTradeNo = params.get("out_trade_no");
				String tradeNo = params.get("trade_no");

				if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
					log.info("支付宝支付成功: outTradeNo={}, tradeNo={}", outTradeNo, tradeNo);
					// 处理支付成功逻辑
				}

				return "success";
			}
			else {
				log.error("支付宝回调验签失败");
				return "fail";
			}
		}
		catch (Exception e) {
			log.error("支付宝回调处理失败: {}", e.getMessage(), e);
			return "fail";
		}
	}

	/**
	 * 查询订单
	 * @param outTradeNo 商户订单号
	 * @return R
	 */
	@GetMapping("/order/{outTradeNo}")
	@Operation(summary = "查询订单", description = "查询支付宝订单状态")
	public R queryOrder(@PathVariable String outTradeNo) {
		try {
			AlipayTradeQueryModel model = new AlipayTradeQueryModel();
			model.setOutTradeNo(outTradeNo);

			AlipayTradeQueryResponse response = AliPayApi.tradeQueryToResponse(model);

			if (response.isSuccess()) {
				return R.ok(response);
			}
			else {
				return R.failed(response.getSubMsg());
			}
		}
		catch (AlipayApiException e) {
			log.error("订单查询失败: {}", e.getMessage(), e);
			return R.failed("订单查询失败: " + e.getMessage());
		}
	}

	/**
	 * 申请退款
	 * @param params 参数
	 * @return R
	 */
	@SysLog("支付宝申请退款")
	@PostMapping("/refund")
	@Operation(summary = "申请退款", description = "支付宝申请退款")
	public R refund(@RequestBody Map<String, Object> params) {
		try {
			String outTradeNo = params.get("outTradeNo").toString();
			String refundAmount = params.get("refundAmount").toString();
			String refundReason = params.getOrDefault("refundReason", "").toString();
			String outRequestNo = params.get("outRequestNo").toString();

			AlipayTradeRefundModel model = new AlipayTradeRefundModel();
			model.setOutTradeNo(outTradeNo);
			model.setRefundAmount(refundAmount);
			model.setRefundReason(refundReason);
			model.setOutRequestNo(outRequestNo);

			AlipayTradeRefundResponse response = AliPayApi.tradeRefundToResponse(model);

			if (response.isSuccess()) {
				return R.ok(response);
			}
			else {
				return R.failed(response.getSubMsg());
			}
		}
		catch (AlipayApiException e) {
			log.error("退款申请失败: {}", e.getMessage(), e);
			return R.failed("退款申请失败: " + e.getMessage());
		}
	}

}
