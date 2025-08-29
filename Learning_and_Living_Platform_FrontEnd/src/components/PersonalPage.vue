<template>
  <div class="users-user-container">
    <v-card class="users-info-box">
      <v-card-item title="用户信息">
        <template v-slot:subtitle>
          <v-icon icon="mdi-account-box-multiple-outline" size="18" class="me-1 pb-1"></v-icon>
          userInfo
        </template>
        <v-btn variant="tonal" style="height: 32px;" color="#2F58CD" @click="router.push('/user/' + formData.id)">
          <p style="color: #2F58CD;">切换访客视图</p>
        </v-btn>
      </v-card-item>

      <v-card-text class="py-0">
        <v-row align="center" no-gutters>
          <v-col cols="4" class="text-center">
            <v-avatar size="180">
              <v-img alt="Avatar" :src="formData.profilePhotoUrl"></v-img>
            </v-avatar>

            <v-list-item-subtitle density="compact" style="margin-top: 10px">
              <Upload :action="uploadUrl" :headers=uploadHeader accept="image/*"
                :format="['jpg', 'jpeg', 'png', 'webp']" :on-success="updateAvatar">
                <Button :disabled=!readonly icon="ios-cloud-upload-outline">更改头像</Button>
              </Upload>
            </v-list-item-subtitle>

          </v-col>

          <v-col class="text-left " cols="4">
            <Form label-position="top" :model="formData" :rules="ruleValidate" ref="editInfoRef">
              <FormItem>
                <Tag>{{ formData.id }}</Tag>
                <template #label>
                  <p class="users-flabel">Uid</p>
                </template>
              </FormItem>
              <FormItem v-if=readonly>
                <p class="users-fitem" style="font-size: 30px">{{ formData.name }}</p>
                <template #label>
                  <p class="users-flabel">昵称</p>
                </template>
              </FormItem>
              <FormItem v-else prop="name">
                <Input style="width: 200px; margin-top: 5px;" size="large" v-model="formData.name"
                  placeholder="请输入昵称"></Input>
                <template #label>
                  <p class="users-flabel">昵称</p>
                </template>
              </FormItem>
              <FormItem v-if=readonly>
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

          <v-col class="text-left" cols="4">
            <Form label-position="top" :model="formData" :rules="ruleValidate" ref="editInfoRef">
              <FormItem v-if=readonly>
                <p class="users-fitem">{{ formData.email }}</p>
                <template #label>
                  <p class="users-flabel">邮箱</p>
                </template>
              </FormItem>
              <FormItem v-else prop="email">
                <Input style="width: 200px; margin-top: 5px;" size="large" v-model="formData.email"
                  placeholder="请输入邮箱"></Input>
                <template #label>
                  <p class="users-flabel">邮箱</p>
                </template>
              </FormItem>
              <FormItem v-if=readonly>
                <p class="users-fitem">{{ formData.gender }}</p>
                <template #label>
                  <p class="users-flabel">性别</p>
                </template>
              </FormItem>
              <FormItem v-else prop="gender">
                <RadioGroup style="margin-top: 6px;" size="large" v-model="formData.gender">
                  <Radio label="Male">男</Radio>
                  <Radio label="Female">女</Radio>
                  <Radio label="Unknown">未设置</Radio>
                </RadioGroup>
                <template #label>
                  <p class="users-flabel">性别</p>
                </template>
              </FormItem>
              <FormItem v-if=readonly>
                <p class="users-fitem">{{ formData.birthday }}</p>
                <template #label>
                  <p class="users-flabel">生日</p>
                </template>
              </FormItem>
              <FormItem v-else prop="birthday">
                <DatePicker size="large" type="date" v-model="formData.birthday" placeholder="选择一个日期"
                  style="width: 200px;margin-top: 5px;" />
                <template #label>
                  <p class="users-flabel">生日</p>
                </template>
              </FormItem>
            </Form>
          </v-col>
        </v-row>
      </v-card-text>



      <div class="d-flex py-3 justify-space-between">
        <v-row>
          <v-col cols="3"></v-col>
          <v-col cols="8" class="text-center">
            <v-list-item density="compact">
              <v-list-item-subtitle>
                <v-row>
                  <v-col cols="3">
                    <v-btn variant="tonal" style="height: 32px;" color="#FFD700" v-if=loading>
                    </v-btn>
                    <v-btn variant="tonal" style="height: 32px;" color="#2F58CD" v-else-if=readonly
                      @click="tryUpdateInfo()">
                      <p style="color: #2F58CD;">编辑个人信息</p>
                    </v-btn>
                    <v-btn variant="tonal" style="height: 32px;" color="#03C4A1" v-else @click="updateInfo()">
                      <p style="color: #03C4A1;">提交个人信息</p>
                    </v-btn>
                  </v-col>
                  <v-col cols="3">
                    <v-btn variant="tonal" style="height: 32px;" color="#2F58CD" v-if=loading>
                    </v-btn>
                    <v-btn variant="tonal" style="height: 32px;" color="#E2703A" v-else-if=readonly
                      @click="isPassword = !isPassword">
                      <p style="color: #E2703A;">修改密码</p>
                    </v-btn>
                    <v-btn variant="tonal" style="height: 32px;" color="#9BABB8" v-else @click="cancelUpdateInfo()">
                      <p style="color: #9BABB8;">取消</p>
                    </v-btn>
                  </v-col>
                  <v-col cols="3">
                    <v-btn variant="tonal" style="height: 32px;" color="#2F58CD" v-if=loading>
                    </v-btn>
                    <v-btn variant="tonal" style="height: 32px;" color="#ED2B2A" v-else-if=readonly
                      @click="isDelete = !isDelete">
                      <p style="color: #ED2B2A;">删除账号</p>
                    </v-btn>
                  </v-col>
                </v-row>
              </v-list-item-subtitle>
            </v-list-item>
          </v-col>
          <v-col cols="1"></v-col>
        </v-row>
      </div>
      <v-divider></v-divider>


      <!-- 下载历史 -->
      <v-card-item title="个人下载历史">
        <template v-slot:subtitle>
          <v-icon icon="mdi-history" size="18" class="me-1 pb-1"></v-icon>
          download history
        </template>
      </v-card-item>
      <v-alert :type="hisAlert.type" variant="tonal" :title="hisAlert.title" :text="hisAlert.message"
        v-if="hisAlert.show"
        style="border-radius: 10px; margin-bottom: 10px; margin-top: 10px; white-space: pre-wrap;" />
      <div>
        <v-timeline side="end" align="center" line-thickness="4" truncate-line="both" style="width: 100%;">
          <v-timeline-item v-for="(entry, index) in hisData" :key="index" size="small">
            <template v-slot:opposite>
              <v-btn variant="tonal" @click="moveToDetail(entry.resourceId)" title="打开相应资源详情页">查看详情</v-btn>
            </template>
            <div style="display: flex; flex-direction: row; align-items: center;">
              <div class="small-title-dark">{{ entry.downloadTime }}</div>
              <div style="margin-left: 30px; width: 360px">
                <div class="small-title-dark" title="下载时资源标题">{{ entry.title || '未命名资源' }}</div>
                <div class="text-dark" title="资源文件名">{{ entry.fileName || '资源文件未命名' }}</div>
              </div>
            </div>
          </v-timeline-item>
        </v-timeline>
      </div>
      <v-pagination v-model="hisPageNow" :length="hisPageCnt" @update:modelValue="changePage"></v-pagination>

    </v-card>


    <!-- 修改密码弹窗 -->
    <Modal v-model="isPassword" title="修改密码" ok-text="确认" cancel-text="取消">
      <Form ref="editPassRef" :model="pass" :rules="passRules">
        <FormItem label="旧密码" prop="oldPass">
          <Input type="password" v-model="pass.oldPass" placeholder="请输入旧密码"></Input>
        </FormItem>
        <FormItem label="新密码" prop="newPass">
          <Input type="password" v-model="pass.newPass" placeholder="请输入新密码"></Input>
        </FormItem>
        <FormItem label="确认密码" prop="confirmPass">
          <Input type="password" v-model="pass.confirmPass" placeholder="请再次输入新密码"></Input>
        </FormItem>
      </Form>
      <template #footer>
        <Button type="primary" @click="confirmEditPass">
          <p style="color: white;">提交</p>
        </Button>
        <Button @click="cancelEditPass" style="margin-left: 10px;">取消</Button>
      </template>
    </Modal>

    <!-- 删除密码 -->
    <Modal v-model="isDelete" width="360">
      <template #header>
        <p style="color:#f60;text-align:center">
          <span>警告</span>
        </p>
      </template>
      <div style="text-align:center">
        <p>删除后，账号将永远无法恢复！</p>
      </div>
      <template #footer>
        <Button type="error" size="large" long @click="selfDelete">
          <p style="color: white;">删除</p>
        </Button>
      </template>
    </Modal>
  </div>

  <!-- <div>
    <div class="page-container-2">
        <div class="left-column-2">
            <div class="side-panel-2 side-panel-custom-2 left-panel-2 side-expand shadow-act glass">
                <div>
                    <p style="font-size: 30px;font-weight: bold;">概要</p>
                </div>

                <Divider></Divider>
                <div>
                  <Row :gutter="16">
                    <Col span="2" offset="3">
                      <Icon size=20 type="md-person" />
                    </Col>
                    <Col offset="2">
                      <p style="font-size: 20;font-weight: bold;margin-top: 2px;">Uid</p>
                    </Col>
                    <Col >
                      <p style="margin-top: 2px;">{{ formData.id }}</p>
                    </Col>
                  </Row>
                </div>
            </div>
        </div>
        <div class="right-column-2">
            <div style="min-width: 800px;"
            class="side-panel-2 side-panel-custom-2 right-panel-2 side-expand shadow-act glass">
                <div style="margin-bottom: 30px;">
                    <p style="font-size: 30px;font-weight: bold;">详细信息</p>
                </div>
                <div style="margin-top: 40px;margin-left: 80px;margin-right: 80px;">
                  <div>
                    <Row>
                      <Col span="6">
                        <div style="text-align: center;">
                          <Avatar
                          :src="formData.profilePhotoUrl"
                          style="width: 100%;height: 100%;"
                          ></Avatar>
                          <Divider>头像</Divider>
                          <Upload 
                          :action="uploadUrl"
                          accept="image/*"
                          :format="['jpg', 'jpeg', 'png', 'webp']" 
                          :on-success="updateAvatar"
                          style="margin-top: 20px;">
                              <Button :disabled=!readonly icon="ios-cloud-upload-outline">更改头像</Button>
                          </Upload>
                        </div>
                      </Col>
                      <Col span="9" offset="2">             
                        <Form 
                        label-position="top"
                        :model="formData"
                        :rules="ruleValidate"
                        ref="editInfoRef"
                        >
                          <FormItem v-if=readonly>
                              <p class="users-fitem">{{ formData.name }}</p>
                              <template #label>
                                <p class="users-flabel">昵称</p>
                              </template>
                          </FormItem>
                          <FormItem v-else prop="name">
                              <Input 
                              style="width: 200px; margin-top: 5px;"
                              size="large" v-model="formData.name" placeholder="请输入昵称"></Input>
                              <template #label>
                                <p class="users-flabel">昵称</p>
                              </template>
                          </FormItem>
                          <FormItem v-if=readonly >
                              <p class="users-fitem">{{ formData.email }}</p>
                              <template #label>
                                <p class="users-flabel">邮箱</p>
                              </template>
                          </FormItem>
                          <FormItem v-else prop="email">
                              <Input 
                              style="width: 200px; margin-top: 5px;"
                              size="large" v-model="formData.email" placeholder="请输入邮箱"></Input>
                              <template #label>
                                <p class="users-flabel">邮箱</p>
                              </template>
                          </FormItem>
                          <FormItem v-if=readonly>
                              <p class="users-fitem">{{ formData.gender }}</p>
                              <template #label>
                                <p class="users-flabel">性别</p>
                              </template>
                          </FormItem>
                          <FormItem v-else prop="gender">
                              <RadioGroup style="margin-top: 6px;" size="large" v-model="formData.gender">
                                  <Radio label="Male">男</Radio>
                                  <Radio label="Female">女</Radio>
                                  <Radio label="Helicopter">武装直升机</Radio>
                              </RadioGroup>
                              <template #label>
                                <p class="users-flabel">性别</p>
                              </template>
                          </FormItem>
                          <FormItem v-if=readonly>
                            <p class="users-fitem">{{ formData.birthday }}</p>
                            <template #label>
                              <p class="users-flabel">生日</p>
                            </template>
                          </FormItem>
                          <FormItem v-else prop="birthday">
                            <DatePicker size="large" type="date" 
                            v-model="formData.birthday" 
                            placeholder="选择一个日期" 
                            style="width: 200px;margin-top: 5px;"/>
                            <template #label>
                                <p class="users-flabel">生日</p>
                            </template>
                          </FormItem>
                        </Form>
                      </Col>
                      <Col span="3" offset="2">
                        <div style="margin-top: 30%;">
                          <var-button v-if=loading loading block type="info">
                          </var-button>
                          <var-button v-else-if=readonly block type="primary" @click="readonly = !readonly">
                              <p style="color: aliceblue;">编辑个人信息</p>
                          </var-button>
                          <var-button v-else block type="success" @click="updateInfo()">
                              <p style="color: aliceblue;">提交个人信息</p>
                          </var-button>
                        </div>
                        <div style="margin-top: 10%;">
                          <var-button v-if=loading loading block type="info">
                          </var-button>
                          <var-button v-else-if=readonly block type="warning" @click="isPassword = !isPassword">
                              <p style="color: aliceblue;">修改密码</p>
                          </var-button>
                        </div>
                        <div style="margin-top: 10%;"> 
                          <var-button v-if=loading loading block type="info">
                          </var-button>
                          <var-button v-else-if=readonly block type="danger" @click="isDelete = !isDelete">
                              <p style="color: aliceblue;">删除账号</p>
                          </var-button>
                        </div>
                      </Col>
                    </Row>
                  </div>
                </div>
                <Divider></Divider>
            </div>
        </div>
    </div>
    </div> -->
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
import { get, put, delet, post } from "@/net";
import { Snackbar } from '@varlet/ui'
import { ruleValidate } from "@/rule"
import router from "@/router"
import { sha256 } from 'js-sha256';

const uploadUrl = "api/resource/uploadImage"
const uploadHeader = ref({
  token: localStorage.getItem('token')
})

const formData = reactive({
  id: '10000000',
  name: '默认用户',
  email: '默认邮箱',
  birthday: '2002-03-03',
  gender: 'unknown',
  level: '0',
  levelName: '',
  profilePhotoUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/avatar/106115311_p0_master1200.jpg'
})

const tempFormData = reactive({
  name: '默认用户',
  email: '默认邮箱',
  birthday: '2002-03-03',
  gender: 'unknown'
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


// TODO: 请求并接受下载历史
function getInfo() {
  loading.value = true;
  get("/user/userInfo",
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
    },)

  updateHistory(1)
}

function updateAvatar(res) {
  this.clearFiles()
  console.log(res.message)
  formData.profilePhotoUrl = res.message
  put("user/updateUserInfo",
    formData,
    () => {
      Snackbar.success("更新成功")
      localStorage.setItem('avatar', formData.profilePhotoUrl)
      getInfo()
    })
}

function tryUpdateInfo() {
  tempFormData.name = formData.name
  tempFormData.email = formData.email
  tempFormData.gender = formData.gender
  tempFormData.birthday = formData.birthday
  //console.log(tempFormData.value)
  readonly.value = !readonly.value
}

function updateInfo() {
  editInfoRef.value.validate((valid) => {
    if (valid) {
      loading.value = true;
      formData.gender = formData.gender.toLowerCase();
      console.log(formData);
      put("user/updateUserInfo",
        formData,
        () => {
          Snackbar.success("更新成功")
          getInfo()
          readonly.value = !readonly.value
          loading.value = false;
        },
        (mes, status) => {
          if (status == 401) {
            localStorage.removeItem('token')
            router.push('/login')
          }
          Snackbar.error("更新个人信息失败，请刷新后重试")
          readonly.value = !readonly.value
          loading.value = false;
        })
    } else {
      Snackbar.error("请正确填写个人信息")
    }
  })
}

function cancelUpdateInfo() {
  formData.name = tempFormData.name
  formData.email = tempFormData.email
  formData.gender = tempFormData.gender
  formData.birthday = tempFormData.birthday
  readonly.value = !readonly.value
}

const pass = reactive({
  oldPass: '',
  newPass: '',
  confirmPass: ''
})

const loading = ref(true)
const readonly = ref(true)
const isPassword = ref(false)
const isDelete = ref(false)

const editInfoRef = ref(null)
const editPassRef = ref(null)

const validateOldPass = (rule, value, callback) => {
  if (value == '') {
    callback(new Error('请输入您的旧密码'));
  } else {
    callback();
  }
};

const validatePass = (rule, value, callback) => {
  if (value == '') {
    callback(new Error('请输入新密码'));
  } else {
    if (value.length < 6 || value.length > 16) {
      callback(new Error('密码的长度必须为6-16位'));
    }
    callback();
  }
};
const validatePassCheck = (rule, value, callback) => {
  if (value == '') {
    callback(new Error('请重复输入您的新密码'));
  } else if (value !== pass.newPass) {
    callback(new Error('两次输入的密码不一致！'));
  } else {
    callback();
  }
};

const passRules = {
  oldPass: [
    { validator: validateOldPass, trigger: 'blur' }
  ],
  newPass: [
    { validator: validatePass, trigger: 'blur' }
  ],
  confirmPass: [
    { validator: validatePassCheck, trigger: 'blur' }
  ],

}

function confirmEditPass() {
  editPassRef.value.validate((valid) => {
    if (valid) {
      post('user/getSalt', {
        idOrEmail: formData.id
      },
        (message) => {
          put("/user/updatePwd",
            {
              userId: formData.id,
              oldPassword: pass.oldPass,
              newPassword: pass.newPass
            },
            () => {
              Snackbar.success("更新成功")
              isPassword.value = false
            })
        },
      )
    } else {
      Snackbar.error("请正确填写密码!")
    }
  })
}

function cancelEditPass() {
  isPassword.value = false
}

function selfDelete() {
  delet("/user/deleteUser", {
    id: formData.id
  },
    () => {
      Snackbar.success("删除成功")
      localStorage.setItem('token', '')
      isDelete.value = !isDelete.value
      router.push('/login')
    },
    () => {
      Snackbar.error("删除失败，请稍后再试！")
      isDelete.value = !isDelete.value
    }
  )
}


// TODO: 添加个人下载历史相关内容
const hisData = ref([])
const hisAll = ref([])
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
  await get(
    '/resource/listDownloadHistoryByUserId',
    (message) => {
      // console.log(message)

      // 没有获取到相关记录
      if (message.length == 0) {
        showAlert(true, 'info', '下载历史记录为空', '没有查询到对应的下载历史记录，可能您还未下载过教育资源，也可能您的下载记录已被清除，欢迎前往教育资源区下载资源。')
        return
      }

      // hisPageCnt.value = (message.length - 1) / entryInPage + 1
      hisPageCnt.value = Math.ceil(message.length / entryInPage)
      // console.log('message get:')
      // console.log(entryInPage)
      // console.log(hisPageCnt.value)

      hisAll.value = message

      changePage(hisPage)
    }
  )
}
const changePage = (pageNow) => {
  // console.log('page change:')
  // console.log(hisPageNow.value)
  // console.log(pageNow)
  var startInx = entryInPage * (pageNow - 1)
  var endInx = entryInPage * (pageNow)

  hisData.value = hisAll.value.slice(startInx, endInx)
  // console.log('hisData:')
  // console.log(hisData.value[0].userId)
  // console.log(hisData.value[1])

  // 当前页数据为空
  if (hisData.value.length == 0) {
    showAlert(true, 'warning', '当前页面历史记录为空', '你不应该看到这条警告，请联系系统管理员，非常感谢')
  } else {
    showAlert(true, 'info', '', '最多展示50条下载历史')
  }
}
const moveToDetail = (resId) => {
  if (!(resId > 0)) return;
  router.push(`/resource/${resId}`);
}


// const radar = ({
//   tooltip: {
//     trigger: 'item'
//   },
//   legend: {
//     type: 'scroll',
//     bottom: 10,
//     data: (function () {
//       var list = [];
//       for (var i = 1; i <= 10; i++) {
//         list.push(i + 2000 + '');
//       }
//       return list;
//     })()
//   },
//   radar: {
//     center: ['50%', '50%'],
//     indicator: [
//       { text: 'IE8-', max: 400 },
//       { text: 'IE9+', max: 400 },
//       { text: 'Safari', max: 400 },
//       { text: 'Firefox', max: 400 },
//       { text: 'Chrome', max: 400 }
//     ]
//   },
//   series: (function () {
//     var series = [];
//     for (var i = 1; i <= 10; i++) {
//       series.push({
//         type: 'radar',
//         symbol: 'none',
//         lineStyle: {
//           width: 1
//         },
//         emphasis: {
//           areaStyle: {
//             color: 'rgba(247, 241, 229, 0.5)'
//           }
//         },
//         data: [
//           {
//             value: [
//               (40 - i) * 10,
//               (38 - i) * 4 + 60,
//               i * 5 + 10,
//               i * 9,
//               (i * i) / 2
//             ],
//             name: i + 2000 + ''
//           }
//         ]
//       });
//     }
//     return series;
//   })()
// });

getInfo();

</script>