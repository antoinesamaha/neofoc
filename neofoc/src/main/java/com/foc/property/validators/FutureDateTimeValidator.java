/*
 * Created on Jul 25, 2005
 */
package com.foc.property.validators;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.OptionDialog;
import com.foc.business.calendar.FCalendar;
import com.foc.desc.FocObject;
import com.foc.property.FProperty;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class FutureDateTimeValidator implements FPropertyValidator {
	
	private String dateFieldName = null;
	private String timeFieldName = null;
	private String customMessage = null;
	
	public FutureDateTimeValidator(String dateFieldName, String timeFieldName) {
		this(dateFieldName, timeFieldName, null);
	}
	
	public FutureDateTimeValidator(String dateFieldName, String timeFieldName, String customMessage) {
		this.dateFieldName = dateFieldName;
		this.timeFieldName = timeFieldName;
		this.customMessage = customMessage;
	}
	
	@Override
	public void dispose() {
	}

	private static java.util.Date getFullDate(Date date, Time time) {
		Calendar dateCalendar = Calendar.getInstance();
		dateCalendar.setTime(date);
		if (time != null) {
			Calendar timeCalendar = Calendar.getInstance();
			timeCalendar.setTime(time);
			dateCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
			dateCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
			dateCalendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		} else {
			dateCalendar.set(Calendar.HOUR_OF_DAY, 0);
			dateCalendar.set(Calendar.MINUTE, 0);
			dateCalendar.set(Calendar.SECOND, 0);
		}
		return dateCalendar.getTime();
	}
	
	@Override
	public boolean validateProperty(FProperty property) {
		boolean valid = true;
		if (	  property != null 
				&& !property.isLastModifiedBySetSQLString()
				&&  property.getFocObject() != null) {
			FocObject focObject = property.getFocObject();
			Date currentDate = Globals.getApp().getSystemDate();
			Time currentTime = new Time(Globals.getApp().getSystemDate().getTime());
		
			Date date = focObject.getPropertyDate(dateFieldName);
			Time time = focObject.getPropertyTime(timeFieldName);

			java.util.Date fullStartDate = getFullDate(date, time);
			java.util.Date fullEndDate = getFullDate(currentDate, currentTime);
			if (!FCalendar.isDateZero(fullStartDate) && !FCalendar.isDateZero(fullEndDate) && fullStartDate.getTime() > fullEndDate.getTime()) {
				focObject.setPropertyDate(dateFieldName, new java.sql.Date(currentDate.getTime()));
				focObject.setPropertyTime(timeFieldName, currentTime);
				
				if(property.isManualyEdited()) {
					String message = "Date/Time cannot be in the future";
					String title   = "Date/Time Error";
					if(ConfigInfo.isArabic()) {
						message = "لا يمكن ادخال التارخ والوقت في المستقبل";
						title   = "خطء";
					}
					if(!Utils.isStringEmpty(customMessage)) {
						message = customMessage;
					}
					OptionDialog dialog = new OptionDialog(title, message) {
						@Override
						public boolean executeOption(String optionName) {
							return false;
						}
					};
					dialog.addOption("YES", ConfigInfo.isArabic() ? "نعم" : "Ok");
					dialog.popup();					
				}
				
				return false;
			}
		}
		
		return valid;
	}
	
};