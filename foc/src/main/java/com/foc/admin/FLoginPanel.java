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
package com.foc.admin;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.gui.FPanel;

@SuppressWarnings("serial")
public class FLoginPanel extends FPanel {
	
  private FocUserGuiDetailsPanel userDetailsLoginPanel = null;
  private boolean                firstTime             = true;

	private FLoginPanel(){

    JPanel jPanel = new JPanel() {

      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (firstTime) {
          userDetailsLoginPanel.refreshFocus();
          firstTime = false;
        }

        /*        if(logPanel == null){
         FocUser user = new FocUser(new FocConstructor(FocUser.getFocDesc(), null, null));
         user.setDbResident(false);
         logPanel = user.newDetailsPanel(LOGIN_VIEW_ID);
         }
         */
        BufferedImage backgroundImage = Globals.getIcons().getLoginPageImage();//getBackgroundImage();
        //if(backgroundImage != null) g.drawImage(backgroundImage, 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight(), this);
        Dimension dim = null;
        if(Globals.getDisplayManager() != null && Globals.getDisplayManager().getNavigator() != null){
        	dim = Globals.getDisplayManager().getNavigator().getViewportDimension();
        }
        if(backgroundImage != null && dim != null && g != null){
          //g.drawImage(backgroundImage, 0, 0, (int) dim.getWidth(), (int) dim.getHeight(), this);
        	g.drawImage(backgroundImage, 0, 0, this);
        }

        //logPanel.paint(g);
        /*
         Dimension dim = logPanel.getPreferredSize();
         Point pt = Globals.getIcons().getTopLeftPointCenteredInBackground(dim);        	
         //g.translate(-pt.x, -pt.y);
         logPanel.setBounds(10, 10, 1000, 1000);
         
         g.translate(200, 200);
         //g.copyArea(x, y, width, height, dx, dy)
         //g.setClip(0, 0, 1000, 1000);
         logPanel.update(g);
         logPanel.paint(g);
         */
      }
    };

    jPanel.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();

    c.gridwidth = 1;
    c.gridheight = 1;
    c.insets.bottom = 0;
    c.insets.top = 0;
    c.insets.left = 0;
    c.insets.right = 0;
    c.weightx = 0.99;
    c.weighty = 0.99;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.NORTHWEST;

    c.gridx = 0;
    c.gridy = 0;

    FocConstructor constr = new FocConstructor(FocUser.getFocDesc(), null, null);
    FocUser user = (FocUser) constr.newItem();
    user.setDbResident(false);
    userDetailsLoginPanel = (FocUserGuiDetailsPanel) user.newDetailsPanel(FocUser.LOGIN_VIEW_ID);

    //logPanel.add(new JTextField(30), 0, 0);

    //JLabel label = new JLabel("TEST TEST");
    //label.setOpaque(false);
    //logPanel.add(label, 0, 1);

    //logPanel.setOpaque(false);
    //logPanel.setBackground(null);

    jPanel.add(new JPanel() {

      Dimension dim = new Dimension(550, 50);

      public void paintComponent(Graphics g) {
        //super.paintComponent(g);
      }

      @Override
      public Dimension getPreferredSize() {
        return dim;
      }

    }, c);

    c.gridx = 1;
    c.gridy = 1;
    jPanel.add(userDetailsLoginPanel, c);

    c.gridx = 2;
    c.gridy = 2;
    jPanel.add(new JPanel() {
      Dimension dim = new Dimension(100, 100);

      public void paintComponent(Graphics g) {
        //super.paintComponent(g);
      }

      @Override
      public Dimension getPreferredSize() {
        return dim;
      }
    }, c);

    //    Dimension dim = logPanel.getPreferredSize();
    //    Point pt = Globals.getIcons().getTopLeftPointCenteredInBackground(dim);
    //    logPanel.setBounds(pt.x, pt.y, (int)dim.getWidth(), (int)dim.getHeight());
    //    jPanel.add(logPanel);
    //    logPanel.setBounds(pt.x, pt.y, (int)dim.getWidth(), (int)dim.getHeight());

    add(jPanel, 0, 0, 1, 1, 0.9, 0.9, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
	}
	
	public void dispose(){
		super.dispose();
		userDetailsLoginPanel = null;
	}
	
	private void setLockUserNamePassword(boolean lock){
		if(userDetailsLoginPanel != null){
			userDetailsLoginPanel.setLockComponent(lock);
		}
	}
	
	public void lockUserNamePassword(){
		setLockUserNamePassword(true);
	}

	public void unlockUserNamePassword(){
		setLockUserNamePassword(false);
	}

	private static FLoginPanel instance = null;
	public static FLoginPanel getInstance(){
		if(instance == null){
			instance = new FLoginPanel();
		}
		return instance;
	}
}
