package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

// 要用到Autowired的时候就要加component
@Component
public class MatchingPool extends Thread{
    private static List<Player> players = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating, Integer botId) {
        lock.lock();
        try{
            players.add(new Player(userId, rating, botId, 0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try{
            List<Player> newPlayers = new ArrayList<>();
            for(Player player : players) {
                if(! player.getUserId().equals(userId)) {
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        } finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime() { // 将所有当前玩家的等待时间加1
        for(Player player : players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    private boolean checkMatched(Player a, Player b) { // 判断两名玩家是否匹配
        // 玩家匹配策略是玩家a、b之间分差小于等于a的等待时间乘以10且小于等于b的等待时间乘以10
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }

    private void sendResult(Player a, Player b) { // 返回匹配结果
        System.out.println("send result: " + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("a_bot_id", a.getBotId().toString());
        data.add("b_id", b.getUserId().toString());
        data.add("b_bot_id", b.getBotId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    private void matchPlayers() { // 尝试匹配所有玩家
        System.out.println("match players: " + players.toString());
        boolean[] used = new boolean[players.size()];
        for(int i = 0; i < players.size(); i++){
            if(used[i]) continue;
            if(players.get(i).getWaitingTime() >= 5) {//匹配池中如果有等待时间超过5s的，大黄蜂与之匹配
                used[i] = true;
                sendResult(players.get(i), new Player(1, 1500, 1, 0));
                continue;
            }
            for(int j = i + 1; j < players.size(); j++){
                if(used[i]) continue;
                Player a = players.get(i), b = players.get(j);
                if(checkMatched(a, b)) {
                    used[i] = used[j] = true;
                    try {
                        sendResult(a, b);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
            }
        }
        List<Player> newPlayers = new ArrayList<>();
        for(int i = 0; i < players.size(); i++){
            if(! used[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                lock.lock();
                try{
                    increaseWaitingTime();
                    matchPlayers();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

        }
    }
}
