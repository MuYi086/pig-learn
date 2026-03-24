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

package com.pig4cloud.pig.flowable.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.flowable.service.FlowableInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 流程实例管理 Controller
 *
 * @author pig4cloud
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/flowable/process/instance")
@Tag(name = "流程实例管理", description = "流程实例启动、查询、操作")
public class FlowableInstanceController {

	private final FlowableInstanceService flowableInstanceService;

	/**
	 * 分页查询运行中的流程实例
	 * @param params 查询参数
	 * @return R
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询运行中的流程实例", description = "分页查询运行中的流程实例列表")
	public R page(@RequestParam Map<String, Object> params) {
		return R.ok(flowableInstanceService.queryPage(params));
	}

	/**
	 * 启动流程实例
	 * @param processDefinitionKey 流程定义 Key
	 * @param variables 流程变量
	 * @return R
	 */
	@SysLog("启动流程实例")
	@PostMapping("/start/{processDefinitionKey}")
	@PreAuthorize("@pms.hasPermission('flowable:instance:start')")
	@Operation(summary = "启动流程实例", description = "根据流程定义 Key 启动流程实例")
	public R start(@PathVariable String processDefinitionKey, @RequestBody Map<String, Object> variables) {
		return flowableInstanceService.startProcessInstance(processDefinitionKey, variables);
	}

	/**
	 * 获取流程实例详情
	 * @param processInstanceId 流程实例 ID
	 * @return R
	 */
	@GetMapping("/{processInstanceId}")
	@Operation(summary = "获取流程实例详情", description = "获取流程实例详细信息")
	public R getById(@PathVariable String processInstanceId) {
		return R.ok(flowableInstanceService.getProcessInstance(processInstanceId));
	}

	/**
	 * 终止流程实例
	 * @param processInstanceId 流程实例 ID
	 * @param deleteReason 删除原因
	 * @return R
	 */
	@SysLog("终止流程实例")
	@DeleteMapping("/{processInstanceId}")
	@PreAuthorize("@pms.hasPermission('flowable:instance:delete')")
	@Operation(summary = "终止流程实例", description = "终止运行中的流程实例")
	public R delete(@PathVariable String processInstanceId, @RequestParam String deleteReason) {
		return flowableInstanceService.deleteProcessInstance(processInstanceId, deleteReason);
	}

	/**
	 * 挂起流程实例
	 * @param processInstanceId 流程实例 ID
	 * @return R
	 */
	@SysLog("挂起流程实例")
	@PostMapping("/suspend/{processInstanceId}")
	@PreAuthorize("@pms.hasPermission('flowable:instance:manage')")
	@Operation(summary = "挂起流程实例", description = "挂起流程实例暂停执行")
	public R suspend(@PathVariable String processInstanceId) {
		return flowableInstanceService.suspendProcessInstance(processInstanceId);
	}

	/**
	 * 激活流程实例
	 * @param processInstanceId 流程实例 ID
	 * @return R
	 */
	@SysLog("激活流程实例")
	@PostMapping("/activate/{processInstanceId}")
	@PreAuthorize("@pms.hasPermission('flowable:instance:manage')")
	@Operation(summary = "激活流程实例", description = "激活挂起的流程实例")
	public R activate(@PathVariable String processInstanceId) {
		return flowableInstanceService.activateProcessInstance(processInstanceId);
	}

	/**
	 * 获取流程实例变量
	 * @param processInstanceId 流程实例 ID
	 * @return R
	 */
	@GetMapping("/variables/{processInstanceId}")
	@Operation(summary = "获取流程实例变量", description = "获取流程实例的所有变量")
	public R getVariables(@PathVariable String processInstanceId) {
		return R.ok(flowableInstanceService.getProcessVariables(processInstanceId));
	}

	/**
	 * 设置流程实例变量
	 * @param processInstanceId 流程实例 ID
	 * @param variables 变量
	 * @return R
	 */
	@SysLog("设置流程实例变量")
	@PostMapping("/variables/{processInstanceId}")
	@PreAuthorize("@pms.hasPermission('flowable:instance:manage')")
	@Operation(summary = "设置流程实例变量", description = "设置流程实例的变量")
	public R setVariables(@PathVariable String processInstanceId, @RequestBody Map<String, Object> variables) {
		return flowableInstanceService.setProcessVariables(processInstanceId, variables);
	}

	/**
	 * 获取流程历史记录
	 * @param processInstanceId 流程实例 ID
	 * @return R
	 */
	@GetMapping("/history/{processInstanceId}")
	@Operation(summary = "获取流程历史记录", description = "获取流程实例的历史记录")
	public R getHistory(@PathVariable String processInstanceId) {
		return R.ok(flowableInstanceService.getProcessHistory(processInstanceId));
	}

	/**
	 * 获取流程进度图
	 * @param processInstanceId 流程实例 ID
	 * @return R
	 */
	@GetMapping("/progress/{processInstanceId}")
	@Operation(summary = "获取流程进度图", description = "获取流程实例的当前进度图")
	public R getProgress(@PathVariable String processInstanceId) {
		return R.ok(flowableInstanceService.getProcessProgress(processInstanceId));
	}

}
