package game.tappydefender.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

import game.tappydefender.R;

/**
 * Created by thang on 9/14/2017.
 */
// xay dung lop ke thù
public class Enemies {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;
    // Detect enemies leaving the screen
    private int maxX;
    private int minX;
    // Spawn enemies within screen bounds
    private int maxY;
    private int minY;
    private Rect hitBox;

    public Enemies(Context context,int screenX,int screenY,int i) {
        if(i==0) {
            this.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        }else if(i==1){
            this.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy2);
        }else if(i==2){
            this.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy3);
        }

        this.x = x;
        this.y = y;
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;
        Random generator = new Random();
        speed = generator.nextInt(6)+10;
        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());

    }
    public  void update(int playSpeed){
        // Move to the left
        x -=playSpeed;
        x-= speed;
        //respawn when off screen
        if(x < minX-bitmap.getWidth()){
            Random generator = new Random();
            speed = generator.nextInt(10)+10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();  }

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();


    }
    // This is used by the TDView update() method to
    // Make an enemy out of bounds and force a re-spawn


    public void setX(int x) {  this.x = x; }
    public Rect getHitBox(){  return hitBox; }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
