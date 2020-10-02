package org.jhahn.tetris2d;

/**
 * Created by JHahn on 10/22/2018.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


enum BlockType {
    L, L_inv, Triangle, Block, Bar, Z, S;

    private static final List<BlockType> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static BlockType random() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}

public class TetrisBlock {
    protected Bitmap image;

    protected int x_grid;
    protected int y_grid;

    private long last_update = -1;
    private long last_drop = -1;

    private final int size_grid_x;
    private final int size_grid_y;

    private int bb_left;
    private int bb_right;
    private int bb_bottom;
    private int bb_top;

    private int move_y_grid = 0;
    private int move_x_grid = 0;

    private int x_world = 0;
    private int y_world = 0;

    private int current_rotation = 0;

    private final static int[][][] L_array = {
            {
                    {0, 1, 0},
                    {0, 1, 0},
                    {0, 1, 1}
            },
            {
                    {0, 0, 0},
                    {1, 1, 1},
                    {1, 0, 0}
            },
            {
                    {1, 1, 0},
                    {0, 1, 0},
                    {0, 1, 0}
            },
            {
                    {0, 0, 1},
                    {1, 1, 1},
                    {0, 0, 0}
            }};
    private final static int[][][] L_inv_array = {
            {
                    {0, 1, 0},
                    {0, 1, 0},
                    {1, 1, 0}
            },
            {
                    {1, 0, 0},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            {
                    {0, 1, 1},
                    {0, 1, 0},
                    {0, 1, 0}
            },
            {
                    {0, 0, 0},
                    {1, 1, 1},
                    {0, 0, 1}
            }
    };
    private final static int[][][] Triangle_array = {
            {
                    {0, 1, 0},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            {
                    {0, 1, 0},
                    {0, 1, 1},
                    {0, 1, 0}
            },
            {
                    {0, 0, 0},
                    {1, 1, 1},
                    {0, 1, 0}
            },
            {
                    {0, 1, 0},
                    {1, 1, 0},
                    {0, 1, 0}
            }
    };
    private final static int[][][] Block_array = {{
            {1, 1},
            {1, 1}}};
    private final static int[][][] Bar_array = {
            {
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
                    {1, 1, 1, 1},
                    {0, 0, 0, 0}
            },
            {
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
                    {0, 1, 0, 0}
            },
            {
                    {0, 0, 0, 0},
                    {1, 1, 1, 1},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
            },
            {
                    {0, 0, 1, 0},
                    {0, 0, 1, 0},
                    {0, 0, 1, 0},
                    {0, 0, 1, 0}
            }
    };
    private final static int[][][] Z_array = {
            {
                    {0, 0, 0},
                    {1, 1, 0},
                    {0, 1, 1}
            },
            {
                    {0, 0, 1},
                    {0, 1, 1},
                    {0, 1, 0}
            }
    };
    private final static int[][][] S_array = {
            {
                    {0, 0, 0},
                    {0, 1, 1},
                    {1, 1, 0}
            },
            {
                    {1, 0, 0},
                    {1, 1, 0},
                    {0, 1, 0}
            }
    };


    private BlockType type = BlockType.L;

    private int[][][] array;

    private boolean is_active;

    public void rotate() {
        current_rotation++;

        if (current_rotation >= this.array.length)
            current_rotation = 0;

        update_bounding_box();
    }

    public void update_bounding_box() {
        int x_min = array[current_rotation].length;
        int x_max = 0;

        int y_min = array[current_rotation][0].length;
        int y_max = 0;

        for (int x = 0; x < this.array[current_rotation].length; x++) {
            for (int y = 0; y < this.array[current_rotation][0].length; y++) {
                if (array[current_rotation][x][y] == 1) {
                    x_min = Math.min(x, x_min);
                    x_max = Math.max(x, x_max);
                    y_min = Math.min(y, y_min);
                    y_max = Math.max(y, y_max);
                }
            }
        }

        this.bb_left = x_min;
        this.bb_bottom = y_max;
        this.bb_top = y_min;
        this.bb_right = x_max;
    }


    public TetrisBlock(Bitmap image, int x_grid, int y_grid, int size_grid_x, int size_grid_y) {
        this.image = image;

        this.x_grid = ThreadLocalRandom.current().nextInt(3, size_grid_x - 3 + 1);

        this.is_active = true;

        type = BlockType.random();

        switch (type) {
            case L:
                this.array = L_array;
                break;
            case Triangle:
                this.array = Triangle_array;
                break;
            case S:
                this.array = S_array;
                break;
            case Z:
                this.array = Z_array;
                break;
            case Bar:
                this.array = Bar_array;
                break;
            case Block:
                this.array = Block_array;
                break;
            case L_inv:
                this.array = L_inv_array;
                break;
        }

        update_bounding_box();

        this.x_grid = x_grid;
        this.y_grid = y_grid;

        this.size_grid_x = size_grid_x;
        this.size_grid_y = size_grid_y;
    }

    public void drop(int[][] array) {
        long now = System.nanoTime();

        if (-1 == last_drop) {
            last_drop = now;
        }

        int delta_time = (int) ((now - last_drop) / 1000000);

        Log.i("", "attempt to drop block type: " + type + " " + delta_time);
        if (delta_time > 1000) {
            last_drop = now;

            if (!is_blocked(array, x_grid, y_grid + 1)) {
                y_grid++;
                Log.i("", "chel done.");
            } else {
                is_active = false;
            }

            Log.i("", "  >> hmm @" + y_grid + " " + y_world);

            y_grid = check_y_grid(y_grid);

            Log.i("", "  >> succeded @" + y_grid + " " + y_world);
        }
    }

    public void process_input(int[][] array_world) {
        long now = System.nanoTime();

        if (-1 == last_update) {
            last_update = now;
        }

        int delta_time = (int) ((now - last_update) / 1000000);

        if (delta_time > 300) {
            last_update = now;

            if (!is_blocked(array_world, x_grid + move_x_grid, y_grid)) {
                x_grid += move_x_grid;
            }

            if (!is_blocked(array_world, x_grid, y_grid + move_y_grid)) {
                y_grid += move_y_grid;
            }

            move_x_grid = 0;
            move_y_grid = 0;

            x_grid = check_x_grid(x_grid);
            y_grid = check_y_grid(y_grid);
        }
    }

    public int[][] update(int[][] array) {
        if (this.is_active) {
            drop(array);
            process_input(array);

            if (!this.is_active) {
                return update_grid(array);
            }
        }

        return array;
    }

    public int[][] update_grid(int[][] array) {
        for (int x = bb_left; x < bb_right + 1; x++) {
            for (int y = bb_top; y < bb_bottom + 1; y++) {
                if (this.array[current_rotation][x][y] == 1) {
                    Log.i("BAM", "x:" + x + x_grid + "/" + array[current_rotation].length + " y:" + y + y_grid + "/" + array[0].length);

                    array[x + x_grid][y + y_grid] += this.array[current_rotation][x][y];

                    if (array[x + x_grid][y + y_grid] > 1) {
                        Log.e("", "SHOULD NOT HAPPEN");
                    }
                }
            }
        }
        return array;
    }

    public boolean is_blocked(int[][] array, int x_grid, int y_grid) {
        Log.i("", "check overlap.");

        for (int x = bb_left; x < bb_right + 1; x++) {
            for (int y = bb_top; y < bb_bottom + 1; y++) {
                Log.i("BAM", "x:" + (x + x_grid) + "/" + array[current_rotation].length + " y:" + (y + y_grid) + "/" + array[0].length);
                Log.i("", "bb_left:" + bb_left);

                if (this.array[current_rotation][x][y] == 1) {
                    if (y + y_grid >= size_grid_y) {
                        return true;
                    }

                    if (x + x_grid < 0 || x + x_grid >= size_grid_x) {
                        return true;
                    }

                    int val = array[x + x_grid][y + y_grid] + this.array[current_rotation][x][y];

                    Log.i("", "x:" + x + " y:" + y);

                    if (val > 1) {
                        Log.i("", "return true");
                        return true;
                    }
                }
            }
        }

        Log.i("", "return false");
        return false;
    }

    public boolean is_active() {
        return is_active;
    }

    public int check_y_grid(int y_grid) {
        if (y_grid < 0) {
            y_grid = 0;
        } else if (y_grid + bb_bottom > this.size_grid_y) {
            y_grid = this.size_grid_y - bb_bottom;
            is_active = false;
        }

        return y_grid;
    }

    public int check_x_grid(int x_grid) {
        Log.i("check_grid", "bb_left:" + bb_left);
        if (x_grid + bb_left < 0) {
            x_grid = -bb_left;
            Log.i("", "x_grid:" + x_grid + " bb_left:" + bb_left);
        } else if (x_grid + bb_right > this.size_grid_x) {
            x_grid = size_grid_x - bb_right;
        }

        return x_grid;
    }

    public void draw(Canvas canvas) {
        for (int x = 0; x < this.array[current_rotation].length; x++) {
            for (int y = 0; y < this.array[current_rotation][0].length; y++) {

                if (this.array[current_rotation][x][y] != 0) {
                    int x_world = (x + x_grid) * image.getWidth();
                    int y_world = (y + y_grid) * image.getHeight();
                    canvas.drawBitmap(image, x_world, y_world, null);
                }
            }
        }

        this.x_world = (int) ((x_grid + (bb_right - bb_left) / 2.0) * image.getWidth());
        this.y_world = (int) ((y_grid + (bb_bottom - bb_top) / 2.0) * image.getHeight());
    }

    public void setMovingVector(int move_x_grid, int move_y_grid) {
        this.move_x_grid = move_x_grid;
        this.move_y_grid = move_y_grid;
    }

    public int get_x() {
        return x_world;
    }

    public int get_y() {
        return y_world;
    }
}
