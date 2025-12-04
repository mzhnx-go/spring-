<script setup>
import Top from "@/components/Top.vue";
import { reactive,inject,ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import ArticleHeader from "@/components/ArticleHeader.vue";
import imageMeUrl from "@/assets/me.jpg"
import ReadRanking from "@/components/ReadRanking.vue";
import {onBeforeRouteLeave} from 'vue-router'
import {useStore} from '@/stores/my'

const store = useStore()
const toArticle = inject("toArticle")

const size = ref(20)
const axios = inject('axios')
const data = reactive({
  "articles": [],
  "pageParams": {"page": store.home.page, "rows": 5, "total": 0},
  "articleVOs": []
}) // 响应式对象

//分页查询文章
function getAPage() { 
  
    axios({
      
        method: 'post',
        url: '/api/article/getAPageOfArticle',
        data: data.pageParams
    }).then((response) => {
        if (response.data.success) {
            if (response.data.map.articles != null) {
                data.articles = response.data.map.articles
                data.pageParams = response.data.map.pageParams
                window.scrollTo(0, 0)//滚动到顶端
            } else {
                ElMessageBox.alert("无文章！", '结果')
            }
        } else {
            ElMessageBox.alert(response.data.msg, '结果')
        }
    }).catch((error) => { //请求失败返回的数据
        ElMessageBox.alert("系统错误！", '结果')
    })
}

function handleCurrentChange(newPage) { // 当前页码改变时触发
    data.pageParams.page = newPage
    getAPage()
}
function getIndexData(){
  console.log('请求参数:', data.pageParams);
  axios({
    method: 'post', 
    url:'/api/article/getIndexData1',
    data:data.pageParams
  })
  .then((response) => { // 请求数据
    console.log('API 响应:', response.data);
    if (response.data.success) {
      data.articles=response.data.map.articles
      data.articleVOs=response.data.map.articleVOs
      if (response.data.map.pageParams) {
        data.pageParams.total = response.data.map.pageParams.total
      }
    } else {
      ElMessageBox.alert(response.data.msg, '结果')
    }
  }).catch((error) => { // 请求失败返回的数据
    console.error('API 错误详情:', error);
    ElMessageBox.alert("系统错误！", "结果")
  })
}
getIndexData()

onBeforeRouteLeave((to, from) => { // 路由离开时触发
  if(to.fullPath.indexOf("article_comment")>=0){
    store.home.page=data.pageParams.page
  }else{
    store.home.page=1
  }
  return true
})

</script>

<template>
    <el-affix><Top/></el-affix>
    <el-row style="margin-top:40px" align="top"> <!-- 垂直对齐方式 -->
      <el-col :span="1"></el-col>
      <el-col :span="14">
        <template v-for="article in data.articles">
          <ArticleHeader :article="article"/> <!-- 循环展示 -->
        </template>
        <el-row>
    <el-col>
        <!-- element-plus的分页组件 -->
        <el-pagination v-model:currentPage="data.pageParams.page"
            v-model:page-size="data.pageParams.rows"
            layout="prev, pager, next"
            :total="data.pageParams.total"
            @current-change="handleCurrentChange"
            :pager-count="7"
        />
    </el-col>
</el-row>
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

