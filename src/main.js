

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

/* import the fontawesome core */
import { library } from '@fortawesome/fontawesome-svg-core'
/* import font awesome icon component */
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
// 引入图标名为weibo和github的两个图标，import时，要改为（fa+首字母大写的图标名）
/* add icons to the library */
import { faWeibo,faGithub } from '@fortawesome/free-brands-svg-icons'
// 将这些图标加入到library，以后vue文件可以使用这些图标
library.add(faWeibo)
library.add(faGithub)


const app = createApp(App)
app.component('font-awesome-icon', FontAwesomeIcon)
app.use(createPinia())
app.use(router)

//导入element-plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
app.use(ElementPlus)
//全局导入ElementPlus的图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

//引入axios和vue-axios
import axios from 'axios'
//vue-axios是将axios集成到Vue.js的小包装器，可以像插件一样进行安装
import VueAxios from 'vue-axios'
app.use(VueAxios, axios)
//provide 'axios'，其它地方可以注入，从而可以使用axios
app.provide('axios', app.config.globalProperties.axios) 

import VueCropper from 'vue-cropper';
import 'vue-cropper/dist/index.css'
app.use(VueCropper)

app.mount('#app')
