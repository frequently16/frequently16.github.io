package com.kob.backend.service.impl.utils.user.account;

import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.InfoService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service //加上Service这个注解才能注入到spring里面
public class InfoServiceImpl implements InfoService {
    @Override
<<<<<<< HEAD:backend/src/main/java/com/kob/backend/service/impl/utils/user/account/InfoServiceImpl.java
    public Map<String, String> getinfo(){
        // 如果授权成功，则从上下文中将用户信息提取出来
        // 通过authentication来找用户信息
=======
    public Map<String, String> getinfo() {
        // 在上下文中将用户信息提取出来
>>>>>>> fcc769d (后端实现注册、登录、访问用户信息，前端实现登录、退出、访问用户信息（4.2完结）):backend/src/main/java/com/kob/backend/service/impl/user/account/InfoServiceImpl.java
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
        User user = loginUser.getUser();
<<<<<<< HEAD:backend/src/main/java/com/kob/backend/service/impl/utils/user/account/InfoServiceImpl.java

        // 返回信息
=======
>>>>>>> fcc769d (后端实现注册、登录、访问用户信息，前端实现登录、退出、访问用户信息（4.2完结）):backend/src/main/java/com/kob/backend/service/impl/user/account/InfoServiceImpl.java
        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("photo", user.getPhoto());
        return map;
    }
}
