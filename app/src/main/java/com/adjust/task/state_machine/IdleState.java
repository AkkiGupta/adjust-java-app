package com.adjust.task.state_machine;

import com.adjust.task.model.Value;

class IdleState extends State {
    StateCallBackListener mListener;

    private IdleState() {}

    public IdleState(StateCallBackListener listener) {
        mListener = listener;
    }

    public void startProcessing() {
        System.out.println("IdleState::startProcessing No Action Needed");
    }

    public void checkForData() {
        System.out.println("IdleState::checkForMoreData No Action Needed");
    }

    public void dataAvailable(Value val) {
        System.out.println("IdleState::dataAvailable No Action Needed");
    }

    public void noMoreDataAvailable() {
        System.out.println("IdleState::noMoreDataAvailable No Action Needed");
    }
}