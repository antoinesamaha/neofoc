/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.desc.parsers.predefinedFields;

import java.lang.annotation.Annotation;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;

public interface IFocPredefinedFieldType<A extends Annotation> {
  public static final String TYPE_CODE           = "CODE";
  public static final String TYPE_COMPANY        = "COMPANY";
  public static final String TYPE_EXTERNAL_CODE  = "EXTERNAL_CODE";
  public static final String TYPE_DATE           = "DATE";
  public static final String TYPE_DESCRIPTION    = "DESCRIPTION";
  public static final String TYPE_NAME           = "NAME";
  public static final String TYPE_SITE           = "SITE";
  public static final String TYPE_ORDER          = "ORDER";
  public static final String TYPE_NOT_COMPLETED  = "NOT_COMPLETED";
  public static final String TYPE_IS_SYSTEM      = "IS_SYSTEM";
  public static final String TYPE_TREE           = "TREE";
  public static final String TYPE_REVIEW_STATUS  = "REVIEW_STATUS";
  public static final String TYPE_REVIEW_COMMENT = "REVIEW_COMMENT";
  public static final String TYPE_DEPRECATED     = "DEPRECATED";
  
	public String getTypeName();
	public FField newFField(FocDesc focDesc, A a);
}
