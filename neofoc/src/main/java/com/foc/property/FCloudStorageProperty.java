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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import javax.imageio.ImageIO;

import com.foc.Globals;
import com.foc.cloudStorage.IFocCloudStorage;
import com.foc.desc.FocObject;
import com.foc.desc.field.FCloudStorageField;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public class FCloudStorageProperty extends FBlobMediumProperty {

	private String           key          = null;
	private BufferedImage    imageValue   = null;
	private boolean          isImage      = true;
	
	public static final String DEFAULT_FILE_NAME = "untitled";
	
	public FCloudStorageProperty(FocObject focObj, int fieldID, Blob defaultValue) {
		super(focObj, fieldID, defaultValue);
	}

	public IFocCloudStorage getCloudStorage(){
		IFocCloudStorage cloudStorage = Globals.getApp().getCloudStorage();
		return cloudStorage;
	}
	
	public void setDirectory(String directory, boolean createIfNeeded){
		if(getCloudStorage() != null){
			try{
				getCloudStorage().setDirectory(directory, createIfNeeded);
			}catch(Exception e){
				Globals.logException(e);
			}
		}
	}
	
	public Object getDirectory(){
		Object result = null;
		if(getCloudStorage() != null){

			try{
				if(getCloudStorage().getDirectory() == null){
					getCloudStorage().setDirectory(Globals.getApp().getCloudStorageDirectory(), true);
				}
				
				result = getCloudStorage().getDirectory();
			}catch(Exception e){
				Globals.logException(e);
			}			
		}
		return result;
	}
	
	public String getKey(){
		
		if(Utils.isStringEmpty(key)){
			generateKey();
		}
		
		return key;
	}
	
	public void generateKey(){
		this.key = generateCloudStorageKeyFromFileName();
	}
	
	public void setKey(String key){
		this.key = key;
	}
	
	public void setFileNameInProperty(String fileName){
		FCloudStorageField csField = (FCloudStorageField) getFocField();
		FProperty nameProp = (csField != null && getFocObject() != null) ? getFocObject().getFocProperty(csField.getFileNameFieldID()) : null; 
		if(nameProp != null){
			nameProp.setString(fileName);
		}
	}
	
	public String getFileName(){
		String fileName = DEFAULT_FILE_NAME;
		
		if(getFocObject() != null){
			
			if(getFocField() != null){
				FCloudStorageField csField = (FCloudStorageField) getFocField();
				FProperty nameProp = getFocObject().getFocProperty(csField.getFileNameFieldID()); 
				if(nameProp != null){
					fileName = nameProp.getString();
				}
			}
		}
		
		return fileName;
	}
	
	public String generateCloudStorageKeyFromFileName(){
		String result = "";

		String fileName = getFileName();
		//140428-We are adding this line so that the reference in the path is not negative temporary reference.
		if(getFocObject().isCreated()) getFocObject().validate(true); 
		//140428------------------
		result = getFocObject().getThisFocDesc().getStorageName()+"/"+getFocObject().getReference().getInteger()+"/"+getFocField().getName()+"/"+fileName;

		return result;
	}
	
	public boolean doesFileExist(){
		String fileName = getFileName();
		return !Utils.isStringEmpty(fileName);
		//DO NOT DELETE THIS COMMENT
		//-----------------------------
		//-----------------------------
		//-----------------------------		
		//THIS BELLOW CHECKS THE FILE SYSTEM OR S3
		/*
		boolean exist = false;
		if(!isUploading()){
			if(getCloudStorage() != null){
				
				if(!Utils.isStringEmpty(getKey())){
					try{
						exist = getCloudStorage().doesFileExist(getKey());
					}catch(Exception e){
						Globals.logException(e);
					}
				}
				else{
					Globals.logString("FCloudStorageProperty: Please specify a file name");
				}
			}
			else{
				Globals.logString("FCloudStorageProperty: Could not connect to cloud storage.");
			}
			
		}
		return exist;
		*/
		//-----------------------------
		//-----------------------------
		//-----------------------------		
		//DO NOT DELETE THIS COMMENT
		//THIS ABOVE CHECKS THE FILE SYSTEM OR S3

	}
	
	//Download
	@Override
	public Object getObject() {
		InputStream is = null;
		if(!isUploading()){
			if(getCloudStorage() != null){
				
				if(!Utils.isStringEmpty(getKey())){
					try{
						is = getCloudStorage().downloadFile(getKey());
					}catch(Exception e){
						Globals.logException(e);
					}
				}
				else{
					Globals.logString("FCloudStorageProperty: Please specify a file name");
				}
				
			}
			else{
				Globals.logString("FCloudStorageProperty: Could not connect to cloud storage.");
			}
			
		}
		return is;
	}
	
	//Upload
	@Override
	public void setObject(Object obj) {
		if(obj != null && obj instanceof InputStream){
			if(getCloudStorage() != null){

				if(!Utils.isStringEmpty(getKey())){
					setUploading(true);
					try{
						setImageValue(null);//20151110
						getCloudStorage().uploadInputStream(getKey(), (InputStream) obj);
					}catch(Exception e){
						Globals.logException(e);
					}
					setUploading(false);
				}
				else{
					System.out.println("Please specify a file name");
				}

			}
			else{
				System.out.println("Could not connect to cloud storage.");
			}
		}

	}
	
	public void deleteObject(){
		if(getCloudStorage() != null && getDirectory() != null && !Utils.isStringEmpty(getKey())){
			try{
				getCloudStorage().deleteFile(key);
			}catch(Exception e){
				Globals.logException(e);
			}
		}
	}
	
	public BufferedImage getBufferedImage(){
		try{
			if(getFocObject() != null && (!getFocObject().isCreated() || getFocObject().isContentValid(false))){//2016-01-19 If the image is created we will have a bad key because ref<0 
 				//and if the content not valid it will not save 
				if(getCloudStorage() != null && getCloudStorage().doesFileExist(getKey()) && imageValue == null && isImage){
					if(getCloudStorage() != null && getDirectory() != null && !Utils.isStringEmpty(getKey())){
						InputStream is = (InputStream) getObject();
		
						if(is != null){
							try {
								imageValue = ImageIO.read(is);
								is.close();
								if(imageValue == null){
									isImage = false;
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}catch(Exception e){
			Globals.logException(e);
		}

		return imageValue;
	}
	
	public void setImageValue(BufferedImage image){
		this.imageValue = image;
	}

}
