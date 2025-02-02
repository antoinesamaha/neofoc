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
package com.foc.pivot;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FPivotRowList extends FocList {
	
	public FPivotRowList(FocDesc desc){
		super(new FocLinkSimple(desc));
	}
	
	public FPivotRowList(FocDesc desc, FPivotRowList list){
	  super(new FocLinkSimple(desc));
	  
	  for(int i=0; i<list.size(); i++){
	    FocObject temp = this.newEmptyItem();
	    temp = list.getFocObject(i);
	    this.add(temp);
	  }
	}

}
