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
 * 流程实例 Service 接口
 *
 * @author pig4cloud
 */
public interface FlowableInstanceService {

	/**
	 * 分页查询流程实例
	 * @param params 查询参数
	 * @return 分页结果
	 */
	R queryPage(Map<String, Object> params);

	/**
	 * 启动流程实例
	 * @param processDefinitionKey 流程定义 Key
	 * @param variables 流程变量
	 * @return 启动结果
	 */
	R startProcessInstance(String processDefinitionKey, Map<String, Object> variables);

	/**
	 * 获取流程实例
	 * @param processInstanceId 流程实例 ID
	 * @return 流程实例信息
	 */
	R getProcessInstance(String processInstanceId);

	/**
	 * 删除流程实例
	 * @param processInstanceId 流程实例 ID
	 * @param deleteReason 删除原因
	 * @return 删除结果
	 */
	R deleteProcessInstance(String processInstanceId, String deleteReason);

	/**
	 * 挂起流程实例
	 * @param processInstanceId 流程实例 ID
	 * @return 挂起结果
	 */
	R suspendProcessInstance(String processInstanceId);

	/**
	 * 激活流程实例
	 * @param processInstanceId 流程实例 ID
	 * @return 激活结果
	 */
	R activateProcessInstance(String processInstanceId);

	/**
	 * 获取流程变量
	 * @param processInstanceId 流程实例 ID
	 * @return 变量列表
	 */
	R getProcessVariables(String processInstanceId);

	/**
	 * 设置流程变量
	 * @param processInstanceId 流程实例 ID
	 * @param variables 变量
	 * @return 设置结果
	 */
	R setProcessVariables(String processInstanceId, Map<String, Object> variables);

	/**
	 * 获取流程历史记录
	 * @param processInstanceId 流程实例 ID
	 * @return 历史记录
	 */
	R getProcessHistory(String processInstanceId);

	/**
	 * 获取流程进度
	 * @param processInstanceId 流程实例 ID
	 * @return 进度信息
	 */
	R getProcessProgress(String processInstanceId);

}
