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
package com.foc.business.notifier;

public interface FocNotificationConst {
//  public static final int FLD_TEMPLATE_OBJECT          = 1;
//  public static final int FLD_TRANSACTION              = 2;
//  public static final int FLD_EVENT                    = 3;
//  public static final int FLD_TABLE_NAME               = 4;
  
  public static final int EVT_CREATE_USER_FROM_CONTACT = 1;
  public static final int EVT_TABLE_ADD                = 2;
  public static final int EVT_TABLE_DELETE             = 3;
  public static final int EVT_TABLE_UPDATE             = 4;
  public static final int EVT_TRANSACTION_APPROVE      = 5;
  public static final int EVT_TRANSACTION_CLOSE        = 6;
  public static final int EVT_TRANSACTION_CANCEL       = 7;
  public static final int EVT_SCHEDULED                = 8;
  public static final int EVT_TRANSACTION_SIGN         = 9;
  public static final int EVT_TRANSACTION_UNSIGN       =10;
  public static final int EVT_DOC_HASH_MISSMATCH       =11;
}
