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
package com.foc.gui.table.cellControler.renderer.gantChartActivityRenderer;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FColorField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.gui.FColorProvider;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class GanttStyleDesc extends FocDesc {
  
  public static final int FLD_NAME                    = FField.FLD_NAME;
  public static final int FLD_BAR_POSITIONS           = 1;//Single Centered, Up_Down, Outer_Inner
  public static final int FLD_FIRST_BAR               = 2;//MINIMUM, MAXIMUM, FORECAST, ACTUAL, SCENARIO
  public static final int FLD_FIRST_BAR_FILLER        = 3;//MINIMUM, MAXIMUM, FORECAST, ACTUAL, SCENARIO
  public static final int FLD_SECOND_BAR              = 4;//MINIMUM, MAXIMUM, FORECAST, ACTUAL, SCENARIO
  public static final int FLD_SECOND_BAR_FILLER       = 5;//MINIMUM, MAXIMUM, FORECAST, ACTUAL, SCENARIO

  public static final int FLD_FIRST_BAR_COLOR         = 6;//First Bar Color
  public static final int FLD_FIRST_BAR_FILLER_COLOR  = 7;//First Bar Fill Color
  public static final int FLD_SECOND_BAR_COLOR        = 8;//Second Bar Color
  public static final int FLD_SECOND_BAR_FILLER_COLOR = 9;//Second Bar Fill Color

  public static final int FLD_RELATIONSHIP_MODE              = 10;
  public static final int FLD_RELATIONSHIP_COLOR             = 11;
  
  public static final int ARROW_MODE_NONE             = 0;
  public static final int ARROW_MODE_RANDOM_COLORS    = 1;
  public static final int ARROW_MODE_SELECTED_COLOR   = 2;
  
  public static final int BAR_POSITION_SINGLE_CENTERED = 0;
  public static final int BAR_POSITION_UP_DOWN         = 1;
  public static final int BAR_POSITION_OUTER_INNER     = 2;

  public static final int BAR_TYPE_NONE     = 0;
  public static final int BAR_TYPE_MINIMUM  = 1;
  public static final int BAR_TYPE_MAXIMUM  = 2;
  public static final int BAR_TYPE_FORECAST = 3;
  public static final int BAR_TYPE_ACTUAL   = 4;
  public static final int BAR_TYPE_SCENARIO = 5;
  
  public static final String DB_TABLE_NAME = "GUI_GANTT_STYLE";
  
  public GanttStyleDesc() {
    super(GanttStyle.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    setGuiBrowsePanelClass(GanttStyleGuiBrowsePanel.class);
    setGuiDetailsPanelClass(GanttStyleGuiDetailsPanel.class);
    FField fld = addReferenceField();

    fld = addNameField();
    fld.setMandatory(true);
  
    FMultipleChoiceField mFld = new FMultipleChoiceField("BAR_POSITIONS", "Bar positions", FLD_BAR_POSITIONS, false, 2);
    mFld.addChoice(BAR_POSITION_SINGLE_CENTERED, "Single centered bar");
    mFld.addChoice(BAR_POSITION_UP_DOWN        , "Up Down Bars");
    mFld.addChoice(BAR_POSITION_OUTER_INNER    , "Outer Inner Bars");
    addField(mFld);
    
    fld = newBarTypeMultipleChoiceField("FIRST_BAR_TYPE", "First Bar Type", FLD_FIRST_BAR);
    addField(fld);

    fld = newBarTypeMultipleChoiceField("FIRST_BAR_FILLER_TYPE", "First Bar Filler Type", FLD_FIRST_BAR_FILLER);
    addField(fld);

    fld = newBarTypeMultipleChoiceField("SECOND_BAR_TYPE", "Second Bar Type", FLD_SECOND_BAR);
    addField(fld);

    fld = newBarTypeMultipleChoiceField("SECOND_BAR_FILLER_TYPE", "Second Bar Filler Type", FLD_SECOND_BAR_FILLER);
    addField(fld);
    
    FColorField colorField = new FColorField("FIRST_BAR_COLOR", "First Bar Color", FLD_FIRST_BAR_COLOR, false, FColorProvider.getColorAt(0));
    addField(colorField);
    
    colorField = new FColorField("FIRST_BAR_FILLER_COLOR", "First Bar Color", FLD_FIRST_BAR_FILLER_COLOR, false, FColorProvider.getColorAt(1));
    addField(colorField);

    colorField = new FColorField("SECOND_BAR_COLOR", "Second Bar Color", FLD_SECOND_BAR_COLOR, false, FColorProvider.getColorAt(2));
    addField(colorField);

    colorField = new FColorField("SECOND_BAR_FILLER_COLOR", "Second Bar Filler Color", FLD_SECOND_BAR_FILLER_COLOR, false, FColorProvider.getColorAt(3));
    addField(colorField);
    
    mFld = new FMultipleChoiceField("ARROW_MODE", "Relationship mode", FLD_RELATIONSHIP_MODE, false, 2);
    mFld.addChoice(ARROW_MODE_NONE          , "None");
    mFld.addChoice(ARROW_MODE_RANDOM_COLORS , "Random colors");
    mFld.addChoice(ARROW_MODE_SELECTED_COLOR, "Selected Color");
    addField(mFld);
    
    colorField = new FColorField("ARROW_COLOR", "Arrow Color", FLD_RELATIONSHIP_COLOR, false, FColorProvider.getColorAt(0));
    addField(colorField);
  }
  
  private FMultipleChoiceField newBarTypeMultipleChoiceField(String fieldName, String fieldTitle, int fieldID){
  	FMultipleChoiceField fld = new FMultipleChoiceField(fieldName, fieldTitle, fieldID, false, 2);
  	fld.addChoice(BAR_TYPE_NONE    ,"-"       ); 
  	fld.addChoice(BAR_TYPE_MINIMUM ,"Minimum" ); 
  	fld.addChoice(BAR_TYPE_MAXIMUM ,"Maximum" );
  	fld.addChoice(BAR_TYPE_FORECAST,"Forecast");
  	fld.addChoice(BAR_TYPE_ACTUAL  ,"Actual"  );
  	fld.addChoice(BAR_TYPE_SCENARIO,"Scenario");
  	return fld;
  }
  
  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_NAME);
      list.setListOrder(order);
    }
    return list;
  }
  
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, GanttStyleDesc.class);    
  }
}
