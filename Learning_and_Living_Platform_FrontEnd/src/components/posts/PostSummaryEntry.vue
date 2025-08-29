<!-- 在该文件中使用了View Design组件 -->
<template>
  <div>
    <!-- v-btn存在显示bug，故弃用 -->
    <!-- <v-card>
        <v-card-item>
            <div>
                <h3 width="100%" class="title-break">{{ postEntryData.title }}</h3>
                <p width="100%">{{ postEntryData.text }}</p>
            </div>
        </v-card-item>
        <v-card-actions>
            
        </v-card-actions>
    </v-card> -->
    <!-- 以下是使用View Design的版本 -->
    <div class="centre-panel centre-expand shadow-act glass clickable" @click="showDetail"
    @click.stop="$emit('detailClicked', postEntryData.postId)">
      <!-- 标题栏 -->
      <div width="100%" style="display: flex; flex-direction: row; align-items: center;">
        <!-- 头像框 -->
        <Avatar size="large" style="margin-right: 10px;" :src="postEntryData.avatarUrl">{{ postEntryData.userNameId }}
        </Avatar>
        <!-- 标题和用户名 -->
        <div style="flex: 2; display: flex; flex-direction: column;">
          <h3 width="100%" class="title-break small-title-dark">
            <p v-if=" postEntryData.hot > 500">🔥{{ postEntryData.title }}</p>
            <p v-else>{{ postEntryData.title }}</p>
          </h3>
          <p class="small-text-dark">{{ postEntryData.userNameId }}</p>
          <!-- 按钮们 -->
        </div>
        <div style="flex: 1; display: flex; justify-content: flex-end;">
          <!-- 使用click.stop阻止事件向上冒泡 -->
          <v-btn variant="tonal" v-if="postEntryData.canDelete" style="margin-right: 10px; color: #F97B22"
            @click.stop="$emit('deleteClicked', postEntryData.postId)"
            >删除</v-btn>
          <v-btn variant="tonal" style="margin-right: 10px; color: #088395" @click.stop="$emit('detailClicked', postEntryData.postId)">查看详情</v-btn>
        </div>
      </div>

      <div width="100%" style="display: flex; flex-direction: row; margin-top: 10px;">
        <div style="display: flex; flex-direction: row;"> 
          <Icon style="margin-top: 1px;" type="md-eye" />
          <p style="margin-left: 5px;">{{ postEntryData.browse }}</p>
        </div>
        <div style="margin-left: 40px; display: flex; flex-direction: row;">
          <Icon style="margin-top: 2px;" color="red" type="md-heart" />
          <p style="margin-left: 5px;">{{ postEntryData.like }}</p>
        </div>
      </div>
    </div>
  </div>
</template>



<script>
import '@/assets/panel.css'
import '@/assets/texts.css'


export default {
  props: ['postEntryData']
}
</script>

<style>
.clickable {
    cursor: pointer;
}
</style>