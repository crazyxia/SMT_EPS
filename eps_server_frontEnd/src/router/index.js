import Vue from 'vue'
import Router from 'vue-router'
import Login from '../home/manage/user/Login'
import Display from '../home/display/Display'
import Home from '../home/Home'
import store from '../store'

Vue.use(Router);

const router = new Router({
  base: '/eps_system',
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/display',
      name: 'display',
      component: Display,
    },
    {
      path: '/home',
      name: 'home',
      component: Home,
      meta: {
        requireAuth: true,  // 进入这个路由是需要登录
      }
    }
  ]
});

if (window.localStorage.getItem('token')) {
  store.commit('setToken', window.localStorage.getItem('token'))
}


router.beforeEach((to, from, next) => {
  if (to.path === '/login' && from.path === '/home') {
    window.localStorage.clear();
    store.commit("setToken", "");
  }
  if (from.path === '/display' && to.path === '/home') {
    clearInterval(window.mainInterval);
  }
  if (to.matched.some(r => r.meta.requireAuth)) {
    if (store.state.token) {
      next();
    } else {
      next({
        path: '/login',
        query: {redirect: to.fullPath}
      })
    }
  } else {
    next();
  }
});

export default router;
