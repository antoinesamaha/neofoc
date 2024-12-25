package com.foc.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FStringField;
import com.foc.list.FocList;
import com.foc.list.FocListWithFilter;
import com.foc.list.filter.FilterCondition;
import com.foc.list.filter.FocListFilter;
import com.foc.util.Utils;

public class FocRestAPICall {
	private HttpServletRequest request = null;

	private FocDesc focDesc;
	
	private boolean masterOwner = false;
	private FocObject master = null;
	private long masterRef = 0;

	private boolean listOwner = false;
	private FocList list = null;
	private long ref = 0;

	public FocRestAPICall(HttpServletRequest request, FocDesc focDesc) {
		this.request = request;
		this.focDesc = focDesc;

		if (request != null) {
			String refStr = request.getParameter("Id");

			long ref = refStr != null ? Utils.parseLong(refStr, 0) : 0;
			setRef(ref);

			String masterRefStr = request.getParameter("master_ref");
			long masterRef = masterRefStr != null ? Utils.parseLong(masterRefStr, 0) : 0;
			setMasterRef(masterRef);
		}
	}

	public void dispose() {
		focDesc = null;
		request = null;

		if (list != null && isListOwner()) {
			list.dispose();
			list = null;
		}

		if (masterOwner && master != null) {
			master.dispose();
			master = null;
		}
	}

	public FocDesc getFocDesc() {
		return focDesc;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public boolean isListOwner() {
		return listOwner;
	}

	public void setListOwner(boolean listOwner) {
		this.listOwner = listOwner;
	}

	public FocList getList() {
		return list;
	}

	public void setList(FocList list) {
		this.list = list;
	}

	public long getRef() {
		return ref;
	}

	public void setRef(long ref) {
		this.ref = ref;
	}

	public long getMasterRef() {
		return masterRef;
	}

	public void setMasterRef(long masterRef) {
		this.masterRef = masterRef;
	}

	public FocObject getMaster() {
		return master;
	}

	public void setMaster(FocObject master) {
		this.master = master;
	}

	public boolean isMasterOwner() {
		return masterOwner;
	}

	public void setMasterOwner(boolean masterOwner) {
		this.masterOwner = masterOwner;
	}

	public void applyFiltersToListWithFilter(FocListWithFilter list) {
		if (getRequest() != null && list != null) {
			FocDesc focDesc = list.getFocDesc();
			if (focDesc != null) {
				Enumeration<String> paramsEnum = getRequest().getParameterNames();

				if (paramsEnum != null) {
					while (paramsEnum.hasMoreElements()) {
						String name = paramsEnum.nextElement();
						if (name.startsWith("filter_")) {
							String conditionName = name.substring("filter_".length());

							FocListFilter filter = (FocListFilter) list.getFocListFilter();
							FilterCondition condition = filter.findFilterCondition(conditionName);
							String value = getRequest().getParameter(name);
							if (condition != null && value != null) {
								condition.parseString(filter, value);
							}
						}
					}
				}
			}
		}
	}

	public void applyFiltersToList(FocList list) {
		if (getRequest() != null && list != null) {
			FocDesc focDesc = list.getFocDesc();
			if (focDesc != null) {
				Enumeration<String> paramsEnum = getRequest().getParameterNames();

				if (paramsEnum != null) {
					while (paramsEnum.hasMoreElements()) {
						String name = paramsEnum.nextElement();
						if (name.startsWith("filter_")) {
							String fieldName = name.substring("filter_".length());
							FField field = focDesc.getFieldByName(fieldName);
							if (field != null) {
								if (list.getFilter() != null) {

									if (field instanceof FStringField) {
										String fieldNameSQL = DBManager.provider_ConvertFieldName(focDesc.getProvider(),
												fieldName);
										String value = getRequest().getParameter(name);
										list.getFilter().putAdditionalWhere("PARAM_FILTER" + fieldName,
												fieldNameSQL + "='" + value + "'");
									} else if (field instanceof FObjectField) {
										String fieldNameSQL = fieldName + "_" + FField.REF_FIELD_NAME;
										fieldNameSQL = DBManager.provider_ConvertFieldName(focDesc.getProvider(),
												fieldNameSQL);
										String value = getRequest().getParameter(name);
										long ref = Utils.parseLong(value, -1);
										list.getFilter().putAdditionalWhere("PARAM_FILTER" + fieldName,
												fieldNameSQL + "=" + ref + "");
									}
								}
							}
						}
					}
				}
			}
		}
	}
}