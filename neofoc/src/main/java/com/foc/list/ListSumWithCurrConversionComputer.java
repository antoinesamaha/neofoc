/*
 * Created on Jul 8, 2005
 */
package com.foc.list;

import java.util.*;

import com.foc.business.currency.Currencies;
import com.foc.business.currency.Currency;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.event.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class ListSumWithCurrConversionComputer implements FocListener{
  private FocList list = null;
  private ArrayList<SumColumn> sumColumns = null;
  private FObject aggCurrProp = null;
  private Currency aggCurr = null;
  private int aggregationMode = 0;
  
  public final static int AGG_MODE_CONVERT_TO_AGG_CURR = 0;
  public final static int AGG_MODE_FILTER_ON_AGG_CURR = 1;
    
  public ListSumWithCurrConversionComputer(FocList list, FObject aggCurrProp, FFieldPath fieldPath, FFieldPath locCurrFieldPath, FFieldPath convDateFieldPath, FProperty sumProp){
    this.list = list;
    sumColumns = new ArrayList<SumColumn>();
    SumColumn col = new SumColumn(fieldPath, locCurrFieldPath, convDateFieldPath, sumProp);
    sumColumns.add(col);
    this.aggCurrProp = aggCurrProp ; 
  }

  public ListSumWithCurrConversionComputer(FocList list, Currency aggCurr, FFieldPath fieldPath, FFieldPath locCurrFieldPath, FFieldPath convDateFieldPath, FProperty sumProp){
    this.list = list;
    sumColumns = new ArrayList<SumColumn>();
    SumColumn col = new SumColumn(fieldPath, locCurrFieldPath, convDateFieldPath, sumProp);
    sumColumns.add(col);
    this.aggCurr = aggCurr ;
  }

  public void dispose(){
    if(sumColumns != null){
      for(int i=0; i<sumColumns.size(); i++){
        SumColumn sumColumn = (SumColumn) sumColumns.get(i);
        sumColumn.dispose();
      }
      sumColumns.clear();
      sumColumns = null;
    }
    
    aggCurr = null;
    aggCurrProp = null;
    list = null;
  }
  
  public void setAggregationMode(int aggregationMode) {
    this.aggregationMode = aggregationMode;
  }

  public int getAggregationMode() {
    return aggregationMode;
  }

  public Currency getAggregationCurrency(){
    Currency zAggCurr = aggCurr;
    if(zAggCurr == null){
      zAggCurr = (Currency) aggCurrProp.getObject_CreateIfNeeded();
    }
    return zAggCurr;
  }
  
  public void addSumComputation(FFieldPath fieldPath, FFieldPath locCurrFieldPath, FFieldPath convDateFieldPath, FProperty sumProp){
    SumColumn col = new SumColumn(fieldPath, locCurrFieldPath, convDateFieldPath, sumProp);
    sumColumns.add(col);
  }
  
  public void compute(){
    for(int i=0; i<sumColumns.size(); i++){
      SumColumn sumCol = (SumColumn) sumColumns.get(i);
      sumCol.reset();
    }    
    
    Iterator iter = list.focObjectIterator();
    while(iter != null && iter.hasNext()){
      FocObject obj = (FocObject) iter.next();
      if(obj != null){
        for(int i=0; i<sumColumns.size(); i++){
          SumColumn sumCol = (SumColumn) sumColumns.get(i);
          sumCol.treatObject(obj);
        }    
      }
    }

    for(int i=0; i<sumColumns.size(); i++){
      SumColumn sumCol = (SumColumn) sumColumns.get(i);
      sumCol.sendResult();
    }    
  }
  
  public void focActionPerformed(FocEvent evt) {
    compute();
  }
  
  private class SumColumn{
    public FFieldPath fieldPath = null; 
    public FFieldPath locCurrFieldPath = null;
    public FFieldPath convDateFieldPath = null;
    public FProperty sumProp = null;
    public double sum = 0;
    
    public SumColumn(FFieldPath fieldPath, FFieldPath locCurrFieldPath, FFieldPath convDateFieldPath, FProperty sumProp){
      this.fieldPath = fieldPath; 
      this.locCurrFieldPath = locCurrFieldPath;
      this.convDateFieldPath = convDateFieldPath;
      this.sumProp = sumProp;
    }
    
    public void dispose(){
      if(fieldPath != null){
        fieldPath.dispose();
        fieldPath = null;
      }
      if(locCurrFieldPath != null){
        locCurrFieldPath.dispose();
        locCurrFieldPath = null;
      }
      if(convDateFieldPath != null){
        convDateFieldPath.dispose();
        convDateFieldPath = null;
      }      
      sumProp = null;
    }
    
    public void reset(){
      sum = 0;  
    }
    
    private double getRate(FocObject obj){
      double d = 0;
      
      Currency aggCurr = getAggregationCurrency();
      FObject pLocCurr = (FObject) locCurrFieldPath.getPropertyFromObject(obj);
      Currency locCurr = (Currency) pLocCurr.getObject_CreateIfNeeded();
      if(locCurr == null){//Just for debug
      	locCurr = (Currency) pLocCurr.getObject();
      }
      
      if(getAggregationMode() == AGG_MODE_CONVERT_TO_AGG_CURR){
        FDate pConvDate = (FDate) convDateFieldPath.getPropertyFromObject(obj);
        java.sql.Date convDate = (java.sql.Date) pConvDate.getDate();
        
        d = Currencies.getRate(convDate, locCurr, aggCurr);
      }else if(getAggregationMode() == AGG_MODE_FILTER_ON_AGG_CURR){
        if(aggCurr == locCurr){
          d=1;
        }
      }
      return d;
    }
    
    public void treatObject(FocObject obj){
      FProperty prop = fieldPath.getPropertyFromObject(obj);
      double rate = getRate(obj);
      if(prop != null){
        sum += rate * prop.getDouble();
      }
    }
    
    public void sendResult(){
      sumProp.setDouble(sum);
    }
  }
  
}
