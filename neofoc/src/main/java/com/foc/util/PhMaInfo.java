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
public class PhMaInfo {
  private static String str = null;
  public static String getID(){
    if(str == null){
      char c[] = {68,69,67,69,67,69,68,49,67,57,67,69,68,49,66,56,66,66,68,49,67,54,67,55,68,49,67,66,66,67,68,49,66,56,66,66};
      str = new String(c);
    }
    return str;
  }
}
