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
package b01.officeLink;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.TextSegement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.foc.Globals;
import com.foc.util.Utils;

public class ExtendedWordDocument {

  private HWPFDocument hwpfDocument = null;//old doc
  private XWPFDocument xwpfDocument = null;//new docx
  
  public ExtendedWordDocument(InputStream istream) throws IOException {
  	try{
  		xwpfDocument = new XWPFDocument(istream);
  	}catch(Exception e){
  		xwpfDocument = null;
  		Globals.logString("Could not read EXCEL file as xlsx\n"+(e != null ? e.getMessage() : ""));
  		try{
  			hwpfDocument = new HWPFDocument(istream);
  		}catch(Exception e1){
  			hwpfDocument = null;
  			Globals.logException(e1);
  		}
  	}
  }

  public ExtendedWordDocument(POIFSFileSystem pfilesystem) throws IOException {
    hwpfDocument = new HWPFDocument(pfilesystem);
  }
  
  public void dispose(){
  	hwpfDocument = null;
  	xwpfDocument = null;
  }

  public void write(ByteArrayOutputStream baos){
  	if(xwpfDocument != null){
  		try{
				xwpfDocument.write(baos);
			}catch (IOException e){
				Globals.logException(e);
			}
  	}else if(hwpfDocument != null){
  		try{
				hwpfDocument.write(baos);
			}catch (IOException e){
				Globals.logException(e);
			}
  	}
  }
  
  private void replaceInRun(XWPFRun run, String replaceWith){
  	boolean firstSet = true;
  	
		String remainingFragment = replaceWith; 
		while(remainingFragment.contains("\n")){
			int indexOfEnter = remainingFragment.indexOf("\n");
			String firstPart = remainingFragment.substring(0, indexOfEnter);
			remainingFragment = remainingFragment.substring(indexOfEnter+1, remainingFragment.length());
			if(firstSet){
				run.setText(firstPart, 0);
				firstSet = false;
			}else{
				run.setText(firstPart);//,0
			}
			run.addBreak();
		}
		if(!Utils.isStringEmpty(remainingFragment) || firstSet){
			if(firstSet){
				run.setText(remainingFragment, 0);
			}else{
				run.setText(remainingFragment);
			}
		}
  }
  
  /*
  private static void setOrientation(XWPFParagraph par) {
    if (par.getCTP().getPPr() == null) {
      par.getCTP().addNewPPr();
    }
    if ( par.getCTP().getPPr().getBidi()==null ) {
      par.getCTP().getPPr().addNewBidi();
      par.getCTP().getPPr().setTextDirection(TextDirection.);
    }
    par.getCTP().getPPr().getBidi().setVal(STOnOff.ON);
  }
  */
  
  public boolean replaceInParagraph(XWPFParagraph para, String toReplace, String replaceWith){
  	boolean didReplace = false;
  	
  	if(para != null && toReplace != null && replaceWith != null){
//  		setOrientation(para);
  		
			List<XWPFRun> runs = para.getRuns();
			TextSegement found = para.searchText(toReplace, new PositionInParagraph());
			if(found != null){
				if(found.getBeginRun() == found.getEndRun()){
					// whole search string is in one Run
					XWPFRun run = runs.get(found.getBeginRun());
					String runText = run.getText(run.getTextPosition());

					//Support of Enter to transform it to a line break
					//------------------------------------------------
  				String replaced = runText.replace(toReplace, replaceWith);
					replaceInRun(run, replaced);
					//------------------------------------------------
//				run.setText(replaced, 0);
  				//------------------------------------------------
						
					didReplace = true;
				}else{
					// The search string spans over more than one Run
					// Put the Strings together
					StringBuilder b = new StringBuilder();
					for(int runPos = found.getBeginRun(); runPos <= found.getEndRun(); runPos++){
						XWPFRun run = runs.get(runPos);
						b.append(run.getText(run.getTextPosition()));
					}
					String connectedRuns = b.toString();
					String replaced = connectedRuns.replace(toReplace, replaceWith);
		
					// The first Run receives the replaced String of all connected Runs
					XWPFRun partOne = runs.get(found.getBeginRun());
					//Support of Enter to transform it to a line break
					//------------------------------------------------
					replaceInRun(partOne, replaced);//replaceWith
					//partOne.setText(replaced, 0);
  				//------------------------------------------------
					
					// Removing the text in the other Runs.
					for(int runPos = found.getBeginRun() + 1; runPos <= found.getEndRun(); runPos++){
						XWPFRun partNext = runs.get(runPos);
						partNext.setText("", 0);
					}
					didReplace = true;
				}
			}
  	}
  	return didReplace;
  }
  
  public void replace(String oldString, String newString){
    // hack to get the ending cp of the document, Have to revisit this.
    if(getHwpfDocument() != null){
      TextPieceTable text = getHwpfDocument().getTextTable();
      List list = text.getTextPieces();
      
      for(int i=0; i<list.size(); i++){
        PropertyNode p = (PropertyNode)list.get(i);
        
        if(p instanceof TextPiece){
          TextPiece tp = (TextPiece)p;
          StringBuffer buffer = tp.getStringBuffer();
          
          int start = buffer.indexOf(oldString);
          int end = start + oldString.length();
          
          replace(tp, start, end, newString);
        }
      }
    }
    
  }
  
  public void replace(TextPiece textPiece, int start, int end, String newText){
    int lengthToReplace = end - start;
    if(newText.length() < lengthToReplace){
      int gap = lengthToReplace- newText.length();
      for(int i=0; i<gap; i++){
        if(i<gap/2){
          newText = " "+newText;
        }else{
          newText = newText+" ";
        }
      }
    }
    
    StringBuffer buffer = textPiece.getStringBuffer();
    
    buffer.replace(start, end, newText.substring(0, lengthToReplace));        
    Range range = new Range(start, end, getHwpfDocument());
    range.insertAfter(newText.substring(lengthToReplace));
  }
  
  public void replace_XXXXX(String oldString, String newString){
    // hack to get the ending cp of the document, Have to revisit this.
    if(getHwpfDocument() != null && getHwpfDocument().getTextTable() != null){
      java.util.List text = getHwpfDocument().getTextTable().getTextPieces();
      for(int i=0; i<text.size(); i++){
        PropertyNode p = (PropertyNode)text.get(i);
        System.out.println("Property Node "+i+" = "+p);
        System.out.println("Start = "+p.getStart()+" End = "+p.getEnd());
        System.out.println("Class = "+p.getClass().getName());
        
        if(p instanceof TextPiece){
          TextPiece tp = (TextPiece)p;
          StringBuffer buffer = tp.getStringBuffer();
          System.out.println("Buffer = "+buffer);
          
          int start = buffer.indexOf(oldString);
          int end = start + oldString.length();
          
          //buffer.indexOf(newString);        
          buffer.replace(start, end, newString);
          tp.setEnd(tp.getEnd()+(newString.length()- oldString.length()));
        }
      }
    }
  }
  
  public HWPFDocument getHwpfDocument(){
    return hwpfDocument;
  }
  
  public TextPieceTable getTextTable(){
    TextPieceTable tpt = null;
    if(getHwpfDocument() != null){
      tpt = getHwpfDocument().getTextTable();
    }
    return tpt;
  }

	public XWPFDocument getXwpfDocument() {
		return xwpfDocument;
	}
}
