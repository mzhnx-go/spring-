

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

const app = createApp(App)

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

app.mount('#app')
