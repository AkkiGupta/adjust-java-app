package com.adjust.task.model;


public class Value {
    private int value_sec;
    private long value_created_at;
    private String value_status;

    public Value(int value_sec, long value_created_at, String value_status) {
        this.value_sec = value_sec;
        this.value_created_at = value_created_at;
        this.value_status = value_status;
    }

    public long getValue_created_at() {
        return value_created_at;
    }

    public void setValue_created_at(long value_created_at) {
        this.value_created_at = value_created_at;
    }

    public String getValue_status() {
        return value_status;
    }

    public void setValue_status(String value_status) {
        this.value_status = value_status;
    }

    public int getValue_sec() {
        return value_sec;
    }

    public void setValue_sec(int value_sec) {
        this.value_sec = value_sec;
    }
}
