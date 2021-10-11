package com.adjust.task.state_machine;

import android.content.Context;

import com.adjust.task.model.Value;

public class Fsm implements StateCallBackListener {

    private int IDLE = 0;
    private int ACTIVE = 1;
    private int Data_CHECK = 2;

    private static Context mContext;

    private static class FsmSingleton {
        private static final Fsm INSTANCE = new Fsm();
    }

    //Private constructor so that no class can use this
    private Fsm() {
    }

    public static Fsm getInstance(Context context) {
        mContext = context;
        return FsmSingleton.INSTANCE;
    }

    private State[] states = {
            new IdleState(this),
            new ActiveState(mContext,this),
            new DataVerificationState(mContext, this)};

    private int[][] transition = {
            {Data_CHECK, IDLE, IDLE, IDLE},
            {ACTIVE, Data_CHECK, ACTIVE, IDLE},
            {Data_CHECK, Data_CHECK, ACTIVE, IDLE}};

    private int current = 0;

    private void nextState(int msg) {
        current = transition[current][msg];
    }

    public void startProcessing() {
        nextState(0);
        states[current].startProcessing();
    }

    public void checkForData() {
        nextState(1);
        states[current].checkForData();
    }

    public void dataAvailable(Value val) {
        nextState(2);
        states[current].dataAvailable(val);
    }

    public void noMoreDataAvailable() {
        nextState(3);
        states[current].noMoreDataAvailable();
    }

    @Override
    public void onStartProcessing() {

    }

    @Override
    public void onCheckForData() {
        checkForData();
    }

    @Override
    public void onDataAvailable(Value val) {
        dataAvailable(val);
    }

    @Override
    public void onNoMoreDataAvailable() {
        noMoreDataAvailable();
    }
}
