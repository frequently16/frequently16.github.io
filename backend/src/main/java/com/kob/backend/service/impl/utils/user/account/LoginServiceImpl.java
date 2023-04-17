package com.kob.backend.service.impl.utils.user.account;

import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.LoginService;
import com.kob.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    //验证用户是否已经登录
    private AuthenticationManager authenticationManager;
<<<<<<< HEAD:backend/src/main/java/com/kob/backend/service/impl/utils/user/account/LoginServiceImpl.java


    @Override
    public Map<String, String> getToken(String username, String password) {
        //UsernamePasswordAuthenticationToken这个类里面存的是加密的字符串而不是明文
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        // 下面一行代码用来验证是否可以正常登录
        // 如果登录失败，会自动处理
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

=======
    @Override
    public Map<String, String> getToken(String username, String password) {
        // 这个对象把接收到的用户名和密码封装起来，生成暗文
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        // 下面这一行类用来验证用户是否登录成功，如果登录失败，会自动处理。authentication（认证）
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 将用户信息取出来
>>>>>>> fcc769d (后端实现注册、登录、访问用户信息，前端实现登录、退出、访问用户信息（4.2完结）):backend/src/main/java/com/kob/backend/service/impl/user/account/LoginServiceImpl.java
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        // 把用户取出来
        User user = loginUser.getUser();
<<<<<<< HEAD:backend/src/main/java/com/kob/backend/service/impl/utils/user/account/LoginServiceImpl.java
        // 将userid封装成一个jwt token
        String jwt = JwtUtil.createJWT(user.getId().toString());

        //定义一个返回结果
=======
        // 将userid封装成一个jwt-token
        String jwt = JwtUtil.createJWT(user.getId().toString());
        // 定义一个返回结果
>>>>>>> fcc769d (后端实现注册、登录、访问用户信息，前端实现登录、退出、访问用户信息（4.2完结）):backend/src/main/java/com/kob/backend/service/impl/user/account/LoginServiceImpl.java
        Map<String, String> map = new HashMap<>();
        // 自定义一个error_message关键字用来映射登录状态
        map.put("error_message", "success");//如果能执行到这行代码，说明一定登录成功了
        // 自定义一个token关键字来映射jwt-token
        map.put("token", jwt);
        return map;
    }
}
