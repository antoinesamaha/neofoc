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

import java.lang.reflect.Method;

import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;

public class ClassFocDescDeclaration implements IFocDescDeclaration {
	
	private Class     cls            = null;
	private FocDesc   focDescription = null;
	private FocModule focModule      = null;
	
	public ClassFocDescDeclaration(FocModule focModule, Class cls){
		this.cls       = cls;
		this.focModule = focModule;
	}
	
	public FocDesc getFocDescription() {
		if(focDescription == null){
			focDescription = ClassFocDescDeclaration.getFocDescriptionForClass(cls);
			if(focDescription != null){
				String name = focDescription.getName();
				Globals.getApp().putIFocDescDeclaration(name, this);
				focDescription.setModule(focModule);
			}
			if(focDescription != null){
				focDescription.setModule(focModule);
			}
		}
		return focDescription;
		
    /*FocDesc focDesc = null;
    try {
      if (cls != null) {
        Class[] argsDeclare = null;
        Object[] args = null;
        Method methodGetFocDesc = null;
        try{
        	methodGetFocDesc = cls.getMethod("getInstance", argsDeclare);
        }catch(NoSuchMethodException e){
        	methodGetFocDesc = cls.getMethod("getFocDesc", argsDeclare);
        }
        if(methodGetFocDesc != null){
        	focDesc = (FocDesc) methodGetFocDesc.invoke(null, args);
        }
      }
    } catch (Exception e) {
    	Globals.logString("Exception while getting FocDesc for class : "+cls.getName());
      Globals.logException(e);
    }
    
    return focDesc;*/
	}
	
	@SuppressWarnings("unchecked")
	public static FocDesc getFocDescriptionForClass(Class cls){
    FocDesc focDesc = null;
    try {
      if (cls != null) {
        Class[] argsDeclare = null;
        Object[] args = null;
        Method methodGetFocDesc = null;
        try{
        	methodGetFocDesc = cls.getMethod("getInstance", argsDeclare);
        }catch(NoSuchMethodException e){
        	methodGetFocDesc = cls.getMethod("getFocDesc", argsDeclare);
        }
        if(methodGetFocDesc != null){
        	focDesc = (FocDesc) methodGetFocDesc.invoke(null, args);
        }
      }
    } catch (Exception e) {
      //Globals.getDisplayManager().popupMessage("getFocDescriptionForClass");
    	Globals.logString("Exception while getting FocDesc for class : "+cls.getName());
      Globals.logException(e);
    }
    return focDesc;
	}

	public int getPriority() {
		return IFocDescDeclaration.PRIORITY_FIRST;
	}

	@Override
	public FocModule getFocModule() {
		return focModule;
	}

	@Override
	public String getName() {
		return null;
	}
}
