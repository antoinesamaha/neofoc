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
package com.foc.desc.parsers.xml;

import com.foc.desc.parsers.fields.IFocFieldType;

public interface FXMLDesc {
  public static final String TAG_TABLE  = "Table";
  public static final String TAG_INDEX  = "Index";
  public static final String TAG_JOINS  = "Joins";
  
  //Filter Tags and Attributes
  public static final String TAG_FILTER                   = "Filter";
  public static final String TAG_FILTER_CONDITION         = "FilterCondition";
  public static final String ATT_FILTER_ON_TABLE          = "onTable";
  public static final String ATT_FILTER_ON_FIELD          = "field";
  public static final String ATT_FILTER_CONDITION_PREFIX  = "prefix";
  public static final String ATT_FILTER_CONDITION_CAPTION = "caption";
  public static final String ATT_FILTER_CONDITION_CLASS_NAME = "className";
  public static final String ATT_FILTER_CONDITION_CAPTION_PROPERTY = "captionProperty";
  public static final String ATT_FILTER_CONDITION_MEMORY_ONLY = "memoryOnly";
  public static final String ATT_FILTER_LEVEL             = "level";
  
  public static final String VAL_FILTER_LEVEL_DATABASE   = "db";
  public static final String VAL_FILTER_LEVEL_MEMORY     = "mem";
  public static final String VAL_FILTER_LEVEL_DATABASE_AND_MEMORY = "db-mem";
  
  //Fields Predefined
  public static final String TAG_REF            = IFocFieldType.TYPE_REF;
  public static final String TAG_CODE           = IFocFieldType.TYPE_CODE;
  public static final String TAG_COMPANY        = IFocFieldType.TYPE_COMPANY;
  public static final String TAG_EXTERNAL_CODE  = IFocFieldType.TYPE_EXTERNAL_CODE;
  public static final String TAG_DATE           = IFocFieldType.TYPE_DATE;
  public static final String TAG_DESCRIPTION    = IFocFieldType.TYPE_DESCRIPTION;
  public static final String TAG_NAME           = IFocFieldType.TYPE_NAME;
  public static final String TAG_SITE           = IFocFieldType.TYPE_SITE;
  public static final String TAG_ORDER          = IFocFieldType.TYPE_ORDER;
  public static final String TAG_NOT_COMPLETED  = IFocFieldType.TYPE_NOT_COMPLETED;
  public static final String TAG_IS_SYSTEM      = IFocFieldType.TYPE_IS_SYSTEM;
  public static final String TAG_DEPRECATED     = IFocFieldType.TYPE_DEPRECATED;
  public static final String TAG_TREE           = IFocFieldType.TYPE_TREE;
  public static final String TAG_REVIEW_STATUS  = IFocFieldType.TYPE_REVIEW_STATUS;
  public static final String TAG_REVIEW_COMMENT = IFocFieldType.TYPE_REVIEW_COMMENT;
  
  //Field Types
  public static final String TAG_STRING          = IFocFieldType.TYPE_STRING;
  public static final String TAG_INTEGER         = IFocFieldType.TYPE_INTEGER;
  public static final String TAG_BOOLEAN         = IFocFieldType.TYPE_BOOLEAN;
  public static final String TAG_DOUBLE          = IFocFieldType.TYPE_DOUBLE;
  public static final String TAG_DATE_FIELD      = IFocFieldType.TYPE_DATE_FIELD;
  public static final String TAG_TIME_FIELD      = IFocFieldType.TYPE_TIME_FIELD;
  public static final String TAG_MULTIPLE_CHOICE = IFocFieldType.TYPE_MULTIPLE_CHOICE;
  public static final String TAG_MULTIPLE_CHOICE_STRING = IFocFieldType.TYPE_MULTIPLE_CHOICE_STRING;
  public static final String TAG_BLOB            = IFocFieldType.TYPE_BLOB;
  public static final String TAG_OBJECT          = "Object";//IFocFieldType.TYPE_FOREIGN_ENTITY;
  public static final String TAG_TABLE_NAME      = IFocFieldType.TYPE_TABLE_NAME;
  public static final String TAG_LONG            = IFocFieldType.TYPE_LONG;
  
  //Object field
  public static final String ATT_CASCADE          = "cascade";
  public static final String ATT_DETACH           = "detach";
  public static final String ATT_SAVE_ONE_BY_ONE  = "oneByOneSave";
  public static final String ATT_TABLE            = "table";
  public static final String ATT_CACHED_LIST      = "cachedList";
  public static final String ATT_FORCED_DB_NAME   = "forcedDBName";
  public static final String ATT_NULL_VALUES_ALLOWED = "nullValueAllowed";
  public static final String ATT_LIST_FILTER_PROPERTY = "listFilterProperty";
  public static final String ATT_LIST_FILTER_VALUE    = "listFilterValue";
  
  //TAG_MULTIPLE_CHOICE
  public static final String ATT_SORT_ITEMS      = "sortChoices";
  public static final String TAG_CHOICE          = "Choice";
  public static final String ATT_ID              = "id";
  public static final String ATT_CAPTION         = "caption";
  
  //Attributes for MultipleChoice String
  public static final String ATT_SAME_COLUMN = "sameCol";
  
  public static final String ATT_NAME        = "name";
  public static final String ATT_TITLE       = "title";
  public static final String ATT_SIZE        = "size";
  public static final String ATT_DECIMALS    = "decimals";
  public static final String ATT_KEY         = "partOfKey";
  public static final String ATT_MANDATORY   = "mandatory";
  public static final String ATT_GROUPING    = "grouping";
  public static final String ATT_COMPRESS    = "compress";
  
  //Table node attributes 
  public static final String ATT_WORKFLOW    = "workflow";
  public static final String ATT_TREE        = "tree";
  public static final String ATT_REPORT_CONTEXT = "reportContext";
  public static final String ATT_DB_RESIDENT = "dbResident";//Used for fields also
  public static final String ATT_CACHED      = "cached";
  public static final String ATT_DB_SOURCE   = "dbSource";
  public static final String ATT_ALLOW_ADAPT_DATA_MODEL 	= "allowAdaptDataModel";
  final public static String ATT_LOGICAL_DELETE       		= "logicalDelete";

  public static final String ATT_IN_TABLE_EDITABLE = "inTableEditable";
  public static final String ATT_STORAGE_NAME     = "storageName";

  //Join
  //-------------------------
  public static final String TAG_JOIN  = "Join";
  
  public static final String ATT_JOIN_ALIAS          = "alias";
  public static final String ATT_JOIN_TABLE          = "table";
  public static final String ATT_JOIN_ON             = "on";
  public static final String ATT_JOIN_TYPE           = "type";
  public static final String ATT_JOIN_IS_PRIMARY_KEY = "isPrimaryKey";
 
  public static final String TAG_JOIN_FIELD   = "JoinField";
  public static final String ATT_JOIN_FLD_SRC = "sourceField";
  public static final String ATT_JOIN_FLD_TAR = "targetField";
  
  public static final String ATT_JOIN_WHERE = "where";
  //-------------------------
  
  //Group By
  //-------------------------
  public static final String TAG_GROUP_BY         = "GroupBy";
  public static final String TAG_GROUP_FIELD      = "GroupField";
  
  public static final String ATT_GROUP_BY_FORMULA = "groupByFormula";
  public static final String ATT_GROUP_BY_FORMULA_ADDITIONAL_FIELDS = "groupByFormulaAdditionalFields";
  //-------------------------
  
  //Group By
  //-------------------------
  public static final String TAG_WORKFLOW         = "Workflow";
  
  public static final String ATT_WORKFLOW_CODE    = "Code";
  public static final String ATT_WORKFLOW_TITLE   = "Title";
  //-------------------------
}
