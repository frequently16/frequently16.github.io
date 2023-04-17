package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
<<<<<<< HEAD
    //mybatiesplus里的注解，来实现用户id自增
=======
    //下面一行注解的目的是让id自增
>>>>>>> fcc769d (后端实现注册、登录、访问用户信息，前端实现登录、退出、访问用户信息（4.2完结）)
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String photo;
}
