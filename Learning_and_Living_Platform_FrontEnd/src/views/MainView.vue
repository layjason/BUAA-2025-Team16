<template>
<div class="background-a" style="display: flex; justify-content: space-between; flex-direction: column;">
  <div>
    <v-layout >
      <v-app-bar
        density="comfortable"
        scroll-behavior="fade-image elevate hide"
        :elevation="24"
        scroll-threshold="150"
        color="#C8C6C6"
        style="color: #080202"
        image="https://learning-and-living.oss-cn-beijing.aliyuncs.com/background/grey.svg"
      >

      <div v-if="guest">
        <p style="margin-left: 40px;font-size:15px;font-weight: bold;color: #080202;">
          欢迎访问易言学习生活平台！想要使用更多功能请👉
          <v-btn color="#080202" @click="()=>{router.push('/login')}">
            登录
          </v-btn>
        </p>
        
      </div>
      <div v-else-if="auth">
        <v-menu>
          <template v-slot:activator="{ props }">
            <v-btn color="#080202" icon="mdi-dots-vertical" v-bind="props"></v-btn>
          </template>
          <v-list>
            <v-list-item 
              v-for="(item, i) in adminMenuItems"
              :key="i"
              :value="i"
              @click="navigateTo(item.url)"
            >
              <v-list-item-title>{{ item.name }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

          <v-btn
          color="#080202"
          icon>
            <v-icon title="导航" icon="mdi-map-legend" @click="navigateTo('/')"/>
          </v-btn>

          <v-btn
          color="#080202"
          icon>
            <v-icon title="教育资源" icon="mdi-package-variant" @click="navigateTo('/resources')"/>
          </v-btn>

          <v-btn
          color="#080202"
          icon>
            <v-icon title="论坛" icon="mdi-post" @click="navigateTo('/posts')"/>
          </v-btn>

          <v-btn
          color="#080202"
          icon>
            <v-icon title="管理用户" icon="mdi-account-group-outline" @click="navigateTo('/manage')"/>
          </v-btn>

          <v-btn
          color="#080202"
          icon>
            <v-icon title="统计数据" icon="mdi-chart-line" @click="navigateTo('/statistics')"/>
          </v-btn>

          <v-btn
          color="#080202"
          icon>
            <v-icon title="帮助" icon="mdi-help-circle-outline" @click="navigateTo('/help')"/>
          </v-btn>

          <v-btn color="#080202" @click="logout()">
            登出
          </v-btn>
      </div>
      <div v-else>
        <v-menu>
          <template v-slot:activator="{ props }">
            <v-btn color="080202" icon="mdi-dots-vertical" v-bind="props"></v-btn>
          </template>
          <v-list>
            <v-list-item 
              v-for="(item, i) in userMenuItems"
              :key="i"
              :value="i"
              @click="navigateTo(item.url)"
            >
              <v-list-item-title>{{ item.name }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

          <v-avatar
          variant="outlined"
          style="margin-right: 10px;" @click="navigateToPersonality(userId)"
          :image=Avatar
          ></v-avatar>

          <v-btn
          color="#080202"
          icon>
            <v-icon title="导航" icon="mdi-map-legend" @click="navigateTo('/')"/>
          </v-btn>

          <v-btn
          color="#080202"
          icon>
            <v-icon title="教育资源" icon="mdi-package-variant" @click="navigateTo('/resources')"/>
          </v-btn>

          <v-btn
          color="#080202"
          icon>
            <v-icon title="论坛" icon="mdi-post" @click="navigateTo('/posts')"/>
          </v-btn>

          <v-btn
          color="#080202"
          icon>
            <v-icon title="个人信息" icon="mdi-account-edit" @click="navigateToPersonality(userId)"/>
          </v-btn>
  
          <v-btn
          color="#080202"
          icon>
            <v-icon title="帮助" icon="mdi-help-circle-outline" @click="navigateTo('/help')"/>
          </v-btn>

          <v-btn 
          color="#080202"
          @click="logout()">
            登出
          </v-btn>
        </div>
      </v-app-bar>
    </v-layout>

    <router-view v-slot="{ Component }" style="margin-top: 55px">
        <component :is="Component" style="height: 100%"/>
        <!-- <transition name="fade" mode="out-in">
            <component :is="Component" style="height: 100%"/>
        </transition> -->
    </router-view>
  </div>
    
  <div>
    <v-layout>
      <div style="width: 100%;height: 50px;display: flex;align-items: center;justify-content: center;font-size: 16px;">
        <p style="color: aliceblue;">Copyright © 2023 LAL team All Rights Reserved</p>
      </div>
    </v-layout>
  </div>
</div>
</template>

<style>
.fade-enter-active, .fade-leave-active {
  transition: opacity .5s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}

.background-post {
  background-image: url("https://learning-and-living.oss-cn-beijing.aliyuncs.com/background/bgrey.png");
  background-size: cover; /* 自动缩放图片大小以完整覆盖整个背景区域，可能会裁切 */
  background-position: center; /* 图片居中显示 */
  background-repeat: no-repeat;  /*不重复平铺图片 */
  background-attachment: fixed;
  min-height: 100vh;
}

.background-res{
  background-image: url("https://learning-and-living.oss-cn-beijing.aliyuncs.com/background/sea.png");
  background-size: cover; /* 自动缩放图片大小以完整覆盖整个背景区域，可能会裁切 */
  background-position: center; /* 图片居中显示 */
  background-repeat: no-repeat;  /*不重复平铺图片 */
  background-attachment: fixed;
  min-height: 100vh;
}

.background-a{
  background-image: url("https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/55957754.jpg");
  background-size: cover; /* 自动缩放图片大小以完整覆盖整个背景区域，可能会裁切 */
  background-position: center; /* 图片居中显示 */
  background-repeat: no-repeat;  /*不重复平铺图片 */
  background-attachment: fixed;
  min-height: 100vh;
}

</style>

<script setup>
import router from "@/router"
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { createVuetify } from 'vuetify'
import { aliases, fa } from 'vuetify/iconsets/fa'
import { mdi } from 'vuetify/iconsets/mdi'
import { get } from "@/net"
import { Snackbar } from "@varlet/ui"

createVuetify({
  icons: {
    defaultSet: 'fa',
    aliases,
    sets: {
      fa,
      mdi,
    },
  },
})

const Avatar = ref("")

const guest = ref(true)
const auth = ref(false)

const adminMenuItems = reactive([
  { name: '导航', url: '/' },
  { name: '教育资源' , url: '/resources' },
  { name: '论坛' , url: '/posts' },
  { name: '用户管理' , url: '/manage' },
  { name: '统计数据', url: '/statistics' },
  { name: '帮助', url: '/help' }
])

const userMenuItems = reactive([
  { name: '导航', url: '/' },
  { name: '教育资源', url: '/resources' },
  { name: '论坛', url: '/posts' },
  { name: '个人信息', url: '/users' },
  { name: '帮助', url: '/help' }
])

function sendHeartbeat() {
  if (localStorage.getItem('token') != '' && localStorage.getItem('token') != null) {
    get('/user/heartbeat',
      (mes) => {
        // Snackbar.warning('HEARTBEAT!')
        console.log('Heartbeat!')
        localStorage.setItem('token', mes)
      },
      () => {
        localStorage.removeItem('token')
        guest.value = true
      }
    )
  } else {
    guest.value = true
  }
}

const heartbeatInterval = ref(null)

onMounted(() => {
  getAvatar()
  heartbeatInterval.value = setInterval(sendHeartbeat, 60000)
})

onUnmounted(() => {
  clearInterval(heartbeatInterval);
})

// const changePage = (name) => {
//   // console.log(name)
//   if (name === "resources") {
//     console.log('to resources')
//     router.push('/resources')
//   } else if (name === "posts") {
//     console.log('to posts')
//     router.push('/posts')
//   } else if (name === "users") {
//     console.log('to users')
//     router.push('/users')
//   } else if (name === 'statistics') {
//     console.log('to statistics')
//     router.push('/statistics')
//   } else if (isTest.value && name === "test1") {
//     console.log('set isAdmin to true')
//     isAdmin.value = true
//   } else if (isTest.value && name === "test2") {
//     console.log('set isAdmin to false')
//     isAdmin.value = false
//   } else {
//     console.log('unknown name: ' + name)
//   }
// }

// const regex1 = /^http:\/\/localhost:5173\/resources\/?.*$/;
// const regex2 = /^http:\/\/localhost:5173\/resource\/\d+$/;
// const regex3 = /^http:\/\/localhost:5173\/posts\/?.*$/;
// const regex4 = /^http:\/\/localhost:5173\/post\/\d+$/;
// const regex5 = /^http:\/\/localhost:5173\/users\/?.*$/;
// const regex6 = /^http:\/\/localhost:5173\/manage$/;
// const regex7 = /^http:\/\/localhost:5173\/statistics$/;

// onMounted(() => {
//   currentURL.value = window.location.href;
//   console.log(currentURL.value);
//   if (regex1.test(currentURL.value)) {
//     title.value = '资源列表';
//   } else if (regex2.test(currentURL.value)) {
//     title.value = '资源详情';
//   } else if (regex3.test(currentURL.value)) {
//     title.value = '帖子列表';
//   } else if (regex4.test(currentURL.value)) {
//     title.value = '帖子详情';
//   } else if (regex5.test(currentURL.value)) {
//     title.value = '个人主页';
//   } else if (regex6.test(currentURL.value)) {
//     title.value = '用户列表';
//   } else if (regex7.test(currentURL.value)) {
//     title.value = '统计数据';
//   }
//   console.log(title.value);
// });

function logout() {
  get('/user/logout',
    () => {
      localStorage.removeItem('token')
      localStorage.removeItem('avatar')
      router.push('/login')
    }
  )
}

const navigateTo = (url) => {
  router.push(url)
}

function getAvatar() {
  if (localStorage.getItem('token') != null) {
    guest.value = false
    Avatar.value = localStorage.getItem('avatar')
    if (localStorage.getItem('auth') == 'admin') {
      auth.value = true
    }
  } else {
    guest.value = true
  }
}

const navigateToPersonality = () => {
  router.push('/users')
}

</script>

<!-- <script >
import router from "@/router"
import { ref } from 'vue'

export default {
    setup() {
        const isTest = ref(true)
        const isAdmin = ref(null)

        const changePage = (name) => {
            // console.log(name)
            if(name == "resources") {
                console.log('to resources')
                router.push('/resources')
            }else if(name == "posts") {
                console.log('to posts')
                router.push('/posts')
            }else if(name == "users") {
                console.log('to users')
                router.push('/users')
            }else if(name == 'statistics') {
                console.log('to statistics')
                router.push('/statistics')
            }
            
            
            
            else if(isTest.value && name == "test1") {
                console.log('set isAdmin to true')
                isAdmin.value = true
            }else if(isTest.value && name == "test2") {
                console.log('set isAdmin to false')
                isAdmin.value = false
            }
            
            
            
            else {
                console.log('unknow name : ' + name)
            }
        }

        return {
            isTest,
            isAdmin,
            changePage,
        }
    }
}



</script> -->