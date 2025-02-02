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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.list.FocList;

public class StringMatchMaking {
	
	private ArrayList<StringMatchMakingLine> arrayLines    = null;
	
	private FocList                     list                 = null;
	private int                         fieldID              = 0   ;
	private ArrayList<IMatchTextObject> txtWithoutMatchArray = null;
	private int                         pushedCount          = 0;
	private HashMap<String, String>     veryCommonWords      = null;
	
	public StringMatchMaking(FocList list, int fieldID){
		this.list    = list;
		this.fieldID = fieldID;
		
		txtWithoutMatchArray = new ArrayList<IMatchTextObject>();
		arrayLines           = new ArrayList<StringMatchMakingLine>();
		veryCommonWords      = new HashMap<String, String>();
	}
	
	public void dispose(){
		list    = null;
		fieldID = 0;
		if(txtWithoutMatchArray != null){
			txtWithoutMatchArray.clear();
			txtWithoutMatchArray = null;
		}
		if(arrayLines != null){
			arrayLines.clear();
			arrayLines = null;
		}
	}
	
	public void pushVeryCommonWord(String word){
		veryCommonWords.put(word.toUpperCase(), word.toUpperCase());
	}

	public void outputArray(){
		for(int i=0; i<arrayLines.size(); i++){
			StringMatchMakingLine line = arrayLines.get(i);
			String objString = line.getObj().getPropertyString(fieldID);
			Globals.logString(objString+","+line.getText()+","+line.getNbrWords()+","+line.isSelected());
		}
	}
	
	public void output(){
		sortByObject();
		
		Globals.logString("\n");
		Globals.logString("Array of correspondance:");
		
		outputArray();
		
		Globals.logString("\n");
		Globals.logString("NOT FOUND OBJECTS:");
		
		for(int i=0; i<txtWithoutMatchArray.size(); i++){
			IMatchTextObject txtObj = txtWithoutMatchArray.get(i);
			Globals.logString(txtObj.getMatchDisplay());
		}
		
		Globals.logString("TOTAL Pushed "+pushedCount);
		Globals.logString("Note Found   "+txtWithoutMatchArray.size());
		Globals.logString("\n");
	}
	
	public boolean isTextPushed(String txt){
		boolean pushed = false; 
		for(int i=0; i<arrayLines.size() && !pushed; i++){
			StringMatchMakingLine line = arrayLines.get(i);
			pushed = txt.equals(line.getText());
		}
		for(int i=0; i<txtWithoutMatchArray.size() && !pushed; i++){
			IMatchTextObject matchObj = txtWithoutMatchArray.get(i);
			pushed = txt.equals(matchObj.getMatchText());
		}
		return pushed;
	}
	
	public void pushText(IMatchTextObject txtObj){
		String txt = txtObj.getMatchText();
		if(!isTextPushed(txt)){
			pushedCount++;
			Globals.logString("TEXT PUSHED : "+txtObj.getMatchDisplay());
			ArrayList<String> tokensArray = new ArrayList<String>();
			
			StringTokenizer strTok = new StringTokenizer(txt, " ");
			while(strTok.hasMoreTokens()){
				String tok = strTok.nextToken();
				if(tok != null && tok.length() > 3 && !veryCommonWords.containsKey(tok.toUpperCase())){
					tokensArray.add(tok);
				}
			}
			
			boolean added = false;
			
			for(int i=0; i<list.size(); i++){
				FocObject obj = list.getFocObject(i);
				if(obj != null){
					String objTxt = obj.getPropertyString(fieldID);
	
					int nbrMatch = 0;
					for(int t=0; t<tokensArray.size(); t++){
						String token = tokensArray.get(t);
						
						if(objTxt.contains(token)){
							nbrMatch += 1; 
						}
					}
					
					if(nbrMatch > 0){
						StringMatchMakingLine line = new StringMatchMakingLine(obj, txtObj, nbrMatch);
						arrayLines.add(line);
						added = true;
					}
				}
			}
			
			if(!added) txtWithoutMatchArray.add(txtObj);
		}
	}
	
	private void sortByObject(){
		Collections.sort(arrayLines, new Comparator<StringMatchMakingLine>(){
			@Override
			public int compare(StringMatchMakingLine o1, StringMatchMakingLine o2) {
				int diff = o1.getObj().getReference().getInteger() - o2.getObj().getReference().getInteger();
				if(diff == 0){
					diff = o2.getNbrWords() - o1.getNbrWords();
				}
				return diff;
			}
		});
	}
	
	private void sortByText(){
		Collections.sort(arrayLines, new Comparator<StringMatchMakingLine>(){
			@Override
			public int compare(StringMatchMakingLine o1, StringMatchMakingLine o2) {
				int diff = o1.getText().compareTo(o2.getText());
				if(diff == 0){
					if(o1.isSelected()) diff = -1;
					else if(o2.isSelected()) diff = +1;
				}
				if(diff == 0){
					diff = o2.getNbrWords() - o1.getNbrWords();
				}
				return diff;
			}
		});
	}
	
	public void select(){
		sortByObject();
			
		FocObject prevObj = null;
		for(int i=0; i<arrayLines.size(); i++){
			StringMatchMakingLine line = arrayLines.get(i);
			
			if(prevObj == null || !prevObj.equalsRef(line.getObj())){
				line.setSelected(true);
			}
			
			prevObj = line.getObj();
		}
		
		sortByText();
		
		Globals.logString("ARRAY AFTER SELECTION AND SORT BY TEXT");
		//outputArray();
		
		String  lastTxt  = null;
		for(int i=0; i<arrayLines.size(); i++){
			StringMatchMakingLine line = arrayLines.get(i);
			
			if(lastTxt == null || !lastTxt.equals(line.getText())){
				if(!line.isSelected()){
					txtWithoutMatchArray.add(line.getMatchTextObject());
				}
			}else{
				line.setSelected(false);
			}
				
			lastTxt  = line.getText();
		}
	}
}
