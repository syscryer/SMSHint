package com.example.sonw.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private SmsObserver smsObserver;
    List<Message> smsList;
    MediaPlayer player;

    public void playAlarm() {
        player = new MediaPlayer();
        player.stop();
        try {
            Uri pickUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            player.setDataSource(getApplicationContext(), pickUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setLooping(true);
            try {
                player.prepare();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            player.start();
        }
    }

    public void click(View v) {
        //访问内容提供者获取短信
        ContentResolver cr = getContentResolver();
        //                        短信内容提供者的主机名
        Cursor cursor = cr.query(Uri.parse("content://sms"), new String[]{"address", "date", "body", "type"},
                null, null, null);
        while (cursor.moveToNext()) {
            String address = cursor.getString(0);
            long date = cursor.getLong(1);
            String body = cursor.getString(2);
            String type = cursor.getString(3);
            Message sms = new Message(body, type, address, date);
            smsList.add(sms);
            Log.e("TAG", sms.toString());
        }
    }

    public void click1(View v) {
        System.exit(0);
        if (player != null)
            player.stop();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smsObserver = new SmsObserver(this, smsHandler);
        getContentResolver().registerContentObserver(SMS_INBOX, true,
                smsObserver);
        smsList = new ArrayList<Message>();
    }

    public Handler smsHandler = new Handler() {
        //这里可以进行回调的操作
        //TODO
        int i = 33;
    };

    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //每当有新短信到来时，使用我们获取短消息的方法
            try {
                getSmsFromPhone();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private Uri SMS_INBOX = Uri.parse("content://sms/");
    public void getSmsFromPhone() throws Exception {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"address", "date", "body", "type"};//"_id", "address", "person",, "date", "type
//        String where = " address = '1065502005903380' AND date >  "
//                + (System.currentTimeMillis() - 10 * 60 * 1000);
        String where = " date >  "
                + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String address = cur.getString(0);
            long date = cur.getLong(1);
            String body = cur.getString(2);
            String type = cur.getString(3);
            TextView helloText = (TextView) findViewById(R.id.mobileText);
            helloText.setText(body);
            if (body.contains("NOT OK.")) {
                playAlarm();
                startLocalApp("com.example.sonw.myapplication");
                //Thread.sleep(1000);

                /**最后将被挤压到后台的本应用重新置顶到最前端
                 * 当自己的应用在后台时，将它切换到前台来*/
                SystemHelper.setTopApp(MainActivity.this);
            }
        }
    }

    private void startLocalApp(String packageNameTarget) {

        Log.i("Wmx logs::", "-----------------------开始启动第三方 APP=" + packageNameTarget);

        if (SystemHelper.appIsExist(MainActivity.this, packageNameTarget)) {
            PackageManager packageManager = getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageNameTarget);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

            /**android.intent.action.MAIN：打开另一程序
             */
            intent.setAction("android.intent.action.MAIN");
            /**
             * FLAG_ACTIVITY_SINGLE_TOP:
             * 如果当前栈顶的activity就是要启动的activity,则不会再启动一个新的activity
             */
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "被启动的 APP 未安装", Toast.LENGTH_SHORT).show();
        }
    }
}
