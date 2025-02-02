/*
 * Created on Oct 24, 2005
 */
package com.foc.file;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import com.foc.Globals;

/**
 * @author 01Barmaja
 */
public abstract class CopyOfFocLineReader {

  public abstract void readToken(String token, int pos);// This function can be
                                                        // called from the
                                                        // readLine function

  private char fieldSeparator[] = null;
  private String tokenDelimiters = null;
  private boolean valid = true;

  public CopyOfFocLineReader() {
    fieldSeparator = new char[1];
    fieldSeparator[0] = ',';
  }

  public CopyOfFocLineReader(char fieldDelimiter) {
    fieldSeparator = new char[1];
    fieldSeparator[0] = fieldDelimiter;
  }

  public CopyOfFocLineReader(char fieldDelimiter1, char fieldDelimiter2) {
    fieldSeparator = new char[2];
    fieldSeparator[0] = fieldDelimiter1;
    fieldSeparator[1] = fieldDelimiter2;
  }

  public void dispose() {
    tokenDelimiters = null;
  }

  private String getTokenDelimiters() {
    if (tokenDelimiters == null) {
      tokenDelimiters = String.valueOf(fieldSeparator) + "\"";
    }
    return tokenDelimiters;
  }

  public String getTokenAt(StringBuffer buff, int at) {
    String token = null;
    StringTokenizer tok = new StringTokenizer(buff.toString(), getTokenDelimiters(), false);

    int count = 1;
    while (tok.hasMoreTokens() && count <= at) {
      String str = tok.nextToken();
      if (count == at) {
        token = str;
      }
      count++;
    }
    return token;
  }

  public void scanTokens(StringBuffer buff) {
    boolean insideSpeachMarks = false;
    String stringInsideSpeachMarks = "";

    StringTokenizer tok = new StringTokenizer(buff.toString(), getTokenDelimiters(), true);
    int pos = 0;
    while (tok.hasMoreTokens()) {
      String str = tok.nextToken();
      if(str != null && !str.isEmpty()){
      	str = str.trim();
      }
      boolean callReadToken = true;

      if (insideSpeachMarks) {
        if (str.compareTo("\"") == 0) {
          insideSpeachMarks = !insideSpeachMarks;
          callReadToken = true;
        } else {
          stringInsideSpeachMarks += str;
          callReadToken = false;
        }

        if (callReadToken) {
          readToken(stringInsideSpeachMarks, pos);
          stringInsideSpeachMarks = "";
        }
      } else {
        for (int i = 0; i < fieldSeparator.length; i++) {
          if (str.compareTo(String.valueOf(fieldSeparator[i])) == 0) {
            pos++;
            callReadToken = false;
          }
        }

        if (str.compareTo("\"") == 0) {
          insideSpeachMarks = !insideSpeachMarks;
          callReadToken = false;
        }

        if (callReadToken) {
          readToken(str, pos);
        }
      }
    }
  }

  public char getFieldSeparator() {
    return fieldSeparator[0];
  }

  public void setFieldSeparator(char fieldSeparator) {
    this.fieldSeparator[0] = fieldSeparator;
  }

  private String readString(StringBuffer buffer, int start, int end) {
    String str = buffer.substring(start, end);
    if (str != null && !str.isEmpty()) {
      str = str.trim();
    } else {
      str = "";
    }
    return str;
  }

  protected int readInt(StringBuffer buffer, int start, int end) {
    String str = readString(buffer, start, end);
    int val = (str != null && !str.isEmpty()) ? Integer.valueOf(str) : 0;
    return val;
  }

  protected double readDouble(StringBuffer buffer, int start, int end) {
    String str = readString(buffer, start, end);
    return (str != null && !str.isEmpty()) ? Double.valueOf(str) : 0;
  }

  protected char readChar(StringBuffer buffer, int start) {
    String str = readString(buffer, start, start + 1);
    return (str != null && !str.isEmpty()) ? str.charAt(0) : 0;
  }

  private Date readDate_WithFormat(String token, int formatIndex){
  	Date date = null;
    try {
    	SimpleDateFormat format = null;
    	
    	if(formatIndex == 0){
    		format = new SimpleDateFormat("dd-MM-yyyy");//hadi 27/Jan/2015 we need to check first for MySql Date Format
    	}else if(formatIndex == 1){
    		format = new SimpleDateFormat("MM/dd/yyyy");
    	}else if(formatIndex == 2){
    		format = new SimpleDateFormat("dd/MM/yy");
    	}else if(formatIndex == 2){
    		format = new SimpleDateFormat("yyyyMMdd");    		
    	}
    	
      date = new Date((format.parse(token)).getTime());
    } catch (Exception e) {
    	
    	if(formatIndex < 3){
	    	formatIndex++;
	    	date = readDate_WithFormat(token, formatIndex);
    	}else{
    		Globals.logString("Could not parse date : '" + token + "'");
    	}
    }
    return date;
  }
  
  protected Date readDate(String token) {
    Date date = readDate_WithFormat(token, 0);
    if(date == null) date = new Date(0);
    return date;
  }
  
  protected Date readDate(String token, String formatTemplate) {
    Date date = new Date(0);
    SimpleDateFormat format = new SimpleDateFormat(formatTemplate);
    try {
      date = new Date((format.parse(token)).getTime());
    } catch (Exception e) {
      Globals.logString("Could not parse date : '" + token + "'");
    }
    return date;
  }
  
  protected double readDouble(String token) {
    token = token.replace(',', '.');
    return !token.isEmpty() ? Double.valueOf(token) : 0;
  }

  protected int readInteger(String token) {
    token = token.trim();
    return !token.isEmpty() ? Integer.valueOf(token) : 0;
  }

  protected char readChar(String token) {
    token = token.trim();
    return !token.isEmpty() ? token.charAt(0) : 0;
  }

}