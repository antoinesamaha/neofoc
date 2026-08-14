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
package com.foc.property;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocObject;

/**
 * Runtime property for FJsonField.
 * Stores the JSON value as a plain string internally (inherited from FString)
 * and takes care of provider-specific SQL quoting when reading/writing the database.
 *
 * On PostgreSQL the value is stored in a jsonb column; the driver accepts a
 * plain quoted string and PostgreSQL validates and converts it automatically.
 * No explicit ::jsonb cast is needed in the INSERT/UPDATE statement.
 *
 * When serialized to a REST response the caller checks instanceof FJsonProperty
 * and emits the raw JSON bytes instead of a quoted string, so the client
 * receives a proper JSON object rather than a JSON-encoded string.
 */
public class FJsonProperty extends FString {

  public FJsonProperty(FocObject focObj, int fieldID, String jsonStr) {
    super(focObj, fieldID, jsonStr);
  }

  /**
   * Returns the SQL representation of this JSON value.
   * For PostgreSQL a plain quoted string is sufficient — the jsonb column
   * validates the content on insert.  For other providers the behaviour is
   * identical to a normal string field.
   */
  @Override
  public String getSqlString() {
    if (isValueNull()) {
      return getNullSQLValue();
    }

    String raw = getString();
    if (raw == null) raw = "";

    if (getProvider() == DBManager.PROVIDER_MSSQL) {
      raw = raw.replace("'", "''");
      return "N'" + raw + "'";
    } else if (getProvider() == DBManager.PROVIDER_ORACLE
        || getProvider() == DBManager.PROVIDER_POSTGRES) {
      raw = raw.replaceAll("'", "''");
      return "'" + raw + "'";
    } else if (getProvider() == DBManager.PROVIDER_H2) {
      raw = raw.replaceAll("'", "''");
      return "'" + raw + "'";
    } else {
      raw = raw.replaceAll("\"", "''");
      return "\"" + raw + "\"";
    }
  }

  /**
   * Called when reading back from the database.
   * PostgreSQL returns jsonb as a plain string; no special conversion is needed
   * beyond what FString already handles ('' → ").
   */
  @Override
  protected void setSqlStringInternal(String str) {
    if (str == null && isAllowNullProperties()) {
      setValueNull_AndResetIntrinsicValue(false);
    } else {
      // For all providers the stored value comes back as a plain string.
      // The parent FString converts '' → " which is correct for Oracle/Postgres/MySQL.
      // For MSSQL and H2 no double-quote conversion is needed but the value is still
      // a plain string, so calling super is safe.
      super.setSqlStringInternal(str);
    }
  }
}
