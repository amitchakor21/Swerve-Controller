package com.example.withcontroller;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener, View.OnTouchListener, View.OnClickListener{
//    public static final String TAG = "MainActivity";

    public static final boolean OFF = false;
    public static final boolean ON = true;

    public static final int DEFAULT_THREAD_POOL_SIZE = 2;
    public static final int PACKET_SIZE = 7;
    public static final int ANGLE = 2;
    public static final int CLICKBUTTONS = 3;
    public static final int LEFTSEEKBAR = 4;
    public static final int TOUCHBUTTONS = 5;
    public static final int RIGHTSEEKBAR = 6;

    //    TextView JoystickX, JoystickY;
//    TextView TextA, TextB, TextC, TextD, TextE;
//    TextView textView_leftSeekbar, textView_rightSeekbar;
//    TextView Value;

    EditText pwm1edittxt, pwm2edittxt, pwm3edittxt;
    Button pwm1btn, pwm2btn, pwm3btn;

    TextView resultTextView;
    Button buttonA, buttonB, buttonC, buttonD, buttonE, buttonF, buttonG;
    Button Left, Right;
    Button Connect, Disconnect;
    SeekBar FirstSeekbar, SecondSeekbar;
    EditText IPAddress, Port;
    ExecutorService executorService;
    Socket socket;
    PrintWriter printWriter = null;
    String IP;
    int port;
    int direction, pwmValue;
    boolean reverseOFF_ON, reverse;
    float Angle, prevAngle;
    int lProgress, rProgress;
    //    char Byte1, Byte2, Byte3, Byte4, Byte5;
    boolean SendingInProgress;
    String result, prevresult;
    char prevByte[] = new char[PACKET_SIZE];
    char currentByte[] = new char[PACKET_SIZE];
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSystemView();

/*        JoystickX = (TextView) findViewById(R.id.JoyX);
        JoystickY = (TextView) findViewById(R.id.JoyY);
        TextA = (TextView) findViewById(R.id.textview_A);
        TextB = (TextView) findViewById(R.id.textview_B);
        TextC = (TextView) findViewById(R.id.textview_C);
        TextD = (TextView) findViewById(R.id.textview_D);
        TextE = (TextView) findViewById(R.id.textview_E);*/
        resultTextView = (TextView) findViewById(R.id.resultText);
//        Value = (TextView) findViewById(R.id.value);
        buttonA = (Button) findViewById(R.id.buttonA);
        buttonB = (Button) findViewById(R.id.buttonB);
        buttonC = (Button) findViewById(R.id.buttonC);
        buttonD = (Button) findViewById(R.id.buttonD);
//        buttonE = (Button) findViewById(R.id.buttonE);
//        buttonF = (Button) findViewById(R.id.buttonF);
        buttonG = (Button) findViewById(R.id.buttonG);
        Left = (Button) findViewById(R.id.left);
        Right = (Button) findViewById(R.id.right);
//        Up = (Button) findViewById(R.id.Button_UP);
//        Down = (Button) findViewById(R.id.Button_DOWN);
        Connect = (Button) findViewById(R.id.connect);
        Disconnect = (Button) findViewById(R.id.disconnect);
        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
//        pwm1edittxt = (EditText) findViewById(R.id.pwm1edittext);
//        pwm1btn = (Button) findViewById(R.id.pwm1button);
//        pwm2edittxt = (EditText) findViewById(R.id.pwm2edittext);
//        pwm2btn = (Button) findViewById(R.id.pwm2button);
        pwm3edittxt = (EditText) findViewById(R.id.pwm3edittext);
        pwm3btn = (Button) findViewById(R.id.pwm3button);
        IPAddress = (EditText) findViewById(R.id.ipaddress);
//        Port = (EditText) findViewById(R.id.portnumber);
        IPAddress.setText("192.168.43.42");
//        Port.setText("1336");
//        ConnectionStatus = (TextView) findViewById(R.id.connectStatus);
//        textView_leftSeekbar = (TextView) findViewById(R.id.leftSeekbarValue);
//        textView_rightSeekbar = (TextView) findViewById(R.id.rightSeekbarValue);
/*        FirstSeekbar = (SeekBar) findViewById(R.id.seekbar1);
        SecondSeekbar = (SeekBar) findViewById(R.id.seekbar2);
        FirstSeekbar.setProgress(0);
        SecondSeekbar.setProgress(0);
        FirstSeekbar.setMax(100);
        SecondSeekbar.setMax(255);*/
        direction = 0;
        pwmValue = 0;
        reverseOFF_ON = ON;
        reverse = OFF;
        Angle = prevAngle = -1;
        lProgress = rProgress = 0;
//        Byte1 = Byte2 = Byte3 = Byte4 = Byte5 = 0;
        prevByte[0] = currentByte[0] = 0x55;
        prevByte[1] = currentByte [1] = 0xAA;
        for (i = 2; i < PACKET_SIZE; i++)
            prevByte[i] = currentByte[i] = 0;

        prevByte [CLICKBUTTONS] = currentByte [CLICKBUTTONS] ^= 0x10;
        SendingInProgress = false;
        prevresult = result = "";

/*        FirstSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (lProgress < 16)
                    lProgress = 0;
                else
                {
                    if ((lProgress >= 16) && (lProgress < 49))
                        lProgress = 33;
                    else
                    {
                        if ((lProgress >= 49) && (lProgress < 83))
                            lProgress = 67;
                        else
                            lProgress = 100;
                    }
                }
                FirstSeekbar.setProgress(lProgress);
                currentByte[LEFTSEEKBAR] = (char) (lProgress);
                uploadData();
            }
        });

        SecondSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentByte[RIGHTSEEKBAR] = (char) rProgress;
                uploadData();
            }
        });
*/
        Left.setOnTouchListener(this);
        Right.setOnTouchListener(this);
//        Up.setOnTouchListener(this);
//        Down.setOnTouchListener(this);

//        pwm1btn.setOnClickListener(this);
//        pwm2btn.setOnClickListener(this);
        pwm3btn.setOnClickListener(this);
//        buttonF.setOnClickListener(this);
//        buttonE.setOnClickListener(this);

        buttonA.setOnTouchListener(this);
        buttonB.setOnTouchListener(this);
        buttonC.setOnTouchListener(this);
        buttonD.setOnTouchListener(this);
        buttonG.setOnTouchListener(this);
//        buttonE.setOnTouchListener(this);
        Connect.setOnTouchListener(this);
        Disconnect.setOnTouchListener(this);

    }

/*    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.Button_A:
                Byte2 ^= 0x01;
                break;
            case R.id.Button_B:
                Byte2 ^= 0x02;
                break;
            case R.id.Button_C:
                Byte2 ^= 0x04;
                break;
            case R.id.Button_D:
                Byte2 ^= 0x08;
                break;
            case R.id.Button_E:
                Byte2 ^= 0x10;
                break;
            case R.id.connect:
                IP = IPAddress.getText().toString();
                port = Integer.valueOf(Port.getText().toString());
                ConnectionStatus.setText("Connecting...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket = new Socket(IP, port);
                            printWriter = new PrintWriter(socket.getOutputStream());
                            if (printWriter == null)
                                ConnectionStatus.setText("Not Connected");
                            else
                                ConnectionStatus.setText("Connected");
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.disconnect:
                ConnectionStatus.setText("Disconnecting");
                printWriter.close();
                try {
                    socket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                printWriter = null;
                ConnectionStatus.setText("Disconnected");
                break;
        }
    }
*/

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            switch (v.getId()) {
                case R.id.left:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            currentByte[TOUCHBUTTONS] |= 0x01;
                            break;
                        case MotionEvent.ACTION_UP:
                            currentByte[TOUCHBUTTONS] &= 0xFE;
                            break;
                    }
                    break;
                case R.id.right:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            currentByte[TOUCHBUTTONS] |= 0x04;
                            break;
                        case MotionEvent.ACTION_UP:
                            currentByte[TOUCHBUTTONS] &= 0xFB;
                            break;
                    }
                    break;
/*                case R.id.Button_UP:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            currentByte[TOUCHBUTTONS] |= 0x02;
                            break;
                        case MotionEvent.ACTION_UP:
                            currentByte[TOUCHBUTTONS] &= 0xFD;
                            break;
                    }
                    break;
                case R.id.Button_DOWN:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            currentByte[TOUCHBUTTONS] |= 0x08;
                            break;
                        case MotionEvent.ACTION_UP:
                            currentByte[TOUCHBUTTONS] &= 0xF7;
                            break;
                    }
                    break;*/
                case R.id.buttonA:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            currentByte[CLICKBUTTONS] |= 0x01;
                            break;
                        case MotionEvent.ACTION_UP:
                            currentByte[CLICKBUTTONS] &= 0xFE;
                            break;
                    }
                    break;
                case R.id.buttonB:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            currentByte[CLICKBUTTONS] |= 0x02;
                            break;
                        case MotionEvent.ACTION_UP:
                            currentByte[CLICKBUTTONS] &= 0xFD;
                            break;
                    }
                    break;
                case R.id.buttonC:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            currentByte[CLICKBUTTONS] |= 0x04;
                            break;
                        case MotionEvent.ACTION_UP:
                            currentByte[CLICKBUTTONS] &= 0xFB;
                            break;
                    }
                    break;
                case R.id.buttonD:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            currentByte[CLICKBUTTONS] |= 0x08;
                            break;
                        case MotionEvent.ACTION_UP:
                            currentByte[CLICKBUTTONS] &= 0xF7;
                            break;
                    }
                    break;
                case R.id.buttonG:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            currentByte [CLICKBUTTONS] |= 0x20;
                            break;
                        case MotionEvent.ACTION_UP:
                            currentByte [CLICKBUTTONS] &= 0xDF;
                            break;
                    }
                    break;
                case R.id.connect:
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            IP = IPAddress.getText().toString();
//                            port = Integer.valueOf(Port.getText().toString());
                            resultTextView.setText("Connecting...");
//                            pwm1edittxt.setFocusable(false);
//                            pwm2edittxt.setFocusable(false);
                            pwm3edittxt.setFocusable(false);
                            IPAddress.setFocusable(false);
                            IPAddress.setLongClickable(false);
                            pwm3edittxt.setLongClickable(false);
                            setSystemView();
/*                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        socket = new Socket("192.168.43.98", 1336);
                                        printWriter = new PrintWriter(socket.getOutputStream());
//                                        printWriter.
                                        if (printWriter == null)
                                            resultTextView.setText("Not Connected");
                                        else
                                            resultTextView.setText("Connected");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();*/
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        socket = new Socket(IP, 1336);
                                        printWriter = new PrintWriter(socket.getOutputStream());
                                        if (printWriter == null)
                                            resultTextView.setText("Not Connected");
                                        else
                                            resultTextView.setText("Connected");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                    }
                    break;
                case R.id.disconnect:
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            resultTextView.setText("Disconnecting...");
//                            pwm1edittxt.setEnabled(true);
//                            pwm1edittxt.setFocusable(true);
//                            pwm1edittxt.setFocusableInTouchMode(true);
//                            pwm1edittxt.setClickable(true);
//                            pwm2edittxt.setEnabled(true);
//                            pwm2edittxt.setFocusable(true);
//                            pwm2edittxt.setFocusableInTouchMode(true);
                            pwm3edittxt.setEnabled(true);
                            pwm3edittxt.setFocusable(true);
                            pwm3edittxt.setFocusableInTouchMode(true);
                            IPAddress.setEnabled(true);
                            IPAddress.setFocusable(true);
                            IPAddress.setFocusableInTouchMode(true);
                            printWriter.close();
                            socket.close();
                            printWriter = null;
                            resultTextView.setText("Disconnected");
                            break;
                    }
                    break;
            }
            if ((v.getId() != R.id.connect) && (v.getId() != R.id.disconnect))
                uploadData();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
/*            case R.id.pwm1button:
                pwmValue = Integer.valueOf (pwm1edittxt.getText().toString());
                pwm1btn.setBackgroundColor(Color.GREEN);
                pwm2btn.setBackgroundColor(Color.GRAY);
                pwm3btn.setBackgroundColor(Color.GRAY);
                currentByte [LEFTSEEKBAR] = (char) pwmValue;
                break;*/
/*            case R.id.pwm2button:
                pwmValue = Integer.valueOf (pwm2edittxt.getText().toString());
//                pwm1btn.setBackgroundColor(Color.GRAY);
                pwm2btn.setBackgroundColor(Color.GREEN);
                pwm3btn.setBackgroundColor(Color.GRAY);
                currentByte [LEFTSEEKBAR] = (char) pwmValue;
                break;*/
            case R.id.pwm3button:
                pwmValue = Integer.valueOf (pwm3edittxt.getText().toString());
//                pwm1btn.setBackgroundColor(Color.GRAY);
//                pwm2btn.setBackgroundColor(Color.GRAY);
                pwm3btn.setBackgroundColor(Color.GREEN);
                currentByte [LEFTSEEKBAR] = (char) pwmValue;
                break;
/*            case R.id.buttonF:
                reverseOFF_ON = !reverseOFF_ON;
                if (reverseOFF_ON) {
                    buttonF.setText("Rev ON");
                    buttonF.setBackgroundColor(Color.GRAY);
                }
                else
                {
                    buttonF.setText("Rev OFF");
                    buttonF.setBackgroundColor(Color.RED);
                }
                break;
            case R.id.buttonE:
                reverse = !reverse;
                if (reverse)
                    buttonE.setBackgroundColor(Color.GREEN);
                else
                    buttonE.setBackgroundColor(Color.RED);
                currentByte[CLICKBUTTONS] ^= 0x10;
                break;*/
/*                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentByte[CLICKBUTTONS] |= 0x10;
                        break;
                    case MotionEvent.ACTION_UP:
                        currentByte[CLICKBUTTONS] &= 0xEF;
                        break;
                }*/
/*                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        reverseOFF_ON = OFF;
                        buttonF.setText("Rev OFF");
                        buttonF.setBackgroundColor(Color.RED);
                        break;
                    case MotionEvent.ACTION_UP:
                        reverseOFF_ON = ON;
                        buttonF.setText("Rev ON");
                        buttonF.setBackgroundColor(Color.GRAY);
                        break;
                }*/
        }
        uploadData();
    }

    private void uploadData ()
    {
        try {
            if (!SendingInProgress) {
                for (i = 0; i < PACKET_SIZE; i++)
                    if (prevByte[i] != currentByte[i])
                        break;
                if (i < PACKET_SIZE) {
                    if (printWriter != null) {
                        SendingInProgress = true;
/*                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                resultTextView.setText("" + (int) currentByte[ANGLE] + "," + (int) currentByte[CLICKBUTTONS] + "," + (int) currentByte[LEFTSEEKBAR] + "," + (int) currentByte[TOUCHBUTTONS] + "," + (int) currentByte[RIGHTSEEKBAR]);
                                printWriter.write(currentByte, 0, PACKET_SIZE);
                                printWriter.flush();
                            }
                        }).start();*/
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                resultTextView.setText("" + (int) currentByte[ANGLE] + "," + (int) currentByte[CLICKBUTTONS] + "," + (int) currentByte[LEFTSEEKBAR] + "," + (int) currentByte[TOUCHBUTTONS] + "," + (int) currentByte[RIGHTSEEKBAR]);
//                                Log.d(TAG, "Bytes" + (int) currentByte[ANGLE]);
                                printWriter.write(currentByte, 0, PACKET_SIZE);
                                printWriter.flush();
                            }
                        });
                        for (i = 0; i < PACKET_SIZE; i++)
                            prevByte[i] = currentByte[i];
                        SendingInProgress = false;
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    @Override
    public void onJoystickMoved(float X, float Y, float radius, int id) {

        Y = -Y;

//        JoystickX = (TextView) findViewById(R.id.JoyX);
//        JoystickY = (TextView) findViewById(R.id.JoyY);

        if ((X == 0) && (Y == 0))
        {
            currentByte[ANGLE] = 0;
            currentByte[CLICKBUTTONS] &= 0x7F;
            uploadData();
            return;
        }

        if (X*X + Y*Y > 1.2*radius*radius)
        {
            Angle = (float) Math.atan2(Y, X);
            Angle = (float) ((Angle * 180) / Math.PI);

/*            if (prevAngle == -1)
                prevAngle = Angle;
            if (Math.abs(Angle - prevAngle) >= 170)
            {
                Angle = prevAngle;
                currentByte[CLICKBUTTONS] ^= 0x80;
            }
            else {
                prevAngle = Angle;*/
            if ((Angle < -10) && (Angle > -170)) {
                if (reverseOFF_ON) {
                    currentByte[CLICKBUTTONS] |= 0x80;
                    Angle = (Angle + 180);
                }
                else {
                    if ((Angle < -10) && (Angle > -90))
                        currentByte [ANGLE] = 255;
                    else
                        currentByte [ANGLE] = 1;
                    uploadData();
                    return;
                }
//                Log.d(TAG, "onJoystickMoved: " + currentByte);
//                uploadData();
//                return;
            } else {
                currentByte[CLICKBUTTONS] &= 0x7F;
                if (Angle < 0) {
                    if (Angle <= -170)
                        Angle = 180;
                    else if (Angle >= -10)
                        Angle = 0;
                }
            }
//            }
            if (Angle >= 0) {
                currentByte[ANGLE] = (char) (Angle * 255 / 180);
                currentByte[ANGLE] = (char) (255 - currentByte[ANGLE]);
            }
            if (currentByte[ANGLE] == 0)
                currentByte[ANGLE] = 1;

            uploadData();
        }
    }

    public void setSystemView ()
    {
        View doorView = getWindow().getDecorView();
        doorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
