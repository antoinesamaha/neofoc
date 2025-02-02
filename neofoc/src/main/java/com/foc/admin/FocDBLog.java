package com.foc.admin;

import java.sql.Date;
import java.util.HashMap;

import com.foc.Globals;
import com.foc.access.AccessSubject;
import com.foc.annotations.model.FocChoice;
import com.foc.annotations.model.FocEntity;
import com.foc.annotations.model.fields.FocDateTime;
import com.foc.annotations.model.fields.FocForeignEntity;
import com.foc.annotations.model.fields.FocLong;
import com.foc.annotations.model.fields.FocMultipleChoice;
import com.foc.annotations.model.fields.FocString;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.parsers.ParsedFocDesc;
import com.foc.desc.parsers.pojo.PojoFocObject;
import com.foc.property.FProperty;
import com.foc.util.Utils;

@FocEntity(cached = false)
public class FocDBLog extends PojoFocObject {

  public static final String DBNAME = "FocDBLog";

  public static final int ACTION_NONE   =  0;
  public static final int ACTION_INSERT =  1;
  public static final int ACTION_UPDATE =  2;
  public static final int ACTION_DELETE =  3;
  
	@FocMultipleChoice(size = 2, choices= {
			@FocChoice(id=ACTION_NONE  , title="None"),
			@FocChoice(id=ACTION_INSERT, title="Created"),
			@FocChoice(id=ACTION_UPDATE, title="Updated"),
			@FocChoice(id=ACTION_DELETE, title="Deleted")
	})
	public static final String FIELD_Action = "Action";

	@FocDateTime()
	public static final String FIELD_DateTime = "DateTime";

	@FocForeignEntity(table = "FUSER")
	public static final String FIELD_User = "User";

	@FocString(size = 5000)
	public static final String FIELD_Change = "Change";
	
	@FocString(size = 250)
	public static final String FIELD_TableName = "TableName";

	@FocLong()
	public static final String FIELD_ObjectRef = "ObjectRef";

	@FocString(size = 250)
	public static final String FIELD_MasterTableName = "MasterTableName";

	@FocLong()
	public static final String FIELD_MasterObjectRef = "MasterObjectRef";

  public FocDBLog(FocConstructor constr) {
		super(constr);
  }
  
	public static ParsedFocDesc getFocDesc() {
		return ParsedFocDesc.getInstance(DBNAME);
	}
	
	public int getAction() {
		return getPropertyInteger(FIELD_Action);
	}

	public void setAction(int value) {
		setPropertyInteger(FIELD_Action, value);
	}

	public Date getDateTime() {
		return getPropertyDate(FIELD_DateTime);
	}

	public void setDateTime(Date value) {
		setPropertyDate(FIELD_DateTime, value);
	}

	public FocUser getUser() {
		return (FocUser) getPropertyObject(FIELD_User);
	}

	public void setUser(FocUser value) {
		setPropertyObject(FIELD_User, value);
	}

	public String getChange() {
		return getPropertyString(FIELD_Change);
	}

	public void setChange(String value) {
		setPropertyString(FIELD_Change, value);
	}

	public String getTableName() {
		return getPropertyString(FIELD_TableName);
	}

	public void setTableName(String value) {
		setPropertyString(FIELD_TableName, value);
	}
	
	public long getObjectRef() {
		return getPropertyLong(FIELD_ObjectRef);
	}

	public void setObjectRef(long value) {
		setPropertyLong(FIELD_ObjectRef, value);
	}

	public String getMasterTableName() {
		return getPropertyString(FIELD_MasterTableName);
	}

	public void setMasterTableName(String value) {
		setPropertyString(FIELD_MasterTableName, value);
	}

	public long getMasterTableRef() {
		return getPropertyLong(FIELD_MasterObjectRef);
	}

	public void setMasterTableRef(long value) {
		setPropertyLong(FIELD_MasterObjectRef, value);
	}
	
	public static String prepareLogChangeString(FocObject focObject) {
  	String change = "";
  	if(focObject != null) {
	  	FocFieldEnum enumer = focObject.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
	  	while(enumer != null && enumer.hasNext()) {
	  		enumer.nextField();
	  		FProperty prop = enumer.getProperty();
	  		if(prop != null && prop.isModifiedFlag() && prop.getFocField() != null && prop.getFocField().isDBResident()) {
	  			if(!Utils.isStringEmpty(change)) {
	  				change += "\n";
	  			}
	  			change += prop.getFocField().getName()+": "+prop.getModificationLogString();  
	  		}
	  	}
  	}
  	return change;
	}
	
	public static FocConstructor constr = null;
	public static void addLog(FocObject focObject, int action, String change) {
		if (focObject != null && focObject.getThisFocDesc() != null && focObject.getReferenceInt() > 0) { 
			String tableName = focObject.getThisFocDesc().getStorageName();
			long ref = focObject.getReferenceInt();
			
			FocObject master = null;
			AccessSubject subject = focObject.getFatherSubject();
			while (subject != null && master == null) {
				if(subject instanceof FocObject) {
					master = (FocObject) subject; 
				}
				subject = subject.getFatherSubject();
			}
			
			String masterTableName = master != null && master.getThisFocDesc() != null ? master.getThisFocDesc().getStorageName() : null;
			long masterRef = master != null ? master.getReferenceInt() : 0;
			
			addLog(tableName, ref, action, change, masterTableName, masterRef);
		} else {
			String message = "";
			if(focObject == null) message = "Object is null";
			else if(focObject.getThisFocDesc() == null) message = "FocDesc is null";
			else if(focObject.getReferenceInt() <= 0) message = "Reference is "+focObject.getReferenceInt();
			Globals.logString("Could not log event because: "+message);
		}
	}
	
	public static void addLog(String tableName, long ref, int action, String change, String masterTableName, long masterRef) {
		if (isIncluded(tableName)) {
			if(constr == null && getFocDesc() != null) {
				constr = new FocConstructor(getFocDesc());
			}
			if(constr != null) {
				FocDBLog log = new FocDBLog(constr);
				log.setCreated(true);
				log.setAction(action);
				log.setTableName(tableName);
				log.setObjectRef(ref);
				log.setMasterTableName(masterTableName);
				log.setMasterTableRef(masterRef);
				log.setUser(Globals.getApp().getUser_ForThisSession());
				log.setDateTime(new Date(System.currentTimeMillis()));
				if(change != null) log.setChange(change);
				log.validate(false);
				log.dispose();
				log = null;
			}
		}
	}
	
	public static HashMap<String, String> tablesToExclude = null;

	private static HashMap<String, String> getTablesToExclude() {
		if(tablesToExclude == null) {
			tablesToExclude = new HashMap<String, String>();
			tablesToExclude.put(DBNAME, DBNAME);
			tablesToExclude.put(FocUserHistoryDesc.DB_TABLE_NAME, FocUserHistoryDesc.DB_TABLE_NAME);
			tablesToExclude.put(FocVersion.TABLE_NAME, FocVersion.TABLE_NAME);
		}
		return tablesToExclude;
	}
	
	public static void addTablesToExclude(String tableName){
		HashMap<String, String> tablesToExclude = getTablesToExclude();
		tablesToExclude.put(tableName, tableName);
	}
	
	public static boolean isIncluded(String tableName) {
		HashMap<String, String> tablesToExclude = getTablesToExclude();		
		return tablesToExclude == null || !tablesToExclude.containsKey(tableName);
	}

}
