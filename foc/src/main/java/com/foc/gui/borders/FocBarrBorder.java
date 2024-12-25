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

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class FocBarrBorder extends AbstractBorder {
	/**
	 * A class which implements a line border of arbitrary thickness
	 * and of a single color.
	 * <p>
	 * <strong>Warning:</strong>
	 * Serialized objects of this class will not be compatible with
	 * future Swing releases. The current serialization support is
	 * appropriate for short term storage or RMI between applications running
	 * the same version of Swing.  As of 1.4, support for long term storage
	 * of all JavaBeans<sup><font size="-2">TM</font></sup>
	 * has been added to the <code>java.beans</code> package.
	 * Please see {@link java.beans.XMLEncoder}.
	 *
	 * @version 1.25 04/07/06
	 * @author David Kloba
	 */
    private static Border blackLine;
    private static Border grayLine;

    protected int thickness;
    protected Color lineColor;
    protected boolean roundedCorners;
    protected int radius;

    /** Convenience method for getting the Color.black LineBorder of thickness 1.
      */
    public static Border createBlackLineBorder() {
        if (blackLine == null) {
            blackLine = new FocBarrBorder(Color.black, 1);
        }
        return blackLine;
    }

    /** Convenience method for getting the Color.gray LineBorder of thickness 1.
      */
    public static Border createGrayLineBorder() {
        if (grayLine == null) {
            grayLine = new FocBarrBorder(Color.gray, 1);
        }
        return grayLine;
    }

    /** 
     * Creates a line border with the specified color and a 
     * thickness = 1.
     * @param color the color for the border
     */
    public FocBarrBorder(Color color) {
        this(color, 1, false);
    }

    /**
     * Creates a line border with the specified color and thickness.
     * @param color the color of the border
     * @param thickness the thickness of the border
     */
    public FocBarrBorder(Color color, int thickness)  {
        this(color, thickness, false);
    }

    /**
     * Creates a line border with the specified color, thickness,
     * and corner shape.
     * @param color the color of the border
     * @param thickness the thickness of the border
     * @param roundedCorners whether or not border corners should be round
     * @since 1.3
     */
    public FocBarrBorder(Color color, int thickness, boolean roundedCorners){
        lineColor = color;
        this.thickness = thickness;
        this.roundedCorners = roundedCorners;
    }

    public FocBarrBorder(Color color, int thickness, int radius){
    	this(color, thickness, true);
    	this.radius = radius;
    }
    
    /**
     * Paints the border for the specified component with the 
     * specified position and size.
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();
        int i;

	/// PENDING(klobad) How/should do we support Roundtangles?
        g.setColor(lineColor);
        for(i = 0; i < thickness; i++)  {
                g.drawLine(x, y+i, x+width, y+i);
                g.drawLine(x, y+height-i-1, x+width, y+height-i-1);
        }
        g.setColor(oldColor);
    }

    /**
     * Returns the insets of the border.
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c)       {
        return new Insets(thickness, thickness, thickness, thickness);
    }

    /** 
     * Reinitialize the insets parameter with this Border's current Insets. 
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = thickness;
        return insets;
    }

    /**
     * Returns the color of the border.
     */
    public Color getLineColor()     {
        return lineColor;
    }

    /**
     * Returns the thickness of the border.
     */
    public int getThickness()       {
        return thickness;
    }

    /**
     * Returns whether this border will be drawn with rounded corners.
     * @since 1.3
     */
    public boolean getRoundedCorners() {
        return roundedCorners;
    }

    /**
     * Returns whether or not the border is opaque.
     */
    public boolean isBorderOpaque() { 
        return !roundedCorners; 
    }

}
	
