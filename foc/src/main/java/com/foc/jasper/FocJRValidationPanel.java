/*
 * Created on Feb 23, 2006
 */
package com.foc.jasper;

import java.awt.event.ActionEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.business.printing.PrnReportLauncher;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.dataModelTree.DataModelGuiPanel_ForReporting;
import com.foc.gui.FGButton;
import com.foc.gui.FGCheckBox;
import com.foc.gui.FPanel;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.view.JasperViewer;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FocJRValidationPanel extends FPanel{

	public static final int BUTTON_PREVIEW      = 1;
  public static final int BUTTON_SAVE_AS_PDF  = 2;
  public static final int BUTTON_EMAIL_AS_PDF = 3;
  public static final int BUTTON_PRINT        = 4;
  public static final int BUTTON_PRINT_ALL    = 5;
  public static final int BUTTON_EXIT         = 6;
  
  private FocJRReportLauncher launcher          = null;
  private int                 clickedButton     = 0;
  private FGCheckBox          printLogoCheckBox = null;
  
  public FocJRValidationPanel(FocJRReportLauncher launcher){
  	this(launcher, false, null);
  }
  
	public FocJRValidationPanel(FocJRReportLauncher launcher, boolean showPrintAllButton, FocDesc rootDictionaryFocDesc){
    this.launcher = launcher;
        
    FGButton previewsButton = new FGButton("Preview");//Previews
    previewsButton.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
      	if(!printPreview(getLauncher())){
          setClickedButton(BUTTON_PREVIEW);
        }
      }
    });
    
    FGButton toPDF = new FGButton("Save As PDF");
    toPDF.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {      	
      	try {
          JasperPrint print = getLauncher().fillReport();
          if(print != null){
            String Output = getSelectedFilePath();
            if (Output != null){
              JasperExportManager.exportReportToPdfFile(print, Output);
              setClickedButton(BUTTON_SAVE_AS_PDF);
            }
      		}
        }catch(Exception x){
        	Globals.logException(x);
        }
      }
    });

    FGButton emailPDF = null;
    if(getLauncher() instanceof PrnReportLauncher){
	    emailPDF = new FGButton("email As PDF");
	    emailPDF.addActionListener(new AbstractAction(){
	      public void actionPerformed(ActionEvent e) {      	
	      	try {
	      		PrnReportLauncher     launcher = (PrnReportLauncher)getLauncher();
	      		
	          JasperPrint print = getLauncher().fillReport();
	          if(print != null){
		      		JRFocObjectParameters params   = launcher != null ? launcher.getParams() : null;
		      		FocObject             focObj   = params != null ? params.getFocObject() : null;
		      		String prefix = focObj != null ? focObj.getPrintingFileName() : "Everpro_doc"; 

	          	boolean getAFileName    = true;
	          	String  outputDirectory = "c:\\temp"; 
	          	String  outputFileName  = null;
	          	
	          	//Create the directory if necessary
	          	File dir = new File(outputDirectory);
	          	dir.mkdirs();
	          	
	          	outputFileName = outputDirectory+"\\"+prefix+".pdf";
	          	File fle = new File(outputFileName);
	          	getAFileName = fle.exists();
	          	
	          	while(getAFileName){
		          	Date 							systemDate 	= new java.sql.Date(System.currentTimeMillis());
		          	SimpleDateFormat 	format 			= new SimpleDateFormat("__yy-MMM-dd__hh_mm_ss");
		          	
		          	String systate = format.format(systemDate);
		
		        		outputFileName = outputDirectory+"\\"+prefix+systate+".pdf";
		        		fle = new File(outputFileName);
		        		getAFileName = fle.exists();
	          	}
	          	
		          if (outputFileName != null){
		            JasperExportManager.exportReportToPdfFile(print, outputFileName);
		            ((PrnReportLauncher)getLauncher()).sendByEmail(outputFileName);
		            
		            setClickedButton(BUTTON_EMAIL_AS_PDF);
		          }
	      		}
	        }catch(Exception x){
	        	Globals.logException(x);
	        }
	      }
	    });
    }
    
    FGButton printButton = new FGButton("Print");
    printButton.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
      	printReport();
      	setClickedButton(BUTTON_PRINT);
      }
    });
    
    FGButton printAllButton = null;
    if(showPrintAllButton){
	    printAllButton = new FGButton("Print All");
	    printAllButton.addActionListener(new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					printReport();
					setClickedButton(BUTTON_PRINT_ALL);
				}
	    });
    }

    FGButton exitButton = new FGButton("Exit");
    exitButton.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        Globals.getDisplayManager().goBack();
        //setClickedButton(BUTTON_EXIT);
      }
    });
    
    printLogoCheckBox = new FGCheckBox("Print logo");
    printLogoCheckBox.setSelected(true);

    FGButton dictionaryButton = null;
    if(ConfigInfo.getReportFileFullPath() != null && (getFieldDictionaryFocDesc() != null || getParameterDictionaryFocDesc() != null)){
	    dictionaryButton = new FGButton("Dictionary");
	    dictionaryButton.addActionListener(new AbstractAction(){
	      public void actionPerformed(ActionEvent e) {
	      	DataModelGuiPanel_ForReporting panel = new DataModelGuiPanel_ForReporting();
	      	
	      	if(getFieldDictionaryFocDesc() != null){
	      		panel.setFieldDictionaryFocDesc(getFieldDictionaryFocDesc(), 4);
	      	}
	      	if(getParameterDictionaryFocDesc() != null){
	      		panel.setParameterDictionaryFocDesc(getParameterDictionaryFocDesc(), 4);
	      	}
	      	
					Globals.getDisplayManager().popupDialog(panel, "Data Dictionay", true);
	      }
	    });
    }
    
    int y = 0; 
    add(previewsButton, y++, 0);
    add(toPDF, y++, 0);
    if(emailPDF != null) add(emailPDF, y++, 0);
    add(printButton, y++, 0);
    if(showPrintAllButton){
    	add(printAllButton, y++, 0);
    }
    add(exitButton, y++, 0);
    if(dictionaryButton != null){
    	add(dictionaryButton, y++, 0);
    }
    add(printLogoCheckBox, y++, 0);
  }
  
  public void dispose(){
  	super.dispose();
  	launcher = null;
  }
  
  public void printReport(){
    try{
    	printReport(getLauncher(), printLogoCheckBox != null ? printLogoCheckBox.isSelected() : true);
    }catch (Exception x){
      Globals.logException(x);
    }
  }

  public String getSelectedFilePath() {
  	String Outputpath = null;
  	JFileChooser fch = new JFileChooser("C:\\");
  	ExtensionFileFilter filter = new ExtensionFileFilter("pdf", "PDF File Format");
  	fch.addChoosableFileFilter(filter);
  	fch.setFileSelectionMode( JFileChooser.FILES_ONLY );
    int result = fch.showSaveDialog( this );
    if ( result == JFileChooser.CANCEL_OPTION ){
    	return null;	
    }else{
      try{
        Outputpath = fch.getSelectedFile().toString();
  		} catch( Exception e ){
        Globals.logException(e);
    		//JOptionPane.showMessageDialog(this, "Error while saving file!", "Error", JOptionPane.ERROR_MESSAGE );
  		}
    	return Outputpath + ".pdf";
    }
  }
  
  public FocJRReportLauncher getLauncher(){
  	setPrintLogoParameter(launcher);
    return launcher;
  }
  
  public int getClickedButton(){
  	return clickedButton;
  }
  
  private void setClickedButton(int clickedButton){
  	this.clickedButton = clickedButton;
  }

	public FocDesc getFieldDictionaryFocDesc() {
		return getLauncher().getFieldDictionaryFocDesc();
	}
	
	public FocDesc getParameterDictionaryFocDesc() {
		return getLauncher().getParameterDictionaryFocDesc();
	}

  private void setPrintLogoParameter(FocJRReportLauncher launcher){
  	if(launcher != null && printLogoCheckBox != null){
  		if(launcher instanceof PrnReportLauncher){
  			((PrnReportLauncher)launcher).pushParam("X_PRINT_LOGO", printLogoCheckBox.isSelected() ? 1 : 0);
  		}
  	}
  }

	//-------------------------------------------------
	// STATIC
	//-------------------------------------------------

  public static void printReport(FocJRReportLauncher launcher, boolean printLogo){
  	printReport(launcher, null, null, printLogo);
  }
  
  public static void printReport(FocJRReportLauncher launcher, FocPrintingConfig focPrintingConfig, String reportTitle){
  	JRPrintServiceExporter   exporter                 = null;
  	PrintRequestAttributeSet printRequestAttributeSet = null;
		Globals.setMouseComputing(true);
		boolean exceptionInExport = false;
  	try{
      JasperPrint print = launcher.fillReport();
      if(print != null){
      	//print.getPages();
        //JasperPrintManager.printReport(print, true);  
        printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(MediaSizeName.ISO_A4);
        
        exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.TRUE);
        //exporter.checkAvailablePrinters();
        try{
        	exporter.exportReport();
        }catch(Exception e){
        	exceptionInExport = true;
        	throw e;
        }
      }
    }catch (Exception x){
    	if(exceptionInExport){
    		if(reportTitle != null){
    			Globals.getDisplayManager().popupMessage("Could Not print document "+reportTitle+".\nMay be the document has no pages.");
    		}else{
    			Globals.getDisplayManager().popupMessage("Could Not print document.\nMay be the document has no pages.");
    		}
    	}else{
    		Globals.logException(x);
    	}
    }
    
    if(focPrintingConfig != null){
    	focPrintingConfig.fillFromExporter(exporter);
    }

		Globals.setMouseComputing(false);
  }

  public static void printReport(FocJRReportLauncher launcher, FocPrintingConfig focPrintingConfig, String reportTitle, boolean popupDialog){
		Globals.setMouseComputing(true);
		boolean exceptionInExport = false;
  	try{
      JasperPrint print = launcher.fillReport();
      if(print != null){
      	PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(MediaSizeName.ISO_A4);

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);

        if(focPrintingConfig != null) focPrintingConfig.fillExporter(exporter);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, popupDialog ? Boolean.TRUE : Boolean.FALSE);

        try{
        	exporter.exportReport();
        }catch(Exception e){
        	exceptionInExport = true;
        	throw e;
        }
        
        if(focPrintingConfig != null){
        	focPrintingConfig.fillFromExporter(exporter);
        }
      }
    }catch (Exception x){
    	if(exceptionInExport){
    		if(reportTitle != null){
    			Globals.getDisplayManager().popupMessage("Could Not print document "+reportTitle+".\nMay be the document has no pages.");
    		}else{
    			Globals.getDisplayManager().popupMessage("Could Not print document.\nMay be the document has no pages.");
    		}
    	}else{
    		Globals.logException(x);
    	}
    }
		Globals.setMouseComputing(false);
  }

	public static boolean printPreview(FocJRReportLauncher launcher){
		Globals.getDisplayManager().setMouseComputing(true);
	  JasperPrint print = launcher.fillReport();
	  boolean     error = print == null;
	  if(print != null){
	  	Globals.getDisplayManager().goBack();
	    final JasperViewer viewer = new JasperViewer(print, false);
	    viewer.setVisible(true);
	  }
		Globals.getDisplayManager().setMouseComputing(false);
	  return error;
	}

}