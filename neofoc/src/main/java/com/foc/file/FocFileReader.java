/*
 * Created on Oct 24, 2005
 */
package com.foc.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.foc.Globals;

/**
 * @author 01Barmaja
 */
public abstract class FocFileReader extends FocLineReader{
	
  public abstract void readLine(StringBuffer buffer);
	
  protected File              file       = null;
  protected InputStreamReader reader     = null;
  private   int               lineNumber = 0;
  private   int               firstReadingLineIndex = 0;
  private   String            charset    = "UTF-8";
  
  public FocFileReader(){
  	lineNumber = 0;
  }

  public FocFileReader(File file, char fieldDelimiter, String charset){
  	super(fieldDelimiter);
  	this.charset = charset;
  	setFile(file);
  }
  
  public FocFileReader(File file, char fieldDelimiter){
  	this(file, fieldDelimiter, null);
  }
  
  public FocFileReader(InputStream inputStream, char fieldDelimiter){
    super(fieldDelimiter);
    setInputStream(inputStream);
  }
  
  public void dispose(){
  	file =null;
  	reader = null;  	
  }
    
  private boolean setInputStream(InputStream inputStream){
    boolean ok = false;
    close();
    if(inputStream != null){
    	if(charset != null){
    		try{
    			reader = new InputStreamReader(inputStream, charset);
    		}catch (Exception e) {
					Globals.logException(e);
				}
    	}
  		if(reader == null){
  			reader = new InputStreamReader(inputStream);
  		}
      ok = true;
    }
    return ok;
  }
  
  public boolean setFile(File file){
    boolean ok = false;
    close();
    
    try{
    	if(charset != null){
    		reader = new InputStreamReader(new FileInputStream(file), charset);
    	}else{
    		reader = new InputStreamReader(new FileInputStream(file));
    	}
		}catch (UnsupportedEncodingException e1){
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (FileNotFoundException e1){
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    
    if(file != null){
      try{
        //reader = new FileReader(file);
      	if(charset != null){
      		try{
      			reader = new InputStreamReader(new FileInputStream(file), charset);
      		}catch (UnsupportedEncodingException e1){
      			Globals.logException(e1);
      			reader = new InputStreamReader(new FileInputStream(file));
      		}
      	}else{
      		reader = new InputStreamReader(new FileInputStream(file));
      	}
        ok = true;
      }catch(Exception e){
        Globals.logException(e);
      }
    }
    return ok;
  }
 
  public File getFile() {
    return file;
  }

  public StringBuffer loadLine(){
    StringBuffer ret = new StringBuffer();
    try{
      boolean stop = false; 
      int aChar = 0;
      do{
      	aChar = reader.read();
      }while(aChar == 10 || aChar == 13);
      stop = aChar == -1;
      while(!stop){
        ret.append((char)aChar);
      	aChar = reader.read();
        stop = aChar == -1 || aChar == 10 || aChar == 13;
      }
    }catch(Exception e){
      Globals.logException(e);
    }
    return ret;
  }
  
  public void readFirstLine(StringBuffer buffer){
  	readLine(buffer);//By default there is no difference between reading the first line and any other
  }
  
  public boolean readLine(){
  	StringBuffer buff = loadLine();
  	boolean error = buff == null || buff.length() <= 0;

    if(!error){
    	lineNumber++;
    	if(getLineNumber() >= (getFirstReadingLineIndex() - 1)){
	    	if(lineNumber == 1){
	    		readFirstLine(buff);
	    	}else{
	    		readLine(buff);
	    	}
    	}
    }
    return error;
  }
  
  public void readFile(){
  	while(!readLine());
  }
  
  public int getLineNumber(){
  	return lineNumber;
  }

  public void close(){
  	if(reader != null){
  		try {
				reader.close();
				reader = null;
			} catch (IOException e) {
				Globals.logException(e);
			}
  	}
  }

	public boolean isFirstLine() {
		return lineNumber == 1;
	}

	public int getFirstReadingLineIndex() {
		return firstReadingLineIndex;
	}

	public void setFirstLineIndex(int firstReadingLineIndex) {
		this.firstReadingLineIndex = firstReadingLineIndex;
	}
}