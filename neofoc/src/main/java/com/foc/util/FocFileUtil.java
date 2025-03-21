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
package com.foc.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FocFileUtil {

	public static void saveStreamToFile(InputStream in, String fileName){
		try {
			OutputStream out = new FileOutputStream(fileName);
		  byte[] buf = new byte[1024];
		  int len;
		  while((len=in.read(buf))>0){
		    out.write(buf,0,len);
		  }
		  out.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	public static byte[] inputStreamToByteArray(InputStream inputStream){
		ByteArrayOutputStream byteArrayOutputStream = inputStreamToByteArrayOutputStream(inputStream);
		return byteArrayOutputStream.toByteArray();
	}
	
	public static ByteArrayOutputStream inputStreamToByteArrayOutputStream(InputStream inputStream){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try{
			int byteRead;
			while((byteRead = inputStream.read()) != -1) {
				byteArrayOutputStream.write(byteRead);
			}
			byteArrayOutputStream.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
		return byteArrayOutputStream;
	}
}
