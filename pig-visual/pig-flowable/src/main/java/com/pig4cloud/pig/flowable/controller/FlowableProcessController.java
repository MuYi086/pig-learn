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
import com.pig4cloud.pig.flowable.service.FlowableProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 流程定义管理 Controller
 *
 * @author pig4cloud
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/flowable/process/definition")
@Tag(name = "流程定义管理", description = "流程定义部署、查询、删除")
public class FlowableProcessController {

	private final FlowableProcessService flowableProcessService;

	/**
	 * 分页查询流程定义列表
	 * @param params 查询参数
	 * @return R
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询流程定义", description = "分页查询流程定义列表")
	public R page(@RequestParam Map<String, Object> params) {
		return R.ok(flowableProcessService.queryPage(params));
	}

	/**
	 * 查询流程定义列表
	 * @param params 查询参数
	 * @return R
	 */
	@GetMapping("/list")
	@Operation(summary = "查询流程定义列表", description = "查询所有流程定义列表")
	public R list(@RequestParam Map<String, Object> params) {
		return R.ok(flowableProcessService.queryList(params));
	}

	/**
	 * 部署流程定义（通过 BPMN XML 文件）
	 * @param file BPMN XML 文件
	 * @param category 流程分类
	 * @param description 流程描述
	 * @return R
	 */
	@SysLog("部署流程定义")
	@PostMapping("/deploy")
	@PreAuthorize("@pms.hasPermission('flowable:process:deploy')")
	@Operation(summary = "部署流程定义", description = "通过 BPMN XML 文件部署流程")
	public R deploy(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "description", required = false) String description) {
		return flowableProcessService.deploy(file, category, description);
	}

	/**
	 * 删除流程定义
	 * @param deploymentId 部署 ID
	 * @param cascade 是否级联删除运行中的实例
	 * @return R
	 */
	@SysLog("删除流程定义")
	@DeleteMapping("/{deploymentId}")
	@PreAuthorize("@pms.hasPermission('flowable:process:delete')")
	@Operation(summary = "删除流程定义", description = "删除流程定义及部署")
	public R delete(@PathVariable String deploymentId,
			@RequestParam(value = "cascade", defaultValue = "false") boolean cascade) {
		return flowableProcessService.deleteDeployment(deploymentId, cascade);
	}

	/**
	 * 获取流程定义 XML
	 * @param processDefinitionId 流程定义 ID
	 * @param response HttpServletResponse
	 * @throws IOException IO 异常
	 */
	@GetMapping("/xml/{processDefinitionId}")
	@Operation(summary = "获取流程定义 XML", description = "获取流程定义的 BPMN XML")
	public void getProcessDefinitionXml(@PathVariable String processDefinitionId, HttpServletResponse response)
			throws IOException {
		flowableProcessService.getProcessDefinitionXml(processDefinitionId, response);
	}

	/**
	 * 获取流程图片
	 * @param processDefinitionId 流程定义 ID
	 * @param response HttpServletResponse
	 * @throws IOException IO 异常
	 */
	@GetMapping("/image/{processDefinitionId}")
	@Operation(summary = "获取流程图片", description = "获取流程定义的图片")
	public void getProcessDefinitionImage(@PathVariable String processDefinitionId, HttpServletResponse response)
			throws IOException {
		flowableProcessService.getProcessDefinitionImage(processDefinitionId, response);
	}

	/**
	 * 激活流程定义
	 * @param processDefinitionId 流程定义 ID
	 * @return R
	 */
	@SysLog("激活流程定义")
	@PostMapping("/activate/{processDefinitionId}")
	@PreAuthorize("@pms.hasPermission('flowable:process:manage')")
	@Operation(summary = "激活流程定义", description = "激活流程定义使其可以被启动")
	public R activate(@PathVariable String processDefinitionId) {
		return flowableProcessService.activateProcessDefinition(processDefinitionId);
	}

	/**
	 * 挂起流程定义
	 * @param processDefinitionId 流程定义 ID
	 * @return R
	 */
	@SysLog("挂起流程定义")
	@PostMapping("/suspend/{processDefinitionId}")
	@PreAuthorize("@pms.hasPermission('flowable:process:manage')")
	@Operation(summary = "挂起流程定义", description = "挂起流程定义使其不能被启动")
	public R suspend(@PathVariable String processDefinitionId) {
		return flowableProcessService.suspendProcessDefinition(processDefinitionId);
	}

	/**
	 * 迁移流程实例到新版本
	 * @param processDefinitionId 目标流程定义 ID
	 * @return R
	 */
	@SysLog("迁移流程实例")
	@PostMapping("/migrate/{processDefinitionId}")
	@PreAuthorize("@pms.hasPermission('flowable:process:manage')")
	@Operation(summary = "迁移流程实例", description = "将所有运行中的实例迁移到新版本")
	public R migrate(@PathVariable String processDefinitionId) {
		return flowableProcessService.migrateProcessInstances(processDefinitionId);
	}

}
