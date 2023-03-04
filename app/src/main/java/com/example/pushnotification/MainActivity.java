package com.example.pushnotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.ClipboardManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = "Push0Notification";
    private static final String CHANNEL_ID = "101";
    private TextView txtToken;
    private String token;

//    private static final String IP = "192.168.1.5";
    private static final String IP = "192.168.1.31";
    private static final int PORT = 9977;
    private Socket socket1;
    private DataOutputStream out1;
    private ImageView imageView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // txtToken = (TextView) findViewById(R.id.txt_token);

        createNotificationChannel();
        // getToken();


        imageView = (ImageView) findViewById(R.id.cam_img);
        button = (Button) findViewById(R.id.send_btn);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessageTask sendMessageTask = new SendMessageTask();
                //sendMessageTask.execute();
                sendMessageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        ImageReceiver imageReceiver = new ImageReceiver();
        imageReceiver.execute(); //
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()) {
                    //Log.d(TAG, "onComplete: Failed token");
                    txtToken.setText(":(");
                }
                token = task.getResult();
                // Log.d(TAG, "onComplete Token: " + token);
                // System.out.println("token " + token);
                txtToken.setText(token);


            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifChannel";
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    private class SendMessageTask extends AsyncTask<Void, Void, Void> {
        // Socket msocket;
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                // InetAddress inet = InetAddress.getByName(IP);
                // msocket = new Socket(inet, PORT);

                // DataOutputStream mout = new DataOutputStream(msocket.getOutputStream());

                // mout.writeBytes("button clicked");
                // mout.flush(); //
                // mout.close(); ////

                //mout.write("button clicked".getBytes());
                //mout.flush(); //
                //mout.close(); ////


                //out1 = new DataOutputStream(socket1.getOutputStream());

                // out1.writeBytes("button clicked");
                // out1.flush(); //
                // out1.close(); ////

                out1.write("Button clicked".getBytes());
                out1.flush(); //
                //out1.close(); ////


                //DataOutputStream out2 = new DataOutputStream(socket1.getOutputStream());

                //out2.writeBytes("button clicked");
                //out2.flush(); //
                // out2.close(); ////

                //out2.write("button clicked".getBytes());
                //out2.flush(); //
                //out2.close(); ////



                // OutputStream out = socket.getOutputStream(); //
                // out.write("button pressed".getBytes());//
                //out1.flush(); ////
                // out1.close(); //////
                // socket.close(); //
                /////// OutputStream out2 = socket1.getOutputStream();
                /////// out2.write("button pressed".getBytes());
                /////// out2.flush(); //
                /////// out2.close(); ////
                // System.out.println("p0rt: " + socket1.getPort())
                // out.close(); //TODO: nie było tego

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

//    private class SendMessageTask extends AsyncTask<String, Void, Void> {
//        // Socket socket2; #
//        @Override
//        protected Void doInBackground(String... params) {
//            try {
//
//                //InetAddress inet = InetAddress.getByName(IP); #
//                // socket2 = new Socket(inet, PORT); #
//
//                // DataOutputStream out3 = new DataOutputStream(socket2.getOutputStream()); #//
//                // out3.writeBytes(params[0]); #//
//                // out3.flush(); #//
//
//                // OutputStream out3 = socket2.getOutputStream(); #////
//                // out3.write(params[0].getBytes()); #////
//
//                // socket2.close(); #
//
//
//                // InetAddress inet = InetAddress.getByName(IP); ##
//                // socket1 = new Socket(inet, PORT); ##
//
//                // out1 = new DataOutputStream(socket1.getOutputStream()); ##//
//                // out1.writeBytes(params[0]); #//
//                // out1.flush(); #//
//
//                // out4 = socket1.getOutputStream(); ##////
//                // out4.write(params[0].getBytes()); ##////
//
//                out4.write(params[0].getBytes());
//
//                // socket1.close(); //// ##
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//    }

    private class ImageReceiver extends AsyncTask<Void, Bitmap, Void> {
        // Socket isocket;

        @Override
        protected Void doInBackground(Void... params) {
            try {


                InetAddress inet = InetAddress.getByName(IP);
                // isocket = new Socket(inet, PORT);
                socket1 = new Socket(inet, PORT);

                out1 = new DataOutputStream(socket1.getOutputStream());

                // out4 = socket1.getOutputStream();

                DataInputStream in = new DataInputStream(socket1.getInputStream());
                //DataInputStream in = new DataInputStream(isocket.getInputStream());

                while (!isCancelled()) { //TODO: while (true) { ?
                    int size = in.readInt();
                    byte[] encodedFrame = new byte[size];
                    in.readFully(encodedFrame);

                    // Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    // publishProgress(bmp);

                    Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(encodedFrame));
                    publishProgress(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            //TODO: super.onProgressUpdate(values);
            super.onProgressUpdate(values);
            imageView.setImageBitmap(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
//                socket.close();
                socket1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

// <?xml version="1.0" encoding="utf-8"?>
/**
 <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
 xmlns:app="http://schemas.android.com/apk/res-auto"
 xmlns:tools="http://schemas.android.com/tools"
 android:layout_width="match_parent"
 android:layout_height="match_parent"
 tools:context=".MainActivity">

 <TextView
 android:id="@+id/txt_token"
 android:layout_width="wrap_content"
 android:layout_height="wrap_content"
 android:rotation="90"
 android:text="Hello World!"
 android:textIsSelectable="true"
 app:layout_constraintBottom_toBottomOf="parent"
 app:layout_constraintLeft_toLeftOf="parent"
 app:layout_constraintRight_toRightOf="parent"
 app:layout_constraintTop_toTopOf="parent"
 tools:visibility="gone" />

 </androidx.constraintlayout.widget.ConstraintLayout>
 */
