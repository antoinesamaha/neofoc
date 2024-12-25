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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.access.AccessSubject;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.event.FocEvent;
import com.foc.event.FocListener;
import com.foc.gui.FColorProvider;
import com.foc.gui.treeTable.FGTreeInTable;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.list.FocList;
import com.foc.list.FocListWithFilter;
import com.foc.list.filter.IFocListFilter;
import com.foc.property.FProperty;
import com.foc.shared.dataStore.IFocData;
import com.foc.tree.objectTree.FObjectNode;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

public abstract class FTree<N extends FNode, O extends Object> implements IFocData, Hierarchical {
  public    abstract void      growTreeFromFocList(FocList focList);
  public    abstract int       getDepthVisibilityLimit();
  public    abstract FProperty getTreeSpecialProperty(N node);
  public    abstract boolean   isNodeLocked(N node);
  public    abstract FocObject newEmptyItem(N fatherNode);
  public    abstract FocList   getFocList();
  public    abstract Icon      getIconForNode(N node);
  public    abstract int       getDisplayCodeFieldID();
  public    abstract N         addFocObject(O childObject);
  protected abstract N         newRootNode();
  
  public    abstract void 		 moveUp(FNode node);
  public    abstract void 		 moveDown(FNode node);
  //public abstract boolean deleteNode(FTreeTableModel treeTableModel, FNode node);
	
	protected N root = null;
  private boolean               allowNonLeavesDeletion        = false;
  private boolean               allowNodeNameDuplication      = false;
  private ArrayList<LevelColor> levelsColors                  = null;
  private Color                 leafColor                     = null;
  private boolean               automaticlyListenToListEvents = false;
  private Comparator<N>         comparator                    = null;
  private HashMap <O, N>        object2NodeMap                = null; 
  private HashMap <Long, N>     ref2NodeMap                   = null;//20150910 - Performance enhancements WBS entry
  private ListRemoveListener    listRemoveListener            = null;
  private boolean               rootVisible                   = true;
  private boolean               hideCode                      = false;
  private ArrayList<Character>  codeTypeByLevel               = null;
  private ArrayList<Character>  codeSeparatorByLevel          = null;
  private boolean               withLineStyle                 = true;
  private boolean               discardHideFlagInFocList      = false;
  private boolean               listListenerEnabled           = true;
  
	private int colorMode = COLOR_MODE_PREDEFINED;
  public final static int COLOR_MODE_GRADIENT   = 0;
  public final static int COLOR_MODE_PREDEFINED = 1;
  public final static int COLOR_MODE_NONE       = 2;         
  private boolean sortable = true;
  
  private HashMap<Object, Boolean> childrenAllowed_ItemIdBooleanMap = null;
  
  //public abstract FTree createSimilarTreeFromFocList(FocList list);
  
  public static final int NO_DISPLAY_CODE_ID = FField.NO_FIELD_ID;
  
  public static char CODE_NUMERIC       = '1';
  public static char CODE_ALPHA_CAPITAL = 'A';
  public static char CODE_ALPHA_SMALL   = 'a';

  public FTree(){
    this(COLOR_MODE_PREDEFINED);
  }

  public FTree(boolean isRootVisible){
    this(COLOR_MODE_PREDEFINED, isRootVisible);
  }

  public FTree(int colorMode){
  	this(colorMode, true);
  }

  public FTree(int colorMode, boolean isRootVisible){
    levelsColors = null;
    setColorMode(colorMode);
    this.rootVisible = isRootVisible;
  }

  public void dispose(){
    if(root != null){
      root.dispose();
      root = null;
    }
    comparator = null;
    object2NodeMap = null;
    ref2NodeMap = null;
    if(listRemoveListener != null && getFocList() != null){
    	getFocList().removeFocListener(listRemoveListener);
    }
    listRemoveListener = null;
    if(childrenAllowed_ItemIdBooleanMap != null){
    	childrenAllowed_ItemIdBooleanMap.clear();
    	childrenAllowed_ItemIdBooleanMap = null;
    }
	}
  
  public boolean isWithLineStyle() {
		return withLineStyle;
	}

  public void setWithLineStyle(boolean withLineStyle) {
		this.withLineStyle = withLineStyle;
	}

  public void resetObject2NodeMap(){
  	if(object2NodeMap != null){
  		object2NodeMap.clear();
  		object2NodeMap = null;
  	}
  }
  
  public void resetRef2NodeMap(){
  	if(ref2NodeMap != null){
  		ref2NodeMap.clear();
  		ref2NodeMap = null;
  	}
  }
  
  public boolean isAllowNodeNameDuplication() {
    return allowNodeNameDuplication;
  }

  public void setAllowNodeNameDuplication(boolean allowNodeNameDuplication) {
    this.allowNodeNameDuplication = allowNodeNameDuplication;
  }
  
  public boolean isHideCode(){
  	return hideCode;
  }

  public void setHideCode(boolean hideCode){
  	this.hideCode = hideCode;
  }

  public boolean isRootVisible() {
		return rootVisible;
	}
  
  public void setRootVisible(boolean visible){
  	rootVisible = visible;
  }
  	
	public boolean isAutomaticlyListenToListEvents() {
		return automaticlyListenToListEvents;
	}
  
	public void setAutomaticlyListenToListEvents(boolean automaticlyListenToListEvents) {
		this.automaticlyListenToListEvents = automaticlyListenToListEvents;
	}

  public String getUniqueCodeFromList(){
    String uniqueCode = null;
    int codeField = getDisplayCodeFieldID();
    if(codeField != FField.NO_FIELD_ID){
	    FocList list = getFocList();
	    for( int i = 65; i < 91 && uniqueCode == null; i++ ){
	      for( int j = 65; j < 91 && uniqueCode == null; j++ ){
	        char c1 = (char)i;
	        char c2 = (char)j;
	        String code = ""+c1+c2;
	        FocObject obj = list.searchByPropertyStringValue(getDisplayCodeFieldID(), code, true);
	        if( obj == null ){
	          uniqueCode = code;
	        }
	      }
	    }
    }
    return uniqueCode;
  }
  	
	private HashMap <O, N> getObject2NodeMap(boolean create){
		if(object2NodeMap == null && create){
			object2NodeMap = new HashMap <O, N> ();
			scan(new TreeScanner<N>(){
				public void afterChildren(N node) {
					FTree tree = node.getTree();
					if(tree != null){
						tree.putObject2NodeMapping(node.getObject(), node);
					}
				}

				public boolean beforChildren(N node) {
					return true;
				}
			});
		}
		return object2NodeMap;
	}

	private HashMap <Long, N> getRef2NodeMap(boolean create){
		if(ref2NodeMap == null && create){
			ref2NodeMap = new HashMap <Long, N> ();
			scan(new TreeScanner<N>(){
				public void afterChildren(N node) {
					FTree tree = node.getTree();
					if(tree != null){
						tree.putRef2NodeMapping(node.getObject(), node);
					}
				}

				public boolean beforChildren(N node) {
					return true;
				}
			});
		}
		return ref2NodeMap;
	}
	
	public N findNode_UsingMapOnly(Object obj){
		HashMap <O, N> map = getObject2NodeMap(true);
		N node = map != null ? map.get(obj) : null;
		return node;
	}
	
	public N findNode(Object obj){
		N node = findNode_UsingMapOnly(obj);
		if(node != null && node.getObject() != obj){
			node = findNode_FullScan(obj);
		}
		
		//BDebug
		if(node != null && ((FocObject)node.getObject()).getReference() != null && ((FocObject)node.getObject()).getReference().getLong() != ((FocObject)obj).getReference().getLong()){
			Globals.logString(" FOUND NODE NOT MATCHING !!! !!!");
		}
		if(node != null && node.getObject() != obj){
			HashMap <O, N> map = getObject2NodeMap(true);
			Globals.logString(" FOUND NODE NOT MATCHING !!!");
			node = map != null ? map.get(obj) : null;
			boolean result = obj.equals(node.getObject());
			Globals.logString(" EQUALS " +result+" obj.hashcode()="+obj.hashCode()+" node.getObject().hashcode()="+node.getObject().hashCode());
			
			node = map != null ? map.get(obj) : null;
			
			Globals.logString(" MAP CONTENT :");
			Iterator iter = map.keySet().iterator();
			while(iter != null && iter.hasNext()){
				FocObject fo = (FocObject) iter.next();
				Globals.logString("   "+fo.getReference().getLong()+" hashcode="+fo.hashCode());
			}
		}
		//EDebug		
		return node;
	}

	//Temp
  private N findNode_FullScan(final Object obj){
  	final Object[] foundNode = new Object[1];
  	foundNode[0] = null;
  	scan(new TreeScanner<FObjectNode>(){
			public void afterChildren(FObjectNode node) {
				if(node.getObject() == obj){
					foundNode[0] = node;
				}
			}

			public boolean beforChildren(FObjectNode node) {
				return foundNode[0] == null;
			}
  	});
  	return (N) foundNode[0];
  }
	//Temp

  public N findNode_UsingMapOnly(int objRef){
		HashMap <Long, N> map = getRef2NodeMap(true);
		N node = map != null ? map.get(objRef) : null;
		return node;
	}

  public N findNode(final long objRef){
  	N node = findNode_UsingMapOnly(objRef);
  	
  	if(node == null || (node.getObject() != null && ((FocObject)node.getObject()).getReferenceInt() != objRef)){
  		node = findNode_FullScan(objRef);
  	}
  	
  	return node;
  }

  public N findNode_FullScan(final long objRef){
  	final Object[] foundNode = new Object[1];
  	foundNode[0] = null;
  	scan(new TreeScanner<N>(){
			public void afterChildren(N node) {
				if(node.getObject() != null && ((FocObject)node.getObject()).getReference().getLong() == objRef){
					foundNode[0] = node;
				}
			}

			public boolean beforChildren(N node) {
				return foundNode[0] == null;
			}
  	});
  	return (N) foundNode[0];
  }

  //This one takes into account the Tree special property
	public N getNodeByDisplayTitle(String nodeTitle){
		N root = getRoot();
		N node = null;
		if(root.getDisplayTitle().equals(nodeTitle)){
			node = root;
		}
		for(int i=0; root != null && i<root.getChildCount() && node == null; i++){
			N currNode = (N) root.getChildAt(i);
			if(currNode != null){
				node = (N) currNode.getNodeByDisplayTitle(nodeTitle);
			}
		}
		return node;
	}

	public N getNodeByTitle(String nodeTitle){
		N root = getRoot();
		N node = null;
		if(root.getTitle().equals(nodeTitle)){
			node = root;
		}
		for(int i=0; root != null && i<root.getChildCount() && node == null; i++){
			N currNode = (N) root.getChildAt(i);
			if(currNode != null){
				node = (N) currNode.getNodeByTitle(nodeTitle);
			}
		}
		return node;
	}

	public N getNodeByCode(String nodeTitle){
		N root = getRoot();
		N node = null;
		for(int i=0; root != null && i<root.getChildCount() && node == null; i++){
			N currNode = (N) root.getChildAt(i);
			if(currNode != null){
				node = (N) currNode.getNodeByCode(nodeTitle);
			}
		}
		return node;
	}

	public N getNodeByFocObjectProperty(int fieldId, Object objectValue){
		N root = getRoot();
		N node = null;
		for(int i=0; root != null && i<root.getChildCount() && node == null; i++){
			N currNode = (N) root.getChildAt(i);
			if(currNode != null){
				node = (N) currNode.getNodeByFocObjectProperty(fieldId, objectValue);
			}
		}
		return node;
	}

	public void putObject2NodeMapping(Object obj, N node){
		if(obj != null && node != null){
			HashMap <Object, N> map = (HashMap <Object, N>) getObject2NodeMap(false);
			if(map != null){
				//BDegbug
				/*
				String str = " - Putting :";
				if(obj != null){
					str += ((FocObject)obj).getReference().getInteger() + " -> " + ((FocObject)node.getObject()).getReference().getInteger()+" hashcode="+obj.hashCode();  
				}
				Globals.logString(str);
				*/
				//EDegbug				
				map.put(obj, node);
			}
		}
	}

	public void putRef2NodeMapping(Object obj, N node){
		if(obj != null && node != null){
			HashMap <Long, N> map = (HashMap <Long, N>) getRef2NodeMap(false);
			if(map != null){
				//BDegbug
				/*
				String str = " - Putting :";
				if(obj != null){
					str += ((FocObject)obj).getReference().getInteger() + " -> " + ((FocObject)node.getObject()).getReference().getInteger()+" hashcode="+obj.hashCode();  
				}
				Globals.logString(str);
				*/
				//EDegbug				
				try{
					map.put(((FocObject)obj).getReferenceInt(), node);
				}catch(Exception e){
					Globals.logExceptionWithoutPopup(e);
				}
			}
		}
	}

	public void removeObject2NodeMapping(Object obj){
		if(obj != null){
			HashMap <Object, N> map = (HashMap <Object, N>) getObject2NodeMap(false);
			if(map != null){
				map.remove(obj);
			}
		}
	}

  public Color getColorForNode(FNode node, int row, FGTreeInTable treeInTable ){
  	Color color = null;
  	if(node != null){
  		if(node.isLeaf()){
  			if(leafColor == null) leafColor = new Color(240,	240,	240);
	    	color = leafColor;
  		}else{
	    	int level = node.getNodeDepth();
	    	color = getBackgroundColorForLevel(level);
	    }
  	}    
    return color;
  }

  private ArrayList<LevelColor> getLevelsColors(boolean create){
  	if(levelsColors == null && create) levelsColors = new ArrayList<LevelColor>();
  	return levelsColors;
  }

  private LevelColor getLevelColorAt(int level){
  	LevelColor levelColor = null;
  	ArrayList<LevelColor> levelsColors = getLevelsColors(true);
  	if(level < 0) level = 0;
    if(level < levelsColors.size()){
    	levelColor = levelsColors.get(level);
    }else if(level == levelsColors.size()){
    	levelColor = new LevelColor(FColorProvider.getColorAt(level), Color.black);
    	levelsColors.add(levelColor);
		}
    return levelColor;
  }
  
  public void setBackgroundColorForLevel(int level, Color color){
  	LevelColor levelColor = getLevelColorAt(level);
  	levelColor.setBackground(color);
  }

  public void setForegroundColorForLevel(int level, Color color){
  	LevelColor levelColor = getLevelColorAt(level);
  	levelColor.setForeground(color);
  }

  public Color getBackgroundColorForLevel(int level){
  	Color color = null;
  	if(getColorMode() == COLOR_MODE_NONE){
  		
  	}else{
	  	if(getLevelsColors(false) == null){
	  		color = FColorProvider.getColorAt(level);
	  	}else{
	  		if(levelsColors.size() > 0){
			    if(level > levelsColors.size()-1){
			      return levelsColors.get(levelsColors.size()-1).getBackground();
			    }
			    color = levelsColors.get(level).getBackground();
	  		}
	  	}
  	}
    return color;
  }
  
  public void initLevelsColors(){
	  if(getColorMode() == FTree.COLOR_MODE_GRADIENT){
	    int r    = 141;
	    int g    = 179;
	    int b    = 255;   
	    int step = (255 - 141) / (getDepthVisibilityLimit());
	    for(int i=0; i< getDepthVisibilityLimit()+1; i++){
	      int c = i ;
	      Color color = new Color(Math.min(r + c * step, 255), Math.min(g + c * step, 255), Math.min(b + c * step, 255));
	      setBackgroundColorForLevel(c, color);
	    }  
	  }
  }
	
	public N getRoot(){
		return root;
	}  
	
	public void scan(TreeScanner scanner){
		if(scanner != null){
			FINode root = getRoot();
			if(root != null){
				root.scan(scanner);
			}
		}
	}

	public void scanVisible(TreeScanner scanner){
		if(scanner != null){
			FINode root = getRoot();
			if(root != null){
				root.scanVisible(scanner);
			}
		}
	}

  public boolean isAllowNonLeavesDeletion() {
    return allowNonLeavesDeletion;
  }
  
  public void setAllowNonLeavesDeletion(boolean allowNonLeavesDeletion) {
    this.allowNonLeavesDeletion = allowNonLeavesDeletion;
  }
  
  public int getColorMode() {
    return colorMode;
  }
  
   public void setColorMode(int colorMode) {
    this.colorMode = colorMode;
    initLevelsColors();
  }
  
  public boolean isSortable(){
  	return this.sortable;
  }
  
  public void setSortable(boolean sortable){
  	this.sortable = sortable;
  }
  
  public void setSortable(Comparator<N> comparator){
  	this.comparator = comparator;
  	setSortable(true);
  }
  
  public Comparator<N> getComparator(){
  	if(this.comparator == null){
  		this.comparator = getDefaultComparator(); 		
  	}
  	return this.comparator;
  }
  
  protected Comparator<N> getDefaultComparator(){
  	return new Comparator<N>(){
  		public int compare(N node1, N node2){
  	  	return node1.getTitle().compareTo(node2.getTitle());
  	  }
  	}; 
  }
  
  public void sort(){
  	if(isSortable()){
	  	scan(new TreeScanner<FNode>(){
				public void afterChildren(FNode node){
					if(node.isSortable()){
						node.sortChildren();
					}
				}
				public boolean beforChildren(FNode node){
					return true;
				}
	  	});
  	}
  }
  
  public void clearTree(){
  	FNode rootNode = (FNode)getRoot();
  	if(rootNode != null && rootNode.getChildCount() > 0){
  		rootNode.dispose();
  	}
  	resetObject2NodeMap();
  }
  
  public boolean deleteNode(FTreeTableModel treeTableModel, FNode node) {
  	return deleteNode(treeTableModel, node, true);
  }
  
  public boolean vaadin_DeleteNode(FNode node) {
    return deleteNode(null, node, false);
  }
  
  public boolean deleteNode(FTreeTableModel treeTableModel, FNode node, boolean displayMessage) {
    FocObject focObj  = (FocObject)node.getObject();
    FocList   focList = getFocList();
    
    boolean deleted = false;
    try {
      if(focObj.isDeletable()){
        if(Globals.getDisplayManager() != null){
          Globals.getDisplayManager().removeLockFocusForObject(focObj);
        }
        StringBuffer message = new StringBuffer();

        int refNbr = focObj.referenceCheck_GetNumber(message);
        if(refNbr > 0){
          focObj.referenceCheck_PopupDialog(refNbr, message);
        }else{
          if (focObj != null) {
          	int dialogRet = JOptionPane.YES_OPTION; 
          	if(displayMessage){
	            dialogRet = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
	                "Confirm item deletion",
	                "01Barmaja - Confirmation",
	                JOptionPane.YES_NO_OPTION,
	                JOptionPane.WARNING_MESSAGE,
	                null);
          	}
            
            switch(dialogRet){
            case JOptionPane.YES_OPTION:
              //We want to set the object status to deleted.
              //But since we might have removed the father status for autonomy reasons
              //We put it again for the moment just to make the list react
              AccessSubject fatherSubject = focObj.getFatherSubject();
              if(fatherSubject != focList){
                focObj.setFatherSubject(focList);
              }

              
              DeleteScanner deleteScanner = new DeleteScanner(getFocList());
              node.scan(deleteScanner);
              StringBuffer errorMessage = deleteScanner.canDeleteAll();
              
              if(errorMessage.toString().isEmpty()){
                deleteScanner.doDelete();
                deleteScanner.dispose();
              }else{
                Globals.showNotification("This item cannot be deleted. ", "", IFocEnvironment.TYPE_ERROR_MESSAGE);
              }
              //deleteDescendents(node);
              
              if(fatherSubject != focList){
                focObj.setFatherSubject(fatherSubject);
              }
              
              deleted = true;
              if(treeTableModel != null) treeTableModel.refreshGui();
              break;
            case JOptionPane.NO_OPTION:
              break;
            }
          }
        }
      }else{
        Globals.showNotification("This item cannot be deleted.", "For further assistance please call 01Barmaja.", IFocEnvironment.TYPE_ERROR_MESSAGE);
//        Globals.getApp().getDisplayManager().popupMessage("This item cannot be deleted.\nFor further assistance please call 01Barmaja.");
      }
    }catch (Exception e2) {
      Globals.logException(e2);
    }
    return deleted;
  }

  public class DeleteScanner implements TreeScanner<FNode>{
  	private ArrayList<FocObject> toDelete = null;
  	private FocList              list     = null;
  	
  	public DeleteScanner(FocList list){
  		toDelete = new ArrayList<FocObject>();
  		this.list = list;
  	}
  	
  	public StringBuffer canDeleteAll(){
  	  StringBuffer message = new StringBuffer();
  	  int count = 0;
  	  for(int i=0; i<toDelete.size() && count <= 3; i++){
  	    FocObject obj = toDelete.get(i);
  	    
  	    if(obj.referenceCheck_GetNumber(message) > 0){
  	      message.append("This Object is referenced by another object.");
  	      count++;
  	    }else if(!obj.isDeletable()){
  	      message.append("Object Ref " + obj.getReference().getLong() + obj.isDeletable() + " cannot be deleted");
  	      count++;
  	    }
  	    
	    }
  	  
  	  return message;
  	}
  	
  	public void doDelete(){
  		for(int i=0; i<toDelete.size(); i++){
  			FocObject obj = toDelete.get(i);
  			//Globals.logString(" OBJ["+i+"] = "+obj.getReference().getInteger());
  			obj.setDeleted(true);
  			//list.remove(obj);
  		}
  	}
  	
  	public void dispose(){
  		if(toDelete != null){
	  		toDelete.clear();
	  		toDelete = null;
  		}
  		list = null;
  	}
  	
		public void afterChildren(FNode node) {
			FocObject obj = (FocObject)node.getObject();
			if(obj != null){
				toDelete.add(obj);
			}
		}

		public boolean beforChildren(FNode node) {
			return true;
		}
  }
  
  private void deleteDescendents(FNode node){
    node.dispose(true);
  }
  
  public ArrayList<FNode> getLeafList(FNode node){
    final ArrayList<FNode> leafList = new ArrayList<FNode>();
    node.scan(new TreeScanner<FNode>(){
      public void afterChildren(FNode node) {
        if(node.isLeaf()){
          leafList.add(node);
        }
      }
      public boolean beforChildren(FNode node) {
        return true;
      }
    });
    return leafList;
  }
    
  public N findNodeFromFocObject(FocObject focObject){
  	N node = findNode(focObject);
  	if(node == null){
	    final FocObject focObj = focObject;
	    final ArrayList<N> nodeList = new ArrayList<N>(1); 
	    scan(new TreeScanner<N>(){
	      public void afterChildren(N node) {
	        if(node.getObject() != null && node.getObject().equals(focObj)){
	          nodeList.add(node);
	        }
	      }
	      public boolean beforChildren(N node) {
	        return true;
	      }
	    });
	    node = nodeList.size() > 0 ? (N)nodeList.get(0) : null;
  	}
    return node;
  }
  
  public IFocListFilter getFocListFilter(){
    IFocListFilter focListFilter = null;
    FocList focList = getFocList();
    if(focList instanceof FocListWithFilter){
      focListFilter = ((FocListWithFilter)focList).getFocListFilter();
    }
    return focListFilter; 
  }
  
  public void resetVisibleChildren(){
    scan(new TreeScanner<FNode>(){

      public void afterChildren(FNode node) {
        node.resetVisibleChildren();
      }

      public boolean beforChildren(FNode node) {
        return true;
      }
      
    });
  }
  
  public class ListRemoveListener implements FocListener{
	  public void focActionPerformed(FocEvent evt) {
	    if(isListListenerEnabled()){
  	    switch(evt.getID()){
  	    case FocEvent.ID_ITEM_REMOVE:
  	      removeNode((FocObject)evt.getEventSubject());
  	      break;
  	    case FocEvent.ID_ITEM_ADD:
  	      addFocObject((O)evt.getEventSubject());
  	      break;
  	    }
	    }
	  }
	  
		public void dispose() {
		}
  }
  
  @SuppressWarnings("unchecked")
  private void removeNode(FocObject focObject){
    HashMap <Object, FNode> objectToMap = (HashMap<Object, FNode>) getObject2NodeMap(true);
    FNode node = objectToMap.get(focObject);
    if(node != null){
      FNode fatherNode = node.getFatherNode();
      if(fatherNode != null){
      	fatherNode.removeChild(node);
      }
    }
  }

  public void removeFocListenerToFocList(){
  	if(listRemoveListener != null){
  		getFocList().removeFocListener(listRemoveListener);
  	}
  }

  public void addFocListenerToFocList(){
  	if(listRemoveListener == null){
  		listRemoveListener = new ListRemoveListener();
  	}
  	removeFocListenerToFocList();
    getFocList().addFocListener(listRemoveListener);
  }
  
  protected String printDebug_GetNodeDebugExpression(FNode node){
  	return node.getTitle();
  }
  
  private void printDebug_Node(FNode node, int indent){
		String str = "";
		for(int i=0; i<indent; i++) str += " "; 
  	Globals.logString(str+printDebug_GetNodeDebugExpression(node));
  	
  	for(int i=0; i<node.getVisibleChildCount(); i++){
  		FNode n = node.getVisibleChildAt(i);
  		printDebug_Node(n, indent+1);
  	}
  }
  
  public void printDebug(){
  	FNode node = getRoot();
  	printDebug_Node(node, 0);
  	/*
  	scan(new TreeScanner<FNode>(){
  		int indent = 0;
			public void afterChildren(FNode node) {
				indent--;
			}

			public boolean beforChildren(FNode node) {
				String str = "";
				for(int i=0; i<indent; i++) str += " "; 
				Globals.logString(str+node.getTitle());
				indent++;
				return true;
			}
  	});
  	*/
  }
  
  public void resetCodes(){
  	if(getDisplayCodeFieldID() != FField.NO_FIELD_ID){
  		scan(new TreeScanner<FNode>(){
  			ArrayList<String> prefix = new ArrayList<String>();
  			
				public void afterChildren(FNode node) {
					prefix.remove(prefix.size()-1);
				}

				public boolean beforChildren(FNode node) {
					String code = (prefix.size() > 0) ? prefix.get(prefix.size()-1) : ""; 
					code += node.getCodeSection();
					node.setCode(code);
					prefix.add(code);
					return true;
				}
  		});
  	}
  }
  
  public void setCodeSectionTypeAndSeparators(String expression){
  	if(expression != null && !expression.isEmpty()){
	  	codeTypeByLevel      = new ArrayList<Character>();
	  	codeSeparatorByLevel = new ArrayList<Character>();
	  	for(int i=0; i<expression.length(); i++){
	  		char c = expression.charAt(i);
	  		if(c == CODE_NUMERIC || c == CODE_ALPHA_CAPITAL || c == CODE_ALPHA_SMALL){
	  			codeTypeByLevel.add(c);
	  		}else{
	  			codeSeparatorByLevel.add(c);
	  		}
	  		
	  		if(codeTypeByLevel.size() >= codeSeparatorByLevel.size() + 2){
	  			codeSeparatorByLevel.add((char)0);
	  		}
	  	}
  	}
  }
  
  public char getCodeSectionSeparatorForNodeLevel(FNode node, int level){
  	char separator = 0;
  	if(!node.getCode().isEmpty()){
	  	if(codeSeparatorByLevel != null){
	  		int index = level - 1;
	  		if(index >= codeSeparatorByLevel.size()) index = codeSeparatorByLevel.size() - 1;
	  		separator = codeSeparatorByLevel.get(index).charValue();
	  	}else{
				if(level >= 4 && !node.getCode().isEmpty()){
					separator = '.';
				}
	  	}
  	}
		return separator;
  }
  
  public char getCodeSectionTypeForNodeLevel(FNode node, int level){
  	char codeType = CODE_NUMERIC;
  	if(codeTypeByLevel != null){
  		int index = level - 1;
  		if(index >= codeTypeByLevel.size()) index = codeTypeByLevel.size() - 1;
  		codeType = codeTypeByLevel.get(index).charValue();
  	}else{
	  	switch(level){
	  	case 1:
	  		codeType = CODE_ALPHA_CAPITAL;
	  		break;
	  	case 3:
	  		codeType = CODE_ALPHA_SMALL;
	  		break;
	  	default:
	  		codeType = CODE_NUMERIC;
	  		break;
	  	}
  	}
	  return codeType;
  }
  
  public String getCodeDisplayString_ForTreeRenderer(N node){
    String code = null;
    if(getDisplayCodeFieldID() != FTree.NO_DISPLAY_CODE_ID){
      FocObject focObj = (FocObject)node.getObject();
      if(focObj != null){
      	code = focObj.getPropertyString(getDisplayCodeFieldID());
	      if(code != null && !code.equals("")){
	        code += " - ";  
	      }
      }
    }
    return code;
  }
  
  public Color getForegroundForNode(FNode node){
  	Color color = Color.black;
  	if(node != null){
  		LevelColor lc = getLevelColorAt(node.getNodeDepth());
  		if(lc != null){ 
  			color = lc.getForeground();
  		}
  	}
  	return color;
  }
  
  public class LevelColor {
  	private Color background = null;
		private Color foreground = null;

  	public LevelColor(Color background, Color foreground){
  		this.background = background;
  		this.foreground = foreground;
  	}
  	
  	public void dispose(){
  		background = null;
  		foreground = null;
  	}

  	public Color getBackground() {
			return background;
		}

		public void setBackground(Color background) {
			this.background = background;
		}

		public Color getForeground() {
			return foreground;
		}

		public void setForeground(Color foreground) {
			this.foreground = foreground;
		}
  }

	public boolean isDiscardHideFlagInFocList() {
		return discardHideFlagInFocList;
	}
	public void setDiscardHideFlagInFocList(boolean discardHideFlagInFocList) {
		this.discardHideFlagInFocList = discardHideFlagInFocList;
	}
	
	public boolean isWithNodeCollapseSaving(){
		boolean b = false;
		FocList list = getFocList();
		FocDesc desc = list != null ? list.getFocDesc() : null;
		if(desc != null){
			b = desc.isWithNodeCollapseField();
		}
		return b; 
	}
	
	//------------------------------------------------------
	// Vaadin Hierarchical
	//------------------------------------------------------
	
  @Override
  public Collection<?> getChildren(Object itemId) {
    //FocObject focObj = (FocObject) getItem(itemId);
    FNode node = vaadin_FindNode(itemId);
    ArrayList<Long> array = null; 
    if(node.getChildCount() > 0 && node.getNodeDepth() < getDepthVisibilityLimit()){
      array = new ArrayList<Long>();
      for(int i=0; i<node.getVisibleChildCount(); i++){
        FNode cNode = node.getVisibleChildAt(i);
        array.add((((FocObject) cNode.getObject()).getReference().getLong()));
      }
    }
    
    return array;
  }

  @Override
  public Object getParent(Object itemId) {
//    FocObject focObj = (FocObject) getItem(itemId);
//    return (focObj != null && focObj.getFatherObject() != null) ? focObj.getFatherObject().getReference().getInteger() : null;
    
    //FocObject focObj = (FocObject) getItem(itemId);
    FNode node = vaadin_FindNode(itemId);
    FNode fatherNode = node != null ? node.getFatherNode() : null;
    FocObject fathrFocObject = fatherNode != null ? (FocObject) fatherNode.getObject() : null;
    return (fathrFocObject != null) ? fathrFocObject.getReference().getLong() : null;
  }

  /*
  @Override
  public Collection<?> rootItemIds() {
    ArrayList<Integer> array = new ArrayList<Integer>();    
//    FocObject obj = getRoot() != null ? (FocObject) getRoot().getObject() : null;
//    if(obj != null){
//      array.add(obj.getReference().getInteger());
//    }else{
      for(int i=0; i<getFocList().size(); i++){
        FocObject focObj = getFocList().getFocObject(i);
        if(focObj.getFatherObject() == null){
          array.add(focObj.getReference().getInteger());
        }
      }
//    }
    return array;
  }
  */
  
  @Override
  public Collection<Long> rootItemIds() {
  	ArrayList<Long> array = new ArrayList<Long>();

  	FocObject obj = getRoot() != null ? (FocObject) getRoot().getObject() : null;
  	if(obj != null && obj.getReference().getLong() != 0){
  		array.add(obj.getReference().getLong());
  	}else if(getRoot() != null){
  		for(int i=0; i<getRoot().getChildCount(); i++){
  			FNode node = getRoot().getChildAt(i);
  			obj = (FocObject) node.getObject();
  			if(obj != null){
  				array.add(obj.getReference().getLong());
  			}
  		}
  	}
  	for(int i=0; i<getRoot().getChildCount(); i++){
  		if(getRoot() != null){
  			FocObject childObj = (FocObject) getRoot().getChildAt(i).getObject();

  			if(childObj != null){
  				array.add(childObj.getReference().getLong());
  			}
  		}
  	}
  	return array;
  }


  @Override
  public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  /*@Override
  public boolean areChildrenAllowed(Object itemId) {
    //FocObject focObj = (FocObject) getItem(itemId);
    FNode node = vaadin_FindNode(itemId);
    int nbrChildren = node != null ? node.getVisibleChildCount() : 0;
    return nbrChildren > 0 && node.getNodeDepth() < getDepthVisibilityLimit();
  }*/
  
  @Override
  public boolean areChildrenAllowed(Object itemId) {
    FNode node = vaadin_FindNode(itemId);
    int nbrChildren = node != null ? node.getVisibleChildCount() : 0;
    boolean levelConditon      = nbrChildren > 0 && node.getNodeDepth() < getDepthVisibilityLimit();
    boolean attributeCondition = areChildrenAllowed_ByAttributeCondition(itemId);
    return levelConditon && attributeCondition;
  }

  private boolean areChildrenAllowed_ByAttributeCondition(Object itemId){
  	boolean val = true;
  	HashMap<Object, Boolean> map = getChildrenAllowedMap(false);
  	if(map != null){
  		Boolean valObject = map.get(itemId);
  		if(valObject != null){
  			val = valObject.booleanValue();
  		}
  	}
  	return val;
  }
  
  @Override
  public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
  	boolean value = true;
  	if(itemId != null){
  		getChildrenAllowedMap(true).put(itemId, areChildrenAllowed);
  	}
  	return value;
  }
  
  private HashMap<Object, Boolean> getChildrenAllowedMap(boolean create){
  	if(childrenAllowed_ItemIdBooleanMap == null && create){
  		childrenAllowed_ItemIdBooleanMap = new HashMap<Object, Boolean>();
  	}
  	return childrenAllowed_ItemIdBooleanMap;
  }

  public FNode vaadin_FindNode(Object itemId){
    FNode node = null;
    try{
      Long integ = (Long) itemId;
      node = findNode(integ.longValue());
    }catch(Exception e){
      Globals.logException(e);
    }
    
    return node;
  }
  
  @Override
  public boolean isRoot(Object itemId) {
    //FocObject focObj = (FocObject) getItem(itemId);
    FNode node = vaadin_FindNode(itemId);
    boolean isRoot = node != null ? (node.isRoot() || node.getFatherNode().getObject() == null) : true; 
    return isRoot;
  }

  @Override
  public boolean hasChildren(Object itemId) {
    //FocObject focObj = (FocObject) getItem(itemId);
    FNode node = vaadin_FindNode(itemId);
    int nbrChildren = node != null ? node.getVisibleChildCount() : 0;
    return nbrChildren > 0 && node.getNodeDepth() < getDepthVisibilityLimit();
  }

  @Override
  public boolean removeItem(Object itemId) throws UnsupportedOperationException {
    return getFocList() != null ? getFocList().removeItem(itemId) : null;
  }

  @Override
  public Item getItem(Object itemId) {
    return getFocList() != null ? getFocList().getItem(itemId) : null;
  }

  @Override
  public Collection<?> getContainerPropertyIds() {
    return getFocList() != null ? getFocList().getContainerPropertyIds() : null;
  }

  /*
  private ReferencesCollection referencesCollection_Vaadin = null;
  private ReferencesCollection getReferencesCollection_Vaadin(){
    if(referencesCollection_Vaadin == null){
      referencesCollection_Vaadin = new ReferencesCollection(this);
    }
    return referencesCollection_Vaadin; 
  }
  */
  
  @Override
  public Collection<?> getItemIds() {
  	final ArrayList<Long> arrayRefs = new ArrayList<Long>(); 
  	
  	scan(new TreeScanner<FNode>() {

			@Override
			public boolean beforChildren(FNode node) {
				boolean toAdd = true;
				
				if(node == null || node.getObject() == null || node.getNodeDepth() >= getDepthVisibilityLimit() || (node.isRoot() && !isRootVisible())){
					toAdd = false;
				}
				
				if(toAdd){
					long ref = ((FocObject)node.getObject()).getReference().getLong();
					if(ref != 0){
						arrayRefs.add(ref);
					}
				}
				
//				if(node.getObject() != null && node.getNodeDepth() < getDepthVisibilityLimit()){
//					int ref = ((FocObject)node.getObject()).getReference().getInteger();
//					if(ref != 0){
//						arrayRefs.add(ref);
//					}
//				}
				return true;
			}

			@Override
			public void afterChildren(FNode node) {
			}
  		
		});
  	
  	return arrayRefs;
    //return getFocList() != null ? getFocList().getItemIds() : null;
  }

  @Override
  public Property getContainerProperty(Object itemId, Object propertyId) {
    return getFocList() != null ? getFocList().getContainerProperty(itemId, propertyId) : null;
  }

  @Override
  public Class<?> getType(Object propertyId) {
    return getFocList() != null ? getFocList().getType(propertyId) : null;
  }

  @Override
  public int size() {
    return getFocList() != null ? getFocList().size() : null;
  }

  @Override
  public boolean containsId(Object itemId) {
    return getFocList() != null ? getFocList().containsId(itemId) : null;
  }

  @Override
  public Item addItem(Object itemId) throws UnsupportedOperationException {
    return getFocList() != null ? getFocList().addItem(itemId) : null;
  }

  @Override
  public Object addItem() throws UnsupportedOperationException {
    return getFocList() != null ? getFocList().addItem() : null;
  }

  @Override
  public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
    return getFocList() != null ? getFocList().addContainerProperty(propertyId, type, defaultValue) : null;
  }

  @Override
  public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
    return getFocList() != null ? getFocList().removeContainerProperty(propertyId) : null;
  }

  @Override
  public boolean removeAllItems() throws UnsupportedOperationException {
    return getFocList() != null ? getFocList().removeAllItems() : null;
  }
  
  
  
  
  
	//-----------------------------------------------------------------------
  //-----------------------------------------------------------------------
  /*
	public class ReferencesCollection implements Collection<Integer> {
	  private FTree tree = null;
	  
	  public ReferencesCollection(FTree list){
	    this.tree = list;
	  }
	  
	  public void dispose(){
	    tree = null;
	  }
	  
    @Override
    public boolean add(Integer arg0) {
      return false;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> arg0) {
      return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean contains(Object arg0) {
      return tree != null ? tree.getFocList().searchByReference((Integer)arg0) != null : false;
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
      Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.containsAll");
      return false;
    }

    @Override
    public boolean isEmpty() {
      return tree != null ? tree.getFocList().isEmpty() : true;
    }

    @Override
    public Iterator<Integer> iterator() {
      return new Iterator<Integer>() {

        FNode   node     = null;
        boolean finished = false;
        
        @Override
        public boolean hasNext() {
          return node != null;
        }

        @Override
        public Integer next() {
        	
        	finished = 
        	
        	
        	if(finished){
        			
        	}else{
        		if(node.getChildCount() > 0) node = node.getChildAt(0);
        	}
        	
        	
          int val = 0;
          if(pos >= 0 && pos < size()){
            FocObject obj = getFocObject(pos);
            if(obj != null && obj.getReference() != null) val = obj.getReference().getInteger();
          }
          
          pos++;          
          return val;
        }

        @Override
        public void remove() {
          Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.Iterator.remove()");
        }
        
      };
    }
  */
  
  
  
  //------------------------------------------------
  // IFocData
  //------------------------------------------------
  
  @Override
  public boolean iFocData_isValid() {
    return true;
  }

  @Override
  public boolean iFocData_validate() {
    return getFocList() != null ? getFocList().iFocData_validate() : false;
  }

  @Override
  public void iFocData_cancel() {
    if(getFocList() != null){
      getFocList().iFocData_cancel();
    }
  }

  @Override
  public IFocData iFocData_getDataByPath(String path) {
    return getFocList() != null ? getFocList().iFocData_getDataByPath(path) : null;
  }
 
  @Override
  public Object iFocData_getValue() {
    return null;
  }
  //------------------------------------------------
  
  public boolean isListListenerEnabled() {
    return listListenerEnabled;
  }
  
  public void setListListenerEnabled(boolean listListenerEnabled) {
    this.listListenerEnabled = listListenerEnabled;
  }  
}
