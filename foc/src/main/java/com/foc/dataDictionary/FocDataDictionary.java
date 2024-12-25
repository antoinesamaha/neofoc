/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.dataDictionary;

import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.access.FocDataConstant;
import com.foc.access.FocDataMap;
import com.foc.admin.FocUser;
import com.foc.business.company.Company;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FNumField;
import com.foc.formula.FocSimpleFormulaContext;
import com.foc.formula.Formula;
import com.foc.list.FocList;
import com.foc.property.FDate;
import com.foc.property.FMultipleChoice;
import com.foc.property.FProperty;
import com.foc.shared.dataStore.IFocData;
import com.foc.shared.dataStore.IFocDataDictionary;
import com.foc.util.ASCII;
import com.foc.util.EnglishNumberToWords;
import com.foc.util.FocMath;
import com.foc.util.expression.FocExpression;
import com.foc.util.expression.IExpressionHandler;

public class FocDataDictionary implements IFocDataDictionary {

	protected HashMap<String, IFocDataResolver> dataMap = null;
	private boolean localDataDictionary = false;
	
	public FocDataDictionary(){
		dataMap = new HashMap<String, IFocDataResolver>();

		putParameter("URL", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				return Globals.getApp() != null ? Globals.getApp().getURL() : "";
			}
		});

		putParameter("WHITE_DOT", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				return "<font color=\"white\">.</font>";
			}
		});

		putParameter("LT", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				return "<";
			}
		});
		
		putParameter("GT", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				return ">";
			}
		});
		
		putParameter("B", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				return "<b>";
			}
		});
		
		putParameter("/B", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				return "</b>";
			}
		});

		putParameter("U", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				return "<u>";
			}
		});
		
		putParameter("/U", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				return "</u>";
			}
		});

		putParameter("I", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				return "<i>";
			}
		});
		
		putParameter("/I", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				return "</i>";
			}
		});

    putParameter("WORDING", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				String value = "";

				if(focData != null && arguments != null && arguments.size() == 1){
					String dataPath = arguments.get(0);
					
					FProperty prop = (FProperty) focData.iFocData_getDataByPath(dataPath);
					if(prop != null){
						double total = prop.getDouble();
						
						if(total < 0) total *= -1;
						
						double dollars = (double)FocMath.floor(total, 1) ;
//						double val = FocMath.ceil(total-dollars,0.01); //Hussein... i added an intermediate variable (val) because without it there was a problem with the variable being retuned
						double val = total*100-dollars*100+0.00001;
						int cent = (int)FocMath.floor(val, 1);
						
						StringBuffer buff = new StringBuffer();
						String dollarsStr = EnglishNumberToWords.convert( (int) dollars );
						dollarsStr = ASCII.convertAllWordBeginningToCapital(dollarsStr);
						buff.append(dollarsStr);
						if(cent > 0){
							buff.append(" and ");
							dollarsStr = EnglishNumberToWords.convert( cent );
							dollarsStr = ASCII.convertAllWordBeginningToCapital(dollarsStr);
							buff.append(dollarsStr);
							buff.append(" cents");
						}
						value = buff.toString();
					}
				}
				
				return value;
			}
		});

    putParameter("TO_ARABIC", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				String value = "";

				try{
					if(focData != null && arguments != null && arguments.size() == 1){
						String dataPath = arguments.get(0);
						
						FProperty prop = (FProperty) focData.iFocData_getDataByPath(dataPath);
						if(prop != null){
							String toConvert = prop.getString();

							char[] arabicChars = { '٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩' };

							StringBuilder builder = new StringBuilder();

							for(int i = 0; i < toConvert.length(); i++){
								char c = toConvert.charAt(i);
								if(Character.isDigit(c)){
									if(((int) (c) - 48) >= 0 && ((int) (c) - 48) <= 9){
										builder.append(arabicChars[(int) (c) - 48]);
									}else{
										builder.append(c);
									}
								}else{
									if(c == '('){
										builder.append(')');
									}else if(c == ')'){
										builder.append('(');
									}else{
										builder.append(c);
									}
								}
							}

							value = builder.toString();							
						}
					}
				}catch(Exception e){
					Globals.logException(e);
				}
				
				return value;
			}
		});
    
		putParameter("FORMAT_DATE_TO_ARABIC", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				String value = "";
				try {
					if (focData != null && arguments != null && arguments.size() == 1) {
						String dataPath = arguments.get(0);
						FDate prop = (FDate) focData.iFocData_getDataByPath(dataPath);
						if (prop != null) {
							Date date = (Date) prop.getDate();							
							SimpleDateFormat RTL_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
							String rtl = "\u200F";
							String ltre = "\u202A";
							String df = "\u202C";							
							value = rtl + ltre + RTL_DATE_FORMAT.format(date) + df + rtl;
						}
					}
				} catch (Exception e) {
					Globals.logException(e);
				}
				return value;
			}
		});
    
    putParameter("FORMAT_DATE", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				String value = "";

				try{
					if(focData != null && arguments != null && arguments.size() == 2){
						String dataPath = arguments.get(0);
						
						FDate prop = (FDate) focData.iFocData_getDataByPath(dataPath);
						if(prop != null){
							Date date = (Date) prop.getDate();
							
							String format = arguments.get(1);
							
							SimpleDateFormat sdf = new SimpleDateFormat(format);
							value = sdf.format(date);
						}
					}
				}catch(Exception e){
					Globals.logException(e);
				}
				
				return value;
			}
		});

    putParameter("FORMAT", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
    		String value = "";

    		if(focData != null && arguments != null && arguments.size() == 3){
    			double total = 0;
    			try{
    				total = Double.valueOf(arguments.get(0));
    			}catch(Exception e){
      			String dataPath = arguments.get(0);
      			FProperty prop = (FProperty) focData.iFocData_getDataByPath(dataPath);
      			if(prop != null){
      				total = prop.getDouble();
      			}
    			}

    			String precisionString = arguments.get(1);

    			double precision = 1;
    			try{
    				precision = Double.parseDouble(precisionString);
    			}catch(NumberFormatException e){
    				Globals.showNotification("String " + precisionString + " could not be parsed to a real number.", "Defaulting to a precision of: " + precision, IFocEnvironment.TYPE_ERROR_MESSAGE);
    			}
    			
    			boolean grouping = arguments.get(2) != null && (arguments.get(2).equals("1") || arguments.get(2).toLowerCase().equals("true")) ? true : false;
    			NumberFormat format = FNumField.newNumberFormat(30, (int)precision, grouping);
    			value = format.format(total);
    	  }
    	  return value;
			}
		});

    putParameter("SYSTEM_DATE", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				
				String dateStr = FDate.getDateFormat().format(Globals.getApp().getSystemDate());
				if(arguments != null && !arguments.isEmpty() && arguments.size() > 0){
					try{
						String dateFormat = arguments.get(0);
						if(dateFormat != null && !dateFormat.isEmpty()){
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
							dateStr = simpleDateFormat.format(Globals.getApp().getSystemDate());
						}
					}catch(Exception e){
						Globals.logException(e);
					}
				}
				return dateStr;
			}
		});

    putParameter("CURRENT_USER", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				FocUser comp = null;
				if(Globals.getApp() != null && Globals.getApp().getUser_ForThisSession() != null){
					comp = Globals.getApp().getUser_ForThisSession();
				}
				return comp;
			}
		});
    
    putParameter("CURRENT_COMPANY", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				Company comp = null;
				if(Globals.getApp() != null && Globals.getApp().getUser_ForThisSession() != null){
					comp = Globals.getApp().getUser_ForThisSession().getCurrentCompany();
				}
				return comp;
			}
		});//$P{EVAL(CURRENT_COMPANY,NAME)}

    putParameter("EVAL", new IFocDataResolver() {
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				String value = "";

				if(arguments != null){
					
					FocObject originalObject = null;
					if(focData != null){
						if(focData instanceof FocObject){
							originalObject = (FocObject) focData;
						}else if(focData instanceof FocDataMap){
							Object obj = ((FocDataMap) focData).getMainFocData();
							if(obj instanceof FocObject){
								originalObject = (FocObject) obj;
							}
						}
					}

					String formulaExpression = null;
					
					if(arguments.size() == 2){
						String dataPath = arguments.get(0);
						IFocData data = focData.iFocData_getDataByPath(dataPath);
						if(data instanceof FocObject){
							originalObject = (FocObject) data;
						}
						formulaExpression = arguments.get(1);
					}else if(arguments.size() == 1){
						formulaExpression = arguments.get(0);
					}

					FocSimpleFormulaContext formulaContext = new FocSimpleFormulaContext(new Formula(formulaExpression));
					Object result = formulaContext.compute(originalObject);
					
					//This is to log the Formula evaluation
					/*
					if(formulaContext.getFormula() != null && formulaContext.getFormula().getFormulaTree() != null){
						formulaContext.getFormula().getFormulaTree().printDebug();
					}
					*/
					
					value = result != null ? result.toString() : "";
				}
				
				return value;
			}
		});
    
    putParameter("MULTIPLE_CHOICE_LABEL", new IFocDataResolver() {

    	//$P{MULTIPLE_CHOICE_LABEL(STORAGE_NAME, MULTIPLECHOICE_FIELD_NAME)} PARTY,TYPE
			@Override
			public Object getValue(IFocData focData, ArrayList<String> arguments) {
				String storageName    = arguments.size() > 0 ? arguments.get(0) : null;
				String multiFieldName = arguments.size() > 1 ? arguments.get(1) : null;
				String value = null;

				if(storageName != null && multiFieldName != null){
					FocDesc focDesc = Globals.getApp().getFocDescByName(storageName);
					
					if(focDesc != null){
						FField field = focDesc.getFieldByName(multiFieldName);
						if(field != null && focData instanceof FocObject){
							FProperty property = ((FocObject) focData).getFocProperty(field.getID());
							value = property != null ? property.getValue()+"" : "";
						}
					}
				}else if(storageName != null){
					//In This Case we only have the dataPath
					String dataPath = storageName;
					IFocData propFocData = ((FocObject) focData).iFocData_getDataByPath(dataPath);
					if(propFocData instanceof FMultipleChoice){
						value = ((FMultipleChoice) propFocData).getString();
					}
				}
				return value;
			}
    });
    
    putParameter("ROUND", new IFocDataResolver() {
    	public Object getValue(IFocData focData, ArrayList<String> arguments) {
    		String value = "";

    		if(focData != null && arguments != null && arguments.size() == 2){
    			double total = 0;
    			try{
    				total = Double.valueOf(arguments.get(0));
    			}catch(Exception e){
      			String dataPath = arguments.get(0);
      			FProperty prop = (FProperty) focData.iFocData_getDataByPath(dataPath);
      			total = prop.getDouble();    				
    			}

    			String precisionString = arguments.get(1);

    			double precision = 1;

    			try{
    				precision = Double.parseDouble(precisionString);
    			}
    			catch(NumberFormatException e){
    				Globals.showNotification("String " + precisionString + " could not be parsed to a real number.", "Defaulting to a precision of: " + precision, IFocEnvironment.TYPE_ERROR_MESSAGE);
    			}
    			value = Double.toString(FocMath.round(total, precision));
    		}

    		return value;
    	}
    });
    
    putParameter("DB_RESIDENT", new IFocDataResolver() {
    	public Object getValue(IFocData focData, ArrayList<String> arguments) {
    		String value = "false";
    		
    		if(focData instanceof FocDataMap) focData = ((FocDataMap)focData).getMainFocData();
    		
    		if(focData instanceof FocObject) {
    			if(((FocObject)focData).isDbResident() && ((FocObject)focData).getThisFocDesc().isDbResident()){
    				value = "true";
    			}
    		}else if(focData instanceof FocList) {
    			if(((FocList)focData).isDbResident() && ((FocList)focData).getFocDesc().isDbResident()) {
    				value = "true";
    			}
    		}
    		
    		return value;
    	}
    });
	}
	
	public void dispose(){
		if(dataMap != null){
			dataMap.clear();
			dataMap = null;
		}
	}
	
	public void putParameter(String key, IFocDataResolver dataFetcher){
		if(key != null && dataFetcher != null && dataMap != null){
			dataMap.put(key, dataFetcher);
		}
	}

	public IFocDataResolver getParameter(String key){
	  IFocDataResolver resolver = null;
	  if(key != null && dataMap != null){
	    resolver = dataMap.get(key);
	  }
	  return resolver;
	}

	private Object getValue_WithTableDisplayObjectCall(IFocData focData, String dataPath){
    Object value = null;
    if(focData != null && dataPath != null){
	    value = focData.iFocData_getDataByPath(dataPath);
	    if(value instanceof FProperty){
	      //value = ((FProperty) value).vaadin_TableDisplayObject(null, null);
	    	value = ((FProperty) value).getTableDisplayObject();
	    }else if(value instanceof FocDataConstant){
	    	FocDataConstant focDataConstant = (FocDataConstant) value;
	    	value = focDataConstant.iFocData_getValue();
	    }
    }
		return value;
	}
	
	public Object getValue(IFocData focData, String key, ArrayList<String> arguments){
		Object value = null; 
		if(dataMap != null && key != null){
			
			//20150928 - If the Key has a 'dot' this means the first part is a parameter like 'CURRENT_COMPANY' or 'CURRENT_USER' and the part after dot is the DataPath 
			String pathPart  = null;
			int dotIndex = key.indexOf('.');
			if(dotIndex > 0){
				pathPart  = key.substring(dotIndex+1);				
				key = key.substring(0, dotIndex);
			}
			//20150928-End
			
			IFocDataResolver fethcer = dataMap.get(key);
			if(fethcer != null){
				try {
					value = fethcer.getValue(focData, arguments);
					
					if(pathPart != null && value != null && value instanceof IFocData){
						IFocData newFocData = (IFocData) value;
						
						value = getValue_WithTableDisplayObjectCall(newFocData, pathPart);
					}
				}catch(Exception e) {
					if(key != null) {
						Globals.logString("Exception while fetching key : "+key+" !");
					}else {
						Globals.logString("Exception while fetching NULL Key!");
					}
					Globals.logException(e);
				}
			}else{
				if(!isLocalDataDictionary()){
					Globals.logString("Data resolver Not found for key : "+key);
					Globals.showNotification("Data Resolver Not Found", "for key = "+key, IFocEnvironment.TYPE_WARNING_MESSAGE);
				}
			}
		}
		return value;
	}
	
	@Override
	public synchronized String resolveExpression(IFocData focData, String expression, boolean replaceUnknownWithEmptyString){
	  String exp = expression;
	  
	  exp = FocExpression.parseExpression(expression, new ExpressionHandler(focData, replaceUnknownWithEmptyString));
	  
		return exp;
	}

	private class ExpressionHandler implements IExpressionHandler {
	  
	  private IFocData focData = null;
	  private boolean  replaceWithEmptyStringWhenUnknown = false;
	  
		public ExpressionHandler(IFocData focData, boolean replaceWithEmptyStringWhenUnknown){
	    this.focData = focData;
	    this.replaceWithEmptyStringWhenUnknown = replaceWithEmptyStringWhenUnknown;
	  }
	  
	  @Override
    public String handleFieldOrParameter(String expression, char type, int startIndex, int endIndex, String fieldOfParameter, ArrayList<String> args) {
      String keyReplacement = null;

      try{
	      Object value = null;
	      if(type == 'P'){
	        value = getValue(focData, fieldOfParameter, args);
	      }else if(focData != null){
	      	value = getValue_WithTableDisplayObjectCall(focData, fieldOfParameter);
	      }
	      if(value instanceof String){
	        keyReplacement = (String) value;
	      }else if(value instanceof Double){
	      	keyReplacement = ((Double) value).toString();
	      }
      }catch(Exception e){
      	Globals.logException(e);
      }
      
      if(keyReplacement == null && isReplaceWithEmptyStringWhenUnknown()) keyReplacement = "";
      
      return keyReplacement;
    }
	  
	  public boolean isReplaceWithEmptyStringWhenUnknown() {
			return replaceWithEmptyStringWhenUnknown;
		}
	}
	
  public ArrayList<String> getFieldsUsed(String expression){
    FieldsUsedFinder finder = new FieldsUsedFinder();
    FocExpression.parseExpression(expression, finder);
    return finder.getFieldsArray();
  }
  
  public Iterator<String> getKeySet(){
  	return dataMap != null ? dataMap.keySet().iterator() : null;
  }
  
  public void copy(FocDataDictionary src){
  	Iterator<String> iter = src.getKeySet();
  	if(iter != null){
  		while(iter.hasNext()){
  			String key = iter.next();
  			putParameter(key, src.getParameter(key));
  		}
  	}
  }
  
  private class FieldsUsedFinder implements IExpressionHandler {
    private ArrayList<String> fieldsArray = null;
    
    @Override
    public String handleFieldOrParameter(String expression, char type, int startIndex, int endIndex, String fieldOfParameter, ArrayList<String> args) {
      if(type == 'F'){
        if(fieldsArray == null){
          fieldsArray = new ArrayList<String>();
        }
        fieldsArray.add(fieldOfParameter);
      }
      return null;
    }

    public ArrayList<String> getFieldsArray() {
      return fieldsArray;
    }
    
  }
	
	// ----------------------------------------------
	// ----------------------------------------------
	// Static
  // ----------------------------------------------
  // ----------------------------------------------
	
	private static FocDataDictionary instance = null;
	
	public static FocDataDictionary getInstance(){
		if(instance == null){
			instance = new FocDataDictionary();
		}
		return instance;
	}

	public boolean isLocalDataDictionary() {
		return localDataDictionary;
	}

	public void setLocalDataDictionary(boolean localDataDictionary) {
		this.localDataDictionary = localDataDictionary;
	}
}
