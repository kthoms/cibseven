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
package org.cibseven.bpm.admin.impl.web;

import static org.cibseven.bpm.engine.authorization.Authorization.ANY;
import static org.cibseven.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.cibseven.bpm.engine.authorization.Permissions.ALL;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import javax.servlet.ServletException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import org.cibseven.bpm.engine.AuthorizationService;
import org.cibseven.bpm.engine.IdentityService;
import org.cibseven.bpm.engine.ProcessEngine;
import org.cibseven.bpm.engine.authorization.Groups;
import org.cibseven.bpm.engine.authorization.Resource;
import org.cibseven.bpm.engine.authorization.Resources;
import org.cibseven.bpm.engine.identity.Group;
import org.cibseven.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.cibseven.bpm.engine.rest.dto.identity.UserDto;
import org.cibseven.bpm.engine.rest.impl.UserRestServiceImpl;
import org.cibseven.bpm.engine.rest.spi.ProcessEngineProvider;
import org.cibseven.bpm.engine.rest.util.ProvidersUtil;
import org.cibseven.bpm.webapp.impl.WebappLogger;
import org.cibseven.bpm.webapp.impl.security.SecurityActions;
import org.cibseven.bpm.webapp.impl.security.SecurityActions.SecurityAction;

/**
 * <p>Jax RS resource allowing to perform the setup steps.</p>
 *
 * <p>All methods of this class must throw Status.FORBIDDEN exception if
 * setup actions are unavailable.</p>
 *
 * @author Daniel Meyer
 *
 */
@Path("/setup/{engine}")
public class SetupResource {

  protected final static WebappLogger LOGGER = WebappLogger.INSTANCE;

  @Context
  protected Providers providers;

  @Path("/user/create")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public void createInitialUser(final @PathParam("engine") String processEngineName, final UserDto user) throws IOException, ServletException {

    final ProcessEngine processEngine = lookupProcessEngine(processEngineName);
    if(processEngine == null) {
      throw LOGGER.invalidRequestEngineNotFoundForName(processEngineName);
    }

    SecurityActions.runWithoutAuthentication(new SecurityAction<Void>() {
      public Void execute() {
        createInitialUserInternal(processEngineName, user, processEngine);
        return null;
      }
    }, processEngine);

  }

  protected void createInitialUserInternal(String processEngineName, UserDto user, ProcessEngine processEngine) {

    ObjectMapper objectMapper = getObjectMapper();

    // make sure we can process this request at this time
    ensureSetupAvailable(processEngine);

    // reuse logic from rest api implementation
    UserRestServiceImpl userRestServiceImpl = new UserRestServiceImpl(processEngineName, objectMapper);
    userRestServiceImpl.createUser(user);

    // crate the camunda admin group
    ensureCamundaAdminGroupExists(processEngine);

    // create group membership (add new user to admin group)
    processEngine.getIdentityService()
      .createMembership(user.getProfile().getId(), Groups.CAMUNDA_ADMIN);
  }

  protected ObjectMapper getObjectMapper() {
    if(providers != null) {
      return ProvidersUtil
        .resolveFromContext(providers, ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE, this.getClass());
    }
    else {
      return null;
    }
  }

  protected void ensureCamundaAdminGroupExists(ProcessEngine processEngine) {

    final IdentityService identityService = processEngine.getIdentityService();
    final AuthorizationService authorizationService = processEngine.getAuthorizationService();

    // create group
    if(identityService.createGroupQuery().groupId(Groups.CAMUNDA_ADMIN).count() == 0) {
      Group camundaAdminGroup = identityService.newGroup(Groups.CAMUNDA_ADMIN);
      camundaAdminGroup.setName("camunda BPM Administrators");
      camundaAdminGroup.setType(Groups.GROUP_TYPE_SYSTEM);
      identityService.saveGroup(camundaAdminGroup);
    }

    // create ADMIN authorizations on all built-in resources
    for (Resource resource : Resources.values()) {
      if(authorizationService.createAuthorizationQuery().groupIdIn(Groups.CAMUNDA_ADMIN).resourceType(resource).resourceId(ANY).count() == 0) {
        AuthorizationEntity userAdminAuth = new AuthorizationEntity(AUTH_TYPE_GRANT);
        userAdminAuth.setGroupId(Groups.CAMUNDA_ADMIN);
        userAdminAuth.setResource(resource);
        userAdminAuth.setResourceId(ANY);
        userAdminAuth.addPermission(ALL);
        authorizationService.saveAuthorization(userAdminAuth);
      }
    }

  }

  protected void ensureSetupAvailable(ProcessEngine processEngine) {
    if (processEngine.getIdentityService().isReadOnly()
        || (processEngine.getIdentityService().createUserQuery().memberOfGroup(Groups.CAMUNDA_ADMIN).count() > 0)) {

      throw LOGGER.setupActionNotAvailable();
    }
  }

  protected ProcessEngine lookupProcessEngine(String engineName) {

    ServiceLoader<ProcessEngineProvider> serviceLoader = ServiceLoader.load(ProcessEngineProvider.class);
    Iterator<ProcessEngineProvider> iterator = serviceLoader.iterator();

    if (iterator.hasNext()) {
      ProcessEngineProvider provider = iterator.next();
      return provider.getProcessEngine(engineName);

    } else {
      throw LOGGER.processEngineProviderNotFound();

    }

  }


}
