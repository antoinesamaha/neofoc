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

import java.sql.Date;

//Interface for FocObject 
public interface IFocObject {
  public String     iFocObject_getPropertyString(String fieldName);
  public void       iFocObject_setPropertyString(String fieldName, String value);

  public double     iFocObject_getPropertyDouble(String fieldName);
  public void       iFocObject_setPropertyDouble(String fieldName, double value);

  public int        iFocObject_getPropertyInteger(String fieldName);
  public void       iFocObject_setPropertyInteger(String fieldName, int value);

  public IFocObject iFocObject_getPropertyFocObject(String fieldName);
  public void       iFocObject_setPropertyFocObject(String fieldName, IFocObject value);

  public Date       iFocObject_getPropertyDate(String fieldName);
  public void       iFocObject_setPropertyDate(String fieldName, Date value);

  public IFocList   iFocObject_getPropertyList(String fieldName);

	public IFocObject iFocObject_getFatherObject();
	
	public boolean    iFocObject_validate();
	public void       iFocObject_cancel();
	public void       iFocObject_dispose();
}
