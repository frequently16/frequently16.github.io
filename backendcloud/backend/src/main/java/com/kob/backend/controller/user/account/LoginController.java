package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    //用post不用get是因为post用密文传输，更安全
    @PostMapping("/api/user/account/token/")
    // getToken方法需要传入两个信息（username、password），服务器需要从post请求中把参数拿出来放到一个map里面
    public Map<String, String> getToken(@RequestParam Map<String, String> map){
        String username = map.get("username");
        String password = map.get("password");
        return loginService.getToken(username, password);
    }
}
