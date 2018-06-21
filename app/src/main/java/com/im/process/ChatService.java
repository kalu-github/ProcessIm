package com.im.process;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * description: 消息服务
 * created by kalu on 2018/6/12 14:14
 */
public final class ChatService extends Service {

    public final static int TYPE_CONN = 1001;
    public final static int TYPE_MESSAGE = 1002;

    public final static String MESSAGE_TEXT = "message_text";

    @Override
    public void onCreate() {
        Log.e("ChatService", "onCreate ==>");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("ChatService", "onBind ==>");
        return mRemoteMessenger.getBinder();
    }

    @SuppressLint("HandlerLeak")
    private final Messenger mRemoteMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TYPE_CONN:
                    try {
                        SystemClock.sleep(500);
                        final Message message = Message.obtain();
                        message.what = TYPE_MESSAGE;
                        final Bundle bundle = new Bundle();
                        bundle.putString(MESSAGE_TEXT, "服务：发送消息 ==> " + System.currentTimeMillis());
                        message.setData(bundle);
                        message.replyTo = mRemoteMessenger;
                        msg.replyTo.send(message);
                    } catch (Exception e) {
                    }
                    break;
                case TYPE_MESSAGE:
                    try {
                        SystemClock.sleep(500);
                        final Message message = Message.obtain();
                        message.what = TYPE_MESSAGE;
                        final Bundle bundle = new Bundle();
                        bundle.putString(MESSAGE_TEXT, "服务：发送消息 ==> " + System.currentTimeMillis());
                        message.setData(bundle);
                        message.replyTo = mRemoteMessenger;
                        msg.replyTo.send(message);
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    });

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("ChatService", "onStartCommand ==>");
        // return Service.START_STICKY
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e("ChatService", "onStart ==>");
        super.onStart(intent, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("ChatService", "onRebind ==>");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("ChatService", "onUnbind ==>");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ChatService", "onTaskRemoved ==>");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.e("ChatService", "onDestroy ==>");
        super.onDestroy();
    }
}
