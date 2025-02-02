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
package com.foc.gui;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import com.foc.*;

/**
 * JFrame object, is the only frame of the application
 */
public class FooterPanel extends JPanel {

  private JButton goBack;
  private JTextField textField;

  /**
   * Constructor, setts the title, and the window closing istener
   */
  public FooterPanel() {
    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.insets.left = 0;
    gbc.insets.right = 0;
    gbc.ipadx = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 0;
    /*
    GoBackListener goBackListener = new GoBackListener();
    this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
    this.getActionMap().put("goBack", goBackListener);
    */
    
    /*
    goBack = new JButton("Back ", Globals.getIcons().getBackIcon());
    goBack.setBorder(BorderFactory.createEtchedBorder());
    goBack.addActionListener(goBackListener);
    add(goBack, gbc);
    */
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.BOTH;
    textField = new JTextField(30);
    textField.setEditable(false);
    textField.setBorder(BorderFactory.createLoweredBevelBorder());
    add(textField, gbc);

    setBackground(Color.lightGray);
    setBorder(BorderFactory.createLoweredBevelBorder());
  }

  public void setFrameFooter(String str) {
    textField.setText(str);
  }

  private class GoBackListener extends AbstractAction implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      Globals.getDisplayManager().goBack();
    }
  }
}
