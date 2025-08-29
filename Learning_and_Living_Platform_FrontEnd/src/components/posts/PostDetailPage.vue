<template>
<div class="page-container-2 " >
    <div class="left-column-2">
      <Affix :offset-top="20" style="margin-top: 20px; margin-bottom: 20px;" >
        <div class="side-panel-2 side-panel-custom-2 left-panel-2 side-expand shadow-act glass">
          <v-btn width="60px" height="30px" color="default"
          @click="router.push('/posts')"
          >返回</v-btn>
            <p style="margin-top: 20px;">您已阅读：</p>
            <Progress style="margin-bottom: 10px;"
            :percent="scrollPercentage" :stroke-color="['#FFD93D','#009FBD']" status="active"
              title="您已阅读："/>
            <Row>
              <Col span="9">
                  <NumberInfo  sub-title="帖子阅读量" >
                    <template #total>
                      <CountUp :end="postForm.read" :duration="4" />
                    </template>
                  </NumberInfo>
              </Col>
              <Col span="9">
                <NumberInfo  sub-title="帖子点赞量" >
                  <template #total>
                    <Numeral :value="postForm.like" />
                  </template>
                </NumberInfo>
              </Col>
              <Col span="6">
                  <v-btn
                    v-if="isLiked"
                    class="ma-2"
                    variant="text"
                    icon="mdi-thumb-up"
                    color="red-lighten-2"
                    :disabled=(likeDisable||postAuth.disabled)
                    @click="toggleLike"
                  ></v-btn>
                  <v-btn
                    v-else
                    class="ma-2"
                    variant="text"
                    icon="mdi-thumb-up"
                    color="grey"
                    :disabled=(likeDisable||postAuth.disabled)
                    @click="toggleLike"
                  ></v-btn>
                  <!-- <Button type="error" @click="toggleLike" style="margin-right: 10px;" shape="circle">
                  <Icon :type="isLiked ? 'md-heart' : 'md-heart-outline'" :color="isLiked ? '#F16643' : '#F16643'"
                    size="20" />
                </Button> -->
              </Col>
            </Row>

            <Divider dashed />

            <Carousel
                :autoplay="setting.autoplay"
                :autoplay-speed="setting.autoplaySpeed"
                :dots="setting.dots"
                :radius-dot="setting.radiusDot"
                :trigger="setting.trigger"
                :arrow="setting.arrow">
                <CarouselItem>
                    <div class="demo-carousel">
                      <Image src="https://learning-and-living.oss-cn-beijing.aliyuncs.com/image/74653061_p0_master1200.jpg" cover /></div>
                </CarouselItem>
                <CarouselItem>
                    <div class="demo-carousel"><Image src="https://learning-and-living.oss-cn-beijing.aliyuncs.com/image/93084292_p0_master1200.jpg" cover /></div>
                </CarouselItem>
            </Carousel>

          </div>
      </Affix>
    </div>
    <div class="right-column-2 content" style="margin-top: 20px;" @scroll="handleScroll">
      <!-- 帖子内容 -->
      <div 
      id="commentTop"
      class="side-panel-2 side-panel-custom-2 right-panel-2 shadow-act glass"
      >
      <div style="display: flex; flex-direction: row; justify-content: space-between;margin-top: 10px;">
        <p class="post-title title-dark">{{ postForm.title }}</p>
        <v-btn v-if="postAuth.canDelete"  color="red" @click="tryDelete()" style="margin-right: 5%;">删除</v-btn>
      </div>

      <div style="margin-top: 10px;">
        <v-row align="center" justify="space-between">
          <v-col cols="auto">
            <v-avatar size="large">
              <v-img 
              @click="jumpToUser(poster.id)"
              :src="poster.avatarUrl" 
              />
            </v-avatar>
          </v-col>
          <v-col>
            <v-list-item-title class="text-dark">{{ poster.name }}</v-list-item-title>
              <Tag v-if="poster.level>=0" :color="getLevelTagColor(poster.level)">{{ poster.userLevelName }}</Tag>
          </v-col>
        </v-row>
      </div>

      <Divider></Divider>
      <div style="margin: 20px 20px 20px 5px;overflow: hidden;">
        <text class="text-dark" v-html="postForm.content"></text>
      </div>
      <div style="margin-top: 15px;margin-bottom: 5px;text-align: right;">
        <p>权限等级：{{ postAuth.authority }}级</p>
        <p>发布于：{{ postForm.postTime }}</p>
      </div>
    </div>
      
      <!-- 评论列表 -->
      <div 
      class="side-panel-2 side-panel-custom-2 right-panel-2 side-expand shadow-act glass"
      v-for="comment in comments" :key="comment.commentTime"
      >
        <v-row align="center" justify="space-between">
            <v-col cols="auto">
              <v-avatar >
                <v-img 
                @click="jumpToUser(comment.userId)"
                :src="comment.avatarUrl" />
              </v-avatar>
            </v-col>
            <v-col>
              <v-list-item-title class="text-dark">{{ comment.name }}</v-list-item-title>
                <Tag v-if="comment.userLevel>=0" :color="getLevelTagColor(comment.userLevel)">{{ comment.userLevelName }}</Tag>
            </v-col>
          </v-row>
          <div style="margin-top: 20px;overflow: hidden;">
            <text class="text-dark" v-html="comment.content"></text>
          </div>
          <div style="margin-top: 15px;margin-bottom: 15px;float: right;">
            {{ comment.floor }}楼
            {{ comment.commentTime }}
          </div>
          <Divider></Divider>
          <div class="text-dark" v-if="comment.replys.length == 0">暂时没有回复哦~</div>
          <div v-else>
            <v-timeline  density="compact" align="start">
              <v-timeline-item v-for="reply in comment.replys" :key="reply.time" size="x-small">
                <template v-slot:icon>
                  <v-avatar 
                  @click="jumpToUser(reply.userId)"
                  :image=reply.avatarUrl size="small"></v-avatar>
                </template>
                <div>
                  <Tag v-if="reply.userLevel>=0" style="margin-right: 5px;" :color="getLevelTagColor(reply.userLevel)">{{ reply.userLevelName }}</Tag>
                  <text class="text-dark" style="font-weight: bold;">{{ reply.name }}</text>
                  <text style="margin-left: 10px;">@{{ reply.time }}</text>
                  <v-btn v-if="reply.canDelete" variant="text" size="small" class="text-dark"
                  @click="deleteReply(comment, reply.replyId)">删除</v-btn>
                </div>
                <div class="text-dark">{{ reply.content }}</div>
              </v-timeline-item>
            </v-timeline>
          <div style="text-align: right">
            <v-btn class="text-dark" v-if="comment.isMoreReply" variant="text" @click="closeMoreReply(comment)">
              收起
            </v-btn>
            <v-btn class="text-dark" v-else variant="text" @click="getmoreReply(comment)">
              更多
            </v-btn>
          </div>
        </div>
          <div v-if=comment.replying style="text-align: right;margin-top: 20px;">
            <v-text-field v-model="comment.newReply"></v-text-field>
            <v-btn @click="confirmReply(comment)">提交</v-btn>
            <v-btn style="margin-left: 5px;"
            @click="cancelReply(comment)">取消</v-btn>
          </div>
          <div v-else style="text-align: right;margin-top: 20px;">
            <v-btn v-if="comment.canDelete" @click="tryDeleteComment(comment.id)" color="red">删除</v-btn>
            <v-btn :disabled="postAuth.disabled" style="margin-left: 20px;" @click="makeReply(comment)">回复</v-btn>
          </div>
      </div>

      <!-- 翻页器 -->
      <div class="side-panel-2 side-panel-custom-2 right-panel-2 shadow-act glass">
        <v-pagination
          class="text-dark"
          v-model="commentForm.curpage"
          :length="commentForm.pageCount"
          size="small"
          :total-visible="8"
          @update:modelValue="changeCommentPage()"
        ></v-pagination>
      </div>

      <!-- 发帖部分 -->
      <div v-if="!postAuth.disabled" class="side-panel-2 side-panel-custom-2 right-panel-2 shadow-act glass">
        <div>
          <div>
            <vue3-tinymce v-model="state.content" :setting="state.setting" />
          </div>
          <div style="text-align: center;margin-top: 20px;">
            <v-btn @click = "makeComment">
              提交
            </v-btn>
            <v-btn @click="makeClear"
            style="margin-left: 10px;">
              清空
            </v-btn>
          </div>
        </div>
      </div>
    </div>
    <Modal
        v-model=commentModal
        title="您确定要发表评论吗">
        <p>请记得遵守社区礼仪哦~</p>
        <template #footer>
            <Button type="primary" @click="confirmComment" ><p style="color: white;">确定</p></Button>
            <Button @click="cancelComment" style="margin-left: 10px;">取消</Button>
        </template>
    </Modal>
    <Modal 
      v-model=deleteCommentModal
      title="您确定要删除这条评论吗">
      <p>被删除的评论将无法回复~</p>
      <template #footer>
          <Button type="error" @click="confirmDeleteComment" ><p style="color: white;">确定</p></Button>
          <Button @click="cancelDeleteComment" style="margin-left: 10px;">取消</Button>
      </template>
    </Modal>
    <Modal
      v-model=deletePostModal
      title="您确定要删除这个帖子吗">
      <p>被删除的帖子将无法恢复哦~</p>
      <template #footer>
          <Button type="error" @click="confirmDeletePost" ><p style="color: white;">确定</p></Button>
          <Button @click="cancelDeletePost" style="margin-left: 10px;">取消</Button>
      </template>
    </Modal>
</div>
</template>
  
<script setup>
import '@/assets/pageLayout2.css'
import '@/assets/panel2.css'
import '@/assets/action.css'
import '@/assets/texts.css'
import 'highlight.js/styles/atom-one-dark.css';
import { ref, reactive } from 'vue';
import { Snackbar } from '@varlet/ui';
import { post, delet } from "@/net"
import router from "@/router"
import Vue3Tinymce from '@jsdawn/vue3-tinymce';

const props = defineProps({
    postId: Number
})

const state = reactive({
  content: '',
  setting: {
    height: 300,
    menubar: false,
    toolbar:
      'bold italic underline h1 h2 blockquote codesample numlist bullist link image | removeformat fullscreen',
    plugins: 'codesample link image table lists fullscreen',
    toolbar_mode: 'sliding',
    nonbreaking_force_tab: true,
    link_title: false,
    link_default_target: '_blank',
    content_style: 'body{font-size: 16px}',
    // 以中文简体为例
    language: 'zh-Hans',
    language_url:
      'https://unpkg.com/@jsdawn/vue3-tinymce@2.0.2/dist/tinymce/langs/zh-Hans.js',
    custom_images_upload: true,
    images_upload_url: "../api/post/uploadImage",
    custom_images_upload_callback: (res) =>
    {
      newCommentForm.imageUrl.push(res.message)
      return res.message
    },
    custom_images_upload_header: { 'token': localStorage.getItem('token') }
  }
});

//走马灯
const setting = reactive({
  autoplay : true,
  autoplaySpeed : 2000,
  dots : 'inside',
  radiusDot : false,
  trigger : 'click',
  arrow : 'never',
})

//页面百分比
const scrollPercentage = ref(0);

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

function handleScroll() {
  const content = document.querySelector('.content');
  const scrollHeight = content.scrollHeight;
  const scrollTop = content.scrollTop;
  const clientHeight = content.clientHeight;

  // 计算已读百分比
  const percentage = (scrollTop / (scrollHeight - clientHeight)) * 100;
  scrollPercentage.value = Math.round(percentage);
};

//删帖
const deletePostModal = ref(false)

const tryDelete = () => {
  deletePostModal.value = true
}

function confirmDeletePost(){
    delet("/post/postDelete",
        {
            postId: postForm.id,
            pageNum: 1
        },
        //成功后调用
            () => {
              deletePostModal.value = false
              router.push('/posts')
            },
        //失败后调用
            () => {
              Snackbar.error("删除失败!")
            }
    )
}

function cancelDeletePost(){
  deletePostModal.value = false
}

function jumpToUser(userId) {
  // if (userId > 0) {
  //   window.open('/user/' + userId, '_blank');
  // }
  if (!(userId > 0)) return
  router.push(`/user/${userId}`)

}

//post

const postForm = reactive({
    id: props.postId,
    title: '默认标题',
    content: '<p style="font-weight: bold;">无内容</p></p>',
    like: 0,
    read: 0,
    postTime: '1998-03-03',
})

const postAuth = reactive({
  authority: 0,
  disabled: false,
  canDelete: false, 
})


const poster = reactive({
  id: 10000000,
  avatarUrl: "https://zzq-typora-picgo.oss-cn-beijing.aliyuncs.com/Eric-cartman.webp",
  name: 'Cartman',
  level: '99999',
  userLevelName: '至尊VIP'
})

function getPostDetail(pid) {
    post('/post/postDetail',
        { postId: pid },
      (message) => {
        console.log(message)
        postForm.content = message.content
        postForm.like = message.likeCount
        postForm.postTime = message.postTime
        postForm.read = message.browseCount
        postForm.title = message.title

        postAuth.authority = message.authority
        postAuth.disabled = !message.canOperate
        postAuth.canDelete = message.canDelete

        isLiked.value = message.like

        poster.id = message.userId
        poster.avatarUrl = message.profilePhotoUrl
        poster.name = message.userName
        poster.level = message.userLevel
        poster.userLevelName = message.userLevelName

        commentForm.pagesize = message.commentList.pagesize
        commentForm.pageCount = message.commentList.pageCount
        setComments(message.commentList.list)
        },
        (message) => {
            Snackbar.error(message)
        }
    )
}

//comment
const commentModal = ref(false)

//存放comments
const comments = reactive([
      
])
//存放分页信息
const commentForm = reactive({
  pageSize: 10,
  curpage: 1,
  pageCount: 1
})

//存放新comment信息
const newCommentForm = reactive({
  postId: postForm.id,
  imageUrl: [],
  content: '',
})

//First Layer
function makeComment() {
  newCommentForm.content = state.content
  if (state.content == '') {
    Snackbar.warning("输入内容不能为空!")
  } else {newCommentForm
    commentModal.value = true
  }
}

function makeClear() {
  state.content = ''
  Snackbar.success("成功清空")
}

//Second Layer
function confirmComment() {
  state.content = ''
  commentPost()
  commentModal.value = false
}

function cancelComment() {
  commentModal.value = false
}

function changeCommentPage() {
  listComment(commentForm.curpage)
}

//Net Layer
function commentPost() {
    post(
        "/post/postComment",
        newCommentForm,
    //成功
      () => {
        newCommentForm.content = ''
        newCommentForm.imageUrl = []
        getPostDetail(postForm.id)
        },
    //失败后调用
        (mes) => {
          Snackbar.error(mes)
        }
    )
}

function listComment(toPage) {
  console.log({
      postId: postForm.id,
      pageNum: toPage,
      cntInPage: commentForm.pageSize
    })
    post(
        "/post/commentList",
        {
            postId: postForm.id,
            pageNum: toPage,
            cntInPage: commentForm.pageSize
        },
    //成功
      (message) => {
        console.log('From listComment:')
        console.log(message)
        commentForm.pagesize = message.pagesize
        commentForm.pageCount = message.pageCount
        setComments(message.list)
        let top = document.getElementById('commentTop')
        top.scrollIntoView({ behavior: 'smooth' })
      },
    //失败后调用
      (message) => {
        //console.log("你不好")
        console.log(message)
        }
    )
}

const deleteCommentModal = ref(false)
const tempDeleteComment = ref(-1)

function tryDeleteComment(comId) {
  deleteCommentModal.value = true
  tempDeleteComment.value = comId
}

function confirmDeleteComment() {
  deleteComment()
}

function cancelDeleteComment() {
  deleteCommentModal.value = false
}

function deleteComment() {
  delet("/post/commentDelete",
    {
        commentId: tempDeleteComment.value,
    },
    //成功后调用
    () => {
      deleteCommentModal.value = false
      tempDeleteComment.value = -1
      changeCommentPage()
    },
    //失败后调用
    () => {
      Snackbar.success('删除失败，请稍后再试')
    }
    )
}

//Fourth Layer
function setComments(coms) {
  comments.length = 0
  console.log('From setComments:')
  console.log(coms)
  for (var i = 0; i < coms.length; i++){
    comments.push({
      id: coms[i].id,
      name: coms[i].userName,
      avatarUrl: coms[i].profilePhotoUrl,
      userId: coms[i].userId,
      userLevel: coms[i].userLevel,
      userLevelName: coms[i].userLevelName,
      commentTime: coms[i].publishTime,
      content: coms[i].content,
      replys: [],
      canDelete: coms[i].canDelete,
      floor: coms[i].floor,
      isMoreReply: false,
      replying: false, 
      newReply: '', 
    })
  }
  for (var j = 0; j < comments.length; j++){
    listReply(comments[j], 3)
  }
}

//reply
function makeReply(comment) {
  comment.replying = true
}

function replyComment(comment) {
    post(
        "/post/commentReply",
        {
        postId: postForm.id,
        commentId: comment.id,
        content: comment.newReply
      },
    //成功
        (message) => {
          listReply(comment, 10)
        },
    //失败后调用
        (message) => {
        console.log(message)
        }
    )
}

function confirmReply(comment) {
  if (!comment.newReply) {
    Snackbar.warning("回复内容不能为空!")
  } else {
    replyComment(comment)
    comment.replying = false
    comment.newReply = ''
  }
}

function cancelReply(comment) {
  comment.replying = false
}

function getmoreReply(comment) {
  comment.isMoreReply = true
  listReply(comment, 10)
}

function closeMoreReply(comment) {
  comment.isMoreReply = false
  listReply(comment, 3)
}

function listReply(comment, cnt) {
    post(
        "/post/replyList",
        {
            commentId: comment.id,
            cntInPage: cnt
        },
    //成功
      (message) => {
        //console.log('From listReply:')
        //console.log(message)
        setReplys(message, comment)
        },
    //失败后调用
        (message) => {
        console.log(message)
        }
    )
}

function setReplys(reps, comment) {
  if (comment) {
    console.log("From setReplys")
    console.log(reps)
    comment.replys.length = 0
    for (var i = 0; i < reps.length; i++){
      comment.replys.push({
        replyId: reps[i].id,
        name: reps[i].userName,
        userId: reps[i].userId,
        userLevel: reps[i].userLevel,
        userLevelName: reps[i].userLevelName,
        avatarUrl : reps[i].profilePhotoUrl,
        content: reps[i].content,
        time: reps[i].publishTime,
        canDelete: reps[i].canDelete
      })
    }
    //console.log('From setReplys:')
    //console.log(comment.replys)
  }
}

function deleteReply(comment, replyId) {
  delet("/post/replyDelete",
      {
        replyId: replyId
      },
      //成功后调用
    () => {
            Snackbar.success("删除")
            listReply(comment, 3)
          },
      //失败后调用
          () => {
            Snackbar.error("删除失败，请稍后再试")
          }
  )
}

//like
const isLiked = ref(false)
const likeDisable = ref(false)

function toggleLike() {
  likeDisable.value = true
  post('/post/postLike',
    {
      postId: postForm.id,
      isLike: isLiked.value
    },
    () => {
      if (isLiked.value) {
        postForm.like--
      } else {
        postForm.like++
      }
      isLiked.value = !isLiked.value
      likeDisable.value = false
    },
    () => {
      Snackbar.error("点赞失败！")
      likeDisable.value = false
    }
  )
  
}

getPostDetail(postForm.id)
</script>
  
<style>
.post-title{
  font-size: 25px;
  font-weight: bold;
}

.content {
  height: 100vh;
  overflow-y: scroll;
}
</style>
  
  