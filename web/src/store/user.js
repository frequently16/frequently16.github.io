import $ from 'jquery'
// store用来存储用户信息，以便每次打开不同的页面都能读取用户信息
export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
    },
    getters: {
    },
    mutations: { // mutations用来修改用户信息
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {// 把state中存下来的用户信息都删掉就是退出
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        }
    },
    actions: { //actions里面都是修改state的函数
        login(context, data) {
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/token/",
                type: "post",
                data: {
                  username: data.username,
                  password: data.password,
                },
                success(resp){
                    if(resp.error_message === "success") {
                        // 如果成功的话就将token存下来
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp){
                    data.error(resp);
                }
            });
        },
        getinfo(context, data) {
            $.ajax({
              url: "http://127.0.0.1:3000/user/account/info/",
              type: "get",
              headers: {//需要传一个表头，传到Authorization(授权)里面
                Authorization: "Bearer " + context.state.token,
              },
              success(resp) {
                if(resp.error_message === "success") {
                    context.commit("updateUser", {
                        ...resp, // 将resp内容解构出来，也就是把resp里面各个属性放到当前对象里
                        is_login: true,
                    });
                    data.success(resp);
                } else {
                    data.error(resp);
                }
              },
              error(resp) {
                data.error(resp);
              }
            });
        },
        logout(context) {
            context.commit("logout");
        }
    },
    modules: {
    }
}