package com.im.process;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        @SuppressLint("HandlerLeak") final Messenger mClientMessenger = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ChatService.TYPE_CONN:
                        insertMessage(msg.getData().get(ChatService.MESSAGE_TEXT).toString());
                        break;
                    case ChatService.TYPE_MESSAGE:
                        insertMessage(msg.getData().get(ChatService.MESSAGE_TEXT).toString());
                        break;
                }
            }
        });

        final Intent intent = new Intent(getApplicationContext(), ChatService.class);
        startService(intent);

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

    private final void insertMessage(String message) {

        TextView text = findViewById(R.id.main_message);
        final CharSequence old = text.getText();
        text.setText(old + "\n" + message);
    }
}
