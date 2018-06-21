package com.im.process;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * description: socket 进程间通信
 * created by kalu on 2018/6/12 14:14
 */
public final class SocketService extends Service {

    public final static int TYPE_CONN = 1001;
    public final static int TYPE_MESSAGE = 1002;

    public final static String MESSAGE_TEXT = "message_text";

    @Override
    public void onCreate() {
        Log.e("SocketService", "onCreate ==>");

        new Thread() {
            @Override
            public void run() {

                try {
                    //监听本地8688端口
                    ServerSocket serverSocket = new ServerSocket(8688);

                    while (true) {
                        // 接受客户端的请求
                        final Socket client = serverSocket.accept();
                        // 用于接受客户端的消息
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String str = in.readLine();
                        Log.e("SocketService", "onCreate ==> 接受消息 = " + str);
                        // 用于向客户端发送消息
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                        final String sendMsg = System.currentTimeMillis() + "";
                        out.println(sendMsg);
                        Log.e("SocketService", "onCreate ==> 发送消息 = " + sendMsg);
                        client.close();
                    }
                } catch (Exception e) {
                    Log.e("SocketService", "onCreate ==> " + e.getMessage(), e);
                }

            }
        }.start();
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("SocketService", "onBind ==>");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("SocketService", "onStartCommand ==>");
        // return Service.START_STICKY
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e("SocketService", "onStart ==>");
        super.onStart(intent, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("SocketService", "onRebind ==>");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("ChatService", "onUnbind ==>");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("SocketService", "onTaskRemoved ==>");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.e("SocketService", "onDestroy ==>");
        super.onDestroy();
    }
}
