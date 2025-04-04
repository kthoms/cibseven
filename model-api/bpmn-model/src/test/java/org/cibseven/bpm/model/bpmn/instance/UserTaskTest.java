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
package org.cibseven.bpm.model.bpmn.instance;

import static org.cibseven.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_NS;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Sebastian Menski
 */
public class UserTaskTest extends BpmnModelElementInstanceTest {

  public TypeAssumption getTypeAssumption() {
    return new TypeAssumption(Task.class, false);
  }

  public Collection<ChildElementAssumption> getChildElementAssumptions() {
    return Arrays.asList(
      new ChildElementAssumption(Rendering.class)
    );
  }

  public Collection<AttributeAssumption> getAttributesAssumptions() {
    return Arrays.asList(
      new AttributeAssumption("implementation", false, false, "##unspecified"),
      /** camunda extensions */
      new AttributeAssumption(CAMUNDA_NS, "assignee"),
      new AttributeAssumption(CAMUNDA_NS, "candidateGroups"),
      new AttributeAssumption(CAMUNDA_NS, "candidateUsers"),
      new AttributeAssumption(CAMUNDA_NS, "dueDate"),
      new AttributeAssumption(CAMUNDA_NS, "followUpDate"),
      new AttributeAssumption(CAMUNDA_NS, "formHandlerClass"),
      new AttributeAssumption(CAMUNDA_NS, "formKey"),
      new AttributeAssumption(CAMUNDA_NS, "formRef"),
      new AttributeAssumption(CAMUNDA_NS, "formRefBinding"),
      new AttributeAssumption(CAMUNDA_NS, "formRefVersion"),
      new AttributeAssumption(CAMUNDA_NS, "priority")
    );
  }
}
