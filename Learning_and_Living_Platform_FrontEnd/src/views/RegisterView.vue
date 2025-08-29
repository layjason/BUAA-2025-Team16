<template>
    <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex;justify-content: center;">
        <div class="wrapper">
            <div class="logo">
                <img src="https://img1.baidu.com/it/u=1960110688,1786190632&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=281" alt="zzq">
            </div>
            <div class="title">
                <p>Sign up</p>
            </div>
            <div class="form">
                <div class="input_field">
                    <label>Email address</label>
                    <Input v-model="form.email"  class="input" />
                </div>
                <div class="input_field">
                    <label>Password</label>
                    <Input v-model="form.password" type="password" class="input" />
                </div>
                <div class="input_field">
                    <label>Confirm Password</label>
                    <Input v-model="form.password_repeat" type="password" class="input" />
                </div>
                <div style="text-align: center;" @click="submitRegister()">
                    <var-button color="#66bb6a" text-color="#fff">Submit</var-button>
                </div>
            </div>
            <div class="create_act">
                <var-link type="primary" to="/login" underline="hover">Back to login</var-link>
            </div>
        </div>
    </div>
</template>

<script setup>
import { Snackbar } from "@varlet/ui";
import { reactive } from "vue";
import { post } from "@/net";

const form = reactive({
    email: '',
    password: '',
    password_repeat: '',
})

function submitRegister() {
    post("user/register", {
        email: form.email,
        password: form.password,
    },
        () => {
            Snackbar.success("success")
         },
        () => { 
            Snackbar.error("error")
        },
    )
}

</script>

<style scoped>

.wrapper{
max-width: 310px;
width: 100%;
margin: 40px auto;
height: auto;
}

.wrapper .logo img{
display: block;
width: 48px;
height: 48px;
margin: 0 auto 25px;
}

.wrapper .title p{
margin-bottom: 15px;
font-size: 24px;
text-align: center;
color: #333;
}

.wrapper .form{
margin-bottom: 15px;
background-color: #fff;
border: 1px solid #d8dee2;
border-radius: 5px;
padding: 20px;
}

.wrapper .form .input_field{
margin-bottom: 15px;
position: relative;
}

.wrapper .form .input_field label{
display: block;
font-weight: 600;
margin-bottom: 7px;
color: #24292e;
}

.wrapper .form .input_field .input{
    text-align: center;
    width: 100%;
    font-size: 16px;
    line-height: 20px;
    min-height: 34px;
    padding: 6px 8px;
}

.wrapper .form .input_field a.forgot{
position: absolute;
top:  0;
right: 0;
font-size: 12px;
}

.wrapper .form .input_field .input:focus{
border-color: #2188ff;
  box-shadow: inset 0 1px 2px rgba(27,31,35,.075), 0 0 0 2.5px rgba(3,102,214,.3);
}


.wrapper .create_act{
border: 1px solid #d8dee2;
  border-radius: 5px;
  padding: 15px 20px;
  text-align: center;
  margin-bottom: 40px;
}

.wrapper .create_act a:hover{
text-decoration: underline;
}

</style>