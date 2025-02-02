/*
 * Created on Feb 23, 2006
 */
package com.foc.jasper;

import com.foc.desc.FocDesc;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author 01Barmaja
 */
public interface FocJRReportLauncher {
  public JasperPrint fillReport();
  public FocDesc     getFieldDictionaryFocDesc();
  public FocDesc     getParameterDictionaryFocDesc();
  //public boolean     sendByEmail(String fileName);
}
