/*
 * Created on 14-Feb-2005
 */
package com.foc.desc.field;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ImageIcon;

import com.foc.business.multilanguage.LanguageKey;
import com.foc.business.multilanguage.MultiLanguage;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * @author 01Barmaja
 */
public class FMultipleChoiceItem implements FMultipleChoiceItemInterface, Item {
  private int         id        = 0   ;
  private String      title     = null;
  private ImageIcon   imageIcon = null;
  private LanguageKey langKey   = null;
  private String      iconPath  = null;
  private String      iconFontAwesomeName = null;

  public FMultipleChoiceItem(int id, String title, String iconPath) {
    this.id       = id      ;
    this.title    = title   ;
    this.iconPath = iconPath;
  }
  
  public FMultipleChoiceItem(int id, String title) {
    this.id      = id   ;
    this.title   = title;
    this.langKey = null ;
  }

  public FMultipleChoiceItem(int id, LanguageKey langKey) {
    this.id = id;
    this.title = null;
    this.langKey = langKey;
  }

  public FMultipleChoiceItem(int id, ImageIcon imageIcon, String title) {
    this.id        = id   ;
    this.title     = title;
    this.imageIcon = imageIcon;
    this.langKey   = null ;
  }
  
  public void dispose(){
  	imageIcon = null;
  	langKey   = null;
  }
  
  public String getIconPath(){
  	return iconPath;
  }
  
  /**
   * @return Returns the id.
   */
  public int getId() {
    return id;
  }

  /**
   * @return Returns the title.
   */
  public String getTitle() {
  	String str = title;
  	if(langKey != null){
  		str = MultiLanguage.getString(langKey);
  	}
    return str;
  }

  public ImageIcon getImageIcon(){
  	return imageIcon;
  }
  
  public String toString() {
    return getTitle();
  }

  //-----------------------------------------------------------------------
  //-----------------------------------------------------------------------
  // VAADIN
  //-----------------------------------------------------------------------
  //-----------------------------------------------------------------------
  
  private static ArrayList<String> vaadinPropertyIDArrayList = null;
  
	@Override
	public Property getItemProperty(Object id) {
		/*
		if(id.equals("id")){
			return new PropertysetItem();
		}else if(id.equals("title")){
			return title;
		}
		*/
		return null;
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		if(vaadinPropertyIDArrayList == null){
			vaadinPropertyIDArrayList = new ArrayList<String>();
			vaadinPropertyIDArrayList.add("id");
			vaadinPropertyIDArrayList.add("title");
//			vaadinPropertyIDArrayList.add("iconPath");
		}

		return vaadinPropertyIDArrayList;
	}

	@Override
	public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("FMultipleChoiceItem.addItemProperty(Object id, Property property)");
	}

	@Override
	public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("FMultipleChoiceItem.addItemProperty(Object id)");
	}
  //-----------------------------------------------------------------------
  //-----------------------------------------------------------------------

	public String getIconFontAwesomeName() {
		return iconFontAwesomeName;
	}

	public void setIconFontAwesomeName(String iconFontAwesomeName) {
		this.iconFontAwesomeName = iconFontAwesomeName;
	}
}
