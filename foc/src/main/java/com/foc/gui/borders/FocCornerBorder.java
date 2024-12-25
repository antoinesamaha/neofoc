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
package com.foc.gui.borders;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Component;

import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class FocCornerBorder extends LineBorder {

	public FocCornerBorder(Color color, int thickness) {
		super(color, thickness);
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Color oldColor = g.getColor();
		int i;

		g.setColor(lineColor);
		for(i = 0; i < thickness; i++){
			int x1 = x;
			int y1 = y + i;
			int x2 = x + i;
			int y2 = y;
			
			g.drawLine(x1, y1, x2, y2);
		}
		g.setColor(oldColor);
	}

	/**
	 * Returns the insets of the border.
	 * 
	 * @param c
	 *          the component for which this border insets value applies
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, 0);
	}

	/**
	 * Reinitialize the insets parameter with this Border's current Insets.
	 * 
	 * @param c
	 *          the component for which this border insets value applies
	 * @param insets
	 *          the object to be reinitialized
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = 0;
		insets.top = 0;
		insets.right = 0;
		insets.bottom = 0;
		return insets;
	}

}
	
