<template>
  <div class="post-page">
    <v-card class="post-title">
      <v-card-title>
        <h2>{{ title }}</h2>
      </v-card-title>
      <v-card-actions>
        <v-list-item class="w-100">
          <template v-slot:prepend>
            <v-avatar color="grey-darken-3">
              <img :src="avatarImage" alt="Avatar" class="avatar-image" />
            </v-avatar>
          </template>
          <v-list-item-title>{{ nickName }}</v-list-item-title>
          <v-list-item-subtitle>{{ userLevel }}</v-list-item-subtitle>
          <template v-slot:append>
            <div class="justify-self-end">
              <Button type="error" ghost @click="toggleLike" style="margin-right: 10px;">
                <Icon :type="isLiked ? 'md-heart' : 'md-heart-outline'" :color="isLiked ? '#F16643' : '#F16643'"
                  size="20" />
              </Button>
              <span class="subheading">{{ likeNumber }}</span>
            </div>
          </template>
        </v-list-item>
      </v-card-actions>
    </v-card>

    <v-card class="post-body">
      <v-card-text>
        <div>{{postDate}}</div>
        <div class="article">
          <!-- vue展示markdown内容,v-highlight自定义指令 -->
          <div v-highlight v-html="article" class="markdown-body" id="content"></div>
        </div>





      </v-card-text>
    </v-card>

    <div v-for="comment in comments">
      <v-card width="auto" class="post-comment">
        <template v-slot:subtitle>
          <v-row align="center">
            <v-col cols="auto">
              <v-avatar size="30">
                <img :src="avatarImage" alt="Avatar" class="avatar-image" />
              </v-avatar>
            </v-col>
            <v-col cols="auto">
              <v-card-item>
                <v-card-title>{{ comment.nickName }}</v-card-title>
                {{ comment.userLevel }}
              </v-card-item>
            </v-col>
          </v-row>
        </template>
        <v-card-text>
          <div style="font-size: 15px; padding-bottom: 10px;">
            {{ comment.content }}
          </div>
          <div v-if="comment.replys.length > 0">
            <Divider />
            <v-timeline density="compact" align="start">
              <v-timeline-item v-for="reply in comment.replys" :key="reply.time" :dot-color="reply.color" size="x-small">
                <div class="mb-4">
                  <div class="font-weight-normal">
                    <strong>{{ reply.from }}</strong> @{{ reply.time }}
                  </div>
                  <div>{{ reply.reply }}</div>
                </div>
              </v-timeline-item>
            </v-timeline>
          </div>
          <div v-else>
          </div>

          <div v-if="comment.replying">
            <div style="width: auto; margin: 10px;">
              <Input v-model="comment.newReply" size="large" placeholder="large size" />
              <WordCount :value="comment.newReply" :total="50" overflow>
                <template #prefix>已输入了</template>
                <template #prefix-overflow>已超出了</template>
                <template #suffix>个字</template>
                <template #suffix-overflow>个字</template>
              </WordCount>
            </div>
            <div class="button-right">
              <Button type="info" ghost :disabled="isDisabled(comment)" @click="confirmReply(comment)"
                style="margin: 5px;">确认</button>
              <Button type="error" ghost @click="cancelReply(comment)" style="margin: 5px;">取消</button>
            </div>
          </div>
          <div v-else>
            <div class="button-right">
              <Button type="success" ghost icon="ios-chatboxes-outline" @click="toggleReplyMode(comment)">回复</Button>
            </div>
          </div>
        </v-card-text>
      </v-card>
    </div>

    <div class="createComment">
      <Tinymce ref="editor" v-model="msg"></Tinymce>
    </div>

  </div>
</template>

<script>
import '@/assets/pageLayout.css';
import { marked } from 'marked';
import 'highlight.js/styles/atom-one-dark.css';
import Tinymce from "@/components/posts/Tinymce.vue";


export default {
  components: { Tinymce },
  data() {
    return {
      title: '张正奇是神',
      avatarImage: 'https://th.bing.com/th/id/OIP.eAKIb5uVZm2iG8Ge953cLwAAAA?pid=ImgDet&rs=1',
      nickName: '张正奇',
      userLevel: 'Lv 6',
      isLiked: false,
      likeNumber: 0,
      postDate: '2002 - 03 - 27',
      article:
        '![](https://ts1.cn.mm.bing.net/th/id/R-C.552c84175034ae84f437768c4544d774?rik=sMLpwK89ZhxB9w&riu=http%3a%2f%2fi3.sinaimg.cn%2fdy%2fo%2f2010-04-16%2f1271404410_goDP78.jpg&ehk=xDg6S5E0KyTH1TFvChR4lRbWx%2fhJJeJhhAakzxCi1u0%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1)\n' +
        '# 你好\n' +
        '```\n' +
        '<p>显示代码高亮</p>\n' +
        '<p>显示代码行号</p>\n' +
        '```',
      comments: [
        {
          nickName: '张正奇',
          userLevel: 'Lv 6',
          replying: false, // 控制回复按钮和输入框的切换
          newReply: '', // 输入框中的新回复内容
          content: '张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神。',
          replys: [
            {
              from: 'ZZQ',
              reply: `Sure, I'll see you later.`,
              time: '10:42am',
              color: 'deep-purple-lighten-1',
            },
            {
              from: 'John Doe',
              reply: 'Yeah, sure. Does 1:00pm work?',
              time: '10:37am',
              color: 'green',
            },
            {
              from: 'You',
              reply: 'Did you still want to grab lunch today?',
              time: '9:47am',
              color: 'deep-purple-lighten-1',
            },
          ],
        },
        {
          nickName: '张正奇',
          userLevel: 'Lv 6',
          replying: false, // 控制回复按钮和输入框的切换
          newReply: '', // 输入框中的新回复内容
          content: '张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神，张正奇是神。',
          replys: [
          ],
        }
      ],
      msg: 'Welcome to Use Tinymce Editor',
    };
  },
  created() {
  },
  methods: {
    toggleLike() {
      this.isLiked = !this.isLiked;
      if (this.isLiked) this.likeNumber++;
      else this.likeNumber--;
    },
    toggleReplyMode(comment) {
      comment.replying = !comment.replying; // 切换回复模式和输入框模式
      comment.newReply = ''; // 清空输入框内容
    },
    isDisabled(comment) {
      return comment.newReply.length > 50; // 超过字数限制时返回 true，禁用按钮
    },
    confirmReply(comment) {
      // 在确认按钮被点击时执行
      // 将输入框中的新回复内容添加到 comment 的 replys 数组
      const newReply = {
        from: 'New User',
        reply: comment.newReply,
        time: '12:00pm',
        color: 'blue',
      };
      comment.replys.push(newReply);

      comment.replying = false; // 切换回回复按钮模式
      comment.newReply = ''; // 清空输入框内容
    },
    cancelReply(comment) {
      comment.replying = false; // 取消回复，切换回回复按钮模式
      comment.newReply = ''; // 清空输入框内容
    },
    onClick(e, editor) {
      console.log('Element clicked')
      console.log(e)
      console.log(editor)
    },
  },
  mounted() {
    // 调用marked()方法，将markdown转换成html
    this.article = marked(this.article);
  },
}

</script>

<style>
.post-page {
  margin-top: 20px;
  margin-left: 10%;
  margin-right: 10%;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-title {
  margin-top: 10px;
  border-radius: 20px;
  box-shadow: 0px 0px 5px #000000;
}

.post-like {
  height: 25px;
  width: 25px;
  border-radius: 25px;
}

.post-body {
  margin-top: 20px;
  border-radius: 20px;
  box-shadow: 0px 0px 5px #000000;
}

.post-comment {
  margin-top: 20px;
  margin-bottom: 20px;
  border-radius: 20px;
  box-shadow: 0px 0px 5px #000000;
}

.article {
  width: auto;
  padding-top: 15px;
  margin-bottom: 5px;
  overflow: hidden;
}

.button-right {
  display: flex;
  justify-content: flex-end;
}

.createComment {
  min-height: 200px;
  margin-top: 20px;
  margin-bottom: 20px;
  border-radius: 20px;
  box-shadow: 0px 0px 5px #000000;
}

.markdown-body {
  
}

 /* markdown 行号的样式 */
#content li {
	list-style: decimal;
}

.article-con div pre {
	position: relative;
	padding: 0 29px;
	overflow: hidden;
	font-size: 90%;
	line-height: 1.9;
	background: #282c34;
}

.article-con div pre code {
	padding: 0.4em;
}

.article-con div .pre-numbering {
	position: absolute;
	top: 0;
	left: 0;
	width: 29px;
	padding: 9px 7px 12px 0;
	border-right: 1px solid #C3CCD0;
	background: #282c34;
	text-align: right;
	font-size: 16px;
	line-height: 1.45;
}

.pre-numbering li{
	list-style:none;
	font-family: Menlo, monospace;
	color: #abb2bf;
}
</style>

