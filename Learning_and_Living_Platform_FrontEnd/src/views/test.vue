<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex;justify-content: center;">
    <div class="wrapper">
      <div class="logo">
        <img src="https://zzq-typora-picgo.oss-cn-beijing.aliyuncs.com/7e1862cd3d3a572d2e6a7173e2f846b4_1.jpg" alt="zzq">
      </div>
      <div class="title">
        <p>Sign in to Github</p>
      </div>
      <div class="form">
        <div class="input_field">
          <label>Username or email address</label>
          <Input v-model="form.idOrEmail" class="input" />
        </div>
        <div class="input_field">
          <label>Password</label>
          <Input v-model="form.password" type="password" class="input" />

          <var-link type="primary" to="/test" class="forgot" underline="hover">Forgot password?</var-link>
        </div>
        <div class="input_field">
          <label>请选择你的身份</label>
          <RadioGroup v-model="auth">
            <Radio label="user">用户</Radio>
            <Radio label="admin">管理员</Radio>
          </RadioGroup>
        </div>
        <div style="text-align: center;">
          <var-button @click="login()" color="#66bb6a" text-color="#fff">Sign in</var-button>
        </div>
      </div>
      <div class="create_act">
        <p>New to GitHub? <var-link type="primary" to="/register" underline="hover">Create idOrEmail</var-link></p>

      </div>
    </div>
  </div>
  <v-dialog v-model="isLogin" :scrim="false" persistent width="auto">
    <v-card color="primary">
      <v-card-text>
        您已登录！
        <v-progress-linear indeterminate color="white" class="mb-0"></v-progress-linear>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { reactive, ref } from "vue";
import { post, get } from "@/net";
import { Snackbar } from '@varlet/ui'
import router from "@/router"

const form = reactive({
  idOrEmail: '',
  password: ''
})

const auth = ref('user')
const isLogin = ref(false)


const login = () => {
  if (form.idOrEmail == '' || form.password == '') {
    Snackbar.warning('请填写登录信息')
  } else {
    console.log({ idOrEmail: form.idOrEmail })
    get('user/getSalt', {
      idOrEmail: "1134380859@qq.com"
    },
      (message) => {
        form.password = form.password + message.salt
        console.log(form.password)
        if (auth.value == 'user') {
          post('user/login', {
            idOrEmail: form.idOrEmail,
            password: form.password
          }, (message) => {
            localStorage.setItem('token', message.token)
            router.push('/')
            Snackbar.success("登陆成功")
          }, () => {
            Snackbar.error("用户不存在或密码错误")
          })
        } else if (auth.value == 'admin') {
          post('user/adminLogin', {
            account: form.idOrEmail,
            password: form.password
          }, (message) => {
            console.log(message)
            localStorage.setItem('token', message.token)
            router.push('/')
            Snackbar.success("登陆成功")
          }, () => {
            Snackbar.error("管理员不存在或密码错误")
          })
        }
      },
      () => {
        Snackbar.error("出现异常错误")
      }
    )
  }
}

function checkLogin() {
  //if (localStorage.getItem('token') != '') {
  isLogin.value = true
  setTimeout(() => {
    isLogin.value = false
  }, 2000)
  //}
}

checkLogin()

</script>

<style scoped>
.wrapper {
  max-width: 310px;
  width: 100%;
  margin: 40px auto;
  height: auto;
}

.wrapper .logo img {
  display: block;
  width: 48px;
  height: 48px;
  margin: 0 auto 25px;
}

.wrapper .title p {
  margin-bottom: 15px;
  font-size: 24px;
  text-align: center;
  color: #333;
}

.wrapper .form {
  margin-bottom: 15px;
  background-color: #fff;
  border: 1px solid #d8dee2;
  border-radius: 5px;
  padding: 20px;
}

.wrapper .form .input_field {
  margin-bottom: 15px;
  position: relative;
}

.wrapper .form .input_field label {
  display: block;
  font-weight: 600;
  margin-bottom: 7px;
  color: #24292e;
}

.wrapper .form .input_field .input {
  text-align: center;
  width: 100%;
  font-size: 16px;
  line-height: 20px;
  min-height: 34px;
  padding: 6px 8px;
}

.wrapper .form .input_field a.forgot {
  position: absolute;
  top: 0;
  right: 0;
  font-size: 12px;
}

.wrapper .form .input_field .input:focus {
  border-color: #2188ff;
  box-shadow: inset 0 1px 2px rgba(27, 31, 35, .075), 0 0 0 2.5px rgba(3, 102, 214, .3);
}


.wrapper .create_act {
  border: 1px solid #d8dee2;
  border-radius: 5px;
  padding: 15px 20px;
  text-align: center;
  margin-bottom: 40px;
}

.wrapper .create_act a:hover {
  text-decoration: underline;
}
</style>