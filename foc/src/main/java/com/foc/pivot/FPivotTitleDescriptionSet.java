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
import java.util.ArrayList;

import com.foc.business.calendar.FCalendar;
import com.foc.desc.FocObject;
import com.foc.property.FBoolean;
import com.foc.property.FDate;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.shared.dataStore.IFocData;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public class FPivotTitleDescriptionSet extends ArrayList<PivotTitleDescription>{

	public static final String TITLE_WHEN_EMPTY = "empty";
	public static final String DESCRIPTION_WHEN_EMPTY = "empty";
	public static final String DATE_PREFIX_TILL = "Till";
	
	public FPivotTitleDescriptionSet(){
	}

	public void dispose(){
		clear();
	}

	public void add(FocObject nativeObject, FPivotBreakdown currentBreakdown){
//    String    sortByDataPath      = currentBreakdown.getSortBy();
//    String    titleDataPath       = currentBreakdown.getTitleCaption();
//    String    descriptionDataPath = currentBreakdown.getDescriptionCaption();
		
//    Object groupByFocData = nativeObject.iFocData_getDataByPath(groupByDataPath);
    Object groupByFocData = currentBreakdown.getGroupByFocData(nativeObject);
    
    if(groupByFocData != null){
    	if(groupByFocData instanceof FProperty){
	    	FProperty groupByProperty = (FProperty) groupByFocData;
	    	
	      if (groupByProperty.isObjectProperty()) {
	        FocObject groupByObject = (FocObject) ((FObject) groupByProperty).getObject_CreateIfNeeded();
	        
         	PivotTitleDescription titleDescription = newTitleDescription(nativeObject, groupByObject, currentBreakdown);
         	if(titleDescription != null) add(titleDescription);
         	
         	if (groupByObject != null) {
	          if (currentBreakdown.isEntireTreeShown()) {
	            while (groupByObject.getFatherObject() != null) {
	            	groupByObject = groupByObject.getFatherObject();
	            	titleDescription = newTitleDescription(nativeObject, groupByObject, currentBreakdown);
	             	if(titleDescription != null) add(titleDescription);
	            }
	          }
	        }
	      } else if (groupByProperty instanceof FDate){
	      	
	  	    String    sortByDataPath      = currentBreakdown.getSortBy();
	  	    String    titleDataPath       = currentBreakdown.getTitleCaption();
	  	    String    descriptionDataPath = currentBreakdown.getDescriptionCaption();
	
	  			String sortBy      = getString_FromNativeOrBreakdown(nativeObject, null, sortByDataPath, currentBreakdown, true);
	  			String title       = getString_FromNativeOrBreakdown(nativeObject, null, titleDataPath);
	  			String description = getString_FromNativeOrBreakdown(nativeObject, null, descriptionDataPath);
	      	
	//  	    String    groupByDataPath     = currentBreakdown.getGroupBy();  			
	//      	String groupBy = getString_FromNativeOrBreakdown(nativeObject, null, groupByDataPath);
	  			String groupBy = currentBreakdown.getGroupByString(nativeObject);
	  			
	        FDate dateProperty = (FDate) groupByProperty;
	        Date  currentDate  = dateProperty.getDate();
	        boolean treaded = false;
	        if(currentBreakdown.getDateStart() != null){
	          Date startDate = currentBreakdown.getDateStart();
	          
	          if (!FCalendar.isDateZero(startDate)) {
            	if(FCalendar.compareDatesRegardlessOfTime(currentDate, startDate) < 0){
            		treaded = true;
	            	groupBy = DATE_PREFIX_TILL+" " + FDate.convertDateToDisplayString(startDate);
	            	description = "Grouping of all items before " + FDate.convertDateToDisplayString(startDate);
		        		title = groupBy;
	            }
	          }
	        }
	        
	        if(currentBreakdown.getDateEnd() != null){
	          Date endDate = currentBreakdown.getDateEnd();
	          
	          if (!FCalendar.isDateZero(endDate)) {
	          	if(FCalendar.compareDatesRegardlessOfTime(currentDate, endDate) > 0){
	            	treaded = true;
	              groupBy = "After " + FDate.convertDateToDisplayString(endDate);
	              description = "Grouping of all items after " + FDate.convertDateToDisplayString(endDate);
	              title = groupBy;
	            }
	          }
	        }
	
	        if(!treaded){
		        ///In this case it is neither Till nor After
		        int choice = currentBreakdown.getDateGrouping();
		        switch (choice){
		        	case FPivotBreakdownDesc.DATE_GROUPING_NONE:
		        		if(Utils.isStringEmpty(groupBy)){
		        			groupBy = groupByProperty.getString();
		        		}
		        		break;
		        	default:
				        groupBy = FPivotBreakdown.getPeriodTitle(choice, currentDate, currentBreakdown.getCutOffDate());		        
		        		title = groupBy;
		        		description = title;
		        		break;
		        }
	        }
	
	        if(sortBy == null) sortBy = groupBy;
	  			if(title  == null) title  = groupBy;
	  			if(description == null) description = "";        
	  			
	  			PivotTitleDescription titleDescription = new PivotTitleDescription(groupBy, description);
	  			titleDescription.setSortBy(sortBy);
	  			titleDescription.setTitle(title);
	  			if(titleDescription != null) add(titleDescription);
	      } else {
	       	PivotTitleDescription titleDescription = newTitleDescription(nativeObject, null, currentBreakdown);
	       	//
	       	String    sortByDataPath      = currentBreakdown.getSortBy();
	       	String sortBy      = getString_FromNativeOrBreakdown(nativeObject, null, sortByDataPath, currentBreakdown, true);
	       	String groupBy = currentBreakdown.getGroupByString(nativeObject);
	       	if(sortBy == null) sortBy = groupBy;
	       	titleDescription.setSortBy(sortBy);
	       	//
	       	if(titleDescription != null) add(titleDescription);
	      }
    	}else{
    		//In this case we did not find the Property. This means it is an empty value
    		if(!currentBreakdown.getPivotTree().shouldRemoveEmpty()){
	  			PivotTitleDescription titleDescription = new PivotTitleDescription(currentBreakdown.getTitleWhenEmpty(), currentBreakdown.getDescriptionWhenEmpty());
	  			add(titleDescription);
    		}
    	}
  	}else{
  		//In this case the breakdown value is not a property
  		PivotTitleDescription titleDescription = newTitleDescription(nativeObject, null, currentBreakdown);
     	if(titleDescription != null) add(titleDescription);
    }
	}

	private PivotTitleDescription newTitleDescription(FocObject nativeObject, FocObject objectFromGroupByProperty, FPivotBreakdown currentBreakdown){
		PivotTitleDescription titleDescription = null;
		
//    String    groupByDataPath     = currentBreakdown.getGroupBy();
//		String groupBy = getString_FromNativeOrBreakdown(nativeObject, objectFromGroupByProperty, groupByDataPath);
		String groupBy = currentBreakdown.getGroupByString(nativeObject);
		
		if(groupBy != null){
	    String    sortByDataPath      = currentBreakdown.getSortBy();
	    String    titleDataPath       = currentBreakdown.getTitleCaption();
	    String    descriptionDataPath = currentBreakdown.getDescriptionCaption();

	    currentBreakdown.getListOrderFocObject_CreateIfNeeded();
	    
			String sortBy      = getString_FromNativeOrBreakdown(nativeObject, objectFromGroupByProperty, sortByDataPath, currentBreakdown, true);
			String title       = getString_FromNativeOrBreakdown(nativeObject, objectFromGroupByProperty, titleDataPath);
			String description = getString_FromNativeOrBreakdown(nativeObject, objectFromGroupByProperty, descriptionDataPath);
			
			if(sortBy == null) sortBy = groupBy;
			if(title  == null) title  = groupBy;
			if(description == null) description = "";
			
			if(title == null || title.isEmpty() || (title.trim().equals("-empty-"))){
				title = currentBreakdown.getTitleWhenEmpty();
			}

			if(description == null || description.isEmpty() || (description.trim().equals("-empty-"))){
				description = currentBreakdown.getDescriptionWhenEmpty();
			}
			
			titleDescription = new PivotTitleDescription(groupBy, description);
			titleDescription.setSortBy(sortBy);
			titleDescription.setTitle(title);
		}
		return titleDescription; 
	}

	private String getString_FromNativeOrBreakdown(FocObject nativeObject, FocObject groupByObject, String dataPath){
		return getString_FromNativeOrBreakdown(nativeObject, groupByObject, dataPath, null, false);
	}
	
	private String getString_FromNativeOrBreakdown(FocObject nativeObject, FocObject groupByObject, String dataPath, FPivotBreakdown breakdown, boolean forSorting){
		String groupBy = null;
		if(!Utils.isStringEmpty(dataPath)){
			if(dataPath.startsWith(".")){
				dataPath = dataPath.substring(1);
				groupBy = getStringFromCaptionProperty(dataPath, groupByObject, breakdown, forSorting);
			}else{
				groupBy = getStringFromCaptionProperty(dataPath, nativeObject, breakdown, forSorting);
			}
		}
		return groupBy;
	}
	
  private String getStringFromCaptionProperty(String captionProperty, FocObject breakdownObject, FPivotBreakdown breakdown, boolean forSorting) {
    String breakdownString = "-empty-";
    if (breakdownObject != null) {
      if (captionProperty != null && !captionProperty.isEmpty()) {
      	IFocData prop = breakdownObject.iFocData_getDataByPath(captionProperty);
      	breakdownString = getStringFromIFocData(prop, breakdown, forSorting);
      } else {
        breakdownString = breakdownObject.toString();
      }
    }
    return breakdownString;
  }
  
  public static String getStringFromIFocData(IFocData prop, FPivotBreakdown breakdown, boolean forSorting){
    String breakdownString = "-empty-";
    if(prop != null){
	    if(forSorting && prop instanceof FDate){
	    	SimpleDateFormat format = null;
	    	int dateGroupBy = breakdown.getDateGrouping();
        switch (dateGroupBy){
        	case FPivotBreakdownDesc.DATE_GROUPING_MONTHLY:
        		format = FDate.getDateFormat_ForSortingMonthly();
        		break;
        	case FPivotBreakdownDesc.DATE_GROUPING_YEARLY:
        		format = FDate.getDateFormat_ForSortingYearly();
        		break;
      		default: format = FDate.getDateFormat_ForSorting();
        }
	    	breakdownString = format.format(((FDate)prop).getDate());
    		//If the Month of that Date is > Month in the Cut Of Date then we add suffix to the breakdownString
//	    	if(((FDate)prop).getDate().after(breakdown.get)){
//	    		breakdownString = breakdownString.concat("OUT_OF_DATE");
//	    	}
	    }else if(prop instanceof FProperty){
	    	breakdownString = ((FProperty)prop).getString();
	    	if(prop != null && prop instanceof FBoolean){
	    		String booleanStringValue = "false";
	    		if(breakdownString != null && breakdownString.trim().equals("1")){
	    			booleanStringValue = "true";
	    		}
	    		breakdownString = booleanStringValue;
	    	}
	    }
    }
    return breakdownString;
  }
}
