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
package b01.officeLink.excel;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.foc.desc.FocObject;
import com.foc.property.FProperty;
import com.foc.property.PropertyFocObjectLocator;

import b01.officeLink.OfficeLink;

public abstract class ExcelRefiller implements ExcelRefillerInterface {

  private HashMap<String, ExcelGroupDefinition> groupMap = null;
  private FocExcelDocument                      focExcelDocument = null;
  private int                                   currentRow = 0;

  public ExcelRefiller() {
    groupMap = new HashMap<String, ExcelGroupDefinition>();
  }

  public void dispose() {
    if (groupMap != null) {
      Iterator iter = (Iterator) groupMap.values().iterator();
      while (iter != null && iter.hasNext()) {
        ExcelGroupDefinition grpDef = (ExcelGroupDefinition) iter.next();
        if (grpDef != null) {
          grpDef.dispose();
        }
      }
      groupMap.clear();
      groupMap = null;
    }
  }

  public void fillGroupDefinition(FocExcelDocument excel) {
    setFocExcelDocument(excel);
    Sheet sourceSheet = excel.getSheetAt(1);

    for (int i = 0; i < 100; i++) {
      Row sRow = sourceSheet.getRow(i);
      Cell sCell = sRow != null ? sRow.getCell(0) : null;

      RichTextString groupColValue = sCell != null ? sCell.getRichStringCellValue() : null;
      String groupColValueStr = groupColValue != null ? groupColValue.getString() : null;

      if (groupColValueStr != null) {
        ExcelGroupDefinition groupDef = groupMap.get(groupColValueStr);
        if (groupDef == null) {
          groupDef = new ExcelGroupDefinition();
          groupMap.put(groupColValueStr, groupDef);
        }
        groupDef.addRow(i);
      }
    }
  }

  public String evaluateExpression(FocObject object, String formulaString) {
    String formulaResult = null;
    PropertyFocObjectLocator propertyFocObjectLocator = new PropertyFocObjectLocator();
    propertyFocObjectLocator.parsePath(formulaString, object.getThisFocDesc(), object, null);
    FProperty property = propertyFocObjectLocator.getLocatedProperty();

    if (property != null && property.getObject() != null) {
      formulaResult = property.getObject().toString();
    }
    return formulaResult;
  }

  private String analyseContent(String cellValue, FocObject object) {
    int startIndex = 0;
    int endIndex = 0;
    String formulaResult = null;

    while (startIndex >= 0 && endIndex >= 0) {
      startIndex = cellValue.indexOf(OfficeLink.FORMULA_START_IDENTIFIER, endIndex);
      if (startIndex >= 0) {
        endIndex = cellValue.indexOf(OfficeLink.FORMULA_END_IDENTIFIER, startIndex);
        if (endIndex > 0) {
          String formulaString = cellValue.substring(startIndex + OfficeLink.FORMULA_START_IDENTIFIER.length(), endIndex);
          formulaResult = evaluateExpression(object, formulaString);
          if (formulaResult == null)
            formulaResult = "";
        }
      }
    }
    return formulaResult != null ? formulaResult : cellValue;
  }

  public ExcelGroupDefinition getGroupDefinition(String groupKey) {
    return groupMap.get(groupKey);
  }

  public void copyColumnWidths() {
    Sheet srcSheet = getSourceSheet();
    Sheet tarSheet = getTargetSheet();
    for (int i = 1; i < 40; i++) {
      int w = srcSheet.getColumnWidth(i);
      tarSheet.setColumnWidth((short) (i - 1), w);
    }
  }

  public void fillGroupContent(String groupStr, FocObject object) {
    ExcelGroupDefinition grpDef = getGroupDefinition(groupStr);
    Sheet srcSheet = getSourceSheet();
    Sheet tarSheet = getTargetSheet();
    if (grpDef != null) {
      for (int i = 0; i < grpDef.getRowCount(); i++) {
        int rowIdx = grpDef.getRowAt(i);
        Row sRow = srcSheet.getRow(rowIdx);
        if (sRow != null) {
          Row tRow = tarSheet.getRow(currentRow);
          if (tRow == null) {
            tRow = tarSheet.createRow(currentRow);
          }
          if (tRow != null) {
            tRow.setHeight(sRow.getHeight());
            for (int c = 0; c < 20; c++) {
              Cell sCell = sRow.getCell(c + 1);
              if (sCell != null) {
                Cell tCell = tRow.getCell(c);
                if (tCell == null) {
                  tCell = tRow.createCell(c);
                }
                if (tCell != null) {
                  tCell.setCellStyle(sCell.getCellStyle());

                  String str = "";
                  if (sCell.getCellType() == Cell.CELL_TYPE_STRING) {
                    RichTextString rts = sCell.getRichStringCellValue();
                    str = rts.getString();
                    str = analyseContent(str, object);
                  } else if (sCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    str = String.valueOf(sCell.getNumericCellValue());
                  }

                  if (str != null && !str.isEmpty()) {
                    int iVal = convertString2Integer(str);
                    double dVal = convertString2Double(str);
                    if (iVal != Integer.MAX_VALUE) {
                      tCell.setCellValue(iVal);
                    } else if (!Double.isNaN(dVal)) {
                      tCell.setCellValue(dVal);
                    } else {
                      if (getFocExcelDocument() != null && getFocExcelDocument().getWorkbook() != null) {
                        tCell.setCellValue(getFocExcelDocument().getWorkbook().getCreationHelper().createRichTextString(str));
                      }
                    }
                  }
                }
              }
            }
          }
          currentRow++;
        }
      }
    }
  }

  private double convertString2Double(String str) {
    double d = Double.NaN;
    try {
      d = Double.valueOf(str);
    } catch (Exception e) {
      d = Double.NaN;
    }
    return d;
  }

  private int convertString2Integer(String str) {
    int i = Integer.MAX_VALUE;
    try {
      i = Integer.valueOf(str);
    } catch (Exception e) {
      i = Integer.MAX_VALUE;
    }
    return i;
  }

  public FocExcelDocument getFocExcelDocument() {
    return focExcelDocument;
  }

  public void setFocExcelDocument(FocExcelDocument focExcelDocument) {
    this.focExcelDocument = focExcelDocument;
  }

  public int getCurrentRow() {
    return currentRow;
  }

  public void setCurrentRow(int currentRow) {
    this.currentRow = currentRow;
  }

  public Sheet getSourceSheet() {
    return focExcelDocument.getSheetAt(1);
  }

  public Sheet getTargetSheet() {
    return focExcelDocument.getSheetAt(0);
  }
}
