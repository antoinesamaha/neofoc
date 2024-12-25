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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;

@SuppressWarnings("serial")
public class FImageProperty extends FBlobMediumProperty {

	private BufferedImage imageValue = null ;
	private boolean       loaded     = false;
	
	private static final double MAX_DIMENSION = 841;
  
  public FImageProperty(FocObject focObj, int fieldID) {
    super(focObj, fieldID, null);
    loaded = false;
  }
    
	public BufferedImage getImageValue() {
		loadImageFromDatabaseIfNeeded();
		return imageValue;
	}

	public void setImageValue(BufferedImage img) {
//		imageValue = img;
		imageValue = resizeUploadedImage(img);
		updateImageInDatabase();
	}
		
	public void setImageValue(String fullPath) {
  	try{
  		File file = new File(fullPath);
  		this.imageValue = ImageIO.read(file);
  		loaded = true;
  		updateImageInDatabase(file);
    }catch(Exception x){
  		Globals.logException(x);
  	}
	}

	@Override
  public Object getObject() {
    return getImageValue();
  }
  
	public void loadImageFromDatabaseIfNeeded(){
		if(loaded == false && imageValue == null){
	  	FocObject focObject      = getFocObject();
	  	FocDesc   focDesc        = focObject != null ? focObject.getThisFocDesc() : null;
	
	    if(focDesc != null && focObject != null && focObject.hasRealReference()){
	    	loaded     = true;
	    	imageValue = Globals.getApp().getDataSource().focObject_LoadImage(focObject, getFocField() != null ? getFocField().getID() : FField.NO_FIELD_ID);
	    }
		}
	}
	
  public void updateImageInDatabase(File file){
  	FocObject focObject      = getFocObject();
  	FocDesc   focDesc        = focObject != null ? focObject.getThisFocDesc() : null;

    if(focDesc != null && focObject != null && focObject.hasRealReference()){
    	Globals.getApp().getDataSource().focObject_UpdateImage(focObject, getFocField().getID(), file);
    }
  }
  
  public void updateImageInDatabase(){
  	FocObject focObject      = getFocObject();
  	FocDesc   focDesc        = focObject != null ? focObject.getThisFocDesc() : null;

    if(focDesc != null && focObject != null && focObject.hasRealReference()){
    	Globals.getApp().getDataSource().focObject_UpdateImage(focObject, getFocField().getID());
    }
  }
  
  public void setModified(boolean modified) {
  	super.setModified(modified);
  }
  
  private BufferedImage resizeUploadedImage(BufferedImage originalImage){
    try{
      int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
      int imageOriginalWidth  = originalImage.getWidth();
      int imageOriginalHeight = originalImage.getHeight();
      if(imageOriginalWidth > MAX_DIMENSION || imageOriginalHeight > MAX_DIMENSION){
        int maxSize = 0;
        
        if(imageOriginalWidth > imageOriginalHeight){
          maxSize = imageOriginalWidth; 
        }else{
          maxSize = imageOriginalHeight;
        }
        double ratio =  MAX_DIMENSION / maxSize;
        
        int newImageWidth  = (int)(imageOriginalWidth  * ratio);
        int newImageHeight = (int)(imageOriginalHeight * ratio);
        
        originalImage = resizeImage(originalImage, type, newImageWidth, newImageHeight);
      }
    }catch(Exception e){
      Globals.showNotification("Please reupload your image", "Image cannot be uploaded", IFocEnvironment.TYPE_ERROR_MESSAGE);
    }
    return originalImage;
  }
  
  private static BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height){
    BufferedImage resizedImage = new BufferedImage(width, height, type);
    Graphics2D g = resizedImage.createGraphics();
    g.drawImage(originalImage, 0, 0, width, height, null);
    //g.dispose();
    
    return resizedImage;
  }
}
