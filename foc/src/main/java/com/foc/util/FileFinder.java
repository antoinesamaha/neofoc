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

import java.io.File;
import java.util.ArrayList;

public class FileFinder {
	
	private String startingDirectory = null;
	private String pattern           = null;
	private int    matchType         = 0;
	
	public static final int MATCH_EQUALS      = 0;
	public static final int MATCH_CONTAINS    = 1;
	public static final int MATCH_STARTS_WITH = 2;
	
	private ArrayList<File> filesFound = null;
	
	public FileFinder(String startingDirectory, String pattern, int matchType){
		this.startingDirectory = startingDirectory;
		this.pattern           = pattern;
		this.matchType         = matchType; 
	}

	public void dispose(){
		this.startingDirectory = null;
		this.pattern           = null;
		filesFound.clear();
		filesFound = null;
	}

	public void find(){
		filesFound = new ArrayList<File>();
		File file = new File(startingDirectory);
		treatFile(file);
	}
	
	public int size(){
		return filesFound != null ? filesFound.size() : 0;
	}
	
	public File get(int i){
		return filesFound != null ? filesFound.get(i) : null;
	}
	
	private void treatFile(File file){
		if(file.isDirectory()){
			String[] str = file.list();
			for(int i=0; i<str.length ;i++){
				File file_I = new File(file.getPath()+"\\"+str[i]);
				treatFile(file_I);
			}
		}else{
			boolean add = false;
			String fileName = file.getPath();
			fileName = fileName.replace('\\', '/');
			switch(matchType){
			case MATCH_EQUALS:
				{
					add = fileName.equals(pattern);
				}
				break;
			case MATCH_CONTAINS:
				{
					add = fileName.contains(pattern);
				}
				break;
			case MATCH_STARTS_WITH:
				{
					add = fileName.startsWith(pattern);
				}
				break;
			}
			if(add){
				filesFound.add(file);
			}
		}
	}
}
