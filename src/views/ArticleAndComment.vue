<script setup>
import Top from "@/components/Top.vue";
import { useRoute } from 'vue-router'
import {inject, reactive, ref, computed, onMounted,watch} from 'vue'
import { useStore } from '@/stores/my' // 导入store（路径和你之前的Top.vue一致）
import { storeToRefs } from 'pinia' // 导入storeToRefs（pinia必备）
import { ElMessageBox } from "element-plus";
import Comment from "@/components/Comment.vue";
const route = useRoute()
const axios = inject('axios')
const store = useStore()

const toLogin = inject("toLogin", () => {
  console.error("toLogin function not provided!")
})
const user = computed(() => store.user.user)
const perArticlePromptShown = ref({})
const currentArticleId = ref(route.params.articleId || '')
const isPrompting = ref(false) 
let articleAndComment=reactive({
    "article":{"content":""},
    "comments":[]
})

//添加评论
const commentContent = ref('')
function submit() {

    if (!user.value) {
    handleNotLoggedIn()
    return
  }
  
    axios({
        method: 'post',
        url: '/api/comment/insert',
        data: {
            "articleId": route.params.articleId,
            "content": commentContent.value
        },
        timeout: 3000000
    }).then((response) => {
        if (response.data.success) {
            articleAndComment.comments.unshift(response.data.map.comment);
            ElMessageBox.alert(response.data.msg,'结果', {
                confirmButtonText: '确定',
                callback: (action) => {
                    commentContent.value = " "
                },
            })
        } else {
            ElMessageBox.alert(response.data.msg, '结果')
        }
    }).catch((error) => {
       // 完整处理未登录情况
    if (!error.response || error.response.status === 401) {
        if (!isPrompting.value) handleNotLoggedIn()
    } else {
      ElMessageBox.alert("系统错误！", '结果')
    }
  })
    }



let pageParams = {"page": 1, "rows" : 2}
//打开新闻时，获取新闻和第一页评论

axios({
    method: 'post',
    url: '/api/article/getArticleAndCommentByArticleId?articleId=' 
    + route.params.articleId,
    data: pageParams,
    timeout: 3000000
}).then((response) => {
    if (response.data.success && response.data.map.article) {
        // 修复图片路径函数
        const fixImagePaths = (content) => {
            return content
                .replace(/src="api\//g, 'src="/api/')   // 修复无斜杠的api路径
                .replace(/src='api\//g, "src='/api/")   // 修复单引号情况
                .replace(/src="\/api\//g, 'src="/api/') // 修复已有斜杠的情况
                .replace(/src='\/api\//g, "src='/api/");
        };
        
        // 应用路径修复
        articleAndComment.article = {
            ...response.data.map.article,
            content: fixImagePaths(response.data.map.article.content)
        };
        articleAndComment.comments = response.data.map.comments;
    } else {
        ElMessageBox.alert(response.data.success ? "无文章！" : response.data.msg, '结果');
    }
}).catch((error) => { 
    console.error('获取文章失败：', error.message)
    // 避免401重复触发弹窗
    if (error.response?.status !== 401) {
      ElMessageBox.alert("加载文章失败！", '结果');
    }
});


//分页展示评论
// 分页展示评论
const noMore = ref(false)
const loading = ref(false)
const disabled = computed(() => loading.value || noMore.value)

// 完整的load函数（核心优化版）
const load = () => {
    console.log('【load函数触发】', new Date().getTime());
    if (loading.value || noMore.value) {
        console.log('【拦截】loading=true 或 noMore=true，终止加载');
        return;
    }

    loading.value = true;
    const targetPage = pageParams.page + 1;
    console.log('【请求参数】页码：', targetPage, '，每页条数：', pageParams.rows);

    axios({
        method: 'post',
        url: '/api/comment/getAPageCommentByArticleId?articleId=' + route.params.articleId,
        data: {
            page: targetPage,
            rows: pageParams.rows
        },
        timeout: 5000
    }).then((response) => {
        if (response.data.success) {
            // 核心修复：后端返回的是comment（单数），不是comments（复数）
            const comments = response.data.map?.comment || []; 
            console.log('【分页数据】第', targetPage, '页评论数：', comments.length);

            if (comments.length > 0) {
                articleAndComment.comments.push(...comments);
                pageParams.page = targetPage;
                
            } else {
                noMore.value = true;
                
            }
        } else {
            ElMessageBox.alert(response.data.msg, '加载失败');
            console.error('【后端失败】', response.data.msg);
        }
    }).catch((error) => {
        console.error('【请求异常】', error);
        if ((!error.response || error.response.status === 401) && !isPrompting.value) {
            if (error.message.includes('Network Error')) {
                ElMessageBox.alert('跨域请求失败，请检查后端配置！', '错误');
                return;
            }
            handleNotLoggedIn();
        } else if (error.response?.status !== 401) {
            ElMessageBox.alert('系统错误！', '错误');
        }
    }).finally(() => {
        loading.value = false;
      
    });
}

const canComment = ref(false)
watch(
  () => user.value,
  (newUser) => {
    // 只要用户已登录就可以评论
    canComment.value = !!newUser
  },
  { immediate: true }
)



function handleNotLoggedIn() {
  // 强制阻止：只要满足任一条件就返回
  if (
    perArticlePromptShown.value[currentArticleId.value] || 
    isPrompting.value ||
    document.querySelector('.el-message-box__wrapper') // 检测弹窗DOM是否存在
  ) {
    return
  }
  perArticlePromptShown.value[currentArticleId.value] = true
  isPrompting.value = true

  // 延迟执行，避免同步触发重复调用
  setTimeout(() => {
    ElMessageBox.confirm(
      '请先登录才能评论', 
      '提示', 
      {
        confirmButtonText: '去登录',
        cancelButtonText: '取消',
        distinguishCancelAndClose: true,
        type: 'info',
        // 新增：禁止点击空白处关闭（减少误触发）
        closeOnClickModal: false
      }
    ).then(() => {
      toLogin()
    }).catch((err) => {
      // 明确捕获取消/关闭类型，仅重置状态
      if (err === 'cancel' || err === 'close') {
        perArticlePromptShown.value[currentArticleId.value] = false
      }
    }).finally(() => {
      isPrompting.value = false
    })
  }, 100);
}

</script>
<template>
    <el-affix>
        <Top />
    </el-affix>

    <el-row>
        <el-col :span="14" :offset="5">
            <div v-html="articleAndComment.article.content"></div>
        </el-col>
        </el-row>
        <el-row style="background-color: #f7f7f7;">
        <el-col :span="14" :offset="5">
         <ul v-infinite-scroll="load" :infinite-scroll-disabled="disabled" infinite-scroll-distance="10" 
            infinite-scroll-immediate="true" class="infinite-list">
             <li class="infinite-list-item" v-if="canComment">
                
                <el-row>
                    <el-col>
                        <el-input v-model="commentContent" :autosize="{ minRows: 4}"
                        type="textarea" placeholder="评论不能为空" minlength="2"/>
                    </el-col>
                </el-row>
                <el-row justify="end">
                    <el-col :xs="8" :sm="6" :md="4" >
                        <el-button @click="submit" type="primary" round style="margin-top: 5px;">提交评论</el-button>
                    </el-col>
                </el-row>
             </li>
                <li class="infinite-list-item" v-else>
                    <div class="login-prompt">
                        请先 <a @click="handleNotLoggedIn" style="color: #10D07A; cursor: pointer;">登录</a> 后评论
                    </div>
                    </li>
            <li v-for="comment in articleAndComment.comments" :key="comment.id" class="infinite-list-item">
                <Comment :comment="comment"></Comment>
            </li>

            
        </ul>
        <p v-if="loading">加载中...</p>
        <p v-if="noMore" class="end-comment">已显示全部评论。</p>
        </el-col>
    </el-row>
</template>
<style scoped>
.infinite-list {
    padding: 0;
    margin: 0;
    list-style: none;
}
.infinite-list-item {
    background: #fff;
    margin-bottom: 10px;
    margin-top: 10px;
    padding: 20px;
}

.login-prompt {
  background: #fff;
  margin-bottom: 10px;
  margin-top: 10px;
  padding: 20px;
  text-align: center;
  color: #999;
}
.login-prompt a {
  font-weight: bold;
}
</style>