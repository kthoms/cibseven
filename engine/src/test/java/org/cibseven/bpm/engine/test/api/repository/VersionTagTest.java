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
package org.cibseven.bpm.engine.test.api.repository;

import static org.junit.Assert.assertEquals;

import org.cibseven.bpm.engine.repository.DecisionDefinition;
import org.cibseven.bpm.engine.repository.ProcessDefinition;
import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.bpm.engine.test.util.PluggableProcessEngineTest;
import org.junit.Test;

/**
 * @author Stefan Hentschel.
 */
public class VersionTagTest extends PluggableProcessEngineTest {

  @Deployment
  @Test
  public void testParsingVersionTag() {
    ProcessDefinition process = repositoryService
      .createProcessDefinitionQuery()
      .orderByProcessDefinitionId()
      .asc()
      .singleResult();

    assertEquals("ver_tag_1", process.getVersionTag());
  }

  @Deployment(resources={"org/cibseven/bpm/engine/test/api/repository/processOne.bpmn20.xml"})
  @Test
  public void testParsingNullVersionTag() {
    ProcessDefinition process = repositoryService
      .createProcessDefinitionQuery()
      .orderByProcessDefinitionId()
      .asc()
      .singleResult();

    assertEquals(null, process.getVersionTag());
  }

  @Deployment(resources={"org/cibseven/bpm/engine/test/api/repository/versionTag.dmn"})
  @Test
  public void testParsingVersionTagDecisionDefinition() {
    DecisionDefinition decision = repositoryService
    .createDecisionDefinitionQuery()
    .orderByDecisionDefinitionVersion()
    .asc()
    .singleResult();

    assertEquals("1.0.0", decision.getVersionTag());
  }

  @Deployment(resources={"org/cibseven/bpm/engine/test/api/repository/noVersionTag.dmn"})
  @Test
  public void testParsingNullVersionTagDecisionDefinition() {
    DecisionDefinition decision = repositoryService
      .createDecisionDefinitionQuery()
    .orderByDecisionDefinitionVersion()
    .asc()
    .singleResult();

    assertEquals(null, decision.getVersionTag());
  }
}
