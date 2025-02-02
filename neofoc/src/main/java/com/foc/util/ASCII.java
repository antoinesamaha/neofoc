/*
 * Created on 16-Jun-2005
 */
package com.foc.util;

import java.io.*;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Random;
import java.util.StringTokenizer;

import com.foc.Globals;

/**
 * @author 01Barmaja
 */
public class ASCII {
  private String str = null;
  
  public final static char NUL =  0;
  public final static char SOH =  1;
  public final static char STX =  2;
  public final static char ETX =  3;
  public final static char EOT =  4;
  public final static char ENQ =  5;
  public final static char ACK =  6;
  public final static char BEL =  7;
  public final static char BS  =  8;
  public final static char TAB =  9;
  public final static char LF  = 10;
  public final static char VT  = 11;
  public final static char FF  = 12;
  public final static char CR  = 13;
  public final static char SO  = 14;
  public final static char SI  = 15;
  public final static char DLE = 16;
  public final static char DC1 = 17;
  public final static char DC2 = 18;
  public final static char DC3 = 19;
  public final static char DC4 = 20;
  public final static char NACK = 21;
  public final static char SYN = 22;
  public final static char ETB = 23;
  public final static char CAN = 24;
  public final static char EN  = 25;
  public final static char SUB = 26;
  public final static char ESC = 27;
  public final static char FS  = 28;
  public final static char GS  = 29;
  public final static char RS  = 30;
  public final static char US  = 31;
  public final static char SPACE = 32;
  
  public final static char DASH                 = 45;
  public final static char SLASH_RIGHT_TO_LEFT  = 47;
  public final static char SLASH_LEFT_TO_RIGHT  = 92;
  public final static char UNDER_SCORE          = 95;
  
  
  private static final String[] ASCII33 = {"NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS", 
  	"TAB", "LF", "VT", "FF", "CR", "SO", "SI", "DLE", "DC1", "DC2", "DC3", "DC4", "NACK", "SYN", "ETB", "CAN", 
  	"EN", "SUB", "ESC", "FS", "GS", "RS", "US", "."};
  
  public ASCII(String str){
    this.str = str;
  }
  
  public void getASCIIArray(){
    for(int i=0 ;i< str.length(); i++){
      Globals.logString("c["+i+"]="+str.charAt(i)+":"+(int)str.charAt(i));
    }
    Globals.logString("char c[] = new char["+str.length()+"];");    
    for(int i=0 ;i< str.length(); i++){
      Globals.logString("c["+i+"]="+(int)str.charAt(i)+";");
    }
    
    System.out.print("char c[] = new {");
    writeAsciiCodeArray(System.out);
    Globals.logString("};");
    
    Globals.logString("String string = new String(c);");    
  }

  public void writeAsciiCodeArray(PrintStream out){
    for(int i=0 ; i<str.length(); i++){
      if(i > 0){
        out.print(",");
      }
      out.print((int)str.charAt(i));
    }
  }

  public static String convertNonCharactersToDescriptions(String str){
    StringBuffer newStr = new StringBuffer();
    
    for(int i=0; i<str.length(); i++){
      char c = str.charAt(i);
      if(c < 33){
        newStr.append("["+ASCII33[c]+"]");
        if(c == 10){
          newStr.append(c);
        }
      }else{
        newStr.append(c);        
      }
    }
    return newStr.toString();
  }
  
  public static String convertJavaNaming_ToVariableGetterSetterNaming(String upper){
  	return convertJavaNaming_Upper_To_Lower_Internal(upper, false);
  }
  
  public static String convertJavaNaming_ToVariableNaming(String upper){
  	return convertJavaNaming_Upper_To_Lower_Internal(upper, true);
  }
  
  private static String convertJavaNaming_Upper_To_Lower_Internal(String upper, boolean firstCharacterIsLowerCase){
  	String lower = "";
  	if(upper != null){
			lower = upper.toLowerCase();
			if(!lower.isEmpty()){
				//Change the first letter to Upper Case
				char[] c = {lower.charAt(0)};
				String cU = new String(c);
				if(firstCharacterIsLowerCase){
					lower = cU.toLowerCase() + lower.substring(1);
				}else{
					lower = cU.toUpperCase() + lower.substring(1);
				}
				
				int idxOfUnderscore = lower.indexOf('_');
				while(idxOfUnderscore >= 0 && idxOfUnderscore < lower.length()-1){//There is an underscore with a character behind it
					String before = lower.substring(0, idxOfUnderscore);
					String after  = lower.substring(idxOfUnderscore+2);

					char[] c2 = {lower.charAt(idxOfUnderscore+1)};
					cU = new String(c2);
					lower = before + cU.toUpperCase() + after;

					idxOfUnderscore = lower.indexOf('_');
				}
			}
  	}
		return lower;
  }
  
  public static String generateRandomString(int nbrChars){
  	return generateRandomString(nbrChars, false);
  }
  
  public static String generateRandomString(int nbrChars, boolean withNumbers){
  	return generateRandomString(nbrChars, withNumbers, false);
  }
  
  public static String generateRandomString(int nbrChars, boolean withNumbers, boolean caseSensitive){
  	StringBuffer str = new StringBuffer();
	SecureRandom ran = new SecureRandom();
  	char cA = 'A';
  	char ca = 'a';
  	char c0 = '0';
  	int maxRandom = 26;
  	if(caseSensitive) maxRandom = 2 * maxRandom;
  	if(withNumbers) maxRandom += 10;
  	for(int i=0; i<nbrChars; i++){
  		int  charInt = (int)cA + ran.nextInt(maxRandom);
  		if(charInt>'Z'){
  			charInt = charInt - 'Z' - 1;
  			charInt = (int)c0 + charInt;
  			
  			if(caseSensitive && charInt>'9') {
    			charInt = charInt - '9' - 1;
    			charInt = (int)ca + charInt;
  			}
  		}
  		char c  = (char)charInt;  		
  		str.append(c);
  	}
  	return str.toString();
  }

  public static String convertAllWordBeginningToCapital(String original){
  	String result = original; 
  	if(result != null && !result.isEmpty()){
			//Change the first letter to Upper Case
			char[] c = {result.charAt(0)};
			String cU = new String(c);
			result = cU.toUpperCase() + result.substring(1);
			
			int idxOfUnderscore = result.indexOf(' ');
			while(idxOfUnderscore >= 0 && idxOfUnderscore < result.length()-1){//There is an underscore with a character behind it
				String before = result.substring(0, idxOfUnderscore);
				String after  = result.substring(idxOfUnderscore+2);

				char[] c2 = {result.charAt(idxOfUnderscore+1)};
				cU = new String(c2);
				result = before + " " + cU.toUpperCase() + after;

				idxOfUnderscore = result.indexOf(' ', idxOfUnderscore+1);
			}
		}
		return result;
  }

	public static String convertJavaClassNameToATitleWithSpacesAndCapitals(String javaClassName){
		String title = javaClassName;
		
		int idx = 1;//We skip the first letter
		while(idx >= 0 && idx < title.length()-1){//There is an underscore with a character behind it
			char c = title.charAt(idx);
			if(Character.isUpperCase(c)){
				String newStr = title.substring(0, idx)+" "+title.substring(idx);
				title = newStr;
				idx++;
			}
			idx++;
		}
		
		return title;
	}

	public static String convertJavaClassNameToA_ConstantNameWith_(String javaClassName){
		String ret = convertJavaClassNameToATitleWithSpacesAndCapitals(javaClassName);
		ret = ret.replace(" ", "_");
		ret = ret.toUpperCase();
		return ret;
	}
	
  public static String newCodeName(String code, String name, int codeLength){
  	StringBuffer buff = new StringBuffer(code);
  	/*  	
  	boolean dot = false;
  	for(int i=code.length(); i<codeLength; i++){
  		if(Globals.getApp().isWithGui()){
  			//buff.append(" ");
  		}else{
  			buff.append(dot ? "." : " ");
  			dot = !dot;
  		}
  	}
  	*/
//  	int add = 50 - buff.length();
//  	while(add > 0){
//  		buff.append("&#160;");
//  		add--;
//  	}
  	buff.append(": ");
  	buff.append(name);
  	return buff.toString();
  }

	public static String convert_InputStreamToString(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuilder out = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
        out.append(line);
    }
    return out.toString();
  }
	
	//Not Finished
	public static String convert_ShrinkName(String in, int limit){
		String out = "";

		if(in.length() > limit){
			StringTokenizer tokenizer = new StringTokenizer(in, "_");
			int count = tokenizer.countTokens();
			
			String[] words = new String[count];
			int idx = 0;
			//Fill the words array
			while(tokenizer.hasMoreTokens()){
				String tok = tokenizer.nextToken();
				tok = tok.toLowerCase();
				String firstChar = tok.substring(0,1).toUpperCase();
				String otherChar = tok.substring(1,tok.length()).toLowerCase();
				tok = firstChar+otherChar;
				words[idx++]=tok;
			}
			
			int deduction = in.length() - (count-1) - limit;
			if(deduction > 0){
				int maxManipulatable = 0;
				for(String w : words){
					if(w.length() >= 5){
						maxManipulatable += w.length();
					}
				}
	
				for(int i=0; i<words.length; i++){
					String w = words[i];
					if(w.length() >= 5){
						double toDeduceHere = ((double)(w.length() * deduction)) / ((double)maxManipulatable);
						int deduceHere = (int)Math.ceil(toDeduceHere);
						if(w.length() - deduceHere < 1){
							Globals.logString("ERROR SHRIKING WORD : "+words[i]);
						}
						if(deduceHere > 0){
							w = w.substring(0, w.length() - deduceHere);
						}
						words[i] = w;
					}
				}
			}
			for(String w : words){
				out += w;
			}			
		}
		
		return out;
	}
}
