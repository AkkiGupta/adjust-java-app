package com.adjust.task.state_machine;

public interface OnPostDone {
    void requestDone(Boolean success, String response, int value, int id);
}
