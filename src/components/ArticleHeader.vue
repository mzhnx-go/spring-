<script setup>
// 引入tool.js中的dateFormat函数
import { dateFormat } from "@/js/tool.js"
import { defineProps, ref } from 'vue' // 导入vue的api
const props = defineProps({ // 定义组件属性
  article: {
    type: Object
  }
})
const size = ref(40) // el-space组件中使用的间距
</script>

<template>
  <!-- 响应式设计，浏览器窗口变小时（sm），占用一行（24），m或更大窗口时，占一行的11/24 -->
  <el-row>
    <el-col :span="24" :sm="11">
      <!-- element-plus的图片组件 -->
      <el-col :image="props.article.thumbnail" />
    </el-col>
    <!-- 窗口为sm或更小时，会隐藏（0） -->
    <el-col :sm="0" :md="1"></el-col>
    <el-col :sm="24" :md="12">
      <el-row :align="middle">
        <el-col :span="5">
          <span :id="size" class="categorie-height" v-html="props.article.categories"></span>
        </el-col>
        <span class="categorie-height" v-html="'发布' + dateFormat(props.article.created, 'yyyy-MM-dd')"></span>
        <!-- 调用tool.js中的dateFormat函数 -->
      </el-row>
      <el-row :align="middle">
        <el-col :span="24">
          <span class="title" v-html="props.article.title"></span>
        </el-col>
      </el-row>
      <el-row :align="middle">
        <el-col :span="24">
          <!-- javascript字符串的求子串的方法 -->
          <span v-html="props.article.content.substring(0,30)"></span>
        </el-col>
      </el-row>
    </el-col>
    <el-col :span="1"></el-col>
  </el-row>
</template>

<style scoped>
/* #表示元素选择器，hover表示鼠标在其上时 */
#categories:hover { color: #10D07A;}
.references
.categorie-height{line-height: 40px;}
.reference
.title{
  color: #0f9ae0;
  font-size:20px;
  line-height: 40px;
}
</style>