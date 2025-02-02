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
package com.foc.business.currency;

public interface CurrencyConstants {
  public static final int FLD_NAME        = 1;
  public static final int FLD_DESCRIPTION = 2;
  public static final int FLD_RATE_RATIO  = 3;

  public static final int RATE_RATIO_BASIC_THIS = 0;
  public static final int RATE_RATIO_THIS_BASIC = 1;

  public static final String RATE_RATIO_LABEL_BASIC_THIS = "1 of the Basic Currency = X of this currency";
  public static final String RATE_RATIO_LABEL_THIS_BASIC = "1 of this Currency = X of the Basic currency";
}
