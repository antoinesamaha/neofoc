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
package com.foc.cloudStorage;

import java.io.InputStream;
import java.util.Date;

public interface IFocCloudStorage {

	//Buckets in the case of S3
	public Object      getDirectory() throws FocCloudStorageException;
	public void        setDirectory(String directory, boolean createIfNeeded) throws FocCloudStorageException;

	public void        uploadInputStream(String key, InputStream stream) throws FocCloudStorageException;
	public InputStream downloadFile(String key) throws FocCloudStorageException;
	public void        deleteFile(String key) throws FocCloudStorageException;
	
	public boolean     doesFileExist(String key) throws FocCloudStorageException;

}
