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
package org.cibseven.bpm.integrationtest.functional.spin;

import static org.junit.Assert.assertNotNull;

import org.cibseven.bpm.application.PostDeploy;
import org.cibseven.bpm.application.ProcessApplication;
import org.cibseven.bpm.engine.ProcessEngine;


/**
 * @author Daniel Meyer
 *
 */
@ProcessApplication(PaDataformatAndPostDeployApp.PA_NAME)
// Using fully-qualified class name instead of import statement to allow for automatic Jakarta transformation
public class PaDataformatAndPostDeployApp extends org.cibseven.bpm.application.impl.ServletProcessApplication {

  public final static String PA_NAME  = "PaDataformatAndPostDeployApp";

  @PostDeploy
  public void onPaDeployed(ProcessEngine e) throws Exception {

    assertNotNull(getVariableSerializers());

  }

}
