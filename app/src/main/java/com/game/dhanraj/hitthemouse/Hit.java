package com.game.dhanraj.hitthemouse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by Dhanraj on 09-02-2017.
 */

public class Hit extends SurfaceView implements SurfaceHolder.Callback {

    private boolean gameOver = false;
    private Bitmap gameOverDialog;
    private Paint paint;
    private static SoundPool sounds;
    private static int hitsound;
    private static int missSound;
    public boolean soundOn=true;
    private Context context;
    private SurfaceHolder mySurfaceHolder;
    private Bitmap background;
    private int screenW=1;
    private int screenH=1;
    private boolean running = false;
    private boolean title=true;
    private hitThread thread;
    private int backgroundW;
    private int backgroundH;
    private float scaleW;
    private float scaleH;
    private float drawScaleW;
    private float drawScaleH;
    private Bitmap mask;
    private Bitmap mole;
    private Bitmap Boom;
    private Boolean hitting = false;
    private int mousehitnumber=0;
    private int mousemissed=0;
    private int mole1x, mole2x, mole3x, mole4x, mole5x,
            mole6x, mole7x;
    private int mole1y, mole2y, mole3y, mole4y, mole5y,
            mole6y, mole7y;
    private int activemouse=0;
    private boolean mouserising=true;
    private boolean mousesinking = false;
    private int mouserate = 50;
    private boolean mousehit=false;
    private int fingerX,fingerY;

    public Hit(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new hitThread(holder,context,
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                    }
                });

        setFocusable(true);
    }


    public hitThread getThread(){
        return thread;
    }


    public class hitThread extends Thread {

        public hitThread(SurfaceHolder surfaceHolder, Context Con,
                         Handler handler) {
            mySurfaceHolder = surfaceHolder;
            context = Con;
            background = BitmapFactory.decodeResource(context.getResources(), R.drawable.startthegame);
            backgroundW=background.getWidth();
            backgroundH=background.getHeight();
            sounds = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
            hitsound = sounds.load(context,R.raw.whack,1);
            missSound = sounds.load(context,R.raw.miss,1);
        }

        @Override
        public void run() {
            while (running) {
                Canvas c = null;
                try {
                    c = mySurfaceHolder.lockCanvas();
                    synchronized (mySurfaceHolder) {
                        if(!gameOver){
                            animateMouse();
                        }
                        draw(c);
                    }
                } finally {
                    if (c != null) {
                        mySurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }


        private void draw(Canvas canvas){
            try{
                canvas.drawBitmap(background,0,0,null);
                if(!title){

                    canvas.drawText("Hit :"+Integer.toString(mousehitnumber),10,paint.getTextSize()+10,paint);
                    canvas.drawText("Missed :"+Integer.toString(mousemissed),screenW-(int)(200*drawScaleW),paint.getTextSize()+10,paint);

                    canvas.drawBitmap(mole, mole1x, mole1y, null);
                    canvas.drawBitmap(mole, mole2x, mole2y, null);
                    canvas.drawBitmap(mole, mole3x, mole3y, null);
                    canvas.drawBitmap(mole, mole4x, mole4y, null);
                    canvas.drawBitmap(mole, mole5x, mole5y, null);
                    canvas.drawBitmap(mole, mole6x, mole6y, null);
                    canvas.drawBitmap(mole, mole7x, mole7y, null);
                    canvas.drawBitmap(mask, (int) 70*drawScaleW,
                            (int) 460*drawScaleH, null);
                    canvas.drawBitmap(mask, (int)170*drawScaleW,
                            (int) 410*drawScaleH, null);
                    canvas.drawBitmap(mask, (int)270*drawScaleW,
                            (int) 460*drawScaleH, null);
                    canvas.drawBitmap(mask, (int)370*drawScaleW,
                            (int) 410*drawScaleH, null);
                    canvas.drawBitmap(mask, (int)470*drawScaleW,
                            (int) 460*drawScaleH, null);
                    canvas.drawBitmap(mask, (int)570*drawScaleW,
                            (int) 410*drawScaleH, null);
                    canvas.drawBitmap(mask, (int)670*drawScaleW,
                            (int) 460*drawScaleH, null);
                    if(hitting){
                        canvas.drawBitmap(Boom,fingerX-(Boom.getWidth()/2),fingerY-(Boom.getHeight()/2),null);
                    }

                    if(gameOver){
                        canvas.drawBitmap(gameOverDialog,(screenW/2)-(gameOverDialog.getWidth()/2),(screenH/2)-(gameOverDialog.getHeight()/2),null);
                    }
                }
            }catch (Exception e){

            }


        }

        boolean TouchEvent(MotionEvent event){
            synchronized (mySurfaceHolder){
                int eventaction = event.getAction();
                int x = (int)event.getX();
                int y = (int)event.getY();

                switch(eventaction){
                    case MotionEvent.ACTION_DOWN:
                        if(!gameOver){
                            fingerY=y;
                            fingerX=x;
                            if(!title && detectMouseContact()){
                                hitting=true;
                                if(soundOn){
                                    AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                                    float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    sounds.play(hitsound,volume,volume,1,0,1);
                                }
                                mousehitnumber++;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if(title){
                            background = BitmapFactory.decodeResource(context.getResources(),
                                   R.drawable.back_ground);
                            background = Bitmap.createScaledBitmap(background,
                                    screenW,screenH,true);
                            mask = BitmapFactory.decodeResource(context.getResources(),R.drawable.maskimg );
                            mole=BitmapFactory.decodeResource(context.getResources(),R.drawable.mouse);
                            Boom = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.boom);
                            gameOverDialog = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.gameover1);

                            //reason for scaleW and scaleH????
                            scaleW=(float) screenW/(float) backgroundW;
                            scaleH=(float) screenH/(float)backgroundH;
                            Boom = Bitmap.createScaledBitmap(Boom,(int)(Boom.getWidth()*scaleW),(int)(Boom.getHeight()*scaleH),true);
                            mask=Bitmap.createScaledBitmap(mask,(int)(mask.getWidth()*scaleW),(int)(mask.getHeight()*scaleH),true);
                            mole=Bitmap.createScaledBitmap(mole,(int)(mole.getWidth()*scaleW),(int)(mole.getHeight()*scaleH),true);
                            gameOverDialog = Bitmap.createScaledBitmap(gameOverDialog,(int)(gameOverDialog.getWidth()*scaleW),(int)(gameOverDialog.getHeight()*scaleH),true);
                            title=false;
                            pickActiveMouse();
                        }
                        hitting=false;
                        if(gameOver){
                            mousehitnumber=0;
                            mousemissed=0;
                            activemouse=0;
                            pickActiveMouse();
                            gameOver=false;
                        }
                        break;
                }
            }
            return true;
        }

        public void setsurfacesize(int w,int h){
            synchronized (mySurfaceHolder){
                    screenW=w;
                    screenH=h;
                background = Bitmap.createScaledBitmap(background,w,h,true);
                drawScaleW = (float) screenW/800;
                drawScaleH = (float) screenH/600;
                mole1x = (int) (70* drawScaleW);
                mole2x = (int) (170* drawScaleW);
                mole3x = (int) (270* drawScaleW);
                mole4x = (int) (370* drawScaleW);
                mole5x = (int) (470* drawScaleW);
                mole6x = (int) (570* drawScaleW);
                mole7x = (int) (670* drawScaleW);
                mole1y = (int) (475* drawScaleH);
                mole2y = (int) (425* drawScaleH);
                mole3y = (int) (475* drawScaleH);
                mole4y = (int) (425* drawScaleH);
                mole5y = (int) (475* drawScaleH);
                mole6y = (int) (425* drawScaleH);
                mole7y = (int) (475* drawScaleH);
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setTextSize(drawScaleW*30);

            }


        }

        public void setRunning(boolean b){
            running = b;
        }




//meri mouse ki foto ka size thoda alag hai to ek baar dekh lena
        private void animateMouse()
        {
            if(activemouse==1){
                if(mouserising)
                {
                    mole1y-=mouserate;
                }else if(mousesinking)
                {
                    mole1y+=mouserate;
                }

                if(mole1y>=(int)(475*drawScaleH)|| mousehit){
                    mole1y=(int)(475*drawScaleH);
                    pickActiveMouse();
                }
                if(mole1y<=(int)(300*drawScaleH)){
                    mole1y = (int)(300*drawScaleH);
                    mouserising=false;
                    mousesinking=true;
                }
            }
            if(activemouse==2){
                if(mouserising)
                {
                    mole2y-=mouserate;
                }else if(mousesinking)
                {
                    mole2y+=mouserate;
                }

                if(mole2y>=(int)(425*drawScaleH)|| mousehit){
                    mole2y=(int)(425*drawScaleH);
                    pickActiveMouse();
                }
                if(mole2y<=(int)(250*drawScaleH)){
                    mole2y = (int)(250*drawScaleH);
                    mouserising=false;
                    mousesinking=true;
                }
            }
            if(activemouse==3){
                if(mouserising)
                {
                    mole3y-=mouserate;
                }else if(mousesinking)
                {
                    mole3y+=mouserate;
                }

                if(mole3y>=(int)(475*drawScaleH)|| mousehit){
                    mole3y=(int)(475*drawScaleH);
                    pickActiveMouse();
                }
                if(mole3y<=(int)(300*drawScaleH)){
                    mole3y = (int)(300*drawScaleH);
                    mouserising=false;
                    mousesinking=true;
                }
            }
            if(activemouse==4){
                if(mouserising)
                {
                    mole4y-=mouserate;
                }else if(mousesinking)
                {
                    mole4y+=mouserate;
                }

                if(mole4y>=(int)(425*drawScaleH)|| mousehit){
                    mole4y=(int)(425*drawScaleH);
                    pickActiveMouse();
                }
                if(mole4y<=(int)(250*drawScaleH)){
                    mole4y = (int)(250*drawScaleH);
                    mouserising=false;
                    mousesinking=true;
                }
            }
            if(activemouse==5){
                if(mouserising)
                {
                    mole5y-=mouserate;
                }else if(mousesinking)
                {
                    mole5y+=mouserate;
                }

                if(mole5y>=(int)(475*drawScaleH)|| mousehit){
                    mole5y=(int)(475*drawScaleH);
                    pickActiveMouse();
                }
                if(mole5y<=(int)(300*drawScaleH)){
                    mole5y = (int)(300*drawScaleH);
                    mouserising=false;
                    mousesinking=true;
                }
            }
            if(activemouse==6){
                if(mouserising)
                {
                    mole6y-=mouserate;
                }else if(mousesinking)
                {
                    mole6y+=mouserate;
                }

                if(mole6y>=(int)(425*drawScaleH)|| mousehit){
                    mole6y=(int)(425*drawScaleH);
                    pickActiveMouse();
                }
                if(mole6y<=(int)(250*drawScaleH)){
                    mole6y = (int)(250*drawScaleH);
                    mouserising=false;
                    mousesinking=true;
                }
            }
            if(activemouse==7){
                if(mouserising)
                {
                    mole7y-=mouserate;
                }else if(mousesinking)
                {
                    mole7y+=mouserate;
                }

                if(mole7y>=(int)(475*drawScaleH)|| mousehit){
                    mole7y=(int)(475*drawScaleH);
                    pickActiveMouse();
                }
                if(mole7y<=(int)(300*drawScaleH)){
                    mole7y = (int)(300*drawScaleH);
                    mouserising=false;
                    mousesinking=true;
                }
            }
        }

    private void pickActiveMouse(){
        if(!mousehit && activemouse>0)
        {
            if(soundOn){
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                sounds.play(missSound,volume,volume,1,0,1);
            }
            mousemissed++;
            if(mousemissed>=5)
            {
                gameOver=true;
            }
        }
        activemouse = new Random().nextInt(7)+1;
        mouserising=true;
        mousesinking=false;
        mousehit=false;
        mouserate = 5+(int)(mousehitnumber/10);}


        private Boolean detectMouseContact()
        {
            boolean contact1 = false;
            if(activemouse==1&&fingerX>=mole1x&&fingerX<mole1x+100&&fingerY>mole1y
                    &&fingerY<(int) 450*drawScaleH){
                contact1=true;
                mousehit=true;
            }
            if(activemouse==2&&fingerX>=mole2x&&fingerX<mole2x+100&&fingerY>mole2y
                    &&fingerY<(int) 400*drawScaleH){
                contact1=true;
                mousehit=true;
            }
            if(activemouse==3&&fingerX>=mole3x&&fingerX<mole3x+100&&fingerY>mole3y
                    &&fingerY<(int) 450*drawScaleH){
                contact1=true;
                mousehit=true;
            }
            if(activemouse==4&&fingerX>=mole4x&&fingerX<mole4x+100&&fingerY>mole4y
                    &&fingerY<(int) 400*drawScaleH){
                contact1=true;
                mousehit=true;
            }
            if(activemouse==5&&fingerX>=mole5x&&fingerX<mole5x+100&&fingerY>mole5y
                    &&fingerY<(int) 450*drawScaleH){
                contact1=true;
                mousehit=true;
            }
            if(activemouse==6&&fingerX>=mole6x&&fingerX<mole6x+100&&fingerY>mole6y
                    &&fingerY<(int) 400*drawScaleH){
                contact1=true;
                mousehit=true;
            }
            if(activemouse==7&&fingerX>=mole7x&&fingerX<mole7x+100&&fingerY>mole7y
                    &&fingerY<(int) 450*drawScaleH){
                contact1=true;
                mousehit=true;
            }
            return contact1;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return thread.TouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
            thread.setRunning(true);
        if(thread.getState() == Thread.State.NEW){
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            thread.setsurfacesize(i1,i2);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                thread.setRunning(false);
    }



}
