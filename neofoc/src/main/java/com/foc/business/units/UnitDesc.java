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
package com.foc.business.units;


import com.fab.gui.xmlView.IAddClickSpecialHandler;
import com.foc.Globals;
import com.foc.desc.AutoPopulatable;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FNumField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;

public class UnitDesc extends FocDesc implements AutoPopulatable{
	
  public static final int FLD_SYMBOL              = 1;
  public static final int FLD_NAME                = 2;
  public static final int FLD_FACTOR              = 3;
  public static final int FLD_DIMENSION           = 4;

	public static final String COUNT_UNIT_UNIT      = "U";
	public static final String COUNT_UNIT_DASH      = "-";  
  public static final String TIME_UNIT_SECOND     = "Second";
  public static final String TIME_UNIT_MINUTE     = "Minute";
  public static final String TIME_UNIT_HOUR       = "Hour";
  public static final String TIME_UNIT_DAY        = "Day";
  public static final String TIME_UNIT_WEEK       = "Week";
  public static final String TIME_UNIT_MONTH      = "Month";
  public static final String TIME_UNIT_YEAR       = "Year";
  
  public static Unit dayUnit = null;//To enhance performance for the Day unit search.
  private static Unit hourUnit = null;
  
  public static final String DB_TABLE_NAME = "UNIT";
  
	public UnitDesc(){
		super(Unit.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		setGuiBrowsePanelClass(UnitGuiBrowsePanel.class);	
		
    FField focFld = addReferenceField();
    
    focFld = new FStringField("NAME", "Unit Name", FLD_NAME,  true, 30);    
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FStringField("SYMBOL", "Symbol", FLD_SYMBOL,  false, 5);    
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FNumField("FACTOR", "Factor", FLD_FACTOR,  false, 5, 5);
    focFld.setMandatory(true);
    addField(focFld);
    
    FObjectField objFld = new FObjectField("DIMENSION", "Dimension", FLD_DIMENSION, false, DimensionDesc.getInstance(), "DIMENSION_", this, DimensionDesc.FLD_UNIT_LIST);
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setSelectionList(DimensionDesc.getList(FocList.NONE));
    objFld.setMandatory(true);
    addField(objFld);
	}

	private void makeSureUnitExists(FocList unitList, String name, String symbol, double factor){
		Unit minUnit = (Unit) unitList.searchByPropertyStringValue(UnitDesc.FLD_NAME, name);
		if(minUnit == null){
			minUnit = (Unit) unitList.newEmptyItem();
			minUnit.setName(name);
			minUnit.setSymbol(symbol);
			minUnit.setFactor(factor);
			minUnit.validate(true);
		}
		((FProperty)minUnit.getFocProperty(UnitDesc.FLD_NAME)).setValueLocked(true);
		((FProperty)minUnit.getFocProperty(UnitDesc.FLD_FACTOR)).setValueLocked(true);
	}

	public void createSystemUnits(){
		FocList dimensionList = DimensionDesc.getList(FocList.LOAD_IF_NEEDED);
		Dimension timeDimension = (Dimension) dimensionList.searchByPropertyStringValue(DimensionDesc.FLD_NAME, DimensionDesc.DIMENSION_TIME);
		if(timeDimension == null){
			timeDimension = (Dimension) dimensionList.newEmptyItem();
			timeDimension.setPropertyString(DimensionDesc.FLD_NAME, DimensionDesc.DIMENSION_LABEL_TIME);
		}

		FocList unitList = timeDimension.getUnitList(FocList.LOAD_IF_NEEDED);
		makeSureUnitExists(unitList, TIME_UNIT_SECOND, "sec", 0.60);
		makeSureUnitExists(unitList, TIME_UNIT_MINUTE, "min", 1);
		makeSureUnitExists(unitList, TIME_UNIT_HOUR, "hr", 60);
		makeSureUnitExists(unitList, TIME_UNIT_DAY, "day", 24*60);
		makeSureUnitExists(unitList, TIME_UNIT_WEEK, "week", 7*24*60);
		makeSureUnitExists(unitList, TIME_UNIT_MONTH, "mo", 30*24*60);
		makeSureUnitExists(unitList, TIME_UNIT_YEAR, "year", 365.25*24*60);
	
		timeDimension.validate(true);
		dimensionList.validate(false);
	}
	
	@Override
	public void afterAdaptTableModel() {
		createSystemUnits();
	}
	
  public static Unit getDayTimeUnit(){
  	if(dayUnit == null){
  		dayUnit = UnitDesc.getUnit(DimensionDesc.DIMENSION_TIME, UnitDesc.TIME_UNIT_DAY);
  	}
    return dayUnit;
  }
  
  public static Unit getHourTimeUnit(){
  	if(hourUnit == null){
  		hourUnit = UnitDesc.getUnit(DimensionDesc.DIMENSION_TIME, UnitDesc.TIME_UNIT_HOUR); 
  	}
  	return hourUnit;
  }

  public static Unit getMonthTimeUnit(){
  	if(hourUnit == null){
  		hourUnit = UnitDesc.getUnit(DimensionDesc.DIMENSION_TIME, UnitDesc.TIME_UNIT_MONTH); 
  	}
  	return hourUnit;
  }
  
  public static Unit getUnit(String dimension, String unitName){
		Dimension timeDimension = Dimension.getDimension(dimension);
		FocList   list = null;
		FocObject unit = null;
		if(timeDimension != null){
			list = timeDimension.getUnitList(FocList.LOAD_IF_NEEDED);
			if(list != null){
				unit = list.searchByPropertyStringValue(UnitDesc.FLD_NAME, unitName);
			}
		}
		return (Unit) unit;
	}
  
	public static void adjustUnitObjectField(FObjectField objFld){
	  objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
	  objFld.setDisplayField(UnitDesc.FLD_SYMBOL);
	  objFld.setComboBoxCellEditor(UnitDesc.FLD_SYMBOL);
	  objFld.setSelectionList(UnitDesc.getList(FocList.NONE));
	  /*
	  FTableView tableView = new FTableView();
	  tableView.addColumn(new FTableColumn(UnitDesc.getInstance(), FFieldPath.newFieldPath(UnitDesc.FLD_DIMENSION, DimensionDesc.FLD_NAME), 1, "Dimension", 15, false));
	  tableView.addColumn(new FTableColumn(UnitDesc.getInstance(), FFieldPath.newFieldPath(UnitDesc.FLD_NAME), 2, "Name", 10, false));
	  tableView.addColumn(new FTableColumn(UnitDesc.getInstance(), FFieldPath.newFieldPath(UnitDesc.FLD_SYMBOL), 3, "Symbol", 5, false));
	  tableView.addColumn(new FTableColumn(UnitDesc.getInstance(), FFieldPath.newFieldPath(UnitDesc.FLD_FACTOR), 4, "Factor", 10, false));
	  objFld.setMultiLineComboBoxCellEditor(UnitDesc.FLD_SYMBOL, tableView);
	  */
	  objFld.setNullValueDisplayString("");
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder();
      order.addField(FFieldPath.newFieldPath(FLD_DIMENSION, DimensionDesc.FLD_NAME));
      order.addField(FFieldPath.newFieldPath(FLD_NAME));
      list.setListOrder(order);
    }
    return list;
  }
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static UnitDesc getInstance() {
    return (UnitDesc) getInstance(DB_TABLE_NAME, UnitDesc.class);    
  }

	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // AUTO POPULATE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public String getAutoPopulatableTitle() {
		return "Units";
	}
	
	public boolean populate() {
		Dimension dimension = pushDimension("Volume");
		dimension.pushUnit("Cubic Meters", "M3", 1);
		dimension.pushUnit("Liters", "L", 1000);
		dimension.validate(true);

		dimension = pushDimension("Weight");
		dimension.pushUnit("Tons", "Ton", 0.001);
		dimension.pushUnit("Kilograms", "KG", 1);
		dimension.pushUnit("Grams", "g", 1000);
		dimension.pushUnit("Libras","Lbs",453.59237);
		dimension.validate(true);

		dimension  = pushDimension(DimensionDesc.DIMENSION_COUNT);
		dimension.pushUnit(COUNT_UNIT_UNIT, "U", 1);
		dimension.pushUnit("Pieces", "Pcs", 1);
		dimension.pushUnit("Pail", "Pail", 1);
		dimension.pushUnit("Bag", "Bag", 1);
		dimension.pushUnit(COUNT_UNIT_DASH, COUNT_UNIT_DASH, 1);
		dimension.validate(true);

		dimension = pushDimension("Length");
		dimension.pushUnit("Meters", "LM", 1);
		dimension.pushUnit("Centimeters", "CM", 100);
		dimension.pushUnit("millimeters", "MM", 1000);
		dimension.validate(true);

		dimension   = pushDimension("Area");
		dimension.pushUnit("Square Meters", "M2", 1);
		dimension.validate(true);
		
		return false;
	}
	
	private Dimension pushDimension(String dimName){
		FocList   dimList  = DimensionDesc.getList(FocList.LOAD_IF_NEEDED);
		Dimension dim      = (Dimension) dimList.searchByPropertyStringValue(DimensionDesc.FLD_NAME, dimName);
		if(dim == null){
			dim = (Dimension) dimList.newEmptyItem();
			dim.setName(dimName);
			dimList.add(dim);
			dim.validate(true);
		}
		return dim;
	}
}
