/*
 * Created on 17 feb 2004
 */
package com.foc;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.sql.Date;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.foc.business.adrBook.AdrBookParty;
import com.foc.business.adrBook.AdrBookPartyDesc;
import com.foc.business.company.UserCompanyRightsDesc;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.business.workflow.rights.RightLevel;
import com.foc.business.workflow.rights.RightLevelDesc;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.foc.access.AccessControl;
import com.foc.access.FocLogger;
import com.foc.admin.AdminModule;
import com.foc.admin.FLoginPanel;
import com.foc.admin.FocGroup;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.admin.FocUserGuiDetailsPanel;
import com.foc.admin.FocVersion;
import com.foc.admin.RightsByLevel;
import com.foc.admin.UserSession;
import com.foc.api.IFocEncryptor;
import com.foc.business.BusinessModule;
import com.foc.business.calendar.FCalendar;
import com.foc.business.company.Company;
import com.foc.business.company.CompanyDesc;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.business.notifier.FocNotificationManager;
import com.foc.business.notifier.actions.FocNotifActionFactory;
import com.foc.business.notifier.manipulators.FocNotificationEventFactory;
import com.foc.business.photoAlbum.PhotoAlbumManagmentModule;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.implementation.WFLogDesc;
import com.foc.cloudStorage.IFocCloudStorage;
import com.foc.dataSource.IFocDataSource;
import com.foc.dataSource.store.DataStore;
import com.foc.db.DBManager;
import com.foc.db.IDBReloader;
import com.foc.depricatedUnit.UnitFactory;
import com.foc.desc.FocDesc;
import com.foc.desc.FocDescMap;
import com.foc.desc.FocDescMap_ByFocObjectClassName;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocModule;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FObjectField;
import com.foc.desc.parsers.pojo.FocDescDeclaration_PojoBased_WFLog;
import com.foc.desc.parsers.pojo.PojoFocDesc;
import com.foc.desc.parsers.xml.FocDescDeclaration_XMLBased_WFLog;
import com.foc.desc.parsers.xml.XMLFocDesc;
import com.foc.fUnit.FocTestSuite;
import com.foc.gui.DisplayManager;
import com.foc.list.FocList;
import com.foc.log.FocLogEvent;
import com.foc.log.FocLogListener;
import com.foc.log.ILoggedHashContainer;
import com.foc.menu.FMenu;
import com.foc.menu.FMenuItem;
import com.foc.menu.FMenuList;
import com.foc.performance.PerfManager;
import com.foc.property.FMultipleChoice;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.saas.manager.SaaSConfig;
import com.foc.serializer.FSerializerDictionary;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class Application {

    public static final String ARG_BLOB_STORAGE_FOLDER_PREFIX = "blobStorageFolderPrefix";
    public static final int LOGIN_WAITING = 1;
    public static final int LOGIN_VALID = 2;
    public static final int LOGIN_ADMIN = 3;
    public static final int LOGIN_WRONG = 4;
    public static final String REGISTRY_PARENT_APPLICATION_NODE_NAME = "01barmaja";
    public static final String REGISTRY_APPLICATION_DIRECTORY = "directory";
    public static final String REGISTRY_APPLICATION_ENVIRONMENT = "lastenv";
    public static final String REGISTRY_APPLICATION_INSTALL_DATE = "install_date";
    public static final String DEFAULT_COMPANY_NAME = "EMPTY";
    public static final int PROCESS_TYPE_DESKTOP_CLIENT = 0;
    public static final int PROCESS_TYPE_WEB_SERVER = 1;
    public static final int PROCESS_TYPE_WEB_CLIENT = 2;
    private static int debug = 1;
    public boolean isUnitTest = false;
    public FSerializerDictionary htmlGeneratorDictionary = null;
    private String url = null;
    private DataStore dataStore = null;//Contains FocLists and Singleton FocObjects
    private FocDescMap focDescMap = null;//Contains the FocDesc definitions
    private FocDescMap_ByFocObjectClassName focDescMap_ByFocObjectClassName = null;
    private HashMap<String, FocDesc> reportConfigFocDescMap = null;
    private FocLogger focLogger = null;//The Logger
    private FocNotificationEventFactory focNEF = null;
    private FocNotifActionFactory notifActionFactory = null;
    private SaaSConfig appConfiguration = null;
    private IFocDataSource dataSource = null;
    private IFocCloudStorage cloudStorage = null;
    private String cloudStorageDirectory = null;
    private boolean cloudStorage_TryToGet = true;
    private PerfManager perfManager = null;
    private DBManager dbManager = null;
    private DisplayManager dispManager = null;
    private HashMap<FocModule, FocModule> modules = null;
    private ArrayList<IFocDescDeclaration> focObjects = null;
    private boolean objectsAlreadyDeclared = false;
    private AccessControl defaultAccessControl = null;
    private UnitFactory unitFactory = null;
    private FocIcons focIcons = null;
    private PrintStream logFile = null;
    private PrintStream pushedLogFile = null;
    private boolean withDatabase = false;
    private boolean doNotCheckTables = false;
    private boolean withLogin = false;
    private int guiNavigatorType = DisplayManager.GUI_NAVIGATOR_MDI;
    private RightsByLevel rightsByLevel = null;
    private int loginStatus = LOGIN_WAITING;
    private FocUser user = null;
    private boolean withReporting = false;
    private UserSession userSession_Swing = null;
    private FMenu mainAppMenu = null;
    private FMenu mainFocMenu = null;
    private FMenu mainAdminMenu = null;
    private DebugOutputInterface debugOutputInterface = null;
    private FocNotificationManager notificationManager = null;
    private java.sql.Date systemDate = null;
    private boolean cashDeskModuleIncluded = false;
    private boolean currencyModuleIncluded = false;
    private boolean disableMenus = false;
    private boolean isExiting = false;
    private String name = null;
    private boolean isDemo = false;
    private int trialPeriod = 0;
    private long timeZoneShift = -10 * Globals.DAY_TIME;
    private ArrayList<IExitListener> exitListenerList = null;// il faut hash map
    private HashMap<String, IFocDescDeclaration> iFocDescDeclarationMap = null;
    private ArrayList<String> fabDefTables_ForAdaptFirst = null;
    private RootGarbageClass rgc = null;
    private FocTestSuite focTestSuite = null;
    private FocLogListener logListener = null;
    //FILE_ENCRYPTION
    private IFocEncryptor iFocEncryptor = null;
    //  private String  predefinedUserLogin           = null;
//  private String  predefinedEncriptedPassword   = null;
    private String printDataModelFileName = null;
    private boolean showBarmajaIconAndTitle = true;
    private ArgumentsHash argHash = null;
    private int processType = PROCESS_TYPE_DESKTOP_CLIENT;
    private FocMenuSettings menuSettings = null;
    private IDBReloader dbReloader = null;

    public Application(boolean withDatabase, boolean withLogin, boolean mdi) {
        this(withDatabase, withLogin, mdi, null);
    }

    public Application(boolean withDatabase, boolean withLogin, boolean mdi, String appName) {
        this(withDatabase, withLogin, mdi, appName, 0);
    }

    public Application(boolean withDatabase, boolean withLogin, boolean mdi, String appName, int trialPeriod) {
        this(withDatabase, withLogin, mdi ? DisplayManager.GUI_NAVIGATOR_MDI : DisplayManager.GUI_NAVIGATOR_MONOFRAME, appName, trialPeriod);
    }

    public Application(boolean withDatabase, boolean withLogin, int guiNavigatorType, String appName, int trialPeriod) {
        this(withDatabase, withLogin, guiNavigatorType, appName, trialPeriod, null);
    }

    public Application(boolean withDatabase, boolean withLogin, int guiNavigatorType, String appName, int trialPeriod, String args[]) {
        this.argHash = new ArgumentsHash(args);
        this.name = appName;
        this.withDatabase = withDatabase;
        this.withLogin = withDatabase && withLogin;
        this.guiNavigatorType = guiNavigatorType;
        this.trialPeriod = trialPeriod;
        fabDefTables_ForAdaptFirst = new ArrayList<String>();
        Class cls = null;
        try {
            String className = argHash.get("dataSourceClass");

            Class[] param = new Class[1];
            param[0] = Application.class;

            Object[] argsNew = new Object[1];
            argsNew[0] = Application.this;

            if (className != null) {
                cls = Class.forName(className);

                Constructor constr = cls.getConstructor(param);
                this.dataSource = (IFocDataSource) constr.newInstance(argsNew);
            } else {
                try {
                    cls = Class.forName("com.foc.focDataSourceDB.FocDataSource_DB");
                } catch (ClassNotFoundException e) {
                    cls = Class.forName("com.foc.focDataSourceHTTP.FocDataSource_HTTP");
                }
                Constructor constr = cls.getConstructor(param);
                this.dataSource = (IFocDataSource) constr.newInstance(argsNew);
            }
        } catch (Exception e) {
            Globals.logException(e);
        }

        String blobStorageFolderPrefix = ConfigInfo.getBlobStorageDirectory();
        if (Utils.isStringEmpty(blobStorageFolderPrefix)) {
            blobStorageFolderPrefix = argHash.get(ARG_BLOB_STORAGE_FOLDER_PREFIX);
            if (Utils.isStringEmpty(blobStorageFolderPrefix)) {
                blobStorageFolderPrefix = "foc";
            }
            setCloudStorageDirectory(blobStorageFolderPrefix + "." + ConfigInfo.getJdbcSchema());
        } else {
            //In the new mode we add a / not a .
            if (!blobStorageFolderPrefix.endsWith("/")) blobStorageFolderPrefix += "/";
            setCloudStorageDirectory(blobStorageFolderPrefix + ConfigInfo.getJdbcSchema());
        }

        menuSettings = new FocMenuSettings();

        //FILE_ENCRYPTION
        setFocEncryptionIfConfigured();
    }

    public static void initArgs(String[] args) {
        String timeZone = Application.argumentGetValue(args, "timeZone");
        if (timeZone != null) {
            TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        }
    }

    public static String argumentGetValue(String[] args, String key) {
        String value = null;
        int pos = argumentGetPositionForKey(args, key);
        if (pos >= 0) {
            String arg = args[pos];
            if (arg.length() > key.length() + 2) {
                value = arg.substring(key.length() + 2);
            }
        }
        return value;
    }

    public static boolean argumentExists(String[] args, String key) {
        return argumentGetPositionForKey(args, key) != -1;
    }

    private static int argumentGetPositionForKey(String[] args, String key) {
        int pos = 0;
        boolean found = false;
        if (args != null) {
            while (pos < args.length && !found) {
                String arg = args[pos];
                if (arg != null) {
                    found = arg.startsWith("-" + key) || arg.startsWith("/" + key);
                }
                if (!found) {
                    pos++;
                }
            }
        }
        return found ? pos : -1;
    }

    public boolean isWithGui() {
        return this.guiNavigatorType != DisplayManager.GUI_NAVIGATOR_NONE;
    }

    public FocMenuSettings getFocMenuSettings() {
        return menuSettings;
    }

    public long getTimeZoneShift() {
        if (timeZoneShift < -Globals.DAY_TIME) {
            Date stringComposed = Date.valueOf("1970-01-01");
            timeZoneShift = stringComposed.getTime();
        }
        return timeZoneShift;
    }

    private boolean isTrialVersionStillValid_WithPopupMessage() {
        java.sql.Date installationDate = getInstallationDate();
        boolean isValid = true;

        if (trialPeriod > 0 && installationDate != null) {
            java.sql.Date currentDate = getSystemDate();
            installationDate = FCalendar.shiftDate(Calendar.getInstance(), installationDate, trialPeriod);

            if (currentDate.compareTo(installationDate) > 0) {
                isValid = false;
                JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(),
                        "You have exceeded your " + trialPeriod + " day trial Period.",
                        "Trial version expired",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                isValid = true;
            }
        }

        return isValid;
    }

    public void initDisplay() {
        try {
            dispManager = new DisplayManager(guiNavigatorType);
            dispManager.init();
            dispManager.startMonoNavigator();
            dispManager.getMainFrame().setVisible(false);

            if (isWithRegistry()) {
                if (!isTrialVersionStillValid_WithPopupMessage()) {
                    System.exit(0);
                } else {
                    String directory = getDefaultAppDirectory();
                    String environment = getDefaultEnvironment();
                    String homePath = directory + "/" + environment;

                    File file = new File(homePath + "/properties/config.properties");
                    if (!file.exists()) {
                        ConfigInfoWizardPanel panel = new ConfigInfoWizardPanel(new GuiConfigInfo(), ConfigInfoWizardPanel.STATE_DIRECTORY);
                        Globals.getDisplayManager().popupDialog(panel, "", true);
                    }
                }
            }

            dispManager.getMainFrame().setVisible(true);

        } catch (Exception e) {
            Globals.logException(e);
        }
    }

    private void initApp() {
        modules = new HashMap<FocModule, FocModule>();
        logFile = null;
        try {
            if (!ConfigInfo.isLog4jActive()) {
                String filePrefix = argHash.get(Globals.ARG_LOG_FILE_PREFIX);
                if (filePrefix == null) filePrefix = "";
                logFile = (ConfigInfo.isLogFileActive() && !Globals.logFile_CheckLogDir()) ? new PrintStream(Globals.logFile_GetFileName(filePrefix, "log"), "UTF-8") : null;
            }
        } catch (Exception e) {
            Globals.logException(e);
        }
        exitListenerList = new ArrayList<IExitListener>();
        printDataModelFileName = argHash.get("printDataModel");
    }

    public void initInternal(String[] args, boolean withDisplay) {
        if (withDisplay) {
            initDisplay();
        }
        ConfigInfo.loadFile();
        PerfManager.start("Main");//This start is ended in the Exit function of the application
        initApp();
    }

    public void init(String[] args) {
        initInternal(args, getGuiNavigatorType() != DisplayManager.GUI_NAVIGATOR_NONE);
    }

    public void initWithoutDisplay(String[] args) {
        initInternal(args, false);
    }

    public boolean isWithRegistry() {
        return name != null;
    }

    private String getPreferencesRegistryStringValue(String key, String pathName) {
        String value = null;

        if (isWithRegistry()) {
            Preferences prefs = Preferences.systemRoot();
            if (preferencesRegistryNodeExists(prefs, pathName)) {
                prefs = prefs.node(pathName);
                value = prefs.get(key, "");
            }
        }
        return value;
    }

    public void setPreferencesRegistryStringValue(String key, String value) {
        if (isWithRegistry() && value != null) {
            Preferences prefs = Preferences.systemRoot();
            prefs = prefs.node(REGISTRY_PARENT_APPLICATION_NODE_NAME + "/" + name);
            prefs.put(key, value);
        }
    }

    public boolean preferencesRegistryNodeExists(Preferences prefs, String pathName) {
        boolean exists = false;
        try {
            if (isWithRegistry()) {
                exists = prefs.nodeExists(pathName);
            }
        } catch (BackingStoreException e) {
            Globals.logException(e);
        }
        return exists;
    }

    public java.sql.Date getInstallationDate() {
        String date = getPreferencesRegistryStringValue(REGISTRY_APPLICATION_INSTALL_DATE, Application.REGISTRY_PARENT_APPLICATION_NODE_NAME + "/" + name);
        java.sql.Date installationDate = null;
        if (date != null && !date.equals("")) {
            installationDate = java.sql.Date.valueOf(date);
        }
        return installationDate;
    }

    public String getDefaultEnvironment() {
        return getPreferencesRegistryStringValue(REGISTRY_APPLICATION_ENVIRONMENT, Application.REGISTRY_PARENT_APPLICATION_NODE_NAME + "/" + name);
    }

    public void setDefaultEnvironment(String defaultEnvironment) {
        setPreferencesRegistryStringValue(REGISTRY_APPLICATION_ENVIRONMENT, defaultEnvironment);
    }

    public String getDefaultAppDirectory() {
        return getPreferencesRegistryStringValue(REGISTRY_APPLICATION_DIRECTORY, Application.REGISTRY_PARENT_APPLICATION_NODE_NAME + "/" + name);
    }

    public void setDefaultAppDirectory(String defaultAppDirectory) {
        setPreferencesRegistryStringValue(REGISTRY_APPLICATION_DIRECTORY, defaultAppDirectory);
    }

    public void dispose() {
        if (perfManager != null) {
            perfManager.dispose();
            perfManager = null;
        }

        if (focIcons != null) {
            focIcons = null;
        }

        if (logFile != null) {
            logFile = null;
        }

        if (modules != null) {
            modules.clear();
            modules = null;
        }

        if (exitListenerList != null) {
            exitListenerList.clear();
            exitListenerList = null;
        }

        if (defaultAccessControl != null) {
            defaultAccessControl = null;
        }

        if (dispManager != null) {
            dispManager = null;
        }

        if (fabDefTables_ForAdaptFirst != null) {
            fabDefTables_ForAdaptFirst.clear();
            fabDefTables_ForAdaptFirst = null;
        }
    }

    private ArrayList<IFocDescDeclaration> getFocObjectsList() {
        if (focObjects == null) {
            focObjects = new ArrayList<IFocDescDeclaration>();
        }
        return focObjects;
    }

    public Iterator<IFocDescDeclaration> getFocDescDeclarationIterator() {
        return getFocObjectsList().iterator();
    }
  
  /*public FocDesc getFocDescByName(String name){
  	FocDesc res = null;
  	if(name != null){
  		res = getFocDescFromMap(name);
  		if(res == null){
		  	Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
		  	while(iter != null && iter.hasNext() && res == null){
		  		IFocDescDeclaration iFocDescDeclaration = iter.next();
		  		if(iFocDescDeclaration != null){
		  			FocDesc focDesc = iFocDescDeclaration.getFocDesctiption();
		  			if(focDesc != null){
		  				if(name.equals(focDesc.getStorageName())){
		  					putFocDescInMap(focDesc);
		  					res = focDesc;
		  				}
		  			}
		  		}
		  	}
  		}
  	}
  	return res;
  }*/

    private HashMap<String, IFocDescDeclaration> getIFocDescDeclarationMap() {
        if (this.iFocDescDeclarationMap == null) {
            this.iFocDescDeclarationMap = new HashMap<String, IFocDescDeclaration>();
        }
        return this.iFocDescDeclarationMap;
    }

    public void putIFocDescDeclaration(String storageName, IFocDescDeclaration iFocDescDeclaration) {
        if (iFocDescDeclaration != null && storageName != null) {
            getIFocDescDeclarationMap().put(storageName, iFocDescDeclaration);
        }
    }

    public void printDebug() {
        Globals.logString("IFocDescMap : ");
  	/*Iterator<IFocDescDeclaration> mapIter = getIFocDescDeclarationMap().values().iterator();
  	while(mapIter != null && mapIter.hasNext()){
  		IFocDescDeclaration decl = mapIter.next();
  		Globals.logString(decl.getFocDesctiption().getStorageName());
  	}*/

        Globals.logString("FocDesc Array : ");
        Iterator<IFocDescDeclaration> arrayIter = getFocDescDeclarationIterator();
        while (arrayIter != null && arrayIter.hasNext()) {
            IFocDescDeclaration decl = arrayIter.next();
            //if(!iFocDescDeclarationMap.containsKey(decl.getFocDesctiption().getStorageName())){
            Globals.logString(decl.getFocDescription().getStorageName());
            //}

        }
    }

    public IFocDescDeclaration getIFocDescDeclarationByName(String storageName) {
        IFocDescDeclaration iFocDescDeclaration = null;
        if (storageName != null) {
            iFocDescDeclaration = getIFocDescDeclarationMap().get(storageName);
        }
        return iFocDescDeclaration;
    }

    public FocDesc getFocDescByName(String name) {
        FocDesc res = null;
        if (name != null) {
            IFocDescDeclaration iFocDescDeclarationFromMap = getIFocDescDeclarationByName(name);
            if (iFocDescDeclarationFromMap != null) {
                res = iFocDescDeclarationFromMap.getFocDescription();
            }
            if (res == null) {
                Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
                while (iter != null && iter.hasNext() && res == null) {
                    IFocDescDeclaration iFocDescDeclaration = iter.next();
                    if (iFocDescDeclaration != null) {
                        FocDesc focDesc = iFocDescDeclaration.getFocDescription();
                        if (focDesc != null) {
                            if (name.equals(focDesc.getName())) {
                                res = focDesc;
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    public boolean isWithReporting() {
        return withReporting;
    }

    public void setWithReporting(boolean withReporting) {
        this.withReporting = withReporting;
    }

    public RightsByLevel getRightsByLevel() {
        return rightsByLevel;
    }

    public void setRightsByLevel(RightsByLevel rightsByLevel) {
        this.rightsByLevel = rightsByLevel;
    }

    public boolean isWithRightsByLevel() {
        return rightsByLevel != null && rightsByLevel.getNbOfLevels() > 0;
    }

    public void initDBManager() {
        if (withDatabase) {
            dbManager = new DBManager();
            Date date = getSystemDate();
            if (date != null) {
                Globals.logString(date.toString());
            }
        }
    }

    public void prepareDBForLogin(String dbFile) {
        Globals.logString("Before Init DB Manager");
        initDBManager();
        Globals.logString("After Init DB Manager");
        //focObjects = new ArrayList<IFocDescDeclaration>();

        AdminModule.getInstance(withLogin).declare();
        BusinessModule.getInstance().declare();
        PhotoAlbumManagmentModule.getInstance().declare();

        FocVersion.addVersion("FOC", "foc1.6", 1634);

        //if(withDatabase && !isDoNotCheckTables()){
        //  adminModule.checkTables();
        //}
        declareFocObjects();
    /*for(int i=0; i<focObjects.size(); i++){
      Class cls = focObjects.get(i);
      FocDesc focDesc = FocDesc.getFocDescriptionForDescClass(cls);
      focDesc.scanFieldsAndAddReferenceLocations();
    }*/

        ArrayList<IFocDescDeclaration> focObjectsList = getFocObjectsList();
        if (focObjectsList != null) {
            Collections.sort(focObjectsList, new Comparator<IFocDescDeclaration>() {
                public int compare(IFocDescDeclaration o1, IFocDescDeclaration o2) {
                    return o1 != null && o2 != null ? o1.getPriority() - o2.getPriority() : 0;
                }
            });
        }

        construction_FocDesc_Init();

        try {
            Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
            while (iter != null && iter.hasNext()) {
                IFocDescDeclaration focDescDeclaration = iter.next();
                FocDesc focDesc = focDescDeclaration.getFocDescription();
                if (focDesc != null) {
                    focDesc.afterConstructionInternal_1();
                }
            }
        } catch (Exception e) {
            Globals.logException(e);
        }

        afterConstruction_Modules();

        Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
        while (iter != null && iter.hasNext()) {
            IFocDescDeclaration focDescDeclaration = iter.next();
            if (focDescDeclaration != null) {
                FocDesc focDesc = focDescDeclaration.getFocDescription();
                if (focDesc != null) {
                    focDesc.scanFieldsAndAddReferenceLocations();
                }
            }
        }

//    if(withDatabase && !isDoNotCheckTables()){
//      adminModule.checkTables();
//    }

        try {
            iter = getFocDescDeclarationIterator();
            while (iter != null && iter.hasNext()) {
                IFocDescDeclaration focDescDeclaration = iter.next();
                FocDesc focDesc = focDescDeclaration.getFocDescription();
                if (focDesc != null) {
                    focDesc.afterConstructionInternal_2();
                }
            }
        } catch (Exception e) {
            Globals.logException(e);
            AdminModule.getInstance().checkTables();
        }

        if (withDatabase && !isDoNotCheckTables()) {
            AdminModule.getInstance().checkTables();
        }

    /*
    if(adminModule.isNewUserTables() && dbFile != null){
      try {
        dbAsciiConverter = new DB2ASCII(dbFile, DB2ASCII.COPY_DIRECTION_ASCII_TO_DB);
      } catch (Exception e) {
        Globals.logException(e);
      }
    }
    */

        iter = getFocDescDeclarationIterator();
        while (iter != null && iter.hasNext()) {
            IFocDescDeclaration focDescDeclaration = iter.next();
            FocDesc focDesc = focDescDeclaration.getFocDescription();
            if (focDesc != null) {
                focDesc.beforeLogin();
            }
        }
    }

    public boolean isLoginAdministrator() {
        return getLoginStatus() == LOGIN_ADMIN;
    }

    public boolean isLoginValid() {
        return getLoginStatus() == LOGIN_VALID;
    }

    public Iterator<FocModule> modules_Iterator() {
        return modules.values().iterator();
    }

    public boolean login() {
        return login(null);
    }

    public boolean login(String dbFile) {
        return login(dbFile, false);
    }

    public boolean login(String dbFile, boolean forWebServer) {
        boolean logOk = true;
        defaultAccessControl = new AccessControl(true, true);
        Globals.logDetail("S - Before Prepare DB");
        prepareDBForLogin(dbFile);
        Globals.logDetail("S - After Prepare DB");
    /*
    if(dbAsciiConverter != null){
    	try{
    		dbAsciiConverter.backupRestore();
    	}catch(Exception e){
    		Globals.logException(e);
    	}
    }
    */

        if (withLogin) {
            logOk = false;

            if (!forWebServer) {
                if (getGuiNavigatorType() != DisplayManager.GUI_NAVIGATOR_NONE) {
                    showLoginScreenAndWaitForUserEntry(3);
                } else {
                    if (ConfigInfo.getUsername() != null && ConfigInfo.getEncryptedPassword() != null && !ConfigInfo.getUsername().trim().isEmpty()) {
                        FocUser.userLoginCheck(ConfigInfo.getUsername(), ConfigInfo.getEncryptedPassword());
                    }
                }
            } else {
                //When it is a server it logs in as 01BARMAJA user and we do not need the password.
                user = FocUser.findUser("01BARMAJA");
                if (user == null) Globals.logString("Could not finduser 01BARMAJA");
                loginStatus = LOGIN_VALID;
            }
            if (loginStatus == LOGIN_WRONG) {
                exit();
            }
            logOk = loginStatus == LOGIN_VALID && loginStatus != LOGIN_ADMIN;
        } else {
            loginStatus = LOGIN_VALID;
            logOk = true;
        }

        Iterator descIter = getFocDescDeclarationIterator();
        while (descIter != null && descIter.hasNext()) {
            IFocDescDeclaration focDescDeclaration = (IFocDescDeclaration) descIter.next();
            FocDesc focDesc = focDescDeclaration.getFocDescription();
            if (focDesc != null) {
                focDesc.afterLogin();
            }
        }

        Iterator<FocModule> iter = modules.values().iterator();
        while (iter != null && iter.hasNext()) {
            FocModule module = iter.next();
            module.afterApplicationLogin();
        }

        printDataModelIfRequired();

        if (loginStatus == LOGIN_VALID) {
            if (BusinessModule.getInstance().isMultiCompany()) {
                FocList companyList = CompanyDesc.getList(FocList.LOAD_IF_NEEDED);
                if (companyList == null || companyList.size() == 0) {
//    			Globals.getDisplayManager().popupMessage("No Company defined !\nYour Software administrator should define at least one Company.");
//    			exit();
                }
            }
        }

        return logOk;
    }

    public void showLoginScreenAndWaitForUserEntry(int maxNbOfAttempt) {
        dispManager.displayLogin();

        int nbOfAttempt = 0;
        setLoginStatus(LOGIN_WRONG);
        while (nbOfAttempt < maxNbOfAttempt && loginStatus == LOGIN_WRONG) {
            if (nbOfAttempt > 0) {
                JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
                        "Failed to login.\nAttempt" + nbOfAttempt + " of " + maxNbOfAttempt,
                        "01Barmaja",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null);
                FLoginPanel.getInstance().unlockUserNamePassword();
            }
            setLoginStatus(LOGIN_WAITING);
            while (loginStatus == LOGIN_WAITING) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    Globals.logException(e);
                }
            }
            nbOfAttempt++;
        }

        if (loginStatus == LOGIN_VALID && Globals.getApp().getUser() != null) {
            FocUser focUser = Globals.getApp().getUser();
            //boolean popup =
            //if(focUser.getCurrentCompany() != null && focUser.getCurrentSite() != null
            FocUserGuiDetailsPanel guiCompanySiteAndTeamSelection = new FocUserGuiDetailsPanel(focUser, FocUser.VIEW_CHANGE_MULTI_COMPANY_FILTER);
            Globals.getDisplayManager().popupDialog(guiCompanySiteAndTeamSelection, "Session : Company / Site / Title", true, 550, 325);
        }
    }

    public SaaSConfig getSaaSConfig() {
        return appConfiguration;
    }

    public SaaSConfig loadAppConfiguration() {
        appConfiguration = SaaSConfig.loadInstance();
        return appConfiguration;
    }

    public String getVersionText() {
        String text = "01Barmaja software\n\n";
        String demoVersion = "";
        if (isDemo()) {
            demoVersion = "(Demo Version)";
        }
        text = text + "  - Versions: " + demoVersion + "\n";

        FocList verList = FocVersion.getVersionList();
        Iterator iter = verList.focObjectIterator();
        while (iter != null && iter.hasNext()) {
            FocVersion ver = (FocVersion) iter.next();
            text = text + "    + " + ver.getJar() + " : " + ver.getName() + " (" + ver.getId() + ")\n";
        }
        return text;
    }

    @SuppressWarnings("serial")
    private void menuPreparation() {
        {
            AbstractAction exitAction = new AbstractAction() {
                /**
                 * Comment for <code>serialVersionUID</code>
                 */
                private static final long serialVersionUID = 3977012929554167095L;

                public void actionPerformed(ActionEvent e) {
                    Application app = Globals.getApp();
                    app.exit();
                }
            };

            AbstractAction aboutAction = new AbstractAction() {
                /**
                 * Comment for <code>serialVersionUID</code>
                 */
                private static final long serialVersionUID = 3546357331844610361L;

                public void actionPerformed(ActionEvent e) {
                    String text = getVersionText();

                    //text = text + "\n"+getDBManager().getConnectionInfo();

                    JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
                            text,
                            "01Barmaja",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null);
                }
            };

            AbstractAction changePasswordAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    getDisplayManager().popupDialog(user.newDetailsPanel(FocUser.CHANGE_PASSWORD_VIEW_ID), "Change password", true);
                }
            };

            AbstractAction changeLanguageAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    //getDisplayManager().newInternalFrame(user.newDetailsPanel(FocUser.CHANGE_LANGUAGE_VIEW_ID));/*, MultiLanguage.getString(FocLangKeys.ADMIN_CHANGE_LANGUAGE), true);*/
                    getDisplayManager().popupDialog(user.newDetailsPanel(FocUser.CHANGE_LANGUAGE_VIEW_ID), MultiLanguage.getString(FocLangKeys.ADMIN_CHANGE_LANGUAGE), true);
                }
            };

      /*
      AbstractAction changeCompanyAction = new AbstractAction(){
        public void actionPerformed(ActionEvent e){
        	if(Globals.getDisplayManager().allScreensClosed()){
        		getDisplayManager().popupDialog(user.newDetailsPanel(FocUser.VIEW_CHANGE_MULTI_COMPANY_FILTER), "Company Filter", true);
        	}else{
        		Globals.getDisplayManager().popupMessage("Please close all internal windows before changing company configuration\nBecause this will exit your session.");
        	}
        }
      };
      */

            AbstractAction viewUserGroupInfoAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    //getDisplayManager().newInternalFrame(user.newDetailsPanel(FocUser.CHANGE_LANGUAGE_VIEW_ID));/*, MultiLanguage.getString(FocLangKeys.ADMIN_CHANGE_LANGUAGE), true);*/
                    getDisplayManager().popupDialog(user.newDetailsPanel(FocUser.USER_GROUP_INFO_VIEW_ID), MultiLanguage.getString(FocLangKeys.ADMIN_USER), true);
                }
            };

            /*AbstractAction navigationHelpAction = new AbstractAction(){
             *//**
         * Comment for <code>serialVersionUID</code>
         *//*
        private static final long serialVersionUID = 3546357331844610361L;

        public void actionPerformed(ActionEvent e){
          FocHelp.popupNavigationHelp();
        }
      };*/

            FMenuList mainMenu = new FMenuList(FocLangKeys.MENU_ADMIN_MENU);

            if (withLogin || MultiLanguage.isMultiLanguage()) {
                FMenuList toolsMenu = new FMenuList(FocLangKeys.MENU_TOOLS);
                if (withLogin) {
                    FMenuItem userGroupInfoItem = new FMenuItem(FocLangKeys.MENU_USER_GROUP_INFO, viewUserGroupInfoAction);
                    toolsMenu.addMenu(userGroupInfoItem);

                    FMenuItem changePasswordItem = new FMenuItem(FocLangKeys.MENU_CHANGE_PASSWORD, changePasswordAction);
                    toolsMenu.addMenu(changePasswordItem);
                }

                FMenuItem changeLanguageItem = new FMenuItem(FocLangKeys.MENU_USER_PREFERENCES, changeLanguageAction);
                toolsMenu.addMenu(changeLanguageItem);

        /*
        if(BusinessModule.getInstance().isMultiCompany() && !Globals.getApp().getUser().isAdmin()){
	        FMenuItem changeCompanyItem = new FMenuItem("Change Company Settings", 'C', changeCompanyAction);
	        toolsMenu.addMenu(changeCompanyItem);
        }
        */

                if (!Globals.getDBManager().sync_isNone()) {
                    FMenuList syncMenuList = new FMenuList("Synchronize", 'y');
                    toolsMenu.addMenu(syncMenuList);

                    if (Globals.getDBManager().sync_isRemote()) {
                        FMenuItem uploadItem = new FMenuItem("Upload...", 'U', new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                                Globals.getDBManager().getSyncManager().fillTableListFromDatabase();
                                Globals.getDBManager().getSyncManager().writeToFile();
                                //getDisplayManager().popupDialog(user.newDetailsPanel(FocUser.USER_GROUP_INFO_VIEW_ID), MultiLanguage.getString(FocLangKeys.ADMIN_USER), true);
                            }
                        });
                        toolsMenu.addMenu(uploadItem);
                    }
                }

                FMenuList memoryMenuList = new FMenuList("Memory", 'M');
                toolsMenu.addMenu(memoryMenuList);

                FMenuItem garbageCollectorItem = new FMenuItem("Free unused memory", 'R', new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        String beforeMessage = Globals.logMemory("Before freeing ");
                        if (rgc != null) {
                            rgc.dispose();
                            rgc = null;
                        }
                        System.gc();
                        Globals.logMemory("");

                        System.gc();
                        Globals.logMemory("");

                        System.gc();
                        String afterMessage = Globals.logMemory("After  freeing ");

                        Globals.getDisplayManager().popupMessage(beforeMessage + "\n" + afterMessage);
                    }
                });
                memoryMenuList.addMenu(garbageCollectorItem);

                FMenuItem allocateGarbageItem = new FMenuItem("memory", 'A', new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        String message = Globals.logMemory("");
                        Globals.getDisplayManager().popupMessage(message);
                    }
                });
                memoryMenuList.addMenu(allocateGarbageItem);

                FocGroup group = Globals.getApp().getGroup();
        /*
        if(group != null && (group.allowDatabaseBackup() || group.allowDatabaseRestore())){
          FMenuList databaseList = new FMenuList("Database", 'D');
          toolsMenu.addMenu(databaseList);

          if(group.allowDatabaseBackup()){
	          FMenuItem backupDatabaseItem = new FMenuItem("Backup Custom Configuration only", 'o', new AbstractAction(){
	  					public void actionPerformed(ActionEvent e) {
	  						try {
		  						DBBackupFileChooser fileChooser = new DBBackupFileChooser();
		  						String outputFile = fileChooser.choose();

		  						if(outputFile != null){
	  	  						DB2ASCII db2Ascii = new DB2ASCII(outputFile, DB2ASCII.COPY_DIRECTION_DB_TO_ASCII);
	  	  						db2Ascii.setFabOnly(true);
										db2Ascii.backupRestore();
	  	  						db2Ascii.dispose();
		  						}
								} catch (Exception e1) {
									Globals.logException(e1);
								}
	  					}
	          });
	          databaseList.addMenu(backupDatabaseItem);
          }

          if(group.allowDatabaseBackup()){
	          FMenuItem backupDatabaseItem = new FMenuItem("Backup", 'B', new AbstractAction(){
	  					public void actionPerformed(ActionEvent e) {
	  						try {
		  						DBBackupFileChooser fileChooser = new DBBackupFileChooser();
		  						String outputFile = fileChooser.choose();

		  						if(outputFile != null){
	  	  						DB2ASCII db2Ascii = new DB2ASCII(outputFile, DB2ASCII.COPY_DIRECTION_DB_TO_ASCII);
										db2Ascii.backupRestore();
	  	  						db2Ascii.dispose();
		  						}
								} catch (Exception e1) {
									Globals.logException(e1);
								}
	  					}
	          });
	          databaseList.addMenu(backupDatabaseItem);
          }

          if(group.allowDatabaseRestore()){
	          FMenuItem restoreDatabaseItem = new FMenuItem("Restore", 'R', new AbstractAction(){
	  					public void actionPerformed(ActionEvent e) {
	  						try {
		  						DBBackupFileChooser fileChooser = new DBBackupFileChooser();
		  						String outputFile = fileChooser.choose();

		  						if(outputFile != null){
	  	  						DB2ASCII db2Ascii = new DB2ASCII(outputFile, DB2ASCII.COPY_DIRECTION_ASCII_TO_DB);
										db2Ascii.backupRestore();
	  	  						db2Ascii.dispose();
		  						}
								} catch (Exception e1) {
									Globals.logException(e1);
								}
	  					}
	          });
	          databaseList.addMenu(restoreDatabaseItem);
          }

          FMenuItem dataDictionaryItem = new FMenuItem("Data Dictionary", 'D', new AbstractAction(){
						public void actionPerformed(ActionEvent e) {
							FocConstructor focConstr = new FocConstructor(DataModelEntryDesc.getInstance(), null);
							DataModelEntry entry = (DataModelEntry) focConstr.newItem();
							DataModelEntryGuiDetailsPanel detailsPanel = new DataModelEntryGuiDetailsPanel(entry, 0);
							Globals.getDisplayManager().popupDialog(detailsPanel, "Dictionay Lookup - Select a table and a depth", true);
						}
          });
          toolsMenu.addMenu(dataDictionaryItem);
        }*/

                mainMenu.addMenu(toolsMenu);
            }

            FMenuList helpMenu = new FMenuList(FocLangKeys.MENU_HELP);
            if (getFocMenuSettings().isIncludeAbout()) {
                FMenuItem versionItem = new FMenuItem(FocLangKeys.MENU_ABOUT, aboutAction);
                helpMenu.addMenu(versionItem);
            }

            FocHelp focHelp = FocHelp.getInstance();
            if (getFocMenuSettings().isIncludeNavigation()) {
                focHelp.putHelpFileUrl("Navigation", "help/foc/navigationTips.html");
            }
            focHelp.fillHelpMenu(helpMenu);

      /*FMenuItem navigationHelpItem = new FMenuItem("Navigation", 'v', navigationHelpAction);
      helpMenu.addMenu(navigationHelpItem);*/

            mainMenu.addMenu(helpMenu);

            if (getFocMenuSettings().isIncludeExit()) {
                FMenuList exitMenu = new FMenuList(FocLangKeys.MENU_EXIT);
                FMenuItem exitItem = new FMenuItem(FocLangKeys.MENU_EXIT, exitAction);
                exitMenu.addMenu(exitItem);
                mainMenu.addMenu(exitMenu);
            }

            setMainFocMenu(mainMenu);
        }

        if (withLogin) {
            if (getMainAdminMenu() == null) {
                setMainAdminMenu(AdminModule.getInstance().getAdminMenu());
            }
        }
    }

    public void menuConstruction() {
        getDisplayManager().popupMenu(loginStatus);
    }

    public void entry() {
        if (getGuiNavigatorType() != DisplayManager.GUI_NAVIGATOR_NONE) {
            menuPreparation();
            menuConstruction();
        }

        Iterator<FocModule> iter = modules.values().iterator();
        while (iter != null && iter.hasNext()) {
            FocModule module = (FocModule) iter.next();
            module.afterApplicationEntry();
        }

    }

    public void reconstructMenu() {
        if (getDisplayManager() != null) getDisplayManager().reconstructMenu(loginStatus);
    }

    public FocTestSuite getFocTestSuite() {
        return focTestSuite;
    }

    public void setFocTestSuite(FocTestSuite focTestSuite) {
        this.focTestSuite = focTestSuite;
    }

    public boolean isUnitTesting() {
        return focTestSuite != null;
    }

    public void exit(boolean forceEvenIfOpenedWindows) {
        if (!isExiting) {
            isExiting = true;
            boolean doExit = true;
            if (!forceEvenIfOpenedWindows) {
                doExit = Globals.getApp().isWithLogin() && Globals.getApp().getLoginStatus() == Application.LOGIN_WAITING;
                if (!doExit) {
                    if (Globals.getDisplayManager() != null) {
                        doExit = Globals.getDisplayManager().allScreensClosed();
                        if (!doExit) {
                            Globals.getDisplayManager().popupMessage("Cannot exit because some windows are still opened!");
                        }
                    }
                }
            }
            if (doExit) {
                for (int i = 0; i < exitListenerList.size(); i++) {
                    IExitListener listener = exitListenerList.get(i);
                    if (listener != null) {
                        listener.replyToExit();
                    }
                }
                DBManager dbManager = getDBManager();
                if (dbManager != null) {
                    dbManager.exit();
                }
                Globals.logString("-- Application exit --");
                PerfManager.end("MAIN");
                PerfManager.print();

                if (!isUnitTesting()) {
                    System.exit(0);
                }
            }
            isExiting = false;
        }
    }

    public void exit() {
        exit(false);
    }

    public UnitFactory getUnitFactory() {
        if (unitFactory == null) {
            unitFactory = new UnitFactory();
        }
        return unitFactory;
    }

    public void declareModule(FocModule module) {
        modules.put(module, module);
    }

    public void declareFocObjects() {
        if (!objectsAlreadyDeclared) {
            Iterator<FocModule> iter = modules.values().iterator();
            while (iter != null && iter.hasNext()) {
                FocModule module = (FocModule) iter.next();
                module.declareFocObjects();
            }
        }
        objectsAlreadyDeclared = true;
    }

    public void construction_FocDesc_Init() {
        Iterator<IFocDescDeclaration> iter2 = getFocDescDeclarationIterator();
        while (iter2 != null && iter2.hasNext()) {
            IFocDescDeclaration focDescDeclaration = iter2.next();
            if (focDescDeclaration != null) {
                focDescDeclaration.getFocDescription();
            }
        }

        //This scan is only useful when we have FocObjects that are declared only by the table name...
        ArrayList<IFocDescDeclaration> newDeclarations = new ArrayList<IFocDescDeclaration>();
        Iterator<IFocDescDeclaration> iter3 = getFocDescDeclarationIterator();
        while (iter3 != null && iter3.hasNext()) {
            IFocDescDeclaration focDescDeclaration = iter3.next();
            if (focDescDeclaration != null) {
                FocDesc focDesc = focDescDeclaration.getFocDescription();
                if (focDesc != null) {
                    //Scanning the FObectField
                    for (int i = 0; i < focDesc.getFieldsSize(); i++) {
                        FField fld = focDesc.getFieldAt(i);
                        if (fld instanceof FObjectField) {
                            ((FObjectField) fld).getFocDescFromStorageNameIfNeeded(focDesc);
                        }
                    }
                    //Adding the Workflow and LOG Table
                    if (focDesc instanceof XMLFocDesc) {
                        XMLFocDesc xmlFocDesc = (XMLFocDesc) focDesc;
                        if (xmlFocDesc.iWorkflow_getWorkflowDesc() != null && !xmlFocDesc.hasJoinNode()) {
                            FocDescDeclaration_XMLBased_WFLog logDeclaration = new FocDescDeclaration_XMLBased_WFLog(xmlFocDesc);
                            logDeclaration.getFocDescription();
                            newDeclarations.add(logDeclaration);
                        }
                    }
                    //Adding the Workflow and LOG Table
                    if (focDesc instanceof PojoFocDesc) {
                        PojoFocDesc pojoFocDesc = (PojoFocDesc) focDesc;
                        if (pojoFocDesc.iWorkflow_getWorkflowDesc() != null && !pojoFocDesc.hasJoinNode()) {
                            FocDescDeclaration_PojoBased_WFLog logDeclaration = new FocDescDeclaration_PojoBased_WFLog(pojoFocDesc);
                            logDeclaration.getFocDescription();
                            newDeclarations.add(logDeclaration);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < newDeclarations.size(); i++) {
            IFocDescDeclaration logDeclaration = newDeclarations.get(i);
            declaredObjectList_DeclareObject(logDeclaration);
        }
    }

    public void afterConstruction_Modules() {
        Iterator<FocModule> iter = modules.values().iterator();
        while (iter != null && iter.hasNext()) {
            FocModule module = (FocModule) iter.next();
            module.afterConstruction();
        }
    }

    public DBManager getDBManager() {
        return dbManager;
    }

    public DisplayManager getDisplayManager() {
        return dispManager;
    }

    private void declaredObjectList_DeclareObject(FocModule module, Class classObject, boolean returnNullFocDesc) {
        ClassFocDescDeclaration classDeclaration = null;
        if (returnNullFocDesc) {
            classDeclaration = new ClassFocDescDeclarationReturnNull(module, classObject);
        } else {
            classDeclaration = new ClassFocDescDeclaration(module, classObject);
        }

        declaredObjectList_DeclareObject(classDeclaration);
    }

    public void declaredObjectList_DeclareObject(IFocDescDeclaration declaration) {
        ArrayList<IFocDescDeclaration> focObjectsArrayList = getFocObjectsList();
        if (declaration != null) {
            focObjectsArrayList.add(declaration);
        }
    }

    public void declaredObjectList_DeclareObject(FocModule module, Class classObject) {
        declaredObjectList_DeclareObject(module, classObject, false);
    }

    public void declaredObjectList_DeclareObjectForExistingInstance(FocModule module, Class classObject) {
        declaredObjectList_DeclareObject(module, classObject, true);
    }

    public void declaredObjectList_DeclareDescription(FocModule module, Class classObject) {
        declaredObjectList_DeclareObject(module, classObject);
    }

    public void declaredObjectList_DeclareDescriptionForExistingInstance(FocModule module, Class classObject) {
        declaredObjectList_DeclareObjectForExistingInstance(module, classObject);
    }

  /*private int declaredObjectList_SizeX() {
    return focObjects.size();
  }

  private Class declaredObjectList_getX(int i) {
    return (Class) focObjects.get(i);
  }*/

  /*public void adaptDataModel() {
    int i;

    dbManager.initAdaptDataModel();

    // Just go and open all descriptions so that links with maters get created
    for (i = 0; i < declaredObjectList_Size(); i++) {
      Class classObj = (Class) declaredObjectList_get(i);
      if (classObj != null) {
        FocDesc.getFocDescriptionForDescClass(classObj);
      }
    }

    for (i = 0; i < declaredObjectList_Size(); i++) {
      Class classObj = declaredObjectList_get(i);
      if (classObj != null) {
        FocDesc focDesc = FocDesc.getFocDescriptionForDescClass(classObj);

        dbManager.adaptTable(focDesc);
        dbManager.adaptTableIndexes(focDesc, false);

        // Scan the descriptions fields and store the links tables
        FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
        while (enumer.hasNext()) {
          FField ffield = enumer.nextField();
          if (ffield != null && ffield.isDBResident()) {
            FocLink link = ffield.getLink();
            FocDesc linkTablDesc = link != null ? link.getLinkTableDesc() : null;
            if (linkTablDesc != null) {
              dbManager.adaptTable(linkTablDesc);
              dbManager.adaptTableIndexes(linkTablDesc, false);
            }
          }
        }

      }
    }
    dbManager.endAdaptDataModel();
    FocVersion.saveVersions();
  }*/

    public void declaredObjectList_DeclareDescription(IFocDescDeclaration focDescDeclaration) {
        ArrayList<IFocDescDeclaration> focObjectsArrayList = getFocObjectsList();
        if (focDescDeclaration != null) {
            focObjectsArrayList.add(focDescDeclaration);
        }
    }

    public void adaptDataModel(boolean forceAlterTables, boolean schemaEmpty) {
        Globals.logString("Init Web Server " + (debug++));
        getDataSource().command_AdaptDataModel(forceAlterTables, schemaEmpty);
    }

    public String getType(int type) {
        switch (type) {
            case Types.NUMERIC:
                return "NUMERIC";
            case Types.INTEGER:
                return "INT";
            case Types.DOUBLE:
                return "DOUBLE";
            case Types.VARCHAR:
                return "VARCHAR";
            case Types.DATE:
                return "DATE";
            case Types.JAVA_OBJECT:
                return "JAVA-OBJECT";
            case Types.BLOB:
                return "BLOB";
        }
        return "UNLISTED";
    }

    public void printDataModelIfRequired() {
        if (Utils.isStringEmpty(printDataModelFileName)) {
            printDataModelFileName = ConfigInfo.getDataModelFileName();
        }
        if (printDataModelFileName != null && !printDataModelFileName.trim().equals("")) {
            String logDir = ConfigInfo.getLogDir();
            if (logDir == null) logDir = "C:";
            String fullFileName = logDir + "/" + printDataModelFileName;
            printDataModel_CSV(fullFileName + ".csv");
            printDataModel_XML(fullFileName + ".uxf");
        }
    }

    private void printDataModel_XML_AddOneTable(Document doc, Element umlDiagram, FocDesc focDesc, int x, int y) {
        String focDescName = focDesc.getStorageName();

        Element element = doc.createElement("element");
        Element type = doc.createElement("type");
        type.setTextContent("com.umlet.element.base.Class");
        Element coordinates = doc.createElement("coordinates");
        Element panelAttributes = doc.createElement("panel_attributes");
        Element additionalAttributes = doc.createElement("additional_attributes");
        Element xElmt = doc.createElement("x");
        xElmt.setTextContent(String.valueOf(x));
        Element yElmt = doc.createElement("y");
        yElmt.setTextContent(String.valueOf(y));
        Element wElmt = doc.createElement("w");
        wElmt.setTextContent(String.valueOf(190));
        Element hElmt = doc.createElement("h");
        hElmt.setTextContent(String.valueOf(80));
        Element helpText = doc.createElement("help_text");
        helpText.setTextContent("fontsize=11");
        umlDiagram.appendChild(helpText);

        umlDiagram.appendChild(element);
        element.appendChild(type);
        element.appendChild(coordinates);
        element.appendChild(panelAttributes);
        element.appendChild(additionalAttributes);
        coordinates.appendChild(xElmt);
        coordinates.appendChild(yElmt);
        coordinates.appendChild(wElmt);
        coordinates.appendChild(hElmt);


        StringBuffer content = new StringBuffer(focDescName + "\n--\n");

        FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
        while (enumer != null && enumer.hasNext()) {
            FField field = enumer.nextField();
            if (field != null) {
                FFieldPath path = enumer.getFieldPath();
                int firstFieldId = path.get(0);
                FField firstField = focDesc.getFieldByID(firstFieldId);
                FocDesc foreignDesc = firstField != null ? firstField.getFocDesc() : null;
                String foreignStorage = foreignDesc != null ? "->" + foreignDesc.getStorageName() : "";

                content.append(enumer.getFieldCompleteName(focDesc) + " : '" + field.getTitle() + "' " + getType(field.getSqlType()) + " " + foreignStorage + "\n");
            }
        }

        content.append("bg=orange");
        panelAttributes.setTextContent(content.toString());
    }

    private void printDataModel_XML(String fullFileName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element umlDiagram = doc.createElement("uml_diagram");
            doc.appendChild(umlDiagram);
            Element helpText = doc.createElement("help_text");
            helpText.setTextContent("fontsize=11");
            umlDiagram.appendChild(helpText);

            int x = 0;
            int y = 0;

            Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
            while (iter != null && iter.hasNext()) {
                IFocDescDeclaration focDescDeclaration = iter.next();
                if (focDescDeclaration != null) {
                    FocDesc focDesc = focDescDeclaration.getFocDescription();
                    if (focDesc != null && focDesc.isDbResident()) {

                        printDataModel_XML_AddOneTable(doc, umlDiagram, focDesc, x, y);
                        x = x + 200;
                        if (x > 1000) {
                            y = y + 100;
                            x = 0;
                        }
                    }
                }
            }

            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty("indent", "yes");

            FileOutputStream fileOutStream = new FileOutputStream(fullFileName);
            t.transform(new DOMSource(doc), new StreamResult(fileOutStream));
            fileOutStream.flush();
            fileOutStream.close();

        } catch (IOException e) {
            Globals.logException(e);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void printModuleStructure_CSV(String fullFileName) {
        Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
        try {
            File fileToPost = new File(fullFileName);
            fileToPost.createNewFile();
            BufferedWriter fileToPostWriter = new BufferedWriter(new FileWriter(fileToPost, true));
            fileToPostWriter.write("TABLE,MODULE");
            while (iter != null && iter.hasNext()) {
                IFocDescDeclaration focDescDeclaration = iter.next();
                if (focDescDeclaration != null) {
                    FocDesc focDesc = focDescDeclaration.getFocDescription();
                    if (focDesc != null && focDesc.isDbResident()) {
                        String focDescName = focDesc.getStorageName();
                        Globals.logString("\n" + focDescName + ",");
                        fileToPostWriter.write("\n" + focDescName + ",");
                        if (focDesc.getModule() != null) {
                            fileToPostWriter.write(focDesc.getModule().getName());
                        }
                        fileToPostWriter.flush();
                    }
                }
            }
            fileToPostWriter.close();
            fileToPostWriter = null;
        } catch (IOException e) {
            Globals.logException(e);
        }
    }

    private void printDataModel_CSV(String fullFileName) {
        Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
        try {
            File fileToPost = new File(fullFileName);
            fileToPost.createNewFile();
            BufferedWriter fileToPostWriter = new BufferedWriter(new FileWriter(fileToPost, true));
            fileToPostWriter.write("TABLE,FIELD,TITLE,TYPE,FOREIGN TABLE");
            while (iter != null && iter.hasNext()) {
                IFocDescDeclaration focDescDeclaration = iter.next();
                if (focDescDeclaration != null) {
                    FocDesc focDesc = focDescDeclaration.getFocDescription();
                    if (focDesc != null && focDesc.isDbResident()) {
                        String focDescName = focDesc.getStorageName();
                        Globals.logString("\n" + "TABLE: " + focDescName);
                        fileToPostWriter.write("\n" + focDescName + ",");
                        fileToPostWriter.flush();

                        FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
                        while (enumer != null && enumer.hasNext()) {
                            FField field = enumer.nextField();
                            if (field != null) {
                                FFieldPath path = enumer.getFieldPath();
                                int firstFieldId = path.get(0);
                                FField firstField = focDesc.getFieldByID(firstFieldId);
                                FocDesc foreignDesc = firstField != null ? firstField.getFocDesc() : null;
                                String foreignStorage = foreignDesc != null ? foreignDesc.getStorageName() : "";

                                fileToPostWriter.write("\n," + enumer.getFieldCompleteName(focDesc) + "," + field.getTitle() + "," + getType(field.getSqlType()) + "," + foreignStorage);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            Globals.logException(e);
        }
    }

    /**
     * @return Returns the defaultAccessControl.
     */
    public AccessControl getDefaultAccessControl() {
        return defaultAccessControl;
    }

    /**
     * @return Returns the focIcons.
     */
    public FocIcons getFocIcons() {
        if (focIcons == null) {
            focIcons = new FocIcons();
        }
        return focIcons;
    }

    /**
     * @return Returns the logFile.
     */
    public PrintStream getLogFile() {
        PrintStream ret = pushedLogFile;
        if (ret == null) {
            ret = logFile;
        }
        return ret;
    }

    public boolean isWithLogin() {
        return withLogin;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public FMenu getMainAppMenu() {
        return mainAppMenu;
    }

    public void setMainAppMenu(FMenu mainAppMenu) {
        this.mainAppMenu = mainAppMenu;
    
    /*
    if(isCashDeskModuleIncluded() && mainAppMenu.isList()){
      FMenuList menuList = CashDeskModule.newMenuList();
      if(menuList != null){
        ((FMenuList) mainAppMenu).addMenu(menuList);
      }
    }
    */
    }

    public FMenu getMainFocMenu() {
        return mainFocMenu;
    }

    public void setMainFocMenu(FMenu mainFocMenu) {
        this.mainFocMenu = mainFocMenu;
    }

    public FMenu getMainAdminMenu() {
        return mainAdminMenu;
    }

    public void setMainAdminMenu(FMenu mainAdminMenu) {
        this.mainAdminMenu = mainAdminMenu;
    }

    public FocUser getUser() {
        return user;
    }

    public void setUser(FocUser user) {
        this.user = user;
        updateLanguage();
        if (user != null) {
            FMultipleChoice langMulti = (FMultipleChoice) user.getFocProperty(FocUser.FLD_LANGUAGE);
            if (langMulti != null) {
                langMulti.addListener(new FPropertyListener() {
                    public void propertyModified(FProperty prop) {
                        updateLanguage();
                    }

                    public void dispose() {
                    }
                });
            }
        }
    }

    public FocUser getUser_ForThisSession() {
        FocUser user = null;
        if (Globals.getApp().isWebServer()) {
            user = UserSession.getInstanceForThread() != null ? UserSession.getInstanceForThread().getUser() : null;
        } else {
            user = getUser();
        }
        return user;
    }

    public String getLanguageForThisSession() {
        String language = null;
        if (Globals.getApp().isWebServer()) {
            language = UserSession.getInstanceForThread() != null ? UserSession.getInstanceForThread().getLanguage() : null;
        } else {
            language = ConfigInfo.getLanguage();
        }
        return language;
    }

    public boolean checkSession() {
        return checkSession(true);
    }

    public boolean checkSession(boolean withPopupMesage) {
        boolean error = false;
        if (getUser_ForThisSession() == null) {
            error = true;
            if (Globals.getApp().isWebServer()) {
                if (UserSession.getInstanceForThread() == null) {
                    Globals.logString("DEBUG_SESSION_NOT_VALID THIS IS NULL UserSession.getInstanceForThread()");
                } else {
                    Globals.logString("DEBUG_SESSION_NOT_VALID THIS IS NULL UserSession.getInstanceForThread().getUser()");
                }
                user = UserSession.getInstanceForThread() != null ? UserSession.getInstanceForThread().getUser() : null;
            }
            if (withPopupMesage) {
                Globals.showNotification("SESSION NOT VALID", "Please close this tab", IFocEnvironment.TYPE_ERROR_MESSAGE);
            }
        }
        return error;
    }

    public void updateLanguage() {
        if (user != null) {
            FMultipleChoice langMulti = (FMultipleChoice) user.getFocProperty(FocUserDesc.FLD_LANGUAGE);
            if (langMulti != null) {
                MultiLanguage.setCurrentLanguage(langMulti.getInteger());
                reconstructMenu();
            }
        }
    }

    public FocGroup getGroup() {
        return user != null ? user.getGroup() : null;
    }

    public FocGroup getGroup_ForThisSession() {
        FocUser user = getUser_ForThisSession();
        return user != null ? user.getGroup() : null;
    }

    public FocObject getAppGroup() {
        return user != null ? user.getAppGroup() : null;
    }

    public DebugOutputInterface getDebugOutputInterface() {
        return debugOutputInterface;
    }

    public void setDebugOutputInterface(DebugOutputInterface debugOutputInterface) {
        this.debugOutputInterface = debugOutputInterface;
    }

    public FocNotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = new FocNotificationManager();
        }
        return notificationManager;
    }

    public void debugOutput(String message) {
        if (debugOutputInterface != null) debugOutputInterface.debugOutput(message);
    }

    public java.sql.Date getSystemDate() {
        if (Globals.getApp() != null && Globals.getApp().isWebServer()) {
            systemDate = new java.sql.Date(System.currentTimeMillis());
        } else {
            if (systemDate == null) {
                if (getDBManager() != null) {
                    systemDate = getDBManager().getCurrentDate();
                } else {
                    systemDate = new java.sql.Date(System.currentTimeMillis());
                }
            }
        }
        return systemDate;
    }

    public void addExitListener(IExitListener exitListener) {
        if (exitListenerList == null) {
            exitListenerList = new ArrayList<IExitListener>();
        }
        exitListenerList.add(exitListener);
    }

    public boolean isCashDeskModuleIncluded() {
        return cashDeskModuleIncluded;
    }

    public void setCashDeskModuleIncluded(boolean cashDeskModuleIncluded) {
        this.cashDeskModuleIncluded = cashDeskModuleIncluded;
    }

    public boolean isCurrencyModuleIncluded() {
        return currencyModuleIncluded;
    }

    public void setCurrencyModuleIncluded(boolean currencyModuleIncluded) {
        this.currencyModuleIncluded = currencyModuleIncluded;
    }

    public boolean isDisableMenus() {
        return disableMenus;
    }

    public void setDisableMenus(boolean disableMenus) {
        this.disableMenus = disableMenus;
    }

    public boolean isDoNotCheckTables() {
        return doNotCheckTables;
    }

    public void setDoNotCheckTables(boolean doNotCheckTables) {
        this.doNotCheckTables = doNotCheckTables;
    }

    public boolean isMdi() {
        return guiNavigatorType == DisplayManager.GUI_NAVIGATOR_MDI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDemo() {
        return isDemo;
    }

    public void setDemo(boolean isDemo) {
        this.isDemo = isDemo;
    }

    public int getTrialPeriod() {
        return trialPeriod;
    }

    public void setTrialPeriod(int days) {
        this.trialPeriod = days;
    }

    public int getGuiNavigatorType() {
        return guiNavigatorType;
    }

    public String getDataModelFileName() {
        return printDataModelFileName;
    }

    public boolean isShowBarmajaIconAndTitle() {
        return showBarmajaIconAndTitle;
    }

    public void setShowBarmajaIconAndTitle(boolean showBarmajaIconAndTitle) {
        this.showBarmajaIconAndTitle = showBarmajaIconAndTitle;
    }

    public String getCommandLineArgument(String key) {
        return argHash.get(key);
    }

    public PrintStream getPushedLogFile() {
        return pushedLogFile;
    }

    public void setPushedLogFile(PrintStream pushedLogFile) {
        this.pushedLogFile = pushedLogFile;
    }

    public Company getCurrentCompany() {
        Company company = null;
        if (Globals.getApp().isWebServer()) {
            company = UserSession.getInstanceForThread() != null ? UserSession.getInstanceForThread().getCompany() : null;
        } else {
            company = (getUser_ForThisSession() != null) ? getUser_ForThisSession().getCurrentCompany() : null;
        }
        return company;
    }

    public Company getCompany() {
        return UserSession.getInstanceForThread().getCompany();
    }

    public void setCompany(Company company) {
        UserSession.getInstanceForThread().setCompany(company);
    }

    public WFSite getCurrentSite() {
        return UserSession.getInstanceForThread() != null ? UserSession.getInstanceForThread().getSite() : null;
    }

    public WFTitle getCurrentTitle() {
        return UserSession.getInstanceForThread() != null ? UserSession.getInstanceForThread().getTitle() : null;
    }

    public boolean isSimulation() {
        return UserSession.getInstanceForThread() != null ? UserSession.getInstanceForThread().isSimulation() : false;
    }

    public PerfManager getPerfManager() {
        if (perfManager == null) {
            perfManager = new PerfManager();
        }
        return perfManager;
    }

    public IFocDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(IFocDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public IFocCloudStorage getCloudStorage() {
        if (cloudStorage == null && cloudStorage_TryToGet) {
            //Creating Cloud Storage
            String className = ConfigInfo.getCloudStorageClassName();
            if (className == null) {
                Globals.showNotification("Everpro Cloud Storage", "No class specified please contact 01Barmaja", IFocEnvironment.TYPE_ERROR_MESSAGE);
            } else {
                try {
                    Class<IFocCloudStorage> cls = (Class<IFocCloudStorage>) Class.forName(className);
                    Class[] param = new Class[0];
                    Constructor constr = cls.getConstructor(param);

                    Object[] argsNew = new Object[0];
                    this.cloudStorage = (IFocCloudStorage) constr.newInstance(argsNew);
                } catch (Exception e) {
                    Globals.logException(e);
                    String message = "Could not Instanciate Class named : " + className;
                    Globals.showNotification("Everpro Cloud Storage", message, IFocEnvironment.TYPE_ERROR_MESSAGE);
                    Globals.logString("Everpro Cloud Storage " + message);
                }
            }

            if (cloudStorage == null) cloudStorage_TryToGet = false;
        }

        return cloudStorage;
    }

    public String getCloudStorageDirectory() {
        return cloudStorageDirectory;
    }

    public void setCloudStorageDirectory(String cloudStorageDirectory) {
        this.cloudStorageDirectory = cloudStorageDirectory;
    }

    public boolean isWebServer() {
        return !isWithGui();
    }

    //----------------------
    //      fabDefTables
    //----------------------
    public int fabDefTables_Size() {
        return fabDefTables_ForAdaptFirst != null ? fabDefTables_ForAdaptFirst.size() : 0;
    }

    public String fabDefTables_Get(int at) {
        return fabDefTables_ForAdaptFirst != null ? fabDefTables_ForAdaptFirst.get(at) : null;
    }

    public void fabDefTables_Add(String fabTableName) {
        if (fabTableName != null) {
            if (fabDefTables_ForAdaptFirst == null) {
                fabDefTables_ForAdaptFirst = new ArrayList<String>();
            }
            fabDefTables_ForAdaptFirst.add(fabTableName);
        }
    }
    //----------------------

    public DataStore getDataStore() {
        if (dataStore == null) {
            dataStore = new DataStore();
        }
        return dataStore;
    }

    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public FocDescMap getFocDescMap() {
        if (focDescMap == null) {
            focDescMap = new FocDescMap();
        }
        return focDescMap;
    }

    public FocDescMap_ByFocObjectClassName getFocDescMap_ByFocObjectClassName() {
        if (focDescMap_ByFocObjectClassName == null) {
            focDescMap_ByFocObjectClassName = new FocDescMap_ByFocObjectClassName();
        }
        return focDescMap_ByFocObjectClassName;
    }

    public FocLogger getFocLogger(boolean create) {
        if (focLogger == null && create) {
            focLogger = new FocLogger();
        }
        return focLogger;
    }

    public void setFocLogger(FocLogger focLogger) {
        this.focLogger = focLogger;
    }

    public FocNotifActionFactory getNotificationActionFactory(boolean create) {
        if (notifActionFactory == null && create) {
            notifActionFactory = new FocNotifActionFactory();
        }
        return notifActionFactory;
    }

    public FocNotificationEventFactory getNotificationEventFactory(boolean create) {
        if (focNEF == null && create) {
            focNEF = new FocNotificationEventFactory();
        }
        return focNEF;
    }

    public void setFocNotificationEventFactory(FocNotificationEventFactory focNEF) {
        this.focNEF = focNEF;
    }

    public UserSession getUserSession_Swing() {
        if (userSession_Swing == null) {
            userSession_Swing = new UserSession();
        }
        return userSession_Swing;
    }

    public void setUserSession_Swing(UserSession userSession_Swing) {
        this.userSession_Swing = userSession_Swing;
    }

    public boolean isUnitTest() {
        return isUnitTest;
    }

    public void setIsUnitTest(boolean isUnitTest) {
        this.isUnitTest = isUnitTest;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public boolean isEmptyDatabaseJustCreated() {
        return (Globals.getApp() != null && Globals.getApp().getDataSource() != null) ? Globals.getApp().getDataSource().isEmptyDatabaseJustCreated() : false;
    }

    public Iterator<FocDesc> reportConfigFocDesc_Ierator() {
        Iterator<FocDesc> iterator = null;
        if (reportConfigFocDescMap != null) {
            iterator = reportConfigFocDescMap.values().iterator();
        }
        return iterator;
    }

    public void reportConfigFocDesc_Add(String context, FocDesc focDesc) {
        if (reportConfigFocDescMap == null) {
            reportConfigFocDescMap = new HashMap<String, FocDesc>();
        }
        reportConfigFocDescMap.put(context, focDesc);
    }

    public FocDesc reportConfigFocDesc_Get(String context) {
        return reportConfigFocDescMap != null ? reportConfigFocDescMap.get(context) : null;
    }

    public void setLogListener(FocLogListener logListener) {
        if (logListener != null) {
            Globals.logString("-FocLogListener: Setting the FocLogListener: " + logListener.getClass().getName());
            this.logListener = logListener;
        }
    }

    public boolean hasLogListener() {
        return logListener != null;
    }

    public boolean logListenerIsEventIncluded(FocLogEvent event) {
        return logListener != null && logListener.isLogEventIncluded(event);
    }

    public boolean logListenerWillVerifyLastHash() {
        return logListener != null && logListener.iVerifyLastHash();
    }

    public void logListenerNotification(FocLogEvent event) {
        if (logListener != null && event != null &&
                (event.logEvent_GetStatus() == FocLogEvent.STATUS_INCLUDED || event.logEvent_GetStatus() == FocLogEvent.STATUS_POSTED)) {
            Globals.logString("-FocLogListener: Notification:" + event.logEvent_GetEventType() + " Entity:" + event.logEvent_GetEntityName() + ":" + event.logEvent_GetEntityCode());
            logListener.addLogEvent(event);
        }
    }

    public void setLogListenerIfConfigured() {
        String listenerClassName = ConfigInfo.getLogListenerClassName();
        Globals.logString("-FocLogListener: ConfigInfo.LogListenerClassName = " + listenerClassName + ".");
        if (!Utils.isStringEmpty(listenerClassName)) {
            try {
                Class<IFocCloudStorage> cls = (Class<IFocCloudStorage>) Class.forName(listenerClassName);
                Class[] param = new Class[0];
                Constructor constr = cls.getConstructor(param);

                Object[] argsNew = new Object[0];
                Object createdObject = constr.newInstance(argsNew);
                setLogListener((FocLogListener) createdObject);
            } catch (Exception e) {
                Globals.logException(e);
                String message = "!! ERROR: LogListener: Could not Instanciate Log Listener Class named: " + listenerClassName;
                Globals.logString(message);
            }
        }
    }

    public boolean setLogEventStatus(String entityName, long logEventRef, int status) {
        return setLogEventStatus(entityName, logEventRef, status, "");
    }

    public boolean setLogEventStatus(String entityName, long logEventRef, int status, String statusCommitError) {
        boolean error = logEventRef <= 0 || Utils.isStringEmpty(entityName);

        if (!error) {
            FocDesc focDesc = getFocDescByName(entityName);
            FocDesc logFocDesc = focDesc != null ? focDesc.getWFLogDesc() : null;

            if (logFocDesc == null) {
                error = true;
            } else {
                if (statusCommitError.length() > (WFLogDesc.LEN_STATUS_ERROR / 2)) {
                    statusCommitError = statusCommitError.substring(0, (WFLogDesc.LEN_STATUS_ERROR / 2));
                }

                StringBuffer buffer = null;
                if (focDesc.getProvider() == DBManager.PROVIDER_MYSQL) {
                    buffer = new StringBuffer("UPDATE " + logFocDesc.getStorageName_ForSQL() + " ");
                    buffer.append("set EVT_STATUS = " + status + " , STATUS_ERROR = \"" + statusCommitError + "\"");
                    buffer.append(" where " + logFocDesc.getRefFieldName() + " = " + logEventRef + " ");
                } else {
                    buffer = new StringBuffer("UPDATE \"" + logFocDesc.getStorageName_ForSQL() + "\" ");
                    buffer.append("set \"EVT_STATUS\" = " + status + " , \"STATUS_ERROR\" = \'" + statusCommitError + "\'");
                    buffer.append(" where \"" + logFocDesc.getRefFieldName() + "\" = " + logEventRef + " ");
                }
                error = Globals.getApp().getDataSource().command_ExecuteRequest(buffer);
            }
        }

        return error;
    }

    public void logListenerGetLastHash(ILoggedHashContainer lastHash) {
        if (logListener != null
                && lastHash != null) {
            Globals.logString("-FocLogListener: LastHash request");
            logListener.getLastHash(lastHash);
        }
    }

    public FSerializerDictionary getHTMLGeneratorDictionary() {
        if (htmlGeneratorDictionary == null) {
            htmlGeneratorDictionary = new FSerializerDictionary();
        }
        return htmlGeneratorDictionary;
    }

    //FILE_ENCRYPTION
    public IFocEncryptor getEncryptor() {
        return iFocEncryptor;
    }

    public void setEncryptor(IFocEncryptor iFocEncryprtion) {
        this.iFocEncryptor = iFocEncryprtion;
    }

    public void setFocEncryptionIfConfigured() {
        String ecryptionClassName = ConfigInfo.getEncryptionClassName();
        Globals.logString("-FocEncryptionClassName : ConfigInfo.EncryptionClassName = " + ecryptionClassName + ".");
        if (!Utils.isStringEmpty(ecryptionClassName)) {
            try {
                Class<IFocEncryptor> cls = (Class<IFocEncryptor>) Class.forName(ecryptionClassName);
                Class[] param = new Class[0];
                Constructor constr = cls.getConstructor(param);

                Object[] argsNew = new Object[0];
                Object createdObject = constr.newInstance(argsNew);
                setEncryptor((IFocEncryptor) createdObject);
            } catch (Exception e) {
                Globals.logException(e);
                String message = "!! ERROR: IFocFileEncryptor: Could not Instanciate Log Listener Class named: " + ecryptionClassName;
                Globals.logString(message);
            }
        }
    }

    public String dumpLivingFocObjectCounts(boolean withGuiMessage, boolean includeCached, boolean html) {
        StringBuffer buffer = new StringBuffer();
        String fileName = null;
        try {
            fileName = Globals.logFile_GetFileName("EntityCounts", "csv");
            PrintStream logFile = (ConfigInfo.isLogFileActive() && !Globals.logFile_CheckLogDir()) ? new PrintStream(fileName, "UTF-8") : null;

            logFile.println("Entity,Cached,Count");

            buffer.append("Log File dumped " + fileName);
            if (html) {
                buffer.append("<table>");
                buffer.append("<tr>");
                buffer.append("  <th>Entity</th>");
                buffer.append("  <th>Is Cahced</th>");
                buffer.append("  <th>Count</th>");
                buffer.append("</tr>");
            } else {
                buffer.append("Entity  |  Cached  |  Count\n");
            }

            Iterator<IFocDescDeclaration> iter = Globals.getApp() != null ? Globals.getApp().getFocDescDeclarationIterator() : null;
            while (iter != null && iter.hasNext()) {
                IFocDescDeclaration focDescDeclaration = iter.next();
                if (focDescDeclaration != null) {
                    FocDesc focDesc = focDescDeclaration.getFocDescription();
                    if (focDesc != null && (includeCached || !focDesc.isListInCache())) {
                        int size = focDesc.allFocObjectArray_size();
                        if (size > 0) {
                            logFile.println(focDesc.getStorageName() + "," + focDesc.isListInCache() + "," + size);
                            if (html) {
                                buffer.append("<tr>");
                                buffer.append("  <td>" + focDesc.getStorageName() + "</td>");
                                buffer.append("  <td>" + focDesc.isListInCache() + "</td>");
                                buffer.append("  <td>" + size + "</td>");
                                buffer.append("</tr>");
                            } else {
                                buffer.append(focDesc.getStorageName() + "  |  " + focDesc.isListInCache() + "  |  " + size + "\n");
                            }
                        }
                    }
                }
            }

            if (html) buffer.append("</table>");

            logFile.close();

            if (withGuiMessage) {
                //Globals.showNotification("Log File dumped", fileName, IFocEnvironment.TYPE_WARNING_MESSAGE);
            }

        } catch (Exception e) {
            Globals.logException(e);
        }

        return buffer.toString();
    }

    public IDBReloader getDbReloader() {
        return dbReloader;
    }

    public void setDbReloader(IDBReloader dbReloader) {
        this.dbReloader = dbReloader;
    }

}