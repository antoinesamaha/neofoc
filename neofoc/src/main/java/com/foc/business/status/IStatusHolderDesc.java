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
package com.foc.business.status;

public interface IStatusHolderDesc {
	public StatusHolderDesc getStatusHolderDesc();
  public int getFLD_STATUS();
  public int getFLD_CREATION_DATE();
  public int getFLD_VALIDATION_DATE();
  public int getFLD_CLOSURE_DATE();
  public int getFLD_CREATION_USER();
}
