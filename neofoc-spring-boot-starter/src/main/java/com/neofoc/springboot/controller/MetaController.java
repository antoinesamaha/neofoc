package com.neofoc.springboot.controller;

import com.foc.Globals;
import com.foc.admin.GrpMobileModuleRights;
import com.foc.business.workflow.implementation.FocWorkflowObject;
import com.foc.controller.FocRestAPICall;
import com.foc.desc.*;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.shared.json.B01JsonBuilder;
import com.foc.util.Utils;
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

import java.util.Iterator;

@RestController
@RequestMapping("meta")
public class MetaController {

    @GetMapping("entities")
    public ResponseEntity<String> entities(HttpServletRequest request, @RequestParam(value = "module", required = false) String module, @RequestParam(value = "entity", required = false) String entity) {
        FocDescMap focDescMap = Globals.getApp().getFocDescMap();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonDataArray = new JSONArray();

        for (String key : focDescMap.keySet()) {
            FocDesc focDesc = focDescMap.get(key);
            if (module != null && focDesc.getModule() != null && !focDesc.getModule().getName().equals(module)) {
                continue;
            }
            if (entity != null && focDesc.getName() != null && !focDesc.getName().equals(entity)) {
                continue;
            }
            JSONObject focDescJson = new JSONObject();
            focDescJson.put("name", focDesc.getName());
            focDescJson.put("storageName", focDesc.getStorageName());
            focDescJson.put("isListInCache", focDesc.isListInCache());
            if (focDesc.getModule() != null) {
                focDescJson.put("module", focDesc.getModule().getName());
            }

            JSONArray jsonFieldArray = new JSONArray();
            for (int i=0; i<focDesc.getFieldsSize(); i++) {
                FField field = focDesc.getFieldAt(i);
                JSONObject fieldJson = new JSONObject();
                fieldJson.put("name", field.getName());
                fieldJson.put("dbName", field.getDBName());
                fieldJson.put("sqlType", field.getSqlType());
                fieldJson.put("type", field.getFabType());
                jsonFieldArray.put(fieldJson);
            }
            focDescJson.put("fields", jsonFieldArray);

            jsonDataArray.put(focDescJson);
        }
        jsonObject.put("data", jsonDataArray);
        jsonObject.put("count", jsonDataArray.length());
        return ResponseEntity.ok().body(jsonObject.toString());
    }

    @GetMapping("modules")
    public ResponseEntity<String> modules(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonDataArray = new JSONArray();

        Iterator<FocModule> iter = Globals.getApp().modules_Iterator();
        while (iter != null && iter.hasNext()) {
            FocModule focModule = iter.next();

            JSONObject focDescJson = new JSONObject();
            focDescJson.put("module", focModule.getName());

            jsonDataArray.put(focDescJson);
        }

        jsonObject.put("data", jsonDataArray);
        jsonObject.put("count", jsonDataArray.length());
        return ResponseEntity.ok().body(jsonObject.toString());
    }
}
