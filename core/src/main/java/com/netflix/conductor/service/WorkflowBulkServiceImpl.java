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

import com.google.inject.Singleton;
import com.netflix.conductor.annotations.Audit;
import com.netflix.conductor.annotations.Trace;
import com.netflix.conductor.core.execution.WorkflowExecutor;
import com.netflix.conductor.service.common.BulkResponse;
import com.netflix.conductor.service.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

@Audit
@Singleton
@Trace
public class WorkflowBulkServiceImpl implements WorkflowBulkService {
    private static final int MAX_REQUEST_ITEMS = 1000;
    private final WorkflowExecutor workflowExecutor;
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowBulkService.class);

    @Inject
    public WorkflowBulkServiceImpl(WorkflowExecutor workflowExecutor) {
        this.workflowExecutor = workflowExecutor;
    }

    /**
     * Pause the list of workflows.
     * @param workflowIds - list of workflow Ids  to perform pause operation on
     * @return bulk response object containing a list of succeeded workflows and a list of failed ones with errors
     */
    public BulkResponse pauseWorkflow(List<String> workflowIds) {
        ServiceUtils.checkNotNullOrEmpty(workflowIds, "WorkflowIds list cannot be null.");
        ServiceUtils.checkArgument(workflowIds.size() < MAX_REQUEST_ITEMS, String.format("Cannot process more than %s workflows. Please use multiple requests", MAX_REQUEST_ITEMS));
        for (String workflowId : workflowIds) {
            workflowExecutor.pauseWorkflow(workflowId);
        }

        BulkResponse bulkResponse = new BulkResponse();
        for (String workflowId : workflowIds) {
            try {
                workflowExecutor.pauseWorkflow(workflowId);
                bulkResponse.appendSuccessResponse(workflowId);
            } catch (Exception e) {
                LOGGER.error("bulk pauseWorkflow exception, workflowId {}, message: {} ",workflowId, e.getMessage(), e);
                bulkResponse.appendFailedResponse(workflowId, e.getMessage());
            }
        }

        return bulkResponse;
    }

    /**
     * Resume the list of workflows.
     * @param workflowIds - list of workflow Ids  to perform resume operation on
     * @return bulk response object containing a list of succeeded workflows and a list of failed ones with errors
     */
    public BulkResponse resumeWorkflow(List<String> workflowIds) {
        ServiceUtils.checkNotNullOrEmpty(workflowIds, "WorkflowIds list cannot be null.");
        ServiceUtils.checkArgument(workflowIds.size() < MAX_REQUEST_ITEMS, String.format("Cannot process more than %s workflows. Please use multiple requests", MAX_REQUEST_ITEMS));

        BulkResponse bulkResponse = new BulkResponse();
        for (String workflowId : workflowIds) {
            workflowExecutor.resumeWorkflow(workflowId);
            try {
                workflowExecutor.resumeWorkflow(workflowId);
                bulkResponse.appendSuccessResponse(workflowId);
            } catch (Exception e) {
                LOGGER.error("bulk resumeWorkflow exception, workflowId {}, message: {} ",workflowId, e.getMessage(), e);
                bulkResponse.appendFailedResponse(workflowId, e.getMessage());
            }
        }
        return bulkResponse;
    }

    /**
     * Restart the list of workflows.
     *
     * @param workflowIds          - list of workflow Ids  to perform restart operation on
     * @param useLatestDefinitions if true, use latest workflow and task definitions upon restart
     * @return bulk response object containing a list of succeeded workflows and a list of failed ones with errors
     */
    public BulkResponse restart(List<String> workflowIds, boolean useLatestDefinitions) {
        ServiceUtils.checkNotNullOrEmpty(workflowIds, "WorkflowIds list cannot be null.");
        ServiceUtils.checkArgument(workflowIds.size() < MAX_REQUEST_ITEMS, String.format("Cannot process more than %s workflows. Please use multiple requests", MAX_REQUEST_ITEMS));
        BulkResponse bulkResponse = new BulkResponse();
        for (String workflowId : workflowIds) {
            workflowExecutor.rewind(workflowId, useLatestDefinitions);
            try {
                workflowExecutor.rewind(workflowId, useLatestDefinitions);
                bulkResponse.appendSuccessResponse(workflowId);
            } catch (Exception e) {
                LOGGER.error("bulk restart exception, workflowId {}, message: {} ",workflowId, e.getMessage(), e);
                bulkResponse.appendFailedResponse(workflowId, e.getMessage());
            }
        }
        return bulkResponse;
    }

    /**
     * Retry the last failed task for each workflow from the list.
     * @param workflowIds - list of workflow Ids  to perform retry operation on
     * @return bulk response object containing a list of succeeded workflows and a list of failed ones with errors
     */
    public BulkResponse retry(List<String> workflowIds) {
        ServiceUtils.checkNotNullOrEmpty(workflowIds, "WorkflowIds list cannot be null.");
        ServiceUtils.checkArgument(workflowIds.size() < MAX_REQUEST_ITEMS, String.format("Cannot process more than %s workflows. Please use multiple requests", MAX_REQUEST_ITEMS));
        BulkResponse bulkResponse = new BulkResponse();
        for (String workflowId : workflowIds) {
            workflowExecutor.retry(workflowId);
            try {
                workflowExecutor.retry(workflowId);
                bulkResponse.appendSuccessResponse(workflowId);
            } catch (Exception e) {
                LOGGER.error("bulk retry exception, workflowId {}, message: {} ",workflowId, e.getMessage(), e);
                bulkResponse.appendFailedResponse(workflowId, e.getMessage());
            }
        }
        return bulkResponse;
    }

    /**
     * Terminate workflows execution.
     * @param workflowIds - list of workflow Ids  to perform terminate operation on
     * @param reason - description to be specified for the terminated workflow for future references.
     * @return bulk response object containing a list of succeeded workflows and a list of failed ones with errors
     */
    public BulkResponse terminate(List<String> workflowIds, String reason) {
        ServiceUtils.checkNotNullOrEmpty(workflowIds, "workflowIds list cannot be null.");
        ServiceUtils.checkArgument(workflowIds.size() < MAX_REQUEST_ITEMS, String.format("Cannot process more than %s workflows. Please use multiple requests", MAX_REQUEST_ITEMS));
        BulkResponse bulkResponse = new BulkResponse();
        for (String workflowId : workflowIds) {
            workflowExecutor.terminateWorkflow(workflowId, reason);
            try {
                workflowExecutor.terminateWorkflow(workflowId, reason);
                bulkResponse.appendSuccessResponse(workflowId);
            } catch (Exception e) {
                LOGGER.error("bulk terminate exception, workflowId {}, message: {} ",workflowId, e.getMessage(), e);
                bulkResponse.appendFailedResponse(workflowId, e.getMessage());
            }
        }
        return bulkResponse;
    }
}
