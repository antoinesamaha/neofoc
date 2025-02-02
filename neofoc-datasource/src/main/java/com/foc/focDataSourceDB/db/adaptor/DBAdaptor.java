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
package com.foc.focDataSourceDB.db.adaptor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.admin.FocVersion;
import com.foc.db.DBIndex;
import com.foc.db.DBManager;
import com.foc.db.FocDBException;
import com.foc.db.SequenceDoesNotExistException;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocModule;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FReferenceField;
import com.foc.focDataSourceDB.FocDataSource_DB;
import com.foc.focDataSourceDB.db.DBManagerServer;
import com.foc.focDataSourceDB.db.SQLAlterTable;
import com.foc.focDataSourceDB.db.SQLCreateIndex;
import com.foc.focDataSourceDB.db.SQLCreateTable;
import com.foc.focDataSourceDB.db.SQLDropIndex;
import com.foc.focDataSourceDB.db.SQLRequest;
import com.foc.focDataSourceDB.db.SQLTableDetails;
import com.foc.focDataSourceDB.db.SQLTableIndexesDetails;
import com.foc.focDataSourceDB.db.connectionPooling.ConnectionPool;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;
import com.foc.focDataSourceDB.db.util.DBUtil;
import com.foc.list.FocLink;
import com.foc.util.ASCII;
import com.foc.util.Utils;

public class DBAdaptor {

    private HashMap<String, Hashtable<String, String>> actualTablesByConnection = null;
    private HashMap<String, HashMap<String, String>> actualConstraintsByConnection = null;

    //  private Hashtable<String, String> allTables      = null;
    private Hashtable<String, String> exeTables = null;
    private boolean alterAllFields = false;
    private FocDataSource_DB dataSource = null;


    public DBAdaptor(FocDataSource_DB dataSource) {
        this.dataSource = dataSource;
    }

    public void dispose() {
        if (exeTables != null) {
            exeTables.clear();
            exeTables = null;
        }
        dataSource = null;
    }

    public void dispose_ActualTablesByConnection() {
        if (actualTablesByConnection != null) {
            actualTablesByConnection.clear();
            actualTablesByConnection = null;
        }
    }

    public void dispose_ActualConstraintsByConnection() {
        if (actualConstraintsByConnection != null) {
            actualConstraintsByConnection.clear();
            actualConstraintsByConnection = null;
        }
    }

    public boolean adaptTable(FocDesc focDesc) {
        dispose_ActualTablesByConnection();
        actualTables_fillMap(true);
        boolean altered = adaptTable_DoNotGetActualTableNames(focDesc);
        dispose_ActualTablesByConnection();
        return altered;
    }

    public boolean adaptTable_DoNotGetActualTableNames(FocDesc focDesc) {
        boolean altered = adaptTable_Internal(focDesc);
        if (focDesc != null && focDesc.getLanguageValueFocDesc() != null) {
            adaptTable_Internal(focDesc.getLanguageValueFocDesc());
        }
        return altered;
    }

    private String adjustedFieldName(String name) {
        String result = ASCII.convert_ShrinkName(name, 30);
        if (result.length() > 30) {
            result = ASCII.convert_ShrinkName(name, 30);
        }
        return result;
    }

    public void adaptDataModel_ReorganzeIndexes() {
        ConnectionPool defaultConnection = DBManagerServer.getInstance().getConnectionPool(null);
        if (defaultConnection.getProvider() == DBManager.PROVIDER_ORACLE) {
            rebuildConstrains_Oracle(null);
        }
    }

    public boolean adaptDataModel(boolean forceAlterTables, boolean schemaEmpty) {
        alterAllFields = forceAlterTables;
        DBManager dbManager = Globals.getDBManager();
        try {
            actualTables_fillMap(true);

            Iterator<FocModule> iter1 = Globals.getApp().modules_Iterator();
            while (iter1 != null && iter1.hasNext()) {
                FocModule module = iter1.next();
                module.beforeAdaptDataModel();
            }

            // Just go and open all descriptions so that links with masters get created
            Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
            while (iter != null && iter.hasNext()) {
                IFocDescDeclaration focDescDeclaration = iter.next();
                if (focDescDeclaration != null) {
                    focDescDeclaration.getFocDescription();
                }
            }

            //Drop all FFK_ constraints on all Oracle connections
            if (ConfigInfo.isAdaptConstraints() && isAlterAllFields()) {
                ConnectionPool defaultConnection = DBManagerServer.getInstance().getConnectionPool(null);
                constrains_DropAll(defaultConnection);

                Iterator<ConnectionPool> auxConnIter = DBManagerServer.getInstance().auxPools_Iterator();
                while (auxConnIter != null && auxConnIter.hasNext()) {
                    ConnectionPool connectionPool = auxConnIter.next();
                    if (connectionPool != null) {
                        constrains_DropAll(connectionPool);
                    }
                }
            }
            //--------------------------------------------------

            //Do the adapt for the 2 FAB tables
            //If Altered we need to redo this process after Desc reload from FAB
            boolean oneOrMoreTableOfFabBasicTableAltered = false;
            for (int i = 0; i < dataSource.getApp().fabDefTables_Size(); i++) {
                String tableName = dataSource.getApp().fabDefTables_Get(i);
                FocDesc fabFocDesc = Globals.getApp().getFocDescByName(tableName);
                if (fabFocDesc != null && fabFocDesc.isAllowAdaptDataModel()) {
                    oneOrMoreTableOfFabBasicTableAltered = adaptTable_DoNotGetActualTableNames(fabFocDesc) || oneOrMoreTableOfFabBasicTableAltered;
                    adaptTableIndexes(fabFocDesc, false);
                    adaptTableSequence(fabFocDesc);
                }
            }

            if (oneOrMoreTableOfFabBasicTableAltered && !schemaEmpty) {
                String message = "Adapt data model not completed. \n " +
                        "Please relaunch application and adapt data modle another time.";
                if (Globals.getDisplayManager() != null) {
                    Globals.getDisplayManager().popupMessage(message);
                } else {
                    Globals.logString(message);
                }
                Globals.getApp().exit(true);
            }

            if (ConfigInfo.isShrinkDBNames()) {
                iter = Globals.getApp().getFocDescDeclarationIterator();
                while (iter != null && iter.hasNext()) {
                    IFocDescDeclaration focDescDeclaration = iter.next();
                    if (focDescDeclaration != null) {
                        FocDesc focDesc = focDescDeclaration.getFocDescription();
                        if (focDesc != null && focDesc.isPersistent()) {
                            if (focDesc.getStorageName_ForSQL().length() > 30) {
                                Globals.logString("  --- TABLE:" + focDesc.getStorageName_ForSQL());
                            }

                            FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
                            while (enumer != null && enumer.hasNext()) {
                                FField fld = enumer.nextField();
                                String dbName = enumer.getFieldCompleteName(focDesc);
                                if (dbName.length() > 30) {
//			    				Globals.logString("    - Field:"+focDesc.getStorageName()+"."+dbName);
//			    				dbName = adjustedFieldName(dbName);
//			    				Globals.logString("            "+focDesc.getStorageName()+"."+dbName+"    ("+dbName.length()+")");
                                    String newDBName = adjustedFieldName(dbName);
                                    Globals.logString("" + focDesc.getStorageName() + "," + dbName + "," + dbName.length() + "," + newDBName + "," + newDBName.length());
                                }
                            }
                        }
                    }
                }
            }

            //Here we need to compare the contents of the table definition table with the memory table definition
            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx
            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx
            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx
            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx
            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx

            iter = Globals.getApp().getFocDescDeclarationIterator();
            while (iter != null && iter.hasNext()) {
                IFocDescDeclaration focDescDeclaration = iter.next();
                //if(focDescDeclaration != null && (!(focDescDeclaration instanceof DBFocDescDeclaration) || !ConfigInfo.isForDevelopment())){
                if (focDescDeclaration != null /*&& (!(focDescDeclaration instanceof DBFocDescDeclaration) || !ConfigInfo.isForDevelopment())*/) {
                    FocDesc focDesc = focDescDeclaration.getFocDescription();
                    if (focDesc != null && focDesc.isAllowAdaptDataModel()) {
                        try {
                            adaptTable_DoNotGetActualTableNames(focDesc);
                        } catch (Exception e) {
                            Globals.logException(e);
                        }
                        try {
                            adaptTableIndexes(focDesc, false);
                        } catch (Exception e) {
                            Globals.logException(e);
                        }
                        try {
                            adaptTableSequence(focDesc);
                        } catch (Exception e) {
                            Globals.logException(e);
                        }

                        // Scan the descriptions fields and store the links tables
                        FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
                        while (enumer.hasNext()) {
                            FField ffield = enumer.nextField();
                            if (ffield != null && ffield.isDBResident()) {
                                FocLink link = ffield.getLink();
                                FocDesc linkTablDesc = link != null ? link.getLinkTableDesc() : null;
                                if (linkTablDesc != null && linkTablDesc.isAllowAdaptDataModel()) {
                                    adaptTable_DoNotGetActualTableNames(linkTablDesc);
                                    adaptTableIndexes(linkTablDesc, false);
                                    adaptTableSequence(linkTablDesc);
                                }
                            }
                        }
                    }
                }
            }

            //Oracle Constraints
            //------------------
            if (ConfigInfo.isAdaptConstraints()) {
                iter = Globals.getApp().getFocDescDeclarationIterator();
                while (iter != null && iter.hasNext()) {
                    IFocDescDeclaration focDescDeclaration = iter.next();
                    if (focDescDeclaration != null) {
                        FocDesc focDesc = focDescDeclaration.getFocDescription();
                        if (focDesc != null && focDesc.isAllowAdaptDataModel()) {
                            try {
                                constraint_Adapt(focDesc);
                            } catch (Exception e) {
                                Globals.logException(e);
                            }
                        }
                    }
                }
            }
            //------------------

            //dbManager.endAdaptDataModel();

            if (DBManagerServer.getInstance() != null) {//Logging unused tables
                Globals.logString("Unused Tables:");

                ConnectionPool connectionPool = DBManagerServer.getInstance().getConnectionPool(null);
                if (connectionPool != null) {
                    Hashtable<String, String> actualTablesMap = connectionPool.newActualTables(getExeTables());
                    if (actualTablesMap != null) {
                        Iterator<String> keyIter = actualTablesMap.keySet().iterator();
                        while (keyIter != null && keyIter.hasNext()) {
                            String str = keyIter.next();
                            if (getExeTables().get(str) == null) {
                                Globals.logString("DROP TABLE IF EXISTS " + str + ";", false);
                            }
                        }
                    }
                }
            }

            //Debug only
            //----------
    	/*
    	if(DBManagerServer.getInstance() != null){//Logging cached not cached
    		Globals.logString("Cached or Not Cached:");
    		
		    iter = Globals.getApp().getFocDescDeclarationIterator();
		    while(iter != null && iter.hasNext()){
		    	IFocDescDeclaration focDescDeclaration = iter.next();
		    	if(focDescDeclaration != null){
			    	FocDesc focDesc =  focDescDeclaration.getFocDescription();
			    	if(focDesc != null && focDesc.isDbResident()){
			    		try{
			    			Globals.logString(focDesc.getStorageName()+" , "+focDesc.isListInCache(), false);
			        }catch(Exception e){
			        	Globals.logException(e);
			    		}
			    	}
		      }
		    }
      }
      */
            //----------

            iter1 = Globals.getApp().modules_Iterator();
            while (iter1 != null && iter1.hasNext()) {
                FocModule module = (FocModule) iter1.next();
                module.declareSP();
            }

            if (dataSource != null) {
                dataSource.setEmptyDatabaseJustCreated(false);
            }

            FocVersion.saveVersions();

            iter1 = Globals.getApp().modules_Iterator();
            while (iter1 != null && iter1.hasNext()) {
                FocModule module = (FocModule) iter1.next();
                module.afterAdaptDataModel();
            }

            dispose_ActualTablesByConnection();
        } catch (Exception e) {
            Globals.logException(e);
            if (Globals.getDisplayManager() != null) {
                Globals.getDisplayManager().popupMessage("Adapt data model failed! and Aborted!");
            }
        } finally {
            dispose_ActualTablesByConnection();
        }
        return false;
    }

    public void adaptTableSequence(FocDesc focDesc) throws Exception {
        if (focDesc.getProvider() == DBManager.PROVIDER_ORACLE
                && focDesc.isPersistent()
                && focDesc.getFieldByID(FField.REF_FIELD_ID) != null) {
            StatementWrapper stm = DBManagerServer.getInstance().lockStatement(focDesc.getDbSourceKey());
            FField indentifyerField = focDesc.getIdentifierField();
            if (indentifyerField != null && indentifyerField.isDBResident()) {
                long maxRef = 0;
                String refFieldName = indentifyerField.getDBName();
                String req = "SELECT MAX(\"" + refFieldName + "\") FROM \"" + focDesc.getStorageName_ForSQL() + "\"";

                stm = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stm, req);

//	  		if(ConfigInfo.isLogDBRequestActive()) Globals.logString(req);
//	  		stm.executeQuery(req);

                ResultSet rs = stm != null ? stm.getResultSet() : null;
                while (rs != null && rs.next()) {
                    maxRef = rs.getLong(1);
                }
                try {
                    rs.close();
                } catch (Exception e) {
                    Globals.logException(e);
                }
                DBManagerServer.getInstance().unlockStatement(stm);

                long seq = 0;

                try {
                    seq = DBUtil.getNextSequence(focDesc);//Cannot use the current value because we will get that it is not defined yet in this session
                } catch (SequenceDoesNotExistException e) {
                    stm = DBManagerServer.getInstance().lockStatement(focDesc.getDbSourceKey());
                    req = "CREATE SEQUENCE " + focDesc.getSequenceName() + " START WITH " + (maxRef + 1) + " INCREMENT BY 1 NOMAXVALUE";
                    if (ConfigInfo.isLogDBRequestActive()) Globals.logString(req);
                    stm.executeUpdate(req);
                    seq = maxRef + 1;
                } catch (Exception e) {
                    Globals.logException(e);
                } finally {
                    DBManagerServer.getInstance().unlockStatement(stm);
                }

                if (seq < maxRef) {
                    stm = DBManagerServer.getInstance().lockStatement(focDesc.getDbSourceKey());
                    req = "ALTER SEQUENCE " + focDesc.getSequenceName() + " INCREMENT BY " + (maxRef + 1 - seq);
                    if (ConfigInfo.isLogDBRequestActive()) Globals.logString(req);
                    stm.executeUpdate(req);
                    DBManagerServer.getInstance().unlockStatement(stm);
                }
            }
        }
    }
    //EAntoineS - AUTOINCREMENT

    //-----------------------------------------------------
    //-----------------------------------------------------
    // command_AdaptDataModel_SingleTable
    //-----------------------------------------------------
    //-----------------------------------------------------

    private Hashtable<String, String> getExeTables() {
        if (exeTables == null) {
            exeTables = new Hashtable<String, String>();
        }
        return exeTables;
    }

    private Hashtable<String, String> actualTables_getTablesForOneConnection(String dbSourceKey) {
        Hashtable<String, String> hashTable = null;
        if (actualTablesByConnection != null) {
            hashTable = actualTablesByConnection.get(dbSourceKey);
        }
        return hashTable;
    }

    private HashMap<String, Hashtable<String, String>> actualTables_fillMap(boolean create) {
        if (actualTablesByConnection == null && create) {
            actualTablesByConnection = new HashMap<String, Hashtable<String, String>>();

            ConnectionPool defaultConnection = DBManagerServer.getInstance().getConnectionPool(null);
            Hashtable<String, String> actualTables = defaultConnection.newActualTables(getExeTables());//actualTables_fillForOneConnection(defaultConnection);
            actualTablesByConnection.put(null, actualTables);

            Iterator<ConnectionPool> auxConnIter = DBManagerServer.getInstance().auxPools_Iterator();
            while (auxConnIter != null && auxConnIter.hasNext()) {
                ConnectionPool connectionPool = auxConnIter.next();
                String sourceKey = connectionPool.getDBSourceKey();
                if (connectionPool != null && sourceKey != null) {
                    actualTables = connectionPool.newActualTables(getExeTables());
//	  	  	actualTables = actualTables_fillForOneConnection(connectionPool);

                    actualTablesByConnection.put(sourceKey, actualTables);
                }
            }
        }

        return actualTablesByConnection;
    }

    private HashMap<String, HashMap<String, String>> constraints_GetActualConstraintsMap(boolean createIfNull) {
        if (actualConstraintsByConnection == null && createIfNull) {
            actualConstraintsByConnection = new HashMap<String, HashMap<String, String>>();
            ConnectionPool defaultConnection = DBManagerServer.getInstance().getConnectionPool(null);
            HashMap<String, String> constraintsMap = constrains_NewAllConstraints(defaultConnection);
            actualConstraintsByConnection.put(null, constraintsMap);

            Iterator<ConnectionPool> auxConnIter = DBManagerServer.getInstance().auxPools_Iterator();
            while (auxConnIter != null && auxConnIter.hasNext()) {
                ConnectionPool connectionPool = auxConnIter.next();
                constraintsMap = constrains_NewAllConstraints(connectionPool);
                actualConstraintsByConnection.put(connectionPool.getDBSourceKey(), constraintsMap);
            }
        }

        return actualConstraintsByConnection;
    }
  
  /*
  public Hashtable<String, String> actualTables_fillForOneConnection(ConnectionPool connectionPool){
  	Hashtable<String, String> allTables = new Hashtable<String, String>();
    try {
    	Connection connection = connectionPool.getConnection();
    	if(connection != null){
//    		DatabaseMetaData dmt = DBManagerServer.getInstance().getConnection().getMetaData();
	      DatabaseMetaData dmt = connection.getMetaData();
	      //Globals.logString(dmt.getTimeDateFunctions());
	      if (dmt != null) {
	        ResultSet resultSet = null;
	        if(connectionPool.getProvider() == DBManager.PROVIDER_ORACLE){
	        	//Instead of Username we should use the TABLESPACE Name
	        	resultSet = dmt.getTables(null, connectionPool.getCredentials().getUsername(), null, new String[] { "TABLE" });
	        }else{
	        	resultSet = dmt.getTables(null, null, null, new String[] { "TABLE" });
	        }
	        
	        if (resultSet != null) {
	          while (resultSet.next()) {
	            String tableName = resultSet.getString(3);
	            
	            Globals.logString(" TABLE : "+resultSet.getString(1)+" | "+resultSet.getString(2)+" | "+resultSet.getString(3));
	            
	            //String upperTableName = tableName.toUpperCase();
	            allTables.put(tableName, tableName);
	            getExeTables().put(tableName, tableName);
	          }
	          resultSet.close();
	        }
	      }
    	}
    } catch (Exception e) {
      Globals.logException(e);
    }
    return allTables;
  }
  */

    private void dropColumnConstrains_MSSQL(FocDesc focDesc, String columnName) {
        try {
            ArrayList<String> constraintsToDrop = new ArrayList<String>();
            StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(focDesc.getDbSourceKey());
            String request = "SELECT df.name FROM sys.default_constraints df INNER JOIN sys.tables t ON df.parent_object_id = t.object_id INNER JOIN sys.columns c ON df.parent_object_id = c.object_id AND df.parent_column_id = c.column_id where t.name='" + focDesc.getStorageName_ForSQL() + "' and c.NAME='" + columnName + "'";
            stmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, request);
            ResultSet resSet = stmt.getResultSet();
            if (resSet != null) {
                while (resSet.next()) {
                    String value = resSet.getString(1);
                    constraintsToDrop.add(value);
                }
                resSet.close();
            }
            DBManagerServer.getInstance().unlockStatement(stmt);

            for (int i = 0; i < constraintsToDrop.size(); i++) {
                String constraintName = constraintsToDrop.get(i);
                StatementWrapper constraintStmt = DBManagerServer.getInstance().lockStatement(focDesc.getDbSourceKey());
                String req = "alter table " + focDesc.getStorageName_ForSQL() + " drop constraint [" + constraintName + "]";
                constraintStmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(constraintStmt, req);
                DBManagerServer.getInstance().unlockStatement(constraintStmt);
            }
        } catch (Exception e) {
            Globals.logException(e);
        }
    }

    private HashMap<String, String> constrains_GetAllConstraints(ConnectionPool connectionPool) {
        HashMap<String, HashMap<String, String>> map = constraints_GetActualConstraintsMap(true);
        HashMap<String, String> allConstraints = map != null ? map.get(connectionPool.getDBSourceKey()) : null;
        return allConstraints;
    }

    private HashMap<String, String> constrains_NewAllConstraints(ConnectionPool connectionPool) {
        HashMap<String, String> constraints = new HashMap<String, String>();
        try {
            if (connectionPool != null) {
                String request = null;
                if (connectionPool.getProvider() == DBManager.PROVIDER_POSTGRES) {
                    request = "SELECT conname, relname FROM pg_catalog.pg_constraint con INNER JOIN pg_catalog.pg_class rel ON rel.oid = con.conrelid INNER JOIN pg_catalog.pg_namespace nsp ON nsp.oid = connamespace where conname like 'FFK_%'";
                } else if (connectionPool.getProvider() == DBManager.PROVIDER_ORACLE) {
                    request = "SELECT constraint_name, table_name FROM user_constraints where constraint_name like 'FFK_%'";
                }

                if (!Utils.isStringEmpty(request)) {
                    StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(connectionPool.getDBSourceKey());
                    stmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, request);
                    ResultSet resSet = stmt.getResultSet();
                    if (resSet != null) {
                        while (resSet.next()) {
                            String contraintName = resSet.getString(1);
                            String tableName = resSet.getString(2);

                            constraints.put(contraintName, tableName);
                        }
                        resSet.close();
                    }
                    DBManagerServer.getInstance().unlockStatement(stmt);
                }
            }
        } catch (Exception e) {
            Globals.logException(e);
        }

        return constraints;
    }

    private void constrains_Drop(ConnectionPool connectionPool, String tableName, String constraintName) {
        try {
            if (connectionPool != null) {
                String request = null;
                if (connectionPool.getProvider() == DBManager.PROVIDER_POSTGRES) {
                    request = "alter table \"" + tableName + "\" drop constraint if exists \"" + constraintName + "\"";
                } else if (connectionPool.getProvider() == DBManager.PROVIDER_ORACLE) {
                    request = "alter table \"" + tableName + "\" drop constraint \"" + constraintName + "\"";
                }

                if (!Utils.isStringEmpty(request)) {
                    StatementWrapper constraintStmt = DBManagerServer.getInstance().lockStatement(connectionPool.getDBSourceKey());
                    //	    	constraintStmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(constraintStmt, request);
                    //	    	DBManagerServer.getInstance().unlockStatement(constraintStmt);
                    constraintStmt.executeUpdate(request);
                    Globals.logString(request);
                    DBManagerServer.getInstance().unlockStatement(constraintStmt);
                }
            }
        } catch (Exception e) {
            Globals.logException(e);
        }
    }

    private void constrains_DropAll(ConnectionPool connectionPool) {
        try {
            if (connectionPool != null) {
                HashMap<String, String> constraintsMap = constrains_GetAllConstraints(connectionPool);
                Iterator<String> iter = constraintsMap.keySet().iterator();
                while (iter != null && iter.hasNext()) {
                    String constraintName = iter.next();
                    String tableName = constraintsMap.get(constraintName);

                    constrains_Drop(connectionPool, tableName, constraintName);
                }
            }
        } catch (Exception e) {
            Globals.logException(e);
        }
    }

    private void rebuildConstrains_Oracle(String dbSourceKey) {
        try {
            ArrayList<String> constraintsDropRequests = new ArrayList<String>();

            ConnectionPool pool = DBManagerServer.getInstance() != null ? DBManagerServer.getInstance().getConnectionPool(dbSourceKey) : null;
            String ownerName = pool != null ? pool.getCredentials().getUsername() : null;
            if (ownerName != null) {
                StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(dbSourceKey);
                String request = "SELECT table_name, constraint_name FROM user_constraints WHERE INDEX_OWNER='" + ownerName + "'";
                stmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, request);
                ResultSet resSet = stmt.getResultSet();
                if (resSet != null) {
                    while (resSet.next()) {
                        String tableName = resSet.getString(1);
                        String contraintName = resSet.getString(2);
                        if (Globals.getApp().getFocDescByName(tableName) != null) {
                            constraintsDropRequests.add("ALTER INDEX \"" + contraintName + "\" REBUILD");//REORGANIZE
                        }
                    }
                    resSet.close();
                }
                DBManagerServer.getInstance().unlockStatement(stmt);

                for (int i = 0; i < constraintsDropRequests.size(); i++) {
                    request = constraintsDropRequests.get(i);
                    StatementWrapper constraintStmt = DBManagerServer.getInstance().lockStatement(dbSourceKey);
                    constraintStmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(constraintStmt, request);
                    DBManagerServer.getInstance().unlockStatement(constraintStmt);
                }
            }
        } catch (Exception e) {
            Globals.logException(e);
        }
    }

    private void constraint_Adapt(FocDesc focDesc) {
        if (focDesc != null && focDesc.isDbResident() && (focDesc.getProvider() == DBManager.PROVIDER_ORACLE || focDesc.getProvider() == DBManager.PROVIDER_POSTGRES)) {
            String dbSourceKey = focDesc.getDbSourceKey();
            ConnectionPool connectionPool = DBManagerServer.getInstance().getConnectionPool(dbSourceKey);
            HashMap<String, String> allConstraints = constrains_GetAllConstraints(connectionPool);

            FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
            while (enumer.hasNext()) {
                FField field = (FField) enumer.next();

                if (field != null && field instanceof FObjectField && field.isDBResident()) {
                    try {
                        int tableNameHashCode = focDesc.getStorageName_ForSQL().hashCode();
                        if (tableNameHashCode < 0) tableNameHashCode = -tableNameHashCode;
                        String constraintName = "FFK_" + tableNameHashCode + "_" + field.getDBName();
                        if (constraintName.endsWith("_REF")) {
                            constraintName = constraintName.substring(0, constraintName.length() - 4);
                        } else if (constraintName.endsWith("REF")) {
                            constraintName = constraintName.substring(0, constraintName.length() - 3);
                        }

                        if (constraintName.length() > 30) {
                            int fieldNameHashCode = field.getDBName().hashCode();
                            if (fieldNameHashCode < 0) {
                                //This is only needed because we used to create constraints with negative field hash
                                String oldName = "FFK_" + tableNameHashCode + "_" + fieldNameHashCode;
                                if (allConstraints != null && allConstraints.get(oldName) != null) {
                                    constrains_Drop(connectionPool, allConstraints.get(oldName), oldName);
                                }

                                fieldNameHashCode = -fieldNameHashCode;
                            }
                            constraintName = "FFK_" + tableNameHashCode + "_" + fieldNameHashCode;
                        }

                        FocDesc otherFocDesc = field.getFocDesc();
                        if (Utils.isEqual_String(otherFocDesc.getDbSourceKey(), focDesc.getDbSourceKey())) {
                            FReferenceField refField = (FReferenceField) otherFocDesc.getFieldByID(FField.REF_FIELD_ID);

                            if (refField != null) {

                                //If the column is already there with zeros instead of null this will rase errors when creating the constraints
                                if (alterAllFields) {
                                    String replaceRequest = "UPDATE \"" + focDesc.getStorageName_ForSQL() + "\" set \"" + field.getDBName() + "\"=null WHERE \"" + field.getDBName() + "\"<=0";
                                    Globals.logString(replaceRequest);
                                    Globals.getApp().getDataSource().command_ExecuteRequest(focDesc.getDbSourceKey(), new StringBuffer(replaceRequest));
                                }
                                //-------------------------------------------------------------------------------------------------------------

                                if (alterAllFields || allConstraints == null || allConstraints.get(constraintName) == null) {
                                    String schemaName = SQLRequest.getNamespacePrefix(focDesc);
                                    String otherSchemaName = SQLRequest.getNamespacePrefix(otherFocDesc);
                                    String request = "alter table " + schemaName + "\"" + focDesc.getStorageName_ForSQL() + "\"";
                                    request += " add constraint \"" + constraintName + "\" foreign key (\"" + field.getDBName() + "\")";
                                    request += " references " + otherSchemaName + "\"" + otherFocDesc.getStorageName_ForSQL() + "\" (\"" + refField.getDBName() + "\") ";
                                    if (focDesc.getProvider() == DBManager.PROVIDER_ORACLE) {
                                        StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(focDesc.getDbSourceKey());
                                        request += " novalidate";
                                        stmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, request);
                                        DBManagerServer.getInstance().unlockStatement(stmt);
                                    } else if (focDesc.getProvider() == DBManager.PROVIDER_POSTGRES) {
                                        Globals.getApp().getDataSource().command_ExecuteRequest(focDesc.getDbSourceKey(), new StringBuffer(request));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Globals.logException(e);
                    }
                }
            }
        }
//  	alter table PLACE
//    add constraint FK_PLACE_REF_15765_COUNTRY foreign key (SEQ_COUNTRY)
//    references COUNTRY (SEQ_COUNTRY);
    }

    private boolean adaptDBTableFields(FocDesc focDesc) {
        boolean adapted = false;
        SQLTableDetails table = new SQLTableDetails(focDesc);
        table.buildRequest();
        table.execute();

        Hashtable actualFields = table.getFieldsHashtable();

        FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
        while (enumer.hasNext()) {
            FField focField = (FField) enumer.next();

            FField realField = null;
            String modelFieldName = enumer.getFieldCompleteName(focDesc);
//      if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
//      	modelFieldName = modelFieldName.toUpperCase();
//      }
            realField = (FField) actualFields.get(modelFieldName);
//      if(enumer.getFieldCompleteName(focDesc)==FField.REF_FIELD_NAME) {
//      	continue;
//      }

            try {
                if (realField == null) {
//	        new SQLTableDetails(focDesc);
                    SQLAlterTable alter = new SQLAlterTable(focDesc, focField, enumer.getFieldCompleteName(focDesc), SQLAlterTable.ADD);
                    if (ConfigInfo.isAdaptEnabled()) {
                        alter.execute();
                    } else {
                        alter.buildRequest();
                        String req = alter.getRequestAdaptedToProvider();
                        if (req != null) Globals.logString("NOT EXECUTED: " + req);
                    }
                    adapted = true;
	        /*
	      } else if (realField.getSqlType() != focField.getSqlType()) {
	        Exception e = new Exception("Field type conflict "+focDesc.getStorageName()+"."+focField.getDBName()+" db:" + realField.getName() + " " + realField.getClass().getName() + " app:" + enumer.getFieldCompleteName(focDesc) + " " + focField.getClass().getName());
	        Globals.logException(e);
	        */
                } else if (isAlterAllFields() || focField.compareSQLDeclaration(realField) != 0) {
                    String fieldCompleteName = enumer.getFieldCompleteName(focDesc);
                    SQLAlterTable alter = new SQLAlterTable(focDesc, focField, fieldCompleteName, SQLAlterTable.MODIFY);
                    if (ConfigInfo.isAdaptEnabled()) {

                        try {
                            //We are Dropping Columns and in MSSQL we need to drop the constraints first
                            if (focDesc.getProvider() == DBManager.PROVIDER_MSSQL) {
                                dropColumnConstrains_MSSQL(focDesc, fieldCompleteName);
                            }
                        } catch (Exception e) {
                            Globals.logException(e);
                        }

                        alter.execute();
                    } else {
                        alter.buildRequest();
                        String req = alter.getRequestAdaptedToProvider();
                        if (req != null) Globals.logString("NOT EXECUTED: " + req);
                    }
                    //BAntoineS - AUTOINCREMENT
                } else if (!realField.isAutoIncrement() && focField.isAutoIncrement() && realField.getName().equals(FField.REF_FIELD_NAME) && focDesc.getProvider() == DBManager.PROVIDER_MYSQL) {
                    SQLAlterTable alter = new SQLAlterTable(focDesc, focField, enumer.getFieldCompleteName(focDesc), SQLAlterTable.MODIFY);
                    if (ConfigInfo.isAdaptEnabled()) {
                        alter.execute();
                    } else {
                        alter.buildRequest();
                        String req = alter.getRequestAdaptedToProvider();
                        if (req != null) Globals.logString("NOT EXECUTED: " + req);
                    }
                    //EAntoineS - AUTOINCREMENT
                }
            } catch (Exception e) {
                Globals.logException(e);
            }
            if (realField != null) {
                actualFields.remove(realField.getName());
            }
        }
        adapted = adapted || actualFields.values().size() > 0;
        for (Enumeration actEnum = actualFields.elements(); actEnum.hasMoreElements(); ) {
            FField actualField = (FField) actEnum.nextElement();
            SQLAlterTable alter = new SQLAlterTable(focDesc, actualField.getName());
            if (ConfigInfo.isAdaptEnabled()) {
                try {
                    //We are Dropping Columns and in MSSQL we need to drop the constraints first
                    if (focDesc.getProvider() == DBManager.PROVIDER_MSSQL) {
                        dropColumnConstrains_MSSQL(focDesc, actualField.getName());
                    }
                    alter.execute();
                } catch (Exception e) {
                    Globals.logException(e);
                }
            } else {
                alter.buildRequest();
                String req = alter.getRequestAdaptedToProvider();
                if (req != null) Globals.logString("NOT EXECUTED: " + req);
            }
        }
        return adapted;
    }

    private void adaptTableIndexes(FocDesc focDesc, boolean reindex) {
        if (focDesc != null && focDesc.isPersistent()) {
            SQLTableIndexesDetails indexesDetails = new SQLTableIndexesDetails(focDesc);

            Iterator iter = focDesc.indexIterator();
            while (iter != null && iter.hasNext()) {
                DBIndex index = (DBIndex) iter.next();
                if (index != null) {
                    DBIndex realIndex = indexesDetails.getIndex(index.getName());
                    try {
                        Iterator<DBIndex> idxIter = indexesDetails.iterator();
                        while (idxIter != null && idxIter.hasNext()) {
                            DBIndex indexDebug = idxIter.next();
                            Globals.logString("   - Index : " + indexDebug.getName());
                        }

                        if (realIndex == null) {
                            SQLCreateIndex sqlIndex = new SQLCreateIndex(index);
                            sqlIndex.buildRequest();
                            if (ConfigInfo.isAdaptIndexesEnabled()) {
                                sqlIndex.execute();
                            } else {
                                Globals.logString("NOT EXECUTED: " + sqlIndex.getRequestAdaptedToProvider());
                            }
                        } else if (realIndex.compareTo(index) != 0 || reindex) {
                            SQLDropIndex dropIndex = new SQLDropIndex(realIndex);
                            dropIndex.buildRequest();
                            if (ConfigInfo.isAdaptIndexesEnabled()) {
                                dropIndex.execute();
                            } else {
                                Globals.logString("NOT EXECUTED: " + dropIndex.getRequestAdaptedToProvider());
                            }

                            SQLCreateIndex createIndex = new SQLCreateIndex(index);
                            createIndex.buildRequest();
                            if (ConfigInfo.isAdaptIndexesEnabled()) {
                                createIndex.execute();
                            } else {
                                Globals.logString("NOT EXECUTED: " + createIndex.getRequestAdaptedToProvider());
                            }
                        }
                    } catch (Exception e) {
                        Globals.logException(e);
                        Globals.logString("Creating the Index");
                    }
                    if (realIndex != null) {
                        indexesDetails.removeIndex(realIndex.getName());
                    }
                }
            }

            if (ConfigInfo.isRemoveUndeclaredIndexesDuringAdaptDataModel()) {
                iter = indexesDetails.iterator();
                while (iter != null && iter.hasNext()) {
                    DBIndex indexToRemove = (DBIndex) iter.next();

                    SQLDropIndex dropIndex = new SQLDropIndex(indexToRemove);
                    dropIndex.buildRequest();
                    try {
                        dropIndex.execute();
                    } catch (Exception e) {
                        Globals.logException(e);
                    }
                }
            }
        }
    }

    private boolean adaptTable_Internal(FocDesc focDesc) {
        boolean altered = false;
        if (focDesc != null && focDesc.isPersistent() && focDesc.isAllowAdaptDataModel()) {
            String dbTableName = focDesc.getStorageName_ForSQL();
            if (dbTableName != null && DBManagerServer.getInstance() != null) {

                String dbSourceKey = focDesc.getDbSourceKey();
                Hashtable<String, String> allTablesForThisConnection = actualTables_getTablesForOneConnection(dbSourceKey);
                if (allTablesForThisConnection != null) {

                    if (allTablesForThisConnection.get(dbTableName) == null) {
                        //Logging the TableNames
	        	/*
		      	Iterator iter = allTablesForThisConnection.values().iterator();
		      	while(iter != null && iter.hasNext()){
		      		String str = (String)iter.next();
		      		Globals.logString("Table : "+str);
		      	}
		      	*/

                        focDesc.beforeAdaptTableModel(false);
                        if (focDesc.isDbResident()) {
                            Globals.getDBManager();
                            SQLCreateTable create = new SQLCreateTable(focDesc);
                            create.buildRequest();
                            if (ConfigInfo.isAdaptEnabled()) {
                                try {
                                    create.execute();
                                } catch (Exception e) {
                                    Globals.logException(e);
                                }
                            } else {
                                create.buildRequest();
                                String req = create.getRequestAdaptedToProvider();
                                if (req != null) Globals.logString("NOT EXECUTED: " + req);
                            }
                        }
                        altered = true;
                    } else {
                        focDesc.beforeAdaptTableModel(true);
                        altered = adaptDBTableFields(focDesc);
                    }
                    focDesc.afterAdaptTableModel();
                    getExeTables().put(dbTableName, dbTableName);
//	        allTablesForThisConnection.remove(dbTableName);
                }
            }
        }

        return altered;
    }

    public boolean isAlterAllFields() {
        return alterAllFields;
    }
}
