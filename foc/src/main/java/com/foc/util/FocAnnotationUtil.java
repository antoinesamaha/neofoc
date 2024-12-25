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
package com.foc.util;

import java.lang.annotation.Annotation;

public class FocAnnotationUtil {
	
	public static Annotation findAnnotation(Class cls, Class annotationClass){
		if(cls != null && annotationClass != null){
			Annotation[] annArray = cls.getAnnotationsByType(annotationClass);
			if(annArray.length > 0){
				return annArray[0];
			}
		}
		return null;
	}
}
