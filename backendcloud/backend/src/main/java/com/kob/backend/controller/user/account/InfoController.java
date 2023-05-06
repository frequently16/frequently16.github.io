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

    // 获取信息用Get，修改、删除、添加信息用Post
    @GetMapping("/api/user/account/info/")
    public Map<String, String> getinfo(){
        return infoService.getinfo();
    }
}
