package com.example.taskReminder.common;

public enum Delete {
	
	DELETED("1");
	
	private String code;

    private Delete(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }      
}