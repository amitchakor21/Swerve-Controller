package com.example.withcontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

//    TextView XOutput = (TextView) findViewById(R.id.XOut);
//    TextView YOutput = (TextView) findViewById(R.id.YOut);

    private float centreX, centreY;
    private float baseRadius, hatRadius;
    private JoystickListener joystickCallback;// = (JoystickListener)  findViewById(R.id.testJoystick);

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setupDimensions();
        drawJoystick(centreX, centreY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void setupDimensions ()
    {
        centreX = (float) (getWidth() / 1.8);
        centreY = (float) (getHeight() / 1.67);
        baseRadius = (Math.min(getWidth(), getHeight()) / 3);
        hatRadius = (float) (Math.min(getWidth(), getHeight()) / 5.5);
    }

    private void drawJoystick (float X, float Y)
    {
        if (getHolder().getSurface().isValid())
        {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            colors.setARGB(255, 50, 50, 50);
            myCanvas.drawCircle(centreX, centreY, baseRadius, colors);
            colors.setARGB(255,0,0,255);
            myCanvas.drawCircle(X, Y, hatRadius, colors);
            colors.setARGB(255, 255, 255, 0);
            myCanvas.drawLine(centreX, centreY, (float) (centreX - (baseRadius * Math.cos(10.0 * Math.PI / 180))), (float) (centreY + (baseRadius * Math.sin(10.0 * Math.PI / 180))), colors);
            myCanvas.drawLine(centreX, centreY, (float) (centreX + (baseRadius * Math.cos(10.0 * Math.PI / 180))), (float) (centreY + (baseRadius * Math.sin(10.0 * Math.PI / 180))), colors);
            getHolder().unlockCanvasAndPost(myCanvas);
        }
//        joystickCallback.onJoystickMoved(X, Y,  getId());
    }

    public JoystickView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;

    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.equals(this))
        {
            if (event.getAction() != MotionEvent.ACTION_UP)
            {
                float displacement = (float) Math.sqrt((Math.pow((event.getX() - centreX), 2)) + (Math.pow((event.getY() - centreY), 2)));
                if (displacement < baseRadius)
                {
                    drawJoystick(event.getX(), event.getY());
                    joystickCallback.onJoystickMoved(((event.getX() - centreX)), ((event.getY() - centreY)), hatRadius, getId());
                }
                else
                {
                    float ratio = baseRadius / displacement;
                    float constrainedX = centreX + ((event.getX() - centreX) * ratio);
                    float constrainedY = centreY + ((event.getY() - centreY) * ratio);
                    drawJoystick(constrainedX, constrainedY);
                    joystickCallback.onJoystickMoved(((constrainedX - centreX)), ((constrainedY - centreY)), hatRadius, getId());
                }
            }
            else
            {
                drawJoystick(centreX, centreY);
                joystickCallback.onJoystickMoved(0,0, hatRadius, getId());
            }
        }
        return true;
    }

    public interface JoystickListener {
        void onJoystickMoved (float X, float Y, float radius, int id);
    }

}
