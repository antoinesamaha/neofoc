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
package com.foc.desc;

import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;

@SuppressWarnings("serial")
public class FocDescMap_ByFocObjectClassName extends HashMap<String, FocDesc>{
	
	public FocDescMap_ByFocObjectClassName(){
		
	}
	
	public void dispose(){
		Iterator<FocDesc> iter = values().iterator();
		while(iter != null && iter.hasNext()){
			FocDesc focDesc = iter.next();
			if(focDesc != null){
				focDesc.dispose();
			}
		}
		clear();
	}
	
	public static FocDescMap_ByFocObjectClassName getInstance(){
		return (Globals.getApp() != null) ? Globals.getApp().getFocDescMap_ByFocObjectClassName() : null;
	}

  @Override
  public FocDesc put(String arg0, FocDesc arg1) {
    return super.put(arg0, arg1);
  }	
}
