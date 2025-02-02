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
package com.foc.util;

import com.foc.desc.field.FMultipleChoiceStringField;

public class FocPrecision {
	public static FMultipleChoiceStringField newPrecisionFieldStringBased(String dbName, String title, int fieldID){
	  FMultipleChoiceStringField multFld = new FMultipleChoiceStringField(dbName, title, fieldID, false, 11);
    multFld.addChoice("10000");
    multFld.addChoice("1000");
    multFld.addChoice("100");
    multFld.addChoice("10");
    multFld.addChoice("1");
    multFld.addChoice("");
    multFld.addChoice("0.1");
    multFld.addChoice("0.01");
    multFld.addChoice("0.001");
    multFld.addChoice("0.0001");
    multFld.addChoice("0.00001");
    multFld.addChoice("0.000001");
    multFld.addChoice("0.0000001");
    multFld.addChoice("0.00000001");
    multFld.addChoice("0.000000001");
    multFld.setSortItems(true);
    return multFld;
	}
}
