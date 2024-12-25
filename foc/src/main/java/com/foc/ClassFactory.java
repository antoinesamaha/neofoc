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
package com.foc;

import java.util.HashMap;
import java.util.Iterator;

public class ClassFactory<C> {
	private HashMap<String, Class<? extends C>> classMap = null;
		
	public ClassFactory(){
		classMap = new HashMap<String, Class<? extends C>>();
	}
	
	public void addClass(String className, Class<? extends C> cls){
  	if(classMap != null && cls != null){
  		classMap.put(className, cls);
  	}
  }
	
	public Class<? extends C> getClass(String code){
    return classMap != null && code != null? classMap.get(code) : null;
  }
	
  public Iterator<String> newClassNameIterator(){
  	return classMap.keySet().iterator();
  }
  
  public C newInstance(String code) throws Exception {
  	Class<? extends C> cls = getClass(code);
  	return (C) cls.newInstance(); 	
  }
}
