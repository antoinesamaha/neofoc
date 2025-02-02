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
package com.foc.shared.prnLayout;

public interface IPrnLayoutConst {
  public static final int TYPE_FORM  = 0;
  public static final int TYPE_TABLE = 1;
  public static final int TYPE_TREE  = 2;
  public static final int TYPE_PIVOT = 3;
  public static final int TYPE_HTML_TABLE = 4;
  
  public static final String TYPE_NAME_FORM       = "form"       ;
  public static final String TYPE_NAME_TABLE      = "table"      ;
  public static final String TYPE_NAME_TREE       = "tree"       ;
  public static final String TYPE_NAME_PIVOT      = "pivot"      ;  
  
  public static final String CONTEXT_DEFAULT         = "Main";
  public static final String CONTEXT_NEW             = "New";
  public static final String VIEW_DEFAULT            = "Standard";
  public static final String VIEW_SIMPLE             = "Simple";
  public static final String VIEW_PRINTING           = "Printing";
  public static final String VIEW_PRINTING_LANDSCAPE = "Prn. Landscape";
  public static final String VIEW_WORD               = "Word";
  public static final String VIEW_MOBILE             = "Mobile";
  public static final String VIEW_SELECTION          = "Selection";

}
