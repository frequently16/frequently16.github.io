import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView'
import RecordIndexView from '../views/record/RecordIndexView'
import RanklistIndexView from '../views/ranklist/RanklistIndexView'
import UserBotIndexView from '../views/user/bot/UserBotIndexView'
import NotFound from '../views/error/NotFound'
import UserAccountLoginView from '../views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '../views/user/account/UserAccountRegisterView'
import store from '../store/index'

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/404/",
    name: "404",
    component: NotFound,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
// // 在router起作用之前执行的函数
// // to表示跳转到哪个页面，from表示从哪个页面跳转过去的，next表示要不要执行下一步操作
// router.beforeEach((to, from, next) => {
//   const jwt_token = localStorage.getItem("jwt_token");
//   if(jwt_token) {
//     // updateToken这个函数存在于mutations里面，因此需要用commit来调用这个函数
//     store.commit("updateToken", jwt_token);
//     // 从服务器端获取信息，即调用actions里面的getinfo函数，因此要用dispatch
//     store.dispatch("getinfo", {
//       success(){},
//       error(){}
//     })
//   }
//   // 每次在跳转页面之前都要判断一下需不需要登录，如果这个页面需要登录的话就自动跳转到登录页面
//   if(to.meta.requestAuth && ! store.state.user.is_login) { // 如果要跳转的页面要登录且用户当前未登录，则跳转到登录页面
//     next({name: "user_account_login"});
//   } else {
//     next();
//     store.commit("updatePullingInfo", false);    
//   }
// })
router.beforeEach((to, from, next) => {
  if (to.meta.requestAuth && !store.state.user.is_login) {
    next({name: "user_account_login"});
  } else {
    next();
  }
})


export default router
