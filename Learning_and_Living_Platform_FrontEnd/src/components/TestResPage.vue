<!-- 在该文件中使用了View Design和varlet组件 -->
<template>
<div style="margin-top: 200px;">
    <Button @click="uploadResource">上传资源</Button>
    <Button @click="getResDetail">获取资源详情</Button>
    <Button @click="updateRes">更新资源</Button>
    <Button @click="deleteRes">删除资源</Button>
    <Button @click="listRes">获取资源列表</Button>
</div>
<div>
    <Button @click="uploadPost">发帖</Button>
    <Button @click="getPostDetail">获取帖子详情</Button>
    <Button @click="listPost">获取帖子列表</Button>
    <Button @click="deletePost">删除帖子</Button>
    <Button @click="likePost">点赞</Button>
    <Button @click="commentPost">发表评论</Button>
    <Button @click="listComment">获取评论列表</Button>
    <Button @click="replyComment">回复评论</Button>
    <Button @click="listReply">获取回复列表</Button>
    <Button @click="deleteReply">删除回复</Button>
    <Button @click="deleteComment">删除评论</Button>
</div>
<div>
    <v-btn @click="resourceClassificationStatistics()">resourceClassificationStatistics</v-btn>
    <v-btn @click="resourceDownloadsByDays()">resourceDownloadsByDays</v-btn>
    <v-btn @click="resourceCountBySubject()">resourceCountBySubject</v-btn>
    <v-btn @click="resourceDownloadsBySubjectAndCategoryAndDate()">resourceDownloadsBySubjectAndCategoryAndDate</v-btn>
</div>
</template>
  

<script setup>
import { reactive } from 'vue'
import { post, get, delet, put } from "@/net"


//资源部分：
const uploadImageUrl = 'api/resource/uploadImage'
const uploadFileUrl = 'api/resource/uploadFile'
//获取到的链接在on_success里面调用

//统计数据
function resourceClassificationStatistics() {
    get('/statistic/resourceClassificationStatistics',
        (mes) => {
            console.log(mes)
        },
        (mes) => { 
            console.log(mes)
        }
    )
}

function resourceDownloadsByDays() {
    get('/statistic/resDownsAndUpsByDays',
        (mes) => {
            console.log(mes)
        },
        (mes) => { 
            console.log(mes)
        }
    )
}

function resourceCountBySubject() {
    get('/statistic/resourceCountBySubject',
        (mes) => {
            console.log(mes)
        },
        (mes) => { 
            console.log(mes)
        }
    )
}

function resourceDownloadsBySubjectAndCategoryAndDate() {
    get('/statistic/resourceDownloadsBySCD',
        (mes) => {
            console.log(mes)
        },
        (mes) => { 
            console.log(mes)
        }
    )
}

//上传资源

//传回后端的变量的key必须和这个变量的key保持一致，后同
const resData = reactive({
    title: '',
    subject: [],
    categories: [],
    content: '',
    imagePath: '',
    filePath: '',
    size: 0
})

function uploadResource() {
    post("/resource/uploadResource",
            resData,
        //成功后调用
            (message) => {
            console.log(message)
            },
        //失败后调用
            (message) => {
            console.log(message)
            }
    )
}

//获取资源详情
function getResDetail() {
    post('/resource/getResDetail',
        { resourceId: 5 },
        (message) => {
            console.log(message)
        },
        (message) => {
            console.log(message)
        }
    )
}

//更新资源（
const newResData = reactive({
    id: 23,
    title: 'sb',
    subject: 1,
    categories: [],
    content: ''
})

function updateRes() {
    put("/resource/updateResource",
         newResData,
        //成功后调用
            (message) => {
            console.log(message)
            },
        //失败后调用
            (message) => {
            console.log(message)
            }
    )
}

//删除资源
function deleteRes() {
    delet("/resource/deleteResource",
           {resourceId: 23},
        //成功后调用
            (message) => {
            console.log(message)
            },
        //失败后调用
            (message) => {
            console.log(message)
            }
    )
}

//获取资源列表
function listRes() {
    post("/resource/listResByClassWithPage",
        {
            subjects: [1],
            categories: [1, 2, 3, 4],
            cntInPage: 10,
            pageNum: 1
        },
        //成功后调用
        (message) => {
            console.log(message)
        },
        //失败后调用
        (message) => {
            console.log(message)
        }
    )
}

//帖子部分

//发帖
//传回后端的变量的key必须和这个变量的key保持一致，后同
const postForm = reactive({
    title: '默认标题',
    content: 'empty',
    authority: 0,
    images: []
})

function uploadPost() {
    post("/post/postUpload",
        postForm,
        //成功后调用
            (message) => {
            console.log(message)
            },
        //失败后调用
            (message) => {
            console.log(message)
            }
    )
}

function getPostDetail() {
    post('/post/postDetail',
        { postId: 7 },
        (message) => {
            console.log(message)
        },
        (message) => {
            console.log(message)
        }
    )
}

function listPost() {
    post("/post/postList",
        {
            cntInPage: 10,
            pageNum: 1
        },
        //成功后调用
        (message) => {
            //这里会返回一个id的数组，需要再一个一个调用
        console.log(message)
        },
        //失败后调用
        (message) => {
        console.log(message)
        }
    )
}

function deletePost() {
    delet("/post/postDelete",
        {
            postId: 12,
            pageNum: 1
        },
        //成功后调用
            (message) => {
            console.log(message)
            },
        //失败后调用
            (message) => {
            console.log(message)
            }
    )
}

function likePost() {
    post(
        "/post/postLike",
        {
            postId: 7,
            //1：已点赞 0：未点赞
            isLike: 0
        },
            (message) => {
            console.log(message)
            },
        //失败后调用
            (message) => {
            console.log(message)
            }
    )
}

//发帖
const commentForm = reactive({
    postId: 7,
    imageUrl: [],
    content: 'empty',
    pageNum: 1
})

function commentPost() {
    post(
        "/post/postComment",
        commentForm,
    //成功
        (message) => {
        console.log(message)
        },
    //失败后调用
        (message) => {
        console.log(message)
        }
    )
}

function listComment() {
    post(
        "/post/commentList",
        {
            postId: 7,
            pageNum: 1,
            cntInPage: 10
        },
    //成功
        (message) => {
        console.log(message)
        },
    //失败后调用
        (message) => {
        console.log(message)
        }
    )
}


const replyForm = reactive({
    postId: 7,
    commentId: 60,
    content: 'empty'
})

function replyComment() {
    post(
        "/post/commentReply",
        replyForm,
    //成功
        (message) => {
        console.log(message)
        },
    //失败后调用
        (message) => {
        console.log(message)
        }
    )
}

function listReply() {
    post(
        "/post/replyList",
        {
            commentId: 60,
            cntInPage: 10
        },
    //成功
        (message) => {
        console.log(message)
        },
    //失败后调用
        (message) => {
        console.log(message)
        }
    )
}

function deleteReply() {
    delet("/post/replyDelete",
        {
            replyId: 12,
        },
        //成功后调用
            (message) => {
            console.log(message)
            },
        //失败后调用
            (message) => {
            console.log(message)
            }
    )
}

function deleteComment() {
    delet("/post/commentDelete",
        {
            commentId: 11,
        },
        //成功后调用
            (message) => {
            console.log(message)
            },
        //失败后调用
            (message) => {
            console.log(message)
            }
    )
}

</script>
  
<style>

</style>