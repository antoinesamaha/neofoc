/*
 * Created on 20 feb. 2004
 */
package com.foc;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.sql.Time;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foc.access.AccessControl;
import com.foc.access.FocLogger;
import com.foc.db.DBManager;
import com.foc.desc.FocObject;
import com.foc.gui.DisplayManager;
import com.foc.gui.FGOptionPane;
import com.foc.gui.FPanel;
import com.foc.list.FocList;
import com.foc.shared.dataStore.IFocData;
import com.foc.shared.xmlView.IXMLViewConst;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class Globals{
  
  public static IFocEnvironment iFocDisplay = null;
  
  public  static int       securityShift = 2906;       
	
  public  static FocObject debugObj = null;
  
  private static Application application = null;
  private static boolean insideGetSessionID = false;

  public static double CHAR_SIZE_FACTOR = 1;

  public static final int MNEMONIC_VALIDATION_S = KeyEvent.VK_S;  
  public static final int MNEMONIC_VALIDATION_O = KeyEvent.VK_O;  
  public static final int MNEMONIC_CANCELATION_C = KeyEvent.VK_C;  
    
  public static final long DAY_TIME = (24 * 60 * 60 * 1000);
  public static final char RC = '|'; 
  
  public static final String ARG_LOG_FILE_PREFIX = "logFilePrefix";
  
  public static String notificationMessageToAssert = "";
  
  static final Logger logger = LoggerFactory.getLogger(Globals.class);
  
  public static Application newApplication(boolean withDB, boolean withAdmin, boolean mdi){
    return newApplication(withDB, withAdmin, mdi, null, null);
  }

  public static Application newApplication(boolean withDB, boolean withAdmin, boolean mdi, String[] args){
    return newApplication(withDB, withAdmin, mdi, args, null);
  }

  public static Application newApplication(boolean withDB, boolean withAdmin, boolean mdi, String[] args, String appName){
    return newApplication(withDB, withAdmin, mdi, args, appName, 0); 
  }
 
  public static Application newApplication(boolean withDB, boolean withAdmin, boolean mdi, String[] args, String appName, int trialPeriod){
  	return newApplication(withDB, withAdmin, mdi ? DisplayManager.GUI_NAVIGATOR_MDI : DisplayManager.GUI_NAVIGATOR_MONOFRAME, args, appName, trialPeriod);
  }

  public static Application newApplication(boolean withDB, boolean withAdmin, int navigatorType, String[] args, String appName, int trialPeriod){
    application = new Application(withDB, withAdmin, navigatorType, appName, trialPeriod, args);
    application.init(args);
    return application;
  }

  public static Application newApplication(boolean withDB, boolean withAdmin, int navigatorType, boolean show01Barmaja, String[] args){
    Application application = new Application(withDB, withAdmin, navigatorType, null, 0, args);
    setApp(application);
    application.setShowBarmajaIconAndTitle(show01Barmaja);
    application.init(args);
    return application;
  }

  public static Application newApplication(boolean withDB, boolean withAdmin, int navigatorType, String[] args){
    return newApplication(withDB, withAdmin, navigatorType, true, args);
  }
  
  public static Application getApp() {
  	Application app = null;
  	if(getIFocNotification() != null){
  		app = getIFocNotification().getFocApplication();
  	}else{
  		app = application;
  	}
    return app;
  }

  public static AccessControl getDefaultAccessControl() {
    return (application != null) ? application.getDefaultAccessControl() : null;
  }
  
  public static void applyUserTheme(){
  	getIFocNotification().applyUserTheme();
  }
  
  public static boolean isValo(){
  	boolean valo = true;
  	return valo;
  }

  public static InputStream getInputStream(String path){
    InputStream in = null;
    try{
      File file = new File(path);
      if(file.exists()){
      	Globals.logString("  1-File Exists :"+path);
        in = new FileInputStream(path);  
      }else{
      	Globals.logString("  1-File Does not Exists :"+path);
      }
      if(in == null){
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if(url != null){
        	Globals.logString("  2-Context loader URL :"+url.toString());
          in = url.openStream();  
        }else{
        	Globals.logString("  2-Context loader URL = null");
        }
      }
      if(in == null){
      	Globals.logString("  3-Cannot Try Classs resource");
//  	    ClassResource resource = new ClassResource(path);
//  	    if(resource.getStream() == null) Globals.logString("    The resource.getStream() = null");
//  	    in = resource.getStream().getStream();
//  	    if(in == null) Globals.logString("    The in = null when using Resource.getStream()");
      }
//      if(in == null){
//      	in = file.getClass().getClassLoader().getResourceAsStream(path);
//      }
    }catch(Exception e){
    	Globals.logString("  getInputStream EXCEPTION WAS COUGHT");
      logException(e);
    }
    return in;
  }
  
  public static DBManager getDBManager() {
    Application app = getApp();
    return app != null ? app.getDBManager() : null;
  }

  public static DisplayManager getDisplayManager() {
    Application app = getApp();
    return app != null ? app.getDisplayManager() : null;
  }

  private static PrintStream getLogFile() {
    Application app = getApp();
    return app != null ? app.getLogFile() : null;
  }
  
  public static void logExceptionWithoutPopup(Exception e) {
  	if(ConfigInfo.isLogConsoleActive()){
  		e.printStackTrace();
  	}
  	if (ConfigInfo.isLog4jActive()) {
 		  logger.error("", e);
  	} else {
	    PrintStream logFile = getLogFile();
	    if(logFile != null){
	    	logString(" -- Exception: "+e.getMessage());
	      e.printStackTrace(logFile);
	    }else{
	    	logString("Log File is NULL");
	    }
  	}
//    String mess = e.getMessage();
//    if(mess == null || mess.compareTo("") == 0){
//      mess = new String("Exception occured");
//    }
    
    if(getApp() != null && getApp().isUnitTest()){
    	try {
    		FocLogger.getInstance().addFailure(e.getMessage());
    	}catch(Exception elog) {
    		logString("Exception while Unit failure log");
    	}
//      getApp().exit(true);
      //getApp().getFocTestSuite().exceptionOccured(e);
    }
  }

  private static boolean insideLogException = false;
  public static void logException(Exception e) {
    logExceptionWithoutPopup(e);
    if(!insideLogException){
	    insideLogException = true;
	    try{
		    if(ConfigInfo.isPopupExceptionDialog() && getApp() != null && !getApp().isUnitTest()){
		    	String message = e.getClass().getName()+": "+e.getMessage();
		    	Globals.showNotification("Exception Error", message, IFocEnvironment.TYPE_ERROR_MESSAGE);
		    }
	    }catch(Exception ex){
	    	ex.printStackTrace();
	    }
	    insideLogException = false;
    }
  }

  private static SimpleDateFormat dateFormat = null;
  
  public static SimpleDateFormat getLogFileTimeFormat(){
  	if(dateFormat == null){
  		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
  	}
  	return dateFormat;
  }
  
  private static String logStringFormat(String str, boolean withTime) {
  	if(withTime){
  		if(!insideLogString){
  			insideLogString = true;
  			//String threadID = Thread.currentThread() != null ? String.valueOf(Thread.currentThread().getId()) : "--"; 
  			//str = getLogFileTimeFormat().format(new Date(System.currentTimeMillis())) + " " + threadID + " ["+ getUsername() +"] <" + getSessionID() + ">:" + str;
  			str = "["+ getUsername() +"] <" + getSessionID() + ">:" + str;
  			insideLogString = false;
  		}
  	}
  	return str;
  }
  
  private static boolean insideLogString = false;
  public static void logString(String str, boolean withTime) {
  	logString(str, withTime, false);
  }
  
  private static void logString(String str, boolean withTime, boolean error) {
  	if (ConfigInfo.isLog4jActive()) {
	  	str = logStringFormat(str, withTime);
	  	if (error) {
	  		logger.error(str);
	  	} else {
	  		logger.info(str);
	  	}
  	} else {
	  	if(withTime){
	  		if(!insideLogString){
	  			insideLogString = true;
	  			String threadID = Thread.currentThread() != null ? String.valueOf(Thread.currentThread().getId()) : "--"; 
	  			str = getLogFileTimeFormat().format(new Date(System.currentTimeMillis())) + " " + threadID + " ["+ getUsername() +"] <" + getSessionID() + ">:" + str;
	  			insideLogString = false;
	  		}
	  	}
	    PrintStream logFile = getLogFile();
	    if(logFile != null){ 
	      logFile.println(str);
	      logFile.flush();
	    }
	    if(ConfigInfo.isLogConsoleActive()){
	      System.out.println(str);
	      System.out.flush();
	    }
  	}
  }
  
  public static String getSessionID(){
  	String sessionID = "XX";
  	if(!insideGetSessionID){
  		insideGetSessionID = true;
  		try{
  			sessionID = getIFocNotification() != null ? getIFocNotification().getSessionID() : "NULL";
  		}catch(Exception e){
  			e.printStackTrace();
  		}
  		insideGetSessionID = false;
  	}
  	return sessionID;
  }

  public static String getUsername(){
  	String username = "no user";
  	if(			Globals.getApp() != null 
  			&& 	Globals.getApp().getUser_ForThisSession() != null){
  		username = Globals.getApp().getUser_ForThisSession().getName();
  	}
  	return username;
  }

  public static void logString(String str) {
  	logString(str, ConfigInfo.isLogFileWithTime(), false);
  }
  
  public static void logError(String str) {
  	logString(str, ConfigInfo.isLogFileWithTime(), true);
  }

  public static void logString(StringBuffer str) {
    Globals.logString(str.toString());
  }
  
  public static void logDetail(String str){
  	if(ConfigInfo.isLogDetails()){
  		logString(str);
  	}
  }

  public static void logDebug(String str){
  	if(ConfigInfo.isLogDebug()){
  		logString(str);
  	}
  }

  public static String logMemory(String str) {
    long total = Runtime.getRuntime().totalMemory();
    long free = Runtime.getRuntime().freeMemory();
    long used = total - free;
    
    NumberFormat _numFmt = NumberFormat.getNumberInstance();
    _numFmt.format(total);
    
    str = str+" Used = "+ _numFmt.format(total) + " - " + _numFmt.format(free) + " = "+_numFmt.format(used); 
    logString(str);
    return str;
  }
  
  private static long lastMemoryCheck = 0;
  public static void logMemoryNewThread(String str){
  	if(ConfigInfo.isLogMemoryUsage() && str != null){
  		long nowTime = System.currentTimeMillis();
  		if(lastMemoryCheck == 0 || nowTime - lastMemoryCheck > 1 * 60 * 1000) {
  			lastMemoryCheck = nowTime;
	  		MemoryLogRunnable runnable = new MemoryLogRunnable(str);
		  	Thread t = new Thread(runnable);
		  	t.start();
  		}
  	}
  }
  
	public static void logUserAction(String action){
		try{
			if(action != null){
				StringBuffer buffer = new StringBuffer("User Action LOG: ");
				
				String userForThisSession = null;
				if(Globals.getApp() != null && Globals.getApp().getUser_ForThisSession() != null){
					userForThisSession = Globals.getApp().getUser_ForThisSession().getName();
				}
				buffer.append(userForThisSession);
				buffer.append(" > ");
				buffer.append(action);
				
				Globals.logString(buffer.toString());
			}
		}catch(Exception e){
			logExceptionWithoutPopup(e);
		}
	}
  
  public static FocIcons getIcons() {
    Application app = getApp();
    FocIcons focIcons = app.getFocIcons();
    if (focIcons == null) {
      focIcons = new FocIcons();
    }
    return focIcons;
  }
  
  /**
   * @param application The application to set.
   */
  public static void setApp(Application application) {
    if(getIFocNotification() != null){
    	getIFocNotification().setFocApplication(application);
    }else{
    	Globals.application = application;
    }
  }
  
  public static void printDebugObj(String prefix){
    if(debugObj != null){
      if(prefix != null){
        Globals.logString(prefix+debugObj.getDebugInfo());
      }else{
        Globals.logString(debugObj.getDebugInfo());
      }
    }
  }
  
  public static boolean logFile_CheckLogDir(){
  	boolean error = true;
  	String archDir = ConfigInfo.getLogDir();
  	if(archDir != null && archDir.compareTo("") != 0){
  		File file = new File(archDir);
  		error = !file.exists() || !file.isDirectory();
  	}
  	return error;
  }
  
  public static String logFile_GetFileName(String suffix, String extension){
  	return logFile_GetFileName(ConfigInfo.getLogDir(), suffix, extension);
  }

  public static String logFile_GetTimeStampString(){
  	long time = System.currentTimeMillis();
  	Time currentTime = new Time(time);
  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
  	String archiveName = sdf.format(currentTime);
  	return archiveName;
  }

  public static String logFile_GetFileName(String archDir, String suffix, String extension, boolean timestampFirst){
  	String archiveName = logFile_GetTimeStampString();
  	if(suffix != null && suffix.compareTo("") != 0){
  		suffix = timestampFirst ? "_" + suffix : suffix + "_" ;
  	}
  	String fileName = null;
  	if(timestampFirst){
  		fileName = archDir+"/"+archiveName+suffix+"."+extension;
  	}else{
  		fileName = archDir+"/"+suffix+archiveName+"."+extension;
  	}
  	return fileName;
  }

  public static String logFile_GetFileName(String archDir, String suffix, String extension){
  	return logFile_GetFileName(archDir, suffix, extension, true);
  }
  
  public static String logFile_GetFileName_timeStampLast(String archDir, String suffix, String extension){
  	return logFile_GetFileName(archDir, suffix, extension, false);
  }
  
  public static void setMouseComputing(boolean computing){
  	DisplayManager dispManager = getDisplayManager();
  	if(dispManager != null){
  		dispManager.setMouseComputing(computing);
  	}
  }
  
  private static IEMailSender emailSender = null;
  public static IEMailSender getEmailSender(){
  	return emailSender;
  }
  
  public static void setEmailSender(IEMailSender ieMailSender ){
  	emailSender = ieMailSender;
  }

  public static void showNotification(String notificationMessage, String description, int notificationType){
  	showNotification(notificationMessage, description, notificationType, -1, null);
  }
  
  public static void showNotification(String notificationMessage, String description, int notificationType, int delay, String style){
    if(Globals.getDisplayManager() != null){
      Globals.getDisplayManager().popupMessage(notificationMessage+"-"+description);
    }else if(getIFocNotification() != null){
  		Globals.logString("SHOW_NOTIFICATION:"+notificationMessage+"-"+description);    		
  		getIFocNotification().showNotification(notificationMessage, description, notificationType, delay, style);
    }
  }
  
  public static void popup(FocObject focObj, boolean dialog) {
    if(Globals.getDisplayManager() != null){
    	FPanel panel = focObj.newDetailsPanel(FocObject.DEFAULT_VIEW_ID);
    	if(dialog){
    		Globals.getDisplayManager().popupDialog(panel, panel.getFrameTitle(), true);
    	}else{
    		Globals.getDisplayManager().changePanel(panel);
    	}
    }else if(getIFocNotification() != null){
      getIFocNotification().popup(focObj, dialog);
    }
  }
  
  //may cause problems 
  public static void popup(IFocData focData, boolean dialog, String storageName, int type, String context, String view){
  	if(Utils.isStringEmpty(view)) {
  		view = IXMLViewConst.VIEW_DEFAULT;
  	}

  	if(focData instanceof FocObject){
  		if(type == -1){
  			type = IXMLViewConst.TYPE_FORM;
  		}
  		if(Utils.isStringEmpty(storageName)){
  			storageName = ((FocObject)focData).getThisFocDesc().getStorageName();
  		}
  		if(Utils.isStringEmpty(context)){
  			context = ((FocObject)focData).getThisFocDesc().focDesc_getGuiContext();
  		}
  	}else if (focData instanceof FocList){
  		if(type == -1){
  			type = IXMLViewConst.TYPE_TABLE;
  		}
  		if(Utils.isStringEmpty(storageName)){
  			storageName = ((FocList)focData).getFocDesc().getStorageName();
  		}
  		if(Utils.isStringEmpty(context)){
  			context = ((FocList)focData).getFocDesc().focDesc_getGuiContext();
  		}
  	}
  	getIFocNotification().popup(focData, dialog, storageName, type, context, view);
  }
  
  public static IFocEnvironment getIFocNotification() {
    return iFocDisplay;
  }

  public static void setIFocNotification(IFocEnvironment iFocNotification) {
    Globals.iFocDisplay = iFocNotification;
  }
  
  public static Object popupDialog(OptionDialog optionDialog){
  	Object object = null;
    if(Globals.getDisplayManager() != null){
    	String optionNameReturned = FGOptionPane.popupOptionPane_Options(optionDialog);
    	optionDialog.executeOption(optionNameReturned);
    }else if(getIFocNotification() != null){
    	object = getIFocNotification().popupOptionDialog(optionDialog);
    }
    return object;
  }
  
  public static boolean isTouchDevice(){
//  	return Page.getCurrent().getWebBrowser().isTouchDevice();
	  return false;
  }
  
	private static class MemoryLogRunnable implements Runnable{
		
		private String message = null;
		
		public MemoryLogRunnable(String message) {
			this.message = message;
		}
				
		@Override
		public void run() {
			try{
				if(message != null){
					//Thread.sleep(1000);//20150822
					System.gc();//20150822
					String memoryMeasures = Globals.logMemory("MEMORY LOG: ");
//					Globals.logString("MEMORY LOG : "+message+" > "+memoryMeasures);
				}
			}catch (Exception e){
				Globals.logException(e);//20150822
			}
		}
	}

	public static String getNotificationMessageToAssert() {
		return notificationMessageToAssert;
	}

	public static void setNotificationMessageToAssert(String notificationMessageToAssert) {
		Globals.notificationMessageToAssert = notificationMessageToAssert;
	}
}
