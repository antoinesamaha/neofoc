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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import com.foc.Globals;

public class Utils {

	public static boolean paramSanityCheck(String param) {
		boolean error = false;
		if(param != null && param.contains("'")) {
			error = true;
			Globals.logString("Sanity Check "+param);
		}
		return error;
	}
	
	public static boolean isStringEmpty(String input){
		return input == null || input.isEmpty();
	}
	
	public static boolean isEmail(String input){
		boolean error = isStringEmpty(input);
		if(!error){
			error = !input.contains("@") || !input.contains(".");
			if(!error){
				error = input.trim().contains(" ");
			}
		}
		return !error;
	}

	public static boolean isEqual_String(String str1, String str2){
		boolean equal = str1==null && str2==null;
		if(!equal && str1 != null && str2 != null){
			try{
				String dblStr1 = str1.replace(",", "");
				String dblStr2 = str2.replace(",", "");

				Double dbl1 = Double.valueOf(dblStr1);
				Double dbl2 = Double.valueOf(dblStr2);
				equal = dbl1.doubleValue() == dbl2.doubleValue();
			}catch(Exception e){

			}
			if(!equal) equal = str1.equals(str2);
		}
		return equal;
	}


	public static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally {
			stream.close();
		}
	}

	private static final String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
	private static final Pattern pattern = Pattern.compile(regExp);
	
	public static boolean isNumeric(String text){
    Matcher m = pattern.matcher(text);
    return m.matches();
/*
		boolean result = true;

		try{
			Double.parseDouble(text);
		}catch(NumberFormatException e){
			result = false;
		}
		return result;
		*/
	}
	
	public static boolean isInteger(String text){
		boolean result = true;

		try{
			Integer.valueOf(text);
		}catch(NumberFormatException e){
			result = false;
		}
		return result;
	}

	public static long parseLong(String text, long defaultValue){
		long result = defaultValue;
		try{
			result = Long.valueOf(text);
		}catch(NumberFormatException e){
		}
		return result;		
	}
	
	public static long[] parseLongs(String[] texts, long defaultValue){
		long[] longs = null;
		if (texts != null) {
			longs = new long[texts.length];
			for (int i=0; i<texts.length; i++) {
				longs[i] = Utils.parseLong(texts[i], defaultValue);
			}
		}
		return longs;
	}

	public static int parseInteger(String text, int defaultValue){
		int result = defaultValue;
		try{
			result = Integer.valueOf(text);
		}catch(NumberFormatException e){
		}
		return result;		
	}
	
	public static int[] parseIntegers(String[] texts, int defaultValue){
		int[] ints = null;
		if (texts != null) {
			ints = new int[texts.length];
			for (int i=0; i<texts.length; i++) {
				ints[i] = Utils.parseInteger(texts[i], defaultValue);
			}
		}
		return ints;		
	}
	
	public static double parseDouble(String text, double defaultValue){
		double result = defaultValue;
		try{
			result = Double.valueOf(text);
		}catch(NumberFormatException e){
		}
		return result;		
	}

	public static boolean isMobile(String userAgent){
		boolean result = false;

		final String[] mobileSpecificSubscriptions = {
				"iPhone","iPad","Android","MIDP","Opera Mobi",
				"Opera Mini","BlackBerry","HP iPAQ","IEMobile",
				"MSIEMobile","Windows Phone","HTC","LG",
				"MOT","Nokia","Symbian","Fennec",
				"Maemo","Tear","Midori","armv",
				"Windows CE","WindowsCE","Smartphone","240x320",
				"176x220","320x320","160x160","webOS",
				"Palm","Sagem","Samsung","SGH",
				"SonyEricsson","MMP","UCWEB"};//"SIE" This makes IE10 think it is Mobile

		if(!Utils.isStringEmpty(userAgent)){

			for(int i=0; i<mobileSpecificSubscriptions.length; i++){
				if(userAgent.contains(mobileSpecificSubscriptions[i])){
					result = true;
					break;
				}
			}
		}

		return result;
	}

	public static String htmlToText(String html){
		String val = html.replace("<div><br></div>","[LINE_BREAK]");
		if(val.startsWith("<div>")){
			val = val.replaceFirst("<div>", "");
		}
		val = val.replace("<br>","[LINE_BREAK]");
		val = val.replace("\n","[LINE_BREAK]");
		val = val.replace("<div>","[LINE_BREAK]");
		val = val.replace("&nbsp;","[SPACE]");
		val = val.replace("&#160;","[SPACE]");
		val = Jsoup.parse(val).text();
		val = val.replace("[LINE_BREAK]","\n");
		val = val.replace("[SPACE]"," ");
		return val;
		
		/*
		if(html.startsWith("<div>")){
			html = html.replaceFirst("<div>", "");
		}
		html = html.replaceAll("\n", "NEW_LINE");
		html = html.replaceAll("<div>", "NEW_LINE");
		html = Jsoup.parse(html).text();
		html = html.replaceAll("NEW_LINE", "\n");
		return html;
	  */
	}
	
	public static String inputStreamToString(InputStream in) throws IOException{
		StringBuilder out = new StringBuilder();
		try{
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);
	        out.append("\n");
	    }
		}catch(Exception e){
			e.printStackTrace();
		}
	  return out.toString();
	}
	
	public static InputStream stringToinputStream(String str) {
		InputStream stream = null;
		try{
			if(str != null) {
				stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	  return stream;
	}
	
	public static int compareObjects_NullityOnly(Object o1, Object o2){
		int comp = 0;
		if(o1 == null && o2 != null){
			comp = -1;
		}else if(o1 != null && o2 == null){
			comp = 1;
		}
		return comp;
	}
	
	public static int compareStrings(String s1, String s2){
		int comp = 0;
		if(s1 != null && s2 == null){
			comp = 1 ;
		}else if(s1 == null && s2 != null){
			comp = -1;
		}else if(s1 != null && s2 != null){
			comp = s1.compareTo(s2);
		}
		return comp;
	}

	public static byte[] compressByteArray(byte[] bytes){
		ByteArrayOutputStream baos = null;
		Deflater dfl = new Deflater();
		dfl.setLevel(Deflater.BEST_COMPRESSION);
		dfl.setInput(bytes);
		dfl.finish();
		baos = new ByteArrayOutputStream();
		byte[] tmp = new byte[4*1024];
		try{
			while(!dfl.finished()){
				int size = dfl.deflate(tmp);
				baos.write(tmp, 0, size);
			}
		} catch (Exception ex){
      
		} finally {
			try{
				if(baos != null) baos.close();
			} catch(Exception ex){
			}
		}
  
 		return baos.toByteArray();
	}

	public static byte[] decompressByteArray(byte[] bytes){
	  ByteArrayOutputStream baos = null;
	  Inflater iflr = new Inflater();
	  iflr.setInput(bytes);
	  baos = new ByteArrayOutputStream();
	  byte[] tmp = new byte[4*1024];
	  try{
	    while(!iflr.finished()){
        int size = iflr.inflate(tmp);
        baos.write(tmp, 0, size);
	    }
	  } catch (Exception ex){
	  	Globals.logException(ex);  
	  } finally {
	    try{
	    	if(baos != null) baos.close();
	    } catch(Exception ex){}
	  }
	   
	  return baos.toByteArray();
	}

	public static String compressString(String in) {
		String out = "";
		
		if(in != null) {
			try{
//				byte[] content = compressByteArray(in.getBytes("UTF-8"));
//				out = new String(content, "ISO-8859-1");

				byte[] content = compressByteArray(in.getBytes("UTF-8"));
				byte[] base64Encoded = Base64.getEncoder().encode(content);
				out = new String(base64Encoded, "ISO-8859-1");
				
			}catch (Exception e){
				Globals.logExceptionWithoutPopup(e);
			}
		}
		
		return out;
	}
	
	public static String decompressString(String zipped){
		String unzipped = "";
    if(zipped != null) {
    	try {
    		if(!Utils.isStringEmpty(zipped)){
	    		byte[] encodedBytes = zipped.getBytes("ISO-8859-1");
	    		byte[] bytes = Base64.getDecoder().decode(encodedBytes);
	    		byte[] decom = decompressByteArray(bytes);
	    		unzipped = new String(decom, "UTF-8");
	//		    byte[] decom = decompressByteArray(zipped.getBytes("ISO-8859-1"));
	//		    unzipped = new String(decom, "UTF-8");
    		}
			}catch (Exception e){
				Globals.logString("Exception while Utils.decompressString() : "+e.getMessage());
				unzipped = zipped;
    	}
    }
    		
    return unzipped;
	}
	
	public static String getCurrentMethodName() {
		return getCurrentMethodName(1);
	}
	
	public static String getCurrentMethodName(int depth) {
		String name = "";
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		if(ste != null && ste.length > 2+depth) {
			name = ste[2+depth].getMethodName();
		}
		return name;
	}
	
	public static void main(String[] args) {
		String in = "{\"Field1\":\"";
		in += "الاسم";
		in += "\"}";
		
		in="{\"REF\":\"4048\",\"SITE\":\"التفتيش المركزي\",\"STATUS\":\"Proposal\",\"CREATTION_DATE\":\"10/09/2018 15:01\",\"VALIDATION_DATE\":\"\",\"CLOSURE_DATE\":\"\",\"CREATION_USER\":\"01BARMAJA\",\"LAST_MODIF_DATE\":\"\",\"LAST_MODIF_USER\":\"0\",\"WF_CURRENT_STAGE\":\"0\",\"WF_CANCELED\":\"0\",\"WF_CANCEL_REASON\":\"\",\"WF_LAST_COMMENT\":\"\",\"WF_COMMENT\":\"\",\"TITLE_1\":\"0\",\"TITLE_2\":\"0\",\"TITLE_3\":\"0\",\"WF_HIDE_1\":\"0\",\"WF_HIDE_2\":\"0\",\"WF_HIDE_3\":\"0\",\"ALL_SIGNATURES\":\"\",\"SIMULATION\":\"0\",\"CODE\":\"01051\",\"DATE\":\"10/09/2018\",\"EXTERNAL_CODE\":\"\",\"ACCUSATION\":\"0\",\"ACCUSATION_DESCRIPTION\":\"NEW FROM MINISTRY\",\"ACCUSATION_SUMMARY\":\"\",\"COMPLAINT_SOURCE\":\"0\",\"COMPLAINT_PURPOSE\":\"\",\"VIOLATION_DATE\":\"\",\"VIOLATION_TIME\":\"00:00\",\"VIOLATION_ADDRESS\":\"\",\"HUMAN_RIGHTS\":\"0\",\"HRIGHTS\":\"\",\"INVESTIGATOR\":\"0\",\"ComplaintStatus\":\"قيد التحقيق\",{\"REF\":\"-1\",\"ORDER_FLD\":\"1\",\"COMPLAINT\":\"\",\"FROM_FACILITY\":\"\",\"TO_FACILITY\":\"\",\"REFERRAL_NUMBER\":\"\",\"REFERRAL_DATE\":\"\"},{\"REF\":\"-2\",\"ORDER_FLD\":\"2\",\"COMPLAINT\":\"\",\"FROM_FACILITY\":\"\",\"TO_FACILITY\":\"\",\"REFERRAL_NUMBER\":\"\",\"REFERRAL_DATE\":\"\"},{\"REF\":\"-3\",\"ORDER_FLD\":\"3\",\"COMPLAINT\":\"\",\"FROM_FACILITY\":\"\",\"TO_FACILITY\":\"\",\"REFERRAL_NUMBER\":\"\",\"REFERRAL_DATE\":\"\"},{\"REF\":\"-4\",\"ORDER_FLD\":\"4\",\"COMPLAINT\":\"\",\"FROM_FACILITY\":\"\",\"TO_FACILITY\":\"\",\"REFERRAL_NUMBER\":\"\",\"REFERRAL_DATE\":\"\"},{\"REF\":\"-5\",\"ORDER_FLD\":\"5\",\"COMPLAINT\":\"\",\"FROM_FACILITY\":\"\",\"TO_FACILITY\":\"\",\"REFERRAL_NUMBER\":\"\",\"REFERRAL_DATE\":\"\"},{\"REF\":\"-6\",\"ORDER_FLD\":\"6\",\"COMPLAINT\":\"\",\"FROM_FACILITY\":\"\",\"TO_FACILITY\":\"\",\"REFERRAL_NUMBER\":\"\",\"REFERRAL_DATE\":\"\"},{\"REF\":\"-7\",\"ORDER_FLD\":\"7\",\"COMPLAINT\":\"\",\"FROM_FACILITY\":\"\",\"TO_FACILITY\":\"\",\"REFERRAL_NUMBER\":\"\",\"REFERRAL_DATE\":\"\"},{\"REF\":\"-8\",\"ORDER_FLD\":\"8\",\"COMPLAINT\":\"\",\"FROM_FACILITY\":\"\",\"TO_FACILITY\":\"\",\"REFERRAL_NUMBER\":\"\",\"REFERRAL_DATE\":\"\"},{\"REF\":\"-9\",\"ORDER_FLD\":\"9\",\"COMPLAINT\":\"\",\"FROM_FACILITY\":\"\",\"TO_FACILITY\":\"\",\"REFERRAL_NUMBER\":\"\",\"REFERRAL_DATE\":\"\"}}";
		
		try {
			String compressed = compressString(in);
			String deCompressed = decompressString(compressed);
			
			byte[] inBytes = in.getBytes("UTF-8");
			byte[] compressedBytes = compressByteArray(inBytes);
			byte[] outBytes = decompressByteArray(compressedBytes);
			
			String in2 = new String(inBytes);
//			String temp = new String(b, "UTF-8");
			String out2 = new String(outBytes);
			
			int debug = 0;
			debug++;
		}catch(Exception e) {
			e.printStackTrace();
		}

		/*
		System.out.println("in:"+in);
		String temp = compressString(in);
		System.out.println("temp:"+temp);
		String out = decompressString(temp);
		System.out.println("out:"+out);
		 */
	}
	
	public static String convertIndianNumberstoArabic(String result) {
		if (result != null) {
			result = result.replace("١", "1");
			result = result.replace("٢", "2");
			result = result.replace("٣", "3");
			result = result.replace("٤", "4");
			result = result.replace("٥", "5");
			result = result.replace("٦", "6");
			result = result.replace("٧", "7");
			result = result.replace("٨", "8");
			result = result.replace("٩", "9");
			result = result.replace("٠", "0");
		}
		return result;
	}

	public static String replaceArabicAmbiguityLettersWithPercent(String text) {
		text = text.replace("ة", "%");
		text = text.replace("ه", "%");
		text = text.replace("ا", "%");
		text = text.replace("أ", "%");
		text = text.replace("آ", "%");
		text = text.replace("ء", "%");
		return text; 
	}
	
	/**
	 * Get a param from a JSONObject, null if not present
	 * @param jsonObj
	 * @param param
	 * @return String
	 */
	public static String getCleanJsonParam(JSONObject jsonObj, String param) {
		//optString gets the param if exists or null if not.
		if(jsonObj!=null) {
			String paramString = jsonObj.optString(param,null);
			if(!Utils.isStringEmpty(paramString)) {
				paramString=paramString.trim();
				if(!Utils.isStringEmpty(paramString))
				return paramString;		
			}			
		}
		return null;
	
	}
	
	/**
	 * Get a param from a HttpServletRequest, null if not present
	 * @param request
	 * @param param
	 * @return String
	 */
	public static String getCleanRequestParam(HttpServletRequest request,String param) {
		
		String paramString = request != null ? request.getParameter(param) : null;
		if(!Utils.isStringEmpty(paramString)) {
			paramString=paramString.trim();
			if(!Utils.isStringEmpty(paramString))
			return paramString;	
		}		
		return null;

	}
	
	/***
	 * Injects parameters into template producing a content message. warns about
	 * parameters with no values passed parameters are case insensitive
	 * 
	 * @param template
	 * @param placeholdersMap
	 * @return
	 */
	public static String replacePlaceholders(String text, Map<String, Object> placeholdersMap) {
//		final Pattern PARAM_PATTERN = Pattern.compile("(\\$\\{.*\\})");
		final String PARAM_PREFIX = "${";
		final String PARAM_SUFFIX = "}";

		String result = text;
		if (placeholdersMap!= null)
			for(Entry<String, Object> paramVal : placeholdersMap.entrySet()){
				result = result.replaceAll("(?i)" + Pattern.quote(PARAM_PREFIX + paramVal.getKey() + PARAM_SUFFIX), paramVal.getValue() == null ? "" : Matcher.quoteReplacement(String.valueOf(paramVal.getValue())));
			}
		// check if there are any remaining parameters not received.
//		Matcher matcher = PARAM_PATTERN.matcher(result);
//		while (matcher.find()) {
//			Globals.logString("Warning: Parameter " + matcher.group(1) + " missing from Text: " + text);
//		}

		return result;
	}

}
