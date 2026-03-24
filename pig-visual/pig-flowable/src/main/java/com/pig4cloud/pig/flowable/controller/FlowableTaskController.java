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
import com.pig4cloud.pig.flowable.service.FlowableTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程任务管理 Controller
 *
 * @author pig4cloud
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/flowable/task")
@Tag(name = "流程任务管理", description = "待办任务、已办任务、任务操作")
public class FlowableTaskController {

	private final FlowableTaskService flowableTaskService;

	/**
	 * 获取当前用户的待办任务
	 * @param params 查询参数
	 * @return R
	 */
	@GetMapping("/todo")
	@Operation(summary = "获取待办任务", description = "获取当前用户的待办任务列表")
	public R todo(@RequestParam Map<String, Object> params) {
		return R.ok(flowableTaskService.getTodoList(params));
	}

	/**
	 * 获取当前用户的已办任务
	 * @param params 查询参数
	 * @return R
	 */
	@GetMapping("/done")
	@Operation(summary = "获取已办任务", description = "获取当前用户的已办任务列表")
	public R done(@RequestParam Map<String, Object> params) {
		return R.ok(flowableTaskService.getDoneList(params));
	}

	/**
	 * 获取任务详情
	 * @param taskId 任务 ID
	 * @return R
	 */
	@GetMapping("/{taskId}")
	@Operation(summary = "获取任务详情", description = "获取任务的详细信息")
	public R getById(@PathVariable String taskId) {
		return R.ok(flowableTaskService.getTask(taskId));
	}

	/**
	 * 认领任务
	 * @param taskId 任务 ID
	 * @return R
	 */
	@SysLog("认领任务")
	@PostMapping("/claim/{taskId}")
	@Operation(summary = "认领任务", description = "认领待办任务")
	public R claim(@PathVariable String taskId) {
		return flowableTaskService.claim(taskId);
	}

	/**
	 * 取消认领任务
	 * @param taskId 任务 ID
	 * @return R
	 */
	@SysLog("取消认领任务")
	@PostMapping("/unclaim/{taskId}")
	@Operation(summary = "取消认领任务", description = "取消认领已认领的任务")
	public R unclaim(@PathVariable String taskId) {
		return flowableTaskService.unclaim(taskId);
	}

	/**
	 * 完成任务
	 * @param taskId 任务 ID
	 * @param variables 流程变量
	 * @param comment 审批意见
	 * @return R
	 */
	@SysLog("完成任务")
	@PostMapping("/complete/{taskId}")
	@Operation(summary = "完成任务", description = "完成任务并提交")
	public R complete(@PathVariable String taskId, @RequestBody Map<String, Object> variables,
			@RequestParam(value = "comment", required = false) String comment) {
		return flowableTaskService.complete(taskId, variables, comment);
	}

	/**
	 * 委派任务
	 * @param taskId 任务 ID
	 * @param assignee 被委派人
	 * @param comment 委派意见
	 * @return R
	 */
	@SysLog("委派任务")
	@PostMapping("/delegate/{taskId}")
	@PreAuthorize("@pms.hasPermission('flowable:task:delegate')")
	@Operation(summary = "委派任务", description = "将任务委派给他人处理")
	public R delegate(@PathVariable String taskId, @RequestParam String assignee,
			@RequestParam(value = "comment", required = false) String comment) {
		return flowableTaskService.delegate(taskId, assignee, comment);
	}

	/**
	 * 转办任务
	 * @param taskId 任务 ID
	 * @param assignee 被转办人
	 * @param comment 转办意见
	 * @return R
	 */
	@SysLog("转办任务")
	@PostMapping("/transfer/{taskId}")
	@PreAuthorize("@pms.hasPermission('flowable:task:transfer')")
	@Operation(summary = "转办任务", description = "将任务转办给他人")
	public R transfer(@PathVariable String taskId, @RequestParam String assignee,
			@RequestParam(value = "comment", required = false) String comment) {
		return flowableTaskService.transfer(taskId, assignee, comment);
	}

	/**
	 * 驳回任务（退回上一步）
	 * @param taskId 任务 ID
	 * @param comment 驳回意见
	 * @return R
	 */
	@SysLog("驳回任务")
	@PostMapping("/reject/{taskId}")
	@Operation(summary = "驳回任务", description = "驳回任务到上一步")
	public R reject(@PathVariable String taskId, @RequestParam(value = "comment", required = false) String comment) {
		return flowableTaskService.reject(taskId, comment);
	}

	/**
	 * 退回任务到指定节点
	 * @param taskId 任务 ID
	 * @param targetActivityId 目标节点 ID
	 * @param comment 退回意见
	 * @return R
	 */
	@SysLog("退回任务")
	@PostMapping("/return/{taskId}")
	@Operation(summary = "退回任务到指定节点", description = "退回任务到指定的流程节点")
	public R returnTo(@PathVariable String taskId, @RequestParam String targetActivityId,
			@RequestParam(value = "comment", required = false) String comment) {
		return flowableTaskService.returnTo(taskId, targetActivityId, comment);
	}

	/**
	 * 获取可退回的节点
	 * @param taskId 任务 ID
	 * @return R
	 */
	@GetMapping("/return/nodes/{taskId}")
	@Operation(summary = "获取可退回的节点", description = "获取当前任务可以退回的节点列表")
	public R getReturnNodes(@PathVariable String taskId) {
		return R.ok(flowableTaskService.getReturnNodes(taskId));
	}

	/**
	 * 获取任务批注列表
	 * @param processInstanceId 流程实例 ID
	 * @return R
	 */
	@GetMapping("/comments/{processInstanceId}")
	@Operation(summary = "获取任务批注", description = "获取流程实例的所有批注")
	public R getComments(@PathVariable String processInstanceId) {
		return R.ok(flowableTaskService.getComments(processInstanceId));
	}

	/**
	 * 获取流程节点列表
		 * @param processDefinitionId 流程定义 ID
	 * @return R
	 */
	@GetMapping("/nodes/{processDefinitionId}")
	@Operation(summary = "获取流程节点", description = "获取流程定义的所有节点")
	public R getNodes(@PathVariable String processDefinitionId) {
		return R.ok(flowableTaskService.getNodes(processDefinitionId));
	}

}
