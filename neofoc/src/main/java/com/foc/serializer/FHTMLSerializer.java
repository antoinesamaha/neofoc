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

import com.foc.desc.FocObject;

public abstract class FHTMLSerializer<O extends FocObject> implements FSerializer {

	private O            focObject = null;
	private StringBuffer buffer    = null;
	private int          version   = 0;
	
	public FHTMLSerializer(O focObject, StringBuffer buffer, int version) {
		this.focObject = focObject;
		this.buffer = buffer;
		this.version = version;
	}
	
	public void dispose() {
		buffer = null;
		focObject = null;
	}
	
	public O getFocObject() {
		return focObject;
	}
	
	public int getVersion() {
		return version;
	}

	public void h1(String text) {
		buffer.append("<h1>"+text+"</h1>");
	}
	
	public void h2(String text) {
		buffer.append("<h2>"+text+"</h2>");
	}
	
	public void h3(String text) {
		buffer.append("<h2>"+text+"</h2>");
	}
	
	public void h4(String text) {
		buffer.append("<h2>"+text+"</h2>");
	}
	
	public void h5(String text) {
		buffer.append("<h2>"+text+"</h2>");
	}
	
	public void bold(String text) {
		buffer.append("<b>"+text+"</b>");
	}
	
	public void paragraph() {
		buffer.append("<p>");
	}
	
	public void breakLine() {
		breakLine(1);
	}
	
	public void breakLine(int nbr) {
		for(int i=0; i<nbr; i++) {
			buffer.append("<br>");
		}
	}
	
	public void space(int nbrOfSpaces) {
		for(int i=0; i<nbrOfSpaces; i++) {
			buffer.append("<&nbsp>");
		}
	}
}
