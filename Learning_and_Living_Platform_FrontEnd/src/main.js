import {createApp } from 'vue'
import { createPinia } from 'pinia'
import Varlet from '@varlet/ui'
import '@varlet/ui/es/style'
import ViewUIPlus from 'view-ui-plus'
import 'view-ui-plus/dist/styles/viewuiplus.css'
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'


const vuetify = createVuetify({
  components,
  directives,
})

import App from './App.vue'
import router from './router'

import axios from 'axios'
axios.defaults.baseURL = '/api'


const app = createApp(App)

app.use(createPinia())
app.use(Varlet)
app.use(ViewUIPlus)
app.use(router)
app.use(vuetify)
//app.use(hljs);
/*
app.directive('highlight', (el) => {
  let blocks = el.querySelectorAll('pre code')
  blocks.forEach((block) => {

    // 创建ol标签元素
    let ol = document.createElement("ol");

    // 2.根据换行符获取行数，根据获取的行数生成行号
    let rowCount = block.outerHTML.split('\n').length;
    for (let i = 1; i < rowCount; i++) {
      // 创建li标签元素
      let li = document.createElement("li");
      let text = document.createTextNode(i);
      // 将生成的行号添加到li标签中
      li.appendChild(text);
      // 将li标签添加到ol标签中
      ol.appendChild(li);
    }
    // 为ol标签添加class名
    ol.className = 'pre-numbering';
    block.parentNode.appendChild(ol);

    hljs.highlightBlock(block)
  })
});
*/
app.mount('#app')

