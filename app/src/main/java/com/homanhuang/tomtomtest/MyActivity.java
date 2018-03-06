package com.homanhuang.tomtomtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Homan on 3/3/2018.
 */

public class MyActivity extends AppCompatActivity {

    private Map<Integer, HandlerHolder> resultHandlers = new HashMap<>();
    private AtomicInteger counter = new AtomicInteger(Integer.MIN_VALUE);

    private int getNewRequestCode() {
        return new AtomicInteger().incrementAndGet();
    }

    protected void startActivityForResult(Intent intent, int successResultCode, SuccessResultHandler handler) {
        int requestCode = getNewRequestCode();
        resultHandlers.put(requestCode, new HandlerHolder(successResultCode, handler));
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Intent intent, ResultHandler handler) {
        int requestCode = getNewRequestCode();
        resultHandlers.put(requestCode, new HandlerHolder(handler));
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HandlerHolder handler = resultHandlers.get(requestCode);
        if (handler != null) {
            if (handler.successHandler != null) {
                if (resultCode == handler.successResultCode) {
                    handler.successHandler.onResult(data);
                }
                return;
            }
            if (handler.resultHandler != null) {
                handler.resultHandler.onResult(resultCode, data);
                return;
            }
        }
    }

    public interface SuccessResultHandler {
        void onResult(Intent data);
    }

    public interface ResultHandler {
        void onResult(int resultCode, Intent data);
    }

    protected class HandlerHolder {
        private Integer successResultCode;
        private SuccessResultHandler successHandler;
        private ResultHandler resultHandler;

        public HandlerHolder(Integer successResultCode, SuccessResultHandler successHandler) {
            this.successResultCode = successResultCode;
            this.successHandler = successHandler;
        }

        public HandlerHolder(ResultHandler resultHandler) {
            this.resultHandler = resultHandler;
        }
    }
}
