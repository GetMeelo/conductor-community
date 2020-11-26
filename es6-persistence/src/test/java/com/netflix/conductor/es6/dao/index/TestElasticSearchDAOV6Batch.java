package com.netflix.conductor.es6.dao.index;

import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.run.SearchResult;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@TestPropertySource(properties = "workflow.elasticsearch.index.batchSize=2")
public class TestElasticSearchDAOV6Batch extends ElasticSearchDaoBaseTest {

    @Test
    public void indexTaskWithBatchSizeTwo() throws Exception {
        String correlationId = "some-correlation-id";

        Task task = new Task();
        task.setTaskId("some-task-id");
        task.setWorkflowInstanceId("some-workflow-instance-id");
        task.setTaskType("some-task-type");
        task.setStatus(Task.Status.FAILED);
        task.setInputData(new HashMap<String, Object>() {{
            put("input_key", "input_value");
        }});
        task.setCorrelationId(correlationId);
        task.setTaskDefName("some-task-def-name");
        task.setReasonForIncompletion("some-failure-reason");

        indexDAO.indexTask(task);
        indexDAO.indexTask(task);

        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    SearchResult<String> result = indexDAO
                            .searchTasks("correlationId='" + correlationId + "'", "*", 0, 10000, null);

                    assertTrue("should return 1 or more search results", result.getResults().size() > 0);
                    assertEquals("taskId should match the indexed task", "some-task-id", result.getResults().get(0));
                });
    }
}
