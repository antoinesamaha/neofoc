/*
 * Created on Jan 10, 2006
 */
package com.foc.join;

import java.util.ArrayList;

import com.foc.desc.FocDesc;

/**
 * @author 01Barmaja
 */
public class TableAlias {
  private String          alias     = null; 
  private FocDesc         focDesc   = null;
  private Join            join      = null;
  private ArrayList<Join> joinArray = null;
  private int             order     = 0;

  public TableAlias(String alias, FocDesc desc){
    this.alias = alias;
    this.focDesc = desc;
    joinArray = new ArrayList<Join>();
  }
  
  public void dispose(){
  	focDesc = null;
  	if(joinArray != null){
  		for(int i=0; i<joinArray.size(); i++){
  			Join join = joinArray.get(i);
  			if(join != null){
  				join.dispose();
  			}
  		}
  		joinArray.clear();
  	}
  	if(join != null){
  		join.dispose();
  		join = null;
  	}
  }

  @Deprecated
  public void setJoin(Join join){
  	addJoin(join);
  }

  public void addJoin(Join join){
    joinArray.add(join);
    join.setTargetAlias(this);
  }

  public FocDesc getFocDesc() {
    return focDesc;
  }
  
  public String getAlias() {
    return alias;
  }

  public int getJoinCount() {
    return joinArray.size();
  }

  public Join getJoin(int i) {
    return joinArray.get(i);
  }

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
