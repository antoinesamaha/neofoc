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
package com.foc.db;

import java.util.HashMap;
import java.util.Iterator;

public class SQLJoinMap {
  
  private HashMap<String, SQLJoin> joinMap = null;
  private int aliasCount = 1;
  public static String MAIN_TABLE_ALIAS = "M";
  
  public SQLJoinMap(){
    
  }
  
  public SQLJoin addJoin (SQLJoin join){
    if(joinMap == null){
      joinMap = new HashMap<String, SQLJoin>();
    }
    SQLJoin newJoin = joinMap.get(join.getKey());
    if(newJoin == null){
      newJoin = join;
      joinMap.put(newJoin.getKey(), newJoin);      
      newJoin.setNewAlias("T"+aliasCount++);
    }
    return newJoin;
  }
  
  public int size(){
    return joinMap != null ? joinMap.size() :0;  
  }
  
  public void clearJoinMap(){
    if(joinMap != null){
      joinMap.clear();
    }
  }
  
  public String getMainTableAlias(){
    return MAIN_TABLE_ALIAS;
  }
  
  public Iterator getJoinMapIterator(){
  return joinMap.values().iterator();
  }
}
