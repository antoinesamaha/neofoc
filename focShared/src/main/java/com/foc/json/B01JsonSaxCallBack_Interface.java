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
package com.foc.json;

public interface B01JsonSaxCallBack_Interface {
	//These are low level functions they can be left unimplemented
	public void startObject();
	public void startList();
	public void endObject();
	public void endList();
	public void startString();
	public void endString(StringBuffer strBuff);

	//These are low level functions better use newKeyValuePair method
	public void newKey(StringBuffer key);
	public void newValue(StringBuffer value);
	
	//For an easier SAX implement these functions
	public void newKeyValuePair(String key, String value);
	public void newObject(String objectKey, String key, String reference);
	public void newList(String key);
	
	public void dispose();
}
