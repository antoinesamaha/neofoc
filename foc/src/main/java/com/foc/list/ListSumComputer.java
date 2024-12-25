/*
 * Created on Jul 8, 2005
 */
package com.foc.list;

import java.util.*;

import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.event.*;
import com.foc.property.*;
import com.foc.util.FocMath;

/**
 * @author 01Barmaja
 */
public class ListSumComputer implements FocListener{
  private FocList              list       = null;
  private ArrayList<SumColumn> sumColumns = null;
  private boolean              ciel       = false;
    
  public ListSumComputer(FocList list, FFieldPath fieldPath, FProperty sumProp, String roundingPrecision, boolean ciel){
    this.list = list;
    setCiel(ciel);
    sumColumns = new ArrayList<SumColumn>();
    SumColumn col = new SumColumn(fieldPath, sumProp);
    col.setRoundingPrecision(roundingPrecision);
    sumColumns.add(col);
  }    

  public ListSumComputer(FocList list, FFieldPath fieldPath, FProperty sumProp){
    this(list, fieldPath, sumProp, null, false);
  }    

  public void dispose(){
    for(int i=0; i<sumColumns.size(); i++){
      SumColumn sumColumn = (SumColumn) sumColumns.get(i);
      sumColumn.dispose();
    }
    sumColumns.clear();
    sumColumns = null;
    
    list = null;
  }
    
  public void addSumComputation(FFieldPath fieldPath, FProperty sumProp){
    SumColumn col = new SumColumn(fieldPath, sumProp);
    sumColumns.add(col);
  }
  
  public ArrayList getSumColumns() {
    return sumColumns;
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
  
  public class SumColumn{
    public FFieldPath fieldPath         = null; 
    public FProperty  sumProp           = null;
    public String     roundingPrecision = null;
    public double     sum               = 0;
    
    public SumColumn(FFieldPath fieldPath, FProperty sumProp){
      this.fieldPath = fieldPath; 
      this.sumProp = sumProp;
    }
    
    public void dispose(){
      if(fieldPath != null){
        fieldPath.dispose();
        fieldPath = null;       
      }
      sumProp = null;      
    }
    
    public void reset(){
      sum = 0;
    }
    
    public void treatObject(FocObject obj){
      FProperty prop = fieldPath.getPropertyFromObject(obj);
      if(prop != null){
        sum += prop.getDouble();
      }
    }
    
    public void sendResult(){
    	if(roundingPrecision != null){
    		if(isCiel()){
    			sum = FocMath.ceil(sum, roundingPrecision);
    		}else{
    			sum = FocMath.round(sum, roundingPrecision);
    		}
    	}
      sumProp.setDouble(sum);
    }

		public String getRoundingPrecision() {
			return roundingPrecision;
		}

		public void setRoundingPrecision(String roundingPrecision) {
			this.roundingPrecision = roundingPrecision;
		}
  }
  
  public FocList getList() {
    return list;
  }

	public boolean isCiel() {
		return ciel;
	}

	public void setCiel(boolean ciel) {
		this.ciel = ciel;
	}
}
