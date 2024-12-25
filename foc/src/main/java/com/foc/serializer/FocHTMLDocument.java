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
package com.foc.serializer;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import com.foc.Globals;

public class FocHTMLDocument {

	public static final String STYLE_VERTICAL = 	"-moz-transform: rotate(-90deg);"  /* FF3.5+ */
																							+	"-o-transform: rotate(-90deg);"  /* Opera 10.5 */
																							+ "-webkit-transform: rotate(-90deg);"  /* Saf3.1+, Chrome */
																							+ "filter:  progid:DXImageTransform.Microsoft.BasicImage(rotation=0.083);"; /* IE6,IE7 */
	
	private Document           doc          = null;
	private ArrayList<Element> elementArray = null;
	private String             style        = null;
	
	public FocHTMLDocument(){
		elementArray = new ArrayList<Element>();
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			doc = docBuilder.newDocument();
		}catch(Exception e){
			Globals.logException(e);
		}
	}
	
	public void dispose(){
		if(elementArray != null){
			elementArray.clear();
			elementArray = null;
		}
		doc = null;
	}

	public void printToStream(StringWriter out){
		try{
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
		  StreamResult result = new StreamResult(out);
		  transformer.transform(source, result);
		}catch(Exception e){
			Globals.logException(e);
		}
	}
	
	public Element getCurrentElement(){
		Element elem = null;
		if(elementArray != null && elementArray.size() > 0){
			elem = elementArray.get(elementArray.size() - 1);
		}
		return elem;
	}
	
	public void openNode(String nodeTitle){
		addStyleAsAttribute();
		
		Element newElement = doc.createElement(nodeTitle);
		
		Element elem = getCurrentElement();
		if(elem == null){
			doc.appendChild(newElement);
		}else{
			elem.appendChild(newElement);
		}
		
		elementArray.add(newElement);
	}

	public void closeNode(){
		addStyleAsAttribute();
		Element elem = getCurrentElement();
		if(elem != null){
			elementArray.remove(elementArray.size() - 1);
		}
	}

	public void addAttribute(String key, String value){
		Element elem = getCurrentElement();
		if(elem != null){
			Attr attr = doc.createAttribute(key);
			attr.setValue(value);
			elem.setAttributeNode(attr);
		}
	}

//	public void addText(int i, String text){
//		Element elem = getCurrentElement();
//		if(elem != null){
//			Text textNode = doc.createTextNode(text);
//			elem.appendChild(textNode);
//		}
//	}
	
	public void addText(int maxLength, String text){
		Element elem = getCurrentElement();
		if(elem != null){
			if(maxLength != -1 && text.length() > maxLength){
				addBreakLine(elem, maxLength, text);
			}else{
				addText(elem, text);
			}
		}
	}
		
	public void addBreakLine(){
		Element element = doc.createElement("br");
		Element elem = getCurrentElement();
		elem.appendChild(element);
	}
	
	private void addBreakLine(Element elem, int maxLength, String text){
		StringTokenizer stringTokenizer = new StringTokenizer(text, " ");
		String line = "";
		
		while(stringTokenizer.hasMoreTokens()){
			String token = stringTokenizer.nextToken();
			if(stringTokenizer.hasMoreTokens()) token +=" ";
			line += token + " ";
			if(line.length() > maxLength){
				addText(elem, token);
				Element element = doc.createElement("br");
				elem.appendChild(element);
				line = "";
			}else{
				addText(elem, token);
			}
		}
	}

	private void addText(Element elem, String text){
		if(doc != null && elem != null){
			Text textNode = doc.createTextNode(text);
			elem.appendChild(textNode);
		}
	}
	
	private void addCheckBox(String text){
		Element elem = getCurrentElement();
		if(doc != null && elem != null){
			Element elementChecbox = doc.createElement("input");
			elementChecbox.setAttribute("type", "checkbox");
			elementChecbox.setAttribute("name", text);
			elem.appendChild(elementChecbox);
			
			EventTarget eventTarget = (EventTarget) elementChecbox;
			eventTarget.addEventListener("click", new EventListener() {
				
				@Override
				public void handleEvent(Event arg0) {
					System.out.println("test Listener");
				}
			}, true);
			
			eventTarget.addEventListener("select", new EventListener() {
				
				@Override
				public void handleEvent(Event arg0) {
					System.out.println("test Listener");
				}
			}, true);
			
			eventTarget.addEventListener("change", new EventListener() {
				
				@Override
				public void handleEvent(Event arg0) {
					System.out.println("test Listener");
				}
			}, true);
		}
	}
	
	private void addStyleAsAttribute(){
		if(style != null){
			addAttribute("style", style);
			style = null;
		}
	}
	
	public void addStyle(String styleText){
		style += styleText;
	}
	
	public void openRow(){
		openNode("tr");
	}

	public void closeRow(){
		closeNode();
	}
	
	public void addTableCell_TextWithColspan(int colspan, int width, int height, String style, String text){
		addTableCell_Text(colspan, 0, width, height, null, style, text);
	}
	
	public void addTableCell_TextWithRowspan(int rowspan, int width, int height, String style, String text){
		addTableCell_Text(0, rowspan, width, height, null, style, text);
	}
	
	public void addTableCell_TextWithColRowspan(int colspan, int rowspan, int width, int height, String style, String text){
		addTableCell_Text(colspan, rowspan, width, height, null, style, text);
	}
	
	public void addTableCell_Text(int height, String style, String text){
		addTableCell_Text(0, height, style, text);
	}
	
	public void addTableCell_Text(int width, int height, String style, String text){
		addTableCell_Text(width, height, null, style, text);
	}
	
	public void addTableCell_Text(int colspan, int rowspan, int width, int height, String cellStyle, String divStyle, String text){
		openTableCell(colspan, rowspan, width, height, cellStyle, divStyle, null);
		addText(-1, text);
		closeTableCell();
	}
	
	public void addTableCell_Text(int width, int height, String cellStyle, String divStyle, String text, String valign, int maxLength){
		addTableCell_TextWithValign(width, height, cellStyle, divStyle, text, valign, maxLength);
	}
	
	public void addTableCell_Text(int width, int height, String cellStyle, String divStyle, String text){
		addTableCell_TextWithValign(width, height, cellStyle, divStyle, text, null, -1);
//		openTableCell(0, 0, width, height, cellStyle, divStyle, null);
//		addText(text);
//		closeTableCell();
	}
	
	public void addTableCell_TextWithValign(int width, int height, String cellStyle, String divStyle, String text, String valign, int maxLength){
		openTableCell(0, 0, width, height, cellStyle, divStyle, valign);
		addText(maxLength, text);
		closeTableCell();
	}
	
	public void openTableCell(int colspan, int rowspan, int width, int height, String cellStyle, String divStyle, String valign){
		openNode("td");
		if(width > 0){
			addAttribute("width", ""+width);
		}				
		if(cellStyle != null){
			addAttribute("style", ""+cellStyle);
		}
		if(colspan > 0){
			addAttribute("colspan", ""+colspan);
		}
		if(rowspan > 0){
			addAttribute("rowspan", ""+rowspan);
		}
		if(height > 0){
			addAttribute("height", ""+height);
		}
		if(valign != null){
			addAttribute("valign", valign);
		}
		openNode("div");
		if(divStyle != null){
			addAttribute("style", ""+divStyle);
		}
	}
	
	public void addTableCell_CheckBox(int width, int height, String cellStyle, String divStyle, String text, String valign, int maxLength){
		openTableCell(0, 0, width, height, cellStyle, divStyle, valign);
		addCheckBox(text);
		closeTableCell();
	}
	
	public void closeTableCell(){
		closeNode();
		closeNode();
	}
		
}
