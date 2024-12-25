/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import java.util.*;

import javax.swing.ImageIcon;

import com.fab.model.table.FieldDefinition;
import com.foc.business.multilanguage.LanguageKey;
import com.foc.desc.*;
import com.foc.gui.*;
import com.foc.gui.table.cellControler.*;
import com.foc.list.*;
import com.foc.list.filter.FilterCondition;
import com.foc.list.filter.MultipleChoiceCondition;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FMultipleChoiceField extends FIntField {
  private HashMap list              = null;

  private FocList focSourceList     = null;
  private int     idField           = -99;
  private int     labelField        = -99;
  private boolean sortItems         = true;
  private int     guiType           = GUI_TYPE_COMBO_BOX    ;

  private int     allignement         = RADIO_BUTTONS_VERTICAL;
  private int     returnToLineIndex   = 1000;
  private boolean allowClearSelection = false;
  private int     clearSelectionValue = 0;
  
  private ArrayList<FMultipleChoiceItemInterface> choicesCollection = null;
  
  public static final int GUI_TYPE_COMBO_BOX   = 0;
  public static final int GUI_TYPE_RADIO_GROUP = 1;
  public static final int GUI_TYPE_CHECK_GROUP = 2;

  public static final int RADIO_BUTTONS_VERTICAL   = FGRadioButtonGroup.VERTICAL;
  public static final int RADIO_BUTTONS_HORIZONTAL = FGRadioButtonGroup.HORIZONTAL;
  
  public FMultipleChoiceField(String name, String title, int id, boolean key, int size) {
    super(name, title, id, key, size);
    list = new HashMap();
  }
  
  public void dispose(){
  	super.dispose();
  	if(choicesCollection != null){
	  	choicesCollection.clear();
	  	choicesCollection = null;
  	}
  }
  
  public void clearChoicesList(){
  	if(choicesCollection != null){
  		choicesCollection.clear();
  	}
  }
  
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE;
  }

  //Vaadin implementation
  public Collection<FMultipleChoiceItemInterface> getChoicesCollection(){
  	if(choicesCollection == null){
	  	choicesCollection = new ArrayList<FMultipleChoiceItemInterface>();
	    
	    Iterator<FMultipleChoiceItemInterface> iter = getChoiceIterator();
	    while (iter != null && iter.hasNext()) {
	      FMultipleChoiceItemInterface item = (FMultipleChoiceItemInterface) iter.next();
	      if (item != null) {
	        choicesCollection.add(item);
	      }
	    }
	    
	    Comparator<FMultipleChoiceItemInterface> comparator = null;
	        
	    if(isSortItems()){
	      comparator = new Comparator<FMultipleChoiceItemInterface>(){
	        public int compare(FMultipleChoiceItemInterface item0, FMultipleChoiceItemInterface item1) {
	          return item0.getTitle().compareTo(item1.getTitle()); 
	        }
	      };
	    }else{
	      comparator = new Comparator<FMultipleChoiceItemInterface>(){
	        public int compare(FMultipleChoiceItemInterface item0, FMultipleChoiceItemInterface item1) {
	          return item0.getId() - item1.getId(); 
	        }
	      };
	    }
	    Collections.sort(choicesCollection, comparator);
  	}
    
    return choicesCollection;
//    return list != null ? list.values() : null;
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FMultipleChoice(masterObj, getID(), defaultValue != null ? ((Integer)defaultValue).intValue() : 0);
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    return newProperty_ToImplement(masterObj, null);
  }

  public FMultipleChoiceItem addChoice(int id, String title, String iconPath) {
    FMultipleChoiceItem item = new FMultipleChoiceItem(id, title, iconPath);
    list.put(Integer.valueOf(id), item);
    return item;
  }
  
  public FMultipleChoiceItem addChoice(int id, ImageIcon imageIcon, String title) {
    FMultipleChoiceItem item = new FMultipleChoiceItem(id, imageIcon, title);
    list.put(Integer.valueOf(id), item);
    return item;
  }
  
  public FMultipleChoiceItem addChoice(int id, String title) {
    FMultipleChoiceItem item = new FMultipleChoiceItem(id, title);
    list.put(Integer.valueOf(id), item);
    return item;
  }

  public void addChoice(int id, LanguageKey langKey) {
    FMultipleChoiceItem item = new FMultipleChoiceItem(id, langKey);
    list.put(Integer.valueOf(id), item);
  }

  public FMultipleChoiceItem getChoiceItemForKey(int id) {
    FMultipleChoiceItem item = null;
    if (list != null) {
      item = (FMultipleChoiceItem) list.get(Integer.valueOf(id));
    }
    return item;
  }

  public Iterator getChoiceIterator() {
    Iterator iter = null;
    if (list != null) {
      Collection coll = list.values();
      if (coll != null) {
        //Collections.sort();
        iter = coll.iterator();
      }
    }
    return iter;
  }

  public void refreshList() {
    if (focSourceList != null) {
      focSourceList.reloadFromDB();
      list.clear();
      for (int i = 0; i < focSourceList.size(); i++) {
        FocObject obj = focSourceList.getFocObject(i);
        if (obj != null) {
          FProperty idProp = obj.getFocProperty(idField);
          FProperty labelProp = obj.getFocProperty(labelField);

          if (idProp != null && labelProp != null) {
            this.addChoice(idProp.getInteger(), labelProp.getString());
          }
        }
      }
    }
  }

  public void setSourceList(FocList focSourceList, int idField, int labelField) {
    this.focSourceList = focSourceList;
    this.idField = idField;
    this.labelField = labelField;
  }
  
  public Component getGuiComponent(FProperty prop){
  	Iterator choices = null;
  	if(prop != null && prop instanceof FMultipleChoice){
  		choices = ((FMultipleChoice)prop).getChoiceIterator();
  	}
    if(choices == null){
    	choices = this.getChoiceIterator();
    }

    Component comp = null;
    if(getGuiType() == GUI_TYPE_COMBO_BOX){
	    FGComboBox comboBox = new FGComboBox(choices, isSortItems());
	    comp = comboBox;
	    if(prop != null) comboBox.setProperty(prop);
    }else{
	    FGRadioButtonGroup radioGroup = new FGRadioButtonGroup(choices, guiType == GUI_TYPE_CHECK_GROUP, allignement, returnToLineIndex, allowClearSelection, clearSelectionValue);
	    comp = radioGroup;
	    if(prop != null) radioGroup.setProperty(prop);
    }
    return comp;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
  	Iterator choices = null;
  	if(prop != null && prop instanceof FMultipleChoice){
  		choices = ((FMultipleChoice)prop).getChoiceIterator();
  	}
  	if(choices == null){
  		choices = this.getChoiceIterator();
  	}
    return new ComboBoxCellControler(choices, isSortItems());
  }
  
  public boolean isSortItems() {
    return sortItems;
  }
  
  public void setSortItems(boolean sortItems) {
    this.sortItems = sortItems;
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		MultipleChoiceCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new MultipleChoiceCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}

	public int getGuiType(){
		return guiType;
	}

	public void setGuiType(int guiType){
		this.guiType = guiType;
	}

	public void setGuiTypeCheckButtons(int allignement, int returnToLineIndex, boolean allowClearSelection, int clearSelectionValue){
		setGuiType(GUI_TYPE_CHECK_GROUP);
		this.returnToLineIndex   = returnToLineIndex  ;
		this.allignement         = allignement        ;
		this.allowClearSelection = allowClearSelection;
		this.clearSelectionValue = clearSelectionValue;
	}
	
	public void setGuiTypeRadioButtons(int allignement, int returnToLineIndex, boolean allowClearSelection, int clearSelectionValue){
		setGuiType(GUI_TYPE_RADIO_GROUP);
		this.returnToLineIndex   = returnToLineIndex  ;
		this.allignement         = allignement        ;
		this.allowClearSelection = allowClearSelection;
		this.clearSelectionValue = clearSelectionValue;
	}
	
	public void setGuiTypeRadioButtons(int allignement, int returnToLineIndex){
		setGuiTypeRadioButtons(allignement, returnToLineIndex, false, 0);
	}

	@Override
	public Class vaadin_getClass() {
		return FMultipleChoiceItem.class;
	}
}
