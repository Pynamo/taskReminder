package com.example.taskReminder.form;

import lombok.Getter;

@Getter
public class ChartJsInputDataObject {

    private String x;
    private int y;

    public ChartJsInputDataObject(String x, int y) {
        this.x = x;
        this.y = y;
    }
}
