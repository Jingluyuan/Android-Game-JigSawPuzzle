package com.game.jigsawpuzzle;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //二维数组创建小方块
    private ImageView[][] iv_game_arr = new ImageView[3][5];

    private GridLayout game_main;

    private ImageView iv_null_ImageView;

    private GestureDetector gestureDetector;

    private boolean isGameStart = false;

    private boolean isMoving = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                int tmp = getDirByGes(motionEvent.getX(),motionEvent.getY(),motionEvent1.getX(),motionEvent1.getY());
                //Toast.makeText(MainActivity.this,""+tmp,Toast.LENGTH_SHORT).show();
                changebyGes(tmp);
                return false;
            }
        });
        setContentView(R.layout.activity_main);

        Bitmap bigBm = ((BitmapDrawable) getResources().getDrawable(R.drawable.puzzle)).getBitmap();
        int picWandH = bigBm.getWidth()/5;
        int ivWandH = getWindowManager().getDefaultDisplay().getWidth()/5;

        for (int i=0;i<iv_game_arr.length;i++)
        {
            for (int j=0;j<iv_game_arr[0].length;j++)
            {
                Bitmap bm = Bitmap.createBitmap(bigBm,j*picWandH,i*picWandH,picWandH,picWandH);
                iv_game_arr[i][j] = new ImageView(this);
                iv_game_arr[i][j].setImageBitmap(bm);
                iv_game_arr[i][j].setLayoutParams(new RelativeLayout.LayoutParams(ivWandH,ivWandH));
                iv_game_arr[i][j].setPadding(2,2,2,2);
                iv_game_arr[i][j].setTag(new GameData(i,j,bm));
                iv_game_arr[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean flag = isAlignNullImageView((ImageView)view);
                        //Toast.makeText(MainActivity.this,"是否相邻: "+ flag,Toast.LENGTH_SHORT).show();
                        if (flag)
                        {
                            changeImage((ImageView)view);
                        }
                    }
                });
            }
        }

        game_main = findViewById(R.id.game_main);

        for (int i=0;i<iv_game_arr.length;i++)
        {
            for (int j=0;j<iv_game_arr[0].length;j++)
            {
                game_main.addView(iv_game_arr[i][j]);
            }
        }

        setNullImageView(iv_game_arr[2][4]);

        RandomMove();

        isGameStart = true;

    }

    public void RandomMove()
    {
        for (int i=0;i<10;i++)
        {
            int type = (int)(Math.random()*4)+1;
            changebyGes(type,false);
        }
    }

    public void changebyGes(int type)
    {
        changebyGes(type,true);
    }

    public void changebyGes(int type, boolean isAnim)
    {
        GameData tmp_null = (GameData) iv_null_ImageView.getTag();
        int cur_x = tmp_null.x;
        int cur_y = tmp_null.y;

        if (type == 1)
        {
            cur_x++;
        }else if (type == 2)
        {
            cur_x--;

        }else if (type == 3)
        {
            cur_y++;
        }else if (type == 4)
        {
            cur_y--;
        }

        if (cur_x>=0&&cur_x<iv_game_arr.length&&cur_y>=0&&cur_y<iv_game_arr[0].length)
        {
            if (isAnim)
            {
                changeImage(iv_game_arr[cur_x][cur_y]);

            }else
            {
                changeImage(iv_game_arr[cur_x][cur_y],isAnim);
            }
        }else {

        }
    }

    public int getDirByGes(float start_x, float start_y, float end_x, float end_y)
    {
        boolean isLeftOrRight = (Math.abs(start_x-end_x)>Math.abs(start_y-end_y))?true:false;
        if (isLeftOrRight)
        {
            boolean isLeft = (start_x-end_x)>0?true:false;
            if (isLeft)
            {
                return 3;
            }else
            {
                return 4;
            }
        }else
        {
            boolean isUp = (start_y-end_y)>0?true:false;
            if (isUp)
            {
                return 1;
            }else
            {
                return 2;
            }

        }

    }

    public void changeImage(final ImageView imageView)
    {
        changeImage(imageView,true);
    }

    public void changeImage(final ImageView imageView,boolean isAnim)
    {
        if (isMoving)
        {
            return;
        }

        if (!isAnim)
        {
            GameData tmp = (GameData) imageView.getTag();
            iv_null_ImageView.setImageBitmap(tmp.bm);
            GameData null_image = (GameData) iv_null_ImageView.getTag();
            null_image.bm = tmp.bm;
            null_image.p_x = tmp.p_x;
            null_image.p_y = tmp.p_y;
            setNullImageView(imageView);
            if (isGameStart)
            {
                isGameOver();
            }
            return;
        }
        TranslateAnimation translateAnimation = null;
        if (imageView.getX()>iv_null_ImageView.getX())
        {
            translateAnimation = new TranslateAnimation(0.1f,-imageView.getWidth(),0.1f,0.1f);
        }else if (imageView.getX()<iv_null_ImageView.getX())
        {
            translateAnimation = new TranslateAnimation(0.1f,imageView.getWidth(),0.1f,0.1f);

        }else if (imageView.getY()>iv_null_ImageView.getY())
        {
            translateAnimation = new TranslateAnimation(0.1f,0.1f,0.1f,-imageView.getWidth());

        }else if (imageView.getY()<iv_null_ImageView.getY())
        {
            translateAnimation = new TranslateAnimation(0.1f,0.1f,0.1f,imageView.getWidth());
        }

        translateAnimation.setDuration(70);
        translateAnimation.setFillAfter(true);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isMoving = true;

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isMoving = false;
                imageView.clearAnimation();
                GameData tmp = (GameData) imageView.getTag();
                iv_null_ImageView.setImageBitmap(tmp.bm);
                GameData null_image = (GameData) iv_null_ImageView.getTag();
                null_image.bm = tmp.bm;
                null_image.p_x = tmp.p_x;
                null_image.p_y = tmp.p_y;
                setNullImageView(imageView);
                if (isGameStart)
                {
                    isGameOver();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(translateAnimation);
    }

    public void setNullImageView(ImageView imageView)
    {
        imageView.setImageBitmap(null);
        iv_null_ImageView = imageView;
    }

    public  boolean isAlignNullImageView(ImageView imageView)
    {
        GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
        GameData mGameData = (GameData) imageView.getTag();

        if (mNullGameData.y==mGameData.y&&mGameData.x+1==mNullGameData.x)
        {
            return true;

        }else if (mNullGameData.y==mGameData.y&&mGameData.x-1==mNullGameData.x)
        {
            return true;

        }else if (mNullGameData.y+1==mGameData.y&&mGameData.x==mNullGameData.x)
        {
            return true;

        }else if (mNullGameData.y-1==mGameData.y&&mGameData.x==mNullGameData.x)
        {
            return true;

        }
        return false;
    }

    public void isGameOver()
    {
        boolean isGameOver = true;
        for (int i=0;i<iv_game_arr.length;i++)
        {
            for (int j=0;j<iv_game_arr[0].length;j++)
            {
                if (iv_game_arr[i][j] == iv_null_ImageView)
                {
                    continue;
                }
                GameData mGameData = (GameData) iv_game_arr[i][j].getTag();
                if (!mGameData.isTrue())
                {
                    isGameOver = false;
                    break;
                }
            }
        }

        if (isGameOver)
        {
            Toast.makeText(MainActivity.this,"Game Over",Toast.LENGTH_SHORT).show();
        }
    }

    class GameData{
        private int x = 0;
        private int y = 0;
        public Bitmap bm;
        public int p_x = 0;
        public int p_y = 0;

        public GameData(int x, int y, Bitmap bm)
        {
            super();
            this.x = x;
            this.y = y;
            this.bm = bm;
            this.p_x = x;
            this.p_y = y;
        }

        public boolean isTrue() {
            if (x==p_x&&y==p_y)
            {
                return true;
            }
            return false;
        }
    }
}
