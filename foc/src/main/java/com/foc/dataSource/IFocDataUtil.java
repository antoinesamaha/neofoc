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
package com.foc.dataSource;

import com.foc.desc.FocDesc;

public interface IFocDataUtil {
	public void dbUtil_RemoveAllIndexesForTable_ExceptRef(FocDesc focDesc);
	public void dbUtil_UpdateFieldEqualToAnother(String tableName, String tarField, String srcField);
	public void dbUtil_DuplicateTable(String previousName, String newName);
	public void dbUtil_RenameTable(String previousName, String newName);
	public void dbUtil_RenameColumnsText(String table, String srcCol, String tarCol, int size);
	public void dbUtil_RenameColumnsInteger(String table, String srcCol, String tarCol);
	public void dbUtil_RenameColumnsDate(String table, String srcCol, String tarCol);
}
