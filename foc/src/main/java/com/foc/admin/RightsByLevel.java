/*
 * Created on Sep 7, 2005
 */
package com.foc.admin;

import java.util.Iterator;

import com.foc.*;
import com.foc.desc.*;
import com.foc.list.*;

/**
 * @author 01Barmaja
 */
public class RightsByLevel {
  private int nbOfLevels = 0;
  
  public RightsByLevel(int nbOfLevels){
    this.nbOfLevels = nbOfLevels;
  }
  
  public int getNbOfLevels() {
    return nbOfLevels;
  }
  
  public boolean lockValuesIfNecessary(FocObject obj){
    boolean shouldLock = obj.getThisFocDesc().getRightsByLevelMode() == FocDesc.RIGHTS_BY_LEVEL_MODE_TRACE_AND_ACCESS;
    
    if(shouldLock){
      int userLevel = Globals.getApp().getUser().getRightsLevel();
      int objLevel = obj.getRightsLevel();
      shouldLock = objLevel >= 1 && userLevel < getNbOfLevels();
      if(shouldLock){
        FocUser objUser = obj.getRightsLevelLastUser();
        shouldLock = (userLevel < objLevel) || (userLevel == objLevel && Globals.getApp().getUser().getName().compareTo(objUser.getName())!=0);
      }
    }
    
    if(shouldLock){
      obj.lockAllproperties();
    }
    return shouldLock;
  }
  
  public void lockValuesIfNecessary(FocList list){
    if(Globals.getApp().getRightsByLevel() != null){
      Iterator iter = list.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        Globals.getApp().getRightsByLevel().lockValuesIfNecessary(obj);
      }
    }
  }

}
