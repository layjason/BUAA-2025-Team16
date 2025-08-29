<template>
  <div>
    <div>
      <vue3-tinymce v-model="state.content" :setting="state.setting" />
    </div>
    <div style="text-align: center;margin-top: 20px;">
      <v-btn @click = "submit">
        提交
      </v-btn>
      <v-btn @click="clear"
      style="margin-left: 10px;">
        清空
      </v-btn>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
// 引入组件
import Vue3Tinymce from '@jsdawn/vue3-tinymce';

const state = reactive({
  content: '',
  setting: {
    height: 300,
    toolbar:
      'undo redo | fullscreen | blocks alignleft aligncenter alignright alignjustify | link unlink | numlist bullist | image media table | fontsize forecolor backcolor | bold italic underline strikethrough | indent outdent | superscript subscript | removeformat |',
    toolbar_mode: 'sliding',
    quickbars_selection_toolbar:
      'removeformat | bold italic underline strikethrough | fontsize forecolor backcolor',
    plugins: 'link image table lists fullscreen quickbars',
    font_size_formats: '12px 14px 16px 18px',
    link_default_target: '_blank',
    link_title: false,
    nonbreaking_force_tab: true,
    // 以中文简体为例
    language: 'zh-Hans',
    language_url:
      'https://unpkg.com/@jsdawn/vue3-tinymce@2.0.2/dist/tinymce/langs/zh-Hans.js',
    custom_images_upload: true,
    images_upload_url: 'your_upload_api_url...',
    custom_images_upload_callback: (res) => res.url,
    custom_images_upload_param: { id: 'xxxx01', age: 18 },
  }
});

function content_clear() {
  state.content = ''
}

// 使用defineEmits创建名称，接受一个数组
const emit = defineEmits(['submit','clear'])
const submit = () => {
  emit('submit', state.content)
}

const clear = () => {
  content_clear()
  emit('clear')
}

</script>
