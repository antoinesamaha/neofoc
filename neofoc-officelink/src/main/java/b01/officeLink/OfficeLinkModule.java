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
package b01.officeLink;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;
import com.foc.desc.FocObject;
import com.foc.gui.FListPanel;

import b01.officeLink.excel.ExcelRefillerInterface;
import b01.officeLink.excel.synchronize.ExcelColumnDesc;
import b01.officeLink.excel.synchronize.ExcelSyncDesc;

public class OfficeLinkModule extends FocModule {

  private static OfficeLinkModule module = null;  
  
	@Override
	public void declareFocObjectsOnce() {
    declareFocDescClass(OfficeLinkDesc.class);
    declareFocDescClass(ExcelSyncDesc.class);
    declareFocDescClass(ExcelColumnDesc.class);
  }

  public static OfficeLinkModule getInstance() {
    if(module == null) module = new OfficeLinkModule();
    return module;
  }

  public void export(FocObject obj, String docName, boolean allowModification){
  	export(obj, docName, allowModification, null);
  }
  
  public void export(FocObject obj, String docName, boolean allowModification, ExcelRefillerInterface excelRefiller){
    FocDesc desc            = obj.getThisFocDesc();
    String  descStorageName = desc.getStorageName();
    
    FListPanel fileTemplatesPanel = new OfficeLinkGuiBrowsePanel(OfficeLinkDesc.getList(/*FocList.FORCE_RELOAD,*/ descStorageName), 0, allowModification, descStorageName, obj, docName, excelRefiller);
    Globals.getDisplayManager().newInternalFrame(fileTemplatesPanel);
  }

  public void declare() {
    Globals.getApp().declareModule(this);
  }
}
