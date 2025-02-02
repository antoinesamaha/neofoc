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

import java.io.InputStream;

import com.foc.Globals;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public class FCloudStoragePropertyResource {

  private FCloudStorageProperty cloudStorageProperty = null;
  
  public FCloudStoragePropertyResource(FCloudStorageProperty property){
  	this.cloudStorageProperty = property;
  }
  
  public void dispose() {
  	cloudStorageProperty = null;
  }
  
//  @Override
//  public DownloadStream getStream() {
//    DownloadStream downloadStream = null;
//
//    if(cloudStorageProperty != null) {
//			if(Utils.isStringEmpty(cloudStorageProperty.getKey())){
//				Globals.logString("DOWNLOAD: newBlobResource 1 Key Empty, generating it");
//				cloudStorageProperty.generateKey();
//			}
//
//			Globals.logString("DOWNLOAD: newBlobResource 1bis Key="+cloudStorageProperty.getKey());
//
//			if(cloudStorageProperty.getDirectory() == null){
//				Globals.logString("DOWNLOAD: newBlobResource 2 Directory null computing it");
//				cloudStorageProperty.setDirectory(Globals.getApp().getCloudStorageDirectory(), false);
//			}
//
//			Globals.logString("DOWNLOAD: newBlobResource 2bis Key="+cloudStorageProperty.getDirectory());
//
//			InputStream is = (InputStream) cloudStorageProperty.getObject();
//
//			if(is == null) Globals.logString("DOWNLOAD: newBlobResource 3 inputStream is null");
//			else Globals.logString("DOWNLOAD: newBlobResource 3bis inputStream is Good");
//
//			Globals.logString("DOWNLOAD: newBlobResource 4 FileName : "+cloudStorageProperty.getFileName());
//
//	    if(is != null){
//	      downloadStream = new DownloadStream(is, "application/x-unknown", cloudStorageProperty.getFileName());
//	      downloadStream.setParameter("Content-Disposition", "attachment; filename=" + DownloadStream.getContentDispositionFilename(cloudStorageProperty.getFileName()));
//	      downloadStream.setCacheTime(0);
//	    }
//    }
//
//    return downloadStream;
//  }
  
//	@Override
	public String getMIMEType() {
		return null;
	}

//	@Override
	public String getFilename() {
		String fileName = "file";
		if(cloudStorageProperty != null) {
			fileName = cloudStorageProperty.getFileName();
		}
		return fileName;
	}
}
