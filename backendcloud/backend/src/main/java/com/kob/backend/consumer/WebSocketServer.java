package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer implements Cloneable {
    // 要能够通过用户id找到对应的链接，因此所有的链接都要存放在一个全局变量里面
    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    // 要知道每个链接对应的用户是谁
    private User user;
    private Session session = null;

    // 定义全局变量用来访问数据库
    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static BotMapper botMapper;

    public static RestTemplate restTemplate;
    public Game game = null;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }

    // 加了Autowired这个注解，引用到RestTempl类变量时就会找是否有唯一的带有@Bean注解的返回值是RestTempl变量的函数
    // 如果有，就调用函数并赋给变量函数的返回值
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;

        System.out.println("connected!");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if(this.user != null){
            users.put(userId, this);
        } else {
            this.session.close();
        }

        if(! users.containsKey(1)) { // 如果大黄蜂没有登录，则登录
            WebSocketServer yellowBee = (WebSocketServer) this.clone();
            yellowBee.user = userMapper.selectById(1);
            users.put(1, yellowBee);
        }

        System.out.println(users);
    }

    // 浅克隆WebSocketServer对象
    @Override
    public Object clone() {
        WebSocketServer newObj = null;
        try{
            newObj = (WebSocketServer)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return newObj;
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected!");
        if(this.user != null) {
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId), botB = botMapper.selectById(bBotId);

        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB
        );
        game.createMap();
        if(users.get(a.getId()) != null){
            users.get(a.getId()).game = game;
        }
        if(users.get(b.getId()) != null){
            users.get(b.getId()).game = game;
        }

        game.start();//另起一个线程

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());
        //生成的地图和玩家起始坐标存放在respGame里面，respGame放在respA和respB里面并发送给前端

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        if(a.getId() != 1 && users.get(a.getId()) != null) {
            users.get(a.getId()).sendMessage(respA.toJSONString());
        }

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        if(b.getId() != 1 && users.get(b.getId()) != null) {
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void startMatching(Integer botId) {
        System.out.println("start matching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        data.add("bot_id", botId.toString());
        restTemplate.postForObject(addPlayerUrl, data, String.class);//String.class表示返回值的类型
    }
    private void stopMatching() {
        System.out.println("stop matching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(int direction) {
        if(game.getPlayerA().getId().equals(user.getId())){
            if(game.getPlayerA().getBotId().equals(-1)) // 只有是亲自出马才会接收用户的指令
                game.setNextStepA(direction);
        }else if(game.getPlayerB().getId().equals(user.getId())){
            if(game.getPlayerB().getBotId().equals(-1)) // 只有是亲自出马才会接收用户的指令
                game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println("receive message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)) {
            startMatching(data.getInteger("bot_id"));
        } else if("stop-matching".equals(event)) {
            stopMatching();
        } else if("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        // 向Client发送信息
        synchronized (this.session) {//因为发送信息是异步的，所以要加锁
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();//为了方便调试，把异常直接输出
            }
        }
    }
}
