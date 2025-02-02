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
package com.foc.business.workflow.rights;

public interface RightLevelConst {
	public static final int FLD_READ                   =  1;
	public static final int FLD_INSERT                 =  2;
	public static final int FLD_DELETE_DRAFT           =  3;
	public static final int FLD_DELETE_APPROVED        =  4;
	public static final int FLD_MODIFY_DRAFT           =  5;
	public static final int FLD_MODIFY_APPROVED        =  6;
	public static final int FLD_PRINT_DRAFT            =  7;
	public static final int FLD_PRINT_APPROVE          =  8;
	public static final int FLD_APPROVE                =  9;
	public static final int FLD_CLOSE                  = 10;
	public static final int FLD_CANCEL                 = 11;
	public static final int FLD_RESET_TO_PROPOSAL      = 12;
	public static final int FLD_RESET_TO_APPROVED      = 13;
	public static final int FLD_MODIFY_CODE_DRAFT      = 14;
	public static final int FLD_MODIFY_CODE_APPROVED   = 15;
	public static final int FLD_UNDO_SIGNATURE         = 16;
	public static final int FLD_MODIFY_SIGNATRUE_STAGE = 17;
	public static final int FLD_VIEW_LOG               = 18;
	
	public static final String ALL_RIGHTS = "All Rights";
}
