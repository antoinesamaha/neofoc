/*
 * Created on 29 fevr. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.foc.sample;

import java.sql.*;

import com.foc.*;

/**
 * @author Standard
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DBTest {

  private Connection connection;
  private Statement statement;

  public DBTest() {

  }

  private void openConnection() {
    String url = "";
    try {
      // Load the jdbc-odbc bridge driver
      // Class.forName ("sun.jdbc.odbc.JdbcOdbcDriver");
      // url = "jdbc:odbc:TestDatabase";

      // Class.forName ("org.gjt.mm.mysql.Driver");
      // url = "jdbc:mysql:///samaha";

      Class.forName("com.mysql.jdbc.Driver").newInstance();
      url = "jdbc:mysql://localhost:3306/test";

      connection = DriverManager.getConnection(url);
      statement = connection.createStatement();
    } catch (SQLException ex) {
      // A SQLException was generated. Catch it and
      // display the error information. Note that there
      // could be multiple error objects chained
      // together
      Globals.logString("\n*** SQLException caught ***\n");

      while (ex != null) {
        Globals.logString("SQLState: " + ex.getSQLState());
        Globals.logString("Message:  " + ex.getMessage());
        Globals.logString("Vendor:   " + ex.getErrorCode());
        ex = ex.getNextException();
        Globals.logString("");
      }
    } catch (java.lang.Exception ex) {
      // Got some other type of exception. Dump it.
      ex.printStackTrace();
    }

  }

  public void closeConnection() {
    try {
      /*
       * for (int i=0;i <maximumNumberOfStatements;i++){ Statement stmt =
       * (Statement) statements.elementAt(i); stmt.close(); }
       */
      connection.close();
    } catch (Exception e) {
      Globals.logString("Exception occured in DataManager.closeConnection");
      Globals.logString(e.getMessage());
      Globals.logString(e.toString());
      Globals.logString("");
    }
  }

  public void printResultSet(ResultSet resultSet) {
    try {
      ResultSetMetaData meta = resultSet.getMetaData();
      int i;

      if (resultSet != null && meta != null) {
        for (i = 1; i <= meta.getColumnCount(); i++) {
          String colLabel = meta.getColumnLabel(i);
          int colType = meta.getColumnType(i);
          String strColType = meta.getColumnTypeName(i);
          int size = meta.getColumnDisplaySize(i);
          int scale = meta.getScale(i);

          Globals.logString(colLabel + " : " + colType + " : " + strColType + " : " + size + " : " + scale);
        }

        for (i = 1; i <= meta.getColumnCount(); i++) {
          String colLabel = meta.getColumnLabel(i);
          if (colLabel != null) {
            System.out.print(meta.getColumnLabel(i) + " ");
          }
        }

        Globals.logString("");

        while (resultSet.next()) {
          for (i = 1; i <= meta.getColumnCount(); i++) {
            String str = resultSet.getString(i);
            System.out.print(str + " ");
          }
        }
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
  }

  public void mainTest() {
    openConnection();
    try {
      ResultSet resultSet = statement.executeQuery("select * from test.boat where 1=2");
      printResultSet(resultSet);
      /*
       * DatabaseMetaData dmt = connection.getMetaData(); resultSet =
       * dmt.getTables(null, null, null, new String[] {"TABLE"});
       * printResultSet(resultSet);
       */
    } catch (Exception e) {

    }
    closeConnection();
  }

}
