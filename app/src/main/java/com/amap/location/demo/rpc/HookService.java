package com.amap.location.demo.rpc;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;

/**
 * @author maple on 2019/5/11 12:00.
 * @version v1.0
 * @see 1040441325@qq.com
 * Messenger实现进程间通信(双向)
 */
public class HookService extends Service {
    public static final String TAG = "HookService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Log.i(TAG, "handleMessage: ");
                Bundle reply = new Bundle();
                reply.putString("loc", "messenger通信完成");
                Message msgToClient = Message.obtain(msg);
                msgToClient.setData(reply);
                msg.replyTo.send(msgToClient);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

}
