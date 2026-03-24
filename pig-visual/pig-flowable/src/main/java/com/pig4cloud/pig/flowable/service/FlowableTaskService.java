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

package com.pig4cloud.pig.flowable.service;

import com.pig4cloud.pig.common.core.util.R;

import java.util.Map;

/**
 * 流程任务 Service 接口
 *
 * @author pig4cloud
 */
public interface FlowableTaskService {

	/**
	 * 获取待办任务列表
	 * @param params 查询参数
	 * @return 待办任务列表
	 */
	R getTodoList(Map<String, Object> params);

	/**
	 * 获取已办任务列表
	 * @param params 查询参数
	 * @return 已办任务列表
	 */
	R getDoneList(Map<String, Object> params);

	/**
	 * 获取任务详情
	 * @param taskId 任务 ID
	 * @return 任务详情
	 */
	R getTask(String taskId);

	/**
	 * 认领任务
	 * @param taskId 任务 ID
	 * @return 认领结果
	 */
	R claim(String taskId);

	/**
	 * 取消认领任务
	 * @param taskId 任务 ID
	 * @return 取消认领结果
	 */
	R unclaim(String taskId);

	/**
	 * 完成任务
	 * @param taskId 任务 ID
	 * @param variables 流程变量
	 * @param comment 审批意见
	 * @return 完成结果
	 */
	R complete(String taskId, Map<String, Object> variables, String comment);

	/**
	 * 委派任务
	 * @param taskId 任务 ID
	 * @param assignee 被委派人
	 * @param comment 委派意见
	 * @return 委派结果
	 */
	R delegate(String taskId, String assignee, String comment);

	/**
	 * 转办任务
	 * @param taskId 任务 ID
	 * @param assignee 被转办人
	 * @param comment 转办意见
	 * @return 转办结果
	 */
	R transfer(String taskId, String assignee, String comment);

	/**
	 * 驳回任务
	 * @param taskId 任务 ID
	 * @param comment 驳回意见
	 * @return 驳回结果
	 */
	R reject(String taskId, String comment);

	/**
	 * 退回任务到指定节点
	 * @param taskId 任务 ID
	 * @param targetActivityId 目标节点 ID
	 * @param comment 退回意见
	 * @return 退回结果
	 */
	R returnTo(String taskId, String targetActivityId, String comment);

	/**
	 * 获取可退回的节点
	 * @param taskId 任务 ID
	 * @return 可退回节点列表
	 */
	R getReturnNodes(String taskId);

	/**
	 * 获取任务批注
	 * @param processInstanceId 流程实例 ID
	 * @return 批注列表
	 */
	R getComments(String processInstanceId);

	/**
	 * 获取流程节点
	 * @param processDefinitionId 流程定义 ID
	 * @return 节点列表
	 */
	R getNodes(String processDefinitionId);

}
