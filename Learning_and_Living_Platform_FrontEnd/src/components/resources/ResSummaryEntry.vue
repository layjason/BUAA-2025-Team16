<!-- 在该文件中使用了View Design和Vuetify组件 -->
<template>
<div>
    <!-- <v-card>
        <v-card-item>
            <div>
                <h3 width="100%" class="title-break">{{ resEntryData.title }}</h3>
                <p width="100%">{{ resEntryData.text }}</p>
            </div>
        </v-card-item>
        <v-card-actions>
            
        </v-card-actions>
    </v-card> -->
    <!-- 以下是使用View Design的版本 -->
    <div class="centre-panel centre-expand shadow-act glass clickable" @click="$emit('detailClicked', resEntryData.resId)" title="查看资源详情">
        <!-- 标题栏 -->
        <div width="100%" style="display: flex; flex-direction: row; align-items: center;">
            <!-- 头像框 -->
            <Avatar size="large" style="margin-right: 10px;" :src="resEntryData.avatarUrl" title="资源上传者头像">{{ resEntryData.userNameTime[0] }}</Avatar>
            <!-- 标题和用户名 -->
            <div style="flex: 2; display: flex; flex-direction: column;">
                <h3 width="100%" class="title-break small-title-dark">{{ resEntryData.title }}</h3>
                <p class="small-text-dark title-break">{{ resEntryData.userNameTime }}</p>
            <!-- 按钮们 -->
            </div>
            <div style="flex: 1; display: flex; justify-content: flex-end;">
                <!-- 使用click.stop阻止事件向上冒泡 -->
                <!-- <v-btn variant="tonal" color="orange" v-if="resEntryData.canModify" style="margin-right: 10px;" @click.stop="deleteRes">修改</v-btn> -->
                
                <v-btn variant="flat" color="red" v-if="resEntryData.canDelete" style="margin-right: 5px;" @click.stop="$emit('deleteClicked', resEntryData.resId, resEntryData.title)" title="删除资源">删除</v-btn>
                <v-btn variant="tonal" color="#0e7bff" style="margin-right: 10px;" @click.stop="$emit('detailClicked', resEntryData.resId)">查看资源详情</v-btn>
                <!-- <v-btn variant="flat" color="green" :disabled="resEntryData.canDownload" @click.stop="downloadRes">下载资源</v-btn> -->
            </div>
        </div>
        <div style="margin-top: 10px;">
            <v-chip variant="flat" title="资源所属频道">{{ resEntryData.channel }}</v-chip>
            <v-chip variant="tonal" v-for="type in resEntryData.type" style="margin-left: 5px;" title="资源包含的文件类型">{{ type }}</v-chip>
        </div>
        
        <p width="100%" style="margin-top: 15px;" class="text-dark title-break-5">{{ resEntryData.text }}</p>
    </div>
</div>
</template>



<!-- <script>
import { ref } from 'vue'
import '@/assets/panel.css'
import '@/assets/texts.css'

export default {
    props: ['resEntryData'],
    setup(props) {
        const showDetail = () => {
            console.log('Detail No.' + props.resEntryData.id +' is clicked, res id is : '+ props.resEntryData.resId)
        }

        const downloadRes = () => {
            console.log('Download No.' + props.resEntryData.id +' is clicked, res id is : '+ props.resEntryData.resId)
        }

        const deleteRes = () => {
            console.log('Delete No.' + props.resEntryData.id +' is clicked, res id is : '+ props.resEntryData.resId)
        }

        return {
            showDetail,
            downloadRes,
            deleteRes
        }
    }
}
</script> -->

<script setup>
import { ref } from 'vue'
import '@/assets/panel.css'
import '@/assets/texts.css'

const deleteModal = ref(false)

const props = defineProps({
    resEntryData: Object,
    deleting: Boolean,
    show: Boolean
})

const deleteRes = () => {
    deleteModal.value = false
    console.log('Delete No.' + resEntryData.value.id +' is clicked, res id is : '+ resEntryData.value.resId)
}


</script>


<style scoped>
.clickable {
    cursor: pointer;
}
</style>