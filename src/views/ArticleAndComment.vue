<script setup>
import Top from "@/components/Top.vue";
import { useRoute } from 'vue-router'
import {inject, reactive, ref, computed, onMounted} from 'vue'
import { ElMessageBox } from "element-plus";
import Comment from "@/components/Comment.vue";
const route = useRoute()
const axios = inject('axios')

let articleAndComment=reactive({
    "article":{"content":""},
    "comments":[]
})

//添加评论
const commentContent = ref('')
function submit() {
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
        ElMessageBox.alert("系统错误！", '结果')
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
});

//分页展示评论
const noMore = ref(false)
const loading = ref(false)
const disabled = computed(() => loading.value || noMore.value)
const load = () => {
    loading.value = true
    pageParams.page++
    axios({
        method: 'post',
        url: '/api/comment/getAPageCommentByArticleId?articleId='
        + route.params.articleId,
        data: pageParams,
        timeout: pageParams,
        timeout: 3000000
    }).then((response) =>{
        if (response.data.success) {
            let comments = response.data.map.comments
            if (comments != null && comments.length > 0) {
                for (let index = 0; index < comments.length; index++) 
                articleAndComment.comments.push(comments[index]);
            } else if (comments == null || comments.length == 0)
            noMore.value = true
        } else
            ElMessageBox.alert(articleAndComment.msg,'结果')
        loading.value = false
    }).catch((error) => {
        ElMessageBox.alert("系统错误！",'结果')
        loading.value = false
    })
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
         <ul v-infinite-scroll="load" :infinite-scroll-disabled="disabled" class="infinite-list">
             <li class="infinite-list-item">
                <el-row>
                    <el-col>
                        <el-input v-model="commentContent" :autosize="{ minRows: 4}"
                        type="textarea" placeholder="评论不能为空" minlength="2"/>
                    </el-col>
                </el-row>
                <el-row justify="end">
                    <el-col :xs="8" :sm="6" :md="4" >
                        <el-button @click="submit" type="primary" round style="margin-top: 5px;">提交评论</el-button>"
                    </el-col>
                </el-row>
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
</style>