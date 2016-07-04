package com.thq.pat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.thq.pat.ScreenListener.ScreenStateListener;
import com.thq.pat.contentfactory.ContentFactory;
import com.thq.pat.image.ui.TouchView;
import com.thq.pat.image.util.ImageDownload;
import com.thq.pat.sina.provider.Tweet;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FxService extends Service 
{

    //定义浮动窗口布局
    FrameLayout mFloatLayout;
    FrameLayout mShitLayout;
    LayoutParams wmPatParams;
    LayoutParams wmShitParams;
    LayoutParams wmInfoParams;
    LayoutParams wmNetImageParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    
    RotateImageView mMyPat;
    TextView mShowInfo;
    TouchView netImage;
    
    int currentPatX,currentPatY;
    int nextPatX,nextPatY;
    int destX,destY;
    int stepX = 0,stepY = 0;
    boolean isDoneCurrentTask = true;
    
    int maxPixels,minPixels;

    
    final static int UNKNOW_STATUS = -1; 
    final static int STOP = 0; 
    final static int RUN = 1; 
    final static int QUESTION = 3; 
    final static int AMAZED = 4; 
    final static int IDLE = 5; 
    
    final static int UPDATE_PAT = 6;
    final static int UPDATE_PAT_DONE = 7;
    final static int SHOW_INFO = 8;
    final static int HIDE_INFO = 9;

    int currentStatus = IDLE;
    int nextStatus = UNKNOW_STATUS;
    

    private Random mRandomGenerator;
    
    private static final String TAG = "FxService";

    Runnable mSwitchPatPicRunnable = new Runnable() {
        public void run() {

            AnimatorSet mShowAnimatorSet = new AnimatorSet();
            Animator showAnimator =  ObjectAnimator.ofFloat(mMyPat, "alpha",
                    new float[] { 0.9F, 1.0F });
            mShowAnimatorSet.playTogether(showAnimator);
            mShowAnimatorSet.setDuration(100l);
            Log.i(TAG, "TTT------------=======---- mThrowable ");
            mMyPat.setImageResource(R.drawable.pic8_question);
            handler.sendEmptyMessageDelayed(IDLE, 3000);
        }
    };
    
    MyThread mPatLifeThread = new MyThread() {

        @Override
        public void run() {
            while (!fxServiceStoped) {
                checkSuspend();
//                Log.i(TAG, "run()");
                if (isDoneCurrentTask /*|| nextStatus == IDLE*/) {
                    int random = mRandomGenerator.nextInt(100);
                    Log.i(TAG, "TTT----------------------------------------------------------- random = " + random);
                    isDoneCurrentTask = false;
                    if (random >= 0 && random < 50) {
                        nextStatus = RUN;
                        handler.sendEmptyMessageDelayed(RUN, 10);
                    } else if (random >= 50 && random < 60) {
                        handler.sendEmptyMessageDelayed(IDLE, 1000);
                    } else if (random >= 60 && random < 85) {
                        nextStatus = QUESTION;
                        handler.sendEmptyMessageDelayed(QUESTION, 10);
//                        handler.post(mThrowable);
                    } else if (random >= 85 && random < 100) {
                        nextStatus = AMAZED;
                        handler.sendEmptyMessageDelayed(AMAZED, 10);
                    }
                }
            }
        };
    };
    
    final Handler handler =new Handler(){
        public void handleMessage(Message msg){

            switch (msg.what) {
                case RUN:
                    destX = mRandomGenerator.nextInt(minPixels);
                    destY = mRandomGenerator.nextInt(maxPixels);
//                    move(destX, destY);
                    moveByLoyout();
                    break;
                case IDLE:
                    nextStatus = IDLE;
//                    mMyPat.setImageDrawable(null);
//                    mMyPat.setImageResource(R.drawable.pic8);
                    isDoneCurrentTask = true;
                    Log.i(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++task is done.");
                    break;
                case QUESTION:
//                    mMyPat.setImageDrawable(null);
/*                    mMyPat.setImageResource(R.drawable.shit);
                    handler.sendEmptyMessageDelayed(IDLE, 3000);*/
                    handler.postDelayed(mSwitchPatPicRunnable, 100);
                    break;
                case AMAZED:
//                    mMyPat.setImageDrawable(null);
                    mMyPat.setImageResource(R.drawable.pic8_surprise);
                    mMyPat.refreshDrawableState();
                    handler.sendEmptyMessageDelayed(IDLE, 3000);
                    break;
                case UPDATE_PAT:
                    doMoveAnimByLoyout();
                    break;
                case UPDATE_PAT_DONE:
                    stopPatAnimByLayout();
//                    doShit();
                    break;
                case SHOW_INFO:
                    showInfo();
                    handler.removeMessages(HIDE_INFO);
                    handler.sendEmptyMessageDelayed(HIDE_INFO, mShowInfoDuration);
                    mShowInfoDuration = 5000;
                    break;
                case HIDE_INFO:
                    hideInfo();
                    break;
                default:
                    break;
            }
            if(msg.what==0x111){
//                params=(RelativeLayout.LayoutParams)imgView.getLayoutParams();
//                params.setMargins(Math.round(nextX), Math.round(nextY)
//                        , Math.round(width-60-nextX), Math.round(height-60-nextY));
            }
        }
    };

    ScreenStateListener mScreenStateListener = new ScreenStateListener() {

        @Override
        public void onScreenOn() {
            mPatLifeThread.setSuspend(false);
            Log.d(TAG, "onScreenOn ");
        }

        @Override
        public void onScreenOff() {
            mPatLifeThread.setSuspend(true);
            Log.d(TAG, "onScreenOff ");
        }

        @Override
        public void onUserPresent() {
            // TODO Auto-generated method stub
            Log.d(TAG, "onUserPresent ");
        }
        
    };
    
    ScreenListener mScreenListener;
    ContentFactory mContentFactory;
    ImageDownload imageDownload;
    private AlarmManager mAlarmManager;
    @Override
    public void onCreate() 
    {
        super.onCreate();
        Log.i(TAG, "oncreat");
        fxServiceStoped = false;
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mScreenListener = new ScreenListener(this);
        mScreenListener.begin(mScreenStateListener);
        mContentFactory = ContentFactory.getInstance();
        mContentFactory.downloadContent();
        imageDownload = new ImageDownload();
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    
    class UpdatePatMove  implements Runnable{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            for (int i = 0; i < countStep ; i++) {

                if (fxServiceStoped) break;
                try {
                    Thread.sleep(mMoveDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateNextPosition(i);
                handler.sendEmptyMessageDelayed(UPDATE_PAT, 10);
            }
            handler.sendEmptyMessageDelayed(UPDATE_PAT_DONE, 10);
        }
    }
    
    
    private void shit() {
        int shit = mRandomGenerator.nextInt(10);

        Log.i(TAG, " shit " + shit);
        if ((shitCount - mRecyledShits.size()) > MAX_SHIT_NUM) {
            updateShowInfo(100);
        } else if (shit == 1 ) {
            doShit();
        }
    }
    
    ArrayList<ImageView> mRecyledShits = new ArrayList<ImageView>();
    HashMap<Integer, ImageView> mShitMap = new HashMap<Integer, ImageView>();
    int shitCount = 0;
    final int MAX_SHIT_NUM = 20;
    private void doShit() {
//        if (mWindowManager == null) return;
        ImageView imageView;
        if (mRecyledShits.size() > 0) {
            imageView = mRecyledShits.get(0);
            mShitMap.put((Integer) imageView.getTag(), imageView);
            mRecyledShits.remove(0);
            wmShitParams.x = (int) currentPatX + 5;
            wmShitParams.y = (int) currentPatY + 16;
            mWindowManager.updateViewLayout(imageView, wmShitParams);
            imageView.setVisibility(View.VISIBLE);
        } else {
            shitCount ++;
            imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.shit);
            imageView.setOnClickListener(new OnClickListener() 
            {
                
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onClick(View v) 
                {
                    // TODO Auto-generated method stub
//                Toast.makeText(FxService.this, "你踩到屎了！！！！！" + currentPatX, Toast.LENGTH_SHORT).show();
//                mShowInfo.setText("你踩到屎了！！！！！");
                    updateShowInfo(-1);
//                    handler.sendEmptyMessage(SHOW_INFO);
                    recycleShit(v);
                }
            });
            imageView.setTag(shitCount);
            mShitMap.put(shitCount, imageView);
            
            wmShitParams.x = (int) currentPatX + 5;
            wmShitParams.y = (int) currentPatY + 16;
            mWindowManager.addView(imageView, wmShitParams);
        }
    }

    private boolean recycleShit(View v) {
        int position = (Integer)v.getTag();
        v.setVisibility(View.GONE);
        mRecyledShits.add(mShitMap.get(position));
        mShitMap.remove(position);
        return true;
    }
    
    private boolean recycleAllShit() {
//        Iterator iter = mShitMap.entrySet().iterator();
        Iterator iter = mShitMap.keySet().iterator();
        while (iter.hasNext()) {
          //Map.Entry entry = (Map.Entry) iter.next();
            Object key = iter.next();
//            Object key = entry.getKey();
//            Object val = entry.getValue();
            mRecyledShits.add(mShitMap.get(key));
            mShitMap.remove(key);
        }
        return true;
    }

    /**
     * Context中有一个startActivity方法，Activity继承自Context，
     * 重载了startActivity方法。如果使用 Activity的startActivity方法，
     * 不会有任何限制，而如果使用Context的startActivity方法的话，
     * 就需要开启一个新的task，遇到上面那个异常的， 都是因为使用了Context的startActivity方法。
     * 解决办法是，加一个flag。intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
     */
    @Override
    public void startActivity(Intent intent) {
        // TODO Auto-generated method stub\
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
    }

    String mShowInfoText = "";
    int mShowInfoDuration = 5000;
    boolean hasImage = false;
    private  void updateShowInfo(int needInfoIndex) {
        hasImage = false;
        
        ArrayList<String> mQiushiList = mContentFactory.getQiuShiList();
        int indexOfQiuShi = (mQiushiList != null && mQiushiList.size() > 0)?mRandomGenerator.nextInt(mQiushiList.size()):-1;
        
        ArrayList<Tweet> mSinaList = mContentFactory.getSinaList();
        int indexOfSina = (mSinaList != null && mSinaList.size() > 0)?mRandomGenerator.nextInt(mSinaList.size()):-1;
        int action = mRandomGenerator.nextInt(20);
        if (needInfoIndex > 0) action = needInfoIndex;
        switch (action) {
        case 1:
        case 2:
        case 9:
        case 10:
        case 12:
        case 13:
        case 14:
        case 15:
            if (indexOfSina >= 0) {
                Tweet tweet = mSinaList.get(indexOfSina);
                String videoLink = tweet.getMiaopaiLink();
                mShowInfoText = "作者：" + tweet.getUserName() + "\n\n"
               + tweet.getTweetContent() + "\n"
               + (!videoLink.equals("null")?videoLink:"")  + "\n\n" 
               +  "来源：新浪微博"
               ;
                mShowInfoDuration = 20000;
                if (tweet.getPicLink() != null && !tweet.getPicLink().contains("null")) {
                    Log.i(TAG, "THQ hasiamge loading ...");
                    hasImage = true;
                    imageDownload.init(maxPixels, minPixels, netImage, this, tweet.getPicLink());
                }
            } else {
                mShowInfoText = "下次遛我的时候，记得开网络哦，有惊喜...";
            }
            break;
        case 3:
        case 5:
            if (indexOfQiuShi >= 0) {
                mShowInfoText = mQiushiList.get(indexOfQiuShi)
                        /*+  "来源：糗事百科" + "\n" */;
                mShowInfoDuration = 20000;
            } else {
                mShowInfoText = "下次遛我的时候，记得开网络哦，有惊喜...";
            }
            break;
        case 4:
            mShowInfoText = "你踩到屎了！！！！！";
            break;
/*        case 5:
            mShowInfoText = "憋踩了!!!";
            break;*/
        case 6:
            mShowInfoText = "疼!!!好疼!!!踩!!!踩了!!!!";
            break;
        case 7:
            mShowInfoText = "开开心心的哦!!!!";
        case 11:
            mShowInfoText = "虚脱了，拉不粗来了!!!!";
            break;
        case 100:
            mShowInfoText = "拉的屎太多了，赶紧去扫一下!!!!";
            break;

        default:
            mShowInfoText = "你要走蟑螂屎运！！！！！";
            break;
        }
        handler.sendEmptyMessage(SHOW_INFO);
//        return mShowInfoText;
    }
    
    private void showInfo() {
        mShowInfo.setText(mShowInfoText);
        mShowInfo.setVisibility(View.VISIBLE);
        if (hasImage) {
            netImage.setVisibility(View.VISIBLE);
        } else if (netImage.getVisibility() == View.VISIBLE) {
            hasImage = false;
            netImage.setVisibility(View.INVISIBLE);
        } 
//        mWindowManager.addView(mShowInfo, wmInfoParams);
    }
    private void hideInfo() {
        mShowInfo.setVisibility(View.INVISIBLE);
        if (hasImage) {
            netImage.setVisibility(View.INVISIBLE);
            hasImage = false;
        }
//        mWindowManager.removeView(mShowInfo);
    }

    /**
     * @return The selected quadrant.
     */
    
    private int getQuadrant(int currentPatX2, int currentPatY2, int destX2, int destY2) {
        if (destX2>currentPatX2) {
            if (destY2>currentPatY2) {
                return (destX2-currentPatX2)>(destY2-currentPatY2)?3:4;
            } else {
                return (destX2-currentPatX2)>(currentPatY2-destY2)?2:1;
            }
        } else {
            if (destY2>currentPatY2) {
                return (currentPatX2-destX2)>(destY2-currentPatY2)?6:5;
            } else {
                return (currentPatX2-destX2)>(currentPatY2-destY2)?7:8;
            }
        }
    }

    /**
     * @return The selected quadrant.
     */
    private double getK(int x, int y, int x1, int y1) {
//        return (double)(y1 - y)/(x1 - x);
        
        double K = 1.0;
        switch (destGuadrant) {
        case 1:
        case 4:
        case 5:
        case 8:
              K = (double)(y1 - y)/(x1 - x);
            break;
        case 2:
        case 3:
        case 6:
        case 7:
            K = (double)(x1 - x)/(y1 - y);
            break;

        default:
            break;
        }
        return K;
    }

    /**
     * @return The angle of the unit circle with the image view's center
     */
    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - (currentPatX );
        double y = currentPatY - yTouch/* - (currentPatY / 2d)*/;

        switch (getQuadrant(x, y)) {
        case 1:
            return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

        case 2:
            
            return /*180*/ - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
        case 3:
            return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);

        case 4:
            return 180 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

        default:
            // ignore, does not happen
            return 0;
        }
    }

    /**
     * @return The selected quadrant.
     */
    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }
    
    
    private void updateNextPosition(int position) {
        switch (destGuadrant) {
        case 1:
            nextPatX++;
            nextPatY = oriY + (int) (position * K);
            break;
        case 2:
            nextPatY--;
            nextPatX = oriX - (int) (position * K);
            
            break;
        case 3:
            nextPatY++;
            nextPatX = oriX + (int) (position * K);
            
            break;
        case 4:
            nextPatX++;
            nextPatY = oriY + (int) (position * K);
            
            break;
        case 5:
            nextPatX--;
            nextPatY = oriY - (int) (position * K);
            
            break;
        case 6:
            nextPatY++;
            nextPatX = oriX + (int) (position * K);
            
            break;
        case 7:
            nextPatX--;
            nextPatY = oriY - (int) (position * K);
            
            break;
        case 8:
            nextPatY--;
            nextPatX = oriX - (int) (position * K);
            
            break;

        default:
            break;
        }
    }
    
    
    
    
    int mMoveDuration = 10;
    int countStep = 1;
    int destGuadrant = 1;
    int oriX,oriY;
    Double K = 1.0;
    private void moveByLoyout() {
        Log.i(TAG, "moveByLoyout: ");
        countStep = Math.min(Math.abs(destX - currentPatX), Math.abs(destY - currentPatY));
        stepX = Math.round(((float) destX - nextPatX)/countStep);
        stepY = Math.round(((float) destY - nextPatY)/countStep);

        oriX = currentPatX;
        oriY = currentPatY;

        mMoveDuration = mRandomGenerator.nextInt(70) +10;
        K = getK(oriX, oriY, destX, destY);

        destGuadrant = getQuadrant(oriX, oriY, destX, destY);

        
//        一种方法是用代码
//        Matrix matrix=new Matrix();
//        mMyPat.setScaleType(ScaleType.MATRIX); //required
//        matrix.postRotate((float) 30, 10, 10);
//        mMyPat.setImageMatrix(matrix);
        
        mMyPat.setRotation((float) getAngle(destX, destY));
        mMyPat.setRotationX(mMyPat.getWidth()/2);
        mMyPat.setRotationY(mMyPat.getHeight()/2);
        
        
        
        Log.i(TAG, " moveByLoyout stepX = " + stepX
                + " stepY = " + stepY +
                " oriX = " + oriX +
                " oriY = " + oriY +
                " destX = " + destX +
                " destY = " + destY +
                " mMoveDuration = " + mMoveDuration +
                " K = " + K +
                " maxPixels = " + maxPixels +
                " minPixels = " + minPixels 
                );
        shit();
        doPatAnim();
        new Thread(new UpdatePatMove()).start();
    }

    private void move(int x, int y) {
        nextPatX =  x;
        nextPatY =  y;
//        Log.i(TAG, " X " + currentPatX
//                + " minPixels = " + minPixels
//        + " maxPixels = " + maxPixels);
//        Log.i(TAG, "X" + event.getX());
        //减25为状态栏的高度
//        Log.i(TAG, "RawY" + event.getRawY());
//        Log.i(TAG, "Y" + event.getY());
         //刷新
//        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
        // 设置悬浮窗口长宽数据
//        wmPatParams.width = 20;
//        wmPatParams.height = 20;
//        mWindowManager.updateViewLayout(mMyPat, wmParams);
//        doPatAnim();
        int duration = (mRandomGenerator.nextInt(6)+2)*1000;
        doMoveAnim(currentPatX, nextPatX, currentPatY, nextPatY, duration);
        Log.i(TAG, "doMoveAnim done" );
        shit();
    }
    
    private void doPatAnim() {
      mMyPat.setBackgroundResource(R.drawable.move_anim);
        AnimationDrawable animator = (AnimationDrawable) mMyPat.getBackground();
//        mMyPat.setBackgroundDrawable(null);
        mMyPat.setImageDrawable(null);
//        handler.sendEmptyMessageDelayed(AMAZED, 10);
        animator.start();
    }
    
    public void stopPatAnim() {
        
        Log.i(TAG, "TTT   mMyPat = ( "
                + " " + mMyPat.getLeft() 
                + " " + mMyPat.getTop()
                + " " + mMyPat.getWidth() 
                + " " + mMyPat.getHeight()
                + ") mFloatLayout = ( "
                + " " + mFloatLayout.getLeft() 
                + " " + mFloatLayout.getTop()
                + " " + mFloatLayout.getWidth() 
                + " " + mFloatLayout.getHeight() + ")"
                );

        
        AnimationDrawable animator = (AnimationDrawable) mMyPat.getBackground();  
        animator.stop();  
        mMyPat.setImageResource(R.drawable.pic8);
        currentPatX = nextPatX;
        currentPatY = nextPatY;
//        mMyPat.layout(nextPatX, nextPatY, nextPatX + 20, nextPatY + 20);
//        mFloatLayout.invalidate();
//            mMyPat.invalidate();
            Log.i(TAG, "TTT   mMyPat = ( "
                    + " " + mMyPat.getLeft() 
                    + " " + mMyPat.getTop()
                    + " " + mMyPat.getWidth() 
                    + " " + mMyPat.getHeight());
        handler.sendEmptyMessageDelayed(IDLE, 1000);
    }
    
    public void stopPatAnimByLayout() {
        currentPatX = nextPatX;
        currentPatY = nextPatY;
        AnimationDrawable animator = (AnimationDrawable) mMyPat.getBackground();  
        animator.stop();
        mMyPat.setBackgroundDrawable(null);
        mMyPat.setImageResource(R.drawable.pic8);
        handler.sendEmptyMessageDelayed(IDLE, 3000);
    }
    
    private void doMoveAnimByLoyout() {
        if (fxServiceStoped) return;
        // TODO Auto-generated method stub
        //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
//        nextPatX+=stepX;
//        nextPatY+=stepY;

        wmPatParams.x = nextPatX;
        currentPatX = wmPatParams.x;
//        Log.i(TAG, "RawX" + event.getRawX());
//        Log.i(TAG, "X" + event.getX());
        //减25为状态栏的高度
        wmPatParams.y = nextPatY;
        currentPatY = wmPatParams.y;
//        Log.i(TAG, "RawY" + event.getRawY());
//        Log.i(TAG, "Y" + event.getY());
         //刷新
//        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
        mWindowManager.updateViewLayout(mFloatLayout, wmPatParams);
    }
    
    private void doMoveAnim(int fromX, int toX, int fromY, int toY, int duration) {
        Log.i(TAG, "TTT MoveAnim =( "  + " " + fromX + " " +  fromY + " " + toX + " " + toY + " " + duration+ ")"
        );
//        Log.i(TAG, "TTT   mMyPat = ( "
//                + " " + mMyPat.getLeft() 
//                + " " + mMyPat.getTop()
//                + " " + mMyPat.getWidth() 
//                + " " + mMyPat.getHeight());
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation animation = new TranslateAnimation(fromX, toX, fromY, toY);
        animation.setDuration(duration); //设置持续时间5秒
//        animation.setFillAfter(true);
        animation.setAnimationListener(mMoveAnimListener);
        set.addAnimation(animation);
        set.setFillAfter(true);
        mMyPat.layout(toX, toY, toX + mMyPat.getWidth(), toY + mMyPat.getHeight());
/*        mMyPat.offsetLeftAndRight(toX - fromX);
        mMyPat.offsetTopAndBottom(toY - fromY);*/
        mMyPat.startAnimation(set);
    }
    
    AnimationListener mMoveAnimListener = new AnimationListener() {
        public void onAnimationEnd(android.view.animation.Animation animation) {
            Log.i(TAG, "onAnimationEnd");
            stopPatAnim();
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }
    };

    private void initPatParams() {
        //设置window type
        wmPatParams.type = LayoutParams.TYPE_TOAST; 
        //设置图片格式，效果为背景透明
        wmPatParams.format = PixelFormat.RGBA_8888; 
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmPatParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_LAYOUT_NO_LIMITS
                /*| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/
                | 0x00000080 |0x20000000;      
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmPatParams.gravity = Gravity.LEFT | Gravity.TOP;       
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmPatParams.x = 150;
        wmPatParams.y = 250;
        
        nextPatX = currentPatX = wmPatParams.x;
        nextPatY = currentPatY = wmPatParams.y;

        // 设置悬浮窗口长宽数据
        wmPatParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmPatParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }
    
    private void createFloatView() {
        wmPatParams = new LayoutParams();
        wmShitParams = new LayoutParams();
        wmInfoParams = new LayoutParams();
        wmNetImageParams = new LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        
        
        
//        DisplayMetrics metrics = new DisplayMetrics();
//        mWindowManager.getDefaultDisplay().getMetrics(metrics);
//        maxPixels = Math.max(metrics.heightPixels, metrics.widthPixels);
//        minPixels = Math.min(metrics.heightPixels, metrics.widthPixels);
        Display display = mWindowManager.getDefaultDisplay();
        maxPixels = Math.max(display.getHeight(), display.getWidth());
        minPixels = Math.min(display.getHeight(), display.getWidth());
        
        
        mRandomGenerator = new Random();
//        mRandomShit = new Random(10);

        //设置悬浮窗口长宽数据  
//        wmParams.width = LayoutParams.WRAP_CONTENT;
//        wmParams.height = LayoutParams.WRAP_CONTENT;
   
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (FrameLayout) inflater.inflate(R.layout.pat_layout, null);
        mShitLayout = (FrameLayout) inflater.inflate(R.layout.shit_layout, null);
        //添加mFloatLayout
        initPatParams();
        wmShitParams.copyFrom(wmPatParams);
//        wmShitParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmShitParams.height = 10;
        wmShitParams.width = 10;
        wmShitParams.type = LayoutParams.TYPE_PHONE;
        mWindowManager.addView(mFloatLayout, wmPatParams);
//         mWindowManager.addView(mShitLayout, wmParams);
        //浮动窗口按钮
        mMyPat = (RotateImageView)mFloatLayout.findViewById(R.id.float_id);
//        mMyPat = new ImageView(this);
//        mMyPat.setBackgroundResource(R.drawable.move_anim);
//        mWindowManager.addView(mMyPat, wmParams);
        
//        mShitView = (ImageView)mShitLayout.findViewById(R.id.shit_id);
        
/*        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mMyCat.getMeasuredWidth()/2);
        Log.i(TAG, "Height/2--->" + mMyCat.getMeasuredHeight()/2);*/
        

        mPatLifeThread.start();
        
        //设置监听浮动窗口的触摸移动
        mMyPat.setOnTouchListener(mPatTouchListener);
        mMyPat.setOnClickListener(mPatClickListener);
        
        
        mShowInfo = new TextView(this);
        //add for link
        mShowInfo.setAutoLinkMask(Linkify.ALL);
        mShowInfo.setMovementMethod(LinkMovementMethod.getInstance());
        
//        mShowInfo.setBackgroundColor(0xFFCCE8CF);
        mShowInfo.setBackgroundColor(0x90CCE8CF);
        mShowInfo.setTextColor(0xFFF008CF);
        wmInfoParams.format = PixelFormat.RGBA_8888; 
//        mShowInfo.getBackground().setAlpha(0);
//        wmInfoParams.copyFrom(wmInfoParams);
        wmInfoParams.type = LayoutParams.TYPE_PHONE;
/*        wmInfoParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;*/
        wmInfoParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        wmInfoParams.x = 0;
        wmInfoParams.y = 0;
        wmInfoParams.width = minPixels;
//        wmInfoParams.height = minPixels/2;
        wmInfoParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        wmInfoParams.gravity = Gravity.LEFT | Gravity.TOP;
        mShowInfo.setVisibility(View.GONE);
        mWindowManager.addView(mShowInfo, wmInfoParams);
        
        netImage = new TouchView(this);
//        mShowInfo.setBackgroundColor(0xFFCCE8CF);
//        netImage.setBackgroundColor(0x90CCE8CF);
        wmNetImageParams.format = PixelFormat.RGBA_8888; 
//        mShowInfo.getBackground().setAlpha(0);
//        wmInfoParams.copyFrom(wmInfoParams);
        wmNetImageParams.type = LayoutParams.TYPE_PHONE;
        wmNetImageParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        wmNetImageParams.x = 0;
        wmNetImageParams.y = 0;
        wmNetImageParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        wmInfoParams.height = minPixels/2;
        wmNetImageParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        wmNetImageParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        netImage.setVisibility(View.GONE);
        mWindowManager.addView(netImage, wmNetImageParams);
    }
    
    OnTouchListener mPatTouchListener = new OnTouchListener() {
        
        @Override
        public boolean onTouch(View v, MotionEvent event) 
        {
            // TODO Auto-generated method stub
            //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
            wmPatParams.x = (int) event.getRawX() - mMyPat.getMeasuredWidth()/2;
            currentPatX = wmPatParams.x;
            nextPatX = wmPatParams.x;
            Log.i(TAG, "RawX" + event.getRawX());
            Log.i(TAG, "X" + event.getX());
            //减25为状态栏的高度
            wmPatParams.y = (int) event.getRawY() - mMyPat.getMeasuredHeight()/2 - 25;
            currentPatY = wmPatParams.y;
            nextPatY = wmPatParams.y;
            Log.i(TAG, "RawY" + event.getRawY());
            Log.i(TAG, "Y" + event.getY());
             //刷新
//            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
            mWindowManager.updateViewLayout(mFloatLayout, wmPatParams);
            return false;  //此处必须返回false，否则OnClickListener获取不到监听
        }
    };
    
    OnClickListener mPatClickListener = new OnClickListener() 
    {
        
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) 
        {
            // TODO Auto-generated method stub
//            Toast.makeText(FxService.this, "onClick X = " + currentPatX, Toast.LENGTH_SHORT).show();
//            FrameLayout.LayoutParams mImageParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
//            mShitLayout.addView(imageView);
//            mShitLayout.updateViewLayout(imageView, mImageParams);
            commandPatToDo();
        }
    };
    
    private void commandPatToDo() {
        int action = mRandomGenerator.nextInt(6);
        switch (action) {
        case 1:
        case 2:
        case 3:
            if ((shitCount - mRecyledShits.size()) > MAX_SHIT_NUM) {
                updateShowInfo(100);
            } else {
                doShit();
            }
            break;
        case 4:
            updateShowInfo(6);
            break;
        case 5:
            updateShowInfo(7);
            break;
        case 6:
            updateShowInfo(11);
            break;

        default:
            break;
        }
    }

    /**
         * 回收ImageView占用的图像内存;
         * @param view
         */
        public static void recycleImageView(View view){
            if(view==null) return;
            if(view instanceof ImageView){
                Drawable drawable=((ImageView) view).getDrawable();
                if(drawable instanceof BitmapDrawable){
                    Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
                    if (bmp != null && !bmp.isRecycled()){
                        ((ImageView) view).setImageBitmap(null);
                        bmp.recycle();
                        bmp=null;
                    }
                }
            }
        }

    boolean fxServiceStoped = false;
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mScreenListener.unregisterListener();
        fxServiceStoped = true;
        handler.removeMessages(RUN);
        handler.removeMessages(UPDATE_PAT);
        handler.removeMessages(QUESTION);
        handler.removeMessages(UPDATE_PAT_DONE);
        recycleAllShit();
        for (int i = 0; i < mRecyledShits.size(); i++) {
            ImageView imageView = mRecyledShits.get(i);
            if (imageView != null) {
                recycleImageView(imageView);
                mWindowManager.removeView(imageView);
                imageView = null;
            }
            mRecyledShits.remove(i);
        }
        if(mFloatLayout != null) {
            recycleImageView(mMyPat);
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
        if(mShowInfo != null) {
            //移除悬浮窗口
            mWindowManager.removeView(mShowInfo);
        }
    }
    
}