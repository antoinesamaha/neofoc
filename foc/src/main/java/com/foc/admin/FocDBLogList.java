package com.foc.admin;

import com.foc.annotations.model.FocEntity;
import com.foc.db.DBManager;
import com.foc.desc.FocObject;
import com.foc.list.FocList;

@FocEntity
public class FocDBLogList extends FocList {

	public FocDBLogList(FocObject focObject) {
		super(FocDBLog.getFocDesc());
		
		if (focObject != null) {
			String tableField = DBManager.provider_ConvertFieldName(FocDBLog.getFocDesc().getProvider(), FocDBLog.FIELD_TableName);
			String refField = DBManager.provider_ConvertFieldName(FocDBLog.getFocDesc().getProvider(), FocDBLog.FIELD_ObjectRef);
			
			getFilter().putAdditionalWhere("FOC_OBJECT", tableField+"='"+focObject.getThisFocDesc().getStorageName()+"' AND "+refField+"="+focObject.getReferenceInt());
		} else {
			getFilter().putAdditionalWhere("FOC_OBJECT", "1=2");
		}
	}
	
}
