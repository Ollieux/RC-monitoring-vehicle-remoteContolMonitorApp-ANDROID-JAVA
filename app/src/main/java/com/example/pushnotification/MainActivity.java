package com.example.pushnotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


public class MainActivity extends AppCompatActivity
{
    private static final String CHANNEL_ID = "101";
    private TextView txtToken;
    private String token;
    public static String address = "";
    public static String ip = "";
    public static int port = 0;
    private Socket socket1;
    private DataOutputStream out1;
    int X;
    int Y;
    int Z;
    private Button btnControl;
    private Button btnRight;
    private Button btnLeft;
    private Button btnCenter;
    private Button btnExit;
    boolean mSending = false;
    boolean mSendingClear = false;
    private SendMessageTask sendMessageTask;
    private ImageReceiver imageReceiver;
    private ImageView imgCam;
    private SensorManager mSensorManager;
    Sensor orientation;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // txtToken = (TextView) findViewById(R.id.txt_token);

        createNotificationChannel();
        // getToken();

        btnControl = (Button) findViewById(R.id.control_btn2);
        btnRight = (Button) findViewById(R.id.right_btn2);
        btnLeft = (Button) findViewById(R.id.left_btn2);
        btnCenter = (Button) findViewById(R.id.center_btn2);
        btnExit = (Button) findViewById(R.id.exit_btn2);

        imgCam = (ImageView) findViewById(R.id.cam_img2);


        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        orientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);


        if (getIntent().hasExtra("address"))
        {
            address = getIntent().getStringExtra("address");
        }

        getIp();


//        ImageReceiver imageReceiver = new ImageReceiver();
        imageReceiver = new ImageReceiver();
        imageReceiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        btnControl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mSending = !mSending;

                if (mSending)
                {
                    mSendingClear = true;
                    sendMessageTask = new SendMessageTask();
                    sendMessageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    btnControl.setText("Stop");
                }
                else
                {
                    btnControl.setText("Start");
                    if(!sendMessageTask.isCancelled())
                        sendMessageTask.cancel(true);
                }


            }
        });

        btnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mSending)
                {
                    sendMessageTask.cancel(true);
                    mSending = false;
                    btnControl.setText("Start");
                }
                sendMessageTask = new SendMessageTask();
                sendMessageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "-5");
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mSending)
                {
                    sendMessageTask.cancel(true);
                    mSending = false;
                    btnControl.setText("Start");
                }
                sendMessageTask = new SendMessageTask();
                sendMessageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "5");
            }
        });

        btnCenter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mSending)
                {
                    sendMessageTask.cancel(true);
                    mSending = false;
                    btnControl.setText("Start");
                }
                sendMessageTask = new SendMessageTask();
                sendMessageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "90");
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSending)
                {
                    sendMessageTask.cancel(true);
                    mSending = false;
                    btnControl.setText("Start");
                }

                imageReceiver.cancel(true);

                finish();
            }
        });
    }

    private void getToken()
    {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>()
        {
            @Override
            public void onComplete(@NonNull Task<String> task)
            {
                if(!task.isSuccessful())
                {
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

    private void getIp()
    {
        String list[] = address.split(":");
        ip = list[0];
        port = Integer.valueOf(list[1]);
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "firebaseNotifChannel";
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... params)
        {
            try
            {
                if (mSending)
                {
                    while (!isCancelled())
                    {
                        try
                        {
                            out1.write(("%" + Integer.toString(Z) + "," + Integer.toString(Y) + "#").getBytes()); //TODO: X, Y zamiast Y, Z
                            Thread.sleep(100);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    out1.write(("$#").getBytes());
                }
                else
                {
                    out1.write((params[0] + "#").getBytes());
                }

                out1.flush();

            } catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class ImageReceiver extends AsyncTask<Void, Bitmap, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                InetAddress inet = InetAddress.getByName(ip);
                socket1 = new Socket(inet, port);

                out1 = new DataOutputStream(socket1.getOutputStream());

                DataInputStream in = new DataInputStream(socket1.getInputStream());

                while (!isCancelled())
                {
                    int size = in.readInt();
                    byte[] encodedFrame = new byte[size];
                    in.readFully(encodedFrame);

                    // Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    // publishProgress(bmp);

                    Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(encodedFrame));
                    publishProgress(bitmap);
                }


            } catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values)
        {
            //TODO: super.onProgressUpdate(values);
            super.onProgressUpdate(values);
            imgCam.setImageBitmap(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            try
            {
                socket1.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            try
            {
                socket1.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private SensorEventListener orientationSensorListener = new SensorEventListener()
    {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            float pitch = event.values[1];
            float roll = event.values[2];
            float azimut = event.values[0];

            X = Math.round(azimut);
            Y = Math.round(pitch);
            Z = Math.round(roll);
        }

    };

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(orientationSensorListener, orientation, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(orientationSensorListener);
    }
}



