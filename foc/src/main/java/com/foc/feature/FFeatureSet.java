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
package com.foc.feature;

public class FFeatureSet {
	private Object featureArray[] = null;
	
	public FFeatureSet(int size){
		featureArray = new Object[size];
	}
	
	public void dispose(){
		featureArray = null;
	}
	
	public int getInteger(int feature){
		return (Integer)featureArray[feature];
	}
	
	public void setInteger(int feature, int i){
		featureArray[feature] = i;
	}
	
	public String getString(int feature){
		return (String)featureArray[feature];
	}
	
	public void setString(int feature, String str){
		featureArray[feature] = str;
	}
	
	public boolean getBoolean(int feature){
		return (Boolean)featureArray[feature];
	}
	
	public void setBoolean(int feature, Boolean b){
		featureArray[feature] = b;
	}
	
	public double getDouble(int feature){
		return (Double)featureArray[feature];
	}
	
	public void setDouble(int feature, double d){
		featureArray[feature] = d;
	}	
}
