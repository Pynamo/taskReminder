package com.example.taskReminder.common;

public enum MessageAlertLevel {
	
	SUCCESS("alert-danger", "成功"),
	WARNING("alert-warning", "警告"),
	ERROR("alert-danger", "エラー");
	
	private String code;
    private String name;
    
    private MessageAlertLevel(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }	
    
    public static MessageAlertLevel getValue(String code) {
    	MessageAlertLevel[] array = values();
    	for(MessageAlertLevel num : array) {
    		if(code.equals(num.getCode())) {
    			return num;
    		}
    	}
    	throw new IllegalArgumentException("undefined : " + code);
    }
}