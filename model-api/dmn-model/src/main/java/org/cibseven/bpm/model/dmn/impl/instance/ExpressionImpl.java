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
package org.cibseven.bpm.model.dmn.impl.instance;

import static org.cibseven.bpm.model.dmn.impl.DmnModelConstants.LATEST_DMN_NS;
import static org.cibseven.bpm.model.dmn.impl.DmnModelConstants.DMN_ATTRIBUTE_TYPE_REF;
import static org.cibseven.bpm.model.dmn.impl.DmnModelConstants.DMN_ELEMENT_EXPRESSION;

import org.cibseven.bpm.model.dmn.instance.DmnElement;
import org.cibseven.bpm.model.dmn.instance.Expression;
import org.cibseven.bpm.model.xml.ModelBuilder;
import org.cibseven.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.cibseven.bpm.model.xml.type.ModelElementTypeBuilder;
import org.cibseven.bpm.model.xml.type.attribute.Attribute;

public abstract class ExpressionImpl extends DmnElementImpl implements Expression {

  protected static Attribute<String> typeRefAttribute;

  public ExpressionImpl(ModelTypeInstanceContext instanceContext) {
    super(instanceContext);
  }

  public String getTypeRef() {
    return typeRefAttribute.getValue(this);
  }

  public void setTypeRef(String typeRef) {
    typeRefAttribute.setValue(this, typeRef);
  }

  public static void registerType(ModelBuilder modelBuilder) {
    ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(Expression.class, DMN_ELEMENT_EXPRESSION)
      .namespaceUri(LATEST_DMN_NS)
      .extendsType(DmnElement.class)
      .abstractType();

    typeRefAttribute = typeBuilder.stringAttribute(DMN_ATTRIBUTE_TYPE_REF)
      .build();

    typeBuilder.build();
  }

}
