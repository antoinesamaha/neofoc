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
package com.foc.business.currency;

import java.sql.Date;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.dataSource.store.DataStore;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

@SuppressWarnings("serial")
public class DateLineList extends FocList {

	private boolean needReset          = false;
	private int     lastIndexRequested = -1; 
	private boolean filled             = false;
	private boolean forDisplay         = false;//The list generated for display copies from the original list and then rigths to it at the end. So it is not stored in the cash
	
	public DateLineList(){
		this(false);
	}
	
	public DateLineList(boolean forDisplay){
		super(new FocLinkSimple(new DateLineDesc()));
		setDirectImpactOnDatabase(false);
		setDirectlyEditable(false);
		
		this.forDisplay = forDisplay;
		FocListOrder order = new FocListOrder();
		order.addField(FFieldPath.newFieldPath(DateLineDesc.FLD_DATE), false);
		setListOrder(order);
		
		if(forDisplay){
			//In this case we need to:
			//1- Get the original cash list 
			//2- Fill it with the rest of the dates

			copy(getInstance(true));
			
			addDate_IfNotExist(Globals.getApp().getSystemDate());
			addDate_IfNotExist(new Date(0));

			sort();
		}
	}
	
	public void dispose(){
		DateLineDesc dateLineDesc = (DateLineDesc) getFocDesc();
		
		super.dispose();
		
		if(dateLineDesc != null){
			dateLineDesc.dispose();
			dateLineDesc = null;
		}
	}
	
	@Override
	public synchronized void add(FocObject focObj, boolean withFatherSubject) {
		super.add(focObj, withFatherSubject);
	}
	
	private DateLine addDate_IfNotExist(Date date){
		DateLine dateLine = findByDate_Exactly(date);
		if(dateLine == null){
			dateLine = addDate(date);
		}
		return dateLine;
	}
		
	private DateLine addDate(Date date){
		DateLine dateLine = (DateLine) newEmptyItem();
		dateLine.setDate(date);
		add(dateLine);
		return dateLine;
	}
	
	public boolean isNeedReset() {
		return needReset;
	}

	public void setNeedReset(boolean needReset) {
		this.needReset = needReset;
	}

	public synchronized void saveRatesToDB(){
		if(!isForDisplay()){
			FocList currRatelist = CurrencyRateDesc.getInstance().getFocList();
			currRatelist.loadIfNotLoadedFromDB();
			
			DateLineDesc dateLineDesc = (DateLineDesc) getFocDesc();
			
			for(int i=0; i<size(); i++){
				DateLine dateLine = (DateLine) getFocObject(i);
				
				Date   date    = dateLine.getDate();
				Object dateObj = dateLine.getDate_Object();
				for(int c=0; c<dateLineDesc.getCurrencyCount(); c++){
					Currency curr = dateLine.getCurrencyAt(c);
					double   rate = dateLine.getRateAt(c);
					
					CurrencyRate foundCurrRate = (CurrencyRate) currRatelist.searchByPropertiesValue(CurrencyRateDesc.FLD_CURRENCY, curr, CurrencyRateDesc.FLD_DATE, dateObj);
					if(foundCurrRate == null && rate > 0){
						foundCurrRate = (CurrencyRate) currRatelist.newEmptyItem();
						foundCurrRate.setCurrency(curr);
						foundCurrRate.setDate(date);
						currRatelist.add(foundCurrRate);
					}

					if(foundCurrRate != null){
						foundCurrRate.setRate(rate);
						foundCurrRate.validate(true);
					}
				}
			}
			
			currRatelist.validate(true);
		}
	}
			
	public synchronized void copyToListInCache(){
		if(isForDisplay()){
			DateLineList tarList = DateLineList.getInstance(true);
			for(int i=0; i<size(); i++){
				DateLine srcDateLine = (DateLine) getFocObject(i);
				DateLine tarDateLine = null; 
					
				DateLineDesc srcFocDesc = (DateLineDesc) srcDateLine.getThisFocDesc();
				for(int c=0; c<srcFocDesc.getCurrencyCount(); c++){
					double   rate     = srcDateLine.getRateAt(c);
					Currency currency = srcDateLine.getCurrencyAt(c);
					
					if(rate > 0){
						if(tarDateLine == null) tarDateLine = tarList.addDate_IfNotExist(srcDateLine.getDate());
						tarDateLine.setRate(currency, rate);
					}
					
				}
			}
		}
	}
	
	public void fillIfNeeded(){
		if(!filled){
			filled = true;
			FocList list = CurrencyRateDesc.getInstance().getFocList();
			list.loadIfNotLoadedFromDB();
			
			DateLine currentDateLine = null; 
			
			for(int i=0; i<list.size(); i++){
				CurrencyRate currRate = (CurrencyRate) list.getFocObject(i);
				Date         date     = currRate.getDate();
				
				if(currentDateLine == null || FCalendar.compareDatesRegardlessOfTime(currentDateLine.getDate(), date) != 0){
					currentDateLine = (DateLine) addDate(date);
				}
				
				Currency curr = currRate.getCurrency();
				currentDateLine.setRate(curr, currRate.getRate());
			}
		}
	}
	
	public double getRate(Date date, Currency currency){
		double rate = 1;

    Currencies currencies = Currencies.getCurrencies();
    if(currency != currencies.getBaseCurrency()){
    	DateLine dateLine = findByDate(date);
    	rate = dateLine != null ? dateLine.getRate(currency) : 0;
    }
		
		return rate;
	}
	
	public DateLine findByDate_Exactly(Date date){
		DateLine dateLine = findByDate(date);
		if(dateLine != null && FCalendar.compareDatesRegardlessOfTime(dateLine.getDate(), date) != 0){
			dateLine = null;
		}
		return dateLine;
	}
	
	public DateLine findByDate(Date date){
		fillIfNeeded();
		DateLine dateLine = null;
  	if(lastIndexRequested >= 0){
  		DateLine lastDateLine = (DateLine) getFocObject(lastIndexRequested);
  		if(lastDateLine != null){
    		if(FCalendar.compareDatesRegardlessOfTime(lastDateLine.getDate(), date) == 0){
    			dateLine = lastDateLine;
    		}
  		}
  	}
  	
  	if(dateLine == null){
  		lastIndexRequested = findIndexForDate(date, 0, size());
  		if(lastIndexRequested >= 0){
  			dateLine = (DateLine) getFocObject(lastIndexRequested);
  		}
  	}

		return dateLine;
	}

	private int findIndexForDate(Date date, int start, int end){
		int foundIdx = -1;
		
		if(start == end){
			//If I have one line in the interval, then that's it
			foundIdx = start;
		}else if(end - start == 1){
			//If I have 2 lines in the interval, then I take the closest smallest (before) one. This is why I am checking the biggest. 
			DateLine dateLine = (DateLine) getFocObject(start);
			long comp = FCalendar.compareDatesRegardlessOfTime(date, dateLine.getDate());
			foundIdx = comp >= 0 ? start : end ;
		}else{
			int idx = start + (end - start) / 2;
			DateLine dateLine = (DateLine) getFocObject(idx);
			long comp = FCalendar.compareDatesRegardlessOfTime(date, dateLine.getDate());
			if(comp == 0){
				foundIdx = idx;
			}else if(comp < 0){
				foundIdx = findIndexForDate(date, idx, end);
			}else if(comp > 0){
				foundIdx = findIndexForDate(date, start, idx);
			}
		}
		
		return foundIdx;  
	}
	
	public static DateLineList getInstance(boolean fillIfNeeded){
		DateLineList instance = (DateLineList) DataStore.getInstance().getList(DateLineDesc.TABLE_NAME);
		if(instance == null || instance.isNeedReset()){
			if(instance != null){
				instance.dispose();
			}
			instance = new DateLineList();
			DataStore.getInstance().putList(DateLineDesc.TABLE_NAME, instance);
		}
		if(instance != null && fillIfNeeded){
			instance.fillIfNeeded();
		}
		return instance;
	}

	public boolean isForDisplay() {
		return forDisplay;
	}

	public void setForDisplay(boolean forDisplay) {
		this.forDisplay = forDisplay;
	}
}

