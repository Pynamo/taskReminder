package com.example.taskReminder.common;

public enum Delete {
	
	VALID("0", "有効"),
	DELETED("1", "削除済");
	
	private String code;
    private String name;
    
    private Delete(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }	
    
    public static Delete getValue(String code) {
    	Delete[] array = values();
    	for(Delete num : array) {
    		if(code.equals(num.getCode())) {
    			return num;
    		}
    	}
    	throw new IllegalArgumentException("undefined : " + code);
    }
}