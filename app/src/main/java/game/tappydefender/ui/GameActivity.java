package game.tappydefender.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import java.util.ArrayList;

import game.tappydefender.model.Enemies;

public class GameActivity extends Activity {
    private TDView gameView;

    // This is where the "Play" button from HomeActivity sends us
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details ( truy cap den thong so chi tiet cua man hinh)
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object ( load dộ phân giải vào đối tượng point)
        Point size = new Point();
        display.getSize(size);
        gameView = new TDView(this, size.x,size.y);

        setContentView(gameView);

    }
    // If the Activity is paused make sure to pause our thread
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
    // If the Activity is resumed make sure to resumed our thread
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resum();
    }
}
