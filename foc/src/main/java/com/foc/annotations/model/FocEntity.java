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
package com.foc.annotations.model;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.foc.list.filter.FocListFilter;

@Retention(RUNTIME)
@Target(TYPE)
public @interface FocEntity {
	public String name() default "";
	public String storageName() default "";
	public boolean dbResident() default true;
	public boolean allowAdaptDataModel() default true;
	public boolean isTree() default false;
	public boolean hasRevision() default false;
	public FocJoin[] joins() default {};
	public String filterOnTable() default "";
	public FocFilterCondition[] filterConditions() default {};
	public FocGroupByField[] groupByFields() default {};
	public String reportContext() default "";
	public boolean cached() default true;
	public int filterLevel() default FocListFilter.LEVEL_DATABASE;
	public boolean logicalDelete() default false;
}
