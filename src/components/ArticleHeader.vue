<script setup>
// 引入tool.js中的dateFormat函数
import { dateFormat } from "@/js/tool.js"
import {  ref } from 'vue' // 导入vue的api
const props = defineProps(['article'])
const size = ref(40) // el-space组件中使用的间距
</script>

<template>
  <!-- 响应式设计，浏览器窗口变小时（sm），占用一行（24），m或更大窗口时，占一行的11/24 -->
  <el-row class="article-item">
    <el-col :sm="24" :md="11">
      <!-- element-plus的图片组件 -->
      <div class="thumbnail-container">
    <el-image 
      :src="props.article.thumbnail" 
      fit="cover" 
      class="thumbnail-image"
    />
  </div>
    </el-col>
    <!-- 窗口为sm或更小时，会隐藏（0） -->
    <el-col :sm="0" :md="1"></el-col>
    <el-col :sm="24" :md="12">
    <el-row align="middle"><el-col>
        <el-space :size="size">
            <span id="categories" class="categorie-height" v-html="props.article.categories"></span>
            <span class="categorie-height" v-html="'发布于'+dateFormat(props.article.created,'yyyy-MM-dd')"></span>
        </el-space>
    </el-col></el-row> 
    <el-row align="middle"><el-col>
        <span class="title" v-html="props.article.title"></span>
    </el-col></el-row>
    <el-row align="middle"><el-col>
        <span v-html="props.article.content.substring(0,90)"></span>
    </el-col></el-row>
    <!-- javascript字符串的求子串的方法 -->
</el-col>
<el-col :span="1"></el-col>
</el-row>
</template>

<style scoped>
/* #表示元素选择器，hover表示鼠标在其上时 */
#categories:hover { color: #10D07A;}
.categorie-height{line-height: 40px;}
.title{
  color: #0f9ae0;
  font-size:20px;
  line-height: 40px;
}

.thumbnail-container {
  width: 100%;
  height: 0;
  padding-bottom: 60%; /* 保持5:3宽高比 */
  position: relative;
  overflow: hidden;
}

.thumbnail-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.article-item {
  margin-bottom: 20px;
}
</style>