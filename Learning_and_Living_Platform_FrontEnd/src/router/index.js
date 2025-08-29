import { createRouter, createWebHistory } from 'vue-router'
import { get } from '@/net'

const router = createRouter({
  // history: createWebHashHistory(),
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'mainView',
      component: () => import('@/views/MainView.vue'),
      children: [
        {
          path: '',
          name: 'guide',
          component: () => import('@/components/GuidePage.vue')
        },
        {
          path: 'test',
          name: 'test',
          component: () => import('@/components/TestResPage.vue'),
        },
        {
          path: 'resources/:page',
          name: 'resources',
          component: () => import('@/components/ResourcesPage.vue'),
          props: (route) => ({ page: Number(route.params.page || 1) }),
        },{
          path: 'resource/:resId',
          name: 'resourceDetail',
          component: () => import('@/components/resources/ResourceDetailPage.vue'),
          // 如果参数 resId 缺省，则会被重定向到资源列表页，所以这里不提供默认值
          props: (route) => ({ resId: Number(route.params.resId) }),
        },{
          path: 'posts/:page',
          name: 'posts',
          component: () => import('@/components/PostPage.vue'),
          props: (route) => ({ page: Number(route.params.page || 1) }),
        },
        {
          path: 'post/:postId',
          name: 'postDetail',
          component: () => import('@/components/posts/PostDetailPage.vue'),
          // 如果参数 postId 缺省，则会被重定向到帖子列表页，所以这里不提供默认值
          props: (route) => ({ postId: Number(route.params.postId) }),
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('@/components/PersonalPage.vue')
        },
        {
          path: 'user/:userId',
          name: 'user',
          component: () => import('@/components/OthersPage.vue'),
          props: (route) => ({ userId: Number(route.params.userId) }),
        },
        {
          path: 'manage',
          name: 'manage',
          component: () => import('@/components/ManageUserPage.vue')
        },
        {
          path: 'statistics',
          name: 'statistics',
          component: () => import('@/components/StatisticsPage.vue')
        },
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LogRegView.vue')
    },
    {
      path: '/tinymce',
      name: 'tinymce',
      component: () => import('@/components/posts/Tinymce.vue')
    },
    {
      path: '/clouds',
      name: 'clouds',
      component: () => import('@/views/ExamplePage.vue')
    },
    {
      path: '/posts',
      redirect: '/posts/1'
    },
    {
      path: '/post',
      redirect: '/posts'
    },

    {
      path: '/resources',
      redirect: '/resources/1'
    },
    {
      path: '/resource',
      redirect: '/resources'
    },
   ]
})

// router.beforeEach((to, from, next) => {
//   //访客直接访问
//   if (to.path === '/login' || to.path==='/resource' || to.path==='/resources')
//     return next()

//   // 获取token
//   var whoami = -1
//   get('/user/verifyToken',
//     (mes) => {
//       if (mes == 0) {
//         whoami = 0
//       } else {
//         whoami = 1
//       }
//     }
//   )

//   if(to.path === '/post' || to.path === '/posts' || t)
  
// })

export default router
