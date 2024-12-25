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
package com.foc.access;

import java.util.Comparator;

import com.foc.desc.field.FField;
import com.foc.tree.TreeScanner;
import com.foc.tree.objectTree.FObjectTree;

@SuppressWarnings("serial")
public class FocLogLineTree extends FObjectTree<FocLogLineNode, FocLogLine> {
	
	public FocLogLineTree(){
    super(true);

    setAutomaticlyListenToListEvents(false);
    setFatherNodeId(FField.FLD_FATHER_NODE_FIELD_ID);
  }
	
	public void dispose(){
		super.dispose();
	}
	
  @Override
  protected FocLogLineNode newRootNode(){
  	return new FocLogLineRootNode("", this);
  }
	
	public void recompute(){
		scan(new TreeScanner<FocLogLineNode>(){
		  
			@Override
			public void afterChildren(FocLogLineNode node) {
			  if(node.getObject().getSuccessful()){
	        for(int i=0; i<node.getChildCount(); i++){
	          FocLogLineNode cNode = node.getChildAt(i);
	          FocLogLine     cLine = cNode.getObject();
	          if(!cLine.getSuccessful()){
	            cLine.setSuccessful(false);
	            node.getObject().setSuccessful(false);
	            node.setCollapsed(false);
	            break;
	          }
	        }
			  }
			}

			@Override
			public boolean beforChildren(FocLogLineNode node) {
				return true;
			}
		});
		
		setSortable(new Comparator<FocLogLineNode>() {
      @Override
      public int compare(FocLogLineNode n1, FocLogLineNode n2) {
        int comp = 0;
        FocLogLine line1 = n1 != null ? n1.getObject() : null;
        FocLogLine line2 = n2 != null ? n2.getObject() : null;
        if(n1 != null && n2 != null){
//          long l = line1.getDateTime().getTime() - line2.getDateTime().getTime();
//          if(l > 0) comp = 1;
//          else if(l < 0) comp = -1;
        }
        if(comp == 0){
        	comp = line1.getOrder() - line2.getOrder();
//          comp = -(line1.getReference().getInteger() - line2.getReference().getInteger());
        }
        return comp;
      }
    });
	}

	/*
	@Override
	public FProperty getTreeSpecialProperty(FocLogLineNode node) {
		FProperty property = null;
		
		BalanceAccount ba = node.getObject();
		
		if(ba.getLevel() == BalanceAccount.LEVEL_ACCOUNT){
			Account account = ba != null ? ba.getAccount() : null;
			property = account != null ? account.getFocProperty(AccountDesc.FLD_CODE) : null;
		}else if(ba.getLevel() == BalanceAccount.LEVEL_ADR_BOOK_PARTY){
			AdrBookParty account = ba != null ? ba.getAdrBookParty() : null;
			property = account != null ? account.getFocProperty(AdrBookPartyDesc.FLD_CODE_NAME) : null;
		}else if(ba.getLevel() == BalanceAccount.LEVEL_AUXILIARY_DISPLAY){
			//AdrBookParty account = ba != null ? ba.getAdrBookParty() : null;
			property = ba!= null ? ba.getFocProperty(BalanceAccountDesc.FLD_AUXILIARY_DISPLAY) : null;
		}else if(ba.getLevel() == BalanceAccount.LEVEL_WBS){
			WBS account = ba != null ? ba.getWBS() : null;
			property = account != null ? account.getFocProperty(WBSDesc.FLD_WBS_CODE) : null;
		}else if(ba.getLevel() == BalanceAccount.LEVEL_COST_CODE){
			CostCode account = ba != null ? ba.getCostCode() : null;
			property = account != null ? account.getFocProperty(CostCodeDesc.FLD_CODE) : null;			
		}else if(ba.getLevel() == BalanceAccount.LEVEL_CURRENCY){
			Currency curr = ba != null ? ba.getCurrency() : null;
			property = curr != null ? curr.getFocProperty(CurrencyDesc.FLD_NAME) : null;
		}else if(ba.getLevel() == BalanceAccount.LEVEL_JV){
			JVJoin join = ba != null ? ba.getJVJoin() : null;
			property = join != null ? join.getFocProperty(JVJoinDesc.FLD_TRANSACTION_CODE) : null;
			if(property.getString().isEmpty()){
				property = join != null ? join.getFocProperty(JVJoinDesc.FLD_CODE) : null;
			}
		}
		
		return property;
	}

  @Override
  public Icon getIconForNode(FocLogLineNode node) {
  	Icon icon = null;

    return icon;
  }
  */
	
}
