/*
 * Created on 14-May-2005
 */
package com.foc.file;

import java.io.*;
import java.util.Properties;

import com.foc.Globals;

/**
 * @author 01Barmaja
 */
public class DirSet implements Runnable{
  public static final String SEND_DIR            = "send";
  public static final String RECEIVE_DIR         = "receive";
  public static final String ERR_DIR             = "err";
  public static final String ARCHIVE_SEND_DIR    = "archSend";
  public static final String ARCHIVE_RECEIVE_DIR = "archReceive";
  public static final int TIME_DELAY             = 5000; 
	
  private FileGrabber fileGrabber = null;
  private boolean stop = false;
  Thread polling = null;
	private  String rootDirPath = null;
  private long checkDelay = 3000;

  private File sendDir = null;
  private File receiveDir = null;
  private File archiveSendDir = null;
  private File archiveReceiveDir = null;
  private File errDir = null;
  private boolean keepFilesForDebug = false;
  
  public DirSet(){
  	rootDirPath        = null;
    sendDir            = null;
    receiveDir         = null;
    archiveSendDir     = null;
    archiveReceiveDir  = null;
    errDir             = null;
    fileGrabber        = null;
    checkDelay         = 5000;
  }
  
  public DirSet(Properties props) throws Exception{
  	this();
  	
  	rootDirPath              = props.getProperty("dirSet.rootDir", "c:/temp");
  	String inDir             = props.getProperty("dirSet.sendDir", SEND_DIR);
  	String outDir            = props.getProperty("dirSet.receiveDir", RECEIVE_DIR);
  	String errDir            = props.getProperty("dirSet.errDir", ERR_DIR);
  	//String archiveSendDir    = props.getProperty("dirSet.archiveSendDir", ARCHIVE_SEND_DIR);
  	String archiveReceiveDir = props.getProperty("dirSet.archiveReceiveDir", ARCHIVE_RECEIVE_DIR);
  	
  	initDirectoriesIfNeeded(inDir, outDir, errDir, archiveReceiveDir);
  	//initDirectoriesIfNeeded(inDir, outDir, errDir, archiveDir);  	
  	
  	String timeDelayStr = props.getProperty("dirSet.timeDelay", String.valueOf(TIME_DELAY));
  	checkDelay = Long.valueOf(timeDelayStr).longValue();
  	
  	String fileGraberClassName = props.getProperty("dirSet.fileGrabber");
  	if(fileGraberClassName != null){
  		fileGrabber = (FileGrabber)Class.forName(fileGraberClassName).newInstance();
  	}
  }

  public void dispose(){
  	rootDirPath       = null;
    sendDir           = null;
    receiveDir        = null;
    errDir            = null;
    archiveReceiveDir = null;
    archiveSendDir    = null;
   
    fileGrabber = null;
    stopPolling();
    polling = null;
  }
  
  public boolean isKeepFilesForDebug(){
    return keepFilesForDebug;
  }
  
  public void setKeepFilesForDebug(boolean keepFilesForDebug){
    this.keepFilesForDebug = keepFilesForDebug;
  }
  	
	public void setFileGrabber(FileGrabber fileGrabber) {
		this.fileGrabber = fileGrabber;
	}
	
	public void setCheckDelay(long checkDelay){
		if(checkDelay > 0){
			this.checkDelay = checkDelay;
		}
	}
  
  private void initDirectoriesIfNeeded(String in, String out, String err, String archive) throws Exception{
  	if(sendDir == null){
	    sendDir = getSubDirectory(rootDirPath, in);	    
	    receiveDir = getSubDirectory(rootDirPath, out);
	    errDir = getSubDirectory(rootDirPath, err);
	    archiveSendDir = getSubDirectory(rootDirPath, archive+in);
	    archiveReceiveDir = getSubDirectory(rootDirPath, archive+out);
  	}
  }

  public void setDirectories(String rootPath, String send, String receive, String err, String archive) throws Exception{
  	if(sendDir == null){
  		rootDirPath = rootPath;
	    sendDir = getSubDirectory(rootDirPath, send);	    
	    if(receive != null) receiveDir = getSubDirectory(rootDirPath, receive);
	    errDir = getSubDirectory(rootDirPath, err);
	    if (!archive.isEmpty() && !send.isEmpty()){
	    	archiveSendDir = getSubDirectory(rootDirPath, archive+send);
	    }
	    if (!archive.isEmpty() && receive != null && !receive.isEmpty()){
	    	archiveReceiveDir = getSubDirectory(rootDirPath, archive+receive);
	    }
  	}
  }

  public void startPolling(){
    stop = false;
    if (polling == null){
      //Thread polling = new Thread(this);
      polling = new Thread(this);
      polling.start();
    }
  }
  
  public void stopPolling(){
    stop = true;
  }
  
  private File getSubDirectory(String root, String subDir) throws Exception {
    File dir = null;
    if (!subDir.isEmpty()) { 
	    String str = root + File.separatorChar + subDir;
	    dir = new File(str);
	    dir.mkdir();
	    if(!dir.exists()){
	      Exception e = new Exception("Directory could not be created:"+str);
	      throw(e);
	    }
    }
    return dir;
  }
  
  public void run() {
  	try {
      while(true){
        if (!stop){
        	getFileList();
        	//Thread.sleep(checkDelay);
        }
        Thread.sleep(checkDelay);
      }
    } catch (InterruptedException e) {
      Globals.logException(e);
    }
  }
  
  public void getFileList(){
    File listOfFiles[] = sendDir.listFiles();
    for(int i=0; listOfFiles != null && i<listOfFiles.length; i++){
      File file = listOfFiles[i];      
      if(file.isFile()){
        //Globals.logString("P:"+file.getParent() + " N:"+file.getName());
        File newFile = null;        
        if(!fileGrabber.grabFile(file)){
        	if(isKeepFilesForDebug()){
        		newFile = new File(archiveSendDir, file.getName()) ;
        	}
        }else{
          newFile = new File(errDir, file.getName()) ;
        }
        
        if(newFile != null && newFile.exists()){
          Globals.logString("File already exists:"+newFile.getPath());
          Globals.logString("Previous file will be replaced");            
          newFile.delete();
        }
        
        if(newFile != null){
	        if(!file.renameTo(newFile)){        	
	          Globals.logString("File move has encountered problems.");
	          Globals.logString("While moving from "+file.getPath()+" to "+newFile.getPath());
	        }
        }else{
        	file.delete();
        }
      }
    }    
  }

	public File getReceiveDir() {
		return receiveDir;
	}
	
	public File getArchiveReceiveDir() {
		return archiveReceiveDir;
	}	
}