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
package com.foc.annotations.model.fields;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface FocForeignEntity {
	public String dbName() default "";
	public boolean mandatory() default false;
	public boolean cascade() default false;
	public boolean detach() default false;
	public boolean saveOnebyOne() default false;
	public String table() default "";
	public boolean cachedList() default true;
	public boolean allowNull() default true;
	public String listFilterProperty() default "";
	public String listFilterValue() default "";
	public String listFilterExpression() default "";
	public boolean dbResident() default true;
}
