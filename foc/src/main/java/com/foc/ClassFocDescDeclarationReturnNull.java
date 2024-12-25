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
package com.foc;

import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;

public class ClassFocDescDeclarationReturnNull extends ClassFocDescDeclaration{

	public ClassFocDescDeclarationReturnNull(FocModule module, Class cls) {
		super(module, cls);
	}

	@Override
	public FocDesc getFocDescription() {
		super.getFocDescription();
		return null;//Because we do not want to see the same FocDesc STorage name in the combo box, 
		//yet we want to call the getInstance to do the necessary modifications on the original FocDesc 
	}
	

}
