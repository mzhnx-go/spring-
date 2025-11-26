<script setup>
import { ref, inject } from 'vue'
const size = ref(30)

const toAdminMain = inject("toAdminMain")
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
        <a href="/login">登录</a>
        <a @click="toAdminMain">后台管理</a>
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

</style>
