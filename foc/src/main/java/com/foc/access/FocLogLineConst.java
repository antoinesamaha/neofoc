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
package com.foc.access;

public interface FocLogLineConst {
	public static final int LEN_MESSAGE = 200;
	
	public static final int FLD_DATE_TIME  = 1; 
	public static final int FLD_MESSAGE    = 2;
	public static final int FLD_TYPE       = 3;
	public static final int FLD_SUCCESSFUL = 4;

	public static final int TYPE_INFO      = 0;
	public static final int TYPE_TEST      = 1;
	public static final int TYPE_COMMAND   = 2;
	public static final int TYPE_ERROR     = 3;
	public static final int TYPE_WARNING   = 4;
	public static final int TYPE_FAILURE   = 5;
}
