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
import com.pig4cloud.pig.flowable.service.FlowableProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

/**
 * 流程定义 Service 实现
 *
 * @author pig4cloud
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowableProcessServiceImpl implements FlowableProcessService {

	private final RepositoryService repositoryService;

	@Override
	public R queryPage(Map<String, Object> params) {
		// 简化实现，返回列表
		return queryList(params);
	}

	@Override
	public R queryList(Map<String, Object> params) {
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();

		// 根据 Key 查询
		if (params.containsKey("key")) {
			query.processDefinitionKeyLike("%" + params.get("key") + "%");
		}

		// 根据名称查询
		if (params.containsKey("name")) {
			query.processDefinitionNameLike("%" + params.get("name") + "%");
		}

		// 根据分类查询
		if (params.containsKey("category")) {
			query.processDefinitionCategory(params.get("category").toString());
		}

		List<ProcessDefinition> list = query.latestVersion().orderByProcessDefinitionVersion().desc().list();

		List<Map<String, Object>> result = list.stream().map(pd -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", pd.getId());
			map.put("key", pd.getKey());
			map.put("name", pd.getName());
			map.put("version", pd.getVersion());
			map.put("category", pd.getCategory());
			map.put("description", pd.getDescription());
			map.put("suspended", pd.isSuspended());
			map.put("deploymentId", pd.getDeploymentId());
			return map;
		}).collect(Collectors.toList());

		return R.ok(result);
	}

	@Override
	public R deploy(MultipartFile file, String category, String description) {
		try {
			String filename = file.getOriginalFilename();
			Deployment deployment;

			if (filename != null && filename.endsWith(".zip")) {
				deployment = repositoryService.createDeployment()
					.addZipInputStream(new ZipInputStream(file.getInputStream()))
					.name(filename)
					.category(category)
					.deploy();
			}
			else {
				deployment = repositoryService.createDeployment()
					.addInputStream(filename, file.getInputStream())
					.name(filename)
					.category(category)
					.deploy();
			}

			log.info("流程部署成功: deploymentId={}, name={}", deployment.getId(), deployment.getName());
			return R.ok(deployment.getId());
		}
		catch (IOException e) {
			log.error("流程部署失败: {}", e.getMessage(), e);
			return R.failed("流程部署失败: " + e.getMessage());
		}
	}

	@Override
	public R deleteDeployment(String deploymentId, boolean cascade) {
		try {
			if (cascade) {
				repositoryService.deleteDeployment(deploymentId, true);
			}
			else {
				repositoryService.deleteDeployment(deploymentId);
			}
			log.info("流程部署删除成功: deploymentId={}", deploymentId);
			return R.ok();
		}
		catch (Exception e) {
			log.error("流程部署删除失败: {}", e.getMessage(), e);
			return R.failed("流程部署删除失败: " + e.getMessage());
		}
	}

	@Override
	public void getProcessDefinitionXml(String processDefinitionId, HttpServletResponse response) throws IOException {
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
		InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
				processDefinition.getResourceName());

		response.setContentType("application/xml");
		response.setHeader("Content-Disposition", "attachment; filename=" + processDefinition.getKey() + ".bpmn20.xml");

		byte[] buffer = new byte[1024];
		int len;
		while ((len = inputStream.read(buffer)) != -1) {
			response.getOutputStream().write(buffer, 0, len);
		}
		inputStream.close();
	}

	@Override
	public void getProcessDefinitionImage(String processDefinitionId, HttpServletResponse response) throws IOException {
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
		InputStream inputStream = repositoryService.getProcessDiagram(processDefinitionId);

		if (inputStream != null) {
			response.setContentType("image/png");
			response.setHeader("Content-Disposition",
					"inline; filename=" + processDefinition.getKey() + ".png");

			byte[] buffer = new byte[1024];
			int len;
			while ((len = inputStream.read(buffer)) != -1) {
				response.getOutputStream().write(buffer, 0, len);
			}
			inputStream.close();
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "流程图不存在");
		}
	}

	@Override
	public R activateProcessDefinition(String processDefinitionId) {
		repositoryService.activateProcessDefinitionById(processDefinitionId);
		log.info("流程定义激活成功: {}", processDefinitionId);
		return R.ok();
	}

	@Override
	public R suspendProcessDefinition(String processDefinitionId) {
		repositoryService.suspendProcessDefinitionById(processDefinitionId);
		log.info("流程定义挂起成功: {}", processDefinitionId);
		return R.ok();
	}

	@Override
	public R migrateProcessInstances(String processDefinitionId) {
		// 简化实现，实际需要遍历实例进行迁移
		log.info("流程实例迁移请求: targetProcessDefinitionId={}", processDefinitionId);
		return R.ok("流程实例迁移功能待实现");
	}

}
