<template>
<div style="font-family: poppins,sans-serif; background-color: #669eff;">
    <header class="header">
    <nav class="navbar">
        <a @click="router.push('/')">主页</a>
        <v-dialog v-model="showAbout" class="overlay">
            <template v-slot:activator="{ props }">
                <a v-bind="props">关于</a>
            </template>
            <div class="dialog-container" @click="showAbout = false">
                <div class="dialog" style="flex-direction: row; width: 1000px;">
                </div>
            </div>
        </v-dialog>
        <v-dialog v-model="showContact" class="overlay">
            <template v-slot:activator="{ props }">
                <a v-bind="props">联系我们</a>
            </template>
            <div class="dialog-container" @click="showContact = false">
                <div class="dialog" style="flex-direction: row; width: 1000px;">
                    <div class="about-colunm">
                        <v-avatar size="80">
                            <v-img src="https://learning-and-living.oss-cn-beijing.aliyuncs.com/start/AkashiSensei.jpg"/>
                        </v-avatar>
                        <div class="title-dark" style="margin-top: 30px;">AkashiSensei</div>
                        <div class="text-dark" style="margin-top: 2px;">刘益洲</div>
                        <div class="text-dark" style="">20374249@buaa.edu.cn</div>
                    </div>

                    <div class="about-colunm">
                        <v-avatar size="80">
                            <v-img src="https://learning-and-living.oss-cn-beijing.aliyuncs.com/start/99956963.jpg"/>
                        </v-avatar>
                        <div class="title-dark" style="margin-top: 30px;">MAKIMA</div>
                        <div class="text-dark" style="margin-top: 2px;">王䶮堃</div>
                        <div class="text-dark" style="">20374126@buaa.edu.cn</div>
                    </div>

                    <div class="about-colunm">
                        <v-avatar size="80">
                            <v-img src="https://learning-and-living.oss-cn-beijing.aliyuncs.com/start/78774953.jpg"/>
                        </v-avatar>
                        <div class="title-dark" style="margin-top: 30px;">pcpas</div>
                        <div class="text-dark" style="margin-top: 2px;">张正奇</div>
                        <div class="text-dark" style="">20374265@buaa.edu.cn</div>
                    </div>

                    <div class="about-colunm">
                        <v-avatar size="80">
                            <v-img src="https://learning-and-living.oss-cn-beijing.aliyuncs.com/start/111098017.jpg"/>
                        </v-avatar>
                        <div class="title-dark" style="margin-top: 30px;">Cheap-Cheer</div>
                        <div class="text-dark" style="margin-top: 2px;">陈之浩</div>
                        <div class="text-dark" style="">20374151@buaa.edu.cn</div>
                    </div>

                    <div class="about-colunm">
                        <v-avatar size="80">
                            <v-img src="https://learning-and-living.oss-cn-beijing.aliyuncs.com/start/95218018.jpg"/>
                        </v-avatar>
                        <div class="title-dark" style="margin-top: 30px;">Cubeying</div>
                        <div class="text-dark" style="margin-top: 2px;">梁跞方</div>
                        <div class="text-dark" style="">20374090@buaa.edu.cn</div>
                    </div>
                </div>
            </div>
        </v-dialog>
        <v-dialog v-model="showHelp" class="overlay">
            <template v-slot:activator="{ props }">
                <a v-bind="props">帮助</a>
            </template>
            <div class="dialog-container" @click="showHelp = false">
                <div class="dialog" @click.stop="" style="padding: 0px; overflow: hidden;">
                    <div><GuidePanel :guideEntry="helpEntry" /></div>
                </div>
            </div>
        </v-dialog>
    </nav>
    </header>
    <!-- LOGIN FORM CREATION -->
    <div :class="['backgroundLogin', (auth == 'admin') ? 'background-night' : 'background-day']"></div>
    <div :class="['container', (auth == 'admin') ? 'background-night' : 'background-day']">
        <div class="item">
            <h2 class="logo"><i class='bx bxl-xing'></i>Leaning and Living Platform</h2>
            <div class="text-item">
                <h2>欢迎!</h2>
                <h2 style="margin-top: 5px;">“易言”学习生活平台</h2>
                <p>Enjoy your life and study hard.</p>
                <div class="social-icon">
                    <a href="https://github.com/BUAA-SEF-Team15/Learning_and_Living_Platform_FrontEnd"><Icon type="logo-github" /></a>
                </div>
            </div>
        </div>
        <div ref="loginSecRef" class="login-section">
            
            <div class="form-box login">
                <form action="">
                    <h2>登录</h2>
                    <var-input 
                    style="margin-top: 20px;"
                    v-model="loginForm.idOrEmail"
                    text-color="white"
                    placeholder="ID or Email" 
                    focus-color="white"
                    blur-color="white"
                    >
                    <template #append-icon>
                        <var-icon name="account-circle" />
                    </template>
                    </var-input>
                    <var-input 
                    type="password"
                    v-model="loginForm.password"
                    style="margin-top: 30px;"
                    placeholder="Password" 
                    text-color="white"
                    focus-color="white"
                    blur-color="white"
                    >
                    <template #append-icon>
                        <Icon type="md-lock" />
                    </template>
                    </var-input>

                    <div class="remember-password" style="margin-bottom: 10px;">
                        <v-radio-group inline 
                        v-model="auth">
                            <v-radio label="User" value="user" />
                            <v-radio label="Admin" value="admin" />
                        </v-radio-group>
                        <v-btn
                            @click="jump2Forget()"
                            style="height: 20px;margin-top: 10px;"
                            variant="text"
                            >忘记密码
                        </v-btn>
                    </div>
                    <var-button 
                    @click="login"
                    text  
                    color="linear-gradient(to right, #69dbaa, #3a7afe)"
                    style="width: 100%;height: 45px;font-size: 16px;"
                    type="primary">
                    确认登录
                    </var-button>
                    <div class="create-account">
                        <p>没有账户？创建新的用户账户。
                            <v-btn
                            @click="jump2Register()"
                            style="height: 20px;"
                            variant="text"
                            >注册</v-btn>
                        </p>
                    </div>
                </form>
            </div>
            <div v-if="forgetPass" class="form-box register">
                <form action="">
                    <h2>忘记密码</h2>
                    <var-input 
                    style="margin-top: 20px;"
                    placeholder="Email" 
                    text-color="white"
                    focus-color="white"
                    blur-color="white"
                    v-model="forgetForm.email"
                    >
                    <template #append-icon>
                        <Icon type="md-mail" />
                    </template>
                    </var-input>
                    <div class="remember-password">
                        <label for="">你的新密码将会发送到你的邮箱，请注意查收</label>
                    </div>

                    <var-button 
                    v-if="waitForget"
                    text
                    color="linear-gradient(to right, #69dbaa, #3a7afe)"
                    style="width: 100%;height: 45px;font-size: 16px;"
                    >
                    Please wait
                    </var-button>
                    <var-button 
                    v-else
                    text  
                    color="linear-gradient(to right, #69dbaa, #3a7afe)"
                    style="width: 100%;height: 45px;font-size: 16px;"
                    @click = "getPassword()"
                    >
                    Get it Back!
                </var-button>

                    <div class="create-account">
                        <p>返回登陆 👉
                            <v-btn
                            @click="jump2LoginFromForget()"
                            style="height: 20px;"
                            variant="text"
                            >登录</v-btn>
                        </p>
                    </div>
                </form>
            </div>
            <div v-else class="form-box register">
                <form action="">
                    <h2>注册</h2>
                    <var-input 
                    style="margin-top: 20px;"
                    v-model="registerForm.email"
                    placeholder="Email" 
                    text-color="white"
                    focus-color="white"
                    blur-color="white"
                    >
                    <template #append-icon>
                        <Icon type="md-mail" />
                    </template>
                    </var-input>
                    <var-input 
                    type="password"
                    v-model="registerForm.password"
                    style="margin-top: 20px;"
                    placeholder="Password" 
                    text-color="white"
                    focus-color="white"
                    blur-color="white"
                    >
                    <template #append-icon>
                        <Icon type="md-unlock" />
                    </template>
                    </var-input>
                    <var-input 
                    type="password"
                    v-model="registerForm.password_repeat"
                    style="margin-top: 20px;"
                    placeholder="Repeat Password" 
                    text-color="white"
                    focus-color="white"
                    blur-color="white"
                    >
                    <template #append-icon>
                        <Icon type="md-lock" />
                    </template>
                    </var-input>
                    <div class="remember-password">
                        <label for=""><input type="checkbox" v-model="accept">我同意
                            <v-dialog v-model="showStatement" class="overlay">
                                <template v-slot:activator="{ props }">
                                    <span v-bind="props" class="clickable">《易言学习生活平台条款》</span>
                                </template>
                                <div class="dialog-container" @click="showStatement = false">
                                    <div class="dialog" style="padding-bottom: 50px;">
                                        <div class="sta-title">易言使用条款</div>
                                        <div class="sta-section">
                                            <div class="sta-section-title">1. 用户条款</div>
                                            <div class="sta-content">1.1 您在注册时应当提供真实、准确、最新、完整的个人信息。如果因注册信息不真实而引起的问题，由用户自行承担相应的后果。</div>
                                            <div class="sta-content">1.2 用户需妥善保管注册账号信息和密码，不得将账号信息透露给他人，因用户保管不善导致账号丢失的，由用户自行承担。</div>
                                            <div class="sta-content">1.3 用户不得利用本平台进行违法行为，包括但不限于发布违法信息、侵害他人权益等。</div>
                                        </div>
                                        <div class="sta-section">
                                            <div class="sta-section-title">2. 平台使用规则</div>
                                            <div class="sta-content">2.1 用户在使用本平台的所有服务时必须遵守相关法律法规，不得在本平台进行发布、传播或者其他方式使用含有以下内容的信息：</div>
                                            <div class="sta-content">- 反对宪法所确定的基本原则的；</div>
                                            <div class="sta-content">- 危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；</div>
                                            <div class="sta-content">- 损害国家荣誉和利益的；</div>
                                            <div class="sta-content">- 煽动民族仇恨、民族歧视，破坏民族团结的；</div>
                                            <div class="sta-content">- 破坏国家宗教政策，宣扬邪教和封建迷信的；</div>
                                            <div class="sta-content">- 散布谣言，扰乱社会秩序。</div>
                                        </div>
                                    </div>
                                </div>
                            </v-dialog>
                        </label>
                    </div>
                    <var-button 
                    @click="register()"
                    text  
                    color="linear-gradient(to right, #69dbaa, #3a7afe)"
                    style="width: 100%;height: 45px;font-size: 16px;"
                    type="primary">确认注册</var-button>
                    <div class="create-account">
                        <p>已经有易言平台的用户账户了？
                            <v-btn
                            @click="jump2Login()"
                            style="height: 20px;margin-bottom: 3px;"
                            variant="text"
                            >登录</v-btn>
                        </p>
                    </div>
                </form>
            </div>
        
        </div>
        <v-dialog
            v-model="isLogin"
            persistent
            width=48%
            height=30%
            >
            <v-card color="blue" >
                <div style="min-width: 320px;min-width: 200px;">
                    <v-card-text>
                    你已经成功登陆了！
                    <v-progress-linear
                        indeterminate
                        color="white"
                        class="mb-0"
                    ></v-progress-linear>
                    </v-card-text>
                </div>
            </v-card>
        </v-dialog>
    </div>
    <!-- <v-dialog v-model="showDelete" :persistent="isDeleting" class="overlay">
        <div class="dialog-container">
            <div class="dialog">
                <div class="title-dark" >是否确认删除资源？</div>
                <div class="text-large-dark" style="margin-top: 8px;">
                    您正在删除资源：<span style="font-weight: bolder;">{{resData.title}}</span>。是否继续？
                </div>
                <div style="display: flex; flex-direction: row; justify-content: end; margin-top: 15px;">
                    <v-btn variant="tonal" :disabled="isDeleting" style="margin-right: 5px;" title="取消" @click.stop="showDelete=false">取消</v-btn>
                    <v-btn variant="flat" :loading="isDeleting" color="red" style="margin-left: 5px;" @click.stop="tryDelete" title="删除资源">确认删除</v-btn>
                </div>
            </div>
        </div>
    </v-dialog> -->
</div> 
</template>

<style>

@import url('https://fonts.googleapis.com/css2?family=Poppins:ital,wght@0,300;0,400;0,500;1,500&display=swap');
/* body{
    height: 100vh;
    width: 100%;
    background: #000;
} */
.backgroundLogin{
    background-position: center;
    background-size:cover ;
    height: 100vh;
    width: 100%;
    filter: blur(10px);
}
.background-day{
    background: url(https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/39578544_p21.jpg) no-repeat;
}
.background-night{
    background: url(https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/39578544_p20.jpg) no-repeat;
}
.header{
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    padding: 25px 13%;
    background: transparent;
    display: flex;
    justify-content: space-between;
    align-items: center;
    z-index: 100;

}
.navbar a{
    position: relative;
    font-size: 16px;
    color: #fff;
    margin-right: 30px;
    text-decoration: none;
}
.navbar a::after{
    content: "";
    position: absolute;
    left: 0;
    width: 100%;
    height: 2px;
    background: #fff;
    bottom: -5px;
    border-radius: 5px;
    transform: translateY(10px);
    opacity: 0;
    transition: .5s ease;
}
.navbar a:hover:after{
    transform: translateY(0);
    opacity: 1;
}
.search-bar{
    width: 250px;
    height: 45px;
    background-color: transparent;
    border: 2px solid #fff;
    border-radius: 6px;
    display: flex;
    align-items: center;
}
.search-bar input{
    width: 100%;
    background-color: transparent;
    border: none;
    outline: none;
    color: #fff;
    font-size: 16px;
    padding-left: 10px;
}
.search-bar button{
    width: 40px;
    height: 100%;
    background: transparent;
    outline: none;
    border: none;
    color: #fff;
    cursor: pointer;
}
.search-bar input::placeholder{
    color: #fff;
}
.search-bar button i{
    font-size: 22px;
}
.container{
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%,-50%);
    width: 75%;
    min-height: 500px;
    max-height: 550px;
    margin-top: 20px;
    background-position: center;
    background-size:cover ;
    border-radius: 20px;
    overflow: hidden;
}
.item{
    position: absolute;
    top: 0;
    left: 0;
    width: 58%;
    height: 100%;
    color: #fff;
    background: transparent;
    padding: 80px;
    display: flex;
    justify-content: space-between;
    flex-direction: column;
}
.item .logo{
    color: #fff;
    font-size: 30px;
}
.text-item h2{
    font-size: 40px;
    line-height: 1;
}
.text-item p{
    font-size: 16px;
    margin: 20px 0;
}
.social-icon a i{
    color: #fff;
    font-size: 24px;
    margin-left: 10px;
    cursor: pointer;
    transition: .5s ease;
}
.social-icon a:hover i{
    transform: scale(1.2);
}
.container .login-section{
    position: absolute;
    top: 0;
    right: 0;
    width: calc(100% - 58%);;
    min-width: 400px;
    max-width: 500px;
    height: 100%;
    color: #fff;
    backdrop-filter: blur(10px);
}

.login-section .form-box{
    position: absolute;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    
}
.login-section .form-box.register{
    transform: translateX(430px);
    transition: transform .6s ease;
    transition-delay: 0s;
}
.login-section.active .form-box.register{
    transform: translateX(0px);
    transition-delay: .7s;
}

.login-section .form-box.login{
    transform: translateX(0px);
    transition: transform .6s ease;
    transition-delay: 0.7s;
}
.login-section.active .form-box.login{
    transform: translateX(430px);
    transition-delay: 0s;
}


.login-section .form-box h2{
    text-align: center;
    font-size: 25px;
}

.role-select{
    font-size: 14px;
    font-weight: 500;
    margin-top: 30px;
    display: flex;
    justify-content: start;
}


.remember-password{
    font-size: 14px;
    font-weight: 500;
    margin:5 0 15px ;
    margin-top: 30px;
    margin-bottom: 30px;
    display: flex;
    justify-content: space-between;
}

.remember-password label input{
    accent-color: #fff;
    margin-right: 3px;
}

.remember-password a{
    color: #fff;
    text-decoration: none;
}
.remember-password a:hover{
    text-decoration: underline;
}

.create-account{
    font-size: 14.5px;
    text-align: center;
    margin: 25px;
}



.sta-title {
    font-size: 24px;
    font-weight: bold;
    text-align: center;
    padding: 20px 0;
}
.sta-section-title {
    font-size: 20px;
    font-weight: bold;
    padding: 10px 0;
}
.sta-content {
    padding: 5px 0;
}


.about-colunm{
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}
</style>

<style scoped>
    .clickable {
        cursor: pointer;
    }
</style>

<script setup>
import { reactive, ref } from 'vue'
import { post } from '@/net'
import { Snackbar } from '@varlet/ui'
import router from '@/router'
import '@/assets/newDialog.css'
import '@/assets/texts.css'
import GuidePanel from '../components/guide/GuidePanel.vue'

const showAbout = ref(false)
const showHelp = ref(false)
const showContact = ref(false)
const showStatement = ref(false)

const loginForm = reactive({
  idOrEmail: '',
  password: ''
})

const registerForm = reactive({
  email: '',
  password: '',
  password_repeat: ''
})

const forgetForm = reactive({
  email: ''
})

const waitForget = ref(false)
const auth = ref('user')
const accept = ref(false)

function checkLogin() {
  if (loginForm.idOrEmail === '') {
    Snackbar.warning('ID或邮箱不能为空')
    return false
  }
  if (loginForm.password === '') {
    Snackbar.warning('密码不能为空')
    return false
  }
  if (loginForm.password.length < 6 || loginForm.password.length > 16) {
    Snackbar.warning('密码长度必须为6到16位')
    return false
  }
  return true
}

function checkMail(email) {
  const reg = /^[0-9a-zA-Z_.-]+[@][0-9a-zA-Z_.-]+([.][a-zA-Z]+){1,2}$/
  return reg.test(email)
}

function checkRegister() {
  if (!accept.value) {
    Snackbar.warning('请同意用户协议')
    return false
  }
  if (registerForm.email === '') {
    Snackbar.warning('邮箱不能为空')
    return false
  }
  if (!checkMail(registerForm.email)) {
    Snackbar.warning('请输入正确的邮箱格式')
    return false
  }
  if (registerForm.password === '') {
    Snackbar.warning('密码不能为空')
    return false
  }
  if (registerForm.password.length < 6 || registerForm.password.length > 16) {
    Snackbar.warning('密码长度必须为6到16位')
    return false
  }
  if (registerForm.password !== registerForm.password_repeat) {
    Snackbar.warning('两次输入的密码不一致')
    return false
  }
  return true
}

const login = () => {
  if (!checkLogin()) return

  if (auth.value === 'user') {
    post('/user/login',
      {
        idOrEmail: loginForm.idOrEmail,
        password: loginForm.password
      },
      (message) => {
        localStorage.setItem('token', message.token)
        localStorage.setItem('avatar', message.profilePhotoUrl)
        localStorage.setItem('auth', 'user')
        Snackbar.success('登陆成功')
        router.push('/')
      },
      () => Snackbar.error('用户不存在或密码错误')
    )
  } else {
    // admin
    post('/user/adminLogin',
      {
        account: loginForm.idOrEmail,
        password: loginForm.password
      },
      (message) => {
        localStorage.setItem('token', message.token)
        localStorage.setItem('auth', 'admin')
        Snackbar.success('登陆成功')
        router.push('/')
      },
      () => Snackbar.error('管理员不存在或密码错误')
    )
  }
}

function register() {
  if (!checkRegister()) return

  post('/user/register',
    {
      email: registerForm.email,
      password: registerForm.password
    },
    () => {
      Snackbar.success('注册成功')
      // optionally prefill login
      loginForm.idOrEmail = registerForm.email
      loginForm.password = registerForm.password
      // clear fields
      registerForm.email = ''
      registerForm.password = ''
      registerForm.password_repeat = ''
      jump2Login()
    },
    () => Snackbar.error('注册失败')
  )
}

const loginSecRef = ref(null)
const forgetPass = ref(false)

function jump2Register() {
  loginSecRef.value?.classList.add('active')
}

function jump2Login() {
  loginSecRef.value?.classList.remove('active')
}

function jump2Forget() {
  forgetPass.value = true
  setTimeout(() => jump2Register(), 100)
}

function jump2LoginFromForget() {
  jump2Login()
  setTimeout(() => {
    forgetPass.value = false
  }, 500)
}

function getPassword() {
  if (!checkMail(forgetForm.email)) {
    Snackbar.warning('请输入正确的邮箱')
    return
  }
  waitForget.value = true
  post('/user/getPwd',
    { email: forgetForm.email },
    () => {
      Snackbar.success('发送成功，请查看您的邮箱！')
      setTimeout(() => { waitForget.value = false }, 60000)
    },
    () => {
      Snackbar.error('该账户不存在！')
      setTimeout(() => { waitForget.value = false }, 1000)
    }
  )
}

const isLogin = ref(false)

function checkLoginState() {
  const token = localStorage.getItem('token') || ''
  if (token !== '') {
    isLogin.value = true
    setTimeout(() => {
      isLogin.value = false
      router.push('/resources/1')
    }, 2000)
  }
}

const helpEntry = [
  {
    src: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/start/login.png',
    title: '登录',
    content: '如果您已经拥有用户账号，或者已经拥有管理员账号，请您选择您的身份，并输入用户名和密码进行登录。'
  },
  {
    src: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/start/register.png',
    title: '注册',
    content: '如果您还没有易言平台账号，您可以点击注册按钮，并提供您的邮箱地址，设置您的密码，注册用户账号。'
  },
  {
    src: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/start/forget.png',
    title: '找回密码',
    content: '如果您忘记了您用户账号的密码，您可以点击找回密码，输入您的邮箱地址，相应账号的密码将被重置，并将重置后的密码发送到您的邮箱。'
  }
]

// checkLoginState()
</script>
