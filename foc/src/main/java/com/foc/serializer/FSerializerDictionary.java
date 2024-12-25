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

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.foc.Globals;
import com.foc.desc.FocObject;

public class FSerializerDictionary {
	
	private HashMap<String, ArrayList<FSerializerIdentifier>> map = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	public FSerializerDictionary(){
		
	}
	
	public static FSerializerDictionary getInstance() {
		return Globals.getApp().getHTMLGeneratorDictionary();
	}
	
	private String buildKey(String storageName, String type) {
		return storageName+"|"+type;
	}
	
	public void put(String storageName, String type, int version, Class<FSerializer> serializerClass) {
		if(map == null) {
			map = new HashMap<String, ArrayList<FSerializerIdentifier>>();
		}
		
		String key = buildKey(storageName,type);
		ArrayList<FSerializerIdentifier> serializerArray = map.get(key);
		if(serializerArray == null) {
			serializerArray = new ArrayList<FSerializerIdentifier>();
			map.put(key, serializerArray);
		}
		
		if(serializerArray != null) {
			FSerializerIdentifier handle = new FSerializerIdentifier(version, serializerClass);
			serializerArray.add(handle);
			
			Collections.sort(serializerArray, new Comparator<FSerializerIdentifier>() {
				@Override
				public int compare(FSerializerIdentifier o1, FSerializerIdentifier o2) {
					return o1.getVersion() - o2.getVersion();
				}
			});
		}
	}
	
	public FSerializerIdentifier getSerializerIdentifier(String storageName, String type) {
		return getSerializerIdentifier(storageName, type, 0);
	}
	
	public FSerializerIdentifier getSerializerIdentifier(String storageName, String type, int version) {
		FSerializerIdentifier foundSerId = null;
		
		String key = buildKey(storageName, type);
		if(map != null){
			ArrayList<FSerializerIdentifier> serializerArray = map.get(key);
			if(serializerArray != null) {
				for(int i=0; i<serializerArray.size(); i++) {
					FSerializerIdentifier serId = serializerArray.get(i);
					if(serId.getVersion() == version) {
						foundSerId = serId;
						break;
					}
					if(version == 0) {
						if(foundSerId == null || foundSerId.getVersion() < serId.getVersion()) {
							foundSerId = serId;
						}
					}
				}
			}
		}
		
		return foundSerId;
	}
	
	public String buildJSON(FocObject focObject) {
		String fullJson = "";
		StringBuffer buff = new StringBuffer();
		FSerializer ser = FSerializerDictionary.getInstance().newSerializer(focObject, buff, FSerializer.TYPE_JSON);
		if(ser != null) {
			try {
				ser.serializeToBuffer();
			} catch(Exception e) {
				Globals.logException(e);
			}
			fullJson = buff.toString();
			ser.dispose();
		}
		return fullJson;
	}
	
	public FSerializer newSerializer(FocObject focObject, StringBuffer buffer, String type) {
		return newSerializer(focObject, buffer, type, 0);
	}
	
	public FSerializer newSerializer(FocObject focObject, StringBuffer buffer, String type, int version) {
		FSerializer serializer = null;
		if(focObject != null && buffer != null && focObject.getThisFocDesc() != null) {
			String storageName = focObject.getThisFocDesc().getStorageName();
			FSerializerIdentifier serializerIdentifier = getSerializerIdentifier(storageName, type, version);
			if(serializerIdentifier != null) {
				if(version == 0) {
					version = serializerIdentifier.getVersion(); 
				}
				Class<FSerializer> serializerClass = serializerIdentifier.getSerializerClass();
				
		    try{
		      if(serializerClass != null){ 
		        Class[] classes = new Class[3];
		        classes[0] = focObject.getClass();
		        classes[1] = StringBuffer.class;
		        classes[2] = Integer.TYPE;
		    
		        Object[] objects = new Object[3];
		        objects[0] = focObject;
		        objects[1] = buffer;
		        objects[2] = version;
		        
		        Constructor construct = serializerClass.getConstructor(classes);
		        if(construct != null){
		        	serializer = (FSerializer) construct.newInstance(objects);
		        }
		      }
		    }catch (Exception e){
		    	if(serializerClass != null){
		    		Globals.logString("Could not create instance for class:"+serializerClass.getName());
		    	}
		      Globals.logException(e);
		    }
			}
		}
		return serializer;
	}
	
	public class FSerializerIdentifier {
		private int                version   = 0;
		private Class<FSerializer> className = null;
		
		public FSerializerIdentifier(int version, Class<FSerializer> serializerClass) {
			this.version = version;
			this.className = serializerClass;
		}

		public int getVersion() {
			return version;
		}

		public Class<FSerializer> getSerializerClass() {
			return className;
		}
	}
	
}
