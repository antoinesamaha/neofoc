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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.foc.list.filter.FocListFilter;

@Retention(RUNTIME)
public @interface FocFilterCondition {
	public String fieldPath() default "";
	public String prefix() default "";
	public String caption() default "";
	public String captionProperty() default "";
	public int    level() default FocListFilter.LEVEL_DATABASE;
	public String className() default "";
}
