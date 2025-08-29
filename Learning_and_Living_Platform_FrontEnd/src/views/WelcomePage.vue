<template>
    <var-space :size="[10, 10]">
        <var-button @click="router.push('/login')"
        color="linear-gradient(to right, #69dbaa, #3a7afe)"
        text-color="#fff"
        >
        登录
        </var-button>
        <var-button @click="hello()"
        color="linear-gradient(to right, #69dbaa, #3a7afe)"
        text-color="#fff"
        >
        hello
        </var-button>
    </var-space>
</template>



<script setup>
import router from "@/router";
import axios from "axios";

const hello = () => {
    axios.get('hello')
    .then(function (response) { 
    console.log(response);
    })
    .catch(function (error) { 
    console.log(error);
    });
}


</script>

<style scoped>

</style>