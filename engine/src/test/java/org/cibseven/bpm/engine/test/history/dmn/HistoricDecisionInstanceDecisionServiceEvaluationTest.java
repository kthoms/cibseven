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
package org.cibseven.bpm.engine.test.history.dmn;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.cibseven.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.cibseven.bpm.engine.HistoryService;
import org.cibseven.bpm.engine.ProcessEngineConfiguration;
import org.cibseven.bpm.engine.RepositoryService;
import org.cibseven.bpm.engine.RuntimeService;
import org.cibseven.bpm.engine.history.HistoricDecisionInstance;
import org.cibseven.bpm.engine.repository.ProcessDefinition;
import org.cibseven.bpm.engine.runtime.ProcessInstance;
import org.cibseven.bpm.engine.test.ProcessEngineRule;
import org.cibseven.bpm.engine.test.RequiredHistoryLevel;
import org.cibseven.bpm.engine.test.history.DecisionServiceDelegate;
import org.cibseven.bpm.engine.test.util.ProcessEngineTestRule;
import org.cibseven.bpm.engine.test.util.ProvidedProcessEngineRule;
import org.cibseven.bpm.engine.test.util.ResetDmnConfigUtil;
import org.cibseven.bpm.engine.variable.Variables;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@RequiredHistoryLevel(ProcessEngineConfiguration.HISTORY_FULL)
public class HistoricDecisionInstanceDecisionServiceEvaluationTest {

  protected static final String DECISION_PROCESS_WITH_DECISION_SERVICE = "org/cibseven/bpm/engine/test/history/HistoricDecisionInstanceTest.testDecisionEvaluatedWithDecisionServiceInsideDelegation.bpmn20.xml";
  protected static final String DECISION_PROCESS_WITH_START_LISTENER = "org/cibseven/bpm/engine/test/history/HistoricDecisionInstanceTest.testDecisionEvaluatedWithDecisionServiceInsideStartListener.bpmn20.xml";
  protected static final String DECISION_PROCESS_WITH_END_LISTENER = "org/cibseven/bpm/engine/test/history/HistoricDecisionInstanceTest.testDecisionEvaluatedWithDecisionServiceInsideEndListener.bpmn20.xml";
  protected static final String DECISION_PROCESS_WITH_TAKE_LISTENER = "org/cibseven/bpm/engine/test/history/HistoricDecisionInstanceTest.testDecisionEvaluatedWithDecisionServiceInsideTakeListener.bpmn20.xml";
  protected static final String DECISION_PROCESS_INSIDE_EXPRESSION = "org/cibseven/bpm/engine/test/history/HistoricDecisionInstanceTest.testDecisionEvaluatedWithDecisionServiceInsideExpression.bpmn20.xml";
  protected static final String DECISION_PROCESS_INSIDE_DELEGATE_EXPRESSION = "org/cibseven/bpm/engine/test/history/HistoricDecisionInstanceTest.testDecisionEvaluatedWithDecisionServiceInsideDelegateExpression.bpmn20.xml";

  protected static final String DECISION_DMN = "org/cibseven/bpm/engine/test/history/HistoricDecisionInstanceTest.decisionSingleOutput.dmn11.xml";

  protected static final String DECISION_DEFINITION_KEY = "testDecision";

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      { DECISION_PROCESS_WITH_DECISION_SERVICE, "task" },
      { DECISION_PROCESS_WITH_START_LISTENER, "task" },
      { DECISION_PROCESS_WITH_END_LISTENER, "task" },
      { DECISION_PROCESS_INSIDE_EXPRESSION, "task" },
      { DECISION_PROCESS_INSIDE_DELEGATE_EXPRESSION, "task" },
      { DECISION_PROCESS_WITH_TAKE_LISTENER, "start" }
    });
  }

  @Parameter(0)
  public String process;

  @Parameter(1)
  public String activityId;

  @Rule
  public ProcessEngineRule engineRule = new ProvidedProcessEngineRule();

  @Rule
  public ProcessEngineTestRule testRule = new ProcessEngineTestRule(engineRule);

  protected RuntimeService runtimeService;
  protected RepositoryService repositoryService;
  protected HistoryService historyService;

  @Before
  public void init() {
    testRule.deploy(DECISION_DMN, process);

    runtimeService = engineRule.getRuntimeService();
    repositoryService = engineRule.getRepositoryService();
    historyService = engineRule.getHistoryService();
  }

  @Before
  public void enableDmnFeelLegacyBehavior() {
    DefaultDmnEngineConfiguration dmnEngineConfiguration =
        engineRule.getProcessEngineConfiguration()
            .getDmnEngineConfiguration();

    ResetDmnConfigUtil.reset(dmnEngineConfiguration)
        .enableFeelLegacyBehavior(true)
        .init();
  }

  @After
  public void disableDmnFeelLegacyBehavior() {

    DefaultDmnEngineConfiguration dmnEngineConfiguration =
        engineRule.getProcessEngineConfiguration()
            .getDmnEngineConfiguration();

    ResetDmnConfigUtil.reset(dmnEngineConfiguration)
        .enableFeelLegacyBehavior(false)
        .init();
  }

  @Test
  public void evaluateDecisionWithDecisionService() {

    runtimeService.startProcessInstanceByKey("testProcess", Variables.createVariables()
        .putValue("input1", null)
        .putValue("myBean", new DecisionServiceDelegate()));

    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().singleResult();
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
    String decisionDefinitionId = repositoryService.createDecisionDefinitionQuery().decisionDefinitionKey(DECISION_DEFINITION_KEY).singleResult().getId();

    HistoricDecisionInstance historicDecisionInstance = historyService.createHistoricDecisionInstanceQuery().singleResult();

    assertThat(historicDecisionInstance).isNotNull();
    assertThat(historicDecisionInstance.getDecisionDefinitionId()).isEqualTo(decisionDefinitionId);
    assertThat(historicDecisionInstance.getDecisionDefinitionKey()).isEqualTo(DECISION_DEFINITION_KEY);
    assertThat(historicDecisionInstance.getDecisionDefinitionName()).isEqualTo("sample decision");

    // references to process instance should be set since the decision is evaluated while executing a process instance
    assertThat(historicDecisionInstance.getProcessDefinitionKey()).isEqualTo(processDefinition.getKey());
    assertThat(historicDecisionInstance.getProcessDefinitionId()).isEqualTo(processDefinition.getId());
    assertThat(historicDecisionInstance.getProcessInstanceId()).isEqualTo(processInstance.getId());
    assertThat(historicDecisionInstance.getCaseDefinitionKey()).isNull();
    assertThat(historicDecisionInstance.getCaseDefinitionId()).isNull();
    assertThat(historicDecisionInstance.getCaseInstanceId()).isNull();
    assertThat(historicDecisionInstance.getActivityId()).isEqualTo(activityId);
    assertThat(historicDecisionInstance.getEvaluationTime()).isNotNull();
  }

}
