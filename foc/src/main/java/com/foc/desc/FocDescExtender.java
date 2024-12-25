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

import java.util.ArrayList;
import java.util.Iterator;

public class FocDescExtender {
	private ArrayList<IFocDescExtension> extensionArray = null;
	
	private ArrayList<IFocDescExtension> getExtensionArray(){
		if(extensionArray == null){
			extensionArray = new ArrayList<IFocDescExtension>();
		}
		return extensionArray;
	}
	
	public void addExtension(IFocDescExtension extension){
		getExtensionArray().add(extension);
	}
	
	public void removeExtension(IFocDescExtension extension){
		getExtensionArray().remove(extension);
	}
	
	private Iterator<IFocDescExtension> getExtensionIterator(){
		return getExtensionArray().iterator();
	}
	
	public void extendFocDesc(FocDesc focDesc){
		Iterator<IFocDescExtension> iter = getExtensionIterator();
		while(iter != null && iter.hasNext()){
			IFocDescExtension extension = iter.next();
			extension.extendFocDesc(focDesc);
		}
	}

}
