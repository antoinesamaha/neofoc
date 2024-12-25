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
package com.foc.focDataSourceDB.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import com.fab.model.table.FieldDefinitionDesc;
import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.dataSource.IFocDataUtil;
import com.foc.db.DBIndex;
import com.foc.db.DBManager;
import com.foc.db.FocDBException;
import com.foc.db.SequenceDoesNotExistException;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.focDataSourceDB.db.DBManagerServer;
import com.foc.focDataSourceDB.db.SQLDropIndex;
import com.foc.focDataSourceDB.db.SQLTableIndexesDetails;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;

public class DBUtil implements IFocDataUtil {

	@Override
	public void dbUtil_RemoveAllIndexesForTable_ExceptRef(FocDesc focDesc){
		SQLTableIndexesDetails indexesDetails = new SQLTableIndexesDetails(focDesc);
		Iterator<DBIndex> iter = indexesDetails.iterator();
		while(iter != null && iter.hasNext()){
			DBIndex indexToRemove = (DBIndex) iter.next();
	   
			String indexRef = Globals.getDBManager().getINDEX_NAME_IDENTIFIER();
			if(!indexToRemove.getName().equals(indexRef)){
				SQLDropIndex dropIndex = new SQLDropIndex(indexToRemove);
				dropIndex.buildRequest();
				try{
					dropIndex.execute();
				}catch(Exception e){
					Globals.logException(e);
				}
			}
		}  		
	}
	
	@Override
	public void dbUtil_UpdateFieldEqualToAnother(String tableName, String tarField, String srcField){
		String req = "";
		try{
  		StatementWrapper stmt = DBManagerServer.getInstance().lockStatement();
  		req = "UPDATE " + tableName + " SET " + tarField + "=" + srcField;
  		Globals.logString(req);
			stmt.executeUpdate(req);
  		DBManagerServer.getInstance().unlockStatement(stmt);
		}catch(Exception e){
			Globals.logExceptionWithoutPopup(e);
		}
	}
	
	@Override
	public void dbUtil_DuplicateTable(String previousName, String newName){
		String req = "";
		try{
  		StatementWrapper stmt = DBManagerServer.getInstance().lockStatement();
  		req = "CREATE TABLE "+newName+" SELECT * FROM "+previousName;
  		Globals.logString(req);
			stmt.executeUpdate(req);
  		DBManagerServer.getInstance().unlockStatement(stmt);
		}catch(Exception e){
			//Globals.logException(e);
			//Globals.getDisplayManager().popupMessage("Error while adapt data model at : "+req);
			Globals.logExceptionWithoutPopup(e);;
			//Globals.getApp().exit(true);
		}
	}
	
	@Override
	public void dbUtil_RenameTable(String previousName, String newName){
		String req = "";
		try{
  		StatementWrapper stmt = DBManagerServer.getInstance().lockStatement();
  		req = "ALTER TABLE "+previousName+" RENAME TO "+newName;
  		Globals.logString(req);
			stmt.executeUpdate(req);
  		DBManagerServer.getInstance().unlockStatement(stmt);
		}catch(Exception e){
			//Globals.logException(e);
			//Globals.getDisplayManager().popupMessage("Error while adapt data model at : "+req);
			Globals.logExceptionWithoutPopup(e);
			//Globals.getApp().exit(true);
		}

  	try{
  		FocDesc desc = FieldDefinitionDesc.getInstance();
  		FField  fld  = desc.getFieldByID(FieldDefinitionDesc.FLD_FOC_DESC);
  		StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(desc.getDbSourceKey());
  		req = "UPDATE " + desc.getStorageName_ForSQL() + " SET " + fld.getDBName() + "='" + newName + "' WHERE " + fld.getDBName() + "='" + previousName + "'";
  		Globals.logString(req);
			stmt.executeUpdate(req);
  		DBManagerServer.getInstance().unlockStatement(stmt);
		}catch(Exception e){
			Globals.logExceptionWithoutPopup(e);
			//Globals.getDisplayManager().popupMessage("Error while adapt data model at : "+req);
			//Globals.getApp().exit(true);
		}
  		
  	try{
  		StatementWrapper stmt = DBManagerServer.getInstance().lockStatement();
  		req = "ALTER TABLE "+previousName+"_FILTER RENAME TO "+newName+"_FILTER";
  		Globals.logString(req);
			stmt.executeUpdate(req);
  		DBManagerServer.getInstance().unlockStatement(stmt);
		}catch(Exception e){
			Globals.logExceptionWithoutPopup(e);
			//Globals.getDisplayManager().popupMessage("Error while adapt data model at : "+req);
			//Globals.getApp().exit(true);
		}
	}

	@Override
	public void dbUtil_RenameColumnsText(String table, String srcCol, String tarCol, int size){
		String req = "";
		try{
  		StatementWrapper stmt = DBManagerServer.getInstance().lockStatement();
  		req = "ALTER TABLE "+table+" CHANGE COLUMN "+srcCol+" "+tarCol+" VARCHAR("+size+") NOT NULL";
  		Globals.logString(req);
			stmt.executeUpdate(req);
  		DBManagerServer.getInstance().unlockStatement(stmt);
		}catch(Exception e){
			Globals.logExceptionWithoutPopup(e);
			//Globals.getDisplayManager().popupMessage("Error while adapt data model at : "+req);
			//Globals.getApp().exit(true);
		}
	}

	@Override
	public void dbUtil_RenameColumnsInteger(String table, String srcCol, String tarCol){
		String req = "";
		try{
  		StatementWrapper stmt = DBManagerServer.getInstance().lockStatement();
  		req = "ALTER TABLE "+table+" CHANGE COLUMN "+srcCol+" "+tarCol+" INTEGER NOT NULL";
  		Globals.logString(req);
			stmt.executeUpdate(req);
  		DBManagerServer.getInstance().unlockStatement(stmt);
		}catch(Exception e){
			Globals.logExceptionWithoutPopup(e);
			//Globals.getDisplayManager().popupMessage("Error while adapt data model at : "+req);
			//Globals.getApp().exit(true);
		}
	}
	
	@Override
	public void dbUtil_RenameColumnsDate(String table, String srcCol, String tarCol) {
		String req = "";
		try{
  		StatementWrapper stmt = DBManagerServer.getInstance().lockStatement();
  		req = "ALTER TABLE "+table+" CHANGE COLUMN "+srcCol+" "+tarCol+" DATE";
  		Globals.logString(req);
			stmt.executeUpdate(req);
  		DBManagerServer.getInstance().unlockStatement(stmt);
		}catch(Exception e){
			Globals.logExceptionWithoutPopup(e);
			//Globals.getDisplayManager().popupMessage("Error while adapt data model at : "+req);
			//Globals.getApp().exit(true);
		}		
	}
	
  //BAntoineS - AUTOINCREMENT
  private static long getNextOrCurrentSequence(FocDesc focDesc, boolean next) throws FocDBException, SequenceDoesNotExistException, SQLException{
  	//This is oracle specific
  	if(focDesc.getProvider() != DBManager.PROVIDER_ORACLE){
  		throw new FocDBException("Illegal Call of an Oracle Specific fucntion");
  	}
  	
  	long nextSequence = -1;
		StatementWrapper stm = DBManagerServer.getInstance().lockStatement(focDesc.getDbSourceKey());
		String function = next ? "nextval" : "currval";
		String req = "SELECT "+focDesc.getSequenceName()+"."+function+" from dual";
		try{
			if(ConfigInfo.isLogDBSelectActive()) Globals.logString(req);
			stm.execute(req);
			ResultSet rs = stm.getResultSet();
			if(rs != null && rs.next()){
				nextSequence = rs.getLong(1);
			}
			if(rs != null) rs.close();
			DBManagerServer.getInstance().unlockStatement(stm);
		}catch(SQLException e){
			DBManagerServer.getInstance().unlockStatement(stm);
			throw new SequenceDoesNotExistException("Sequence does not exist");
		}
		return nextSequence;
  }

  public static long getNextSequence(FocDesc focDesc) throws FocDBException, SequenceDoesNotExistException, SQLException{
  	return getNextOrCurrentSequence(focDesc, true);
  }

  public static void focObject_AssignReferenceIfNeeded(FocObject focObject, boolean callFromInsertWithProviderSpecificTreatment) {
    FocDesc focDesc = focObject.getThisFocDesc();
    //BAntoineS - AUTOINCREMENT
    if (focObject.needsAssignReference()) {
    //EAntoineS - AUTOINCREMENT
    	//FocConstructor constr = new FocConstructor(NumericalReference.getFocDesc(), focDesc.getStorageName(), null);
    	//NumericalReference numRef = (NumericalReference)constr.newItem();
    	
      //BAntoineS - AUTOINCREMENT
      if(focDesc.getProvider() == DBManager.PROVIDER_ORACLE){
        if(!callFromInsertWithProviderSpecificTreatment){
        	Globals.logString("!!! Call used in Oracle, but will not work for MySQL!!!");
        }
      	long ref = 0;
      	try{
      		ref = getNextSequence(focDesc);
      		focObject.setReference_WithFocListRefHashMapAdjustment(ref);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
      }else{
        if(!callFromInsertWithProviderSpecificTreatment){
        	Globals.logString("!!! REF = 0 SAVED !!! Counld not Assign Reference because AUTOINCREMENT !!!");
        }
      }
      //EAntoineS - AUTOINCREMENT
      
      focObject.setCreated(true);
    }
  }
}
