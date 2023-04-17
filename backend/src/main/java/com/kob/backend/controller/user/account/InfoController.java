package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InfoController {
    @Autowired
    private InfoService infoService;
<<<<<<< HEAD

    // 获取信息用Get，修改、删除、添加信息用Post
=======
>>>>>>> fcc769d (后端实现注册、登录、访问用户信息，前端实现登录、退出、访问用户信息（4.2完结）)
    @GetMapping("/user/account/info/")
    public Map<String, String> getinfo(){
        return infoService.getinfo();
    }
}
