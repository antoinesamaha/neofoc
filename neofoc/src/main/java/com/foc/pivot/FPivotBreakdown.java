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
package com.foc.pivot;

import java.sql.Date;
import java.text.SimpleDateFormat;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.formula.FocSimpleFormulaContext;
import com.foc.formula.Formula;
import com.foc.list.FocListOrderFocObject;
import com.foc.property.FObject;
import com.foc.shared.dataStore.IFocData;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public class FPivotBreakdown extends FocObject implements FPivotConst {

	private FocSimpleFormulaContext groupBy_FormulaContext = null;
	
	//This object is used when the sort expression is not a simple dataPath example DATE,-MVT_SIGN
	private FocListOrderFocObject   listOrderFocObject    = null;
	private FPivotRowTree pivotTree = null;
	
	public FPivotBreakdown(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public FPivotBreakdown(FocConstructor constr, String dataPath, boolean isEntireTreeShown) {
	  super(constr);
	  newFocProperties();
	  
	  setGroupBy(dataPath);
	  setName(dataPath);
	  setTreeShown(isEntireTreeShown);
	}
	
	public void dispose(){
		super.dispose();
	}
	
	public String getGroupBy(){
	  return getPropertyString(FLD_BKDN_GROUP_BY);
	}
	
	public void setGroupBy(String datapath){
	  setPropertyString(FLD_BKDN_GROUP_BY, datapath);
	}
	
	public String getSortBy(){
	  return getPropertyString(FLD_BKDN_SORT_BY);
	}
	
	public void setSortBy(String datapath){
	  setPropertyString(FLD_BKDN_SORT_BY, datapath);
	}
	
	public boolean isEntireTreeShown(){
	  return getPropertyBoolean(FLD_BKDN_SHOW_ENTIRE_TREE);
	}
	
	public void setTreeShown(boolean bool){
	  setPropertyBoolean(FLD_BKDN_SHOW_ENTIRE_TREE, bool);
	}
	
	public boolean isWrapeNativeObject(){
	  return getPropertyBoolean(FLD_WRAPE_NATIVE_OBJECT);
	}

	public void setWrapeNativeObject(String wrapeStr){
		if(wrapeStr != null){
			if(wrapeStr.toLowerCase().equals("true") || wrapeStr.equals("1")){
				setWrapeNativeObject(true);
			}
		}
	}

	public void setWrapeNativeObject(boolean wrape){
	  setPropertyBoolean(FLD_WRAPE_NATIVE_OBJECT, wrape);
	}
	
	public String getTitleCaption(){
	  return getPropertyString(FLD_BKDN_TITLE_CAPTION);
	}
	
	public void setTitleCaption(String property){
	  setPropertyString(FLD_BKDN_TITLE_CAPTION, property);
	}
	
	public String getDescriptionCaption(){
	  return getPropertyString(FLD_BKDN_DESCRIPTION_CAPTION);
	}
	
	public void setDescriptionCaption(String property){
	  setPropertyString(FLD_BKDN_DESCRIPTION_CAPTION, property);
	}
	
	public String getTitleWhenEmpty(){
	  return getPropertyString(FLD_TITLE_WHEN_EMPTY);
	}
	
	public void setTitleWhenEmpty(String property){
	  setPropertyString(FLD_TITLE_WHEN_EMPTY, property);
	}
	
	public String getDescriptionWhenEmpty(){
	  return getPropertyString(FLD_DESCRIPTION_WHEN_EMPTY);
	}
	
	public void setDescriptionWhenEmpty(String property){
	  setPropertyString(FLD_DESCRIPTION_WHEN_EMPTY, property);
	}

	public Date getCutOffDate(){
	  return getPropertyDate(FLD_BKDN_CUT_OFF_DATE);
	}
	
	public void setCutOffDate(Date start){
	  setPropertyDate(FLD_BKDN_CUT_OFF_DATE, start);
	}
	
	public void setCutOffDate(String cutOfDate){
	  if(!Utils.isStringEmpty(cutOfDate)){
	    Date temp = null;
	    
	    try{
	      temp = Date.valueOf(cutOfDate);
	    }
	    catch(IllegalArgumentException e){
	      Globals.logException(e);
	    }
	    
	    setCutOffDate(temp);
	  }
	}

	public Date getDateStart(){
	  return getPropertyDate(FLD_BKDN_START_DATE);
	}
	
	public void setDateStart(Date start){
	  setPropertyDate(FLD_BKDN_START_DATE, start);
	}
	
	public Date getDateEnd(){
	  return getPropertyDate(FLD_BKDN_END_DATE);
	}
	
	public void setDateEnd(Date end){
	  setPropertyDate(FLD_BKDN_END_DATE, end);
	}
	
	public boolean isHideWhenOnlyChild(){
		return getPropertyBoolean(FLD_BKDN_HIDE_WHEN_ALONE);
	}
	
	public void setHideWhenOnlyChild(boolean hide){
		setPropertyBoolean(FLD_BKDN_HIDE_WHEN_ALONE, hide);
	}
	
	public int getDateGrouping(){
		return getPropertyMultiChoice(FPivotBreakdownDesc.FLD_DATE_GROUPING);
	}	

	public void setDateGrouping(String dateGrouping){
		int dateGroupingInt = DATE_GROUPING_NONE;
		if(dateGrouping != null && !dateGrouping.isEmpty()){
			if(dateGrouping.equals(DATE_GROUPING_CHOICE_MONTHLY)){
				dateGroupingInt = DATE_GROUPING_MONTHLY;
			}else if(dateGrouping.equals(DATE_GROUPING_CHOICE_YEARLY)){
				dateGroupingInt = DATE_GROUPING_YEARLY;
			}
		}
		setPropertyMultiChoice(FPivotBreakdownDesc.FLD_DATE_GROUPING, dateGroupingInt);
	}
	
	public FocSimpleFormulaContext groupBy_FormulaContext(){
		String groupBy = getGroupBy();
		if(groupBy_FormulaContext == null && groupBy.toUpperCase().startsWith("EVAL(")){
			String formulaString = groupBy.substring(groupBy.indexOf("(")+1, groupBy.lastIndexOf(")"));
			if(formulaString != null && !formulaString.isEmpty()){
				Formula formula = new Formula(formulaString);
				groupBy_FormulaContext =new FocSimpleFormulaContext(formula);
			}			
		}
		return groupBy_FormulaContext;
	}
	
	public IFocData getGroupByFocData(FocObject nativeObject){
		IFocData breakdownValueObject = null;
		String groupBy = getGroupBy();
		if(nativeObject != null && groupBy != null && !groupBy.isEmpty()){
			breakdownValueObject = nativeObject.iFocData_getDataByPath(groupBy);
		}
		return breakdownValueObject;
	}
	
	public String getGroupByString(FocObject nativeObject){
		return getGroupByString(nativeObject, false);
	}
	
	public String getGroupByString(FocObject nativeObject, boolean forSorting){
		String value = null;
		FocSimpleFormulaContext formulaContext= groupBy_FormulaContext();
		if(formulaContext != null){
			value = formulaContext.compute(nativeObject) != null ? formulaContext.compute(nativeObject).toString() : null;
		}else{
			IFocData focData = getGroupByFocData(nativeObject);
			if(focData instanceof FObject && getTitleCaption() != null && !getTitleCaption().isEmpty()){
				focData = focData.iFocData_getDataByPath(getTitleCaption());
			}
			value = FPivotTitleDescriptionSet.getStringFromIFocData(focData, this, forSorting);
		}
		return value;		
	}
	
	public FocListOrderFocObject getListOrderFocObject() {
		return listOrderFocObject;
	}
	
	public FocListOrderFocObject getListOrderFocObject_CreateIfNeeded() {
		if(listOrderFocObject == null && getSortBy() != null && (getSortBy().contains(",") || getSortBy().contains("-"))){
			FocDesc focDesc = (getPivotTree() != null && getPivotTree().getNativeDataFocList() != null) ? getPivotTree().getNativeDataFocList().getFocDesc() : null;
			if(focDesc != null){
				listOrderFocObject = FocListOrderFocObject.newFocListOrder_ForExpression(focDesc, getSortBy(), false);
			}
	  }	
		return listOrderFocObject;
	}

	public void setListOrderFocObject(FocListOrderFocObject listOrderFocObject) {
		this.listOrderFocObject = listOrderFocObject;
	}

	public FPivotRowTree getPivotTree() {
		return pivotTree;
	}

	public void setPivotTree(FPivotRowTree pivotTree) {
		this.pivotTree = pivotTree;
	}
	
	public static String getPeriodTitle(int dateGroupingChoice, Date date, Date cutOffDate){
		String title = "";
		if(!FCalendar.isDateZero(cutOffDate) && FCalendar.compareDatesIgnoreYear(date, cutOffDate) >= 0){
  		SimpleDateFormat yearlyFormater = new SimpleDateFormat("yyyy");
  		title = "Rem " + yearlyFormater.format(date);
		}else{
	    switch (dateGroupingChoice){
	  	case FPivotBreakdownDesc.DATE_GROUPING_MONTHLY:
	  		SimpleDateFormat monthlyFormater = new SimpleDateFormat("MMM yy");
	  		title = monthlyFormater.format(date);
	  		break;
	  	case FPivotBreakdownDesc.DATE_GROUPING_YEARLY:
	  		SimpleDateFormat yearlyFormater = new SimpleDateFormat("yyyy");
	  		title = yearlyFormater.format(date);
	  		break;
	    }
		}
    return title;
	}
	
	public static String getPeriodTitle(int dateGroupingChoice, Date date){
		return getPeriodTitle(dateGroupingChoice, date, null);
	}
}
