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
package org.cibseven.bpm.engine.impl;

import org.cibseven.bpm.engine.query.QueryProperty;

/**
 * @author roman.smirnov
 */
public interface IncidentQueryProperty {

  public static final QueryProperty INCIDENT_ID = new QueryPropertyImpl("ID_");
  public static final QueryProperty INCIDENT_MESSAGE = new QueryPropertyImpl("INCIDENT_MSG_");
  public static final QueryProperty INCIDENT_TIMESTAMP = new QueryPropertyImpl("INCIDENT_TIMESTAMP_");
  public static final QueryProperty INCIDENT_TYPE = new QueryPropertyImpl("INCIDENT_TYPE_");
  public static final QueryProperty EXECUTION_ID = new QueryPropertyImpl("EXECUTION_ID_");
  public static final QueryProperty ACTIVITY_ID = new QueryPropertyImpl("ACTIVITY_ID_");
  public static final QueryProperty PROCESS_INSTANCE_ID = new QueryPropertyImpl("PROC_INST_ID_");
  public static final QueryProperty PROCESS_DEFINITION_ID = new QueryPropertyImpl("PROC_DEF_ID_");
  public static final QueryProperty CAUSE_INCIDENT_ID = new QueryPropertyImpl("CAUSE_INCIDENT_ID_");
  public static final QueryProperty ROOT_CAUSE_INCIDENT_ID = new QueryPropertyImpl("ROOT_CAUSE_INCIDENT_ID_");
  public static final QueryProperty CONFIGURATION = new QueryPropertyImpl("CONFIGURATION_");
  public static final QueryProperty TENANT_ID = new QueryPropertyImpl("TENANT_ID_");

}
