package com.adjust.task.state_machine;

import com.adjust.task.model.Value;

interface StateCallBackListener {
        void onStartProcessing();
        void onCheckForData();
        void onDataAvailable(Value val);
        void onNoMoreDataAvailable();
    }