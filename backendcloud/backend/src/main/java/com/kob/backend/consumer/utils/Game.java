package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    private final Integer rows, cols, inner_walls_count;
    private final int [][]g;
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private final Player playerA, playerB;
    // 记录两名玩家的下一步操作，如果为空就代表还没有获取到玩家的下一步操作
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; // playing(正在游戏) -> finished(游戏结束)
    private String loser = ""; // all(平局)、A(A输)、B(B输)
    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(
            Integer rows,
            Integer cols,
            Integer inner_walls_count,
            Integer idA,
            Bot botA,
            Integer idB,
            Bot botB
    ) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if(botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if(botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }

        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
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

    private String getInput(Player player) { // 将当前的局面信息编码成字符串
        // 地图#me.sx#me.sy#我的操作序列#you.sx#you.sy#你的操作序列
        Player me, you;
        if(playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }

        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";

    }

    private void sendBotCode(Player player) {
        if(player.getBotId().equals(-1)) return; // 如果亲自出马则不需要执行代码
        //如果玩家用的bot，且玩家还没走第一步，那么此时他的前端界面还在显示对方玩家信息，并没有进入游戏界面
        //所以此时不应该立即执行bot代码，不然返回的结果前端因为还没创建对象，所以收不到。这样会导致整场游戏都错位了。
        if(player.getSteps().size() == 0 && player.getId() != 1) {
            try {
                sleep(1000);//与前端展示对手头像的时间相同
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    private boolean nextStep(){// 等待两名玩家的下一步操作
        //在前端，一条蛇移动一个格子需要200ms，因此要在200ms后才能接收下一步操作，否则前端会用新操作覆盖旧操作
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for(int i = 0; i < 50; i++) {// 每名玩家等待10秒
            try {
                Thread.sleep(200);
                lock.lock();
                try {
                    // 如果接收到了两名玩家的下一步输入就退出函数
                    if(nextStepA != null && nextStepB != null){
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 10秒后还没有输入则判输
        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        // 如果蛇头位置是墙的话就判输
        if(g[cell.x][cell.y] == 1) return false;
        // 如果蛇头位置同时也是蛇身体位置，就判输
        for(int i = 0; i < n - 1; i++) {
            if(cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }
        for(int i = 0; i < n - 1; i++) {
            if(cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }

        return true;
    }

    private void judge() { // 判断两名玩家下一步操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if(! validA || ! validB) {
            status = "finished";
            if(! validA && ! validB){
                loser = "all";
            } else if(! validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }

    private void sendAllMessage(String message) { //向除了大黄蜂之外的所有用户发送信息
        if(playerA.getId() != 1 && WebSocketServer.users.get(playerA.getId()) != null) {
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        }
        if(playerB.getId() != 1 &&  WebSocketServer.users.get(playerB.getId()) != null) {
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
        }
    }

    private void sendMove() { // 每移动一步都要向两个Client传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            // 在玩家执行下一步操作之前，把之前的操作清空掉
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDatabase() { // 每局游戏结束后将对局记录存到数据库里
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();
        if("A".equals(loser)) {
            ratingA -= 5;
            ratingB += 5;
        } else if("B".equals(loser)) {
            ratingA += 5;
            ratingB -= 5;
        }
        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),//在数据库中用一个01字符串保存地图
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult(){ // 向两个Client公布结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {//新线程的入口就是run函数

        for(int i = 0; i < 1000; i++){
            if(nextStep()) { // 是否获取了两条蛇的下一步操作
                judge();
                if(status.equals("playing")){
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            } else { // 如果没有获取两条蛇的下一步操作也就是说nextStep函数等待了10s后没接收到蛇A或B的操作，返回了false，整个游戏就结束了
                status = "finished";
                lock.lock();
                try {
                    if(nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if(nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
