package com.kob.backend.controller.pk;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//pk模块都在链接后的/pk/里面，RequestMapping表示父目录
public class indexController {
    @RequestMapping("/")
    public String index(){
        //return的值表示templates下pk下的index.html文件
        return "pk/index.html";
    }
}
