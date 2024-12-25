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

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.ArrayList;

public class FColorProvider {
	private static ArrayList<Color> colorArray = null;

	//private static Color COLOR_ALERT = new Color(255, 170, 170);
	//private static Color COLOR_ALERT = new Color(190, 250, 2);
	private static Color COLOR_ALERT = new Color(250, 160, 250);
	public static Color getAlertColor(){
		return COLOR_ALERT;
	}

	private static Color COLOR_COLUMN_HIGHLIGHT = Color.ORANGE;
	public static Color getColumnHighlight(){
		return COLOR_COLUMN_HIGHLIGHT;
	}

	private static ArrayList<Color> getColorArray(){
		if(colorArray == null){
			colorArray = new ArrayList<Color>();
			
			colorArray.add(new Color(166, 193, 221)); // bleu fonce
      colorArray.add(new Color(204, 227, 255)); // bleu clair
      colorArray.add(new Color(214, 255, 176)); //vert clair
      colorArray.add(new Color(230, 213, 202)); //magenta
      colorArray.add(new Color(255, 235, 155)); //orange    

      /*			
			colorArray.add(new Color(128,	208,	255));
			colorArray.add(new Color(128,	255,	255));
			colorArray.add(new Color(128,	255,	159));
			colorArray.add(new Color(191,	255,  128));
			colorArray.add(new Color(240,	255,	128));
			colorArray.add(new Color(255,	223,	128));
			colorArray.add(new Color(255,	174,	128));
			*/		
			/*
			colorArray.add(new Color(165, 165, 10));
			colorArray.add(new Color(203, 203, 20));
			colorArray.add(new Color(230, 230, 40));
			colorArray.add(new Color(255, 255, 66));

			colorArray.add(new Color(165, 165, 10));
			colorArray.add(new Color(203, 203, 20));
			colorArray.add(new Color(230, 230, 40));
			colorArray.add(new Color(255, 255, 66));
			
			colorArray.add(new Color(165, 165, 10));
			colorArray.add(new Color(203, 203, 20));
			colorArray.add(new Color(230, 230, 40));
			colorArray.add(new Color(255, 255, 66));
			
			colorArray.add(new Color(165, 165, 10));
			colorArray.add(new Color(203, 203, 20));
			colorArray.add(new Color(230, 230, 40));
			colorArray.add(new Color(255, 255, 66));
			*/
		}
		return colorArray;
	}
	
	public static Color getColorAt(int at){
		return getColorArray().get(at % getColorArray().size());
	}
	
	private static GradientPaint GP_BLUE_CLEAR = null;
	public static GradientPaint getGradientPaintBlueClear(){
		if(GP_BLUE_CLEAR == null){
			//GP_BLUE_CLEAR = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0xFF, 0xFF), 0.0f, 0.0f, new Color(0x88, 0xFF, 0xFF));
			GP_BLUE_CLEAR = new GradientPaint(0.0f, 0.0f, 
					new Color(133, 146, 168), 0.0f, 0.0f, 
					new Color(175, 192, 222));
		}
		return GP_BLUE_CLEAR;
	}
	
	private static GradientPaint GP_BLUE = null;
	public static GradientPaint getGradientPaintBlue(){
		if(GP_BLUE == null){
			//GP_BLUE = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0x22, 0xFF), 0.0f, 0.0f, new Color(0x88, 0x88, 0xFF));
			GP_BLUE = new GradientPaint(0.0f, 0.0f, 
					new Color(44, 93, 152), 0.0f, 0.0f, 
					new Color(58, 124, 203));
		}
		return GP_BLUE;
	}

	private static GradientPaint GP_RED = null;
	public static GradientPaint getGradientPaintRed(){
		if(GP_RED == null){
			//GP_RED = new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0x22, 0x22), 0.0f, 0.0f, new Color(0xFF, 0x88, 0x88));
			GP_RED = new GradientPaint(0.0f, 0.0f, 
					new Color(155, 45, 42), 0.0f, 0.0f, 
					new Color(206, 59, 55));
		}
		return GP_RED;
	}

	private static GradientPaint GP_ROSE = null;
	public static GradientPaint getGradientPaintRose(){
		if(GP_ROSE == null){
			//GP_ROSE = new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0x77, 0x77), 0.0f, 0.0f, new Color(0xFF, 0xBB, 0xBB));
			//GP_ROSE = new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0x22, 0xFF), 0.0f, 0.0f, new Color(0xFF, 0x88, 0xFF));
			GP_ROSE = new GradientPaint(0.0f, 0.0f, 
					new Color(235, 162, 161), 0.0f, 0.0f, 
					new Color(255, 195, 185));
		}
		return GP_ROSE;
	}

	private static GradientPaint GP_YELLOW = null;
	public static GradientPaint getGradientPaintYellow(){
		if(GP_YELLOW == null){
			GP_YELLOW = new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0xFF, 0x22), 0.0f, 0.0f, new Color(0xFF, 0xFF, 0x88));
		}
		return GP_YELLOW;
	}

	private static GradientPaint GP_GREEN = null;
	public static GradientPaint getGradientPaintGreen(){
		if(GP_GREEN == null){
			//GP_GREEN = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0xFF, 0x22), 0.0f, 0.0f, new Color(0x88, 0xFF, 0x88));
			GP_GREEN = new GradientPaint(0.0f, 0.0f, 
					new Color(118, 149, 53), 0.0f, 0.0f, 
					new Color(156, 199, 70));
		}
		return GP_GREEN;
	}

	private static GradientPaint GP_VIOLET = null;
	public static GradientPaint getGradientPaintViolet(){
		if(GP_VIOLET == null){
			GP_VIOLET = new GradientPaint(0.0f, 0.0f, 
					new Color(93, 65, 126), 0.0f, 0.0f, 
					new Color(123, 87, 168));
		}
		return GP_VIOLET;
	}
	
	private static GradientPaint GP_ORANGE = null;
	public static GradientPaint getGradientPaintOrange(){
		if(GP_ORANGE == null){
			GP_ORANGE = new GradientPaint(0.0f, 0.0f, 
					new Color(203, 108, 29), 0.0f, 0.0f, 
					new Color(255, 143, 38));
		}
		return GP_ORANGE;
	}

	private static GradientPaint GP_LIGHT_GREEN = null;
	public static GradientPaint getGradientPaintLightGreen(){
		if(GP_LIGHT_GREEN == null){
			GP_LIGHT_GREEN = new GradientPaint(0.0f, 0.0f, 
					new Color(176, 221, 127), 0.0f, 0.0f, 
					new Color(190, 227, 149));
		}
		return GP_LIGHT_GREEN;
	}

	private static GradientPaint GP_MARON = null;
	public static GradientPaint getGradientPaintMaron(){
		if(GP_MARON == null){
			GP_MARON = new GradientPaint(0.0f, 0.0f, 
					new Color(118, 101, 58), 0.0f, 0.0f, 
					new Color(194, 185, 98));
		}
		return GP_MARON;
	}

	private static GradientPaint GP_UNDEFINED_5 = null;
	public static GradientPaint getGradientPaintUndefined5(){
		if(GP_UNDEFINED_5 == null){
			GP_UNDEFINED_5 = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0xFF, 0x22), 0.0f, 0.0f, new Color(0x88, 0xFF, 0x88));
		}
		return GP_UNDEFINED_5;
	}
	
	private static ArrayList<GradientPaint> gradientPaintArray = null;	
	public static ArrayList<GradientPaint> getGradientPaintArray(){
		if(gradientPaintArray == null){
			gradientPaintArray = new ArrayList<GradientPaint>();
			gradientPaintArray.add(FColorProvider.getGradientPaintGreen());
			gradientPaintArray.add(FColorProvider.getGradientPaintRed());
			gradientPaintArray.add(FColorProvider.getGradientPaintYellow()); 
			gradientPaintArray.add(FColorProvider.getGradientPaintBlueClear());    
			gradientPaintArray.add(FColorProvider.getGradientPaintRose());
			gradientPaintArray.add(FColorProvider.getGradientPaintBlue());			
			gradientPaintArray.add(FColorProvider.getGradientPaintViolet());
			gradientPaintArray.add(FColorProvider.getGradientPaintOrange());
			gradientPaintArray.add(FColorProvider.getGradientPaintLightGreen());
			gradientPaintArray.add(FColorProvider.getGradientPaintMaron());
		}
		return gradientPaintArray;
	}

	public static GradientPaint getGradientPaintAt(int at){
		return getGradientPaintArray().get(at % getGradientPaintArray().size());
	}
}
