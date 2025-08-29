<!-- 在该文件中使用了View Design和varlet组件 -->
<template>
    <div class="page-container">
        <div class="left-column">
            <Affix :offset-top="20" style="margin-top: 20px;">
                <div>
                    <!-- 频道选择框 -->
                    <div class="side-panel left-panel top-panel side-panel-custom side-expand shadow-act glass"
                        title="选择资源频道以作为搜索筛选条件">
                        <div style="display: flex; flex-direction: row; align-items: center;">
                            <h2 class="title-dark" style="flex: 1;">频道</h2>
                            <!-- <div style="flex: 1; display: flex; justify-content: flex-end;">
                            <Checkbox
                            :indeterminate="channelIndeterminate"
                            :model-value="channelAll"
                            @click.prevent="handleChannelAll">全选</Checkbox>
                        </div> -->
                            <v-btn variant="tonal" color="red"
                                style="margin-right: 10px; border-radius: 20px; height: 30px;"
                                @click="setChannelEmpty">全部取消</v-btn>
                            <v-btn variant="tonal" color="blue" style="border-radius: 20px; height: 30px;"
                                @click="setChannelAll">全选</v-btn>
                        </div>
                        <v-alert type="error" variant="tonal" title="频道为空" text="必须至少选择一个资源频道" v-if="channelAlert"
                            style="border-radius: 10px; margin-top: 10px;" />
                        <div style="display: flex; flex-direction: column;">
                            <!-- <CheckboxGroup v-model="channelGroup" @on-change="checkChannelGroupChange" style="margin-top: 15px;" class="checkbox-container-2">
                            <Checkbox v-for="item in fullChannelGroup" :label="item"></Checkbox>
                        </CheckboxGroup> -->
                            <v-chip-group v-model="channelGroup" column mandatory multiple class="checkbox-container-2">
                                <div v-for="t in fullChannelGroup">
                                    <v-chip variant="tonal" filter class="chip-entry">{{ t }}</v-chip>
                                </div>
                            </v-chip-group>
                        </div>
                    </div>
                    <!-- 类型选择框 -->
                    <div class="side-panel left-panel side-panel-custom side-expand shadow-act glass"
                        title="选择资源类型以作为搜索筛选条件">
                        <div style="display: flex; flex-direction: row; align-items: center;">
                            <h2 class="title-dark" style="flex: 1;">类型</h2>
                            <!-- <Checkbox
                        :indeterminate="typeIndeterminate"
                        :model-value="typeAll"
                        @click.prevent="handleTypeAll">全选</Checkbox> -->
                            <v-btn variant="tonal" color="red"
                                style="margin-right: 10px; border-radius: 20px; height: 30px;"
                                @click="setTypeEmpty">全部取消</v-btn>
                            <v-btn variant="tonal" color="blue" style="border-radius: 20px; height: 30px;"
                                @click="setTypeAll">全选</v-btn>
                        </div>
                        <v-alert type="error" variant="tonal" title="类型为空" text="必须至少选择一个资源类型" v-if="typeAlert"
                            style="border-radius: 10px; margin-top: 10px;" />
                        <div style="display: flex; flex-direction: column;">
                            <!-- <CheckboxGroup v-model="typeGroup" @on-change="checkTypeGroupChange" class="checkbox-container-3">
                            <Checkbox v-for="item in fullTypeGroup" :label="item"></Checkbox>
                        </CheckboxGroup> -->
                            <v-chip-group v-model="typeGroup" column mandatory multiple class="checkbox-container-3">
                                <div v-for="t in fullTypeGroup">
                                    <v-chip variant="tonal" filter class="chip-entry">{{ t }}</v-chip>
                                </div>
                            </v-chip-group>
                        </div>
                    </div>
                    <!-- 修改确认 -->
                    <div class="side-panel left-panel shadow-act side-expand glass insert-img">
                        <v-btn width="100%" variant="flat" color="blue" title="应用筛选并重新搜索"
                            @click="trySearch">应用筛选</v-btn>
                    </div>
                </div>
            </Affix>
        </div>








        <!-- 中间栏 -->
        <div class="centre-column" style="margin-bottom: 50px;">
            <!-- 搜索栏 -->
            <!-- 将margin设置在这里能够让Vue正确的处理在Affix吸顶之后之后组件的位置 -->
            <Affix :offset-top="20" style="margin-top: 20px; margin-bottom: 20px;" @on-change="changeFix">
                <div style="margin-left: 50px; margin-right: 50px; border-radius: 10px; padding: 10px; background-color: #ffffff;"
                    :class="isFix ? 'search_shadow_large' : 'search_shadow_small'" title="输入内容以在删选条件下搜索">
                    <Input v-model="searchText" placeholder="搜索..." :disabled="isLoading" search enter-button
                        :border="false" size="large" @on-search="trySearch" @on-enter="trySearch" />
                </div>
            </Affix>
            <!-- 资源列表 -->
            <div v-if="!isLoading">
                <!-- key属性保证每个小组件有唯一确定的id，方便Vue进行就地更新与就地复用 -->
                <ResSummaryEntry v-for="data in resDatas" :key="data.id" :resEntryData="data" :show="showDelete"
                    :deleting="isDeleting" @deleteClicked="clickDelete" @detailClicked="tryDetail" />
            </div>
            <!-- 骨架屏 -->
            <div v-if="isLoading">
                <Skeleton loading avatar animated class="centre-panel centre-expand shadow-static skeleton-static glass"
                    v-for="index in entryInPage" />
            </div>
            <!-- 大翻页器 -->
            <div style="display: flex; justify-content: center; align-items: center;"
                class="bottom-page shadow-act glass-light">
                <!-- <Page :total="entryCnt" v-model:model-value="pageNow" :page-size="entryInPage"/> -->
                <v-pagination v-model="pageNow" :length="totalPage" size="small" :total-visible="8"
                    @update:modelValue="changePage" :disabled="isLoading"></v-pagination>
            </div>
        </div>





        <!-- 右边栏 -->
        <div class="right-column">
            <Affix :offset-top="20" style="margin-top: 20px; margin-bottom: 20px;">
                <div>
                    <!-- 通知栏 -->
                    <div class="side-panel right-panel top-panel side-panel-custom shadow-act glass insert-img"
                        title="欢迎来到本平台">
                        <Image src="https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/74537149_p0.jpg"
                            fit='contain' />
                        <div style="margin: 15px;">
                            <p>欢迎使用易言学习生活平台，请尽情徜徉在资源的海洋，更欢迎上传自己的资源。另，广告位招租。</p>
                        </div>
                    </div>
                    <!-- 新资源选择框 -->
                    <div class="side-panel right-panel shadow-act side-expand glass insert-img">
                        <!-- <Button type="success" long style="color: #ffffff;" @click="openNewRes">发布新资源</Button> -->
                        <!-- 发布新资源对话框及按钮 -->
                        <v-dialog v-model="newResModal" persistent class="overlay">
                            <template v-slot:activator="{ props }">
                                <v-btn width="100%" color="green" v-bind="props" title="激活对话框以发布新资源">发布新资源</v-btn>
                            </template>
                            <!-- 包裹整个弹出框 -->
                            <div class="dialog-container">
                                <div class="dialog">
                                    <h2 class="title-dark">发布新资源</h2>
                                    <v-alert :type="alert.type" variant="tonal" :title="alert.title"
                                        :text="alert.message" v-if="alert.show"
                                        style="border-radius: 10px; margin-bottom: 10px; margin-top: 10px; white-space: pre-wrap;" />
                                    <!-- 按钮之上的部分 -->
                                    <div style="margin-top: 20px;">
                                        <div class="form-row">
                                            <div :class="['form-title', { 'red': newResForm.title === '' }]">
                                                <p>资源标题</p>
                                            </div>
                                            <div class="form-content">
                                                <Input v-model="newResForm.title" placeholder="请输入资源标题..."
                                                    :disabled="isSending" show-word-limit maxlength="20" />
                                            </div>
                                        </div>

                                        <div class="form-row">
                                            <div :class="['form-title', { 'red': newResForm.fileUrl === '' }]">
                                                <p>资源文件</p>
                                            </div>
                                            <div class="form-content">
                                                <Upload ref="fileUpload" type="drag" action="../api/resource/uploadFile"
                                                    :disabled="isSending || fileSending" :max-size="512000"
                                                    :on-success="successFile" :on-exceeded-size="exceedFile"
                                                    :on-error="errorFile" :on-progress="fileOrPhotoSending"
                                                    :on-remove="closeFile" :before-upload="beforeFile" :headers="{
                                                        'token': getToken(),
                                                        'Accept': 'application/json, text/plain, */*'
                                                    }" title="上传一个文件作为资源的内容">
                                                    <div style="padding: 10px 0">
                                                        <!-- 未上传 -->
                                                        <div v-if="!isSending && newResForm.fileUrl == ''">
                                                            <v-icon icon="mdi-file-document-plus" size="large" />
                                                            <p>点击或将文件拖拽至此处以上传资源</p>
                                                        </div>
                                                        <!-- 已上传 -->
                                                        <div v-if="!isSending && newResForm.fileUrl != ''">
                                                            <v-icon icon="mdi-file-document-check" size="large" />
                                                            <p>{{ newResForm.fileUrl }}</p>
                                                        </div>
                                                        <!-- 正在提交 -->
                                                        <div v-if="isSending">
                                                            <v-icon icon="mdi-file-document-refresh" size="large" />
                                                            <p>正在提交...</p>
                                                        </div>
                                                    </div>
                                                </Upload>
                                            </div>
                                        </div>

                                        <div class="form-row">
                                            <div :class="['form-title', { 'red': newResForm.channel.length === 0 }]"
                                                style="margin-top: 13px;">
                                                <p>资源频道</p>
                                            </div>
                                            <div class="form-content">
                                                <v-chip-group v-model="newResForm.channel" column mandatory
                                                    :disabled="isSending" title="选择资源所属的频道">
                                                    <v-chip variant="tonal" filter v-for="t in fullChannelGroup">{{ t
                                                    }}</v-chip>
                                                </v-chip-group>
                                            </div>
                                        </div>

                                        <div class="form-row">
                                            <div :class="['form-title', { 'red': newResForm.type.length === 0 }]"
                                                style="margin-top: 13px;">
                                                <p>资源类型</p>
                                            </div>
                                            <div class="form-content">
                                                <v-chip-group v-model="newResForm.type" column multiple mandatory
                                                    :disabled="isSending" title="选择资源中包含的文件类型">
                                                    <v-chip variant="flat" filter v-for="t in fullTypeGroup">{{ t
                                                    }}</v-chip>
                                                </v-chip-group>
                                            </div>
                                        </div>

                                        <div class="form-row">
                                            <div :class="['form-title', { 'red': newResForm.imgUrl === '' }]">
                                                <p>描述图片</p>
                                            </div>
                                            <div class="form-content">
                                                <Upload ref="imgUpload" type="drag" action="../api/resource/uploadImage"
                                                    :disabled="isSending && imgSending" accept="image/*"
                                                    :format="['jpg', 'jpeg', 'png', 'webp']" :max-size="5120"
                                                    :on-success="successPhoto" :on-exceeded-size="exceedPhoto"
                                                    :on-error="errorPhoto" :on-format-error="isNotPhoto"
                                                    :on-progress="fileOrPhotoSending" :on-remove="closePhoto"
                                                    :before-upload="beforePhoto" :headers="{
                                                        'token': getToken(),
                                                        'Accept': 'application/json, text/plain, */*'
                                                    }" title="上传一张图片以描述资源文件">
                                                    <div style="padding: 10px">
                                                        <!-- 未上传 -->
                                                        <div v-if="!isSending && newResForm.imgUrl == ''">
                                                            <v-icon icon="mdi-image-plus" size="large" />
                                                            <p>点击或将图片拖拽至此处以上传资源描述图片</p>
                                                        </div>
                                                        <!-- 已上传 -->
                                                        <div v-if="!isSending && newResForm.imgUrl != ''">
                                                            <!-- <v-icon icon="mdi-image-check" size="large"/>
                                                        <p>{{ newResForm.imgUrl }}</p> -->
                                                            <Image :src="newResForm.imgUrl" style="height: 100px;"
                                                                contain />
                                                        </div>
                                                        <!-- 正在提交 -->
                                                        <div v-if="isSending">
                                                            <v-icon icon="mdi-image-refresh" size="large" />
                                                            <p>正在提交...</p>
                                                        </div>
                                                    </div>
                                                </Upload>
                                            </div>
                                        </div>

                                        <div class="form-row">
                                            <div :class="['form-title', { 'red': newResForm.description == '' }]">
                                                <p>资源描述</p>
                                            </div>
                                            <div class="form-content">
                                                <Input v-model="newResForm.description" :disabled="isSending"
                                                    type="textarea" :autosize="{ minRows: 2, maxRows: 5 }"
                                                    placeholder="请输入资源描述..." show-word-limit maxlength="500"
                                                    title="输入文字以对资源进行描述" />
                                            </div>
                                        </div>

                                    </div>
                                    <!-- 按钮部分 -->
                                    <div
                                        style="display: flex; justify-content: flex-end; width: 100%; margin-top: 20px;">
                                        <!-- <v-btn color="orange" variant="tonal" @click="test1" style="width: 100px; margin-right: 15px;" :disabled="isSending">测试逻辑</v-btn> -->
                                        <v-btn color="red" variant="tonal" @click="newResModal = false"
                                            style="width: 100px; margin-right: 15px;" :disabled="isSending">取消</v-btn>
                                        <v-btn color="green" variant="flat" @click="trySend" width="100px"
                                            :loading="isSending">发布</v-btn>
                                    </div>
                                </div>
                            </div>
                        </v-dialog>
                    </div>
                    <!-- 小翻页器 -->
                    <div style="display: flex; justify-content: center; align-items: center;"
                        class="side-panel right-panel side-panel-custom side-expand shadow-act glass">
                        <Page :total="entryCnt" simple v-model:model-value="pageNow" :page-size="entryInPage"
                            :disabled="isLoading" />
                    </div>
                </div>
            </Affix>
        </div>
        <v-dialog v-model="showDelete" :persistent="isDeleting" class="overlay">
            <div class="dialog-container">
                <div class="dialog">
                    <!-- 标题 -->
                    <div class="title-dark">是否确认删除资源？</div>
                    <div class="text-large-dark" style="margin-top: 8px;">
                        您正在删除资源：<span style="font-weight: bolder;">{{ deleteTitle }}</span>。是否继续？
                    </div>
                    <div style="display: flex; flex-direction: row; justify-content: end; margin-top: 15px;">
                        <v-btn variant="tonal" :disabled="isDeleting" style="margin-right: 5px;" title="取消"
                            @click.stop="showDelete = false">取消</v-btn>
                        <v-btn variant="flat" :loading="isDeleting" color="red" style="margin-left: 5px;"
                            @click.stop="tryDelete" title="删除资源">确认删除</v-btn>
                    </div>
                </div>
            </div>
        </v-dialog>
    </div>
</template>







<!-- 修改为 script setup 的形式 -->
<script setup>
import { ref, watch, defineComponent, onMounted } from 'vue'
import { post, get, delet, put } from "@/net"
import { Snackbar } from '@varlet/ui'
import '@/assets/pageLayout.css'
import '@/assets/texts.css'
import '@/assets/newDialog.css'
import '@/assets/panel.css'
import '@/assets/action.css'
import router from "@/router"
import ResSummaryEntry from './resources/ResSummaryEntry.vue';

defineComponent({
    components: {
        ResSummaryEntry
    },
})

const props = defineProps({
    page: Number
})

const isLoading = ref(false)
const isSending = ref(false)

const isAdmin = ref(false)

const newResModal = ref(false)

// 翻页器
const entryInPage = ref(10)
const totalPage = ref(1)

const pageNow = ref(props.page)
console.log(pageNow.value)
const entryCnt = ref(10)
// 显示条目
const resDatas = ref([])

const channelAlert = ref(false)
const typeAlert = ref(false)

// resDatas.value = [
//     {id: 1, resId: 100, title:'标题测试' + pageNow, avatarUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/ava1.webp', userNameTime: '张神@10086114·', channel: '理学工学', type: ['文档', '视频', '图片'], canDelete: true, canDownload: false, text:'这是一段内'},
//     {id: 2, resId: 100, title:'标题测试1：' + pageNow, avatarUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/ava1.webp', userNameTime: '张神@10086114·', channel: '理学工学', type: ['文档', '视频', '图片'], canDelete: true, canDownload: false, text:'这是一段内'},
//     {id: 3, resId: 143, title:'标题测试2 page :' + pageNow.value, avatarUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/ava0.webp', userNameTime: '张神@10086114·', channel: '理学工学', type: ['文档', '视频', '图片'],  canDelete: false, canDownload: false, text:'这是一段内容'},
//     {id: 4, resId: 125, title:'标题测试3', avatarUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/ava2.webp', userNameTime: '张神@10086114·', channel: '心理学', type: ['文档', '视频', '图片'],  canDelete: true, canDownload: true, text:'这是一段内容'},
//     {id: 5, resId: 157, title:'标题测试4', avatarUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/ava3.webp', userNameTime: '张神@10086114·', channel: '文史哲法', type: ['文档', '视频', '图片'],  canDelete: false, canDownload: false, text:'这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。'},
//     {id: 6, resId: 13, title:'标题测试5', avatarUrl: '', userNameTime: '张神@10086114·', channel: '理学工学', type: ['文档', '图片'],  canDelete: false, canDownload: true, text:'这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。'},
//     {id: 7, resId: 123, title:'标题测试6', avatarUrl: '', userNameTime: '张神@10086114·', channel: '外语', type: ['文档', '视频', '图片'],  canDelete: false, canDownload: false, text:'这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。这是一段内容文本示例。'},
// ];

const changePage = (newPageNumber) => {
    router.push('/resources/' + newPageNumber)
}

// 搜索栏
const searchText = ref('')
// TODO: 添加搜索按钮逻辑


// 侧边选择器
const isFix = ref(false)
const changeFix = (status) => {
    isFix.value = status
    console.log(status)
}

// 频道选择器
// const channelIndeterminate = ref(false)
// const channelAll = ref(true)
const fullChannelGroup = ['理学工学', '农学', '外语', '经济管理', '计算机', '音乐与艺术', '心理学', '文史哲法', '医学与保健', '教育教学']
// const channelGroup = ref(fullChannelGroup)
const channelGroup = ref([...Array(fullChannelGroup.length).keys()])
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
const setChannelAll = () => {
    channelGroup.value = [...Array(fullChannelGroup.length).keys()]
}
const setChannelEmpty = () => {
    channelGroup.value = []
}
// const handleChannelAll = () => {
//     if(channelIndeterminate.value) {
//         channelAll.value = false
//     }else {
//         channelAll.value = !channelAll.value
//     }
//     channelIndeterminate.value = false

//     if (channelAll.value) {
//         channelGroup.value = fullChannelGroup
//     } else {
//         channelGroup.value = []
//     }
// }
// const checkChannelGroupChange = (data) => {
//     if (data.length == fullChannelGroup.length) {
//         channelIndeterminate.value = false
//         channelAll.value = true
//     } else if (data.length == 0) {
//         channelIndeterminate.value = false
//         channelAll.value = false
//     } else {
//         channelIndeterminate.value = true
//         channelAll.value = false
//     }
// }

// 类型选择器
// const typeIndeterminate = ref(false)
// const typeAll = ref(true)
const fullTypeGroup = ['文档', '源代码', '视频', '图片', '音频', '其他']
const typeMapping = {
    0: '文档',
    1: '源代码',
    2: "视频",
    3: "图片",
    4: "音频",
    5: "其他",
    // TODO
};
const typeGroup = ref([...Array(fullTypeGroup.length).keys()])
const setTypeAll = () => {
    typeGroup.value = [...Array(fullTypeGroup.length).keys()]
}
const setTypeEmpty = () => {
    typeGroup.value = []
}
function convertNumbersToTypes(numbers) {
    return numbers.map(number => typeMapping[number]);
}
// const typeGroup = ref(fullTypeGroup)
// const handleTypeAll = () => {
//     if(typeIndeterminate.value) {
//         typeAll.value = false
//     }else {
//         typeAll.value = !typeAll.value
//     }
//     typeIndeterminate.value = false

//     if (typeAll.value) {
//         typeGroup.value = fullTypeGroup
//     } else {
//         typeGroup.value = []
//     }
// }
// const checkTypeGroupChange = (data) => {
//     if (data.length == fullTypeGroup.length) {
//         typeIndeterminate.value = false
//         typeAll.value = true
//     } else if (data.length == 0) {
//         typeIndeterminate.value = false
//         typeAll.value = false
//     } else {
//         typeIndeterminate.value = true
//         typeAll.value = false
//     }
// }

// 新建资源表单
const newResForm = ref({
    title: '',
    description: '',
    channel: [],
    type: [],
    imgUrl: '',
    fileUrl: '',
    fileName: '',
    fileSize: 0,
})

// 新建资源警告
const alert = ref({
    show: false,
    title: '',
    message: '',
})

const updateData = async (pageNow) => {
    isLoading.value = true

    // 到时候要换搜索算法
    var res = await post("/resource/searchResource",
        {
            keywords: searchText.value,
            subjects: channelGroup.value,
            categories: typeGroup.value,
            cntInPage: entryInPage.value,
            pageNum: pageNow
        },
        //成功后调用
        (message) => {
            console.log(message)

            resDatas.value = message.list.map((item, index) => ({
                id: index,
                resId: item.id,
                title: item.title,
                avatarUrl: item.profilePhotoUrl,
                userNameTime: item.userName + "@" + item.userId,
                channel: channelMapping[item.subject],
                type: convertNumbersToTypes(item.categories),
                canDelete: item.canDelete,
                text: item.content
            }));

            entryCnt.value = message.count
            totalPage.value = message.pageCount
        },
        //失败后调用
        (message) => {
            console.log(message)
        }

    )
    console.log("发送的参数:", {
        keywords: searchText.value,
        subjects: channelGroup.value,
        categories: typeGroup.value,
        cntInPage: entryInPage.value,
        pageNum: pageNow
    });


    isLoading.value = false
    return res
}


// 切换页面和初始化时执行的代码
watch(pageNow, async (pageNow) => {
    // 当页码改变时，获取并显示新的页面内容
    console.log('page changed : ' + pageNow + '. Getting data')

    await updateData(pageNow)
}, { immediate: true })


// 尝试发送新建资源请求
const imgUpload = ref(null)
const fileUpload = ref(null)
const sendError = (msg, status) => {
    console.log('资源发布失败')
    showAlert(true, 'error', '资源发布时出错', '返回状态码：' + status + '\n错误消息：' + msg)

    if (msg == '由于您过长时间未操作，需要重新上传资源') {
        imgUpload.value.clearFiles()
        fileUpload.value.clearFiles()
        newResForm.value.fileUrl = ''
        newResForm.value.imgUrl = ''
    }
}

const trySend = async () => {
    showAlert(false, '', '')

    if (
        newResForm.value.title == '' ||
        newResForm.value.fileUrl == '' ||
        newResForm.value.channel.length == 0 ||
        newResForm.value.type.length == 0 ||
        newResForm.value.imgUrl == '' ||
        newResForm.value.description == ''
    ) {
        showAlert(true, 'error', '必填项缺失', '该表单中所有项均必填，请检查是否有未填的部分，这部分的标题将以红色显示')
        return
    }

    isSending.value = true
    await post("/resource/uploadResource",
        {
            title: newResForm.value.title,
            subject: newResForm.value.channel,
            categories: newResForm.value.type,
            content: newResForm.value.description,
            imagePath: newResForm.value.imgUrl,
            filePath: newResForm.value.fileUrl,
            fileName: newResForm.value.fileName,
            size: newResForm.value.fileSize,
        },
        //成功后调用
        (message) => {
            console.log(message)
            newResForm.value = {
                title: '',
                description: '',
                channel: [],
                type: [],
                imgUrl: '',
                fileUrl: '',
                fileName: '',
                fileSize: 0,
            }
            newResModal.value = false
            updateData(pageNow.value)
            Snackbar.success('新资源发布成功')
        }, sendError
    )
    isSending.value = false;

}
const showAlert = (show, type, title, message) => {
    alert.value.show = show
    alert.value.type = type
    alert.value.title = title
    alert.value.message = message
}

const fileSending = ref(false)
const imgSending = ref(false)


const isNotPhoto = (file, fileList) => {
    showAlert(true, 'error', '图片格式错误', '您所选择文件格式不受支持，请上传图片')
    console.log(file)
}
const exceedFile = () => {
    showAlert(true, 'error', '资源文件大小超出限制', '资源文件最大为 500M ，请重新上传')
}
const exceedPhoto = () => {
    showAlert(true, 'error', '描述图片大小超出限制', '描述图片最大为 5M ，请重新上传')
}
const successFile = (response, file, fileList) => {
    fileSending.value = false
    newResForm.value.fileUrl = response.message
    newResForm.value.fileName = file.name
    newResForm.value.fileSize = file.size
    showAlert(true, 'success', '资源上传成功', '资源文件名：' + file.name + '\n资源大小：' + file.size + '字节')
}
const successPhoto = (response, file, fileList) => {
    imgSending.value = false
    newResForm.value.imgUrl = response.message
    showAlert(true, 'success', '描述图片上传成功', '描述图片文件名：' + file.name)
}
const errorFile = (error, file, fileList) => {
    fileSending.value = false
    showAlert(true, 'error', '资源上传失败', '错误码：' + error.status + '\n错误类型：' + error.message)
}
const errorPhoto = (error, file, fileList) => {
    imgSending.value = false
    showAlert(true, 'error', '描述图片上传失败', '错误码：' + error.status + '\n错误类型：' + error.message)
}
const fileOrPhotoSending = (event, file, fileList) => {
    showAlert(true, 'info', '正在上传', '正在上传：' + file.name)
}
const closeFile = (file, fileList) => {
    //TODO
    newResForm.value.fileName = ''
    newResForm.value.fileUrl = ''
    newResForm.value.fileSize = 0
    showAlert(true, 'info', '文件资源已取消', '已经取消资源文件，请重新上传')
}
const closePhoto = (file, fileList) => {
    newResForm.value.imgUrl = ''
    showAlert(true, 'info', '文件资源已取消', '已经取消资源描述图片，请重新上传')
}
const getToken = () => {
    return localStorage.getItem('token')
}
const beforeFile = (file) => {
    if (newResForm.value.fileUrl != '') {
        showAlert(true, 'error', '已上传资源文件', '仅接受一个资源文件，目前已经上传，请不要重复上传')
        return false
    }
    fileSending.value = true
    return true
}
const beforePhoto = (file) => {
    if (newResForm.value.imgUrl != '') {
        showAlert(true, 'error', '已上传描述图片', '仅接受一张描述图片，目前已经上传，请不要重复上传')
        return false
    }
    imgSending.value = true
    return true
}


const test1 = () => {
    console.log(newResForm.value.title)
    console.log(newResForm.value.description)
    console.log(newResForm.value.channel)
    console.log(newResForm.value.type)
    console.log(newResForm.value.imgUrl)
    console.log(newResForm.value.fileUrl)
}

const test2 = () => {

}

const isDeleting = ref(false)
const showDelete = ref(false)
const deleteTitle = ref('')
const deleteId = ref(0)
const clickDelete = (resId, resTitle) => {
    deleteTitle.value = resTitle
    showDelete.value = true
    deleteId.value = resId
    isDeleting.value = false
}
const tryDelete = async () => {
    console.log("delete emit received : " + deleteId.value)

    isDeleting.value = true
    await delet("/resource/deleteResource",
        { resourceId: deleteId.value },
        //成功后调用
        (message) => {
            console.log(message)
            Snackbar.success('帖子删除成功')
            updateData(pageNow.value)
        },
    )
    isDeleting.value = false
    showDelete.value = false
}

const tryDetail = (resId) => {
    if (!(resId > 0)) return;
    router.push(`/resource/${resId}`);
    console.log("detail emit received : " + resId)
}

const trySearch = (value) => {
    console.log(value)
    var legal = true
    channelAlert.value = false
    typeAlert.value = false

    if (channelGroup.value.length == 0) {
        channelAlert.value = true
        legal = false
    }

    if (typeGroup.value.length == 0) {
        typeAlert.value = true
        legal = false
    }

    if (legal == false) {
        return
    }

    updateData(1)
    router.push('./1')
}
</script>

<style>
.search_shadow_large {
    box-shadow: 5px 5px 15px #3e3e3e;
}

.search_shadow_small {
    box-shadow: 2px 2px 5px #3e3e3e;
}

.bottom-page {
    border-radius: 50px;
    margin-top: 20px;
    margin-left: 50px;
    margin-right: 50px;
    padding: 10px;
    transition-property: box-shadow;
    transition-duration: 0.2s;
}

.skeleton-static:hover {
    padding-top: 15px;
    padding-bottom: 15px;
    padding-left: 20px;
    padding-right: 20px;
    margin-top: 10px;
    margin-bottom: 10px;
    margin-left: 10px;
    margin-right: 10px;
}


/* 用于添加背景图，为此必须先将padding 置零 */
.insert-img {
    padding: 0px;
    overflow: hidden;
}


.checkbox-container-2 {
    display: flex;
    flex-wrap: wrap;
}

.checkbox-container-2>* {
    flex: 1 1 34%;
}

.checkbox-container-3 {
    display: flex;
    flex-wrap: wrap;
}

.checkbox-container-3>* {
    flex: 1 1 26%;
}

.chip-entry {
    font-size: 13px;
    height: 28px;
    margin-top: -5px;
}

.red {
    color: red;
}
</style>    