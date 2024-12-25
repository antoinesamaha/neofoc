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
package com.foc.htmldoc;

import java.io.PrintStream;

import com.foc.Globals;
import com.foc.util.Utils;

public class HTMLDocumentation {
	
	private PrintStream unitDoc = null;
	private int classNumber = 1;
	private static final int MAX_CLASS_NBR = 4;
	
	public HTMLDocumentation(String dir, String fileName) {
		open(dir, fileName);
	}
	
	public HTMLDocumentation() {
	}
	
	public void dispose() {
		close();
	}
	
	public void close() {
		if(unitDoc != null) {
			closeBody();
			unitDoc.flush();
			unitDoc.close();
			unitDoc = null;
		}
	}
	
	public void open(String dir, String fileName) {
		try{
//			unitDoc = new PrintStream(ConfigInfo.getLogDir()+"/fileName.html", "UTF-8");
			unitDoc = new PrintStream(dir+"/"+fileName+".html", "UTF-8");
		}catch (Exception e){
			Globals.logException(e);
		}
		openBody();
	}
	
	public void openBody() {
		if(unitDoc != null) {
			unitDoc.println("<!DOCTYPE html>");
			unitDoc.println("  <html>");
			unitDoc.println("    <head>");
			unitDoc.println("      <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
			unitDoc.println("      <style>");
			unitDoc.println("        .collapsible {");
//			unitDoc.println("          background-color: rgb(97, 175, 254);");
			unitDoc.println("          margin-top: 10px;");
			unitDoc.println("          border-radius: 5px;");
			unitDoc.println("          border: 2px solid;");
			unitDoc.println("          color: white;");
			unitDoc.println("          cursor: pointer;");
			unitDoc.println("          padding: 10px;");
			unitDoc.println("          width: 100%;");
//			unitDoc.println("          border: none;");
			unitDoc.println("          text-align: left;");
			unitDoc.println("          outline: none;");
			unitDoc.println("          font-size: 15px;");
			unitDoc.println("        }");
			unitDoc.println();
			unitDoc.println("        .collapsible:after {");
			unitDoc.println("          content: \"\\002B\";");
			unitDoc.println("          color: white;");
			unitDoc.println("          font-weight: bold;");
			unitDoc.println("          float: right;");
			unitDoc.println("          margin-left: 5px;");
			unitDoc.println("        }");
			
			unitDoc.println("        .active:after {");
			unitDoc.println("          content: \"\\2212\";");
			unitDoc.println("        }");
				
			unitDoc.println();
			unitDoc.println("        .content {");
			unitDoc.println("           padding: 0 10px;");
			unitDoc.println("           max-height: 0;");
			unitDoc.println("           overflow: hidden;");
			unitDoc.println("           font-weight: normal;");
			unitDoc.println("           transition: max-height 0.2s ease-out;");
//			unitDoc.println("           background-color: rgb(235, 243, 251);");
			unitDoc.println("        }");
			
			//Blue
			unitDoc.println("        .collapsible1 {");
			unitDoc.println("          background-color: rgb(97, 175, 254);");
			unitDoc.println("          border-color: rgb(80, 80, 254);");
			unitDoc.println("        }");
			unitDoc.println();
			unitDoc.println("        .collapsible1:hover {");
			unitDoc.println("          background-color: rgb(80, 80, 254);");
			unitDoc.println("        }");
			unitDoc.println();
			unitDoc.println("        .content1 {");
			unitDoc.println("           background-color: rgb(235, 243, 251);");
			unitDoc.println("        }");

			//Orange
			unitDoc.println("        .collapsible2 {");
			unitDoc.println("          background-color: rgb(252, 161, 48);");
			unitDoc.println("          border-color: rgb(240, 140, 48);");
			unitDoc.println("        }");
			unitDoc.println();
			unitDoc.println("        .collapsible2:hover {");
			unitDoc.println("          background-color: rgb(240, 140, 48);");
			unitDoc.println("        }");
			unitDoc.println();
			unitDoc.println("        .content2 {");
			unitDoc.println("           background-color: rgb(251, 242, 230);");
			unitDoc.println("        }");

			//Green
			unitDoc.println("        .collapsible3 {");
			unitDoc.println("          background-color: rgb(73, 204, 144);");
			unitDoc.println("          border-color: rgb(73, 180, 120);");
			unitDoc.println("        }");
			unitDoc.println();
			unitDoc.println("        .collapsible3:hover {");
			unitDoc.println("          background-color: rgb(73, 180, 120);");
			unitDoc.println("        }");
			unitDoc.println();
			unitDoc.println("        .content3 {");
			unitDoc.println("           background-color: rgb(233, 246, 240);");
			unitDoc.println("        }");

			//Red
			unitDoc.println("        .collapsible4 {");
			unitDoc.println("          background-color: rgb(249, 62, 62);");
			unitDoc.println("          border-color: rgb(210, 30, 30);");
			unitDoc.println("        }");
			unitDoc.println();
			unitDoc.println("        .collapsible4:hover {");
			unitDoc.println("          background-color: rgb(210, 30, 30);");
			unitDoc.println("        }");
			unitDoc.println();
			unitDoc.println("        .content4 {");
			unitDoc.println("           background-color: rgb(251, 232, 232);");
			unitDoc.println("        }");
			
			unitDoc.println();
			unitDoc.println("      </style>");
			unitDoc.println("    </head>");
			unitDoc.println("  <body>");
		}
	}
	
	public void closeBody() {
		if(unitDoc != null) {
			unitDoc.println("    <script>");
			unitDoc.println("      var coll = document.getElementsByClassName(\"collapsible\");");
			unitDoc.println("      var i;");
			unitDoc.println("      for (i = 0; i < coll.length; i++) {");
			unitDoc.println("        coll[i].addEventListener(\"click\", function() {");
			unitDoc.println("          this.classList.toggle(\"active\");");
			unitDoc.println("          var content = this.nextElementSibling;");
			unitDoc.println("          if (content.style.maxHeight){");
			unitDoc.println("            content.style.maxHeight = null;");
			unitDoc.println("          } else {");
			unitDoc.println("            content.style.maxHeight = content.scrollHeight + \"px\";");
			unitDoc.println("          }"); 
			unitDoc.println("        });");
			unitDoc.println("      }");
			unitDoc.println("    </script>");
			unitDoc.println("  </body>");
			unitDoc.println("</html>");
		}
	}
	
	public void addSection(String suiteName, String description) {
		classNumber++;
		if(classNumber > MAX_CLASS_NBR) classNumber = 1;
		if(unitDoc != null && !Utils.isStringEmpty(suiteName)) {
			unitDoc.println("<H1>"+suiteName+"</H1>");
			if(!Utils.isStringEmpty(description)) {
				unitDoc.println("<P>"+description+"</P>");
			}
		}
	}
	
	public void addCollapsible(String title, String description) {
		if(unitDoc != null && !Utils.isStringEmpty(title)) {
			unitDoc.println("<button class=\"collapsible collapsible"+classNumber+"\">"+title+"</button>");
			unitDoc.println("<div class=\"content content"+classNumber+"\">");
			unitDoc.println("   <p>"+description+"</p>");
			unitDoc.println("</div>");
		}
	}

}
