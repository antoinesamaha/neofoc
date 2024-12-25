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
package com.foc.desc.parsers.filter;

import java.util.HashMap;

import org.xml.sax.Attributes;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.annotations.model.FocFilterCondition;
import com.foc.desc.parsers.xml.FXMLDesc;
import com.foc.desc.parsers.xml.XMLFocDescParser;
import com.foc.list.filter.FocListFilter;
import com.foc.util.Utils;

public class ParsedFilterCondition {
	private String  fieldPath       = null;
	private String  prefix          = null;
	private String  caption         = null;
	private String  captionProperty = null;
	private String  className       = null;
	private int     level           = FocListFilter.LEVEL_DATABASE;
	
	private HashMap<String, String> captionLanguageMap = null;

	public ParsedFilterCondition(FocFilterCondition filterCondAnnotation){
		this.fieldPath = filterCondAnnotation.fieldPath();
		this.prefix = filterCondAnnotation.prefix();
		this.caption = filterCondAnnotation.caption();
		buildCaptionLanguageMap();
		this.captionProperty = filterCondAnnotation.captionProperty();
		this.level = filterCondAnnotation.level();
		this.className = filterCondAnnotation.className();
	}
	
	public ParsedFilterCondition(Attributes att){
		this.fieldPath = XMLFocDescParser.getString(att, FXMLDesc.ATT_FILTER_ON_FIELD);
		this.prefix = XMLFocDescParser.getString(att, FXMLDesc.ATT_FILTER_CONDITION_PREFIX);
		this.caption = XMLFocDescParser.getString(att, FXMLDesc.ATT_FILTER_CONDITION_CAPTION);
		buildCaptionLanguageMap();
		this.captionProperty = XMLFocDescParser.getString(att, FXMLDesc.ATT_FILTER_CONDITION_CAPTION_PROPERTY);
		this.level = FocListFilter.LEVEL_DATABASE;
		String levelStr = XMLFocDescParser.getString(att, FXMLDesc.ATT_FILTER_LEVEL);
		if(levelStr != null && levelStr.equals(FXMLDesc.VAL_FILTER_LEVEL_MEMORY)) {
			this.level = FocListFilter.LEVEL_MEMORY;
		}else if(levelStr != null && levelStr.equals(FXMLDesc.VAL_FILTER_LEVEL_DATABASE)) {
			this.level = FocListFilter.LEVEL_DATABASE;
		}else if(levelStr != null && levelStr.equals(FXMLDesc.VAL_FILTER_LEVEL_DATABASE_AND_MEMORY)) {
			this.level = FocListFilter.LEVEL_DATABASE_AND_MEMORY;
		}			
		this.className = XMLFocDescParser.getString(att, FXMLDesc.ATT_FILTER_CONDITION_CLASS_NAME);
	}
	
	public void buildCaptionLanguageMap() {
		try {
			if(!Utils.isStringEmpty(caption) && caption.startsWith("LANG{")) {
				String inner = caption.substring(5, caption.length()-1);

				while(inner.length() > 5) {
					String lang = inner.substring(0, 2);
					int endCrochet = inner.indexOf("]");
					String content = inner.substring(4, endCrochet);
					if(captionLanguageMap == null) captionLanguageMap = new HashMap<String, String>();
					captionLanguageMap.put(lang, content);
					if(inner.length() > endCrochet+1) {
						inner = inner.substring(endCrochet+1);
					} else {
						inner="";
					}
				}				
	 		}
		} catch (Exception e) {
			Globals.logException(e);
		}
	}

	public String getFieldPath() {
		return fieldPath;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getCaption() {
		String ret = caption;
		if(captionLanguageMap != null) {
			String lang = ConfigInfo.getLanguage();
			if(lang == null) lang = "en";
			ret = captionLanguageMap.get(lang);
		}
		return ret;
	}

	public String getCaptionProperty() {
		return captionProperty;
	}

	public void setCaptionProperty(String captionProperty) {
		this.captionProperty = captionProperty;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getClassName() {
		return className;
	}

}
