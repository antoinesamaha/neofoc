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
package com.foc.gui.image;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.foc.Globals;
import com.foc.desc.field.FImageField;
import com.foc.gui.FGFileChooser;
import com.foc.gui.FPopupMenu;
import com.foc.property.FImageProperty;
import com.foc.property.FString;

@SuppressWarnings("serial")
public class FImagePanel extends JPanel {
		
	private FPopupMenu     popupMenu     = null;
	private int            imageWidth    = 0;
	private int            imageHeight   = 0;
	private FImageProperty imageProperty = null;

	public FImagePanel(FImageProperty imageProperty) {
		FImageField imageField = (FImageField) imageProperty.getFocField();
		
		this.imageProperty = imageProperty;
		if(imageField != null){
			this.imageWidth  = imageField.getWidth();
			this.imageHeight = imageField.getHeight();
		}
		init();
	}
	
	public FImagePanel(int imageWidth, int imageHeight) {
		this.imageWidth  = imageWidth ;
		this.imageHeight = imageHeight;
		init();
	}
	
	public void init(){
		setPreferredSize(new Dimension(imageWidth, imageHeight));
		
		JMenuItem menuItem = new JMenuItem("Change image...", 'C');
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FString       stringProp  = new FString(null, 1, "");
				FGFileChooser fileChooser = new FGFileChooser(stringProp, Globals.getDisplayManager().getMainFrame(), "Choose an image file with size ("+getImageWidth()+", "+getImageHeight()+")", FGFileChooser.MODE_FILES_ONLY, null, null);
				fileChooser.popupFileChooser();
				fileChooser.dispose();
				
				String fullPath = stringProp.getString();
				if(fullPath != null && !fullPath.isEmpty()){
					if(imageProperty != null){
						imageProperty.setImageValue(fullPath);
					}
				}
			}
		});
		
		FPopupMenu popupMenu = getPopupMenu();
		popupMenu.add(menuItem);
	}
	
	public void dispose() {
		if(popupMenu != null){
			popupMenu.dispose();
			popupMenu = null;
		}
		imageProperty = null;
	}

  public FPopupMenu getPopupMenu(){
    if(popupMenu == null){
      popupMenu = new FPopupMenu();
      
      MouseListener mouseListener = new MouseAdapter(){
        public void mousePressed(MouseEvent e) {
          maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
          maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
          if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
          }
        }
      };
      
      addMouseListener(mouseListener);
    }
    return popupMenu;
  }
	
  @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(imageProperty != null && imageProperty.getImageValue() != null){
	    TexturePaint barmajaLogoPaint = new TexturePaint(imageProperty.getImageValue(), new Rectangle(0, 0, imageWidth, imageHeight));
			
	    Graphics2D g2 = (Graphics2D) g;
	    if(imageProperty.getImageValue() != null){
	      g2.setPaint(barmajaLogoPaint);
	      g2.fillRect( 0, 0, imageWidth, imageHeight);
	    }
		}
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}
}
