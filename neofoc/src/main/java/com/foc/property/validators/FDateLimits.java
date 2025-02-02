/*
 * Created on Jul 25, 2005
 */
package com.foc.property.validators;

import java.sql.Date;

import javax.swing.JOptionPane;

import com.foc.Globals;
import com.foc.desc.field.FDateField;
import com.foc.property.FDate;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FDateLimits implements FPropertyValidator{
  private Date  maxDate  = null;
  private Date  minDate  = null;
  private FDate minFDate = null;
  private FDate maxFDate = null;
     
  public FDateLimits(Date minDate, Date maxDate){
    this.maxDate = maxDate;
    this.minDate = minDate;
    minFDate = new FDate(null, 10, minDate);
    maxFDate = new FDate(null, 10, maxDate);
  }
  
  public void dispose(){
    minDate = null;
    maxDate = null;
    minFDate.dispose();
    maxFDate.dispose();
    minFDate = null;
    maxFDate = null;
  }
  
  public boolean validateProperty(FProperty property){
    FDate pDate = (FDate) property;
    Date date = pDate.getDate();
       
    if(date.getTime() > 0){
      if(
          (minDate != null && date.before(minDate)) || 
          (maxDate != null && date.after(maxDate))
        ){
        String message = null;
        if(minDate != null && maxDate != null){
          message = "Date should be between "+minFDate.getString()+" and "+maxFDate.getString();
        }else if(minDate != null){
          message = "Date should be after "+minFDate.getString();
        }else if(maxDate != null){
          message = "Date should be before "+maxFDate.getString();
        }
        
        message += "\nDo you realy want to set the date to "+pDate.getString();
        
        int dialogRet = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
            message,
            "01Barmaja",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null);
        
        switch(dialogRet){
        case JOptionPane.YES_OPTION:
  
          break;
        case JOptionPane.NO_OPTION:
          pDate.setDate(new java.sql.Date(0));        
          break;
        }
       
      }
    }
    return true;
  }
}
