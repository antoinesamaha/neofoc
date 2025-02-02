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
package com.foc.focDataSourceDB.db.connectionPooling;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.foc.Globals;

public class StatementWrapper {
	private ConnectionWrapper connectionWrapper = null;
	private Statement         statement         = null;
	private Connection        connection        = null;
	
	public StatementWrapper(ConnectionWrapper connectionWrapper, Statement statement){
		this.connectionWrapper = connectionWrapper;
		this.statement = statement;
	}
	
	public void dispose(){
		connectionWrapper = null;
		statement = null;
		connection = null;
	}
	
	public void close(){
  	try{
  		if(statement != null){
  			statement.close();
  		}
  	}catch(Exception e){
  		Globals.logException(e);
  	}
	}
	
	public void unlockStatement() {
		if(connectionWrapper != null){
			connectionWrapper.unlockStatement(this);
		}
	}
	
	public Statement getStatement(){
		return statement;
	}
	
	public ResultSet getResultSet() throws SQLException {
		ResultSet resultSet = null;
		Statement stmt = getStatement();
		resultSet = stmt.getResultSet();
		return resultSet; 
	}
	
	public String getDBSourceKey(){
		return connectionWrapper != null ? connectionWrapper.getDBSourceKey() : null;
	}
	
	public void executeUpdate(String req) throws SQLException {
		getStatement().executeUpdate(req);
	}

	public ResultSet executeQuery(String req) throws SQLException {
		return getStatement() != null ? getStatement().executeQuery(req) : null;
	}
	
	public void execute(String req) throws SQLException{
		if(getStatement() != null){
			getStatement().execute(req);
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public ConnectionWrapper getConnectionWrapper() {
		return connectionWrapper;
	}
}
