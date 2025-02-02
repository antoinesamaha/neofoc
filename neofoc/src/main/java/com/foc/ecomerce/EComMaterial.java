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

import java.io.InputStream;

import com.foc.Globals;
import com.foc.business.adrBook.AdrBookParty;
import com.foc.business.workflow.implementation.IAdrBookParty;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class EComMaterial extends FocObject implements IAdrBookParty{

	public EComMaterial(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
	
	public EComAccount getSoukSupplierAccount(){
  	return (EComAccount) getPropertyObject(EComMaterialDesc.FLD_SOUK_SUPPLIER_ACCOUNT);
  }
	
	public void setSoukSupplierAccount(EComAccount stoneSoukSupplierAccount){
  	setPropertyObject(EComMaterialDesc.FLD_SOUK_SUPPLIER_ACCOUNT, stoneSoukSupplierAccount);
  }
	
	public int getPriorityOrderNumber(){
		return getPropertyInteger(EComMaterialDesc.FLD_PRIORITY_ORDER_NUMBER);
	}
	
	public void setPriorityOrderNumber(int orderNumber){
		setPropertyInteger(EComMaterialDesc.FLD_PRIORITY_ORDER_NUMBER, orderNumber);
	}
	
	public int getAvailableQuantity(){
		return getPropertyInteger(EComMaterialDesc.FLD_AVAILABLE_QUANTITY);
	}
	
	public void setAvailableQuantity(int availableQuantity){
		setPropertyInteger(EComMaterialDesc.FLD_AVAILABLE_QUANTITY, availableQuantity);
	}
	
	public void setImageName(String imageName){
    setPropertyString(EComMaterialDesc.FLD_IMAGE_NAME, imageName);
  }
  
  public String getImageName(){
    return getPropertyString(EComMaterialDesc.FLD_IMAGE_NAME);
  }
  
  public void setImage(InputStream inputStream){
  	Globals.getApp().getDataSource().focObject_addBlobFromInputStream(this, EComMaterialDesc.FLD_IMAGE, inputStream);
  }
  
  public InputStream getImage(){
  	return Globals.getApp().getDataSource().focObject_LoadInputStream(this, EComMaterialDesc.FLD_IMAGE);
  }
  
  public void setImageCloud(InputStream inputStream, String directoryName, boolean createDirIfNeeded, String fileName){
  	setPropertyCloudStorage(EComMaterialDesc.FLD_IMAGE, inputStream, directoryName, createDirIfNeeded, fileName);
  }
  
  public InputStream getImageCloud(){
  	return getPropertyCloudStorage(EComMaterialDesc.FLD_IMAGE);
  }

  //20150913-EComMaterial-REMOVED_TEMPORARILY_FOR_COMPILE_ONLY						
  /*
  public Material getMaterial(){
		return (Material) getPropertyObject(EComMaterialDesc.FLD_MATERIAL);
	}

	public void setMaterial(Material material){
		setPropertyObject(EComMaterialDesc.FLD_MATERIAL, material);
	}
	*/
	
	@Override
	public AdrBookParty iWorkflow_getAdrBookParty() {
		return getSoukSupplierAccount() != null ? getSoukSupplierAccount().getAdrBookParty() : null;
	}
}
