<!-- 在该文件中使用了View Design和varlet组件 -->
<template>
  <div  class="page-container">

    <div class="left-column">
      <Affix :offset-top="20" style="margin-top: 20px; margin-bottom: 20px;">
        <!-- 推荐栏 -->
        <div class="side-panel left-panel top-panel side-panel-custom shadow-act glass insert-img">
          <v-img src="https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/108236103_p0.jpg" fit='contain'
            cover></v-img>
          <v-card-title>
            热门神贴🔥🔥🔥
          </v-card-title>
          <v-card-subtitle>
            QAQ关注洲洲喵，关注zzq谢谢喵！
          </v-card-subtitle>
          <v-card-actions>
            <v-spacer style="margin-left: 3%;">展开获取帖子列表</v-spacer>
            <v-btn variant="plain" style="color: #EFA3C8;" @click="showRecommended = !showRecommended">
              <v-img v-if="showRecommended" :width="20" :height="20" cover src="/icons8-up-64.png"></v-img>
              <v-img v-else :width="20" :height="20" cover src="/icons8-down-64.png"></v-img>
            </v-btn>
          </v-card-actions>
          <v-expand-transition>
            <div v-show="showRecommended">
              <v-divider></v-divider>
              <div class="custom-list">
                <router-link v-for="(item, i) in recommendDatas" :key="i" :to="`/post/${item.postId}`"
                  class="custom-list-item">
                  <Ellipsis :text="`${item.title}`" :length="16" tooltip class="custom-list-item-title" />
                </router-link>
              </div>
            </div>
          </v-expand-transition>
        </div>
          <!-- 小翻页器 -->
        <div style="display: flex; justify-content: center; align-items: center;" class="side-panel left-panel side-panel-custom side-expand shadow-act glass">
          <Page :total="entryCnt" simple v-model:model-value="pageNow" :page-size="entryInPage" :disabled="isLoading"/>
        </div>
      </Affix>
    </div>

    <div  class="centre-column" style="margin-bottom: 50px;">

      <!-- 帖子列表 -->
      <div v-if="!isLoading">
        <!-- key属性保证每个小组件有唯一确定的id，方便Vue进行就地更新与就地复用 -->
        <PostSummaryEntry v-for="data in postDatas" :key="data.id" :postEntryData="data" 
        @deleteClicked="tryDelete"
        @detailClicked="tryDetail" />
      </div>


      <!-- 骨架屏 -->
      <div v-if="isLoading">
        <Skeleton 
        loading 
        avatar 
        animated 
        class="centre-panel side-expand centre-panel-custom shadow-static skeleton-static glass"
        v-for="index in entryInPage" 
        :key="index"
          />
      </div>


      <!-- 大翻页器 -->
      <div style="display: flex; justify-content: center; align-items: center;" class="bottom-page shadow-act glass-light">
            <!-- <Page :total="entryCnt" v-model:model-value="pageNow" :page-size="entryInPage"/> -->
            <v-pagination
                v-model="pageNow"
                :length="totalPage"
                size="small"
                :total-visible="8"
                @update:modelValue="changePage"
                :disabled="isLoading"
            ></v-pagination>
        </div>

    </div>

    <div class="right-column">
      <Affix :offset-top="20" style="margin-top: 20px; margin-bottom: 20px;">
          <!-- 发帖功能 -->
          <div class="side-panel top-panel right-panel side-panel-custom shadow-act glass">  
          <v-row>
            <v-col>
              <v-btn block color="#FFC26F" @click="tryPost" class="glass">发 布 帖 子</v-btn>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="6">
              <v-btn block color="#F9E0BB" @click="sharePage" class="glass">分 享</v-btn>
            </v-col>
            <v-col cols="6">
              <v-btn block color="#F9E0BB" @click="refreshPage" class="glass">刷 新</v-btn>
            </v-col>
          </v-row>

        </div>
        <!-- 排序功能 -->
        <div class="side-panel right-panel side-panel-custom shadow-act side-expand glass">
          <p class="text-large-dark">请选择帖子排序的方式：</p>
          <v-radio-group
          
          style="margin-top: 15px;"
            v-model="indexMode"
            @update:modelValue="getPostList(pageNow)"
          >
            <v-radio
              label="按最新发帖时间排序"
              value="1"
            ></v-radio>
            <v-radio
              label="按最新回复时间排序"
              value="2"
            ></v-radio>
            <v-radio
              label="只看自己的帖子"
              value="3"
            ></v-radio>
          </v-radio-group>
        </div>
      </Affix>
    </div>

    <Modal 
      title="发表帖子" 
      v-model="PostModal"
      height="500"
      width="1000"
      placement="bottom"
      :mask-closable="false"    
      >
        <Input :border="false" v-model="newPost.title" maxlength="50" show-word-limit size="large" placeholder="帖子标题" />
        <Select v-model="newPost.authority"
        style="margin-top: 10px;margin-bottom: 10px;"
        placeholder="请选择帖子可见范围"
        >
          <Option v-if="curUserLevel >= 0" value="0">所有人可见内容</Option>
          <Option v-if="curUserLevel >= 1" value="1">一级及以上用户可见内容</Option>
          <Option v-if="curUserLevel >= 2" value="2">二级及以上用户可见内容</Option>
          <Option v-if="curUserLevel >= 3" value="3">三级及以上用户可见内容</Option>
          <Option v-if="curUserLevel >= 4" value="4">四级及以上用户可见内容</Option>
          <Option v-if="curUserLevel >= 5" value="5">五级及以上用户可见内容</Option>
          <Option v-if="curUserLevel >= 6" value="6">六级及以上用户可见内容</Option>
          <Option v-if="curUserLevel >= 7" value="7">七级及以上用户可见内容</Option>
          <Option v-if="curUserLevel >= 8" value="8">八级及以上用户可见内容</Option>
          <Option v-if="curUserLevel >= 9" value="9">九级及以上用户可见内容</Option>
          <Option v-if="curUserLevel >= 10" value="10">十级及以上用户可见内容</Option>
        </Select>
        <div>
          <div>
            <vue3-tinymce v-model="state.content" :setting="state.setting" />
          </div>
          <div style="text-align: center;margin-top: 20px;">
            <v-btn @click="makePost()">
              提交
            </v-btn>
            <v-btn @click="makeClear()"
            style="margin-left: 10px;">
              清空
            </v-btn>
          </div>
        </div>

      <template #footer>
        <p>发帖请遵守社区公约</p>
      </template>
    </Modal>

    <Modal
      v-model=confirmNewPostModal
      title="您确定要发表帖子吗">
      <p>请记得遵守社区礼仪哦~</p>
      <template #footer>
          <Button type="primary" @click="confirmNewPost" ><p style="color: white;">确定</p></Button>
          <Button @click="cancelNewPost" style="margin-left: 10px;">取消</Button>
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
import { ref, reactive, watch, defineComponent } from 'vue'
import '@/assets/pageLayout.css'
import '@/assets/texts.css'
import '@/assets/newDialog.css'
import '@/assets/panel.css'
import '@/assets/action.css'
import Vue3Tinymce from '@jsdawn/vue3-tinymce';
import PostSummaryEntry from './posts/PostSummaryEntry.vue';
import router from "@/router"
import { Snackbar } from '@varlet/ui';
import { post,get,delet } from '@/net'

const state = reactive({
  content: '',
  setting: {
    height: 300,
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
      newPost.images.push(res.message)
      return res.message
    },
    custom_images_upload_header: { 'token': localStorage.getItem('token') }
  }
});


defineComponent({
  components: {
    PostSummaryEntry
  },
})

const props = defineProps({
  page: Number
})

const isLoading = ref(false)
const showRecommended = ref(false)
const PostModal = ref(false);

//帖子
const newPost = reactive({
  title: '',
  content: '',
  authority: '0',
  images: []
})

//刷新网页
const refreshPage = () => {
  location.reload();
};

//分享
const sharePage = () => {
  // 检查浏览器是否支持分享功能
  if (navigator.share) {
    navigator.share({
      title: '分享的标题',
      text: '分享的文本内容',
      url: window.location.href,
    })
      .then(() => {
        Snackbar.success('分享成功')
        //console.log('分享成功');
      })
      .catch((error) => {
        Snackbar.success('分享失败:', error)
        //console.log('分享失败:', error);
      });
  } else {
    Snackbar.warning('浏览器不支持分享功能')
    //console.log('浏览器不支持分享功能');
  }
}


// 翻页器
const entryCnt = ref(10)
const entryInPage = ref(10)
const totalPage = ref(1)
const pageNow = ref(props.page)
const indexMode = ref("2")

// 显示条目
const postDatas = ref([])

const getPostList = async (pageNow) => {
    console.log("getPostList");
    isLoading.value = true
    // 到时候要换搜索算法
    post("/post/postList",
      {
          cntInPage: entryInPage.value,
          pageNum: pageNow,
          mode: parseInt(indexMode.value)
      },
        //成功后调用
      (message) => {
        //console.log("From getPostList")
        //console.log(message)

        postDatas.value = message.list.map((item, index) => ({
          id: index,   
          postId: item.id,   
          title: item.title, 
          avatarUrl: item.profilePhotoUrl,
          userNameId: item.userName + "@" + item.userId,
          access: item.authority,
          canDelete: item.canDelete,
          browse: item.browseCount,
          like: item.likeCount,
          hot: item.hotPoint
        }));
        console.log(postDatas)
        isLoading.value = false 
        entryCnt.value = message.count
        totalPage.value = message.pageCount
            
        }
    )
    
}

// 切换页面和初始化时执行的代码
watch(pageNow, async (pageNow) => {
    console.log('page changed : ' + pageNow + '. Getting data')

    await getPostList(pageNow)
}, { immediate: true })

const changePage = (newPageNumber) => {
  router.push('/posts/' + newPageNumber)
}

//推荐条目
const recommendDatas = ref([
])

function getrecommendPosts() {
  get('/post/getHotPost',
    (mes) => {
      // console.log(mes);
      // console.log("gethotpost");
      if (!mes || !Array.isArray(mes)) {
        console.log("No hot posts available or invalid response");
        return;
      }
      mes.slice(0, 5).forEach((item, i) => {
        recommendDatas.value.push({
          id: i,
          postId: item.id,
          title: item.title
        });
      });
    },
    (err, status) => {
      console.error("Failed to fetch hot posts:", err, status);
    }
  );
}

//删帖
const deletePostModal = ref(false)
const tempDeletePostId = ref(-1)

const tryDelete = (postId) => {
  tempDeletePostId.value = postId
  deletePostModal.value = true
}

function confirmDeletePost(){
    delet("/post/postDelete",
        {
            postId: tempDeletePostId.value,
            pageNum: pageNow.value
        },
        //成功后调用
            () => {
              getPostList(pageNow.value)
              tempDeletePostId.value = -1
              deletePostModal.value = false
            },
        //失败后调用
    )
}

function cancelDeletePost(){
  deletePostModal.value = false
}
  
const tryDetail = (postId) => {
  //onsole.log("detail emit received : " + postId);
  // window.open('../post/' + postId, '_blank');
  if (!(postId > 0)) return
  router.push(`/post/${postId}`)

}

//帖子发布逻辑
const confirmNewPostModal = ref(false)
const curUserLevel = ref(-1)

const tryPost = () => {
  get("/user/getLevel",
    (message) => {
      curUserLevel.value = message.userLevel
      PostModal.value = true;
      console.log(newPost)
    },)
}

//提交
const makePost = () => {
  //console.log('auth:'+newPost.authority)
  newPost.content = state.content
  if (newPost.content == '') {
    Snackbar.error("帖子内容不能为空!")
  } else if(newPost.title == ''){
    Snackbar.error("帖子标题不能为空!")
  } else if (newPost.authority < 0) {
    Snackbar.error("请选择帖子可见范围!")
  }
  else {
    confirmNewPostModal.value = true
  }
}

//清空
const makeClear = () => {
  state.content = ''
  Snackbar.success("成功清空")
}

const confirmNewPost = () => {
  post("/post/postUpload",
      newPost,
      //成功后调用
      () => {
        confirmNewPostModal.value = false
        newPost.title = ''
        newPost.content = ''
        newPost.authority = '0'
        newPost.images = []
        state.content = ''
        PostModal.value = false
        getPostList(pageNow.value)
      },
      //失败后调用
    (mes, status) => {
      confirmNewPostModal.value = false
      if (status == 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
      Snackbar.error(mes)
      }
  )
}

const cancelNewPost = () => {
  confirmNewPostModal.value = false
}

getrecommendPosts()
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
  padding-top: 5px;
}

.checkbox-container-3 {
  display: flex;
  flex-wrap: wrap;
}

.checkbox-container-3>* {
  flex: 1 1 26%;
  padding-top: 5px;
}

.custom-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.custom-list-item {
  display: flex;
  align-items: center;
  padding: 8px;
  border-bottom: 1px solid #ccc;
  text-decoration: none;
  color: #3A1078;
}

.custom-list-item-title {
  margin-left: 16px;
}
</style>