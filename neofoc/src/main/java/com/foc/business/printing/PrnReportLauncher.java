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
package com.foc.business.printing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.jasper.FocJRReportLauncher;
import com.foc.jasper.FocJRValidationPanel;
import com.foc.jasper.JRFocListDataSource;
import com.foc.jasper.JRFocListWrapperDataSource;
import com.foc.jasper.JRFocObjectParameters;
import com.foc.list.FocList;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

public class PrnReportLauncher implements FocJRReportLauncher {

	@SuppressWarnings("unused")
	private PrnContext                    context               = null;
	private JRFocObjectParameters         params                = null;
	private JRFocListDataSource           dataSource            = null;
	private JRFocListWrapperDataSource    wrapperDataSource     = null;
	private FocDesc                       rootDictionaryFocDesc = null;
	private IPrnReportCreator             creator               = null;
	private PrnLayoutGuiBrowsePanel_Print layoutBrowsePanel     = null;
	private boolean                       withLogo              = true;
	
//  private boolean           objectToPrint_Owner = false;
//  private IFocData          objectToPrint       = null;
//  private String            printingContext     = null;
  
	public PrnReportLauncher(IPrnReportCreator creator){
		this.creator = creator;
	}

	public void dispose(){
//		if(objectToPrint != null) {
//			if(objectToPrint_Owner) {
//				objectToPrint.dispose();
//			}
//			objectToPrint       = null;
//			objectToPrint_Owner = false;
//		}
		
		dispose_DataSourceAndParams();
		context = null;
		creator = null;
		layoutBrowsePanel = null;
	}

	/*
	public boolean isObjectToPrint_Owner() {
		return objectToPrint_Owner;
	}

	public void setObjectToPrint_Owner(boolean objectToPrintOwner) {
		objectToPrint_Owner = objectToPrintOwner;
	}
	
	public FocObject getObjectToPrint() {
		FocObject focObject = null;
		if(objectToPrint != null && objectToPrint instanceof FocDataMap){
			FocDataMap dataMap = (FocDataMap) objectToPrint;
			focObject = (FocObject) dataMap.getMainFocData();
		}else{
			focObject = objectToPrint instanceof FocObject ? (FocObject) objectToPrint : null;
		}
		return focObject;
	}
	
	public IFocData getFocDataToPrint(){
		return objectToPrint;
	}

	public void setObjectToPrint(IFocData objectToPrint) {
		this.objectToPrint = objectToPrint;
	}

	public String getPrintingContext() {
		return printingContext;
	}

	public void setPrintingContext(String printingContext) {
		this.printingContext = printingContext;
	}
	*/
	
	public boolean isWithLogo() {
		return withLogo;
	}
	
	public void setWithLogo(boolean withLogo) {
		this.withLogo = withLogo;
	}
	
	public void init(PrnContext context, PrnLayout layout, FocObject focObject, FocList focList){
		init(context, layout, focObject, new JRFocListDataSource(focList));
	}

	public void init(PrnContext context, PrnLayout layout, FocObject focObject, JRFocListDataSource dataSource){
		params = new JRFocObjectParameters(focObject);
		this.dataSource = dataSource;
	}
	
	public void init(PrnContext context, PrnLayout layout, FocObject focObject, JRFocListWrapperDataSource dataSource){
		params = new JRFocObjectParameters(focObject);
		this.wrapperDataSource = dataSource;
	}
	
	public void dispose_DataSourceAndParams(){
		if(dataSource != null){
			dataSource.dispose();
			dataSource = null;
		}
		if(wrapperDataSource != null){
			wrapperDataSource.dispose();
			wrapperDataSource = null;
		}
		if(params != null){
			params.dispose();
			params = null;
		}
	}
		
	public JRFocObjectParameters getParams(){
		return params;
	}
	
	public void pushParam(String param, Object obj){
		if(params != null){
			params.put(param, obj);
		}
	}

	public void pushParam_IfEmpty(String param, Object obj){
		if(params != null){
			String value = (String) params.get(param);
			if(value == null || value.trim().isEmpty()){
				params.put(param, obj);
			}
		}
	}

	public Object getParam(String param){
		return params != null ? params.get(param) : null; 
	}

	public boolean fillReportContent() {
		return false;
	}
	
	public byte[] web_FillReport(PrnLayout prnLayout, String fileName){
		byte[] bytes = null;
		if(prnLayout != null){
			InputStream inputStream = common_FillReport(prnLayout);
			try {
				bytes = JasperRunManager.runReportToPdf(inputStream, getParams(), getDataSource());
			} catch (JRException e) {
				Globals.logException(e);
			}
			common_DisposeReport();
		}
		return bytes;
	}
	
	public byte[] printRTFDocument(PrnLayout layout){
		return printDocument(layout, true);
	}

	public byte[] printWordDocument(PrnLayout layout){
		return printDocument(layout, false);
	}
	
	private byte[] printDocument(PrnLayout layout, boolean rtf){
		byte[] result = null;
		
		JasperPrint jasperPrint = null;
    if(layout != null && creator != null){
    	InputStream inputStream = common_FillReport(layout);
    	try {
    		jasperPrint = JasperFillManager.fillReport(inputStream, params, dataSource);
			} catch (JRException e) {
				Globals.logException(e);
			}
    	common_DisposeReport();
    }
		
    result = jasperPrint2ByteArray(jasperPrint, rtf);

		return result;
	}
	
	public static byte[] jasperPrint2ByteArray(JasperPrint jasperPrint, boolean rtf) {
		byte[] result = null;
		
    Exporter exporter = null;
    if(rtf) {
    	exporter = new JRRtfExporter();
    } else {
    	exporter = new JRDocxExporter();
    }
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		
		// Exporting to a ByteArrayOutputStream for the Web
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    if(rtf) {
    	exporter.setExporterOutput(new SimpleWriterExporterOutput(baos));
    } else {
    	exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
    }
		
		//Exporting to a file saved locally
		//File exportReportFile = new File("C:\\temp\\report.docx");
		//exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(exportReportFile));

		try{
			exporter.exportReport();
		}catch (JRException e){
			Globals.logException(e);
		}
		
		result = baos.toByteArray();
		return result;
	}
	
	private void common_DisposeReport(){
		if(creator != null){
			creator.disposeLauncherContent();
		}
		dispose_DataSourceAndParams();
	}
	
	private InputStream common_FillReport(PrnLayout prnLayout){
		InputStream inputStream = null;
		if(creator != null){
			creator.resetLauncherContent(prnLayout, null);
			pushParam("X_PRINT_LOGO", isWithLogo() ? 1 : 0);
			
			try{
	    	String path = getReportFileName(prnLayout);
	    	Globals.logString("FileName : "+path);
	    	
	      try{
	        File file = new File(path);
	        Globals.logString("File Exists before URL : "+(file != null ? "YES" : "NO"));        
	        if(file.exists()){
	        	inputStream = new FileInputStream(path);
	        }
	        Globals.logString("Stream Exists before URL : "+(inputStream != null ? "YES" : "NO"));
	        if(inputStream == null){
	          URL url = Thread.currentThread().getContextClassLoader().getResource(path);
	          Globals.logString("URL OK : "+(url != null ? "YES" : "NO"));
	          if(url != null){
	          	inputStream = url.openStream();  
	          }
	          Globals.logString("Stream Exists After URL : "+(inputStream != null ? "YES" : "NO"));          
	        }
	      }catch(Exception e){
	        Globals.logException(e);
	      }
	    	
	      
	    }catch(Exception e){
	      Globals.logException(e);
	    } 	
		}
		return inputStream;
	}
	
	@Override
	public JasperPrint fillReport() {
    JasperPrint jrPrint = null;
    PrnLayout layout = (PrnLayout) getLayoutBrowsePanel().getSelectedObject();
    if(layout != null && creator != null){
    	InputStream inputStream = common_FillReport(layout);
    	try {
				jrPrint = JasperFillManager.fillReport(inputStream, params, dataSource);
			} catch (JRException e) {
				Globals.logException(e);
			}
    	common_DisposeReport();
    }
    return jrPrint;
	}
	
	/*@Override
	public JasperPrint fillReport() {
    JasperPrint jrPrint = null;
    PrnLayout layout = (PrnLayout) getLayoutBrowsePanel().getSelectedObject();
    if(layout != null && creator != null){
    	Object objPrintLogo = getParam("X_PRINT_LOGO");
    	creator.resetLauncherContent(layout);
    	pushParam("X_PRINT_LOGO", objPrintLogo);
	    try{
	    	String path = getReportFileName(layout);
	    	Globals.logString("FileName : "+path);
	    	
	      InputStream in = null;
	      try{
	        File file = new File(path);
	        Globals.logString("File Exists before URL : "+(file != null ? "YES" : "NO"));        
	        if(file.exists()){
	          in = new FileInputStream(path);  
	        }
	        Globals.logString("Stream Exists before URL : "+(in != null ? "YES" : "NO"));
	        if(in == null){
	          URL url = Thread.currentThread().getContextClassLoader().getResource(path);
	          Globals.logString("URL OK : "+(url != null ? "YES" : "NO"));
	          if(url != null){
	            in = url.openStream();  
	          }
	          Globals.logString("Stream Exists After URL : "+(in != null ? "YES" : "NO"));          
	        }
	      }catch(Exception e){
	        Globals.logException(e);
	      }
	    	
	      jrPrint = JasperFillManager.fillReport(in, params, dataSource);
	    	
	      //jrPrint = JasperFillManager.fillReport(Globals.getInputStream(getReportFileName()), params, dataSource);
	    }catch(Exception e){
	      Globals.logException(e);
	    }
    }
    return jrPrint;
	}*/

	@Override
	public FocDesc getFieldDictionaryFocDesc() {
		return rootDictionaryFocDesc;
	}

	public void setRootDictionaryFocDesc(FocDesc focDesc) {
		rootDictionaryFocDesc = focDesc;
	}
	
	public boolean sendByEmail(String fileName) {
		boolean error = true;
		if(Globals.getEmailSender() != null){
			error = Globals.getEmailSender().sendEMail_SelectContactAndPopupEMail("", fileName);
		}
		return error;
	}
	
  public String getReportFileName(PrnLayout layout){
  	return "reports/"+layout.getFileName();
  }

  public FPanel newPanel(){
  	FPanel panel = new FPanel();
  	panel.add(new FocJRValidationPanel(this, false, this.getFieldDictionaryFocDesc()), 0, 0);
  	return panel;
  }

  public void popupPanel(){
  	FPanel panel = new FPanel();
  	panel.add(new FocJRValidationPanel(this, false, this.getFieldDictionaryFocDesc()), 0, 0);
  	Globals.getDisplayManager().popupDialog(panel, "Report", false);
  }

	public PrnLayoutGuiBrowsePanel_Print getLayoutBrowsePanel() {
		return layoutBrowsePanel;
	}

	public void setLayoutBrowsePanel(PrnLayoutGuiBrowsePanel_Print layoutBrowsePanel) {
		this.layoutBrowsePanel = layoutBrowsePanel;
	}

	@Override
	public FocDesc getParameterDictionaryFocDesc() {
		return null;
	}

	public JRDataSource getDataSource() {
		return dataSource != null ? dataSource : wrapperDataSource ;
	}
}
