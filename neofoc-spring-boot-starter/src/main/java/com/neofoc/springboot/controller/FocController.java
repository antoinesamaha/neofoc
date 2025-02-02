package com.neofoc.springboot.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import com.foc.Globals;
import com.foc.admin.GrpMobileModuleRights;
import com.foc.business.workflow.implementation.FocWorkflowObject;
import com.foc.controller.FocRestAPICall;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocDescMap;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.shared.json.B01JsonBuilder;
import com.foc.util.Utils;

@RestController
@RequestMapping("foc")
public class FocController {

    @Autowired
    private PathMatcher pathMatcher;

    private String getWildcardParam(HttpServletRequest request) {
        String patternAttribute = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String mappingAttribute = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return this.pathMatcher.extractPathWithinPattern(patternAttribute, mappingAttribute);
    }

    @PostMapping("asdasdaobj/**")
    public ResponseEntity<String> post(HttpServletRequest request, @RequestBody String body) {
        String result = "";

        String path = this.getWildcardParam(request);
        String[] params = path.split("/");
        String focDescName = params[0];

        FocDesc focDesc = Globals.getApp().getFocDescMap().get(focDescName);
        if (focDesc != null) {
            FocList list = focDesc.getFocList();
            FocObject obj = list.newEmptyItem();
//			obj.parse

        } else {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("asdasdsadobj/**")
    public ResponseEntity<String> getDashboard(HttpServletRequest request, String shortUrl) {
        String path = this.getWildcardParam(request);
        System.out.print("THE URL IS: " + path);

        String result = "";

        String[] params = path.split("/");
        String focDescName = params[0];
        FocDesc focDesc = Globals.getApp().getFocDescMap().get(focDescName);
        if (focDesc == null) {
            result = "Error";
            FocDescMap map = Globals.getApp().getFocDescMap();
            java.util.Iterator<String> iter = map.keySet().iterator();
            while (iter != null && iter.hasNext()) {
                String key = iter.next();
                System.out.println("   Table: " + key);
            }
        } else {
            FocList focList = focDesc.getFocList();

            B01JsonBuilder builder = new B01JsonBuilder();
            focList.toJson(builder);
            result = builder.toString();

            System.out.print("JSON: " + result);
        }

        return ResponseEntity.ok().body(result);
    }

    protected void setCORS(HttpServletResponse response) {
        if (response != null) {
            String specialHeader = "os, browser, device, os_version, browser_version, deviceType, App-Version";

            response.setHeader("Content-Type", "application/json; charset=UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers",
                    "Content-Type, X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Date, X-Api-Version, X-File-Name, X-Pagination, Content-Disposition, showLoader, Authorization, x-authorization, username, password, "
                            + specialHeader);
            response.setHeader("Access-Control-Expose-Headers",
                    "Content-Type, X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Date, X-Api-Version, X-File-Name, X-Pagination, Content-Disposition, showLoader, Authorization, x-authorization, username, password, "
                            + specialHeader);
            response.setHeader("Access-Control-Max-Age", "86400");
            response.setHeader("Allow", "GET, HEAD, POST, TRACE, OPTIONS");
        }
    }

    public void fillFocObjectFromJson(FocObject focObj, JSONObject jsonObj) throws Exception {
        String[] names = JSONObject.getNames(jsonObj);
        for (int i=0; i<names.length; i++) {
            focObj.jsonParse(jsonObj, names[i]);
        }
    }

    public boolean allowGet(FocRestAPICall focRequest) {
        return true;
    }

    public boolean allowPost(FocRestAPICall focRequest) {
        return true;
    }

    public boolean allowPut(FocRestAPICall focRequest) {
        return true;
    }

    public boolean allowDelete(FocRestAPICall focRequest) {
        return true;
    }

    public boolean allowOptions(FocRestAPICall focRequest) {
        return true;
    }

    public boolean useCachedList(FocRestAPICall focRequest) {
        return true;
    }

    // ------------------------------------
    // ------------------------------------
    // RIGHTS
    // ------------------------------------
    // ------------------------------------

    protected String mobileModule_GetModuleName(FocRestAPICall focRequest) {
        return null;
    }

    protected GrpMobileModuleRights mobileModule_GetModule(FocRestAPICall focRequest, String mobileModuleName) {
        GrpMobileModuleRights mobileModule = null;
        if (Globals.getApp() != null && Globals.getApp().getUser_ForThisSession() != null
                && Globals.getApp().getUser_ForThisSession().getGroup() != null) {
            mobileModule = Globals.getApp().getUser_ForThisSession().getGroup()
                    .getMobileModuleRightsObject(mobileModuleName);
        }
        return mobileModule;
    }

    protected boolean mobileModule_HasRight(FocRestAPICall focRequest, char crud) {
        boolean right = false;
        String moduleName = mobileModule_GetModuleName(focRequest);
        if (Utils.isStringEmpty(moduleName)) {
            right = true;
        } else {
            GrpMobileModuleRights module = mobileModule_GetModule(focRequest, moduleName);
            if (module == null) {
                right = false;
            } else {
                switch (crud) {
                    case 'C':
                        right = module.getCreate();
                        break;
                    case 'R':
                        right = module.getRead();
                        break;
                    case 'U':
                        right = module.getUpdate();
                        break;
                    case 'D':
                        right = module.getDelete();
                        break;
                }
            }
        }
        return right;
    }

    public boolean mobileModule_HasCreate(FocRestAPICall focRequest) {
        return mobileModule_HasRight(focRequest, 'C');
    }

    public boolean mobileModule_HasRead(FocRestAPICall focRequest) {
        return mobileModule_HasRight(focRequest, 'R');
    }

    public boolean mobileModule_HasUpdate(FocRestAPICall focRequest) {
        return mobileModule_HasRight(focRequest, 'U');
    }

    public boolean mobileModule_HasDelete(FocRestAPICall focRequest) {
        return mobileModule_HasRight(focRequest, 'D');
    }

    // --------------------------------------------------------------------------------

    @Deprecated
    protected void copyDATEFromJson(FocObject focObj, JSONObject jsonObj) {
        if (focObj != null) {
            focObj.jsonParseDATE(jsonObj);
        }
    }

    @Deprecated
    protected void copyStringFromJson(FocObject focObj, JSONObject jsonObj, String fieldName) {
        if (focObj != null) {
            focObj.jsonParse(jsonObj, fieldName);
        }
    }

    @Deprecated
    protected void copyDateFromJson(FocObject focObj, JSONObject jsonObj, String fieldName) {
        if (focObj != null) {
            focObj.jsonParse(jsonObj, fieldName);
        }
    }

    @Deprecated
    protected void copyBooleanFromJson(FocObject focObj, JSONObject jsonObj, String fieldName) {
        if (focObj != null) {
            focObj.jsonParse(jsonObj, fieldName);
        }
    }

    @Deprecated
    protected void copyIntFromJson(FocObject focObj, JSONObject jsonObj, String fieldName) {
        if (focObj != null) {
            focObj.jsonParse(jsonObj, fieldName);
        }
    }

    @Deprecated
    protected void copyLongFromJson(FocObject focObj, JSONObject jsonObj, String fieldName) {
        if (focObj != null) {
            focObj.jsonParse(jsonObj, fieldName);
        }
    }

    @Deprecated
    protected void copyDoubleFromJson(FocObject focObj, JSONObject jsonObj, String fieldName) {
        if (focObj != null) {
            focObj.jsonParse(jsonObj, fieldName);
        }
    }

    @Deprecated
    protected void copyForeignKeyFromJson(FocObject focObj, JSONObject jsonObj, String fieldName) {
        if (focObj != null) {
            focObj.jsonParse(jsonObj, fieldName);
        }
    }

    private static final long EXPIRY_TIME = 1 * 60 * 1000;
    private long lastLoadTime = 0;

    public FocList newFocList(FocRestAPICall request, boolean loaded) {
        FocDesc focDesc = request != null ? request.getFocDesc() : null;
        FocList list = focDesc != null ? focDesc.getFocList() : null;
        if (loaded) {
            // If refresh cached list is functional on the foc level then here we do not
            // need to do it
//			if(false && !ConfigInfo.isRefreshCachedLists() && System.currentTimeMillis() - lastLoadTime > EXPIRY_TIME) {
//				lastLoadTime = System.currentTimeMillis();
//				list.reloadFromDB();
//			} else {
            list.loadIfNotLoadedFromDB();
//			}
        }
        return list;
    }

    public void disposeFocList(FocRestAPICall focRequest, FocList focList) {
        if (focList != null) {
            focList.dispose();
            focList = null;
        }
    }

    public void applyRefFilterIfNeeded(HttpServletRequest request, FocList list) {
        if (list != null && request != null) {
            long filter_Ref = doGet_GetReference(request, list);
            if (filter_Ref > 0 && list.getFilter() != null)
                list.getFilter().putAdditionalWhere("REF", "\"REF\"=" + filter_Ref);
        }
    }

    protected long doPost_GetReference(JSONObject jsonObj, FocList list) {
        int ref = 0;
        if (jsonObj != null && jsonObj.has("REF")) {
            try {
                ref = jsonObj.getInt("REF");
            } catch (Exception e) {
//                Globals.logException(e);
            }
            if (ref == 0) {
                try {
                    String strValue = jsonObj.getString("REF");
                    if (!strValue.equalsIgnoreCase("null")) {
                        ref = Utils.parseInteger(strValue, 0);
                    }
                } catch (Exception e) {
                    Globals.logException(e);
                }
            }
        }
        return ref;
    }

    protected long doGet_GetReference(HttpServletRequest request, FocList list) {
        return getFilterRef(request);
    }

    public long getFilterRef(HttpServletRequest request) {
        String refStr = request != null ? request.getParameter("REF") : null;
        long ref = refStr != null ? Utils.parseLong(refStr, 0) : 0;
        return ref;
    }

    protected B01JsonBuilder newJsonBuiler(HttpServletRequest request) {
        B01JsonBuilder builder = new B01JsonBuilder();
        builder.setPrintForeignKeyFullObject(true);
        builder.setHideWorkflowFields(true);
        builder.setScanSubList(true);
        return builder;
    }

    protected void logRequestHeaders(HttpServletRequest request) {
        if (request != null) {
            Enumeration<String> enums = request.getHeaderNames();
            if (enums != null) {
                while (enums.hasMoreElements()) {
                    String key = enums.nextElement();
                    if (key != null) {
                        String value = "";
                        if (key.equals("x-authorization") || key.equals("Authorization")) {
                            value = "***";
                        } else {
                            value = request.getHeader(key);
                        }
                        Globals.logString(" = Header: " + key + ": " + value);
                    }
                }
            }
        }
    }

    protected FocRestAPICall newFocRestAPICall(HttpServletRequest request, FocDesc focDesc) {
        FocRestAPICall focRequest = new FocRestAPICall(request, focDesc);
        return focRequest;
    }

    protected int getStartParameter(HttpServletRequest request) {
        String startStr = request != null ? request.getParameter("start") : null;
        int start = startStr != null ? Integer.valueOf(startStr) : -1;
        return start;
    }

    protected int getCountParameter(HttpServletRequest request) {
        String countStr = request != null ? request.getParameter("count") : null;
        int count = countStr != null ? Integer.valueOf(countStr) : -1;
        return count;
    }

    protected int requestTotalCount(FocList list) {
        int totalCount = list != null ? list.requestCount() : 0;
        return totalCount;
    }

    protected String toJsonDetails(FocObject focObject, B01JsonBuilder builder) {
        focObject.toJson(builder);
        return builder.toString();
    }

    private PathDetails getPathDetails(HttpServletRequest request) {
        PathDetails requestParams = new PathDetails();

        String path = getWildcardParam(request);
        String[] params = path.split("/");

        if (params != null) {
            if (params.length > 0) {
                requestParams.setResourceName(params[0]);
                requestParams.setFocDesc(Globals.getApp().getFocDescMap().get(params[0]));
            }
            if (params.length > 1) {
                requestParams.setId(Utils.parseLong(params[1], 0));
            }
            if (params.length > 2) {
                requestParams.setAction(params[2]);
            }
        }
        return requestParams;
    }

    private FocDesc getFocDescFromPath(HttpServletRequest request) {
        String path = getWildcardParam(request);
        String[] params = path.split("/");
        String focDescName = params[0];
        return Globals.getApp().getFocDescMap().get(focDescName);
    }

    @GetMapping("obj/**")
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String result = "";

        PathDetails reqParams = getPathDetails(request);
        FocDesc focDesc = reqParams.getFocDesc();

        try {
            if (request != null && request.getSession() != null) {
                Globals.logString("Session ID Started request" + request.getSession().getId());
            }
            FocRestAPICall focRequest = null;
            try {
                focRequest = newFocRestAPICall(request, focDesc);
                String userJson = "";
                int returnedStatus = HttpServletResponse.SC_NOT_IMPLEMENTED;

                Globals.logString(" => GET Begin ");
                if (allowGet(null) && mobileModule_HasRead(focRequest)) {
                    logRequestHeaders(request);

                    String responseBody = "";
                    B01JsonBuilder builder = newJsonBuiler(request);

                    // We get this only to see if we return a list or just an object

                    FocList list = newFocList(focRequest, true);

                    long filterRef = reqParams.getId();

                    if (filterRef == 0) {
                        filterRef = doGet_GetReference(request, list);
                    }
                    if (filterRef > 0) {
                        FocObject focObject = null;
                        if (!useCachedList(null)) {
                            if (list.size() == 1) {
                                focObject = list.getFocObject(0);
                            }
                        } else {
                            focObject = list.searchByReference(filterRef);
                        }

                        if (focObject != null) {
                            userJson = toJsonDetails(focObject, builder);
                            responseBody = userJson;
                        } else {
                            userJson = "{}";
                            responseBody = userJson;
                        }
                    } else {
                        int start = -1;
                        int count = -1;
                        if (useCachedList(null)) {
                            start = getStartParameter(request);
                            count = getCountParameter(request);
                        }
                        int totalCount = list.size();
                        if (useCachedList(focRequest) && builder.getObjectFilter() != null) {
                            totalCount = list.toJson_TotalCount(builder);
                        }
                        if (list.getFilter() != null && list.getFilter().getOffset() >= 0
                                && list.getFilter().getOffsetCount() >= 0) {
                            totalCount = requestTotalCount(list);
                        }

                        builder.setListStart(start);
                        builder.setListCount(count);
                        list.toJson(builder);
                        userJson = builder.toString();
                        // responseBody = "{ \"" + getNameInPlural() + "\":" + userJson + "}";
                        // add total if start or count is present in the request. If not paginated, no
                        // need to do a count query
                        responseBody = "{ \"data\":" + userJson + ", \"totalCount\":" + totalCount
                                + "}";
//							responseBody = "{ \"list\":" + userJson + ", \"totalCount\":"+totalCount+"}";
                    }

                    if (!useCachedList(null)) {
                        disposeFocList(focRequest, list);
                        list = null;
                    }

                    returnedStatus = HttpServletResponse.SC_OK;
                    response.setStatus(returnedStatus);

                    setCORS(response);
                    response.getWriter().println(responseBody);
                    String log = responseBody;
                    if (log.length() > 500)
                        log = log.substring(0, 499) + "...";
                    Globals.logString("  = Returned: " + log);
                } else {
                    returnedStatus = HttpServletResponse.SC_BAD_REQUEST;
                    response.setStatus(returnedStatus);

                    userJson = "{\"message\": \"Get not allowed by server\"}";
                    setCORS(response);
                    response.getWriter().println(userJson);
                }
                Globals.logString(" <= GET End " + focDesc.getName() + " " + returnedStatus);
            } catch (Exception e) {
                Globals.logException(e);
            } finally {
                focRequest.dispose();
            }

        } catch (Exception e) {
            Globals.logException(e);
            throw e;
        }
    }

    protected B01JsonBuilder newJsonBuilderForPostResponse() {
        B01JsonBuilder builder = new B01JsonBuilder();
        builder.setPrintForeignKeyFullObject(true);
        builder.setHideWorkflowFields(true);
        builder.setScanSubList(true);
        return builder;
    }

    protected FocObject newFocObject_POST(FocRestAPICall focRequest, long ref) {
        FocConstructor constr = new FocConstructor(focRequest.getFocDesc());
        FocObject focObj = (FocObject) constr.newItem();

        if (ref > 0) {
            focObj.setReference(ref);
            focObj.load();
        } else {
            focObj.setCreated(true);
            focObj.code_resetCode();
        }
        return focObj;
    }

    protected void disposeFocObject_POST(FocRestAPICall focRequest, FocObject focObj) {
        if (focObj != null) {
            focObj.dispose();
        }
    }

    protected String doPost_CheckError(FocRestAPICall focRequest, JSONObject jsonObj) {
        return null;
    }

    @PutMapping("obj/**")
    protected void doPut(HttpServletRequest request, HttpServletResponse response, @RequestBody String body)
            throws ServletException, IOException {
        doPostPut_Internal(request, response, body);
    }

    @PostMapping("obj/**")
    protected void doPost(HttpServletRequest request, HttpServletResponse response, @RequestBody String body)
            throws ServletException, IOException {
        doPostPut_Internal(request, response, body);
    }

    protected void doPostPut_Internal(HttpServletRequest request, HttpServletResponse response, @RequestBody String body)
            throws ServletException, IOException {
        String result = "";

        PathDetails details = getPathDetails(request);

        FocDesc focDesc = details.getFocDesc();
        int returnedStatus = HttpServletResponse.SC_NOT_IMPLEMENTED;

        FocRestAPICall focRequest = newFocRestAPICall(request, focDesc);

        String userJson = "";

        Globals.logString(" => POST Begin " + focDesc.getName());
        if (allowPost(focRequest) && (mobileModule_HasCreate(focRequest) || mobileModule_HasUpdate(focRequest))) {
            logRequestHeaders(request);

            if (body != null) {
                Globals.logString(" = Body: " + body);
            }

            B01JsonBuilder builder = newJsonBuilderForPostResponse();

            try {
                JSONObject jsonObj = new JSONObject(body);

                FocObject focObj = null;
                FocList list = null;

                String checkErrorJson = doPost_CheckError(focRequest, jsonObj);
                if (checkErrorJson == null) {
                    if (useCachedList(null)) {
                        list = newFocList(focRequest, false);
                        if (list != null) {
                            list.loadIfNotLoadedFromDB();
                            long ref = details.getId();
                            if (ref == 0) {
                                ref = doPost_GetReference(jsonObj, list);
                            }
                            if (ref > 0) {
                                focObj = (FocObject) list.searchByRealReferenceOnly(ref);
                            } else {
                                focObj = (FocObject) list.newEmptyItem();
                                focObj.code_resetCode();
                            }
                        }
                    } else {
                        long ref = doPost_GetReference(jsonObj, list);
                        focObj = newFocObject_POST(focRequest, ref);
                    }

                    if (focObj != null) {
                        fillFocObjectFromJson(focObj, jsonObj);

                        boolean created = focObj.isCreated();
                        if (created) {
                            if (focObj instanceof FocWorkflowObject) {
                                ((FocWorkflowObject) focObj).setSiteToAnyValueIfEmpty();
                            }
                        }

                        boolean errorSaving = false;

                        if (list != null) {
                            list.add(focObj);
                            errorSaving = !focObj.validate(true);
                            if (!errorSaving) {
                                errorSaving = !list.validate(true);
                            }
                        } else {
                            errorSaving = !focObj.validate(true);
                        }

                        if (errorSaving) {
                            userJson = "{\"message\": \"Could not save\"}";
                            returnedStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                            response.setStatus(returnedStatus);
                            setCORS(response);
                            response.getWriter().println(userJson);
                        } else {
                            // afterPost(focRequest, focObj, created);

                            userJson = toJsonDetails(focObj, builder);
                            // focObj.toJson(builder);
                            // userJson = builder.toString();
                            returnedStatus = HttpServletResponse.SC_OK;
                            response.setStatus(returnedStatus);

                            setCORS(response);
                            response.getWriter().println(userJson);
                        }

                        if (!useCachedList(null)) {
                            disposeFocObject_POST(focRequest, focObj);
                        }

                    } else {
                        userJson = "{\"message\": \" Does not exists \"}";
                        returnedStatus = HttpServletResponse.SC_BAD_REQUEST;
                        response.setStatus(returnedStatus);

                        setCORS(response);
                        response.getWriter().println(userJson);
                    }
                } else {
                    returnedStatus = HttpServletResponse.SC_BAD_REQUEST;
                    response.setStatus(returnedStatus);

                    userJson = checkErrorJson;
                    setCORS(response);
                    response.getWriter().println(userJson);
                }

            } catch (Exception e) {
                returnedStatus = HttpServletResponse.SC_BAD_REQUEST;
                response.setStatus(returnedStatus);

                userJson = "{\"message\": \"Bad Request\"}";
                Globals.logException(e);
                setCORS(response);
                response.getWriter().println(userJson);
            }

        } else {
            returnedStatus = HttpServletResponse.SC_BAD_REQUEST;
            response.setStatus(returnedStatus);

            userJson = "{\"message\": \"Post not allowed by server\"}";
            setCORS(response);
            response.getWriter().println(userJson);
        }
        Globals.logString("  = Returned: " + userJson);
        Globals.logString(" <= POST End " + focDesc.getName() + " " + returnedStatus);

    }

//	@Override
//	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		Globals.logString(" => OPTIONS Begin " + getNameInPlural());
//		if (allowOptions(null)) {
//			super.doOptions(request, response);
//			// The following are CORS headers. Max age informs the
//			// browser to keep the results of this call for 1 day.
//			setCORS(response);
//		}
//		Globals.logString(" <= OPTIONS End " + getNameInPlural());
//	}

    @DeleteMapping("obj/**")
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FocDesc focDesc = getFocDescFromPath(request);
        Globals.logString(" => DELETE Begin " + focDesc.getName());

        int returnedStatus = HttpServletResponse.SC_NOT_IMPLEMENTED;

        String userJson = "";

        B01JsonBuilder builder = new B01JsonBuilder();
        builder.setPrintForeignKeyFullObject(true);
        builder.setHideWorkflowFields(true);

        FocRestAPICall focRequest = newFocRestAPICall(request, focDesc);
        if (allowDelete(null) && !mobileModule_HasDelete(focRequest)) {
            returnedStatus = HttpServletResponse.SC_BAD_REQUEST;
            response.setStatus(returnedStatus);
            userJson = "{\"message\": \"Delete not allowed by server\"}";
            setCORS(response);
            response.getWriter().println(userJson);
        } else {
            FocList list = focDesc.getFocList(FocList.LOAD_IF_NEEDED);

            long ref = doGet_GetReference(request, list);
            FocObject focObj = null;
            if (ref > 0) {
                focObj = list.searchByRealReferenceOnly(ref);
                if (focObj != null) {
                    focObj.toJson(builder);
                    userJson = builder.toString();

                    focObj.setDeleted(true);
                    focObj.validate(true);
                    list.validate(true);
                } else {
                    userJson = "{\"message\": \" Does not exists \"}";
                }

                returnedStatus = HttpServletResponse.SC_OK;
                response.setStatus(returnedStatus);
            }
        }
        setCORS(response);
        response.getWriter().println(userJson);

        Globals.logString("  = Returned: " + userJson);
        Globals.logString(" <= DELETE End " + focDesc.getName());
    }

    public void postSlaveList(FocObject focObject, FocList list, JSONObject jsonObj,
                              ICopyFromJsonToSlave copyFromJsonToSlave) throws Exception {
        if (list != null && jsonObj != null && copyFromJsonToSlave != null) {
            HashMap<Long, FocObject> toDelete = new HashMap<Long, FocObject>();
            for (int i = 0; i < list.size(); i++) {
                FocObject slaveObj = (FocObject) list.getFocObject(i);
                toDelete.put(slaveObj.getReferenceInt(), slaveObj);
            }

            String tableName = list.getFocDesc().getStorageName();
            String listFieldName = tableName + "_LIST";
            if (list != null && jsonObj.has(listFieldName)) {
                JSONArray jsonArray = jsonObj.getJSONArray(listFieldName);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject slaveJson = (JSONObject) jsonArray.get(i);
                        FocObject slaveObj = null;
                        if (slaveJson.has(FField.REF_FIELD_NAME)) {
                            // long ref = slaveJson.getLong(FField.REF_FIELD_NAME);
                            String strValue = slaveJson.getString(FField.REF_FIELD_NAME);
                            long ref = Utils.parseLong(strValue, 0);
                            if (ref > 0) {
                                slaveObj = list.searchByRealReferenceOnly(ref);
                                if (slaveObj != null) {
                                    toDelete.remove(slaveObj.getReferenceInt());
                                }
                            }
                        }

                        if (slaveObj == null) {
                            slaveObj = list.newEmptyItem();
                            slaveObj.setCreated(true);
                        }

                        copyFromJsonToSlave.copyJsonToObject(slaveObj, slaveJson);

                        slaveObj.validate(false);
                    }
                }
            }

            Iterator<FocObject> iter = toDelete.values().iterator();
            while (iter != null && iter.hasNext()) {
                FocObject slaveObj = iter.next();
                slaveObj.setDeleted(true);
                list.remove(slaveObj);
                slaveObj.validate(false);
            }
        }

    }

    public interface ICopyFromJsonToSlave {
        public void copyJsonToObject(FocObject slaveObj, JSONObject slaveJson);
    }

    private class PathDetails {
        private String resourceName;
        private FocDesc focDesc;
        private long id;
        private String action;

        public FocDesc getFocDesc() {
            return focDesc;
        }

        public void setFocDesc(FocDesc focDesc) {
            this.focDesc = focDesc;
        }

        public String getResourceName() {
            return resourceName;
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }
}
