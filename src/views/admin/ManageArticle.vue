<script setup>
import {reactive, inject,ref} from 'vue'
import { ElMessageBox, ElDialog} from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'
import { useStore } from '@/stores/my'
import {nullZeroBlank} from "@/js/tool.js"
const store = useStore()
const dialogVisible = ref(false) // 是否显示对话框
let selectedArticleId

const axios = inject('axios')
let myData = reactive({
  "articleVOs": [],
  "pageParams": {
    "page": 1, 
    "rows": 4,
    "total": 0  // 确保包含total字段
  }
})

function getAPage() {
  // 从store恢复分页状态
  if (!nullZeroBlank(store.page.pageParams)) {
    myData.pageParams = { ...store.page.pageParams }
  }
  
  // 调试日志，便于检查请求参数
  console.log('[分页调试] 请求参数:', myData.pageParams)
  
  axios({
    method: 'post',
    url: '/api/article/getAPageOfArticleVO',
    data: myData.pageParams
  }).then((response) => {
    // 调试日志，检查后端返回的完整数据结构
    console.log('[分页调试] 后端返回的完整数据:', response.data)
    
    if (response.data.success && response.data.map) {
      // 处理文章列表数据
      myData.articleVOs = response.data.map.articleVOs || []
      
      // 关键修复：正确获取总记录数
      // 根据后端实现，total 应该在 pageParams 对象中
      const backendPageParams = response.data.map.pageParams
      if (backendPageParams && typeof backendPageParams.total === 'number') {
        myData.pageParams.total = backendPageParams.total
        console.log(`[分页调试] 成功获取总记录数: ${backendPageParams.total}`)
      } else {
        console.warn('[分页警告] 后端未返回有效的 total 值，尝试从其他途径获取')
        // 如果后端没有正确返回 total，尝试估算
        myData.pageParams.total = myData.articleVOs.length * myData.pageParams.rows
      }
      
      // 如果当前页无数据且不是第一页，自动跳回第一页
      if (myData.articleVOs.length === 0 && myData.pageParams.page > 1) {
        myData.pageParams.page = 1
        getAPage()
      }
    } else {
      // 后端返回失败的处理
      const errorMsg = response.data.msg || "获取数据失败"
      console.error('[分页错误] 后端返回错误:', errorMsg)
      ElMessageBox.alert(errorMsg, '错误')
      myData.articleVOs = []
      myData.pageParams.total = 0
    }
    
    // 清除store中的分页状态
    store.page.pageParams = null
  }).catch((error) => {
    // 网络请求错误处理
    console.error('[分页错误] 请求异常:', error)
    
    let errorMsg = "系统错误"
    if (error.response) {
      errorMsg = `请求失败 (HTTP ${error.response.status}): ${error.response.data?.msg || '服务器错误'}`
    } else if (error.request) {
      errorMsg = "无法连接到服务器，请检查网络"
    } else {
      errorMsg = `请求配置错误: ${error.message}`
    }
    
    ElMessageBox.alert(errorMsg, '错误')
    store.page.pageParams = null
  }).finally(() => {
    // 确保无论成功或失败，store状态都被清理
    store.page.pageParams = null
  })
}

getAPage()
function handleSizeChange(newRows) {
  myData.pageParams.rows = newRows
  myData.pageParams.page = 1  // 重置到第一页
  getAPage() 
}

function handleCurrentChange(newPage) {
  myData.pageParams.page = newPage
  getAPage() 
}

const editArticle = inject("editArticle")
function editArticle1(articleId){
  store.articleId=articleId
  store.page.pageParams=myData.pageParams
  editArticle()
}



function showDialog(articleId) { // 显示对话框
  selectedArticleId = articleId
  dialogVisible.value = true
}

function deleteArticle() { // 删除文章
  axios({
    method: 'post',
    url: '/api/article/deleteById?id=' + selectedArticleId
  }).then((response) => {
    if (response.data.success) {
      getAPage() // 删除成功后，刷新列表
    } else {
      ElMessageBox.alert(response.data.msg, '结果')
    }
  }).catch((error) => { // 请求失败返回的数据
    ElMessageBox.alert("系统错误！", '结果')
  })
}
</script>
<template>
    <el-row>
      <el-col :span="24"><h4 style="margin-left: 10px;">文章管理</h4></el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-table :data="myData.articleVOs" stripe border style="width: 100%">
          <el-table-column label="文章标题" width="360">
            <template #default="scope">
              <router-link :to="{path:'/article_comment/'+scope.row.id}" style="text-decoration: none;">
                {{scope.row.title}}
              </router-link>
            </template>
          </el-table-column>
          <el-table-column prop="created" label="发布时间" width="170" />
          <el-table-column prop="hits" label="浏览量" width="70" />
          <el-table-column prop="categories" label="所属分类" width="100" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="primary" @click="editArticle1(scope.row.id)":icon="Edit" size="small">
                编辑
              </el-button>
              <el-button type="danger" :icon="Delete" size="small" @click="showDialog(scope.row.id)" >
                删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row justify="center" align="middle" style="height:60px">
  <el-col :span="16">
    <el-pagination 
      v-model:current-page="myData.pageParams.page"
      v-model:page-size="myData.pageParams.rows"
      :page-sizes="[2, 4, 10, 20]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="myData.pageParams.total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :pager-count="7"
      background
    />
  </el-col>
</el-row>

<!-- 3. 关键修复：添加删除确认对话框（之前模板里漏了，对话框根本不渲染） -->
  <el-dialog v-model="dialogVisible" title="删除确认" width="30%" center>
    <span>确定要删除这篇文章吗？删除后不可恢复！</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="deleteArticle">确定删除</el-button>
      </span>
    </template>
  </el-dialog>
  </template>
<style scoped>

</style>