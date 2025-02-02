package com.foc.admin;

import com.foc.annotations.model.FocEntity;
import com.foc.annotations.model.fields.FocString;
import com.foc.desc.FocConstructor;
import com.foc.desc.parsers.ParsedFocDesc;
import com.foc.desc.parsers.pojo.PojoFocObject;
import com.foc.list.FocList;

@FocEntity(cached = true)
public class FocExternalAuthConfig extends PojoFocObject {

  public static final String DBNAME = "FocExternalAuthConfig";

	@FocString(size = 100)
	public static final String FIELD_JWTIssuer = "JWTIssuer";

	@FocString(size = 1000)
	public static final String FIELD_SQLSelectKeys = "SQLSelectKeys";

	@FocString(size = 2000)
	public static final String FIELD_SQLSelectRolePermission = "SQLSelectRolePermission";

	@FocString(size = 100)
	public static final String FIELD_Permission = "Permission";

  public FocExternalAuthConfig(FocConstructor constr) {
		super(constr);
  }
  
	public static ParsedFocDesc getFocDesc() {
		return ParsedFocDesc.getInstance(DBNAME);
	}
	
	public String getJWTIssuer() {
		return getPropertyString(FIELD_JWTIssuer);
	}

	public void setJWTIssuer(String value) {
		setPropertyString(FIELD_JWTIssuer, value);
	}

	public String getSQLSelectKeys() {
		return getPropertyString(FIELD_SQLSelectKeys);
	}

	public void setSQLSelectKeys(String value) {
		setPropertyString(FIELD_SQLSelectKeys, value);
	}

	public String getSQLSelectRolePermission() {
		return getPropertyString(FIELD_SQLSelectRolePermission);
	}

	public void setSQLSelectRolePermission(String value) {
		setPropertyString(FIELD_SQLSelectRolePermission, value);
	}

	private static FocExternalAuthConfig config = null; 
	public static FocExternalAuthConfig getInstance() {
		if (getFocDesc() != null) {
			FocList list = getFocDesc().getFocList();
			if (list != null) {
				list.loadIfNotLoadedFromDB();
				if(list.size() == 0) {
					config = (FocExternalAuthConfig) list.newEmptyItem();
					list.add(config);
		
					config.save();
				}
				
				if(config == null) {
					config = (FocExternalAuthConfig) list.getFocObject(0);
				}
			}
		}

		return config;
	}

	public String getPermission() {
		return getPropertyString(FIELD_Permission);
	}

	public void setPermission(String value) {
		setPropertyString(FIELD_Permission, value);
	}

}
