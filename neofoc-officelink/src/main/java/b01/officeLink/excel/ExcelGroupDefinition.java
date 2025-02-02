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
package b01.officeLink.excel;

import java.util.ArrayList;

public class ExcelGroupDefinition {
	ArrayList<Integer> rowArray = null;
	
	public ExcelGroupDefinition(){
		rowArray = new ArrayList<Integer>();
	}
	
	public void dispose(){
	}

	public void addRow(int row){
		rowArray.add(row);
	}
	
	public int getRowCount(){
		return rowArray.size();
	}
	
	public int getRowAt(int i){
		return rowArray.get(i);
	}
}
