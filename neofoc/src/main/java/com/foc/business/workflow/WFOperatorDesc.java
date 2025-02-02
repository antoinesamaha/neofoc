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
package com.foc.business.workflow;

import com.foc.Globals;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.business.department.DepartmentDesc;
import com.foc.business.notifier.FNotifTrigger;
import com.foc.business.notifier.FocNotificationConst;
import com.foc.business.notifier.FocNotificationEvent;
import com.foc.business.notifier.FocNotificationManager;
import com.foc.business.notifier.actions.FocNotifAction_Abstract;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class WFOperatorDesc extends FocDesc {
	public static final int FLD_AREA               = 1;
	public static final int FLD_USER               = 2;
	public static final int FLD_TITLE              = 3;
	public static final int FLD_DEPARTMENT         = 4;
	
	public static final String DB_TABLE_NAME = "WF_OPERATOR";
  
	public WFOperatorDesc(){
		super(WFOperator.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(WFOperatorGuiBrowsePanel.class);
		setGuiDetailsPanelClass(WFOperatorGuiDetailsPanel.class);
		addReferenceField();
		
		FObjectField objFld = new FObjectField("AREA", "Site", FLD_AREA, false, WFSiteDesc.getInstance(), "AREA_", this, WFSiteDesc.FLD_OPERATOR_LIST);
		objFld.setSelectionList(WFSiteDesc.getInstance().getFocList(FocList.NONE));
//		objFld.setWithList(false);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFSiteDesc.FLD_NAME);
		objFld.setDisplayField(WFSiteDesc.FLD_NAME);
		objFld.setMandatory(true);
		addField(objFld);

		FPropertyListener listener = new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				if(property != null && !property.isLastModifiedBySetSQLString()){
					WFOperator operator = (WFOperator) property.getFocObject();
					operator.resetUserOperatorArray();
				}
			}
			
			@Override
			public void dispose() {
			}
		};
		
		objFld = new FObjectField("TITLE", "Title", FLD_TITLE, false, WFTitleDesc.getInstance(), "TITLE_");
		objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFTitleDesc.FLD_NAME);
		objFld.setDisplayField(WFTitleDesc.FLD_NAME);
		objFld.setMandatory(true);
		addField(objFld);
		objFld.addListener(listener);
		
		FObjectField fObjectFld = new FObjectField("USER", "User", FLD_USER, false, FocUser.getFocDesc(), "USER_", this, FocUserDesc.FLD_WF_OPERATOR_LIST);
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setMandatory(true);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    addField(fObjectFld);
    fObjectFld.addListener(listener);
    
		fObjectFld = new FObjectField("DEPARTMENT", "Department", FLD_DEPARTMENT, DepartmentDesc.getInstance());
    fObjectFld.setComboBoxCellEditor(DepartmentDesc.FLD_NAME);
    fObjectFld.setDisplayField(DepartmentDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setSelectionList(DepartmentDesc.getList(FocList.NONE));
    addField(fObjectFld);
    fObjectFld.addListener(listener);
  }
	
	@Override
  protected void afterConstruction_1(){
  	super.afterConstruction_1();
  	setSiteModificationNotifier();
  }
	
	private void setSiteModificationNotifier() {
		FocNotifAction_ReloadList manipulator = new FocNotifAction_ReloadList();
  	addNotifTriggers(manipulator, this, FocNotificationConst.EVT_TABLE_ADD);
	  addNotifTriggers(manipulator, this, FocNotificationConst.EVT_TABLE_UPDATE);
		addNotifTriggers(manipulator, this, FocNotificationConst.EVT_TABLE_DELETE);
	}
	
	private void addNotifTriggers(FocNotifAction_ReloadList manipulator, FocDesc desc, int notificationActionId) {
		if(desc != null && FocNotificationManager.getInstance() != null) {
			FocNotificationManager.getInstance().addInternalEventNotifier(notificationActionId, desc, null, manipulator);
		}
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
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_TITLE);
      list.setListOrder(order);
    }
    return list;
  }

//	private static ArrayList<WFOperator> operatorsArrayForThisUser_AllAreas = null;
//	public static ArrayList<WFOperator> getOperatorsArrayForThisUser_AllAreas(){
//		if(operatorsArrayForThisUser_AllAreas == null){
//			operatorsArrayForThisUser_AllAreas = new ArrayList<WFOperator>(); 
//
//			WFSiteTree areaTree = WFSiteTree.getInstance();
//			
//			//We scan the saved Operators list. 
//			//Then fore each operator that matches the user 
//			//1- We add its 
//			FocList operatorList = WFOperatorDesc.getList(FocList.LOAD_IF_NEEDED);
//			for(int i=0; i<operatorList.size(); i++){
//				WFOperator operator = (WFOperator) operatorList.getFocObject(i);
//				if(operator != null && operator.getUser().getName().equals(Globals.getApp().getUser_ForThisSession().getName())){
//					FNode areaNode = areaTree.findNode(operator.getArea().getReference().getInteger());
//					if(areaNode != null){
//						//Scan the are node tree and copies of this operator with these areas
//						areaNode.scan(new AreaOperatorTreeScanner(operator));
//					}
//				}
//			}
//		}		
//		return operatorsArrayForThisUser_AllAreas;
//	}
	
	/*TO DELETE
	private static class AreaOperatorTreeScanner implements TreeScanner<FNode>{
		private WFOperator operator = null;

		public AreaOperatorTreeScanner(WFOperator operator){
			this.operator = operator;
		}
		
		public void dispose(){
			operator = null;
		}
		
		public void afterChildren(FNode node) {
		}

		public boolean beforChildren(FNode node) {
			WFSite area = (WFSite) node.getObject();
			
			WFOperator newOperator = new WFOperator(); 
			newOperator.copyPropertiesFrom(operator);
			newOperator.setArea(area);
			operatorsArrayForThisUser_AllAreas.add(newOperator);
			
			return true;
		}
	}
	*/

	//USERREFACTOR
  /*
	public static ArrayList<WFOperator> newListOfTitlesForUserAndArea(WFSite area){
		ArrayList<WFOperator> titlesAndAreasForUser = new ArrayList<WFOperator>(); 
		
		if(area != null){
			ArrayList<WFOperator> arrayAll = FocUser.getOperatorsArrayForThisUser_AllAreas(true);
			
			for(int i=0; i<arrayAll.size(); i++){
				WFOperator operator = (WFOperator) arrayAll.get(i);
				if(operator != null 
						&& operator.getUser().getName().equals(Globals.getApp().getUser_ForThisSession().getName())
						&& operator.getArea().getName().equals(area.getName())
						){
					titlesAndAreasForUser.add(operator);
				}
			}
		}
		
		return titlesAndAreasForUser;
	}
	*/

	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, WFOperatorDesc.class);    
  }
	
	public class FocNotifAction_ReloadList extends FocNotifAction_Abstract{

		@Override
		public String execute(FNotifTrigger notifier, FocNotificationEvent event) {
			try{
				WFOperator operatorObject = ((WFOperator)event.getEventFocObject());
				if(operatorObject != null && operatorObject.getUser() != null)	operatorObject.getUser().userSites_Rebuild();
			} catch (Exception e){
				Globals.logException(e);
			}
			return null;
		}
	}
}
