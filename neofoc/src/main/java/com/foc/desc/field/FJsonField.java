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
package com.foc.desc.field;

import java.awt.Component;
import java.sql.Types;

import com.foc.db.DBManager;
import com.foc.desc.FocObject;
import com.foc.gui.FGTextArea;
import com.foc.gui.FGTextAreaPanel;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.gui.table.cellControler.BlobStringCellControler;
import com.foc.property.FJsonProperty;
import com.foc.property.FProperty;

/**
 * Field type for storing JSON data.
 * Uses jsonb on PostgreSQL (binary, indexed, validated), TEXT/CLOB on other databases.
 * When serialized to REST API responses the value is emitted as a raw JSON object,
 * not as a quoted string.
 */
public class FJsonField extends FStringField {

  private int rows    = 20;
  private int columns = 80;

  public FJsonField(String name, String title, int id, boolean key) {
    super(name, title, id, key, Integer.MAX_VALUE);
  }

  public FJsonField(String name, String title, int id, boolean key, int rows, int columns) {
    super(name, title, id, key, Integer.MAX_VALUE);
    this.rows    = rows;
    this.columns = columns;
  }

  @Override
  public int getSqlType() {
    // Types.OTHER covers PostgreSQL jsonb; for other providers the DDL uses TEXT/CLOB
    return Types.OTHER;
  }

  @Override
  public String getCreationString(String name) {
    if (getProvider() == DBManager.PROVIDER_POSTGRES) {
      return " \"" + name + "\" jsonb";
    } else if (getProvider() == DBManager.PROVIDER_ORACLE) {
      return " \"" + name + "\" CLOB";
    } else if (getProvider() == DBManager.PROVIDER_MSSQL) {
      return " " + name + " nvarchar(MAX)";
    } else {
      // MySQL, H2, and everything else
      return " " + name + " TEXT";
    }
  }

  @Override
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue) {
    FJsonProperty prop = new FJsonProperty(masterObj, getID(), (String) defaultValue);
    if (isAllowNullProperties() && defaultValue == null) {
      prop.setValueNull(true);
    }
    return prop;
  }

  private FGTextArea newTextArea() {
    FGTextArea textArea = new FGTextArea();
    textArea.setColumns(columns);
    textArea.setRows(rows);
    textArea.setColumnsLimit(Integer.MAX_VALUE);
    textArea.setCapital(false);
    return textArea;
  }

  @Override
  public Component getGuiComponent(FProperty prop) {
    FGTextArea textArea = newTextArea();
    if (prop != null) textArea.setProperty(prop);
    return new FGTextAreaPanel(textArea, getTitle());
  }

  @Override
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop) {
    FGTextArea textAreaEditor   = newTextArea();
    FGTextArea textAreaRenderer = newTextArea();
    return new BlobStringCellControler(textAreaEditor, textAreaRenderer);
  }

  @Override
  public int compareSQLDeclaration(FField field) {
    // PostgreSQL jsonb columns should not be compared for schema migration purposes
    if (getProvider() == DBManager.PROVIDER_POSTGRES) {
      return 0;
    }
    return super.compareSQLDeclaration(field);
  }

  public int getRows() {
    return rows;
  }

  public int getCols() {
    return columns;
  }
}
