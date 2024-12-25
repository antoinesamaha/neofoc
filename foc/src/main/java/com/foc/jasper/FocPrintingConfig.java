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
package com.foc.jasper;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

public class FocPrintingConfig {
	private String printServiceName = null;

	public String getPrintServiceName() {
		return printServiceName;
	}

	public void setPrintServiceName(String printServiceName) {
		this.printServiceName = printServiceName;
	}
	
	public void fillFromExporter(JRPrintServiceExporter exporter){
		PrintService service = exporter.getPrintService();
		printServiceName = service.getName();
	}
	
	public void fillExporter(JRPrintServiceExporter exporter){
		if(printServiceName != null){
			PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
			if(services != null){
				int selectedService = -1;
				/* Scan found services to see if anyone suits our needs */
				for(int i = 0; i < services.length && selectedService < 0;i++){
					if(services[i].getName().equals(printServiceName)){
						/*If the service is named as what we are querying we select it */
						selectedService = i;
					}
				}
	
				if(selectedService >= 0){
					exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, services[selectedService]);
					//exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, services[selectedService].getAttributes());
				}
			}
		}
	}
}
