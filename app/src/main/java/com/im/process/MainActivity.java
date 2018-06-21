package com.im.process;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

          starMessengerService();
        // starSocketService();
    }

    private final void insertMessage(String message) {

        TextView text = findViewById(R.id.main_message);
        final CharSequence old = text.getText();
        text.setText(old + "\n" + message);
    }

    private final void starSocketService() {

        final Intent intent = new Intent(getApplicationContext(), SocketService.class);
        startService(intent);

        final EditText input = findViewById(R.id.main_input);
        findViewById(R.id.main_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String str = input.getText().toString();
                input.getText().clear();

                new Thread() {
                    @Override
                    public void run() {

                        // 发送消息
                        try {
                            Socket socket = new Socket("localhost", 8688);
                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                            out.println(str);
                            socket.close();

                            Looper.prepare();
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    insertMessage("用户：发送消息 ==> " + str);
                                }
                            });
                        } catch (IOException e) {
                            Log.e("MainActivity", "starSocketService ==> 接受消息 = " + str);
                        }
                    }
                }.start();
            }
        });

        new Thread() {
            @Override
            public void run() {

                while (true) {
                    // 接收消息
                    try {
                        Socket socket = new Socket("localhost", 8688);
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String str = br.readLine();
                        insertMessage("服务：发送消息 ==> " + str);
                        Log.e("MainActivity", "starSocketService ==> 接受消息 = " + str);
                        socket.close();
                    } catch (IOException e) {
                        Log.e("MainActivity", "starSocketService ==> 接受消息 = " + e.getMessage(), e);
                    }
                }
            }
        }.start();
    }

    private final void starMessengerService() {

        final Intent intent = new Intent(getApplicationContext(), ChatService.class);
        startService(intent);

        final HandlerThread messageThread = new HandlerThread("MessengerThread");
        messageThread.start();

        final Messenger mClientMessenger = new Messenger(new Handler(messageThread.getLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                switch (msg.what) {
                    case ChatService.TYPE_CONN:
                        Log.e("MainActivity", "starMessengerService ==> 接受消息 = " + Thread.currentThread().getName());
                        final String str = msg.getData().get(ChatService.MESSAGE_TEXT).toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                insertMessage(str);
                            }
                        });
                        break;
                    case ChatService.TYPE_MESSAGE:
                        Log.e("MainActivity", "starMessengerService ==> 接受消息 = " + Thread.currentThread().getName());
                        final String str2 = msg.getData().get(ChatService.MESSAGE_TEXT).toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                insertMessage(str2);
                            }
                        });
                        break;
                }
            }
        });

        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, final IBinder service) {
                try {
                    insertMessage("用户：发送消息 ==> 连接成功");
                    Messenger mMessenger = new Messenger(service);
                    Message obtain = Message.obtain();
                    obtain.what = ChatService.TYPE_CONN;
                    obtain.replyTo = mClientMessenger;
                    mMessenger.send(obtain);
                } catch (RemoteException e) {
                }

                final EditText input = findViewById(R.id.main_input);
                findViewById(R.id.main_send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String str = input.getText().toString();
                        input.getText().clear();

                        try {
                            Messenger mMessenger = new Messenger(service);
                            Message obtain = Message.obtain();
                            obtain.what = ChatService.TYPE_MESSAGE;
                            final Bundle bundle = new Bundle();
                            bundle.putString(ChatService.MESSAGE_TEXT, str);
                            obtain.setData(bundle);
                            obtain.replyTo = mClientMessenger;
                            mMessenger.send(obtain);
                            insertMessage("用户：发送消息 ==> " + str);
                            Log.e("MainActivity", "starMessengerService ==> 发送消息 = " + Thread.currentThread().getName());
                        } catch (RemoteException e) {
                        }
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        }, Context.BIND_AUTO_CREATE);
    }
}
