/*
 * Created on Jul 20, 2005
 */
package com.foc;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.foc.admin.FocUserDesc;
import com.foc.business.status.StatusHolderDesc;
import com.foc.gui.DisplayManager;
import com.foc.gui.MainFrame;
import com.foc.gui.table.FTable;
import com.foc.util.Encryptor;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class ConfigInfo {

  public static final String JDBC_DRIVERS        = "jdbc.drivers";
  public static final String JDBC_URL            = "jdbc.url";
  public static final String JDBC_USERNAME       = "jdbc.username";
  public static final String JDBC_PASSWORD       = "jdbc.password";
  public static final String JDBC_XPASSWORD      = "jdbc.Xpassword";
  public static final String JDBC_SERVER_VERSION = "jdbc.server.version";
  public static final String JDBC_NAMESPACE      = "jdbc.namespace";

  private static boolean loaded = false;

  private static String  appDirectory  = null;

  private static String  jdbcDrivers   = null;
  private static String  jdbcURL       = null;
  private static String  jdbcSchema    = null;
  private static String  jdbcNamespace = null;
  private static String  jdbcUserName  = null;
  private static String  jdbcPassword  = null;
  private static String  jdbcXPassword = null;
  private static int     jdbcServerVersion = 0;
  private static String  syncMode      = null;//N none, S server, R remote

  private static String  userName          = null;
  private static String  encriptedPassword = null;
  private static String  password          = null;
  private static boolean createAdminUserIfNotExist = false;
  private static boolean exitWithoutValidationPrompt = false;
  private static int     passwordPolicy = 0;
  private static int     passwordEncryptionMethod = FocUserDesc.PASSWORD_ENCRYPTION_METHOD_1;

  private static boolean allowNullProperties = false;
  private static int accountLockThreshold    = 10;

  private static long    dbConnectionDuration = 0;

  private static String  windowTitle   = null;

  private static int guiNavigatorWidth  = 0;
  private static int guiNavigatorHeight = 0;
  private static int fontSize = 14;

  private static String  dataModelFileName           = null;
  private static String  helpURL                     = null;
  private static String  logDir                      = null;
  private static boolean rtl                         = false;
  private static String  language                    = null;
	private static boolean logDetails                  = false;
  private static boolean logDebug                    = false;
  private static boolean logConsoleActive            = true;
  private static boolean logDBRequest                = true;
  private static boolean logDBSelect                 = true;
  private static boolean logFileActive               = false;
  private static boolean logMemoryUsage              = false;
  private static boolean logFileWithTime             = false;
  private static boolean log4jActive                 = false;
  private static boolean dbLogActive                 = true;
  private static boolean unitDevMode                 = true;
  private static boolean devMode                     = false;
  private static boolean contextHelpActive           = false;
  private static boolean unitAllowed                 = false;
  private static boolean popupExceptionDialog        = false;
  private static boolean showStatusColumn            = false;
  private static boolean performanceActive           = false;
  private static boolean withLicenseBooking          = true;
  private static boolean showFullScreenButton        = false;
  private static boolean showSignatureButton         = true;
  private static String  codeProjectPath             = null;
  private static String  focDataServletURL           = null;
  private static String  focWebServerClassName       = null;
  private static String  focWebUIClassName           = null;
  private static boolean adaptDataModelAtStartup     = true;
  private static boolean adaptEnabled                = true;
  private static boolean adaptIndexesEnabled         = true;
  private static boolean adaptConstraints            = true;
  private static String  cloudStorageClassName       = null;
  private static String  logListenerClassName        = null;
  private static String  encryptionClassName         = null;
  private static boolean keepFocObjectArrayInFocDesc = false;
  private static boolean contextHelpAllowed          = false;
  private static boolean simulationAllowed          = false;

  private static boolean comboBoxShowDropDownEvenWhenDisabled = false;

  private static boolean logListeningEnabled = false;
  private static boolean useLocalEmailClientForNotification = false;
  private static boolean allowAddInsideComboBox  = true;
  private static boolean shrinkDBNames = false;
  private static boolean showStageNameOnValidationLayoutButton = true;

  private static int     uploadMaxSize = 6048576;

  private static boolean allowCSVExport          = true;
  private static boolean allowEXCELExport        = true;

  private static String defaultConfigInfoFilePath = "testDB/defaultConfig.properties";

  private static String reportFileFullPath = "d:/eclipseworkspace_c3/c3plugin_mg/defaultConfig.properties";

  private static boolean reportingLayout_RTFExport  = true;
  private static boolean reportingLayout_wordExport = true;
  private static boolean reportingLayout_emailSend  = true;

  private static String tempDownloadFolder = null;
  private static String blobStorageDirectory = null;

  private static boolean refreshCachedLists = false;
  private static boolean logOpenEvent       = true;

  private static boolean oracleListAggCLOB = false;

  private static String jwtTokenAlgorithmKey = null;
  private static String allowedUrlsForAdmin = null;

  private static Properties props = null;

  public static String getProperty(String key){
  	String val = Globals.getApp() != null ? Globals.getApp().getCommandLineArgument(key) : null;
  	if(val == null || val.isEmpty()){
  		val = props != null ? props.getProperty(key) : null;
  	}

  	String valueFromEnvVariables = null;
  	try {

	  	if (val != null && val.startsWith("${") && val.endsWith("}") && val.length() > 3) {
	  		String varName = val.substring(2, val.length()-1);
	  		String varDefaultValue = null;

	  		int indexOfColumn = varName.indexOf(":");
	  		if (indexOfColumn > 0) {
	  			varDefaultValue = varName.substring(indexOfColumn+1);
	  			varName = varName.substring(0, indexOfColumn);
	  		}

	  		valueFromEnvVariables = System.getenv(varName);
	  		if (valueFromEnvVariables != null) {
	  			val = valueFromEnvVariables;
	  		} else if (varDefaultValue != null) {
	  			val = varDefaultValue;
	  		}
	  	}

  	} catch (Exception e) {
  		valueFromEnvVariables = null;
  	}

  	return val;
  }

  public static boolean isGuiRTL(){
  	return rtl;
  }

  public static boolean isArabic(){
  	return isGuiRTL();
  }

  public static void loadFile() {

  	//Display all Environment Variables
  	//---------------------------------
//    Map <String, String> map = System.getenv();
//    for (Map.Entry <String, String> entry: map.entrySet()) {
//        System.out.println("Variable Name:- " + entry.getKey() + " Value:- " + entry.getValue());
//    }
  	//---------------------------------

    if(!loaded){
      loaded = true;
      try{
        props = new Properties();
        //FileInputStream in = null;
        InputStream in = null;

//        String directory = Globals.getApp().getDefaultAppDirectory();
//        String environment = Globals.getApp().getDefaultEnvironment();
        String homePath = "";//(directory != null && environment != null) ? directory+"/"+environment : "";

        Globals.logString("A-reading file "+homePath+"properties/config.properties");
        in = Globals.getInputStream(homePath+"/properties/config.properties");

//        if(Globals.getApp().isWithRegistry() && in == null){
//          in = Globals.getInputStream(defaultConfigInfoFilePath);
//        }

        if( in == null ){
        	Globals.logString("B-Trying properties/config.properties");
          in = Globals.getInputStream("properties/config.properties");
        }
        if( in == null ){
        	Globals.logString("C-Trying /properties/config.properties");
          in = Globals.getInputStream("/properties/config.properties");
        }
        if( in == null ){
        	Globals.logString("D-Trying ./properties/config.properties");
          in = Globals.getInputStream("./properties/config.properties");
        }
        if( in == null ){
        	Globals.logString("E-Trying configuration.properties");
          in = Globals.getInputStream("configuration.properties");
        }

        if(in != null){
  	      props.load(in);
  	      in.close();
        }else{
        	Globals.logString("--Could not load properties file!");
        	if(Globals.getDisplayManager() != null){
        		Globals.getDisplayManager().popupMessage("--Could not load config file");
        	}

        	String path = ConfigInfo.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        	String decodedPath = URLDecoder.decode(path, "UTF-8");
        	Globals.logString("--This JAR path is:"+decodedPath);
        }

        String str = null;

        appDirectory  = getProperty("appDirectory");

        jdbcDrivers   = getProperty(JDBC_DRIVERS);
        jdbcURL       = getProperty(JDBC_URL);
        jdbcUserName  = getProperty(JDBC_USERNAME);
        jdbcPassword  = getProperty(JDBC_PASSWORD);
        jdbcXPassword = getProperty(JDBC_XPASSWORD);
        jdbcNamespace = getProperty(JDBC_NAMESPACE);

        str = getProperty(JDBC_SERVER_VERSION);
        if(str != null) jdbcServerVersion = Utils.parseInteger(str, 0);

        Globals.logString("Drivers: "+jdbcDrivers);
        Globals.logString("URL: "+jdbcURL);

        try{
	        if( jdbcURL.indexOf("mysql") != -1 ){//MySQL
	        	//jdbc.url=jdbc:mysql://localhost:3306/marblestone?zeroDateTimeBehavior=convertToNull
	          String localhost = jdbcURL.substring(jdbcURL.indexOf("//", 10)+2, jdbcURL.indexOf(':', 13));
	          String port = jdbcURL.substring(jdbcURL.indexOf(':', 13)+1, jdbcURL.indexOf('/', 13));
	          String schema = jdbcURL.substring(jdbcURL.indexOf('/', 13)+1, jdbcURL.length());

	          jdbcSchema = schema.split("\\?")[0];
	        }else if( jdbcURL.indexOf("sqlserver") != -1 ){//SQL Server
	          String localhost = jdbcURL.substring(jdbcURL.indexOf("//", 10)+2, jdbcURL.indexOf(':', 16));
	          String port = jdbcURL.substring(jdbcURL.indexOf(':', 16)+1, jdbcURL.indexOf(';', 16));

	          int schemaIndex = jdbcURL.indexOf("databaseName=");
	          if(schemaIndex > 0){
	          	schemaIndex += "databaseName=".length();

	          	int schemaEnd = jdbcURL.indexOf(";", schemaIndex);
	          	if(schemaEnd < schemaIndex) schemaEnd = jdbcURL.length();

	          	jdbcSchema = jdbcURL.substring(schemaIndex, schemaEnd);
	          }
	        }else if( jdbcURL.indexOf("postgresql") != -1 ){//postgresql
	          String localhost = jdbcURL.substring(jdbcURL.indexOf("//")+2, jdbcURL.indexOf(':', 18));
	          String port = jdbcURL.substring(jdbcURL.indexOf(':', 18)+1, jdbcURL.indexOf('/', 18));
	          jdbcSchema = jdbcURL.substring(jdbcURL.indexOf('/', 18)+1, jdbcURL.length());
	        }
        }catch(Exception e){
        	Globals.logException(e);
        }

        dataModelFileName = getProperty("dataModelFileName");

        codeProjectPath = getProperty("code.project.root");

        focDataServletURL = getProperty("focDataServletURL");

        tempDownloadFolder = getProperty("tempDownloadFolder");
        blobStorageDirectory = getProperty("blobStorageDirectory");

        jwtTokenAlgorithmKey = getProperty("jwtTokenAlgorithmKey");
        allowedUrlsForAdmin = getProperty("allowedUrlsForAdmin");

        String serverClassName = getProperty("focWebServerClassName");
        if(serverClassName == null){
        	serverClassName = "com.foc.web.server.FocWebServer";
        }
        focWebServerClassName = serverClassName;
        Globals.logString("WebServerClass: "+focWebServerClassName);

        focWebUIClassName = getProperty("focWebUIClassName");
        Globals.logString("focWebUIClassName - Used un REST API backend: "+focWebUIClassName);

        String cloudClassName = getProperty("cloudStorageClass");
        if(cloudClassName == null){
        	cloudClassName = "com.foc.cloudStorage.FocCloudStorage_LocalDisc";
        }
        setCloudStorageClassName(cloudClassName);

        //FILE_ENCRYPTION
        String encryptionClassName = getProperty("encryptionClass");
        setEncryptionClassName(encryptionClassName);

        String logListenerClassName = getProperty("logListenerClassName");
        setLogListenerClassName(logListenerClassName);

        String logListeningEnabledString = getProperty("logListeningEnabled");
        if(!Utils.isStringEmpty(logListeningEnabledString)) {
        	if(logListeningEnabledString.trim().equals("1") || logListeningEnabledString.trim().toLowerCase().equals("true")) {
        		logListeningEnabled = true;
        	}
        }

        //Setting default navigator size to full screen
        //Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        //guiNavigatorWidth = (int)screenDim.getWidth();
        //guiNavigatorHeight = (int)screenDim.getHeight();
        //Globals.logString(new String(guiNavigatorWidth + ","+guiNavigatorHeight));
        //System.out.print(new String(guiNavigatorWidth + ","+guiNavigatorHeight));

        windowTitle = getProperty("gui.windowTitle");
        str = getProperty("gui.navigator.width");
        if(str != null) guiNavigatorWidth = Integer.valueOf(str).intValue();
        str = getProperty("gui.navigator.height");
        if(str != null) guiNavigatorHeight = Integer.valueOf(str).intValue();
        str = getProperty("gui.table.maxWidth");
        if(str != null) FTable.MAX_WIDTH = Integer.valueOf(str).intValue();

        str = getProperty("uploadMaxSize");
        if(str != null) uploadMaxSize = Integer.valueOf(str).intValue();

        str = getProperty("devMode");
        devMode = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("contextHelpActive");
        contextHelpActive = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("localEmailClient");
        useLocalEmailClientForNotification = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("unitDevMode");
        unitDevMode = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("unitAllowed");
        unitAllowed = str != null ? str.compareTo("1") == 0 : false;

        if(logDir == null){//In few cases we set the logDir from the main without passing by the config.properties
  	      logDir = getProperty("log.dir");
  	      if(logDir == null) logDir = ".";
        }

        helpURL = getProperty("help.URL");

        syncMode = getProperty("sync.mode");

        str = getProperty("log.fileActive");
        logFileActive = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("log.memoryUsage");
        logMemoryUsage = str != null ? str.compareTo("1") == 0 : false;//Default value is false log Memory usage

        str = getProperty("showFullScreenButton");
        showFullScreenButton = str != null ? str.compareTo("1") == 0 : false;//Default value is false showFullScreenButton

        str = getProperty("showSignatureButton");
        showSignatureButton = str != null ? str.compareTo("0") != 0 && !str.trim().toLowerCase().equals("false") : true;//Default value is true

        str = getProperty("allowAddInsideComboBox");
        allowAddInsideComboBox = str != null ? str.compareTo("1") == 0 : true;//Default value is true

        str = getProperty("shrinkDBNames");
        shrinkDBNames = str != null ? str.compareTo("1") == 0 : false;//Default value is false

        str = getProperty("allowCSVExport");
        if(str != null && str.compareTo("0") == 0){//Default value is true
        	allowCSVExport = false;
				}

        str = getProperty("allowEXCELExport");
        if(str != null && str.compareTo("0") == 0){//Default value is true
        	allowEXCELExport = false;
        }

        str = getProperty("log.fileWithTime");
        logFileWithTime = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("log.log4j.active");
        log4jActive = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("log.dbLogActive");
        dbLogActive = str != null ? str.compareTo("1") == 0 : true;

        str = getProperty("log.ConsoleActive");
        logConsoleActive = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("log.dbRequest");
        logDBRequest = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("perf.active");
        performanceActive = str != null ? str.compareTo("1") == 0 : false;

  //      str = getProperty("l");
  //      withLicenseBooking = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("log.dbSelect");
        logDBSelect = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("adaptDataModelAtStartup");
        adaptDataModelAtStartup = str != null ? str.compareTo("1") == 0 : true;

        str = getProperty("adaptEnabled");
        adaptEnabled = str != null ? str.compareTo("1") == 0 : true;

        str = getProperty("adaptIndexesEnabled");
        adaptIndexesEnabled = str != null ? str.compareTo("1") == 0 : true;

        str = getProperty("adaptConstraints");
        adaptConstraints = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("log.details");
        logDetails = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("gui.rtl");
        rtl = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("keepFocObjectArrayInFocDesc");
        keepFocObjectArrayInFocDesc = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("comboBoxShowDropDownEvenWhenDisabled");
        comboBoxShowDropDownEvenWhenDisabled = str != null ? str.compareTo("1") == 0 : false;

        language = getProperty("gui.language");

        str = getProperty("log.debug");
        logDebug = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("log.popupExceptionDialog");
        popupExceptionDialog = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("debug.showStatusColumn");
        showStatusColumn = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("passwordPolicy");
        if(str != null) passwordPolicy = Utils.parseInteger(str, 0);

          str = getProperty("passwordEncryptionMethod");
          if(str != null) passwordEncryptionMethod = Utils.parseInteger(str, 0);
          
          str = getProperty("dbConnectionDuration");
        if(str != null) dbConnectionDuration = Utils.parseLong(str, 0);

        str = getProperty("statusTitle.proposal");
        if(str != null && !str.isEmpty()){
        	StatusHolderDesc.PRINTED_LABEL_FOR_PROPOSAL = str;
        }

        str = getProperty("foc.showStageNameOnValidationLayoutButton");
        showStageNameOnValidationLayoutButton = str != null ? str.compareTo("1") == 0 : true;

        str = getProperty("reportingLayout.wordExport");
        reportingLayout_wordExport = str != null ? str.compareTo("1") == 0 : true;

        str = getProperty("reportingLayout.RTFExport");
        reportingLayout_RTFExport = str != null ? str.compareTo("1") == 0 : true;

        str = getProperty("reportingLayout.emailSend");
        reportingLayout_emailSend = str != null ? str.compareTo("1") == 0 : true;

        str = getProperty("refreshCachedLists");
        refreshCachedLists = str != null ? str.compareTo("1") == 0 : false;

        str = getProperty("logOpenEvent");
        logOpenEvent = str != null ? str.compareTo("1") == 0 : true;

        str = getProperty("oracleListAggCLOB");
        oracleListAggCLOB = str != null ? str.compareTo("1") == 0 : true;

        userName          = getProperty("userLogin");
        password          = getProperty("password");
        encriptedPassword = getProperty("encriptedPassword");
      	if(encriptedPassword == null){
      		encriptedPassword = password;
      		encriptedPassword = Encryptor.encrypt_MD5(String.valueOf(encriptedPassword));
      	}

      	String exitWithoutValidationPromptString = getProperty("exitWithoutValidationPrompt");
      	if(exitWithoutValidationPromptString != null
      			&& (exitWithoutValidationPromptString.toLowerCase().equals("true") || exitWithoutValidationPromptString.equals("1"))) {
      		exitWithoutValidationPrompt = true;
      	}

      	String allowNullPropertiesString = getProperty("allowNullProperties");
      	if(allowNullPropertiesString != null) {
      		allowNullProperties = allowNullPropertiesString.equals("1");
      	}

				// failedLoginsNbr
				String accountLockThresholdTxt = getProperty("accountLockThreshold");
				if (accountLockThresholdTxt != null && !Utils.isStringEmpty(accountLockThresholdTxt)) {
					setAccountLockThreshold(Utils.parseInteger(accountLockThresholdTxt, 0));
				}

      	String createAdminUserIfNotExistString = getProperty("createAdminUserIfNotExist");
      	createAdminUserIfNotExist = 		createAdminUserIfNotExistString != null
      															&& 	(
      																	   createAdminUserIfNotExistString.toUpperCase().equals("TRUE")
      																	|| createAdminUserIfNotExistString.equals("1")
      																	);

        reportFileFullPath = getProperty("report.fileFullPath");

        str = getProperty("gui.font.size");
        if(str != null){
          fontSize = Utils.parseInteger(str, 14);
        }
        DisplayManager dm = Globals.getDisplayManager();
        if(dm != null){
        	MainFrame mainFrame = dm.getMainFrame();
        	if(mainFrame != null){
        		mainFrame.setWindowTitle();
        	}
        }
      }catch(Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(),
            "Error during config file load : properties/config.properties\n" + e.getMessage(),
            "01Barmaja",
            JOptionPane.ERROR_MESSAGE,
            null);
      }
    }
  }

  public static String getDataModelFileName(){
  	return dataModelFileName;
  }

  public static String getTempDownloadFolder(){
  	return tempDownloadFolder;
  }

  public static String getBlobStorageDirectory(){
  	return blobStorageDirectory;
  }

  public static String getAllowedUrlsForAdmin() {
  	return allowedUrlsForAdmin;
  }

  public static String getJWTTokenAlgorithmKey() {
  	return jwtTokenAlgorithmKey;
  }

  public static void setJWTTokenAlgorithmKey(String key) {
  	jwtTokenAlgorithmKey = key;
  }

  public static String getWindowTitle() {
    return windowTitle;
  }

  public static String getAppDirectory() {
    return appDirectory;
  }

  public static int getGuiNavigatorHeight() {
    return guiNavigatorHeight;
  }

  public static int getGuiNavigatorWidth() {
    return guiNavigatorWidth;
  }

  public static void setGuiNavigatorHeight(int guiNavigatorHeight) {
    ConfigInfo.guiNavigatorHeight = guiNavigatorHeight;
  }

  public static void setGuiNavigatorWidth(int guiNavigatorWidth) {
    ConfigInfo.guiNavigatorWidth = guiNavigatorWidth;
  }

  public static String getJdbcDrivers() {
    return jdbcDrivers;
  }

  public static String getJdbcPassword() {
    return jdbcPassword;
  }

  public static String getJdbcXPassword() {
    return jdbcXPassword;
  }

  public static String getJdbcURL() {
    return jdbcURL;
  }

  public static String getJdbcSchema(){
  	return jdbcSchema;
  }

  public static String getJdbcUserName() {
    return jdbcUserName;
  }

  public static String getJdbcNamespace() {
    return jdbcNamespace;
  }

  public static int getJdbcServerVersion() {
    return jdbcServerVersion;
  }

  public static boolean isDevMode() {
    return devMode;
  }

  public static boolean isContextHelpActive() {
    return contextHelpActive;
  }

  public static boolean isUnitDevMode() {
    return unitDevMode;
  }

  public static boolean isUnitAllowed() {
    return unitAllowed;
  }

  public static void setUnitAllowed(boolean allowed) {
    unitAllowed = allowed;
  }

  public static String getLogDir(){
  	return logDir;
  }

  public static String getHelpURL(){
  	return helpURL;
  }

  public static void setLogDir(String logDir){
  	ConfigInfo.logDir = logDir;
  }

  public static String getSyncMode(){
  	return syncMode != null ? syncMode : "N";//N none, S server, R remote
  }

  public static boolean isLogFileActive() {
    return logFileActive;
  }

  public static boolean isLogFileWithTime() {
  	return logFileWithTime;
  }

  public static boolean isLog4jActive() {
  	return log4jActive;
  }

  public static void setLogFileWithTime(boolean withTime) {
  	logFileWithTime = withTime;
  }

  public static boolean isLogConsoleActive() {
    return logConsoleActive;
  }

  public static void setLogConsoleActive(boolean logConsActive) {
    logConsoleActive = logConsActive;
  }

  public static boolean isLogDBRequestActive() {
    return logDBRequest;
  }

  public static boolean isLogDBSelectActive() {
    return logDBSelect;
  }

  public static boolean isAdaptDataModelAtStartup() {
    return adaptDataModelAtStartup;
  }

  public static boolean isAdaptEnabled() {
    return adaptEnabled;
  }

  public static boolean isAdaptIndexesEnabled() {
    return adaptIndexesEnabled;
  }

  public static boolean isAdaptConstraints() {
    return adaptConstraints;
  }

  public static boolean isShowStatusColumn() {
    return showStatusColumn;
  }

  public static int getPasswordPolicy() {
    return passwordPolicy;
  }

    public static int getPasswordEncryptionMethod() {
        return passwordEncryptionMethod;
    }

    public static boolean isPopupExceptionDialog() {
    return popupExceptionDialog;
  }

  public static boolean isLogDetails(){
  	return logDetails;
  }

  public static boolean isLogDebug(){
  	return logDebug;
  }

  public static boolean isDebugMode(){
  	return isLogDebug();
  }

  public static int getFontSize() {
    return fontSize;
  }
  public static void setFontSize(int iFont){
    fontSize = iFont;
  }

  public static String getReportFileFullPath() {
    return ConfigInfo.reportFileFullPath;
  }

  public static void setReportFileFullPath(String reportFileFullPath) {
    ConfigInfo.reportFileFullPath = reportFileFullPath;
  }

  public static boolean isPerformanceActive() {
		return performanceActive;
	}

	public static void setPerformanceActive(boolean performanceActive) {
		ConfigInfo.performanceActive = performanceActive;
	}

	/**
	 * @return the withLicenseBooking
	 */
	public static boolean isWithLicenseBooking() {
		return withLicenseBooking;
	}

	public static void setWithLicenseBooking(boolean b) {
		withLicenseBooking = b;
	}

	public static boolean isForDevelopment() {
		return codeProjectPath != null && !codeProjectPath.isEmpty();
	}

	/**
	 * @return the codeProjectPath
	 */
	public static String getCodeProjectPath() {
		return codeProjectPath;
	}

	public static String getFocDataServletURL() {
		return focDataServletURL;
	}

  public static String getFocWebServerClassName() {
	  return focWebServerClassName;
	}

  public static String getFocWebUIClassName() {
	  return focWebUIClassName;
	}

	public static boolean isRemoveUndeclaredIndexesDuringAdaptDataModel(){
		return false;
	}

	public static String getUsername(){
		return userName;
	}

	public static String getPassword(){
		return password;
	}

	public static String getEncryptedPassword(){
		return encriptedPassword;
	}

	public static String getCloudStorageClassName() {
		return cloudStorageClassName;
	}

	public static void setCloudStorageClassName(String cloudStorageClassName) {
		ConfigInfo.cloudStorageClassName = cloudStorageClassName;
	}

	public static boolean isLogMemoryUsage() {
		return logMemoryUsage;
	}

	public static void setLogMemoryUsage(boolean logMemoryUsage) {
		ConfigInfo.logMemoryUsage = logMemoryUsage;
	}

	public static boolean isUseLocalEmailClientForNotification() {
		return useLocalEmailClientForNotification;
	}

	public static boolean isSimulationAllowed(){
		return simulationAllowed ;
	}

	public static boolean isContextHelpAllowed(){
		return contextHelpAllowed ;
	}

	public static void setUseLocalEmailClientForNotification(boolean useLocalEmailClientForNotification) {
		ConfigInfo.useLocalEmailClientForNotification = useLocalEmailClientForNotification;
	}

	public static boolean showFullScreenButton(){
		return showFullScreenButton;
	}

	public static boolean isAllowAddInsideComboBox() {
		return allowAddInsideComboBox;
	}

	public static void setAllowAddInsideComboBox(boolean allowAddInsideComboBox) {
		ConfigInfo.allowAddInsideComboBox = allowAddInsideComboBox;
	}

	public static boolean isAllowCSVExport() {
		return allowCSVExport;
	}

	public static void setAllowCSVExport(boolean allowCSVExport) {
		ConfigInfo.allowCSVExport = allowCSVExport;
	}

	public static boolean isAllowEXCELExport() {
		return allowEXCELExport;
	}

	public static void setAllowEXCELExport(boolean allowEXCELExport) {
		ConfigInfo.allowEXCELExport = allowEXCELExport;
	}

	public static boolean isCreateAdminUserIfNotExist() {
		return createAdminUserIfNotExist;
	}

	public static void setCreateAdminUserIfNotExist(boolean createAdminUserIfNotExist) {
		ConfigInfo.createAdminUserIfNotExist = createAdminUserIfNotExist;
	}

	public static boolean isShrinkDBNames() {
		return shrinkDBNames;
	}

	public static boolean comboBoxShowDropDownEvenWhenDisabled(){
		return comboBoxShowDropDownEvenWhenDisabled;
	}

  public static String getLanguage() {
		return language;
	}

	public static void setLanguage(String language) {
		ConfigInfo.language = language;
	}

	public static boolean isShowSignatureButton() {
		return showSignatureButton;
	}

	public static boolean isShowStageNameOnValidationLayoutButton() {
		return showStageNameOnValidationLayoutButton;
	}

	public static void setShowStageNameOnValidationLayoutButton(boolean showStageNameOnValidationLayoutButton) {
		ConfigInfo.showStageNameOnValidationLayoutButton = showStageNameOnValidationLayoutButton;
	}

	public static boolean isLogListeningEnabled() {
		return ConfigInfo.logListeningEnabled;
	}

	public static String getLogListenerClassName() {
		return logListenerClassName;
	}

	public static void setLogListenerClassName(String logListenerClassName) {
		ConfigInfo.logListenerClassName = logListenerClassName;
	}

	public static boolean isReportingLayout_RTFExport() {
		return reportingLayout_RTFExport;
	}

	public static boolean isReportingLayout_WordExport() {
		return reportingLayout_wordExport;
	}

	public static void setReportingLayout_WordExport(boolean reportingLayout_wordExport) {
		ConfigInfo.reportingLayout_wordExport = reportingLayout_wordExport;
	}

	public static boolean isReportingLayout_EmailSend() {
		return reportingLayout_emailSend;
	}

	public static void setReportingLayout_EmailSend(boolean reportingLayout_emailSend) {
		ConfigInfo.reportingLayout_emailSend = reportingLayout_emailSend;
	}

	public static int getUploadMaxSize() {
		return uploadMaxSize;
	}

	//FILE_ENCRYPTION
	public static String getEncryptionClassName() {
		return encryptionClassName;
	}

	public static void setEncryptionClassName(String encryptionClassName) {
		ConfigInfo.encryptionClassName = encryptionClassName;
	}

	public static boolean isKeepFocObjectArrayInFocDesc() {
		return keepFocObjectArrayInFocDesc;
	}

	public static void setKeepFocObjectArrayInFocDesc(boolean keepFocObjectArrayInFocDesc) {
		ConfigInfo.keepFocObjectArrayInFocDesc = keepFocObjectArrayInFocDesc;
	}

	public static boolean isExitWithoutValidationPrompt() {
		return exitWithoutValidationPrompt;
	}

	public static boolean isRefreshCachedLists() {
		return refreshCachedLists;
	}

	public static boolean isLogOpenEvent() {
		return logOpenEvent;
	}

	public static boolean isOracleListAggCLOB() {
		return oracleListAggCLOB;
	}

	public static long getDbConnectionDuration() {
		return dbConnectionDuration;
	}

	public static boolean isAllowNullProperties() {
		return allowNullProperties;
	}

	public static int getAccountLockThreshold() {
		return accountLockThreshold;
	}

	public static void setAccountLockThreshold(int accountLockThreshold) {
		ConfigInfo.accountLockThreshold = accountLockThreshold;
	}

	public static boolean isDbLogActive() {
		return dbLogActive;
	}
}
