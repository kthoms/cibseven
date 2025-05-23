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
package org.cibseven.bpm.spring.boot.starter.contextcache.pa;

import org.cibseven.bpm.engine.ProcessEngines;
import org.cibseven.bpm.engine.runtime.ProcessInstance;
import org.cibseven.bpm.spring.boot.starter.contextcache.AbstractContextCacheTest;
import org.cibseven.bpm.spring.boot.starter.test.pa.TestProcessApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cibseven.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;

/**
 * Tests {@link PaContextCacheTest1}, {@link PaContextCacheTest2}, {@link PaContextCacheTest3}
 * {@link PaContextCacheTest4} and {@link PaContextCacheTest5} are meant to be run together
 * so that ApplicationContext caching is tested.
 * See {@link PaContextCacheSuiteTest} for a detailed explanation.
 *
 * @author Nikola Koevski
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("contextcaching")
@SpringBootTest(
  classes = { TestProcessApplication.class },
  properties = {
    "camunda.bpm.generate-unique-process-engine-name=true",
    "camunda.bpm.generate-unique-process-application-name=true",
    "spring.datasource.generate-unique-name=true",
    "paTest2Property=paTest2Value"
  },
  webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class PaContextCacheTest4 extends AbstractContextCacheTest {

  @Before
  public void setUp() {
    this.testName = "paTest4";
    this.contextMap.put(this.testName, applicationContext.hashCode());

    // ensure that Camunda Platform Assert is using the non-default engine
    init(processEngine);
  }

  @Test
  public void testContextCaching() {
    assertThat(applicationContext.hashCode()).isNotEqualTo(contextMap.get("paTest1"));
    assertThat(applicationContext.hashCode()).isEqualTo(contextMap.get("paTest2"));
  }

  @Override
  @Test
  public void testEngineName()
  {
    assertThat(processEngine.getName()).isNotEqualTo(ProcessEngines.NAME_DEFAULT);
    assertThat(processEngine.getName()).containsPattern("processEngine\\w{10}");
  }

  @Override
  @Test
  public void testDbIsolation() {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("TestProcess");
    assertThat(instance).isNotNull();

    long numInstances = runtimeService.createProcessInstanceQuery().count();
    assertThat(numInstances).isEqualTo(2);
  }
}
