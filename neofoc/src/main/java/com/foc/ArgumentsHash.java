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
package com.foc;

import java.util.HashMap;

public class ArgumentsHash {
	private HashMap<String, String> argsHash = null;
	
	public ArgumentsHash(String[] args){
		argsHash = new HashMap<String, String>();

		if(args != null){
			for(int i=0; i<args.length; i++){
				String name  = null;
				String value = null;
				if (args[i] != null && args[i].startsWith("/")){
					String fullNoSlash  = args[i].substring(1);
					int separatorIndex = fullNoSlash.indexOf(':');
					if(separatorIndex < 0){
						name = fullNoSlash;
					}else{
						name  = fullNoSlash.substring(0, separatorIndex);
						if(separatorIndex+1 < fullNoSlash.length()){
							value = fullNoSlash.substring(separatorIndex+1);
						}
					}
				}
	    	if(value != null && name != null && name.trim().compareTo("") != 0 && value.trim().compareTo("") != 0){
	    		argsHash.put(name.toUpperCase(), value);
	    	}
	  		name  = null;
	  		value = null;
			}
		}
	}
	
	public String get(String key){
		return (String)argsHash.get(key.toUpperCase());
	}
}
