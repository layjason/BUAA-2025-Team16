<template>


        <!-- 发布资源历史 -->
        <v-card-item title="发布资源历史">
          <template v-slot:subtitle>
            <v-icon
              icon="mdi-history"
              size="18"
              class="me-1 pb-1"
            ></v-icon>
              Resource history
          </template>
        </v-card-item>
        <v-alert :type="hisAlert.type" variant="tonal" :title="hisAlert.title" :text="hisAlert.message" v-if="hisAlert.show" style="border-radius: 10px; margin-bottom: 10px; margin-top: 10px; white-space: pre-wrap;"/>
        <div>
          <v-timeline side="end" align="center" line-thickness="4" truncate-line="both" style="width: 100%;">
            <v-timeline-item v-for="(entry, index) in hisData" :key="index" size="small">
              <template v-slot:opposite>
                <v-btn variant="tonal" @click="moveToDetail(entry.id)" title="打开相应资源详情页">查看详情</v-btn>
              </template>
              <div style="display: flex; flex-direction: row; align-items: center;">
                <div class="small-title-dark">资源id：{{ entry.id }}</div>
                <div style="margin-left: 30px; width: 360px">
                  <div class="small-title-dark" title="资源标题">{{ entry.title || '未命名资源' }}</div>
                </div>
              </div>
            </v-timeline-item>
          </v-timeline>
        </div>
        <v-pagination v-model="hisPageNow" :length="hisPageCnt" @update:modelValue="updateHistory(hisPageNow)"></v-pagination>
      

</template>

<script setup>


 // TODO: 请求并接受资源上传和发帖历史
 function getInfo() {
    loading.value = true;
        post("/user/otherUserInfo",
              {userId : userId},
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
            },
      () => {
        Snackbar.error("获取个人信息失败")
      })
      console.log('hello')
      updateHistory(1)
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
            userId: userId,
            pageNum: hisPage,
            cntInPage: entryInPage
            },
            (message) => {
                console.log(message)
    
            // 没有获取到相关记录
            if(message.count == 0) {
            showAlert(true, 'info', '历史记录为空', '没有查询到对应的历史记录，可能他还未过发布教育资源，也可能他的资源记录已被清除。')
            return
            }
    
            // hisPageCnt.value = (message.length - 1) / entryInPage + 1
            //hisPageCnt.value = Math.ceil(message.length / entryInPage)
            hisPageCnt.value = message.pageCount
            // console.log('message get:')
            // console.log(entryInPage)
            // console.log(hisPageCnt.value)
            showAlert(true, 'info', '', '发布资源历史')
            hisData.value = message.list
        }
    )
  }

</script>