/*
 * Created on 26 Avril 2005
 */
package com.foc.focDataSourceDB.db;

import java.sql.*;
import java.util.*;

import com.foc.*;
import com.foc.db.DBIndex;
import com.foc.db.DBManager;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;

/**
 * @author 01Barmaja
 */
public class SQLTableIndexesDetails{

  Hashtable<String, DBIndex> indexesContainer = null;

  public SQLTableIndexesDetails(FocDesc focDesc) {
    try{
      indexesContainer = new Hashtable<String, DBIndex>(); 

      if(focDesc.getProvider() == DBManager.PROVIDER_ORACLE){
      	StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(focDesc.getDbSourceKey());
      	ResultSet rs = stmt.executeQuery("SELECT i.INDEX_NAME,i.UNIQUENESS,c.COLUMN_NAME FROM USER_INDEXES i JOIN ALL_IND_COLUMNS c ON c.INDEX_NAME=i.INDEX_NAME where i.TABLE_NAME= '"+focDesc.getStorageName_ForSQL()+"'");

        int nameIdx   = 1;
        int uniqueIdx = 2;
        int columnIdx = 3;
        
        while(rs.next()){
          String nameValue = rs.getString(nameIdx);
          String uniqueString = rs.getString(uniqueIdx);
          boolean uniqueValue = uniqueString != null && uniqueString.equals("UNIQUE");
          String columnValue = rs.getString(columnIdx);

          if (nameValue != null){
            DBIndex dbIndex = (DBIndex) indexesContainer.get(nameValue);
            if(dbIndex == null){
              dbIndex = new DBIndex(nameValue, focDesc, uniqueValue, false);
              indexesContainer.put(nameValue, dbIndex);
            }
            FField field = focDesc.getFieldByDBCompleteName(columnValue); 
            if(field == null){
              Globals.logString("COl value : "+columnValue+" table:"+focDesc.getStorageName_ForSQL());
              field = focDesc.getFieldByName(columnValue);
            }
            if(field != null){
            	dbIndex.addField(field.getID());
            }
          }
        }
        rs.close();
        DBManagerServer.getInstance().unlockStatement(stmt);
        
      } else if (focDesc.getProvider() == DBManager.PROVIDER_POSTGRES) {
      	StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(focDesc.getDbSourceKey());
      	String query = "SELECT indexname, indexdef FROM pg_indexes WHERE schemaname = 'public' AND tablename= '"+focDesc.getStorageName_ForSQL()+"'";
      	Globals.logString(query);
      	ResultSet rs = stmt.executeQuery(query);

        int nameIdx = 1;
        int definitionIdx  = 2;
        
        while(rs.next()){
          String nameValue = rs.getString(nameIdx);
          String definitionString = rs.getString(definitionIdx);
          boolean uniqueValue = definitionString != null && definitionString.toUpperCase().contains("UNIQUE");

          if (nameValue != null){
            DBIndex dbIndex = (DBIndex) indexesContainer.get(nameValue);
            if(dbIndex == null){
              dbIndex = new DBIndex(nameValue, focDesc, uniqueValue, false);
              indexesContainer.put(nameValue, dbIndex);
            }
            
            int parenthesisOpen  = definitionString.lastIndexOf("(");
            int parenthesisClose = definitionString.lastIndexOf(")");
            if(parenthesisOpen > 0 && parenthesisClose > parenthesisOpen) {
            	String fieldsStr = definitionString.substring(parenthesisOpen+1, parenthesisClose);
            	StringTokenizer tokzer = new StringTokenizer(fieldsStr, ",\"", false);
            	while(tokzer != null && tokzer.hasMoreTokens()) {
            		String columnValue = tokzer.nextToken();
                FField field = focDesc.getFieldByDBCompleteName(columnValue); 
                if(field == null){
                  Globals.logString("COl value : "+columnValue+" table:"+focDesc.getStorageName_ForSQL());
                  field = focDesc.getFieldByName(columnValue);
                }
                if(field != null){
                	dbIndex.addField(field.getID());
                }
            	}
            }
            
            /*
            FField field = focDesc.getFieldByDBCompleteName(columnValue); 
            if(field == null){
              Globals.logString("COl value : "+columnValue+" table:"+focDesc.getStorageName_ForSQL());
              field = focDesc.getFieldByName(columnValue);
            }
            if(field != null){
            	dbIndex.addField(field.getID());
            }
            */
          }
        }
        rs.close();
        DBManagerServer.getInstance().unlockStatement(stmt);
      } else {
      	Connection connection = DBManagerServer.getInstance().getConnection();
	      DatabaseMetaData dbmd = connection.getMetaData();
	      ResultSet rs = dbmd.getIndexInfo(null, null, focDesc.getStorageName_ForSQL(), false, false);
	
	      //Getting the idx of the interesting columns
	      //------------------------------------------
	      int nameIdx = -1;
	      int uniqueIdx = -1;
	      int columnIdx = -1;
	      
	      ResultSetMetaData rsMeta = rs.getMetaData();
	      for(int i=1; i<=rsMeta.getColumnCount(); i++){
	        if(rsMeta.getColumnName(i).compareTo("COLUMN_NAME") == 0){
	          columnIdx = i;
	        }else if(rsMeta.getColumnName(i).compareTo("INDEX_NAME") == 0){
	          nameIdx = i;          
	        }else if(rsMeta.getColumnName(i).compareTo("NON_UNIQUE") == 0){
	          uniqueIdx = i;          
	        }
	      }

	      // Display MetaData 
	      /*
	      while (rs != null && rs.next()) {
	         Object nameValue;
	         for(int i=1; i<rsMeta.getColumnCount(); i++){
	           System.out.print(nameValue = rsMeta.getColumnLabel(i)+" , ");
	         }
	
	        for(int i=1; i<rsMeta.getColumnCount(); i++){
	          System.out.print(nameValue = rs.getObject(i)+" , ");
	        }
	        System.out.println();
	      }
	      */
      
	      //Scan the results and read the interesting columns and fill the hash table
	      //-------------------------------------------------------------------------
	      while (rs != null && rs.next()) {
	        String nameValue = rs.getString(nameIdx);
	        boolean uniqueValue = !rs.getBoolean(uniqueIdx);
	        String columnValue = rs.getString(columnIdx);
	
	        if (nameValue != null){
	          DBIndex dbIndex = (DBIndex) indexesContainer.get(nameValue);
	          if(dbIndex == null){
	            dbIndex = new DBIndex(nameValue, focDesc, uniqueValue, false);
	            indexesContainer.put(nameValue, dbIndex);
	          }
	          FField field = focDesc.getFieldByDBCompleteName(columnValue); 
	          if(field == null){
	            Globals.logString("COl value : "+columnValue+" table:"+focDesc.getStorageName_ForSQL());
	            field = focDesc.getFieldByName(columnValue);
	          }
	          if(field != null){
	          	dbIndex.addField(field.getID());
	          }
	        }
	      }
	      
	      if(rs != null){
	      	rs.close();
	      }
	      
	      DBManagerServer.getInstance().releaseConnection(connection);
      }
    }catch (Exception e){
    	if(focDesc != null){
    		Globals.logString("Getting Index details for: "+focDesc.getStorageName_ForSQL());
    	}
      Globals.logException(e);
    }
  }
  
  public DBIndex getIndex(String name){
    return indexesContainer != null ? (DBIndex)indexesContainer.get(name) : null;
  }
  
  public void removeIndex(String name){
    indexesContainer.remove(name);
  }
  
  public Iterator<DBIndex> iterator(){
    return indexesContainer.values().iterator();
  }
}
