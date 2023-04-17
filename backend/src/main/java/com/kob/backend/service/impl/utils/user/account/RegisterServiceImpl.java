package com.kob.backend.service.impl.utils.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    // @Autowired用来访问数据库
    @Autowired
    // 把用户的数据导入进来，以判断新注册用户名是否重复
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if(username == null) {
            map.put("error_message", "用户名不能为空");
            return map;
        }
        if(password == null || confirmedPassword == null) {
            map.put("error_message", "密码不能为空");
            return map;
        }

        username = username.trim();
        if(username.length() == 0){
            map.put("error_message", "用户名不能为空");
            return map;
        }

        if(password.length() == 0 || confirmedPassword.length() == 0){
            map.put("error_message", "密码不能为空");
            return map;
        }

        if(username.length() > 100){
            map.put("error_message", "用户名长度不能大于100");
            return map;
        }

        if(password.length() > 100 || confirmedPassword.length() > 100){
            map.put("error_message", "密码长度不能大于100");
            return map;
        }

        if(! password.equals(confirmedPassword)){
            map.put("error_message", "两次输入的密码不一致");
            return map;
        }
        // 从pojo层里读取所有用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        //数据库中所有与新注册的用户名相同的用户信息都存在了一个叫users的List变量里面了
        List<User> users = userMapper.selectList(queryWrapper);
        if(! users.isEmpty()){
            map.put("error_message", "用户名已存在");
            return map;
        }

        //如果用户输入的注册信息合规，则将密码加密再存到数据库里
        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/263323_lg_b117825085.jpg";
        User user = new User(null, username, encodedPassword, photo);
        userMapper.insert(user);

        map.put("error_message", "success");
        return map;
    }
}
