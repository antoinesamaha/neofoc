/*
 * Created on Aug 2009 By Antoine SAMAHA
 */
package com.foc.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FNumField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FReferenceField;
import com.foc.desc.field.FStringField;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class FocListGroupBy {
	private static final String LISTAGG_SEPARATOR = "|";
	private static final String LISTAGG_SUB_SEPARATOR = "~";
	private static final String COUNT_DISTINCT = "COUNT_DISTINCT(";
	private static final String COUNT_DISTINCT_QUERY = "COUNT(DISTINCT ";
	
	private ArrayList<String>                     arrayOfAtomicExpressions = null;
	private String                                groupByExpression        = null;
	private HashMap<Integer, GroupByFieldFormula> fieldsInFormulas         = null;
	
	public FocListGroupBy(){
		fieldsInFormulas = new HashMap<Integer, GroupByFieldFormula>();
	}
	
	public void dispose(){
		if(fieldsInFormulas != null){
			Iterator<GroupByFieldFormula> iter = fieldsInFormulas.values().iterator();
			while(iter != null && iter.hasNext()){
				GroupByFieldFormula frml = iter.next();
				frml.dispose();
			}
			fieldsInFormulas.clear();
		}
		groupByExpression = null;
		if(arrayOfAtomicExpressions != null){
			arrayOfAtomicExpressions.clear();
			arrayOfAtomicExpressions = null;
		}
	}

	public ArrayList<String> getArrayOfAtomicExpressions(boolean create){
		if(arrayOfAtomicExpressions == null && create){
			arrayOfAtomicExpressions = new ArrayList<String>(); 
		}
		return arrayOfAtomicExpressions;
	}
	
	public void addAtomicExpression(String atomic){
		getArrayOfAtomicExpressions(true).add(atomic);
	}
	
	private void buildExpressionFromAtomicArrayOfExpressions(FocDesc focDesc){
		ArrayList<String> arr = getArrayOfAtomicExpressions(false);
		
		StringBuffer buff = new StringBuffer();
		if(arr.size() == 1){
			String fName = focDesc != null ? FField.adaptFieldNameToProvider(focDesc.getProvider(), arr.get(0)) : arr.get(0);
			buff.append(fName);
		}else if(arr.size() > 1){
			buff.append("concat(");
			for(int i=0; i<arr.size(); i++){
				if(i>0) buff.append(",'|',");
				String fName = focDesc != null ? FField.adaptFieldNameToProvider(focDesc.getProvider(), arr.get(i)) : arr.get(i);
				buff.append(fName);
			}
			buff.append(")");
		}
		setGroupByExpression(buff.toString());
	}
	
	public void addField_Formulas(int fieldName, String formulaFullExpression){
		GroupByFieldFormula groupByFormula = new GroupByFieldFormula(formulaFullExpression); 
		fieldsInFormulas.put(fieldName, groupByFormula);
	}

//	public void addField_FormulaSingleText(int fieldID, String fieldName, String formula){
//		addField_FormulaSingleText(null, fieldID, fieldName, formula);
//	}
	
	private String addAConcat(String a, String b) {
		return "CONCAT("+a+","+b+")";
	}
	
	private String getConcatsFromConcatenationFields(String concatenationFields) {
		String concats = "";
		String[] parts = concatenationFields.split(LISTAGG_SUB_SEPARATOR);
		for(int p=parts.length-1; p>=0; p--) {
			String subfield = DBManager.provider_ConvertFieldName(Globals.getDBManager().getProvider(), parts[p]);

			if(!Utils.isStringEmpty(concats)) {
				concats = addAConcat(subfield, concats);
			} else {
				concats = subfield;
			}
			concats = addAConcat("'~'", concats);
		}
		return concats;
	}
	
	public FField addField_FormulaSingleText(FocDesc focDesc, FField field, String formula, String concatenationFields){
		int fieldID = field.getID();
		String fieldName = field.getDBName(); 
		
		if(formula.toUpperCase().startsWith("LISTAGG")) {
			fieldName = DBManager.provider_ConvertFieldName(Globals.getDBManager().getProvider(), fieldName);
			
			String formulaBefore = "";
			String formulaAfter  = "";
			
			if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE) {
				if(ConfigInfo.isOracleListAggCLOB() && focDesc.isUseOracleListAggCLOB()) {
					formulaBefore = "LISTAGG_CLOB(";
					formulaAfter  = ")";
					
					if(!Utils.isStringEmpty(concatenationFields)) {
						formulaBefore += "CONCAT(";
						String concats = getConcatsFromConcatenationFields(concatenationFields);
						formulaAfter = "," + concats + "))"; 
					}
					
					addField_Formulas(fieldID, formulaBefore, formulaAfter);
				} else {
					formulaBefore = "LISTAGG(";
					formulaAfter  = ", '"+LISTAGG_SEPARATOR+"') WITHIN GROUP (ORDER BY "+fieldName+")";
					
	//				formulaBefore = "RTRIM(XMLAGG(XMLELEMENT(E,";
	//			  formulaAfter  = ",'"+LISTAGG_SEPARATOR+"').EXTRACT('//text()') ORDER BY "+fieldName+").GetClobVal(),',') AS LIST";
	
					if(!Utils.isStringEmpty(concatenationFields)) {
						formulaBefore += "CONCAT(";
						String concats = getConcatsFromConcatenationFields(concatenationFields);
						formulaAfter = "," + concats + ")" + formulaAfter; 
					}
					
					addField_Formulas(fieldID, formulaBefore, formulaAfter);
				}
			} else if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_POSTGRES) {
				formulaBefore = "STRING_AGG(";
				formulaAfter  = "::character varying, '"+LISTAGG_SEPARATOR+"' ORDER BY "+fieldName+")";

				/*
				if(!Utils.isStringEmpty(concatenationFields)) {
					String concats = "";
					formulaBefore += "CONCAT(";
					String[] parts = concatenationFields.split(LISTAGG_SUB_SEPARATOR);
					for(int p=parts.length-1; p>=0; p--) {
						String subfield = DBManager.provider_ConvertFieldName(Globals.getDBManager().getProvider(), parts[p]);

						if(!Utils.isStringEmpty(concats)) {
							concats = addAConcat(subfield, concats);
						} else {
							concats = subfield;
						}
						concats = addAConcat("'~'", concats);
					}
					formulaAfter = "," + concats + ")" + formulaAfter; 
				}
				*/
				
				addField_Formulas(fieldID, formulaBefore, formulaAfter);
			} else if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_MSSQL) {
//			,STUFF((SELECT ',' + Number FROM ContactPhones FOR XML PATH ('')), 1, 1, '')
				FField originalField = field;
				if(field.getJoinOriginalField() != null) originalField = field.getJoinOriginalField();
				if(originalField != null && originalField.getFocDescParent() != null && !Utils.isStringEmpty(originalField.getFocDescParent().getStorageName_ForSQL())) {
					fieldName = originalField.getDBName();
					formulaBefore = "STUFF((SELECT ',' + ";
					formulaAfter  = " FROM "+originalField.getFocDescParent().getStorageName_ForSQL()+" FOR XML PATH ('')), 1, 1, '')";
					addField_Formulas(fieldID, formulaBefore + fieldName + formulaAfter);
	  		}
			}
			
			if(focDesc != null) {
				FocList selectionList = null;
				String captionProperty = null;
				
				if(    field instanceof FNumField       || field instanceof FIntField 
						|| field instanceof FReferenceField || field instanceof FObjectField) {
					//AggList will not work if we do not put the result in a string even if the initial field is not a string
					boolean wasAlreadyAddedToDesc = focDesc.getFieldByID(field.getID()) != null; 
					if(wasAlreadyAddedToDesc) focDesc.removeField(field);
					FStringField newFld = new FStringField(field.getDBName(), field.getTitle(), field.getID(), false, 1000);
					if(wasAlreadyAddedToDesc) focDesc.addField(newFld);
					if(field instanceof FObjectField && formula.toUpperCase().length() > 8) {
						selectionList = ((FObjectField)field).getSelectionList();
						captionProperty = formula.substring(8);
					}
					field = newFld;
				}else {
					//The Agglist needs more size to concat the different values
					field.setSize(field.getSize() * 20);
				}
				
				field.addListener(new ListAggListener(selectionList, captionProperty));
			}
		}else if(   formula.toUpperCase().startsWith("MAX")) {
			if(			focDesc.getProvider() == DBManager.PROVIDER_ORACLE
					&& 	field instanceof FStringField
					&&  ((FStringField)field).isClob()
					) {
				addField_Formulas(fieldID, "MAX(TO_CHAR(", "))");
			} else if(    focDesc.getProvider() == DBManager.PROVIDER_MSSQL
				         && field instanceof FBoolField){
				String formulaBefore = "MAX(CAST(";
				String formulaAfter  = " AS tinyint))";
	      addField_Formulas(fieldID, formulaBefore, formulaAfter);
			} else {
				addField_Formulas(fieldID, formula+"(", ")");
			}
		}else{
			addField_Formulas(fieldID, formula+"(", ")");
		}
		return field;
	}
	
	public void addField_Formulas(int fieldName, String formulaPartBefore, String formulaPartAfter){
		GroupByFieldFormula groupByFormula = new GroupByFieldFormula(formulaPartBefore, formulaPartAfter); 
		fieldsInFormulas.put(fieldName, groupByFormula);
	}
	
	/*
	public void addField_Formulas(String alias, FocDesc focDesc, int fieldID, String formulaPartBefore, String formulaPartAfter){
		FField fld = focDesc.getFieldByID(fieldID);
		
		addField_Formulas(alias+"."+fld.getName(), formulaPartBefore, formulaPartAfter);
	}
	*/

	public String getGroupByExpression(FocDesc focDesc) {
		if(groupByExpression == null || groupByExpression.isEmpty()){
			buildExpressionFromAtomicArrayOfExpressions(focDesc);
		}
		return groupByExpression;
	}

	public void setGroupByExpression(String groupByExpression) {
		this.groupByExpression = groupByExpression;
	}

	public String addFormulaToFieldName(int key, String str){
		String ret = str;
		GroupByFieldFormula formula = fieldsInFormulas.get(key);
		if(formula != null){
			ret = formula.composeFormulaForFieldName(str);
		}
		return ret;
	}
	
	private class GroupByFieldFormula {
		private String     formulaBefore         = null;
		private String     formulaAfter          = null;
		private String     formulaFullExpression = null;
		
		private GroupByFieldFormula(String formulaFullExpression){
			this.formulaFullExpression = formulaFullExpression;
		}
		
		private GroupByFieldFormula(String formulaBefore, String formulaAfter){
			this.formulaBefore = formulaBefore;
			this.formulaAfter  = formulaAfter;
		}
		
		public void dispose(){
			formulaBefore         = null;
			formulaAfter          = null;
			formulaFullExpression = null;
		}

		public String composeFormulaForFieldName(String str){
			String ret = null;
			if(formulaFullExpression != null){
				ret = formulaFullExpression;
			}else{
				if (formulaBefore.equals(COUNT_DISTINCT)){
					formulaBefore = COUNT_DISTINCT_QUERY;
				}
				ret = formulaBefore + str + formulaAfter;
			}
			return ret;
		}
	}
	
	private class ListAggListener implements FPropertyListener {
		
		protected FocList list = null;
		protected String  captionProperty = null;
		
		//For all other cases than object fields
		public ListAggListener() {
			
		}
		
		//For Object fields only
		public ListAggListener(FocList list, String  captionProperty) {
			this.list = list;
			this.captionProperty = captionProperty;
		}
		
		@Override
		public void propertyModified(FProperty property) {
			if(property.isLastModifiedBySetSQLString() && property.getString() != null) {
				ArrayList valuesArray = null;
				String result = "";
				StringTokenizer tokzer = new StringTokenizer(property.getString(), LISTAGG_SEPARATOR, false);
				while(tokzer.hasMoreTokens()) {
					String token = tokzer.nextToken();
					if(!Utils.isStringEmpty(token)) {
						token = token.trim();
						try {
							//if Object I want to convert to String
							if(list != null && !Utils.isStringEmpty(captionProperty)) {
								long ref = Long.valueOf(token);
								if(ref == 0) {
									token = "";
								} else {
									FocObject focObjectValue = list.searchByReference(ref);
									FProperty prop = focObjectValue != null ? focObjectValue.getFocPropertyForPath(captionProperty) : null;
									token = prop != null ? prop.getString() : "";
								}
							}
							//----------------

							if(!Utils.isStringEmpty(token)) {
								if(valuesArray == null) valuesArray = new ArrayList();
								if(!valuesArray.contains(token)) {
									valuesArray.add(token);
									
									if(!result.isEmpty()) result += ", ";
									result += token;
								}											
							}
						}catch(Exception e) {
							Globals.logExceptionWithoutPopup(e);
						}
					}
				}
				property.setLastModifiedBySetSQLString(false);
				property.setString(result);
			}
		}
			
		@Override
		public void dispose() {
		}
	}
}