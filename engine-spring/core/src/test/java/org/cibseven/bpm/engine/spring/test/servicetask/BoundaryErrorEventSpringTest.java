/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
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
package org.cibseven.bpm.engine.spring.test.servicetask;

import org.cibseven.bpm.engine.spring.test.SpringProcessEngineTestCase;
import org.cibseven.bpm.engine.task.Task;
import org.cibseven.bpm.engine.test.Deployment;
import org.springframework.test.context.ContextConfiguration;

/**
 * @see http://jira.codehaus.org/browse/ACT-1166
 * @author Angel López Cima
 * @author Falko Menge
 */
@ContextConfiguration("classpath:org/cibseven/bpm/engine/spring/test/servicetask/serviceraskSpringTestCatchError-context.xml")
public class BoundaryErrorEventSpringTest extends SpringProcessEngineTestCase {

  @Deployment
  public void testCatchErrorThrownByJavaDelegateOnServiceTask() {
    String procId = runtimeService.startProcessInstanceByKey("catchErrorThrownByExpressionDelegateOnServiceTask").getId();
    assertThatErrorHasBeenCaught(procId);
  }

  private void assertThatErrorHasBeenCaught(String procId) {
    // The service task will throw an error event,
    // which is caught on the service task boundary
    assertEquals("No tasks found in task list.", 1, taskService.createTaskQuery().count());
    Task task = taskService.createTaskQuery().singleResult();
    assertEquals("Escalated Task", task.getName());

    // Completing the task will end the process instance
    taskService.complete(task.getId());
    assertProcessEnded(procId);
  }
}
