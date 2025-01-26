/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.foc.api;

import java.util.Iterator;

public interface IFocList {
	public Iterator<IFocObject> iFocList_newIterator();
	public IFocObject           iFocList_newFocObject();
	public void                 iFocList_addFocObject(IFocObject iFocObject);
	public void                 iFocList_newAddedFocObject();
	public IFocObject           iFocList_searchByPropertyValue(String fieldName, Object value);
	public IFocObject           iFocList_searchByPropertiesValues(String[] fieldNames, Object[] values);
	
	public boolean              iFocList_validate();
	public void                 iFocList_cancel();
	public void                 iFocList_dispose();
}
