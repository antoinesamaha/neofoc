/*
 * Created on Jun 27, 2005
 */
package com.foc.desc.field;

import com.foc.business.multilanguage.MultiLanguage;

/**
 * @author 01Barmaja
 */
public class FFieldArrayMultilangPlug implements FFieldArrayPlug{
  public int getCurrentIndex(){
    return MultiLanguage.getCurrentLanguage().getId();    
  }
}
