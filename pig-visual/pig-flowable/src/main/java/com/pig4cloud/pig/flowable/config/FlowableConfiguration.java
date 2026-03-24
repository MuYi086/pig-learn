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

package com.pig4cloud.pig.flowable.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;

/**
 * Flowable 工作流配置类
 *
 * @author pig4cloud
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlowableConfiguration {

	private final RepositoryService repositoryService;

	/**
	 * 初始化流程引擎
	 */
	@PostConstruct
	public void init() {
		log.info("Flowable 工作流引擎初始化完成");
		log.info("已部署流程定义数量: {}", repositoryService.createProcessDefinitionQuery().count());
	}

	/**
	 * Flowable 异步任务执行器
	 * @return AsyncTaskExecutor
	 */
	@Bean("flowableTaskExecutor")
	public AsyncTaskExecutor flowableTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("flowable-");
		executor.initialize();
		return executor;
	}

}
