/*
 * Created on Sep 26, 2005
 */
package com.foc.focDataSourceDB.db.util;

import java.sql.SQLException;

import com.foc.Application;
import com.foc.Globals;
import com.foc.admin.AdminModule;
import com.foc.admin.FocUser;
import com.foc.focDataSourceDB.db.DBManagerServer;
import com.foc.focDataSourceDB.db.SQLRequest;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;

/**
 * @author 01Barmaja
 */
public class MainDAU {
  
  private static class DeleteAdminUser extends SQLRequest{

    /**
     * @param focDesc
     */
    public DeleteAdminUser() {
      super(null);
    }

    public boolean buildRequest() {
      request = new StringBuffer("");
      
      request.append("DELETE FROM ");
      request.append(FocUser.DB_TABLE_NAME);
      request.append(" WHERE "+FocUser.FLDNAME_NAME+"=\""+AdminModule.ADMIN_USER+"\"");
      return false ;
    }
    
    public boolean execute(){
      boolean error = false;
      StatementWrapper stmt = DBManagerServer.getInstance().lockStatement();
      if (stmt != null) {
        error = buildRequest();
        try {
          String req = request.toString();
          Globals.logString(req);
          stmt.executeUpdate(req);
        } catch (Exception e) {
          SQLException sqlE = (SQLException) e;  
          Globals.logString(sqlE.getMessage());
          error = true;
          Globals.logException(e);
        }
        DBManagerServer.getInstance().unlockStatement(stmt);      
      }

      return error;
    }
  }
  
  public static void main(String args[]){
    Application app = Globals.newApplication(true, true, true);
    app.initDBManager();
    DeleteAdminUser request = new DeleteAdminUser();
    request.execute();
    app.exit();
  }
}
