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
package com.foc.admin;

public interface FocUserHistoryConst {
  public static final int    FLD_USER                = 1;
  public static final int    FLD_HISTORY             = 2;
  public static final int    FLD_FULLSCREEN          = 3;
  public static final int    FLD_RECENT_TRANSACTIONS = 4;
  
  public static final int    HISTORY_SIZE      = 10;
  public static final String HISTORY_DELIMITER = ";";
  public static final String INNER_DELIMITER   = ",";
  
  public static final int    MODE_DEFAULT      = 0;
  public static final int    MODE_FULLSCREEN   = 1;
  public static final int    MODE_WINDOWED     = 2;
}
