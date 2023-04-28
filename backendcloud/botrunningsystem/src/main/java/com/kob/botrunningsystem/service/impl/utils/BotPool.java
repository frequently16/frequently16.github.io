package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input) {
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();//唤醒其他的线程
        } finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot) {//consume函数用来取出队列中的bot并执行各个bot中的代码
        Consumer consumer = new Consumer();
        consumer.startTimeout(4000, bot); // 两个bot一共可以等待10s，因此每个bot的代码最多可运行4s比较合理
    }

    @Override
    public void run() {
        while(true) {
            lock.lock();
            if(bots.isEmpty()) {
                try {
                    condition.await();//如果队列是空的，这个线程就先阻塞住并将锁释放掉
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                Bot bot = bots.remove();
                lock.unlock();

                consume(bot); // consume函数可能要执行几秒钟，因此应该先解锁再执行consume函数
            }
        }
    }
}
