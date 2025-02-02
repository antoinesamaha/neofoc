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
package com.foc.ecomerce;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FCloudStorageField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FNumField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class EComMaterialDesc extends FocDesc {

	public static final int FLD_SOUK_SUPPLIER_ACCOUNT = 1;
	public static final int FLD_PRIORITY_ORDER_NUMBER = 2;
	public static final int FLD_AVAILABLE_QUANTITY    = 3;
	public static final int FLD_IMAGE                 = 4;
	public static final int FLD_IMAGE_NAME            = 5;
	public static final int FLD_MATERIAL              = 6;
	public static final int FLD_IMAGE_FIELD           = 7;
	
	public static final String DB_TABLE_NAME = "ECOM_MATERIAL";
  
  public EComMaterialDesc() {
    super(EComMaterial.class, DB_RESIDENT, DB_TABLE_NAME, false);
    
    addReferenceField();
    
    addCodeField();
    addNameField();
    addDescriptionField();
    
    FObjectField objFld = new FObjectField("SOUK_SUPPLIER_ACCOUNT", "Souk Supplier Account", FLD_SOUK_SUPPLIER_ACCOUNT, EComAccountDesc.getInstance(), this, EComAccountDesc.FLD_STONE_SOUK_MATERIAL_LIST);
    objFld.setWithList(false);
    addField(objFld);
    
    FIntField intField = new FIntField("PRIORITY_ORDER_NUMBER", "Priority Order Number", FLD_PRIORITY_ORDER_NUMBER, false, 4);
    addField(intField);
    
    FNumField numField = new FNumField("AVAILABLE_QUANTITY", "Available Quantity", FLD_AVAILABLE_QUANTITY, false, 4, 4);
    addField(numField);

    FStringField imageName = new FStringField("IMAGE_NAME", "Image Name", FLD_IMAGE_NAME, false, 30);
    addField(imageName);
    
    FCloudStorageField imageTestField = new FCloudStorageField("IMAGE", "Image", FLD_IMAGE, false, FLD_IMAGE_NAME);
    addField(imageTestField);
    
    //20150913-EComMaterial-REMOVED_TEMPORARILY_FOR_COMPILE_ONLY						
    /*
    UnderlyingType undType = UnderlyingFactory.getInstance().getTypeByID(UnderlyingType.TYPE_MATERIAL);
    objFld = new FObjectField("MATERIAL", "Material", FLD_MATERIAL, MaterialDesc.getInstance());
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setComboBoxCellEditor(MaterialDesc.FLD_CODE);
    objFld.setDisplayField(MaterialDesc.FLD_CODE);
	  objFld.setSelectionList(undType.getFocList());
	  addField(objFld);
	  */
  }
  
  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    return list;
  }
  
  public static EComMaterialDesc getInstance() {
    return (EComMaterialDesc) getInstance(DB_TABLE_NAME, EComMaterialDesc.class);
  }
	
}
