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
package com.foc.gui.table.view;

import java.util.ArrayList;

public class ViewKeyFactory {

	private ArrayList<ViewKey> viewKeyArray = null;
	
	private ViewKeyFactory(){
		viewKeyArray = new ArrayList<ViewKey>();
	}
	
	public void add(String key, String context){
		viewKeyArray.add(new ViewKey(key, context));
	}

	public int size(){
		return viewKeyArray.size();
	}

	public String getViewKey(int at){
		return viewKeyArray.get(at).key;
	}

	public String getViewContext(int at){
		return viewKeyArray.get(at).context;
	}

	private class ViewKey {
		public String key     = "";
		public String context = "";
		
		public ViewKey(String key, String context){
			this.key     = key;
			this.context = context;
		}
	}
	
	private static ViewKeyFactory factory = null;
	
	public static ViewKeyFactory getInstance(){
		if(factory == null){
			factory = new ViewKeyFactory();
		}
		return factory;
	}
}
