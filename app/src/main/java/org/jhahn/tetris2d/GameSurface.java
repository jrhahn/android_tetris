package org.jhahn.tetris2d;


// source: https://o7planning.org/en/10521/android-2d-game-tutorial-for-beginners

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.jhahn.tetris2d.R;

/**
 * Created by JHahn on 10/23/2018.
 */

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;


    private final int display_x;
    private final int display_y;

    private TetrisManager tm;

    public GameSurface(Context context) {
        super(context);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.e("Width", "" + width);
        Log.e("height", "" + height);

        display_x = width;
        display_y = height;

        this.setFocusable(true);

        this.getHolder().addCallback(this);
    }

    public void update() {
        tm.update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        tm.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            double dist_sqr = Math.pow(((double) tm.get_active_pos_x() - x), 2.0) + Math.pow(((double) tm.get_active_pos_y() - y), 2.0);

            if (dist_sqr < Math.pow(tm.get_block_size() * 4, 2)) {
                tm.rotate();
            } else {
                int move_x = (int) Math.signum(x - display_x / 2);

                tm.set_move(move_x, 0);
            }

            return true;
        }
        return false;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.block);
        image = getResizedBitmap(image, display_x / 10, display_x / 10);

        this.tm = new TetrisManager(image, display_x, display_y);
        this.gameThread = new GameThread(this, holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                this.gameThread.setRunning(false);
                this.gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = true;
        }
    }


}
