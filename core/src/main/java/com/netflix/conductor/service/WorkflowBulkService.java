/*
 * Copyright 2018 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.service;

import com.netflix.conductor.service.common.BulkResponse;

import java.util.List;

public interface WorkflowBulkService {

   BulkResponse pauseWorkflow(List<String> workflowIds);

   BulkResponse resumeWorkflow(List<String> workflowIds);

   BulkResponse restart(List<String> workflowIds, boolean useLatestDefinitions);

   BulkResponse retry(List<String> workflowIds);

   BulkResponse terminate(List<String> workflowIds, String reason);
}
