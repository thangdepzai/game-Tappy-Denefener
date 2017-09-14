package game.tappydefender.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.constraint.solver.widgets.Rectangle;

import game.tappydefender.R;

/**
 * Created by thang on 9/14/2017.
 */

public class PlayerShip {
    private Bitmap bitmap; //What it looks like
    private int x,y; //Know where it is on the screen
    private int speed; //How fast it is flying
    private boolean boosting; // kiem tra xem tau co dc kich thich hay k ?
    private final int GRAVITY = -12;
// Stop ship leaving the screen
    private int minX=0;
    private int maxX;
    private int maxY;
    private int minY;
//Limit the bounds of the ship's speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;
    private int shieldStrength;

    private Rect hitBox;


    public PlayerShip(Context context, int screenX,int screenY) {
        boosting = false;
        this.x = 50;
        this.y = 50;
        shieldStrength=3;
        this.speed = 1;
        this.bitmap =  BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        this.maxY = screenY - bitmap.getHeight();
        this.minY =0;
        this.maxX= screenX;
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());

    }
    public void reduceShieldStrength(){
        shieldStrength --; }
    public int getShieldStrength() {
        return shieldStrength;
    }

    public Rect getHitBox() {
        return hitBox;
    }

   public  void setBoosting(){
       this.boosting = true;
   }
   public  void stopBoosting(){
       this.boosting = false;
   }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void update(){
        // can giai quyet 3 van de
        // vân de 1: phai gioi han toc do cua tau
        //2. phai chan k cho tau vut khoi man hinh va k nhin thay tau lan nao nua
        //3. khi tau k tang toc va seep ->0 thì cai gi mang tau tro lai
        // Are we boosting?
        if (boosting) {
            // Speed up
            speed += 2;
        } else {
            // Slow down
            speed -= 5;  }
        // Constrain top speed
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }
        // Never stop completely
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED; }
        // move the ship up or down
        y -= speed + GRAVITY;
        // But don't let ship stray off screen
        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {    y = maxY;  }
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();



    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

}
