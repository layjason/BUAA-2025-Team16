<template>
  <div class="users-user-container">

    <v-card class="users-info-box">
      <v-card-item title="用户信息">
        <template v-slot:subtitle>
          <v-icon icon="mdi-account-box-multiple-outline" size="18" class="me-1 pb-1"></v-icon>
          userInfo
        </template>
        <v-btn variant="tonal" style="height: 32px;" color="#2F58CD" @click="router.push('/users')">
          <p style="color: #2F58CD;">查看我的主页</p>
        </v-btn>
      </v-card-item>

      <v-card-text class="py-0">
        <v-row align="center" no-gutters>
          <v-col cols="4" class="text-center">
            <v-avatar size="180">
              <v-img alt="Avatar" :src="formData.profilePhotoUrl"></v-img>
            </v-avatar>
          </v-col>

          <!-- 昵称等级-->
          <v-col class="text-left " cols="4">
            <Form label-position="top" :model="formData">
              <FormItem>
                <Tag>{{ props.userId }}</Tag>
                <template #label>
                  <p class="users-flabel">Uid</p>
                </template>
              </FormItem>
              <!-- 昵称 -->
              <FormItem>
                <p class="users-fitem" style="font-size: 30px">{{ formData.name }}</p>
                <template #label>
                  <p class="users-flabel">昵称</p>
                </template>
              </FormItem>
              <!-- 等级 -->
              <FormItem>
                <Tag :color="getLevelTagColor(formData.level)">{{ formData.levelName }}</Tag>
                <template #label>
                  <Poptip trigger="hover" placement="right">
                    <template #title>
                      <p style="text-align: left;font-weight: bold;">
                        等级说明
                      </p>
                    </template>
                    <template #content>
                      <p style="text-align: left;">
                        LV.0 新手小白：需经验值达到0exp；<br />
                        LV.1 入门菜鸟：需经验值达到64exp；<br />
                        LV.2 勤奋学徒：需经验值达到128exp；<br />
                        LV.3 沉淀学士：需经验值达到256exp；<br />
                        LV.4 探索能手：需经验值达到512exp；<br />
                        LV.5 初现锋芒：需经验值达到1024exp；<br />
                        LV.6 高超大师：需经验值达到2048exp；<br />
                        LV.7 学富五车：需经验值达到4096exp；<br />
                        LV.8 知识巨人：需经验值达到8192exp；<br />
                        LV.9 传奇天才：需经验值达到16384exp；<br />
                        LV.10 神一般的存在：需经验值达到32768exp；<br />
                        *注：每天最多只能获取30exp
                      </p>
                    </template>

                    <p class="users-flabel">
                      等级
                    </p>
                  </Poptip>
                </template>
              </FormItem>
            </Form>
          </v-col>

          <!-- 个人信息-->
          <v-col class="text-left" cols="4">
            <Form label-position="top" :model="formData">
              <FormItem>
                <p class="users-fitem">{{ formData.email }}</p>
                <template #label>
                  <p class="users-flabel">邮箱</p>
                </template>
              </FormItem>
              <FormItem>
                <p class="users-fitem">{{ formData.gender }}</p>
                <template #label>
                  <p class="users-flabel">性别</p>
                </template>
              </FormItem>
              <FormItem>
                <p class="users-fitem">{{ formData.birthday }}</p>
                <template #label>
                  <p class="users-flabel">生日</p>
                </template>
              </FormItem>

            </Form>
          </v-col>
        </v-row>
      </v-card-text>

      <v-divider></v-divider>


      <!-- 发布资源历史 -->
      <v-card-item title="发布资源历史">
        <template v-slot:subtitle>
          <v-icon icon="mdi-history" size="18" class="me-1 pb-1"></v-icon>
          Resource history
        </template>
      </v-card-item>
      <v-alert :type="hisAlert.type" variant="tonal" :title="hisAlert.title" :text="hisAlert.message"
        v-if="hisAlert.show"
        style="border-radius: 10px; margin-bottom: 10px; margin-top: 10px; white-space: pre-wrap;" />
      <div>
        <v-timeline side="end" align="center" line-thickness="4" truncate-line="both" style="width: 100%;">
          <v-timeline-item v-for="(entry, index) in hisData" :key="index" size="small">
            <template v-slot:opposite>
              <v-btn variant="tonal" @click="moveToResDetail(entry.id)" title="打开相应资源详情页">查看详情</v-btn>
            </template>
            <div style="display: flex; flex-direction: row; align-items: center;">
              <div style="margin-left: 10px; width: 360px">
                <div class="small-title-dark" title="资源标题">{{ entry.title || '未命名资源' }}</div>
              </div>
            </div>
          </v-timeline-item>
        </v-timeline>
      </div>
      <v-pagination size="small" v-model="hisPageNow" :length="hisPageCnt"
        @update:modelValue="updateHistory(hisPageNow)"></v-pagination>

      <v-divider></v-divider>



      <!-- 发布帖子历史 -->
      <v-card-item title="发布帖子历史">
        <template v-slot:subtitle>
          <v-icon icon="mdi-history" size="18" class="me-1 pb-1"></v-icon>
          Post history
        </template>
      </v-card-item>
      <v-alert :type="postAlert.type" variant="tonal" :title="postAlert.title" :text="postAlert.message"
        v-if="postAlert.show"
        style="border-radius: 10px; margin-bottom: 10px; margin-top: 10px; white-space: pre-wrap;" />
      <div>
        <v-timeline side="end" align="center" line-thickness="4" truncate-line="both" style="width: 100%;">
          <v-timeline-item v-for="(entry, index) in postData" :key="index" size="small">
            <template v-slot:opposite>
              <v-btn variant="tonal" @click="moveToPostDetail(entry.id)" title="打开相应帖子详情页">查看详情</v-btn>
            </template>
            <div style="display: flex; flex-direction: row; align-items: center;">
              <div style="margin-left: 10px; width: 360px">
                <div class="small-title-dark" title="帖子标题">{{ entry.title || '未知帖子' }}</div>
              </div>
            </div>
          </v-timeline-item>
        </v-timeline>
      </div>
      <v-pagination size="small" v-model="postPageNow" :length="postPageCnt"
        @update:modelValue="updatePostHistory(postPageNow)"></v-pagination>
    </v-card>

  </div>
</template>

<style>
.users-user-container {
  padding-top: 40px;
  padding: 30px;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 设置框的最小宽度为600px */
.users-info-box {
  min-width: 900px;
  min-height: 600px;
  padding: 20px;
  background-color: #ffffff;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.users-cardRadar {
  width: 350px;
  height: auto;
  margin: 20px 20px;
}

.users-setRadar {
  min-width: 100px;
  min-height: 350px;
  background-color: white;
  padding: 0;
  margin: 0;
  border: 0;
}

.users-fitem {
  margin-top: 8px;
  font-size: 17px;
}

.users-flabel {
  font-size: 20px;
  font-weight: bold;
}
</style>


<script setup>
import { reactive, ref } from 'vue'
import '@/assets/pageLayout2.css'
import '@/assets/panel2.css'
import '@/assets/action.css'
import '@/assets/texts.css'
import { post } from "@/net";
import { Snackbar } from '@varlet/ui'
import router from "@/router"
const props = defineProps({
  userId: Number
})


const formData = reactive({
  id: '10000002',
  name: '默认用户',
  email: '默认邮箱',
  birthday: '2002-03-03',
  gender: 'unknown',
  level: '0',
  levelName: '',
  profilePhotoUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/avatar/106115311_p0_master1200.jpg'
})


function getLevelTagColor(level) {
  switch (level) {
    case 0:
      return 'default'
    case 1:
      return 'green'
    case 2:
      return 'cyan'
    case 3:
      return 'blue'
    case 4:
      return 'geekblue'
    case 5:
      return 'purple'
    case 6:
      return 'gold'
    case 7:
      return 'orange'
    case 8:
      return 'volcano'
    case 9:
      return 'red'
    case 10:
      return 'error'
    default:
      return 'primary'
  }
}

// TODO: 请求并接受资源上传和发帖历史
function getInfo() {
  loading.value = true;
  postLoading.value = true
  post("/user/otherUserInfo",
    { userId: props.userId },
    (message) => {
      formData.id = message.id
      formData.name = message.name
      formData.birthday = message.birthday
      formData.gender = message.gender
      formData.profilePhotoUrl = message.profilePhotoUrl
      formData.email = message.email
      formData.level = message.userLevel
      formData.levelName = message.userLevelName
      loading.value = false
      postLoading.value = false
    },)
  //console.log('hello')
  updateHistory(1)
  updatePostHistory(1)
}


const loading = ref(true)


// TODO: 添加个人下载历史相关内容
const hisData = ref([])
const hisPageNow = ref(1)
const hisPageCnt = ref(1)
const entryInPage = 10

// 历史记录相关警告
const hisAlert = ref({
  show: true,
  type: 'info',
  title: '正在加载',
  message: '正在获取历史记录',
})
const showAlert = (show, type, title, message) => {
  hisAlert.value.show = show
  hisAlert.value.type = type
  hisAlert.value.title = title
  hisAlert.value.message = message
}

const updateHistory = async (hisPage) => {
  showAlert(true, 'info', '正在加载', '正在获取历史记录')
  await post('/resource/listResourceByUserId',
    {
      userId: props.userId,
      pageNum: hisPage,
      cntInPage: entryInPage
    },
    (message) => {
      console.log(message)

      // 没有获取到相关记录
      if (message.count == 0) {
        showAlert(true, 'info', '历史记录为空', '没有查询到对应的历史记录，可能他还未过发布教育资源，也可能他的资源记录已被清除。')
        return
      }

      // hisPageCnt.value = (message.length - 1) / entryInPage + 1
      //hisPageCnt.value = Math.ceil(message.length / entryInPage)
      hisPageCnt.value = message.pageCount
      // console.log('message get:')
      // console.log(entryInPage)
      // console.log(hisPageCnt.value)
      showAlert(true, 'info', '发布资源历史', '')
      hisData.value = message.list
    }
  )
}

const moveToResDetail = (resId) => {
  if (!(resId > 0)) return
  router.push(`/resource/${resId}`)
}

const moveToPostDetail = (postId) => {
  if (!(postId > 0)) return
  router.push(`/post/${postId}`)
  // window.open('/post/' + postId, '_blank');
}


//帖子记录
const postLoading = ref(true)


// TODO: 添加个人下载历史相关内容
const postData = ref([])
const postPageNow = ref(1)
const postPageCnt = ref(1)

// 历史记录相关警告
const postAlert = ref({
  show: true,
  type: 'info',
  title: '正在加载',
  message: '正在获取历史记录',
})
const showPostAlert = (show, type, title, message) => {
  postAlert.value.show = show
  postAlert.value.type = type
  postAlert.value.title = title
  postAlert.value.message = message
}

const updatePostHistory = async (hisPage) => {
  showPostAlert(true, 'info', '正在加载', '正在获取历史记录')
  await post('/post/getUserPost',
    {
      userId: props.userId,
      pageNum: hisPage,
      cntInPage: entryInPage,
    },
    (message) => {
      console.log('From updatePostHistory')
      console.log(message)

      // 没有获取到相关记录
      if (message.count == 0) {
        showPostAlert(true, 'info', '历史记录为空', '没有查询到对应的历史记录，可能他还未过发布帖子，也可能他的发帖记录已被清除。')
        return
      }

      // postPageCnt.value = (message.length - 1) / entryInPage + 1
      //postPageCnt.value = Math.ceil(message.length / entryInPage)
      postPageCnt.value = message.pageCount
      // console.log('message get:')
      // console.log(entryInPage)
      // console.log(postPageCnt.value)
      showPostAlert(true, 'info', '发布帖子历史', '')
      postData.value = message.list
    }
  )
}

getInfo()
</script>