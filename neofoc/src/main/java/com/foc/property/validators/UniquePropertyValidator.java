/*
 * Created on Jul 25, 2005
 */
package com.foc.property.validators;

import java.util.ArrayList;
import java.util.Iterator;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.OptionDialog;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.property.FProperty;
import com.foc.tree.FNode;
import com.foc.util.ASCII;

/**
 * @author 01Barmaja
 */
public class UniquePropertyValidator implements FPropertyValidator {

  private ArrayList<Character> specialCharactersArrayList = null;

  private final char CHAR_SPACE = ASCII.SPACE;
  private final char CHAR_DASH = ASCII.DASH;
  private final char CHAR_SLASH_RIGHT_TO_LEFT = ASCII.SLASH_RIGHT_TO_LEFT;
  private final char CHAR_SLASH_LEFT_TO_RIGHT = ASCII.SLASH_LEFT_TO_RIGHT;
  private final char CHAR_UNDER_SCORE = ASCII.UNDER_SCORE;

  private boolean validateOnlyIfDBResident = true;
  
  public void dispose() {
    if (specialCharactersArrayList != null) {
      for (int i = 0; i < specialCharactersArrayList.size(); i++) {
        specialCharactersArrayList.clear();
      }
      specialCharactersArrayList = null;
    }
  }

  public boolean validateProperty(FProperty property) {
    newSpecialCharactersList();
    FocObject focObject = (property != null ? property.getFocObject() : null);

    if (focObject != null) {
      if (focObject.getFatherSubject() instanceof FocList) {

        FocList list = (FocList) focObject.getFatherSubject();
        if(list.isDbResident() || !isValidateOnlyIfDBResident()){
	        Iterator iter = list.newSubjectIterator();	
	        int fieldID = property.getFocField().getID();
	
	        boolean found = false;
	        boolean modified = false;
	        String focObjectValueString = focObject.getPropertyString(fieldID).toUpperCase();
	
	        String focObjectValueStringModified = deleteSpecialCharactersFromString(focObjectValueString);
	
	        while (iter != null && iter.hasNext() && !found) {
	          FocObject focObjInList = (FocObject) iter.next();
	          String focObjectInListValueString = focObjInList.getPropertyString(fieldID).toUpperCase();
	          if (focObject != focObjInList) {
	            if (focObjectValueString.equals(focObjectInListValueString)) {
	              found = true;
	            } else {
	              focObjectInListValueString = deleteSpecialCharactersFromString(focObjectInListValueString);
	              if (focObjectValueStringModified.equals(focObjectInListValueString)) {
	                found = true;
	                modified = true;
	              }
	            }
	          }
	        }
	
	        if (found) {
	          if (modified) {
	            if (focObject.getPropertyString(fieldID).indexOf(FNode.NEW_ITEM) == -1) {
	            	UniqueWarningDialog optionDialog = new UniqueWarningDialog(property);
	            	Globals.popupDialog(optionDialog);
	            }
	          } else {
	            if (focObject.getPropertyString(fieldID).indexOf(FNode.NEW_ITEM) == -1) {
	    					String title = "Alert";
	    					String message = "Value '"+focObjectValueString+"' already exist.";
	    					if (ConfigInfo.isArabic()) {
	    						title = "تنبيه";
	    						message = focObjectValueString + " : "+ "موجودة حاليا, يرجى تبديل الاختيار";
	    					}
	    					
	    					OptionDialog dialog = new OptionDialog(title, message) {
	    						@Override
	    						public boolean executeOption(String optionName) {
	    							return false;
	    						}
	    					};
	    					dialog.addOption("OK", ConfigInfo.isArabic() ? "نعم" : "Ok");
	    					dialog.popup();
	            }
	            property.setString("");
	          }
	        }
        }
      }
    }
    return true;
  }

  public void newSpecialCharactersList() {
    specialCharactersArrayList = new ArrayList<Character>();
    specialCharactersArrayList.add(CHAR_DASH);
    specialCharactersArrayList.add(CHAR_SPACE);
    specialCharactersArrayList.add(CHAR_SLASH_LEFT_TO_RIGHT);
    specialCharactersArrayList.add(CHAR_SLASH_RIGHT_TO_LEFT);
    specialCharactersArrayList.add(CHAR_UNDER_SCORE);
  }

  public ArrayList<Character> getSpecialCharactersArrayList() {
    return specialCharactersArrayList;
  }

  public boolean isSpecialCharacter(char c) {
    boolean found = false;
    for (int i = 0; i < getSpecialCharactersArrayList().size(); i++) {
      char specialChar = specialCharactersArrayList.get(i);
      if (specialChar == c) {
        found = true;
      }
    }
    return found;
  }

  public String deleteSpecialCharactersFromString(String str) {
    String newStr = new String();
    if (str != null) {
      for (int i = 0; i < str.length(); i++) {
        char c = str.charAt(i);
        if (!isSpecialCharacter(c)) {
          newStr += c;
        }
      }
    }
    return newStr;
  }

  public class UniqueWarningDialog extends OptionDialog {

  	private FProperty property = null;
  	
  	public UniqueWarningDialog(FProperty property){
  		super("Similar value exist", "Value exist in other format.\n Do you want to keep it?");
  		this.property = property;
  		
  		addOption("YES", "Yes keep new item");
  		addOption("NO", "No cancel new item");
  	} 
  	
		@Override
		public boolean executeOption(String optionName) {
			if(optionName.equals("NO")){
				property.setString("");
			}
			return false;
		}
  }

	public boolean isValidateOnlyIfDBResident() {
		return validateOnlyIfDBResident;
	}

	public void setValidateOnlyIfDBResident(boolean checkOnlyIfDBResident) {
		this.validateOnlyIfDBResident = checkOnlyIfDBResident;
	}
}
