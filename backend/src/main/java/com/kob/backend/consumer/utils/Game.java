package com.kob.backend.consumer.utils;

import java.util.Random;

public class Game {
    final private Integer rows, cols, inner_walls_count;
    final int [][]g;
    final private static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    public Game(Integer rows, Integer cols, Integer inner_walls_count) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
    }

    public int[][] getG() { // 返回地图
        return g;
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if(sx == tx && sy == ty) return true;
        g[sx][sy] = 1;
        for(int i = 0; i < 4; i++) {
            int x = sx + dx[i], y = sy + dy[i];
            if(x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if(check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
     }

    private boolean draw() { // 画地图,这个函数每执行一次就随机生成一张地图
        // 初始化地图
        for(int i = 0; i < this.rows; i++) {
            for(int j = 0; j < this.cols; j++) {
                g[i][j] = 0;
            }
        }
        // 给四周加上墙
        for(int r = 0; r < this.rows; r++) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for(int c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }
        // 随机生成障碍物
        Random random = new Random();
        for(int i = 0; i < this.inner_walls_count / 2; i++) {
            for(int j = 0; j < 1000; j++) {
                int r = random.nextInt(this.rows);//返回0~this.rows中的随机值（左闭右开）
                int c = random.nextInt(this.cols);
                // 两个障碍物不能重叠在一处
                if(g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) {
                    continue;
                }
                // 起点和终点不能有障碍物
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) {
                    continue;
                }

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {//
        for(int i = 0; i < 1000; i++) {
            if(draw())
                break;
        }
    }
}
