package org.jhahn.tetris2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TetrisManager {
    private int size_grid_x;
    private int size_grid_y;

    private Bitmap image;

    private TetrisBlock block_active;
    private int[][] array;

    private int score = 0;

    public TetrisManager(Bitmap image, int display_x, int display_y) {
        this.image = image;
        this.size_grid_x = display_x / image.getWidth();
        this.size_grid_y = display_y / image.getHeight();

        this.array = new int[size_grid_x][size_grid_y];

        create_new_block();
    }

    private void create_new_block() {
        block_active = new TetrisBlock(image, 0, 0, size_grid_x, size_grid_y);
    }

    public void draw(Canvas canvas) {
        block_active.draw(canvas);

        for (int x = 0; x < this.size_grid_x; x++) {
            for (int y = 0; y < this.size_grid_y; y++) {
                if (this.array[x][y] == 1) {
                    canvas.drawBitmap(image, x * image.getWidth(), y * image.getHeight(), null);
                }
            }
        }
    }

    public void set_move(int move_x, int move_y) {
        block_active.setMovingVector(move_x, move_y);
    }

    public void rotate() {
        block_active.rotate();
    }

    public int get_block_size() {
        return image.getWidth();
    }

    public int get_active_pos_x() {
        return block_active.get_x();
    }

    public int get_active_pos_y() {
        return block_active.get_y();
    }

    private void check_if_line_is_full() {
        for (int y = 0; y < array[0].length; y++) {
            boolean is_complete = true;

            for (int x = 0; x < array.length; x++) {
                is_complete = this.array[x][y] > 0 && is_complete;
            }

            if (is_complete) {
                score++;
                for (int y_ = y; y_ > 0; y_--) {
                    for (int x = 0; x < array.length; x++) {
                        this.array[x][y_] = this.array[x][y_ - 1];
                    }
                }
            }
        }
    }

    public void update() {
        this.array = block_active.update(this.array);

        if (!block_active.is_active()) {
            create_new_block();

            check_if_line_is_full();
        }
    }
}
