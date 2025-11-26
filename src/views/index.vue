<script setup>
import Top from "@/components/Top.vue";
import {inject} from 'vue'
const toArticle = inject("toArticle")
</script>
<template>
<el-affix><Top/></el-affix>
<el-row>
    <el-col :span="2"></el-col>
    <el-col :span="14">
        <el-button type="primary" @click="toArticle"> 
            跳转文章
        </el-button>
    </el-col>
</el-row>
a<br>a</br>a<br>a</br>a<br>a</br>a<br>a</br>
a<br>a</br>a<br>a</br>a<br>a</br>a<br>a</br>
a<br>a</br>a<br>a</br>a<br>a</br>a<br>a</br>
a<br>a</br>a<br>a</br>a<br>a</br>a<br>a</br>
a<br>a</br>a<br>a</br>a<br>a</br>a<br>a</br>
a<br>a</br>a<br>a</br>a<br>a</br>a<br>a</br>
a<br>a</br>a<br>a</br>a<br>a</br>a<br>
</template>
<style scoped>

</style>