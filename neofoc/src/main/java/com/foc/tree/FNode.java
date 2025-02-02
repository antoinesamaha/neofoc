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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.filter.FocListFilter;
import com.foc.list.filter.IFocListFilter;
import com.foc.property.FProperty;
import com.foc.property.FString;

public abstract class FNode<N extends FNode, O extends Object> implements FINode<N, O>{
	private   String       code        = ""  ;
	protected String       title       = "." ;
	private   String       codeSection = ""  ;
	private   int          indexInChildrenArray = -1;       
	
	private   boolean      collapsed   = true;
  
	private   ArrayList<N> children    = null;
  private   O            object      = null;
  protected N            fatherNode  = null;
  
  public static final int    NO_SPECIFIC_VIEW_ID = -1;
  public static final String NEW_ITEM = "NewItem";
  
  public  abstract int getLevelDepth();
  public  abstract N   createChildNode(String childTitle);
  private ArrayList<Integer> visibleChildren     = null;
 	
  private int visibilityFlagForFilter = VISIBILITY_ANY;
  
  public static final int VISIBILITY_ANY   = 0;
  public static final int VISIBILITY_TRUE  = 1;
  public static final int VISIBILITY_FALSE = 2;
  
	public FNode(){
		this(".", null);
	}
	
	public FNode(String title, N fatherNode){
		setTitle(title);
		children = null;
    setFatherNode(fatherNode);
	}
	
	public void dispose(){
    dispose(false);
	}

  public void dispose(final boolean withSetDeleted){
    scan(new TreeScanner<FNode>(){
      public void afterChildren(FNode node) {
        if(node != null){
        	node.disposeWithoutRecursivity(withSetDeleted);
        }
      }

      public boolean beforChildren(FNode node) {
        return true;
      }
    });
  }
  
  private void disposeWithoutRecursivity(final boolean withSetDeleted){
    if(withSetDeleted){
      FocObject focObj = (FocObject)getObject();
      focObj.setDeleted(true);
    }

    disposeChildrenArray();
    disposeVisibleChildrenArray();

    setObject(null);
    fatherNode = null;
    title = null;          
  }
  
  public String toString(){
    return title != null ? title : "";
  }
	
	public int compareTo(FNode o) {
		return getTitle().compareTo(o.getTitle());
	}

  @Override
  public boolean equals(Object obj) {
    /*if(toString() == null){
      Globals.logString("toString isNull");
    }
    if(obj == null){
      Globals.logString("obj isNull");
    }
    if(obj.toString() == null){
      Globals.logString("obj.toString isNull");
    }*/
    //return toString().equals(((FNode)obj).toString());
  	boolean equal = toString().equals(obj.toString());
  	if(equal){
  		equal = getObject() == ((FNode)obj).getObject();
  		/*
  		equal = false;
  		if(getObject() == null && ((FNode)obj).getObject() == null){
  			equal = true;
  		}else if(getObject() != null && ((FNode)obj).getObject() != null){
  			equal = getObject() == ((FNode)obj).getObject();
  		}
  		*/
  	}
  	return equal;
  }
  
  public N getSimilarNode(N node){
    /*FNode similarNode = null;
    if(node != null){
      if(this.equals(node)){
        similarNode = this; 
      }
      for(int i = 0; i < getChildCount() && similarNode == null; i ++){
        FNode childNode = getChildAt(i);
        similarNode = childNode.getSimilarNode(node);
      }
    }
    return similarNode;*/
  	
  	N similarNode = null;
  	if(node != null){
  		similarNode = (N) getNodeByTitle(node.getTitle());
  	}
  	return similarNode;
  }
  
  public boolean isDescendentOf(FNode supposedFather){
  	boolean isDescend = false; 
  	FNode curr = this;
  	while(curr != null){
  		if(curr == supposedFather){
  			isDescend = true;
  		}
  		curr = curr.getFatherNode();
  	}
  	return isDescend;
  }
  
  @SuppressWarnings("unchecked")
	public N getNodeByTitle(String title){
    N similarNode = null;
    if(getTitle() != null &&  title != null){
      if(getTitle().equals(title)){
        similarNode = (N) this;
      }
      for(int i = 0; i < getChildCount() && similarNode == null; i ++){
        N childNode = getChildAt(i);
        similarNode = (N) childNode.getNodeByTitle(title);
      }
    }
    return similarNode;
  }

  //This one takes into account the Tree special property
  @SuppressWarnings("unchecked")
	public N getNodeByDisplayTitle(String title){
    N similarNode = null;
    String thisTitle = getDisplayTitle();
    if(thisTitle != null &&  title != null){
      if(thisTitle.equals(title)){
        similarNode = (N) this;
      }
      for(int i = 0; i < getChildCount() && similarNode == null; i ++){
        N childNode = getChildAt(i);
        similarNode = (N) childNode.getNodeByDisplayTitle(title);
      }
    }
    return similarNode;
  }

  @SuppressWarnings("unchecked")
	public N getNodeByCode(String code){
    N similarNode = null;
    String currCode = getCode();
    if(code != null){
    	//Globals.logString(" CURRENT NODE :"+currCode);
      if(currCode != null && currCode.equals(code)){
        similarNode = (N) this; 
      }
      for(int i = 0; i < getChildCount() && similarNode == null; i ++){
        FNode childNode = getChildAt(i);
        similarNode = (N) childNode.getNodeByCode(code);
      }
    }
    return similarNode;
  }

  public FNode getNodeByFocObjectProperty(int fieldID, Object value){
    FNode similarNode = null;
    FocObject obj = (FocObject) getObject();
    if(obj != null){
    	Object currValue = obj.getFocProperty(fieldID).getObject();
      if(currValue.equals(value)){
        similarNode = this; 
      }
      for(int i = 0; i < getChildCount() && similarNode == null; i ++){
        FNode childNode = getChildAt(i);
        similarNode = childNode.getNodeByFocObjectProperty(fieldID, value);
      }
    }
    
    return similarNode;
  }
  
  public boolean isLeaf(){
  	return getVisibleChildCount() <= 0;
	}
  
  /*public boolean isLeaf(){
  	return getChildCount() <= 0;
	}*/
	
  public int getNodeDepth(){
    int depth = 0;
    FINode node = this.getFatherNode();
    while(node != null){
      depth++;
      node = node.getFatherNode();
    }
    return depth;
  }
  
	public String getTitle(){
    return title;
	}
  
  public void setTitle(String title){
    this.title = title;
  }

  public String getDisplayTitle(){
  	String str = getTitle();
  	FTree fTree = getTree();
  	if(fTree != null){
      FProperty prop = fTree.getTreeSpecialProperty(this);
      if(prop != null){
      	str = String.valueOf(prop.getTableDisplayObject(null));
      }
  	}
  	
  	return str;
  }
  
	private FString getCodeProperty(){
		FString codePprop = null;
		int codeId = getTree().getDisplayCodeFieldID();
		if(codeId != FField.NO_FIELD_ID){
			Object obj = getObject();
			if(obj != null && obj instanceof FocObject){
				FocObject focObj = (FocObject) obj;
				codePprop = (FString)focObj.getFocProperty(codeId);
			}
		}
    return codePprop;
	}
	
	public String getCodeSection() {
		return codeSection;
	}
	
	public void setCodeSection(String codeSection) {
		this.codeSection = codeSection;
		FNode father = getFatherNode();
		char separation = getTree().getCodeSectionSeparatorForNodeLevel(father, father.getNodeDepth());
		if(separation > 0){
			setCode(father.getCode() + separation + codeSection);
		}else{
			setCode(father.getCode() + codeSection);
		}

		/*
		if(father != null && father.getNodeDepth() > 0 && !father.getCode().isEmpty()){
			if(father.getNodeDepth() >= 4){
				setCode(father.getCode() + "." + codeSection);
			}else{
				setCode(father.getCode() + codeSection);
			}
		}else{
			setCode(codeSection);
		}
		*/
	}
	
	public String getCode(){
		return code;
	}

	public void setCode(String code){
		/*
		if(!this.code.equals(code)){
			this.code = code;
			FString codeProp = getCodeProperty();
			if(codeProp != null) codeProp.setString(code);
		}
		*/

		boolean updated = false;
		if(!this.code.equals(code)){
			this.code = code;
			updated = true;
		}
		FString codeProp = getCodeProperty();
		if(codeProp != null && (codeProp.getString() == null || !codeProp.getString().equals(code))){
			codeProp.setString(code);
			updated = true;
		}
		if(updated){
			scanChildrenAndComputeCodeSection(true);
		}
	}
	
	/*
  public String getHierarchyCodeForNode(){
  	String code = ""; 
  	ArrayList<FNode> list = new ArrayList<FNode>();
  	FNode curr = this;
  	list.add(curr);  	
  	while(curr != null && curr.getFatherNode() != null){
  		curr = curr.getFatherNode();
  		list.add(curr);
  	}
  	
  	if(list.size() >= 2){
	  	for(int i=list.size()-2; i>=0 ; i--){
	  		FNode node = list.get(i);
	  		if(i == 0){
	  			FNode father = node.getFatherNode();
	  			if(father.get)
	  			for(int k=0; i<)
	  		}
	  		code += node.getCodeSection();
	  	}
  	}
  	
  	return code;
  }
  */
	
  public N getFatherNode() {
    return fatherNode;
  }

  public void setFatherNode(N fatherNode) {
    this.fatherNode = fatherNode;
  }
  
  public boolean isRoot(){
    return getFatherNode() == null;
  }
  
  @SuppressWarnings("unchecked")
	public N getRootNode(){
    FNode rootNode = this;
    while (!rootNode.isRoot()){
      rootNode = rootNode.getFatherNode();
    }
    return (N)rootNode;
  }
  
  @SuppressWarnings("unchecked")
	public FTree<N, O> getTree(){
    N rootNode = getRootNode();
    return (rootNode != null) ?	rootNode.getTree() : null;
  }
  
	public int findChildIndex(FNode child){
		int index = -1;
		for(int i=0; i<getChildCount() && index < 0; i++){
			if(((FNode)getChildAt(i)) == child){
				index = i;
			}
		}
		return index;
	}	
	
	public N findChild(String childTitle){
		N node = null;
    for(int i=0; i<getChildCount() && node == null; i++){
    	N currNode = getChildAt(i);
      if(currNode.getTitle().compareTo(childTitle) == 0){
        node = currNode;
      }
    }  
		return node;
	}

	public N findChildByObject(Object obj){
		N node = null;
		//Because the full search is taking time we introduced this
		node = getTree().findNode_UsingMapOnly(obj);
		if(node != null && node.getFatherNode() != this){
			node = null;
		}
		//---------------------------------------------------------
    for(int i=0; i<getChildCount() && node == null; i++){
    	N currNode = getChildAt(i);
      if(currNode.getObject() == obj){
        node = currNode;
      }
    }  
		return node;
	}

  private IFocListFilter getFocListFilter(){
    return getTree().getFocListFilter();
  }

  private boolean hasVisibilityFlag_TRUE_inFathers(){
  	boolean vis 	= false;
  	FNode 	node 	= this;
  	while(node != null && !vis){
  		vis  = node.getVisibilityFlagForFilter() == VISIBILITY_TRUE;
  		node = node.getFatherNode();
  	}
  	return vis;
  }

  private boolean hasVisibilityFlag_FALSE_inFathers(){
  	boolean hasFalse = false;
  	FNode 	node 	   = this;
  	while(node != null && !hasFalse){
  		hasFalse = node.getVisibilityFlagForFilter() == VISIBILITY_FALSE;
  		node     = node.getFatherNode();
  	}
  	return hasFalse;
  }

  public boolean isVisible(){
    boolean visible = hasVisibilityFlag_TRUE_inFathers();//getVisibilityFlagForFilter() == VISIBILITY_TRUE;
    //Here visible = true if flag is true and visible = false otherwize.
    
    //We will go into conditions only if the flag is ANY
    if(getVisibilityFlagForFilter() == VISIBILITY_ANY){
	    if(getChildCount() == 0){
	      FocObject focObject = (FocObject) getObject();
	      
	      if(focObject != null){
	      	boolean localVisible = false;
	      	
	        //Check the hide flag in the FocList      	
	        FocList focList = getTree().getFocList();
	        FocListElement element = null;
	        if(focList != null){
	          element = focList.getFocListElement(focObject);
	          localVisible = element != null ? !element.isHide() : true;
	        }
	        //--------
	        
	        //Check the FocListFilter internal visible array 
	        if(visible){
	  	      IFocListFilter focListFilter = getFocListFilter();
	  	      if(focListFilter != null && element != null){
	  	        int position = focList.getFocListElementPosition(element);
	  	        localVisible = focListFilter.isObjectExist(position);
	  	      }
	        }
	        if(!localVisible){
	        	visible = false;
	        }else{
	        	visible = true;
	        }
	        //--------        
	      }else{
	      	visible = true;
	      }
	    }else{
	    	visible = false;
	      for(int i=0; i<getChildCount() && !visible; i++){
	      	visible = getChildAt(i).isVisible();
	      }
	    }
    }

    return visible;
  }
    
  public int getVisibleChildCount(){
  	ArrayList<Integer> visibleChildren = getVisibleChildrenArray();
    return visibleChildren != null ? visibleChildren.size() : 0;
  }
  
	public boolean isSortable(){
		return true;
	}
  
  public void computeCodeSectionAccordingToLevel(int index){
  	if(!isRoot()){
	  	int level = getNodeDepth();
	  	
	  	char codeType = getTree().getCodeSectionTypeForNodeLevel(this, level);
	  	
	  	String newCodeSection = "";
	  	if(codeType > 0){
		  	if(codeType == FTree.CODE_NUMERIC){
		  		newCodeSection = String.valueOf(index + 1);
		  	}else if(codeType == FTree.CODE_ALPHA_CAPITAL || codeType == FTree.CODE_ALPHA_SMALL){
		  		char firstLetter = codeType == FTree.CODE_ALPHA_CAPITAL ? 'A' : 'a';
		  		
		  		int nbrChars = 1;
		  		
		  		while(index >= Math.pow(26, nbrChars)){
		  			nbrChars++;
		  		}
		  		
		  		int[] codeArray = new int[nbrChars];
		  		
		  		int remain = index; 
		  		for(int i=nbrChars-1; i>=0; i--){
		  			int divider = (i == 0) ? 1 : (int)Math.pow(26, i);
		  			codeArray[i] = remain / divider;
		  			remain = remain - (codeArray[i] * divider); 
		  		}

		  		for(int i=nbrChars-1; i>=0; i--){
		  			char c = (char)(codeArray[i] + firstLetter);
		  			newCodeSection += String.valueOf(c);
		  		}
		  	}
	  	}
	  	
	 		setCodeSection(newCodeSection);
  	}
  }
	
	public void sortChildren(){
		if(isSortable()){
			ArrayList<N> children = getChildrenArray(false);
			if(children != null && getTree() != null){
				Comparator<N> comparator = getTree().getComparator();
				Collections.sort(children, comparator);
				disposeVisibleChildrenArray();
				scanChildrenAndComputeCodeSection(false);
			}
		}
	}

	public void scanChildrenAndComputeCodeSection(boolean withRecursivity){
		if(children != null){
			for(int i=0; i<children.size(); i++){
				FNode child = (FNode) children.get(i);
				child.computeCodeSectionAccordingToLevel(i);
				if(withRecursivity){
					child.scanChildrenAndComputeCodeSection(withRecursivity);
				}
			}
		}
	}
	
	public O getObject() {
		return object;
	}

	public void setObject(O object) {
		FTree tree = getTree();
		if(tree == null){
			tree = getTree();
		}
		if(object == null && tree != null){
			tree.removeObject2NodeMapping(this.object);
		}
		this.object = object;
		if(this.object != null && tree != null){
			tree.putObject2NodeMapping(this.object, this);
		}
	}
	
	public void scan(TreeScanner scanner){
		if(scanner != null){
			boolean goInside = scanner.beforChildren(this);
			if(goInside){
				for(int i=0; i<getChildCount(); i++){
					FNode child = getChildAt(i);
					child.scan(scanner);
				}
			}
			scanner.afterChildren(this);
		}
	}

	public void scanVisible(TreeScanner scanner){
		if(scanner != null){
			boolean goInside = scanner.beforChildren(this);
			if(goInside){
				for(int i=0; i<getVisibleChildCount(); i++){
					FNode child = getVisibleChildAt(i);
					child.scanVisible(scanner);
				}
			}
			scanner.afterChildren(this);			
		}
	}
	
  public void moveTo(N targetNode){
    getFatherNode().removeChild(this);
    setFatherNode(targetNode);
    targetNode.addChild(this);
  }
  
  /*public void copyTo( FNode targetNode ){
    
  }*/
  
  public boolean isAncestorOf(FNode node){
    while(node != null && node != getTree().getRoot()){
      node = node.getFatherNode();  
      if(node == this){
        return true;
      }
    }
    return false;
  }
  
  public FocObject getFocObjectToShowDetailsPanelFor(){
  	return (FocObject) getObject();
  }
  
  public int getDetailsPanelViewId(){
  	return FNode.NO_SPECIFIC_VIEW_ID;
  }
  
  private ArrayList<N> getChildrenArray(boolean create){
		if(children == null && create){
			children = new ArrayList<N>();
		}
		return children;
  }
  
  private void disposeChildrenArray(){
    if(children != null){
      children.clear();
    }
    children = null;
  }
 
  public N getChildAt(int at){
		N node = null;
		ArrayList<N> children = getChildrenArray(false);
		if(children != null && at < children.size()){
			node = children.get(at);
		}
		return node ;
	}
  
  public int getChildCount(){
		ArrayList<N> children = getChildrenArray(false);
		return (children != null) ? children.size() : 0;
  }

	public N addChild_WithoutSearch(String childTitle, O obj){
    N inserted = createChildNode(childTitle);
    addChild(inserted, obj);
		return inserted;
	}
  
	public N addChild(String code, String childTitle, O obj){
		N inserted = getNodeByCode(code);

		if(inserted == null){
			inserted = addChild_WithoutSearch(childTitle, obj);
		}
		
		return inserted;
	}

	public N addChild(String childTitle, O obj){
		N inserted = !getTree().isAllowNodeNameDuplication() ? findChild(childTitle) : null;

		if(inserted == null){
			inserted = addChild_WithoutSearch(childTitle, obj);
		}
		
		return inserted;
	}

	public N addChild_SearchByObject(String childTitle, O obj){
		N inserted = findChildByObject(obj);

		if(inserted == null){
			inserted = addChild_WithoutSearch(childTitle, obj);
		}
		
		return inserted;
	}

	public void addChild(N node, O obj){
		ArrayList<N> children = getChildrenArray(true);
    children.add(node);
    if(obj != null) node.setObject(obj);
    sortChildren();
    //There is one dispose visible children inside the sortChildren in case we do sort
    //disposeVisibleChildrenArray();
  }
	
	public N addChild(String code, String childTitle){
		return addChild(code, childTitle, null);
	}

	public N addChild(String childTitle){
		return addChild(childTitle, (O) null);
	}
  
	public void addChild(N node){
		addChild(node, null);
  }
	
	public void removeChild(FNode node){
  	ArrayList<N> children = getChildrenArray(false);
  	if(children != null){
  		children.remove(node);
  		disposeVisibleChildrenArray();
  	}
  }
  
  private ArrayList<Integer> getVisibleChildrenArray(){
    if(visibleChildren == null && getChildCount() > 0){
      visibleChildren = new ArrayList<Integer>();
      int idx = 0;
      for(int i=0; i<getChildCount(); i++){
        FNode node = getChildAt(i);
        if(node.isVisible()){
          visibleChildren.add(i);
          node.setIndexInChildrenArray(idx);
          idx++;
        }else{
        	node.setIndexInChildrenArray(-1);
        }
      }
    }
    return visibleChildren; 
  }
  
  private void disposeVisibleChildrenArray(){
    if(visibleChildren != null){
    	visibleChildren.clear();
    }
    visibleChildren = null;
  }
  
  public void resetVisibleChildren(){
  	disposeVisibleChildrenArray();
  }
  
  public void addVisibleChild(int childPosition){
    ArrayList<Integer> visibleChildren = getVisibleChildrenArray();
    if(visibleChildren != null){
    	visibleChildren.add(childPosition);
    }
  }
  
  public FNode getVisibleChildAt(int at){
    FNode node = null;
    ArrayList<Integer> visibleChildren = getVisibleChildrenArray();
    if(visibleChildren != null){
    	Integer integerValue = visibleChildren.get(at);
    	if(integerValue != null){
	    	int position = integerValue.intValue();
	    	node = getChildAt(position);
    	}
    }
    return node;
  }
  
  public FProperty getFirstAncestorCustomizedProperty(int fieldID){
    FProperty property = null;
    FNode fatherNode = this;
    while(fatherNode != null && property == null){
      FocObject focObject = (FocObject) fatherNode.getObject();
      FProperty prop = focObject.getFocProperty(fieldID);
      
      if(prop != null && !prop.isInherited()){
        property = prop;
      }
      fatherNode = fatherNode.getFatherNode();
    }
    return property;
  }
  
	public int getVisibilityFlagForFilter() {
		return visibilityFlagForFilter;
	}
	
	public void setVisibilityFlagForFilter(int visibilityFlagForFilter) {
		this.visibilityFlagForFilter = visibilityFlagForFilter;
	}
	
	public boolean isCollapsed(){
		return collapsed;
	}
	
	public void setCollapsed(boolean collapsed){
		this.collapsed = collapsed;
	}
	
	public void setCollapsedWithPropagation(final boolean collapsed){
		scan(new TreeScanner<FNode>() {

			@Override
			public boolean beforChildren(FNode node) {
				node.setCollapsed(collapsed);
				return true;
			}

			@Override
			public void afterChildren(FNode node) {
			}
		});
	}
	public int getIndexInChildrenArray() {
		return indexInChildrenArray;
	}
	public void setIndexInChildrenArray(int indexInChildrenArray) {
		this.indexInChildrenArray = indexInChildrenArray;
	}
}
