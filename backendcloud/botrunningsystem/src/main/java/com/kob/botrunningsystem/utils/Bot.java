package com.kob.botrunningsystem.utils;

import java.util.LinkedList;
import java.util.List;

public class Bot implements com.kob.botrunningsystem.utils.BotInterface {
    int headX, headY; // 记录getCells函数执行后的蛇头坐标
    int[][] g;
    int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    boolean find_inf;

    static class Cell {
        public int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    private boolean check_tail_increasing(int step) { // 检验当前回合蛇的长度是否增加
        if(step <= 10) return true;
        return step % 3 == 1;
    }
    public List<Cell> getCells(int sx, int sy, String steps) {
        steps = steps.substring(1, steps.length() - 1); // 去除steps中的括号
        List<Cell> res = new LinkedList<>();
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        headX = x;
        headY = y;
        for(int i = 0; i < steps.length(); i++){
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            headX = x;
            headY = y;
            if( ! check_tail_increasing(++ step)) {
                res.remove(0);
            }
        }
        return res;
    }

    int evaluate(int curX, int curY, int direction, int cnt) {
        int _inf = 15; // 如果可行空间长度大于15则认为可行空间无穷大
        if(find_inf || cnt >= _inf){
            find_inf = true;
            return _inf;
        }
        curX += dx[direction];
        curY += dy[direction];
        if(curX >= 0 && curX < 13 && curY >= 0 && curY < 14 && g[curX][curY] == 0) {
            g[curX][curY] = 1;
            int[] choose = {evaluate(curX, curY, 0, cnt + 1),
                    evaluate(curX, curY, 1, cnt + 1),
                    evaluate(curX, curY, 2, cnt + 1),
                    evaluate(curX, curY, 3, cnt + 1)
            };
            g[curX][curY] = 0;
            cnt = choose[0];
            for(int i = 1; i < 4; i++) {
                cnt = Math.max(cnt, choose[i]);
            }
        }

        return cnt;
    }
    @Override
    public Integer nextMove(String input) {
        String[] strs = input.split("#");
        g = new int[13][14];
        // 将一维的地图strs[0]还原为二维的g[13][14]
        for(int i = 0, k = 0; i < 13; i++) {
            for(int j = 0; j < 14; j++, k++) {
                if(strs[0].charAt(k) == '1') {
                    g[i][j] = 1;
                }
            }
        }

        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);

        List<Cell> aCells = getCells(aSx, aSy, strs[3]);
        int x = headX, y = headY; // 记录我方蛇头位置
        List<Cell> bCells = getCells(bSx, bSy, strs[6]);
        int opponentHeadX = headX, opponentHeadY = headY; // 记录敌方蛇头位置

        // 蛇所在的位置标记为1
        for(Cell c : aCells) g[c.x][c.y] = 1;
        for(Cell c : bCells) g[c.x][c.y] = 1;

        // 伪dfs搜索找到四个方向的最长路径
        int[] dec_len = new int[4];
        for(int i = 0; i < 4; i++) {
            find_inf = false;
            dec_len[i] = evaluate(x, y, i, 0);
        }
        System.out.print("dec_len：");
        for(int it : dec_len){
            System.out.print(it + " ");
        }
        System.out.println();

        int decision = 0;
        for(int i = 1; i < 4; i++) {
            if(dec_len[decision] == dec_len[i]) { //如果存在两个方向的可行长度都是四个方向中的最大值，就选择远离敌方蛇头的那个方向
                decision = Math.abs(x + dx[decision] - opponentHeadX) + Math.abs(y + dy[decision] - opponentHeadY) > Math.abs(x + dx[i] - opponentHeadX) + Math.abs(y + dy[i] - opponentHeadY) ? decision : i;
            }else{
                decision = dec_len[decision] > dec_len[i] ? decision : i;
            }
        }

        return decision; // 如果四个方向都不能走decision==3
    }
}
