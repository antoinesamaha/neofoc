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
package com.foc.desc;

import com.foc.desc.field.FObjectField;
import com.foc.util.Utils;

public class ReferenceCheckerDelete {
	private ReferenceChecker 	referenceChecker 			= null;
	private FocObject 				objectToRedirecctFrom = null;
	
	public ReferenceCheckerDelete(ReferenceChecker referenceChecker, FocObject objectToRedirecctFrom){
		this.referenceChecker      = referenceChecker;
		this.objectToRedirecctFrom = objectToRedirecctFrom;
	}
	
	public void dispose(){
		this.referenceChecker      = null;
		this.objectToRedirecctFrom = null;
	}
	
	/**
	 * @return the referenceChecker
	 */
	public ReferenceChecker getReferenceChecker() {
		return referenceChecker;
	}

	/**
	 * @param referenceChecker the referenceChecker to set
	 */
	public void setReferenceChecker(ReferenceChecker referenceChecker) {
		this.referenceChecker = referenceChecker;
	}

	/**
	 * @return the objectToRedirecctFrom
	 */
	public FocObject getObjectToRedirecctFrom() {
		return objectToRedirecctFrom;
	}

	/**
	 * @param objectToRedirecctFrom the objectToRedirecctFrom to set
	 */
	public void setObjectToRedirecctFrom(FocObject objectToRedirecctFrom) {
		this.objectToRedirecctFrom = objectToRedirecctFrom;
	}
	
	public StringBuffer buildDeleteRequest(){
		StringBuffer sql = null;
		FocObject obj = getObjectToRedirecctFrom();
		if(obj != null && getReferenceChecker() != null && getReferenceChecker().getFocDesc() != null){
			FocDesc      focDesc = getReferenceChecker().getFocDesc();
			FObjectField objFld  = (FObjectField) focDesc.getFieldByID(referenceChecker.getObjectFieldID());
			if(focDesc != null && objFld != null){
				long   ref       = obj.getReferenceInt();
				String fiedlName = objFld.getDBName();
		
				if(ref > 0 && !Utils.isStringEmpty(fiedlName)){
					sql = new StringBuffer("delete from ");
					sql.append(focDesc.getStorageName_ForSQL());
					sql.append(" where ");
					
					sql.append(fiedlName);
					sql.append(" = ");
					sql.append(ref);
				}
			}
		}
		return sql;
	}
}

