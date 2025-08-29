<template>
  <div>
    <ImagePreview v-model="showImagePreview" :preview-list="[resData.imgUrl]" />
    <div class="page-container-3" style="margin-top: 50px">
      <!-- 左边栏（主要栏） -->
      <div class="left-column-3">
        <!-- 资源标题 -->
        <div
          class="left-panel-3 side-panel-3 panel-padding-3 glass shadow-act"
          style="padding-top: 10px; padding-bottom: 10px"
          title="资源标题"
        >
          <h1 v-if="!isEdting">{{ resData.title }}</h1>
          <v-alert
            v-if="isEdting && showTitleAlert"
            variant="tonal"
            type="error"
            title="资源标题为空"
            text="请输入资源标题"
            style="padding-top: 12px; padding-bottom: 12px; margin-bottom: 10px"
          />
          <v-text-field
            v-if="isEdting"
            :loading="isSending"
            :disabled="isSending"
            v-model="newData.title"
            label="资源标题"
            variant="underlined"
            hint="请输入新资源标题"
          ></v-text-field>
        </div>

        <!-- 描述图片 -->
        <div
          class="left-panel-3 side-panel-3 panel-padding-3 glass shadow-act side-expand clickable"
          style="display: flex; justify-content: center; overflow: hidden"
          @click="showImagePreview = true"
          title="点击以显示完整图片"
        >
          <Image :src="resData.imgUrl" fit="contain" style="max-height: 60vh; min-height: 30px">
            <template #error>
              <div
                style="
                  min-width: 100px;
                  display: flex;
                  justify-content: center;
                  flex-direction: column;
                  text-align: center;
                "
              >
                <v-icon
                  icon="mdi-image-broken-variant"
                  size="large"
                  style="width: 100%; margin-top: 5px"
                />
                <p width="90%">图片显示失败</p>
              </div>
            </template>
          </Image>
        </div>

        <!-- 描述文字 -->
        <div class="left-panel-3 side-panel-3 panel-padding-3 glass shadow-act">
          <p v-if="!isEdting" style="white-space: pre-line" class="text-large-dark">
            {{ resData.description }}
          </p>

          <v-alert
            v-if="isEdting && showTextAlert"
            variant="tonal"
            type="error"
            title="资源描述为空"
            text="请输入资源描述"
            style="padding-top: 12px; padding-bottom: 12px; margin-bottom: 10px"
          />
          <Input
            v-if="isEdting"
            v-model="newData.description"
            :disabled="isSending"
            type="textarea"
            :border="false"
            placeholder="请输入资源描述..."
            :autosize="{ minRows: 10 }"
            show-word-limit
            maxlength="500"
            title="修改教育资源描述"
          />
        </div>
      </div>

      <!-- 右边栏 -->
      <div class="right-column-3">
        <Affix :offset-top="10" style="margin-top: 10px; margin-bottom: 20px">
          <!-- 来自于 -->
          <div
            class="right-panel-3 side-panel-3 panel-padding-3 glass shadow-act top-panel"
            style="margin-top: 0px"
            title="资源上传用户"
          >
            <!-- 头像栏 -->
            <div style="display: flex; flex-direction: row; align-items: center">
              <Avatar
                @click="jumpToUser(resData.userId)"
                size="large"
                style="margin-right: 10px"
                :src="resData.userAvatarUrl"
              />
              <!-- 用户名和id -->
              <div>
                <h3 class="small-title-dark title-break" style="max-width: 250px">
                  {{ resData.userName }}
                </h3>
                <p class="small-text-dark">{{ '@ ' + resData.userId }}</p>
              </div>
            </div>
            <!-- 发布时间栏 -->
            <div
              style="
                display: flex;
                flex-direction: row;
                justify-content: space-between;
                margin-top: 15px;
              "
              class="title-small-dark"
            >
              <div style="text-align: left">发布于</div>
              <!-- <div style="text-align: center;">|</div> -->
              <div style="text-align: right">{{ resData.uploadTime }}</div>
            </div>
          </div>

          <!-- 频道与类别 -->
          <div
            class="right-panel-3 side-panel-3 panel-padding-3 glass shadow-act"
            title="当前资源所属的频道和包含的资源种类"
          >
            <div>
              <v-chip
                v-if="!isEdting"
                variant="outlined"
                :title="'当前资源属于' + channelMapping[resData.channel] + '频道'"
                >{{ channelMapping[resData.channel] }}</v-chip
              >
              <v-chip-group
                v-if="isEdting"
                :disabled="isSending"
                v-model="newData.channel"
                column
                mandatory
                title="修改资源所属的频道"
              >
                <v-chip variant="outlined" filter v-for="t in fullChannelGroup">{{ t }}</v-chip>
              </v-chip-group>
            </div>
            <div style="margin-top: 10px">
              <v-chip
                v-if="!isEdting"
                variant="tonal"
                v-for="type in convertNumbersToTypes(resData.type)"
                style="margin-right: 5px"
                :title="'当前资源包含类型为' + type + '的内容'"
              >
                {{ type }}
              </v-chip>
              <v-chip-group
                v-if="isEdting"
                :disabled="isSending"
                v-model="newData.type"
                column
                mandatory
                multiple
                title="修改资源包含的文件类型"
              >
                <v-chip variant="tonal" filter v-for="t in fullTypeGroup">{{ t }}</v-chip>
              </v-chip-group>
            </div>
          </div>

          <!-- 下载栏 -->
          <div class="right-panel-3 side-panel-3 panel-padding-3 glass shadow-act side-expand">
            <!-- 下载量统计 -->
            <div
              style="display: flex; flex-direction: row; justify-content: space-between"
              class="title-small-dark"
            >
              <div style="text-align: left">当前下载量</div>
              <!-- <div style="text-align: center;">|</div> -->
              <div style="text-align: right">{{ resData.downloadCnt }}</div>
            </div>
            <!-- 文件名 -->
            <div
              style="display: flex; flex-direction: row; justify-content: space-between"
              class="title-small-dark"
            >
              <div style="text-align: left">文件名</div>
              <!-- <div style="text-align: center;">|</div> -->
              <div style="text-align: right; max-width: 70%" class="title-break">
                {{ resData.fileName }}
              </div>
            </div>
            <!-- 文件大小 -->
            <div
              style="display: flex; flex-direction: row; justify-content: space-between"
              class="title-small-dark"
            >
              <div style="text-align: left">文件大小</div>
              <!-- <div style="text-align: center;">|</div> -->
              <div style="text-align: right">{{ resData.fileSize + '字节' }}</div>
            </div>
            <v-btn
              :disabled="isEdting || !resData.canDownload"
              :loading="isTryingDownload"
              variant="tonal"
              color="green"
              style="margin-top: 10px; width: 100%"
              title="下载资源"
              @click="tryDownload"
              >下载资源</v-btn
            >
          </div>

          <!-- 删除与编辑框 -->
          <div
            class="right-panel-3 side-panel-3 panel-padding-3 glass shadow-act side-expand"
            v-if="resData.canModify || resData.canDelete"
            title="操作当前教育资源"
          >
            <div style="display: flex; flex-direction: row">
              <!-- 未进入编辑状态的按钮组 -->
              <v-btn
                v-if="!isEdting"
                :disabled="!resData.canModify"
                variant="tonal"
                color="orange"
                style="flex: 1; margin-right: 5px"
                title="编辑教育资源"
                @click="isEdting = true"
                >编辑</v-btn
              >
              <v-btn
                v-if="!isEdting"
                :disabled="!resData.canDelete"
                variant="flat"
                color="red"
                style="flex: 1; margin-left: 5px"
                title="删除教育资源"
                @click="clickDelete"
                >删除</v-btn
              >
              <!-- 进入编辑状态的按钮组 -->
              <v-btn
                v-if="isEdting"
                :disabled="isSending"
                variant="outlined"
                color="red"
                style="flex: 1; margin-right: 5px"
                title="取消编辑"
                @click="isEdting = false"
                >取消</v-btn
              >
              <v-btn
                v-if="isEdting"
                :disabled="isSending"
                variant="tonal"
                color="#0e7bff"
                style="flex: 1; margin-left: 5px; margin-right: 3px"
                title="将待编辑内容重置为原内容"
                @click="resetNewData"
                >重置</v-btn
              >
              <v-btn
                v-if="isEdting"
                :loading="isSending"
                variant="flat"
                color="green"
                style="flex: 1; margin-left: 5px"
                title="提交编辑"
                @click="tryModify"
                >提交</v-btn
              >
            </div>
          </div>
        </Affix>
      </div>
    </div>
    <v-dialog v-model="showDelete" :persistent="isDeleting" class="overlay">
      <div class="dialog-container">
        <div class="dialog">
          <!-- 标题 -->
          <div class="title-dark">是否确认删除资源？</div>
          <div class="text-large-dark" style="margin-top: 8px">
            您正在删除资源：<span style="font-weight: bolder">{{ resData.title }}</span
            >。是否继续？
          </div>
          <div style="display: flex; flex-direction: row; justify-content: end; margin-top: 15px">
            <v-btn
              variant="tonal"
              :disabled="isDeleting"
              style="margin-right: 5px"
              title="取消"
              @click.stop="showDelete = false"
              >取消</v-btn
            >
            <v-btn
              variant="flat"
              :loading="isDeleting"
              color="red"
              style="margin-left: 5px"
              @click.stop="tryDelete"
              title="删除资源"
              >确认删除</v-btn
            >
          </div>
        </div>
      </div>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import '@/assets/pageLayout3.css'
import '@/assets/texts.css'
import '@/assets/newDialog.css'
import '@/assets/panel3.css'
import '@/assets/panel.css'
import '@/assets/action.css'
import router from '@/router'
import { post, get, delet, put } from '@/net'
import { Snackbar } from '@varlet/ui'
import { useRoute } from 'vue-router'
import axios from 'axios'

const props = defineProps({
  resId: Number
})

// 路由参数兜底
const route = useRoute()
const id = props.resId || Number(route.params.resId)

// 需要与 ResourcePage 中的一致
const fullChannelGroup = [
  '理学工学',
  '农学',
  '外语',
  '经济管理',
  '计算机',
  '音乐与艺术',
  '心理学',
  '文史哲法',
  '医学与保健',
  '教育教学'
]
const channelMapping = {
  null: '正在寻找的资源',
  99: '被吃掉的资源',
  0: '理学工学',
  1: '农学',
  2: '外语',
  3: '经济管理',
  4: '计算机',
  5: '音乐与艺术',
  6: '心理学',
  7: '文史哲法',
  8: '医学与保健',
  9: '教育教学'
}

const fullTypeGroup = ['文档', '源代码', '视频', '图片', '音频', '其他']
const typeMapping = {
  0: '文档',
  1: '源代码',
  2: '视频',
  3: '图片',
  4: '音频',
  5: '其他'
}

// 用于转换数字到类型
function convertNumbersToTypes(numbers) {
  return numbers.map((number) => typeMapping[number])
}

// // 用于转换类型到数字
// function convertTypesToNumbers(types) {
//   // 创建反向映射
//   const reverseMapping = Object.keys(typeMapping).reduce((result, key) => {
//     result[typeMapping[key]] = parseInt(key);
//     return result;
//   }, {});

//   return types.map(type => reverseMapping[type]);
// }

watch(
    () => route.params.resId,
    (newVal) => {
      if (newVal) {
        updateData()
      }
    }
  )

const isEdting = ref(false)
const isSending = ref(false)
const isFail = ref(false)

const testData = {
  title: '色色学习资料',
  description:
    '开放教育资源（Open Educational Resources,OER）这个术语是在2002年的一次联合国教科文组织会议上被采纳的，意思指通过信息与传播技术来建立教育资源的开放供给，用户为了非商业的目的可以参考、使用和修改这些资源。这次会议的参与者表示“希望一起开发一种全人类可以使用的全球性教育资源”和“希望这种未来的开放资源能够动员起全球的教育工作者”。\n这是一资源，堃堃好强，张正奇好强，方方好强，陈哥好强我是傻逼。软工做不完了OS复习不完了真的折磨怎么这么多考试这么多大作业。软工狗都不做，怎么还有另外一个小组作业杀了我把而且还是明天交这怎么做的完啊。\n不许发癫，不行我就要在这里发颠。我是傻逼，真做不完了真复习不完了，还有从来没学过的数据库和软件工程基础和操作系统。OS还改评分标准，那我拼死拼活拿78分有个皮卡丘用，真的折磨。真得复习了，不行，不许复习，复习软工做不完了，不行，不复习复习不完了。研讨室预约系统怎么还出bug啊真的晦气。\n“‘开放在教育中作为一个流行术语在不断扩散，因此理解开放的定义就越来越重要。开放通常被简单地等同于‘免，这是错误的。开放教育的倡导者们正在努力形成一个共同的愿景，将‘开放义为：免费、可复制、可重组、没有任何获取或交互上的障碍。” [2]人们经常应用的开放教育资源的定义是指：免费开放的数字化材料，教育工作者、学生以及自主学习者可以在他们的教学、学习和研究中使用和再次使用(OECD, 2007)。 这里的资源包含三个部分：（1）学习内容：完整的课程、课件、内容模块、学习对象、论文集和期刊；（2）工具：有助于开发、使用、重复使用及传递学习内容的软件，包括内容的搜索与组织、内容与学习管理系统、内容开发工具和在线学习社区；（3）实施资源：促进材料公开发布的知识产权许可，最佳实践的设计原则和本地化内容。 [3]休特基金会（William and Flora Hewlett Foundation）官方网站的界定是：OER是指在在公共领域存在的，或者在允许他人免费应用和修改的知识产权许可协议下发布的教学、学习和研究资源，包括整门课程、课程材料、模块、教材、流媒体视频、测验、软件以及支持获取知识的其它任何工具、材料或和技术。 [4]',
  imgUrl:
    'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/105609878_p0_master1200.jpg',
  userName: '凯丁·布罗夫斯真',
  userAvatarUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/ava1.webp',
  userId: 10086094,
  uploadTime: '2023/15/13',
  channel: 5,
  type: [2, 3],
  downloadCnt: 50,
  fileSize: 15,
  fileName: '阿哲.pdf',
  canDelete: true,
  canModify: true,
  canDownload: true
}
const emptyData = {
  title: '正在加载...',
  description: '正在加载教育资源描述',
  imgUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/108396266_p0.jpg',
  userName: '正在寻找资源上传者',
  userAvatarUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/108396266_p0.jpg',
  userId: '00000000',
  uploadTime: '',
  channel: null,
  type: [],
  downloadCnt: 0,
  fileSize: 0,
  fileName: '',
  canDelete: false,
  canModify: false,
  canDownload: false
}
const failData = {
  title: '资源被吃掉了',
  description: '没有找到相应的教育资源，也许是被吃掉了，哭哭',
  imgUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/106349809_p0.jpg',
  userName: 'KraHa~n',
  userAvatarUrl: 'https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/79815374_p00.jpg',
  userId: '35644228',
  uploadTime: 'Pixiv',
  channel: 99,
  type: [],
  downloadCnt: 0,
  fileSize: 0,
  fileName: '',
  canDelete: false,
  canModify: false,
  canDownload: false
}

const resData = ref(emptyData)

const newData = ref({
  title: resData.value.title,
  description: resData.value.description,
  channel: resData.value.channel,
  type: resData.value.type
})

const resetNewData = () => {
  newData.value.title = resData.value.title
  newData.value.description = resData.value.description
  newData.value.channel = resData.value.channel
  newData.value.type = resData.value.type
}

const showImagePreview = ref(false)

// 下载
const isTryingDownload = ref(false)
const tryDownload = async () => {
  isTryingDownload.value = true
  await post('/resource/downloadResource', { resourceId: id }, (message) => {
    try {
      downloadFile(message.path, message.fileName)
    } catch (error) {
      Snackbar.error('下载处理时出错')
    }
  })
  updateData()
  isTryingDownload.value = false
}
function downloadFile(fileUrl, fileName) {
  getBlob(fileUrl).then((blob) => {
    save(blob, fileName)
  })
}
function getBlob(fileUrl) {
  return new Promise((resolve) => {
    const xhr = new XMLHttpRequest()
    xhr.open('GET', fileUrl, true)
    xhr.responseType = 'blob'
    xhr.onload = () => {
      if (xhr.status === 200) {
        resolve(xhr.response)
      }
    }
    xhr.send()
  })
}
function save(blob, filename) {
  if (window.navigator.msSaveOrOpenBlob) {
    navigator.msSaveBlob(blob, filename)
  } else {
    const link = document.createElement('a')
    const body = document.querySelector('body')

    let binaryData = []
    binaryData.push(blob)
    link.href = window.URL.createObjectURL(new Blob(binaryData))
    link.download = filename

    // fix Firefox
    link.style.display = 'none'
    body.appendChild(link)

    link.click()
    body.removeChild(link)

    window.URL.revokeObjectURL(link.href)
  }
}

// 删除
const showDelete = ref(false)
const isDeleting = ref(false)
const clickDelete = () => {
  showDelete.value = true
  isDeleting.value = false
}
const tryDelete = async () => {
  isDeleting.value = true
  await delet(
    '/resource/deleteResource',
    { resourceId: props.resId },
    { resourceId: id },
    (message) => {
      router.push('/resources/1')
      Snackbar.success('帖子删除成功')
    },
    (message, status) => {
      Snackbar.error('删除失败，错误码：' + message + '；错误消息：' + status)
    }
  )
  showDelete.value = false
  isDeleting.value = false
}

// 修改
const tryModify = async () => {
  var legal = true
  showTitleAlert.value = false
  showTextAlert.value = false
  console.log('try submit modify')
  if (newData.value.title.length == 0) {
    legal = false
    showTitleAlert.value = true
  }
  if (newData.value.description.length == 0) {
    legal = false
    showTextAlert.value = true
  }
  if (legal == false) {
    return
  }

  

  isSending.value = true
  await put(
    '/resource/updateResource',
    {
      id: id,
      title: newData.value.title,
      subject: newData.value.channel,
      categories: newData.value.type,
      content: newData.value.description
    },
    (message) => {
      isEdting.value = false
      Snackbar.success('资源修改成功')
      updateData()
    },
    (message, status) => {
      Snackbar.error('资源修改失败，错误码：' + message + '；错误消息：' + status)
    }
  )
  isSending.value = false
}


const updateData = async () => {
  try {
    const resId = props.resId ?? Number(route.params.resId);
    if (!resId) return;

    // Use post with callbacks, wrapped in a Promise for await
    await new Promise((resolve, reject) => {
      post('/resource/getResDetail', { resourceId: resId }, (resp) => {
        const message = resp.message; // Assuming post's success callback gets { message: { ... } }
        resData.value = {
          title: message.title,
          description: message.content,
          imgUrl: message.imageUrl,
          userName: message.username,
          userAvatarUrl: message.profilePhotoUrl,
          userId: message.userId,
          uploadTime: message.publishTime || '未知',
          channel: message.subject,
          type: Array.isArray(message.categories) ? message.categories : [],
          downloadCnt: message.downloadCount,
          fileSize: message.size,
          fileName: message.fileName,
          canDelete: message.canDelete,
          canModify: message.canModify,
          canDownload: message.canDownload
        }
        resetNewData()
        resolve();
      }, (errorMsg, status) => { // Optional failure callback
        Snackbar.error(`获取资源详情失败: ${errorMsg} (状态: ${status})`);
        reject(new Error(`Error: ${errorMsg}, Status: ${status}`));
      })
    });
  } catch (e) {
    console.error(e)
    resData.value = failData
  }
}

function jumpToUser(userId) {
  if (userId > 0) {
    // window.open('/user/' + userId, '_blank');
    router.push('/user/' + userId)
  }
}

updateData()
    
const showTitleAlert = ref(false)

const showTextAlert = ref(false)
</script>

<style></style>

<style scoped>
.clickable {
  cursor: pointer;
}
</style>
