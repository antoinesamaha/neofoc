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
package com.foc.list.filter;

import java.util.ArrayList;

import com.foc.desc.FocObject;
import com.foc.list.FocListWithFilter;

public interface IFocListFilter {
	public boolean includeObject(FocObject focObject);
  public boolean isActive();
	public void setActive(boolean active);
	public void setActive(FocListWithFilter focList, boolean active);
	public FilterDesc getThisFilterDesc();
	public ArrayList<Integer> getVisibleArray();

	public boolean isObjectExist(int objectIndex);
	public void addListener(FocListFilterListener listener);
	public void removeListener(FocListFilterListener listener);
}
