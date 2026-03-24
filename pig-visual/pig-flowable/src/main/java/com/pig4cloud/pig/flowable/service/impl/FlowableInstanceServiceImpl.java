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

package com.pig4cloud.pig.flowable.service.impl;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.flowable.service.FlowableInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程实例 Service 实现
 *
 * @author pig4cloud
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowableInstanceServiceImpl implements FlowableInstanceService {

	private final RuntimeService runtimeService;
	private final HistoryService historyService;
	private final IdentityService identityService;

	@Override
	public R queryPage(Map<String, Object> params) {
		return queryList(params);
	}

	private R queryList(Map<String, Object> params) {
		ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();

		// 根据流程定义 Key 查询
		if (params.containsKey("processDefinitionKey")) {
			query.processDefinitionKey(params.get("processDefinitionKey").toString());
		}

		// 根据业务 Key 查询
		if (params.containsKey("businessKey")) {
			query.processInstanceBusinessKey(params.get("businessKey").toString());
		}

		// 根据发起人查询
		if (params.containsKey("startUserId")) {
			query.startedBy(params.get("startUserId").toString());
		}

		List<ProcessInstance> list = query.orderByStartTime().desc().list();

		List<Map<String, Object>> result = list.stream().map(pi -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", pi.getId());
			map.put("processDefinitionId", pi.getProcessDefinitionId());
			map.put("processDefinitionName", pi.getProcessDefinitionName());
			map.put("processDefinitionKey", pi.getProcessDefinitionKey());
			map.put("businessKey", pi.getBusinessKey());
			map.put("startUserId", pi.getStartUserId());
			map.put("startTime", pi.getStartTime());
			map.put("isSuspended", pi.isSuspended());
			return map;
		}).collect(Collectors.toList());

		return R.ok(result);
	}

	@Override
	public R startProcessInstance(String processDefinitionKey, Map<String, Object> variables) {
		try {
			// 添加发起人信息
			String startUserId = variables.getOrDefault("startUserId", "system").toString();
			identityService.setAuthenticatedUserId(startUserId);

			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);

			log.info("流程实例启动成功: processInstanceId={}, processDefinitionKey={}",
					processInstance.getId(), processDefinitionKey);

			Map<String, Object> result = new HashMap<>();
			result.put("processInstanceId", processInstance.getId());
			result.put("processDefinitionId", processInstance.getProcessDefinitionId());
			result.put("businessKey", processInstance.getBusinessKey());

			return R.ok(result);
		}
		catch (Exception e) {
			log.error("流程实例启动失败: {}", e.getMessage(), e);
			return R.failed("流程实例启动失败: " + e.getMessage());
		}
	}

	@Override
	public R getProcessInstance(String processInstanceId) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
			.processInstanceId(processInstanceId)
			.singleResult();

		if (processInstance == null) {
			return R.failed("流程实例不存在或已结束");
		}

		Map<String, Object> result = new HashMap<>();
		result.put("id", processInstance.getId());
		result.put("processDefinitionId", processInstance.getProcessDefinitionId());
		result.put("processDefinitionName", processInstance.getProcessDefinitionName());
		result.put("processDefinitionKey", processInstance.getProcessDefinitionKey());
		result.put("businessKey", processInstance.getBusinessKey());
		result.put("startUserId", processInstance.getStartUserId());
		result.put("startTime", processInstance.getStartTime());
		result.put("isSuspended", processInstance.isSuspended());

		return R.ok(result);
	}

	@Override
	public R deleteProcessInstance(String processInstanceId, String deleteReason) {
		try {
			runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
			log.info("流程实例删除成功: processInstanceId={}", processInstanceId);
			return R.ok();
		}
		catch (Exception e) {
			log.error("流程实例删除失败: {}", e.getMessage(), e);
			return R.failed("流程实例删除失败: " + e.getMessage());
		}
	}

	@Override
	public R suspendProcessInstance(String processInstanceId) {
		runtimeService.suspendProcessInstanceById(processInstanceId);
		log.info("流程实例挂起成功: {}", processInstanceId);
		return R.ok();
	}

	@Override
	public R activateProcessInstance(String processInstanceId) {
		runtimeService.activateProcessInstanceById(processInstanceId);
		log.info("流程实例激活成功: {}", processInstanceId);
		return R.ok();
	}

	@Override
	public R getProcessVariables(String processInstanceId) {
		Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
		return R.ok(variables);
	}

	@Override
	public R setProcessVariables(String processInstanceId, Map<String, Object> variables) {
		runtimeService.setVariables(processInstanceId, variables);
		log.info("流程变量设置成功: processInstanceId={}", processInstanceId);
		return R.ok();
	}

	@Override
	public R getProcessHistory(String processInstanceId) {
		// 获取历史活动记录
		List<Map<String, Object>> history = historyService.createHistoricActivityInstanceQuery()
			.processInstanceId(processInstanceId)
			.orderByHistoricActivityInstanceStartTime()
			.asc()
			.list()
			.stream()
			.map(hai -> {
				Map<String, Object> map = new HashMap<>();
				map.put("activityId", hai.getActivityId());
				map.put("activityName", hai.getActivityName());
				map.put("activityType", hai.getActivityType());
				map.put("assignee", hai.getAssignee());
				map.put("startTime", hai.getStartTime());
				map.put("endTime", hai.getEndTime());
				map.put("durationInMillis", hai.getDurationInMillis());
				return map;
			})
			.collect(Collectors.toList());

		return R.ok(history);
	}

	@Override
	public R getProcessProgress(String processInstanceId) {
		// 返回流程进度图的相关数据
		Map<String, Object> result = new HashMap<>();
		result.put("processInstanceId", processInstanceId);
		result.put("activeActivityIds", runtimeService.getActiveActivityIds(processInstanceId));
		return R.ok(result);
	}

}
