<script setup>
import Top from "@/components/Top.vue";
import { reactive,inject,ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import ArticleHeader from "@/components/ArticleHeader.vue";
const toArticle = inject("toArticle")


const axios = inject('axios')
const data=reactive({articles:[]}) // 响应式对象

function getIndexData(){
  axios({method: 'post', url:'/api/article/getIndexData'})
  .then((response) => { // 请求数据
    if (response.data.success) {
      data.articles=response.data.map.articles
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
      </el-col>
      <el-col :span="1"></el-col>
    </el-row>
  </template>
  
  <style scoped>
  </style>
