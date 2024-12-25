/*
 * Created on 01-Feb-2005
 */
package com.foc.depricatedUnit;

import java.util.*;

import com.foc.desc.field.FMultipleChoiceField;

/**
 * @author 01Barmaja
 */
public class UnitFactory {
  HashMap<Integer, Unit> unitList = null;

  public UnitFactory() {
    unitList = new HashMap<Integer, Unit>();
    this.addUnit(new SquareMeterUnit());
    this.addUnit(new NumberUnit());
    this.addUnit(new LinearMeterUnit());
    this.addUnit(new CubicMeterUnit());
    this.addUnit(new HourUnit());
    this.addUnit(new Kilogram());
    this.addUnit(new Karat());
    this.addUnit(new Gram());    
    this.addUnit(new Ton());
  }

  public void addUnit(Unit unit) {
    if (unit != null && unitList != null) {
      unitList.put(Integer.valueOf(unit.getID()), unit);
    }
  }

  public Unit getUnit(int unitId) {
    Unit unit = null;
    if (unitList != null) {
      unit = (Unit) unitList.get(Integer.valueOf(unitId));
    }
    return unit;
  }

  public Iterator newUnitsIterator() {
    Iterator iter = null;
    if (unitList != null) {
      Collection coll = unitList.values();
      if (coll != null) {
        iter = coll.iterator();
      }
    }
    return iter;
  }
  
  public void addUnitToMuiltipleChoice(FMultipleChoiceField multiFld, int unit){
    Unit unitObj = getUnit(unit);
    if (unitObj != null) {
      multiFld.addChoice(unitObj.getID(), unitObj.getName());
    }
  }
  
  public void addAllUnitsToMuiltipleChoice(FMultipleChoiceField multiFld){
    Iterator iter = newUnitsIterator();
    while(iter != null && iter.hasNext()){
      Unit unitObj = (Unit) iter.next();
      if (unitObj != null) {
        multiFld.addChoice(unitObj.getID(), unitObj.getName());
      }
    }
  }
}