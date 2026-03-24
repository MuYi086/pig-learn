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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 流程定义 Service 接口
 *
 * @author pig4cloud
 */
public interface FlowableProcessService {

	/**
	 * 分页查询流程定义
	 * @param params 查询参数
	 * @return 分页结果
	 */
	R queryPage(Map<String, Object> params);

	/**
	 * 查询流程定义列表
	 * @param params 查询参数
	 * @return 列表结果
	 */
	R queryList(Map<String, Object> params);

	/**
	 * 部署流程定义
	 * @param file BPMN XML 文件
	 * @param category 流程分类
	 * @param description 流程描述
	 * @return 部署结果
	 */
	R deploy(MultipartFile file, String category, String description);

	/**
	 * 删除部署
	 * @param deploymentId 部署 ID
	 * @param cascade 是否级联删除
	 * @return 删除结果
	 */
	R deleteDeployment(String deploymentId, boolean cascade);

	/**
	 * 获取流程定义 XML
	 * @param processDefinitionId 流程定义 ID
	 * @param response HttpServletResponse
	 * @throws IOException IO 异常
	 */
	void getProcessDefinitionXml(String processDefinitionId, HttpServletResponse response) throws IOException;

	/**
	 * 获取流程定义图片
	 * @param processDefinitionId 流程定义 ID
	 * @param response HttpServletResponse
	 * @throws IOException IO 异常
	 */
	void getProcessDefinitionImage(String processDefinitionId, HttpServletResponse response) throws IOException;

	/**
	 * 激活流程定义
	 * @param processDefinitionId 流程定义 ID
	 * @return 激活结果
	 */
	R activateProcessDefinition(String processDefinitionId);

	/**
	 * 挂起流程定义
	 * @param processDefinitionId 流程定义 ID
	 * @return 挂起结果
	 */
	R suspendProcessDefinition(String processDefinitionId);

	/**
	 * 迁移流程实例到新版本
	 * @param processDefinitionId 目标流程定义 ID
	 * @return 迁移结果
	 */
	R migrateProcessInstances(String processDefinitionId);

}
