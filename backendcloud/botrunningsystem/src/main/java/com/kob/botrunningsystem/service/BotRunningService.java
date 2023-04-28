package com.kob.botrunningsystem.service;

public interface BotRunningService {
    // userId:当前用户是谁，botCode:执行的代码，input:当前的游戏情况
    String addBot(Integer userId, String botCode, String input);

}
