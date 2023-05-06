package com.kob.botrunningsystem.service.impl.utils;

import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class Consumer extends Thread {
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }

    // 传入timeout这个参数表示若执行时间超过这么多则停止执行以防止bot代码里有死循环
    public void startTimeout(long timeout, Bot bot) {
        this.bot = bot;
        this.start(); //开一个新的线程执行run函数，当前线程继续执行下面的代码

        try {
            this.join(timeout); // 当start函数开的线程执行完毕或者那个线程执行了timeout这么久时，才会继续执行下面的代码
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt(); // 不论start函数开的线程是否执行完毕，都中断它
        }

    }

    private String addUid(String code, String uid) { //在code中的Bot类名后面加上uid
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);//使用uid是为了保证每次类名不一样

        Supplier<Integer> botInterface = Reflect.compile(
            "com.kob.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        File file = new File("input.txt");
        try(PrintWriter fout = new PrintWriter(file)) {
            fout.println(bot.getInput());
            fout.flush();//清空缓冲区，防止文件里读不到信息
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Integer direction = botInterface.get();
        System.out.println("move-direction: " + bot.getUserId() + " " + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());
        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }
}
