package com.adjust.task.state_machine;

import com.adjust.task.model.Value;

abstract class State {
    public void startProcessing() {
        System.out.println("startProcessing");
    }

    public void checkForData() {
        System.out.println("checkForMoreData");
    }

    public void dataAvailable(Value val) {
        System.out.println("dataAvailable");
    }

    public void noMoreDataAvailable() {
        System.out.println("noMoreDataAvailable");
    }
}