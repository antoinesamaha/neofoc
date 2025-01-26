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
package com.foc.shared.dataStore;

import java.util.ArrayList;

public class FDataRequestList<O extends FDataRequest_Abstract> {
	
	private ArrayList<O> arrayList = null;

	private int nextSerialNumber = 1;
	
	public FDataRequestList(){
		arrayList = new ArrayList<O>();
	}
	
	public int size(){
		return arrayList != null ? arrayList.size() : 0;
	}
	
	public O get(int i){
		return (O) (arrayList != null ? arrayList.get(i) : 0);
	}
	
	public void addDataRequest(O o){
		o.setSerialNumber(nextSerialNumber++);
		if(arrayList != null) arrayList.add(o);
	}

}
