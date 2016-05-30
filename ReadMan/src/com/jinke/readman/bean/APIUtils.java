package com.jinke.readman.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 
*@ClassName:APIUtils
*@Description:TODO(what to do)
*@auchor: nk_jinke_yujinyang
*@date:2011-10-26下午07:24:02
*@tags:
 */
public class APIUtils {
	
	public static final String XML_HEAD_TYPE_UTF8 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final String XML_HEAD_TYPE_GBK = "<?xml version=\"1.0\" encoding=\"GBK\"?>";
	
	public static final String GET = "get";
	
	public static final String START_TAG_OUT = "<";
	public static final String START_TAG_IN = " <";
	public static final String START_TAG_N = "</";
	
	public static final String END_TAG = ">";
	public static final String END_TAG_N = ">\n";
	
	public static final String NULL_VALUE = "";
	
	public static final String ERROR_RESULT_TAG_OUT_STATR = "<results>\n";
	public static final String ERROR_RESULT_TAG_IN_START = " <result>";
	public static final String ERROR = "1"; 
	public static final String ERROR_RESULT_TAG_IN_END = "</result>\n";
	public static final String ERROR_RESULT_TAG_OUT_END = "</results>\n";
	
	/**
	 * 
	 *@Title: getXMLModel
	 *@Description: TODO (description the fun)
	 *@return String
	 *@params @param object
	 *@params @return
	 *@throws
	 */
	public static String getXMLModel(Object object){
		if (object == null) {
			return NULL_VALUE;
		}
		StringBuffer  xmlSb = new StringBuffer();
		
		Field[] fields = object.getClass().getDeclaredFields();
		if (fields.length == 0) {
			xmlSb.append(ERROR_RESULT_TAG_OUT_STATR).append(ERROR_RESULT_TAG_IN_START).append(ERROR).append(ERROR_RESULT_TAG_IN_END).append(ERROR_RESULT_TAG_OUT_END);
			return xmlSb.toString();
		}
		Method[] methods = object.getClass().getMethods();
		String ClassName = object.getClass().getSimpleName();
		xmlSb.append(START_TAG_OUT).append(ClassName.toLowerCase()).append(END_TAG_N);
		
		for (int i = 0; i < fields.length; i++) {
			String propertyName = fields[i].getName();
			String propertyGetName = NULL_VALUE;
			if (propertyName != null && !NULL_VALUE.equals(propertyName)) {
				propertyGetName = GET+propertyName.toLowerCase();
			}else {
				return null;
			}
			
			for (int j = 0; j < methods.length; j++) {
				String methodName = methods[j].getName();
				if (methodName != null && !NULL_VALUE.equals(methodName)) {
					methodName = methodName.toLowerCase();
				}else {
					return null;
				}
				
				if (methodName.equals(propertyGetName)) {
					try {
						Object propertyValue = methods[j].invoke(object, null);
						xmlSb.append(START_TAG_IN).append(propertyName).append(END_TAG).append(propertyValue).append(START_TAG_N).append(propertyName).append(END_TAG_N);
					} catch (Exception e) {
						System.out.println(" >>> get method exception");
						e.printStackTrace();
					}
				}
			}
		}
		xmlSb.append(START_TAG_N).append(ClassName.toLowerCase()).append(END_TAG_N);
		return  xmlSb.toString();
	}
}
