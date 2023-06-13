package com.example.taskReminder.common;

public enum MessageDestination {
	
	FORWARD("0", "フォワード先"),
	REDIRECT("1", "リダイレクト先");
	
	private String code;
    private String name;
    
    private MessageDestination(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }	
    
    public static MessageDestination getValue(String code) {
    	MessageDestination[] array = values();
    	for(MessageDestination num : array) {
    		if(code.equals(num.getCode())) {
    			return num;
    		}
    	}
    	throw new IllegalArgumentException("undefined : " + code);
    }
}