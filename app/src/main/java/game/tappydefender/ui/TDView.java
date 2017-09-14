package game.tappydefender.ui;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

import game.tappydefender.model.Enemies;
import game.tappydefender.model.PlayerShip;
import game.tappydefender.model.SpaceDust;

/**
 * Created by thang on 9/14/2017.
 */

public class TDView extends SurfaceView implements Runnable {
    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int win = -1;
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private boolean isWin = false;
    private long fastestTime = Long.MAX_VALUE;
    private Context context;
    private int screenX;
    private int screenY;
    private boolean gameEnded;
    volatile boolean playing;
    Thread gameThread = null;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    PlayerShip player;
    ArrayList<SpaceDust> dataDust = new ArrayList<>();
    ArrayList<Enemies> dataEne = new ArrayList<>();
    public TDView(Context context, int screenX,int screenY) {
        super(context);
        this.context =context;
        this.screenX =screenX;
        this.screenY = screenY;
        ourHolder = getHolder();
        paint = new Paint();
        // This SoundPool is deprecated but don't worry
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try {
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            //create our three fx in memory ready for use
            descriptor = assetManager.openFd("start.ogg");
            start = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("win.ogg");

            win = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("bump.ogg");
            bump = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("destroyed.ogg");
            destroyed = soundPool.load(descriptor, 0);
        }catch(IOException e){    //Print an error message to the console
            Log.e("error", "failed to load sound files");  }

        StartGame();
    }
    private  void StartGame(){

        soundPool.play(start, 1, 1, 0, 0, 1);
        isWin =false;
        dataEne = new ArrayList<>();
        gameEnded = false;
        player = new PlayerShip(context,screenX,screenY);

        dataEne.add(new Enemies(context,screenX,screenY,0));
        dataEne.add(new Enemies(context,screenX,screenY,1));
        dataEne.add(new Enemies(context,screenX,screenY,2));

        for(int i=0; i<40;i++){
            dataDust.add(new SpaceDust(screenX,screenY));
        }
        // Reset time and distance
        distanceRemaining = 10000;// 10 km
        timeTaken = 0;
        // Get start time
        timeStarted = System.currentTimeMillis();

    }

    @Override
    public void run() {
        while (playing){
            update(); 
            draw();           
            control();
        }

    }
// chi chay 60 lan moi s
    private void update() {
        player.update();
        for(int i=0;i<dataEne.size();i++){
            dataEne.get(i).update(player.getSpeed());
        }
        for(int i=0;i<40;i++){
            dataDust.get(i).update(player.getSpeed());
        }
        // Collision detection on new positions
        // Before move because we are testing last frames
        // position which has just been drawn
// If you are using images in excess of 100 pixels
// wide then increase the -250 value accordingly

        boolean hitDetected = false;
        for(Enemies e:dataEne){
            if(Rect.intersects(player.getHitBox(),e.getHitBox())){
                hitDetected = true;
                e.setX(screenX);
            }
        }
        if(hitDetected){
            soundPool.play(bump, 1, 1, 0, 0, 1);
            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0) {
                soundPool.play(destroyed, 1, 1, 0, 0, 1);
                gameEnded = true;
                //game over so do something
                }
        }
        if(!gameEnded) {
            //subtract distance to home planet based on current speed
            distanceRemaining -= player.getSpeed();
            //How long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
            //Completed the game!
            if (distanceRemaining <=0) {
                soundPool.play(win, 1, 1, 0, 0, 1);
                //check for new fastest time
                if (timeTaken < fastestTime) {
                    fastestTime = timeTaken;
                }
                // avoid ugly negative numbers
                // in the HUD
                distanceRemaining = 0;
                // Now end the game
                isWin = true;
                gameEnded = true;
            }
        }
    }

    private void control() {
//        Tại thời điểm này, chúng tôi thực sự có thể chạy các trò chơi.
//                Nếu thị lực của chúng tôi đủ nhanh hoặc thiết bị Android của chúng tôi đủ chậm,
//        chúng tôi sẽ chỉ nhìn thấy máy bay của chúng tôi của người chơi bay qua màn hình với tốc độ to lớn.
//                Chỉ còn một việc phải làm trước khi chúng tôi triển khai trò chơi của chúng tôi cho đến nay.
//        Sáu mươi khung hình mỗi giây (FPS) là một mục tiêu hợp lý.
//        Mục tiêu này hàm ý cần thời gian. Hệ thống Android đo thời gian bằng mili giây (phần nghìn giây).
//                Vì vậy, chúng ta có thể thêm mã sau vào phương thức điều khiển:
        try {
            gameThread.sleep(17);
        }
        catch (InterruptedException e) {
        }
    }
    private void draw() {

        if (ourHolder.getSurface().isValid()) {
            //First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();
            // Rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            paint.setColor(Color.argb(255,255,255,255));
            for(int i=0;i<dataDust.size();i++){
                canvas.drawPoint(dataDust.get(i).getX(),
                        dataDust.get(i).getY(),
                        paint
                );
            }
            // Draw the player

            canvas.drawBitmap(player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            for(Enemies e:dataEne){

                canvas.drawBitmap(e.getBitmap(),
                        e.getX(),
                        e.getY(),
                        paint);

            }
            if(!gameEnded) {
                // Draw the hud
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                if(!(fastestTime== Long.MAX_VALUE)) {
                    canvas.drawText("Fastest:" + fastestTime / 1000 + "s", 10, 20, paint);
                } else canvas.drawText("Fastest:" + "vô cùng" + "s", 10, 20, paint);
                canvas.drawText("Time:" + timeTaken/1000 + "s", screenX / 2, 20, paint);
                canvas.drawText("Distance:" + distanceRemaining / 1000 + " KM", screenX / 3, screenY - 20, paint);
                canvas.drawText("Shield:" + player.getShieldStrength(), 10, screenY - 20, paint);
                canvas.drawText("Speed:" + player.getSpeed() * 60 + " MPS", (screenX / 3) * 2, screenY - 20, paint);
            }else{

                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                if(!isWin) {
                    canvas.drawText("Game Over", screenX / 2, 100, paint);
                }else  canvas.drawText("Winner", screenX / 2, 100, paint);
                paint.setTextSize(25);
                if(!(fastestTime== Long.MAX_VALUE)) {
                    canvas.drawText("Fastest:" + fastestTime / 1000 + "s", 10, 20, paint);
                } else canvas.drawText("Fastest:" + "vô cùng" + "s", 10, 20, paint);
                canvas.drawText("Time:" + timeTaken/1000 +   "s", screenX / 2, 200, paint);
                canvas.drawText("Distance remaining:" +   distanceRemaining/1000 + " KM",screenX/2, 240, paint);                paint.setTextSize(80); canvas.drawText("Tap to replay!", screenX/2, 350, paint);

            }

            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }
    public  void pause(){
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    // Make a new thread and start it
    // Execution moves to our R
    public void resum(){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // Has the player lifted their finger up? (người dùng nhấc tay khỏi màn hình)
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            // Has the player touched the screen?(người dùng chạm tay vào màn hình)
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                if(gameEnded){
                    StartGame();   }
                break;
        }
        return true; }

//    Đây là cách phương pháp onTouchEvent hoạt động cho đến nay.
//    Người chơi chạm vào màn hình; điều này có thể là bất kỳ loại liên lạc nào cả. Nó có thể là vuốt, nhúm, nhiều ngón tay, vân vân.
//    Một thông báo chi tiết được gửi đến phương thức onTouchEvent.
//    Các chi tiết của sự kiện được chứa trong tham số lớp MotionEvent, như chúng ta có thể thấy trong mã của chúng ta. Lớp MotionEvent chứa rất nhiều dữ liệu.
//    Nó biết có bao nhiêu ngón tay đã được đặt trên màn hình, tọa độ của mỗi, và nếu có cử chỉ cũng được thực hiện
    }




