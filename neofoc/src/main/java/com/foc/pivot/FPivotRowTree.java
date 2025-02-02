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
package com.foc.pivot;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListOrderFocObject;
import com.foc.property.FDate;
import com.foc.property.FDouble;
import com.foc.property.FInt;
import com.foc.property.FProperty;
import com.foc.property.FString;
import com.foc.shared.dataStore.IFocData;
import com.foc.tree.TreeScanner;
import com.foc.tree.objectTree.FObjectTree;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public class FPivotRowTree extends FObjectTree<FPivotRowNode, FPivotRow> {

  private boolean shouldRemoveEmpty = true;
  private boolean shouldCollapse = false;
  private boolean shouldRemoveOnlyChildren = false;
  
  //Native objects to be analysed
	private FocList nativeDataFocList = null;  

	//Contains the Configuration of the Pivot
  private FPivotView pivotView = null;
  
	//Object of type native created especially to accompany the father 
	//nodes so that columns of the native kind can be manipulated for 
	//display and computation 
  private FocList fathersRawFocList = null;

  //The FocDesc of the FPivotRow FocObjects.
  //Contains basically the Node title, the Native, and breakdown fields Values... 
  private FPivotRowDesc      rowFocDesc = null;
  
  //FocList of FPivotRow : FocObjects that will wrap the Native ones
  private FPivotRowList pivotList = null;
  //FObjectTree of FPivotRow : Built from pivotList
//  private FPivotRowTree pivotTree = null;
  
  private boolean filterByCompany = true;

  private IPivotTableHandler handler    = null;
	
  public FPivotRowTree(FocList nativeDataFocList) {
    super(true);
    setNativeDataFocList(nativeDataFocList);
    
//    setDisplayFieldId(FPivotRowDesc.FLD_PVT_ROW_TITLE);
    init();
  }

  public void init(){
    setAutomaticlyListenToListEvents(false);
    setFatherNodeId(FField.FLD_FATHER_NODE_FIELD_ID);
    
    setSortable(new Comparator<FPivotRowNode>(){
			@Override
			public int compare(FPivotRowNode o1, FPivotRowNode o2) {
				FPivotRow r1 = o1.getObject();
				FPivotRow r2 = o2.getObject();
				
				int comp = 0;
				
				FPivotBreakdown breakdown1 = r1.getPivotBreakdownStart();
				FPivotBreakdown breakdown2 = r2.getPivotBreakdownStart();
				if(breakdown1.equalsRef(breakdown2) && breakdown1.getListOrderFocObject() != null){
					FocListOrderFocObject listOrder = breakdown1.getListOrderFocObject();
					comp = listOrder.compare(r1.getPivotRowObject(), r2.getPivotRowObject());
				}else{
					comp = r1.getSortByValue().compareTo(r2.getSortByValue());
				}
				
				return comp;
			}
    });  	
  }
  
  public void dispose() {
    super.dispose();
    dispose_Contents();
    if (pivotView != null) {
      pivotView.dispose();
      pivotView = null;
    }    
  }
  
  public void dispose_Contents() {
    if (rowFocDesc != null) {
      rowFocDesc.dispose();
      rowFocDesc = null;
    }

    if (fathersRawFocList != null) {
      fathersRawFocList.dispose();
      fathersRawFocList = null;
    }
    nativeDataFocList = null;
    if (pivotList != null) {
      pivotList.dispose();
      pivotList = null;
    }
//    super.dispose();
//    if (pivotTree != null) {
//      pivotTree.dispose();
//      pivotTree = null;
//    }
  }

  public FocList getNativeDataFocList() {
    return nativeDataFocList;
  }
  
  public boolean isFilterByCompany(){
  	return filterByCompany;
  }

  public void setFilterByCompany(boolean filterByCompany){
  	this.filterByCompany = filterByCompany;
  }

  public FPivotView getPivotView() {
    if (pivotView == null) {
      pivotView = new FPivotView(new FocConstructor(FPivotViewDesc.getInstance(), null));
    }
    return pivotView;
  }

  public void setPivotView(FPivotView view) {
    pivotView = view;
  }

  public FPivotBreakdown addBreakdown(String name, String dataPath, String showTree, String captionProperty, String descriptionProperty, String dateStart, String dateEnd, String hideWhenAlone, String titleWhenEmpty, String descriptionWhenEmpty) {
    return getPivotView().addBreakdown(this, name, dataPath, showTree, captionProperty, descriptionProperty, dateStart, dateEnd, hideWhenAlone, titleWhenEmpty, descriptionWhenEmpty);
  }

  public void addValue(String title, String dataPath) {
    getPivotView().addValue(title, dataPath);
  }

  public FPivotRowDesc getRowFocDesc() {
    if (rowFocDesc == null && nativeDataFocList != null) {
      rowFocDesc = new FPivotRowDesc(nativeDataFocList.getFocDesc());
    }
    return rowFocDesc;
  }

  public IPivotTableHandler getHandler() {
		return handler;
	}

	public void setHandler(IPivotTableHandler handler) {
		this.handler = handler;
	}
  
  public FPivotRowList getPivotRowList() {
    if (pivotList == null) {
      pivotList = new FPivotRowList(getRowFocDesc());
    }
    return pivotList;
  }

  public FPivotRowTree getTree() {
    return getTree(false);
  }

  private boolean alreadyGrown = false;
  public FPivotRowTree getTree(boolean grow) {
  	if(grow && !alreadyGrown){
  		alreadyGrown = true;
  		growPivotRowTree();
  	}
    return this;
  }
  
  public FPivotRowTree growPivotRowTree() {
    populatePivotRowList();

    FocList pivotFocList = getPivotRowList();
    if (pivotFocList != null) {
      growTreeFromFocList(pivotFocList);
      setSortable(true);
      sort();
    }

//    debug(null);    
//    FPivotRowTree pivotTree = getTree();

    return this;
  }

  public class Comparator_ForNativeObjectListAccordingToBreakdownFields implements Comparator<FocListElement> {
    @Override
    public int compare(FocListElement e1, FocListElement e2) {
      FocObject o1 = e1.getFocObject();
      FocObject o2 = e2.getFocObject();
      int ret = 0;
      FocList breakdownList = getPivotView().getBreakdownList();

      for (int i = 0; i < breakdownList.size() && ret == 0; i++) {
        FPivotBreakdown currentBreakdown = (FPivotBreakdown) breakdownList.getFocObject(i);
//        FProperty o1CurrentProperty = (FProperty) o1.iFocData_getDataByPath(currentBreakdown.getGroupBy());
//        FProperty o2CurrentProperty = (FProperty) o2.iFocData_getDataByPath(currentBreakdown.getGroupBy());
        /*
        FProperty o1CurrentProperty = (FProperty) currentBreakdown.getGroupByFocData(o1);
        FProperty o2CurrentProperty = (FProperty) currentBreakdown.getGroupByFocData(o2);

        if (o1CurrentProperty == null && o2CurrentProperty == null) {
          ret = 0;
        } else if (o1CurrentProperty != null && o2CurrentProperty != null) {
          if (currentBreakdown.getTitleCaption() != null && !currentBreakdown.getTitleCaption().isEmpty()) {
            Object capProp1 = o1CurrentProperty.iFocData_getDataByPath(currentBreakdown.getTitleCaption());
            Object capProp2 = o2CurrentProperty.iFocData_getDataByPath(currentBreakdown.getTitleCaption());
            if (capProp1 != null && capProp2 != null && capProp1 instanceof FProperty && capProp2 instanceof FProperty) {
            	//We get here only if the o1CurrenProperty is a FObject property. So it will become the deeper title property in this section
            	//Otherwize it stays the same FString, FDate,... and the comparison is done on it directly
              o1CurrentProperty = (FProperty) capProp1;
              o2CurrentProperty = (FProperty) capProp2;
            }
          }
          ret = o1CurrentProperty.compareTo(o2CurrentProperty);
        } else {
          // In this case only one of the 2 is null
          ret = o2CurrentProperty == null ? 1 : -1;
        }
        */
        
        String o1CurrentString = currentBreakdown.getGroupByString(o1, true);
        String o2CurrentString = currentBreakdown.getGroupByString(o2, true);

        if (o1CurrentString == null && o2CurrentString == null) {
          ret = 0;
        } else if (o1CurrentString != null && o2CurrentString != null) {
        	ret = o1CurrentString.compareTo(o2CurrentString);
        } else {
          // In this case only one of the 2 is null
          ret = o2CurrentString == null ? 1 : -1;
        }
        
      }

      return ret;
    }

  }

  /**
   * Method that builds the tree structure of the FPivotRowList. This method
   * adds father nodes according to groupings by breakdown.
   */
  public void populatePivotRowList() {
  	//Get the Native Objects list and sort it according to the Breakdown fields 
    FocList nativeList = getNativeDataFocList();
    nativeList.setListOrder(null);
    
    Comparator comparatorBackup = nativeList.getOrderComparator(); 
    nativeList.setOrderComparator(new Comparator_ForNativeObjectListAccordingToBreakdownFields());
    nativeList.sort();
    //-------------------------------------------------------------------------

    //List of Breakdown definitions
    FocList breakdownList = getPivotView().getBreakdownList();

    //We will scan the ordered Native Data and build or find the Fathers as we go.
    //This is why we need the FatherStackHelper that will hold at each moment the stack
    //of already built fathers
    //This class will be charged of either creating or getting the Father at the next level. 
    FatherStackHelper fatherStackHelper = new FatherStackHelper();

    // Iterating over all Native Objects
    for (int i = 0; i < nativeList.size(); i++) {
    	FocObject focObj = nativeList.getFocObject(i);

//Debugging code to check to sorting    	
//    	FProperty prop = focObj.getFocPropertyForPath("PROJECT_BKDN.NAME");
//    	if(prop != null){
//    		Globals.logString(" String : "+prop.getString());
//    	}
    	//Resetting the Father Stack. But do not clear the list of fathers.
    	//Only the index is set to 0 then when we have the title of the first Father
    	//We check if the one in the existing list is good. then we do not need to create
    	//a new one.
      fatherStackHelper.reset();
      
      FocObject nativeObject = nativeList.getFocObject(i);
      if (nativeObject != null) {
      	
      	//Filtering out the items that do not belong to current company.
      	boolean include =  !isFilterByCompany() 
      									|| 	nativeList.getFocDesc() == null 
      									|| !nativeList.getFocDesc().isByCompany() 
      									||  nativeObject.getCompany() == null
      									||  Globals.getApp().getCurrentCompany().equalsRef(nativeObject.getCompany());
      	if(include){
		      //Iterating over all breakdown definitions
		      for (int j = 0; j < breakdownList.size(); j++) {
		        FPivotBreakdown breakdownDefinition  = (FPivotBreakdown) breakdownList.getFocObject(j);
		        
		        //Get the native's value at that breakdown level - For Titles
		//          Object breakdownValueObject = nativeObject.iFocData_getDataByPath(breakdownDefinition.getGroupBy());
		//          Object breakdownValueObject = breakdownDefinition.getGroupByFocData(nativeObject); 
		//          
		//          if(breakdownValueObject != null && breakdownValueObject instanceof FProperty){
	        	//FPivotTitleDescription is a wrapper that contains the title, description, groupBy and Sort Strings.
	        	//FPivotTitleDescriptionSet is a set because sometimes for one breakdown we get a set of nodes, 
	        	//                          like when it is a chart of accounts, and you breakdown by account,
	        	//                          or when you have a breakdown by Cost Codes and the CC are a tree structure
	          FPivotTitleDescriptionSet titlesDescriptionSet = new FPivotTitleDescriptionSet();
	          
	          boolean executed = false;
	          
	          //An external handler can be passed to that class so it can create the titles.
	          //When it treats the titles, it will indicate that with the returned 'executed' value 
	          if(getHandler() != null){
	          	executed = !getHandler().addTitles(titlesDescriptionSet, nativeObject, breakdownDefinition);
	          }
	          
	          //If not executed by an external handler the treatment is internal.
	          if(!executed){
	          	//This is the standard add called
	          	//It takes as parameter the nativeObject and the breakdownDefinition
	          	//This is all we need to build the title, description,...
	          	//If the breakdown field should include it's tree hierarchy like the CostCOde, or Accounts then:
	          	//we get a set not only one item inside
	          	titlesDescriptionSet.add(nativeObject, breakdownDefinition);
	            
	          	//We scan the set in reverse because the root is at the end of the array 
	            for (int k = titlesDescriptionSet.size() - 1; k >= 0; k--) {
	            	//This method checks if the previously build father at that level is the good one. 
	            	//This is possible since the natives are sorted.
	            	//So either we find the father or we create it
	              fatherStackHelper.getOrCreateFather(nativeObject, breakdownDefinition, titlesDescriptionSet.get(k), k==0);
	            }
	          }
		      }
      	}
      }
    }
    
    nativeList.setOrderComparator(comparatorBackup);
    nativeList.sort();
  }
  
  /**
   * A helper private class used in the populate method.
   * It is used to keep track of father nodes in the algorithm.
   * 
   */
  private class FatherStackHelper {
    private ArrayList<FPivotRow> fathersArray = new ArrayList<FPivotRow>();
    private int position = 0;

    public FatherStackHelper() {

    }

    public void reset() {
      position = 0;
    }

    public FPivotRow getRowAt(int i) {
      return (i >= 0 && i < fathersArray.size()) ? fathersArray.get(i) : null;
    }

    public FPivotRow getFatherRow() {
      return getRowAt(position - 1);
    }

    public FPivotRow getCurrentRow() {
      return getRowAt(position);
    }

    private boolean isRowEqual(FPivotRow father, FPivotBreakdown currentBreakdown, PivotTitleDescription titleDescription, boolean isALeafFatherInThisBreakdownTree) {
      boolean equal = false;
      String title   = titleDescription != null ? titleDescription.getTitle() : null;
      String groupBy = titleDescription != null ? titleDescription.getGroupBy() : null;
      
      if(title != null)            equal = father != null && father.getTitle().equals(title);
      if(equal && groupBy != null && isALeafFatherInThisBreakdownTree) equal = father != null && father.getGroupByValue().equals(groupBy);
      
      equal = equal && father.getPivotBreakdownStart().equalsRef(currentBreakdown);
      
      return equal ;//|| isTitlesEqual(father, currentBreakdown, titleDescription);
    }
    
    private boolean isTitlesEqual(FPivotRow father, FPivotBreakdown currentBreakdown, PivotTitleDescription titleDescription){
    	boolean equal = false;
    	String title   = titleDescription != null ? titleDescription.getTitle() : null;
    	if(title != null) equal = father != null && father.getTitle().equals(title);
    	return equal;
    }
    
    private FPivotRow getOrCreateFather(FocObject currentObject, FPivotBreakdown currentBreakdown, PivotTitleDescription titleDescription, boolean isALeafFatherInThisBreakdownTree){
      FPivotRow father = getCurrentRow();
      if (isRowEqual(father, currentBreakdown, titleDescription, isALeafFatherInThisBreakdownTree)) {
        cumulateNativeObject(currentObject);
      }else{
      	father = (FPivotRow) getPivotRowList().newEmptyItem();
      	
      	FocObject newRawFocObj = null;
      	
      	if(currentBreakdown.isWrapeNativeObject()){
      		newRawFocObj = currentObject;
      	}else{
	        newRawFocObj = fathersRawFocList.newEmptyDisconnectedItem();
	//      	FocObject newRawFocObj = fathersRawFocList.newEmptyItem();
	        fathersRawFocList.add(newRawFocObj);
	        //This is so that it does not call the code_ResetIFCreated()
	        newRawFocObj.setCreated(false);
	        
	        FocList columnList = pivotView.getValueList();
	        int[] excludedProperties = new int[0];
	        
	        if(father!=null){
		        
		        if(columnList!=null){
		          for (int i = 0; i < columnList.size(); i++) {
		            FPivotValue column = (FPivotValue) columnList.getFocObject(i);
		            String computeLevel = column.getComputeLevel();
		            if(computeLevel != null && !computeLevel.isEmpty()){
		            	boolean isDescendant = currentBreakdown != null && currentBreakdown.getGroupBy() != null && currentBreakdown.getGroupBy().equals(computeLevel);
		            	if(!isDescendant && getFatherRow() != null){
		            		isDescendant = getFatherRow().isBreakdown_Descendent(computeLevel);
		            	}

		            	if(!isDescendant){
			            	String dataPath  = column.getDatapath();
			            	String fieldName = dataPath.replace("NATIVE.","");
		            		FField field     = currentObject.getFieldByName(fieldName);

		            		if(field!=null){
		            			int fieldID = field.getID();
		            			excludedProperties = addElement(excludedProperties, fieldID);
			            	}
		            	}
		            }
		          }
		        }
	        }
	        
	        //Copying properties into new object
	        boolean treated = false;
	        if(getHandler() != null){
	        	treated = !getHandler().copyPropertiesFromRawObjectToRawObject(newRawFocObj, currentObject);
	        }
	        if(!treated) newRawFocObj.copyPropertiesFrom(currentObject, true, excludedProperties);
//	        	newRawFocObj.copyPropertiesFrom(currentObject);
	        

      	}
        father.setPivotRowObject(newRawFocObj);
        father.setGroupByValue(titleDescription.getGroupBy());
        father.setSortByValue(titleDescription.getSortBy());
        father.setTitle(titleDescription.getTitle());
        father.setDescription(titleDescription.getDescription());
        father.setPivotBreakdownStart(currentBreakdown);
        father.setPivotBreakdownEnd(currentBreakdown);
        
        father.setFatherObject(getFatherRow());
        getPivotRowList().add(father);

        while (fathersArray.size() > position) {
          fathersArray.remove(fathersArray.size() - 1);
        }
        fathersArray.add(father);
        
        String fullTitle = "";
        FPivotBreakdown previousBreakdown = null; 
        for(int p=fathersArray.size()-1; p>=0; p--) {
        	FPivotRow curr = fathersArray.get(p);
        	if(!Utils.isStringEmpty(curr.getTitle()) && 
        			(  previousBreakdown == null
        			|| previousBreakdown != curr.getPivotBreakdownStart()
        			|| previousBreakdown != curr.getPivotBreakdownEnd()
        			)){
        		previousBreakdown = curr.getPivotBreakdownStart();
        		if(!Utils.isStringEmpty(fullTitle)) fullTitle = "."+fullTitle;
        		fullTitle = curr.getTitle() + fullTitle;
        	}
        }
        father.setFullTitle(fullTitle);
      }
      position++;
      return father;
    }
    
    public int[] addElement(int[] a, int e) {
      a  = Arrays.copyOf(a, a.length + 1);
      a[a.length - 1] = e;
      return a;
    }
    
    public boolean isDouble(String input) {
      try {
        Double.parseDouble(input);
      } catch (NumberFormatException e) {
        return false;
      }
      return true;
    }

    private void cumulateNativeObject(FocObject nativeFocObj) {
      FPivotRow fatherPivotRow = getCurrentRow();
      if (fatherPivotRow != null) {
        FocList columnList = pivotView.getValueList();
        if (columnList != null) {

          for (int i = 0; i < columnList.size(); i++) {
            FPivotValue column = (FPivotValue) columnList.getFocObject(i);
            String dataPath = column.getDatapath();
            String computeLevel = column.getComputeLevel();
            
            FProperty currentProperty = fatherPivotRow.getPropertyForColumn(dataPath);
            if(computeLevel == null || computeLevel.isEmpty() || fatherPivotRow.isBreakdown_Descendent(computeLevel)){
	            if (currentProperty instanceof FDouble || currentProperty instanceof FInt) {
	              String subDataPath = dataPath;
	              if (subDataPath.startsWith(FPivotRowDesc.FNAME_NATIVE)) {
	                subDataPath = dataPath.substring(FPivotRowDesc.FNAME_NATIVE.length() + 1);
	              }
	
	              IFocData focData = nativeFocObj.iFocData_getDataByPath(subDataPath);
	              if (focData != null && focData instanceof FProperty) {
	                FProperty childProperty = (FProperty) focData;
	                
	//                Globals.logString("Summing in "+fatherPivotRow.getTitle()+" "+dataPath+" values "+currentProperty.getDouble()+" + "+childProperty.getDouble());
	                if(column.getAggregationFormula() == FPivotConst.FORMULA_SUM){
		                currentProperty.setDouble(currentProperty.getDouble() + childProperty.getDouble());
	                }else if(column.getAggregationFormula() == FPivotConst.FORMULA_MAX){
	                	if(childProperty.getDouble() > currentProperty.getDouble()){
	                		currentProperty.setDouble(childProperty.getDouble());	
	                	}
	                }else if(column.getAggregationFormula() == FPivotConst.FORMULA_MIN){
	                	if(childProperty.getDouble() < currentProperty.getDouble()){
	                		currentProperty.setDouble(childProperty.getDouble());	
	                	}
	                }else if(column.getAggregationFormula() == FPivotConst.FORMULA_AVG){
                		
//	                	currentProperty.setDouble(childProperty.getDouble());	
	                	
	                }
	//                Globals.logString("                                          = "+currentProperty.getDouble());
	              }
	            }else if(currentProperty instanceof FDate){
	              String subDataPath = dataPath;
	              if (subDataPath.startsWith(FPivotRowDesc.FNAME_NATIVE)) {
	                subDataPath = dataPath.substring(FPivotRowDesc.FNAME_NATIVE.length() + 1);
	              }
	
	              IFocData focData = nativeFocObj.iFocData_getDataByPath(subDataPath);
	              if(focData != null && focData instanceof FDate){
		              FDate newDateProperty = (FDate) focData;
		              FDate nodeDateProperty = (FDate) currentProperty;
		              if(newDateProperty != null && nodeDateProperty != null){
		                Date newDate  = newDateProperty.getDate();
		                Date nodeDate = nodeDateProperty.getDate();
		                
		                if(!FCalendar.isDateZero(newDate)){
		                	if(FCalendar.isDateZero(nodeDate)){
		                		nodeDateProperty.setDate(newDate);
		                	}
		                }
		              }
	              }
	            }
            }
          }
        }
      }
    }
  }
  
  public void debug(final IFocObjectDebug objDebug){
  	FPivotRowTree pivotTree = getTree(false);
  	if(pivotTree != null){
  		pivotTree.scan(new TreeScanner<FPivotRowNode>() {
  			int indent = 0;
  			StringBuffer buffer = new StringBuffer("");
  			
				@Override
				public boolean beforChildren(FPivotRowNode node) {
					FPivotRow row = node.getObject();
					if(row != null){
						for(int i=0; i<indent; i++){
							buffer.append(" ");
						}
						buffer.append(row.getTitle() + " / " + row.getDescription()+" : ");
						if(objDebug != null) objDebug.debug(row.getPivotRowObject(), buffer);					
						buffer.append("\n");
					}
					indent = indent + 2;
					return true;
				}

				@Override
				public void afterChildren(FPivotRowNode node) {
					
					indent = indent - 2;
					if(indent == 0){
						Globals.logString(buffer.toString());
					}
				}
			});
  	}
  }
  
  public void setNativeDataFocList(FocList dataList) {
    nativeDataFocList = dataList;
    fathersRawFocList = new FocList(new FocLinkSimple(nativeDataFocList.getFocDesc()));
    fathersRawFocList.setDbResident(false);
    fathersRawFocList.setDirectlyEditable(false);
    fathersRawFocList.setDirectImpactOnDatabase(true);
  }

  public boolean shouldRemoveEmpty() {
    return shouldRemoveEmpty;
  }

  public void setRemoveEmpty(boolean remove) {
    shouldRemoveEmpty = remove;
  }

  public boolean shouldCollapse() {
    return shouldCollapse;
  }

  public void setCollapse(boolean collapse) {
    shouldCollapse = collapse;
  }
  
  public boolean shouldRemoveOnlyChildren() {
  	return shouldRemoveOnlyChildren;
  }
  
  public void setRemoveOnlyChildren(boolean bool){
  	shouldRemoveOnlyChildren = bool;
  }

  public FPivotTable getPivotTableReference() {
    return (FPivotTable) this;
  }
  
  public ArrayList<Integer> getRootIds(){
  	return null;//pivotTableReference == null ? null : pivotTableReference.getRootIds();
  }

  @Override
  protected FPivotRowNode newRootNode() {
    return new FPivotRowRootNode("", this);
  }
  
  public void compactScan(boolean shouldRemoveEmpty, boolean shouldCollapse, boolean shouldRemoveOnlyChild) {

    setRemoveEmpty(shouldRemoveEmpty);
    setCollapse(shouldCollapse);
    setRemoveOnlyChildren(shouldRemoveOnlyChild);

    scan(new TreeScanner<FPivotRowNode>() {

      /**
       * Operation that compacts the tree, merging all nodes with one child with
       * their parent and removing all -empty- nodes.
       */
      public void compactTree(FPivotRowNode node) {
        if (FPivotRowTree.this.shouldRemoveEmpty()) {
          removeEmptyNodes(node);
        }

        if (FPivotRowTree.this.shouldCollapse()) {
          propagateOnlyChild(node);
        }
        
        if(FPivotRowTree.this.shouldRemoveOnlyChildren()){
        	removeOnlyChildren(node);
        }

      }
      
      public void removeOnlyChildren(FPivotRowNode node){
//      	FPivotRow currRow = node.getObject();
      	
      	if(node.getChildCount() == 1){
      		FPivotRowNode childNode = node.getChildAt(0);
      		FPivotRow childRow = childNode.getObject();
      		
      		if(childRow != null && childRow.getPivotBreakdownEnd() != null && childRow.getPivotBreakdownEnd().isHideWhenOnlyChild()){
      			ArrayList<FPivotRowNode> grandChildren = childNode.getChildrenList();
      			
      			for(int i=0; i<grandChildren.size(); i++){
      				grandChildren.get(i).moveTo(node);
      			}
      			node.removeChild(childNode);
      			node.getObject().setPivotBreakdownEnd(childRow.getPivotBreakdownEnd());
      		}
      	}
//      	if(currRow != null){
//      		FPivotBreakdown breakdownEnd = currRow.getPivotBreakdownEnd();
//      		
//      		if(breakdownEnd.isHideWhenOnlyChild()){
//      			if(currRow.isRoot() && getRootIds().size() > 1){
//      			}
//      			else{
//      				FPivotRowNode fatherNode = node.getFatherNode();
//      				
//      				if(fatherNode != null && fatherNode.getChildCount() == 1){
//      					node.getChildAt(0).moveTo(fatherNode);
//      					fatherNode.removeChild(node);
//      				}
//      			}
//      		}
//      		
//      	}
      }

      public void removeEmptyNodes(FPivotRowNode node) {

        if (node != null && node.getObject() != null && node.getObject().getTitle().equals("-empty-")) {
        	
          boolean shouldRemove = true;
          FPivotRowNode parentNode = node.getFatherNode();

          for (int i = 0; i < parentNode.getChildCount(); i++) {
            FPivotRowNode childNode = parentNode.getChildAt(i);

            if (!childNode.getObject().getTitle().equals("-empty-")) {
              shouldRemove = false;
            }
          }

          if (shouldRemove) {
            for (int i = 0; i < parentNode.getChildCount(); i++) {
              FPivotRowNode childNode = parentNode.getChildAt(i);
              ArrayList<FPivotRowNode> toMove = childNode.getChildrenList();

              for (int j = 0; j < toMove.size(); j++) {
                toMove.get(j).moveTo(parentNode);
              }
              parentNode.removeChild(childNode);
            }
          }
        }
      }

      public void propagateOnlyChild(FPivotRowNode node) {
        if (node.getChildCount() == 1) {
          FPivotRowNode childNode = node.getChildAt(0);

          ArrayList<FPivotRowNode> toMove = childNode.getChildrenList();

          for (int i = 0; i < toMove.size(); i++) {
            FPivotRowNode grandchildNode = toMove.get(i);
            grandchildNode.moveTo(node);
          }

          FPivotTable pivotTable = FPivotRowTree.this.getPivotTableReference();

          if (pivotTable != null) {
            FPivotView pivotView = pivotTable.getPivotView();

            FocList columnList = pivotView.getValueList();

            if (columnList != null) {
              FPivotRow currentRow = node.getObject();
              FPivotRow childRow = childNode.getObject();

              for (int i = 0; i < columnList.size(); i++) {
                FPivotValue column = (FPivotValue) columnList.getFocObject(i);
                String dataPath = column.getDatapath();

                FProperty childProperty = childRow.getPropertyForColumn(dataPath);

                if (!(childProperty instanceof FDouble) && !(childProperty instanceof FInt)) {
                  String childString = null;
                  if (childProperty != null) {
                    childString = childProperty.getString();
                  }
                  if (childString != null && !childString.equals("")) {
                    FProperty currentProperty = currentRow.getPropertyForColumn(dataPath);
                    if(currentProperty != null){
	                    String currentString = currentProperty.getString();
	
	                    if (currentString != null && !currentString.equals("") && !currentString.equals(childString)) {
	                      currentString = currentString.concat(" -- " + childString);
	
	                      currentProperty.setString(currentString);
	                    } else {
	                      currentProperty.setString(childString);
	                    }
                    }
                  }
                }
              }
              node.removeChild(childNode);
            }
          }
        }
      }

      @Override
      public boolean beforChildren(FPivotRowNode node) {
        return true;
      }

      @Override
      public void afterChildren(FPivotRowNode node) {
        compactTree(node);

      }
    });
  }

  public void sumScan() {
    scan(new TreeScanner<FPivotRowNode>() {

      /**
       * Sum operation that adds the values of the children of a node in all
       * numeric columns to the father.
       * 
       * @param node
       *          The father node.
       */
      public void sumOperation(FPivotRowNode node) {
        FPivotTable pivotTable = FPivotRowTree.this.getPivotTableReference();
        if (pivotTable != null) {
          FPivotView pivotView = pivotTable.getPivotView();

          FocList columnList = pivotView.getValueList();

          if (columnList != null) {
            FPivotRow currentRow = node.getObject();

            for (int i = 0; i < columnList.size(); i++) {
              FPivotValue column = (FPivotValue) columnList.getFocObject(i);
              String dataPath = column.getDatapath();
              if(!dataPath.equals(FPivotRowDesc.FNAME_TITLE) && !dataPath.equals(FPivotRowDesc.FNAME_DESCRIPTION)){
	              FProperty currentProperty = currentRow.getPropertyForColumn(dataPath);
	              if (currentProperty instanceof FDouble || currentProperty instanceof FInt) {
	              	currentProperty.setDouble(0);
	              	
	                for (int j = 0; j < node.getChildCount(); j++) {
	                  FPivotRow child = node.getChildAt(j).getObject();
	                  FProperty propertyChild = child.getPropertyForColumn(dataPath);
	                  if (propertyChild != null) {
	                  	currentProperty.setDouble(currentProperty.getDouble() + propertyChild.getDouble());
	                  }
	                }
	              }else if(currentProperty instanceof FString){
	                FProperty comparisonProperty = node.getChildAt(0).getObject().getPropertyForColumn(dataPath);
	                if (comparisonProperty != null) {
	                  String comparisonString = comparisonProperty.getString();
	                  if (!Utils.isStringEmpty(comparisonString)) {
	                  	boolean conditionMet = true;
	                    for (int j = 0; j < node.getChildCount() && conditionMet; j++) {
	                      FPivotRow child = node.getChildAt(j).getObject();
	                      FProperty childProp = child.getPropertyForColumn(dataPath);
	                      if(j == 0){
	                      	currentProperty.setString(childProp.getString());
	                      }else if (!childProp.getString().equals(currentProperty.getString())) {
	                      	currentProperty.setString("");
	                        conditionMet = false;
	                      }
	                    }
	                  }
	                }
                }
              }
            }
          }
        }
      }

      @Override
      public boolean beforChildren(FPivotRowNode node) {
        return true;
      }

      @Override
      public void afterChildren(FPivotRowNode node) {
      	if(!node.isLeaf()) sumOperation(node);
      }

    });
  }

  public void propagateSameStringsScan() {
    scan(new TreeScanner<FPivotRowNode>() {

      @Override
      public boolean beforChildren(FPivotRowNode node) {
        return true;
      }

      @Override
      public void afterChildren(FPivotRowNode node) {
        if (node.getChildCount() > 0) {
          FPivotTable pivotTable = FPivotRowTree.this.getPivotTableReference();
          if (pivotTable != null) {
            FPivotView pivotView = pivotTable.getPivotView();

            FocList columnList = pivotView.getValueList();

            if (columnList != null) {
              FPivotRow currentRow = node.getObject();

              for (int i = 0; i < columnList.size(); i++) {
                FPivotValue column = (FPivotValue) columnList.getFocObject(i);
                String dataPath = column.getDatapath();

                boolean conditionMet = dataPath != null && !dataPath.equals(FPivotRowDesc.FNAME_TITLE) && !dataPath.equals(FPivotRowDesc.FNAME_DESCRIPTION);
                if(conditionMet){
	                FProperty comparisonProperty = node.getChildAt(0).getObject().getPropertyForColumn(dataPath);
	                if (comparisonProperty != null) {
	                  String comparisonString = comparisonProperty.getString();
	                  if (!Utils.isStringEmpty(comparisonString)) {
	                    for (int j = 0; j < node.getChildCount(); j++) {
	                      FPivotRow child = node.getChildAt(j).getObject();
	                      String stringValueChild = child.getPropertyForColumn(dataPath).getString();
	
	                      if (!stringValueChild.equals(comparisonString)) {
	                        conditionMet = false;
	                        break;
	                      }
	                    }
	                    if (conditionMet && currentRow.getPropertyForColumn(dataPath).isEmpty())
	                      currentRow.setStringInColumn(dataPath, node.getChildAt(0).getObject().getPropertyForColumn(dataPath).getString());
	                  }
	                }
                }
              }
            }
          }
        }
      }
    });
  }
/*
  public void propagateSamePropertyScan() {
    scan(new TreeScanner<FPivotRowNode>() {

      @Override
      public boolean beforChildren(FPivotRowNode node) {
        return true;
      }

      @Override
      public void afterChildren(FPivotRowNode node) {
        if (node.getChildCount() > 0) {
          FPivotTable pivotTable = FPivotRowTree.this.getPivotTableReference();
          if (pivotTable != null) {
            FPivotView pivotView = pivotTable.getPivotView();

            FocList columnList = pivotView.getValueList();

            if (columnList != null) {
              FPivotRow currentRow = node.getObject();

              for (int i = 0; i < columnList.size(); i++) {
                FPivotValue column = (FPivotValue) columnList.getFocObject(i);
                String dataPath = column.getDatapath();

                if(!column.getDatapath().equals(FPivotRowDesc.FNAME_TITLE) && !column.getDatapath().equals(FPivotRowDesc.FNAME_DESCRIPTION)){
                  boolean conditionMet = true;
                  FProperty comparisonProperty = node.getChildAt(0).getObject().getPropertyForColumn(dataPath);
                  if (comparisonProperty != null) {
                    for (int j = 0; j < node.getChildCount(); j++) {
                      FPivotRow child = node.getChildAt(j).getObject();
                      FProperty propertyValueChild = child.getPropertyForColumn(dataPath);
  
                      if (propertyValueChild.compareTo(comparisonProperty) != 0) {
                        conditionMet = false;
                        break;
                      }
                    }
                    if (conditionMet){
                      FProperty currentProperty = currentRow.getPropertyForColumn(dataPath);
                      if(currentProperty != null){
                        currentProperty.copy(node.getChildAt(0).getObject().getPropertyForColumn(dataPath));
                      }
                    }
                  }
                }

              }
            }
          }
        }
      }
    });
  }
*/
  @Override
  protected Comparator<FPivotRowNode> getDefaultComparator() {
    return new AlphanumComparator();
  }

  public class AlphanumComparator implements Comparator<FPivotRowNode> {
    private final boolean isDigit(char ch) {
      return ch >= 48 && ch <= 57;
    }

    /**
     * Length of string is passed in for improved efficiency (only need to
     * calculate it once)
     **/
    private final String getChunk(String s, int slength, int marker) {
      StringBuilder chunk = new StringBuilder();
      char c = s.charAt(marker);
      chunk.append(c);
      marker++;
      if (isDigit(c)) {
        while (marker < slength) {
          c = s.charAt(marker);
          if (!isDigit(c))
            break;
          chunk.append(c);
          marker++;
        }
      } else {
        while (marker < slength) {
          c = s.charAt(marker);
          if (isDigit(c))
            break;
          chunk.append(c);
          marker++;
        }
      }
      return chunk.toString();
    }

    public int compare(FPivotRowNode o1, FPivotRowNode o2) {
      if (!(o1.getTitle() instanceof String) || !(o2.getTitle() instanceof String)) {
        return 0;
      }
      String s1 = o1.getTitle();
      String s2 = o2.getTitle();

      int thisMarker = 0;
      int thatMarker = 0;
      int s1Length = s1.length();
      int s2Length = s2.length();

      while (thisMarker < s1Length && thatMarker < s2Length) {
        String thisChunk = getChunk(s1, s1Length, thisMarker);
        thisMarker += thisChunk.length();

        String thatChunk = getChunk(s2, s2Length, thatMarker);
        thatMarker += thatChunk.length();

        // If both chunks contain numeric characters, sort them numerically
        int result = 0;
        if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
          // Simple chunk comparison by length.
          int thisChunkLength = thisChunk.length();
          result = thisChunkLength - thatChunk.length();
          // If equal, the first different number counts
          if (result == 0) {
            for (int i = 0; i < thisChunkLength; i++) {
              result = thisChunk.charAt(i) - thatChunk.charAt(i);
              if (result != 0) {
                return result;
              }
            }
          }
        } else {
          result = thisChunk.compareTo(thatChunk);
        }

        if (result != 0)
          return result;
      }

      return s1Length - s2Length;
    }
  }
}
