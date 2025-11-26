
<script setup>
import { reactive, ref, inject, watch, onMounted } from 'vue'
import Editor from '@tinymce/tinymce-vue'
import { ElMessageBox } from 'element-plus'
import { useStore } from '@/stores/my'
// 1. 先声明所有变量
const store = useStore()
let type = "add"
const header = ref("发布文章")
const loading = ref(false)
const axios = inject('axios')
const gotoArticleManage = inject("gotoArticleManage")

// 文章数据
let article = reactive({ "title": "", "tags": "", "content": "" })

const image_upload_handler = (blobInfo, progress) => new Promise((resolve, reject) => {
  const xhr = new XMLHttpRequest();
  xhr.withCredentials = false;
  xhr.open('POST', 'http://localhost:5173/api/article/upload');

  xhr.upload.onprogress = (e) => {
    progress(e.loaded / e.total * 100);
  };

  xhr.onload = () => {
    if (xhr.status === 403) {
      reject({ message: 'HTTP Error: ' + xhr.status, remove: true });
      return;
    }

    if (xhr.status < 200 || xhr.status >= 300) {
      reject('HTTP Error: ' + xhr.status);
      return;
    }

    const json = JSON.parse(xhr.response);

    if (!json || !json.map || typeof json.map.url != 'string') {
      reject('Invalid JSON: ' + xhr.responseText);
      return;
    }

    resolve(json.map.url);
  };

  xhr.onerror = () => {
    reject('Image upload failed due to a XHR Transport error. Code: ' + xhr.status);
  };

  const formData = new FormData();
  formData.append('file', blobInfo.blob(), blobInfo.filename());

  xhr.send(formData);
});

// 专门处理文章加载的函数
const loadArticle = (articleId) => {
  console.log('[调试] 尝试加载文章, articleId:', articleId, 'store.articleId:', store.articleId);
  
  const id = Number(articleId);
  if (isNaN(id) || id <= 0) {
    console.warn('[调试] 无效的 articleId:', articleId);
    return;
  }
  
  loading.value = true;
  type = "edit";
  header.value = "编辑文章";
  
  axios({
    method: 'post',
    url: '/api/article/selectById',
    params: { id: id }
  }).then((response) => {
    console.log('[调试] API 响应:', JSON.stringify(response.data, null, 2));
    
    if (response.data.success) {
      console.log('[调试] 文章数据结构:', Object.keys(response.data.map.article || {}));
      
      if (!response.data.map?.article) {
        console.error('[调试] 文章数据为空', response.data.map);
        ElMessageBox.alert("文章数据为空，请检查后端返回", '结果');
        return;
      }
      
      const nowArticle = response.data.map.article;
      
      // 详细检查文章属性
      console.log('[调试] 文章属性检查:', {
        id: nowArticle.id,
        title: nowArticle.title,
        content: nowArticle.content ? `${nowArticle.content.substring(0, 50)}...` : '空内容',
        tags: nowArticle.tags
      });
      
      article.id = nowArticle.id;
      article.title = nowArticle.title;
      article.tags = nowArticle.tags;
      article.content = nowArticle.content;
      store.articleId = 0;
    } else {
      console.error('[调试] API 返回失败:', response.data.msg);
      ElMessageBox.alert(response.data.msg || "请求成功但数据异常", '结果');
    }
  }).catch((error) => {
    console.error('[严重错误] 加载文章失败详情:', {
      message: error.message,
      stack: error.stack,
      response: error.response ? {
        status: error.response.status,
        data: error.response.data
      } : '无响应数据',
      request: error.request ? '存在请求对象' : '无请求对象',
      config: {
        url: error.config?.url,
        method: error.config?.method,
        data: error.config?.data
      }
    });
    
    let errorMsg = "未知错误";
    if (error.response) {
      errorMsg = `HTTP ${error.response.status} 错误: ${error.response.data?.msg || '服务器返回错误'}`;
    } else if (error.request) {
      errorMsg = "请求已发送但无响应，请检查网络连接";
    } else {
      errorMsg = `请求配置错误: ${error.message}`;
    }
    
    ElMessageBox.alert(`加载失败：${errorMsg}`, '错误');
  }).finally(() => {
    loading.value = false;
  });
}

// 监听 store.articleId 变化
watch(() => store.articleId, loadArticle, { immediate: true })


//有可能导致不显示
const apikey = ref('9u4emcsp2r866o2emqp3iug7x96rxbidwznkctb0q33bkdz6')
const init = reactive({
    language: "zh_CN",
    placeholder: "在这里输入文字",
    plugins: ['image', 'code'],
    toolbar: 'image',
    images_file_types: 'jpg,jpeg,png,gif,bmp',
    images_upload_handler: image_upload_handler,
    convert_urls:false,
    
})



function publishArticle() {
    axios({
        method:'post',
        url:'/api/article/publishArticle?type='+type,
        data: article,
        timeout: 3000000
    }).then((response) => {
        //response.data代表后端服务器返回的json格式的数据
        ElMessageBox.alert(response.data, '结果')
    })
}



</script>
<template>
<el-row>
    <el-col :span="24">
        <h4>发布文章</h4>
    </el-col>
</el-row>

<el-row>
    <el-col :span="12">
        <el-input v-model="article.title" placeholder="请输入文章标题(必须)" clearable/>
    </el-col>
    <el-col :span="12">
        <el-input v-model="article.tags" :rows="1" type="textarea" placeholder="请输入文章标签，可以多行" />
    </el-col>
</el-row>
<el-col :span="24">
    <div id="editor">
        <editor v-model="article.content" :init="init" :api-key="apikey"/>
    </div>
</el-col>
<el-row>
    <el-col :span="24">
        <div align="right">
            <el-button @click="gotoArticleManage">返回列表</el-button>
            <el-button type="primary" @click="publishArticle">保存文章</el-button>
        </div>
    </el-col>
</el-row>
</template>

<style scoped>

</style>