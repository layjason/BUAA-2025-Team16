<template>
    <div>
        <!-- 最外层容器，用于将面板放置在中间 -->
        <div style="display: flex; justify-content: center; align-items: center; min-height: 85vh;">
            <!-- 面板容器 -->
            <div style="width: 60%;">
                <!-- 导航区 -->
                <div
                    style="display: flex; flex-direction: row; justify-content: space-between; padding-top: 10px; width: 100%;">
                    <div class="guide-panel glass shadow-act clickable" title="点击进入教育资源专区"
                        @click="router.push('/resources/1')">
                        <v-icon icon="mdi-package-variant" size="100" class="guide-icon"></v-icon>
                        <div class="guide-title title-dark">教育资源</div>
                    </div>
                    <div class="guide-panel"
                        :class="[isGuest ? 'shadow-static disable-guide-panel disable-glass' : 'shadow-act glass clickable']"
                        :title="postTitle" @click="clickPost">
                        <v-icon icon="mdi-post" size="100" class="guide-icon dark-title"></v-icon>
                        <div class="guide-title title-dark">论坛</div>
                    </div>
                    <div class="guide-panel"
                        :class="[isGuest ? 'shadow-static disable-guide-panel disable-glass' : 'shadow-act glass clickable']"
                        :title="personTitle" @click="clickPerson">
                        <v-icon icon="mdi-account-edit" size="100" class="guide-icon"></v-icon>
                        <div class="guide-title title-dark">{{ personName }}</div>
                    </div>
                    <div v-if="isAdmin" class="guide-panel glass shadow-act clickable" title="点击以查看统计信息"
                        @click="router.push('/statistics')">
                        <v-icon icon="mdi-chart-line" size="100" class="guide-icon"></v-icon>
                        <div class="guide-title title-dark">统计信息</div>
                    </div>
                    <div class="guide-panel glass shadow-act clickable" title="点击以查看帮助">
                        <v-icon icon="mdi-help-circle" size="100" class="guide-icon"></v-icon>
                        <div class="guide-title title-dark">帮助</div>
                    </div>
                </div>

                <!-- 推荐资源区 -->
                <div style="margin-top: 0px; width: 100%">
                    <v-carousel v-if="!isGettingRes" cycle hide-delimiters hide-delimiter-background show-arrows="hover"
                        height="250" style="width: 100%" interval="7000">
                        <!-- 一页 -->
                        <v-carousel-item v-for="(group, index) in hotResData" :key="index">
                            <div style="display: flex; justify-content: space-between;">
                                <!-- 一项 -->
                                <div v-for="(item, subIndex) in group" :key="subIndex" class="res-panel"
                                    :class="[item.title == '' ? 'disable-res-panel disable-glass shadow-static' : 'glass clickable shadow-act']"
                                    @click="openRes(item.id)" title="点击查看推荐资源">
                                    <div v-if="item.title != ''"
                                        style="display: flex; flex-direction: column; justify-content: start; align-items: start; width: 100%;">
                                        <v-icon icon="mdi-package-variant" size="30"></v-icon>
                                        <div class="small-title-dark title-break" :title="item.title">{{ item.title }}
                                        </div>
                                        <v-chip variant="flat" style="margin-top: 3px; font-size: 13px; height: 25px;"
                                            :title="'该资源属于' + channelMapping[item.subject] + '频道'">{{
                                            channelMapping[item.subject] }}</v-chip>
                                        <!-- <div>
                                        <v-chip variant="outlined"
                                            style="margin-top: 5px; margin-right: 5px; font-size: 13px; height: 25px;" 
                                            v-for="type in convertNumbersToTypes(item.categories)"
                                            :title="'该资源包含类型为' + type + '的内容'"
                                        > {{ type }} </v-chip>
                                    </div> -->
                                    </div>
                                    <div v-if="item.profilePhotoUrl != null && item.profilePhotoUrl != ''"
                                        style="margin-top: 5px;">
                                        <Image :src="item.profilePhotoUrl" fit="contain" style="height: 100px"></Image>
                                    </div>
                                    <div v-if="item.title == ''">
                                        <v-icon icon="mdi-package-variant-closed" size="30"></v-icon>
                                    </div>
                                </div>
                            </div>
                        </v-carousel-item>
                    </v-carousel>
                    <div v-if="isGettingRes" style="display: flex; justify-content: space-between;">
                        <Skeleton v-for="index in resGroupSize" loading animated :title="{ width: 100 }"
                            class="res-panel disable-res-panel disable-glass shadow-static"
                            style="height: 220px; margin-bottom: 18px;" title="请稍候">
                            <template #template>
                                <v-icon icon="mdi-package-variant-closed" size="30"></v-icon>
                                <SkeletonItem block width="70%" height="16px" style="margin: 3px 0px;" />
                                <SkeletonItem block width="20%" height="16px" style="margin-top: 3px 0px;" />
                                <SkeletonItem width="80%" height="100px" />
                            </template>
                        </Skeleton>
                    </div>
                </div>

                <!-- 热门帖子区 -->
                <div style="margin-top: -10px; width: 100%">
                    <v-carousel v-if="!isGettingPost && !isGuest" cycle hide-delimiters hide-delimiter-background
                        show-arrows="hover" height="250" style="width: 100%" interval="5000">
                        <!-- 一页 -->
                        <v-carousel-item v-for="(group, index) in hotPostData" :key="index">
                            <div style="display: flex; justify-content: space-between;">
                                <!-- 一项 -->
                                <div v-for="(item, subIndex) in group" :key="subIndex" class="res-panel"
                                    :class="[item.title == '' ? 'disable-res-panel disable-glass shadow-static' : 'glass clickable shadow-act']"
                                    @click="openPost(item.id)" title="点击查看帖子详情">
                                    <div v-if="item.title != ''"
                                        style="display: flex; flex-direction: column; justify-content: start; align-items: start; width: 100%;">
                                        <v-icon icon="mdi-post" size="30"></v-icon>
                                        <div class="small-title-dark title-break" :title="item.title"
                                            style="margin-top: 5px;">{{
                                            item.title }}</div>
                                        <div style="display: flex; justify-content: space-between; margin-top: 3px; width: 100%;"
                                            class="text-dark">
                                            <div style="text-align: left;" class="title-break">发布者</div>
                                            <div style="text-align: right;" class="title-break">{{ item.userName || '未知'
                                                }}</div>
                                        </div>
                                        <div style="display: flex; justify-content: space-between; margin:0px 0px; width: 100%;"
                                            class="text-dark">
                                            <div style="text-align: left;" class="title-break">权限等级</div>
                                            <div style="text-align: right;" class="title-break">{{ item.authority +
                                                '级'}}</div>
                                        </div>
                                        <div style="display: flex; justify-content: space-between; margin:0px 0px; width: 100%;"
                                            class="text-dark">
                                            <div style="text-align: left;" class="title-break">更新时间</div>
                                            <div style="text-align: right;" class="title-break">{{ item.updateTime ||
                                                '未知' }}</div>
                                        </div>
                                        <div
                                            style="display: flex; justify-content: space-between; margin-top: 10px; width: 100%;">
                                            <div
                                                style="display: flex; flex-direction: column; align-items: center; margin: 10px;">
                                                <v-icon icon="mdi-eye" size="25"></v-icon>
                                                <div class="text-dark">{{ item.browseCount }}</div>
                                            </div>
                                            <div
                                                style="display: flex; flex-direction: column; align-items: center; margin: 10px;">
                                                <v-icon icon="mdi-thumb-up" size="25"></v-icon>
                                                <div class="text-dark">{{ item.likeCount }}</div>
                                            </div>
                                            <div
                                                style="display: flex; flex-direction: column; align-items: center; margin: 10px;">
                                                <v-icon icon="mdi-fire" size="25"></v-icon>
                                                <div class="text-dark">{{ item.hotPoint }}</div>
                                            </div>
                                        </div>
                                    </div>

                                    <div v-if="item.title == ''">
                                        <v-icon icon="mdi-post-outline" size="30"></v-icon>
                                    </div>
                                </div>
                            </div>
                        </v-carousel-item>
                    </v-carousel>
                    <div v-if="isGettingPost || isGuest" style="display: flex; justify-content: space-between;"
                        :title="isGettingPost ? '请稍候' : '登陆后查看热门帖子'">
                        <Skeleton v-for="index in postGroupSize" loading :animated="isGettingPost"
                            class="res-panel disable-res-panel disable-glass shadow-static">
                            <template #template>
                                <div style="display: flex; flex-direction: column;">
                                    <v-icon icon="mdi-post-outline" size="30"></v-icon>
                                    <SkeletonItem v-if="isGettingPost" block width="40%" height="20px"
                                        style="margin: 5px 0px;" />
                                    <div class="small-title-dark">登录以查看热帖</div>
                                    <SkeletonItem block width="100%" height="16px" style="margin: 3px 0px;" />
                                    <SkeletonItem block width="100%" height="16px" style="margin: 3px 0px;" />
                                    <SkeletonItem block width="100%" height="16px" style="margin: 3px 0px;" />
                                    <div style="display: flex; justify-content: space-between; margin-top: 10px;">
                                        <SkeletonItem width="60px" height="60px" style="margin-top: 0px;" />
                                        <SkeletonItem width="60px" height="60px" style="margin-top: 0px;" />
                                        <SkeletonItem width="60px" height="60px" style="margin-top: 0px;" />
                                    </div>
                                </div>
                            </template>
                        </Skeleton>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>



<script setup>
import '@/assets/action.css'
import '@/assets/texts.css'
import router from "@/router"
import { ref, watch } from 'vue'
import { Snackbar } from '@varlet/ui'
import { post, get, delet, put } from "@/net"

const isAdmin = ref(false)
const isGuest = ref(false)



// 是否登录判断
const token = localStorage.getItem('token')
// console.log(token)
if (token == null || token == '') {
    isGuest.value = true
}



// 显示内容判断
const postTitle = ref('点击进入帖子专区')
const personTitle = ref('点击进入个人中心')
const personName = ref('个人中心')


// 导航点击事件
const clickPost = () => {
    if (isGuest.value) {
        return
    }

    router.push('/posts/1')
}
const clickPerson = () => {
    if (isGuest.value) {
        return
    }

    if (isAdmin.value) {
        router.push('/manage')
    } else {
        router.push('/users')
    }
}

// 推荐资源
const isGettingRes = ref(true)
const hotResData = ref([])
const resGroupSize = ref(5)

const channelMapping = {
    0: '理学工学',
    1: '农学',
    2: '外语',
    3: '经济管理',
    4: '计算机',
    5: '音乐与艺术',
    6: '心理学',
    7: '文史哲法',
    8: '医学与保健',
    9: '教育教学',
}
const typeMapping = {
    0: '文档',
    1: '源代码',
    2: "视频",
    3: "图片",
    4: "音频",
    5: "其他",
    // TODO
};
function convertNumbersToTypes(numbers) {
    return numbers.map(number => typeMapping[number]);
}
const saveHotRes = (message) => {
    console.log('saveHotRes')
    if (message.list != null) {
        message = message.list
    }
    console.log(message)
    if (message.length == 0) {
        Snackbar.error('推荐资源为空！')
        return
    }

    // 将数据转化为二维数组，并补充空数据
    hotResData.value = []
    var groupCnt = Math.ceil(message.length / resGroupSize.value)
    for (let i = 0; i < groupCnt; i++) {
        let group = message.slice(i * resGroupSize.value, (i + 1) * resGroupSize.value);

        while (group.length < resGroupSize.value) {
            group.push({
                title: '',
                id: -1,
            })
        }

        hotResData.value.push(group)
    }

    // console.log(hotResData.value)
    // console.log(hotResData.value[0])
    // console.log(hotResData.value[0][0])
}
const getHotRes = async () => {
    isGettingRes.value = true
    if (isGuest.value || isAdmin.value) {
        console.log('search')
        await post(
            // 搜索
            '/resource/searchResource',
            {
                keywords: '',
                subjects: [...Array(10).keys()],
                categories: [...Array(6).keys()],
                cntInPage: 14,
                pageNum: 1,
            },
            saveHotRes
        )
    } else {
        console.log('recommend')
        await get(
            // 推荐
            '/resource/listResourceRecommend',
            saveHotRes
        )
    }
    isGettingRes.value = false
}
const openRes = (id) => {
    if (!(id > 0)) return;
    router.push(`/resource/${id}`);
}

// 推荐帖子
const isGettingPost = ref(true)
const hotPostData = ref([])
const postGroupSize = 3
const getHotPost = async () => {
    isGettingPost.value = true
    console.log("get hot post");
    await get(
        '/post/getHotPost',
        (message) => {
            console.log('getHotPost')
            console.log(message)

            if (message.length == 0) {
                Snackbar.error('热门帖子为空！')
                return
            }

            hotPostData.value = []
            var groupCnt = Math.ceil(message.length / postGroupSize)
            for (let i = 0; i < groupCnt; i++) {
                let group = message.slice(i * postGroupSize, (i + 1) * postGroupSize);

                while (group.length < postGroupSize) {
                    group.push({
                        title: '',
                        id: -1,
                    })
                }

                hotPostData.value.push(group)
            }
        }
    )
    isGettingPost.value = false
}

const openPost = (postId) => {
    // if (!(postId > 0)) {
    //     return
    // }

    // window.open('/post/' + postId, '_blank');
    if (!(postId > 0)) return
  router.push(`/post/${postId}`)
}

// 是否管理员判断
const judgeAdmin = async () => {
    await get(
        '/user/verifyToken',
        (message) => {
            console.log(message)
            if (message == 1) {
                isAdmin.value = true
                console.log('Admin')
                resGroupSize.value = 4
                personName.value = '用户管理'
                personTitle.value = '点击进入用户管理'
            } else {
                console.log('User')
            }
        }
    )
    getHotPost()
    getHotRes()
}
if (isGuest.value == false) {
    judgeAdmin()
} else {
    isGettingPost.value = false
    postTitle.value = '登陆后进入帖子专区'
    personTitle.value = '登陆后进入个人中心'
    getHotRes()
}

</script>



<style scoped>
.clickable {
    cursor: pointer;
}
</style>


<style>
.guide-panel {
    border-radius: 10px;
    padding: 10px;
    width: 100%;

    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;

    margin: 10px;

    transition-property: margin, padding, box-shadow;
    transition-duration: 0.2s;
    transition-timing-function: linear;
}

.guide-panel:hover {
    margin-left: 6px;
    margin-right: 6px;
    margin-top: 2px;
    margin-bottom: 2px;
    padding: 15px;
}

.disable-guide-panel:hover {
    margin: 10px;
    padding: 10px;
}

.res-panel {
    border-radius: 10px;
    padding: 15px;
    margin: 10px;
    width: 100%;
    height: 220px;

    margin: 10px;

    transition-property: margin, padding, height, box-shadow;
    transition-duration: 0.2s;
    transition-timing-function: linear;
}

.res-panel:hover {
    margin-left: 6px;
    margin-right: 6px;
    margin-top: 5px;
    margin-bottom: 5px;
    padding: 20px 18px;
    height: 230px;
}

.disable-res-panel:hover {
    margin: 10px;
    padding: 15px;
    height: 220px;
}


.res-chip {
    font-size: 12px;
    height: 20px;
}
</style>