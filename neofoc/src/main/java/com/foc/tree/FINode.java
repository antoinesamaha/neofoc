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
package com.foc.tree;

public interface FINode<N extends FINode, O extends Object> {
  public void    dispose();
  public String  getTitle();
  public void    setTitle(String title);
  
  public N       getFatherNode();
  public void    setFatherNode(N fatherNode);
  
  public int     getNodeDepth();
  public boolean isLeaf();  
  public boolean isRoot();
  public N       getRootNode();
  public FTree   getTree();
  
  public N       addChild(String childTitle);
  
  public O       getObject();
  public void    setObject(O object);
  public void    scan(TreeScanner scanner);
  public void    scanVisible(TreeScanner scanner);
  
  public int     getChildCount();
  public N       getChildAt(int i);
}
