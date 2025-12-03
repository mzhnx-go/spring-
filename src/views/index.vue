<script setup>
import Top from "@/components/Top.vue";
import { reactive,inject,ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import ArticleHeader from "@/components/ArticleHeader.vue";
import imageMeUrl from "@/assets/me.jpg"
import ReadRanking from "@/components/ReadRanking.vue";

const toArticle = inject("toArticle")

const size = ref(20)
const axios = inject('axios')
const data=reactive({articles:[],articleVOs:[]}) // 响应式对象

function getIndexData(){
  axios({method: 'post', url:'/api/article/getIndexData'})
  .then((response) => { // 请求数据
    if (response.data.success) {
      data.articles=response.data.map.articles
      data.articleVOs=response.data.map.articleVOs
    } else {
      ElMessageBox.alert(response.data.msg, '结果')
    }
  }).catch((error) => { // 请求失败返回的数据
    ElMessageBox.alert("系统错误！", "结果")
  })
}

getIndexData()

</script>

<template>
    <el-affix><Top/></el-affix>
    <el-row style="margin-top:40px" align="top"> <!-- 垂直对齐方式 -->
      <el-col :span="1"></el-col>
      <el-col :span="14">
        <template v-for="article in data.articles">
          <ArticleHeader :article="article"/> <!-- 循环展示 -->
        </template>
      </el-col>
      <el-col :span="1"></el-col>
      <el-col :span="7">
      <fieldset align="center">
        <legend><h3>CrazyStone</h3></legend>
        <el-image :src="imageMeUrl"/>
        <div style="margin-top: 16px;">Java后台开发</div>
        <div style="margin-top: 16px;">
          个人博客小站,主要发布Java、Spring、Docker等相关文章
        </div>
      </fieldset>

      <fieldset align="center">
        <legend><h3>联系我</h3></legend>
        <el-space :size="size">
          <font-awesome-icon class="icon" icon="fa-brands fa-github" size="lg" border />
          <font-awesome-icon class="icon" icon="fa-brands fa-weibo" size="lg" border />
        </el-space>
      </fieldset>

      <ReadRanking :articles="data.articleVOs" />
      </el-col>
      <el-col :span="1"></el-col>
    </el-row>
  </template>
  
  <style scoped>
.icon:hover{
  color:#10D07A;
}

  fieldset {
    border-color: #eee;
    border-width:  1px;
    border-style: solid;
  }
  </style>

