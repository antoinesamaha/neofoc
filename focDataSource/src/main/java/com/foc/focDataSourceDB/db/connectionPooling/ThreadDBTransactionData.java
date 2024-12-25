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

public class ThreadDBTransactionData {
	private ConnectionPool pool = null;
	
  private boolean transaction_Began                         = false;
  private int     nbrOfBeginTransactionsInTransactionThread = 0    ;
  
  public ThreadDBTransactionData(ConnectionPool pool){
  	this.pool = pool;
  }
  
  public void dispose(){
  	pool = null;
  }
  
	public boolean transaction_BeginTransactionIfRequestIsToBeExecuted(){
		boolean shouldBegin = false;
		if(!transaction_Began){
			transaction_Began = true;
			shouldBegin = true;//beginTransaction();
		}
		return shouldBegin;
	}
	
	public void transaction_setShouldSurroundWithTransactionIfRequest(){
		if(nbrOfBeginTransactionsInTransactionThread == 0){
			transaction_Began = false;
		}
		nbrOfBeginTransactionsInTransactionThread++;
	}
	
	public boolean transaction_SeeIfShouldCommit(){
		boolean shouldCommit = false;
    if(nbrOfBeginTransactionsInTransactionThread > 0){
      nbrOfBeginTransactionsInTransactionThread--;
    }
    if(nbrOfBeginTransactionsInTransactionThread == 0){
    	if(transaction_Began){
    		shouldCommit = true;
    	}
    }
    return shouldCommit;
	}
}
