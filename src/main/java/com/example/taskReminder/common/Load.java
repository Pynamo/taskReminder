package com.example.taskReminder.common;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Load {
	
	LOW("L000", "低"),
	MIDDLE("L001", "中"),
	HIGH("L002", "高");
	
	private String code;
    private String name;
    
    private Load(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }	
    
    public static Load getValue(String code) {
    	Load[] array = values();
    	for(Load num : array) {
    		if(code.equals(num.getCode())) {
    			return num;
    		}
    	}
    	throw new IllegalArgumentException("undefined : " + code);
    }
    
    public static Map<String,String> getLoadData(){
        Map<String, String> load = new LinkedHashMap<String, String>();
        load.put(Load.LOW.getCode(), Load.LOW.getName());
        load.put(Load.MIDDLE.getCode(), Load.MIDDLE.getName());
        load.put(Load.HIGH.getCode(), Load.HIGH.getName());
        return load;
    }  
           
}
	