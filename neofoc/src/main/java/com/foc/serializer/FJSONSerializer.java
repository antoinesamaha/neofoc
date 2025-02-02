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
package com.foc.serializer;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

import com.foc.desc.FocObject;
import com.foc.property.FProperty;
import com.foc.shared.json.B01JsonBuilder;
import com.foc.util.Utils;

public abstract class FJSONSerializer<O extends FocObject> extends B01JsonBuilder implements FSerializer {

	private O            focObject = null;
	private int          version   = 0;
	
	public FJSONSerializer(O focObject, StringBuffer buffer, int version) {
		super(buffer);
		this.focObject = focObject;
		this.version = version;
	}
	
	public void dispose() {
		super.dispose();
		focObject = null;
	}
	
	public O getFocObject() {
		return focObject;
	}
	
	public int getVersion() {
		return version;
	}

	public String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		return date != null ? sdf.format(date) : "";
	}
	
	public String formatTime(Time date) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return date != null ? sdf.format(date) : "";
	}
	
	public void appendPropertyValue(String key, String propertyPath){
		appendPropertyValue(key, focObject, propertyPath);
	}
	
	public void appendPropertyValue(String key, FocObject focObj, String propertyPath){
		String value = null;
		FProperty prop = null;
		if(propertyPath != null && focObj != null) {
			prop = focObj.getFocPropertyForPath(propertyPath);
		}
		if(prop != null) {
			value = prop.getString();
			if(!Utils.isStringEmpty(value)) {
				appendKeyValue(key, value);
			}
		}
	}
}
