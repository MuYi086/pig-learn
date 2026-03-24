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
import com.pig4cloud.pig.flowable.service.FlowableTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程任务 Service 实现
 *
 * @author pig4cloud
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowableTaskServiceImpl implements FlowableTaskService {

	private final TaskService taskService;
	private final HistoryService historyService;
	private final RepositoryService repositoryService;
	private final IdentityService identityService;

	@Override
	public R getTodoList(Map<String, Object> params) {
		String userId = getCurrentUserId();

		TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(userId);

		// 根据流程定义 Key 筛选
		if (params.containsKey("processDefinitionKey")) {
			query.processDefinitionKey(params.get("processDefinitionKey").toString());
		}

		// 根据任务名称筛选
		if (params.containsKey("taskName")) {
			query.taskNameLike("%" + params.get("taskName") + "%");
		}

		List<Task> list = query.orderByTaskCreateTime().desc().list();

		List<Map<String, Object>> result = list.stream().map(task -> {
			Map<String, Object> map = new HashMap<>();
			map.put("taskId", task.getId());
			map.put("taskName", task.getName());
			map.put("taskDefinitionKey", task.getTaskDefinitionKey());
			map.put("processInstanceId", task.getProcessInstanceId());
			map.put("processDefinitionId", task.getProcessDefinitionId());
			map.put("processDefinitionName", task.getProcessDefinitionName());
			map.put("assignee", task.getAssignee());
			map.put("createTime", task.getCreateTime());
			map.put("dueDate", task.getDueDate());
			map.put("description", task.getDescription());
			map.put("owner", task.getOwner());
			return map;
		}).collect(Collectors.toList());

		return R.ok(result);
	}

	@Override
	public R getDoneList(Map<String, Object> params) {
		String userId = getCurrentUserId();

		org.flowable.task.api.history.HistoricTaskInstanceQuery query = historyService
			.createHistoricTaskInstanceQuery()
			.taskAssignee(userId)
			.finished();

		// 根据流程定义 Key 筛选
		if (params.containsKey("processDefinitionKey")) {
			query.processDefinitionKey(params.get("processDefinitionKey").toString());
		}

		List<HistoricTaskInstance> list = query.orderByHistoricTaskInstanceEndTime().desc().list();

		List<Map<String, Object>> result = list.stream().map(task -> {
			Map<String, Object> map = new HashMap<>();
			map.put("taskId", task.getId());
			map.put("taskName", task.getName());
			map.put("processInstanceId", task.getProcessInstanceId());
			map.put("processDefinitionName", task.getProcessDefinitionName());
			map.put("assignee", task.getAssignee());
			map.put("createTime", task.getCreateTime());
			map.put("endTime", task.getEndTime());
			map.put("durationInMillis", task.getDurationInMillis());
			map.put("deleteReason", task.getDeleteReason());
			return map;
		}).collect(Collectors.toList());

		return R.ok(result);
	}

	@Override
	public R getTask(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

		if (task == null) {
			return R.failed("任务不存在");
		}

		Map<String, Object> result = new HashMap<>();
		result.put("taskId", task.getId());
		result.put("taskName", task.getName());
		result.put("taskDefinitionKey", task.getTaskDefinitionKey());
		result.put("processInstanceId", task.getProcessInstanceId());
		result.put("processDefinitionId", task.getProcessDefinitionId());
		result.put("processDefinitionName", task.getProcessDefinitionName());
		result.put("assignee", task.getAssignee());
		result.put("createTime", task.getCreateTime());
		result.put("dueDate", task.getDueDate());
		result.put("description", task.getDescription());
		result.put("owner", task.getOwner());
		result.put("formKey", task.getFormKey());

		// 获取流程变量
		Map<String, Object> processVariables = taskService.getVariables(taskId);
		result.put("processVariables", processVariables);

		return R.ok(result);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R claim(String taskId) {
		String userId = getCurrentUserId();
		taskService.claim(taskId, userId);
		log.info("任务认领成功: taskId={}, userId={}", taskId, userId);
		return R.ok();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R unclaim(String taskId) {
		taskService.unclaim(taskId);
		log.info("任务取消认领成功: taskId={}", taskId);
		return R.ok();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R complete(String taskId, Map<String, Object> variables, String comment) {
		try {
			String userId = getCurrentUserId();
			identityService.setAuthenticatedUserId(userId);

			// 添加批注
			if (comment != null && !comment.isEmpty()) {
				Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
				if (task != null) {
					taskService.addComment(taskId, task.getProcessInstanceId(), comment);
				}
			}

			taskService.complete(taskId, variables);
			log.info("任务完成: taskId={}, userId={}", taskId, userId);
			return R.ok();
		}
		catch (Exception e) {
			log.error("任务完成失败: {}", e.getMessage(), e);
			return R.failed("任务完成失败: " + e.getMessage());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R delegate(String taskId, String assignee, String comment) {
		try {
			taskService.delegateTask(taskId, assignee);

			// 添加批注
			if (comment != null && !comment.isEmpty()) {
				Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
				if (task != null) {
					taskService.addComment(taskId, task.getProcessInstanceId(),
							"委派给: " + assignee + ", 意见: " + comment);
				}
			}

			log.info("任务委派成功: taskId={}, assignee={}", taskId, assignee);
			return R.ok();
		}
		catch (Exception e) {
			log.error("任务委派失败: {}", e.getMessage(), e);
			return R.failed("任务委派失败: " + e.getMessage());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R transfer(String taskId, String assignee, String comment) {
		try {
			taskService.setAssignee(taskId, assignee);

			// 添加批注
			if (comment != null && !comment.isEmpty()) {
				Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
				if (task != null) {
					taskService.addComment(taskId, task.getProcessInstanceId(),
							"转办给: " + assignee + ", 意见: " + comment);
				}
			}

			log.info("任务转办成功: taskId={}, assignee={}", taskId, assignee);
			return R.ok();
		}
		catch (Exception e) {
			log.error("任务转办失败: {}", e.getMessage(), e);
			return R.failed("任务转办失败: " + e.getMessage());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R reject(String taskId, String comment) {
		// 驳回功能需要通过 Flowable 的跳转 API 实现
		// 简化实现，返回提示
		return R.failed("驳回功能需要自定义实现，建议使用 returnTo 退回到指定节点");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R returnTo(String taskId, String targetActivityId, String comment) {
		// 退回功能需要通过 Flowable 的动态节点跳转实现
		// 简化实现，返回提示
		return R.failed("退回功能需要自定义实现，涉及 Flowable 动态节点跳转");
	}

	@Override
	public R getReturnNodes(String taskId) {
		// 获取可退回的节点列表
		// 简化实现
		return R.ok("获取可退回节点功能待实现");
	}

	@Override
	public R getComments(String processInstanceId) {
		List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);

		List<Map<String, Object>> result = comments.stream().map(comment -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", comment.getId());
			map.put("type", comment.getType());
			map.put("time", comment.getTime());
			map.put("userId", comment.getUserId());
			map.put("taskId", comment.getTaskId());
			map.put("processInstanceId", comment.getProcessInstanceId());
			map.put("fullMessage", comment.getFullMessage());
			return map;
		}).collect(Collectors.toList());

		return R.ok(result);
	}

	@Override
	public R getNodes(String processDefinitionId) {
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);

		// 获取流程定义的所有节点
		// 简化实现
		return R.ok("获取流程节点功能待实现");
	}

	/**
	 * 获取当前用户 ID
	 * @return 用户 ID
	 */
	private String getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() != null) {
			return authentication.getName();
		}
		return "anonymous";
	}

}
