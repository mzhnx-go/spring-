<script setup>
import { ref, inject, computed , watch} from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useStore } from '@/stores/my'
import { ElMessageBox } from 'element-plus'
const size = ref(30)
const toLogin = inject("toLogin")

const store = useStore()
const userName = ref("")
const isLogined = ref(false)

const toAdminMain = inject("toAdminMain")

// 监听store中的用户数据变化（替换上面错误的watch写法）
watch(
  () => store.user.user, // 监听的目标：store里的user对象
  (newVal) => { // 数据变化时执行的逻辑
    if (newVal) {
      userName.value = "hi!" + newVal.username; // 修正属性名：username
      isLogined.value = true;
    } else {
      userName.value = "";
      isLogined.value = false;
    }
  },
  { immediate: true } // 立即执行一次（替代原有的if初始化逻辑）
)

const toHome = inject("toHome")
const axios = inject('axios')

function toExit(){
  axios({
    method:'post',
    url:'/api/logout'
  }).then(response=>{
    ElMessageBox.alert(response.data.msg,'结果')
    store.user.user=null
    isLogined.value=false
    userName.value=""
    toHome()
  }).catch((error)=>{//请求失败返回的数据
    ElMessageBox.alert("系统错误！",'结果')
  })
}

</script>
<template>
  <el-row>
    <el-col :span="3">
    </el-col>
    <el-col :span="12">
      <a href="/">个人博客小站</a>
    </el-col>
    <el-col :span="6">
      <el-space :size="size">
        <a @click="toLogin" v-if="!isLogined">登录</a>
        <a @click="toExit" v-if="isLogined">退出</a>
        <span v-html="userName"></span>
        <router-link title="查询" :to="{path: '/search'}" style="text-decoration: none;">
          <el-icon>
            <Search class="searchIcon"/>
          </el-icon>
        </router-link>
      </el-space>
    </el-col>
    <el-col :span="3">
    </el-col>
  </el-row>

</template>

<style scoped>
* {
  background-color: #5f9ea0;
  color: #fff;
  font-size: 22px;
}

.top {height: 80px;}
a {
  text-decoration: none;
  cursor: pointer;
}
a:hover {color: #10D07A;}

.searchIcon {color: black;}
.searchIcon:hover{color: #10D07A;}

</style>
