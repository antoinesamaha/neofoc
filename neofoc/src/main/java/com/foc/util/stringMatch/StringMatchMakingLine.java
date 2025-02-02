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
package com.foc.util.stringMatch;

import com.foc.desc.FocObject;

public class StringMatchMakingLine {
	private FocObject        focObj    = null;
	private int              nbrWords  = 0;
	private IMatchTextObject matchText = null;
	private boolean          selected  = false;
	
	public StringMatchMakingLine(FocObject focObj, IMatchTextObject matchTextObj, int nbrWords){
		this.focObj      = focObj;
		this.matchText   = matchTextObj;
		this.nbrWords    = nbrWords;
	}
	
	public void dispose(){
		this.focObj    = null;
		this.matchText = null;
		this.nbrWords  = 0;
	}

	public FocObject getObj() {
		return focObj;
	}

	public int getNbrWords() {
		return nbrWords;
	}

	public String getText() {
		IMatchTextObject iMatchText = getMatchTextObject();
		return iMatchText.getMatchText();
	}
	
	public IMatchTextObject getMatchTextObject() {
		return matchText;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
