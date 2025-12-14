<script setup>
import Top from "@/components/Top.vue";
import { reactive ,inject} from 'vue'
import { ElMessageBox } from 'element-plus'
const data=reactive({ //分页条件查询文章的参数
    "articleCondition": { "title":"", "startDate": "", "endDate":""},
    "pageParams": { "page": 1,  "rows": 5 ,  "total":0}
})
let myData = reactive({ //查询结果
    "articleVOs": [],
})

const axios = inject('axios')

function search(){
    axios({
        method:'post',
        url:'/api/article/articleSearch',
        data:data
    }).then((response) => {
        if (response.data.success) {
            myData.articleVOs=response.data.map.articleVOs //符合条件的文章信息
            myData.pageParams=response.data.map.pageParams //返回结果中包含total
        }else{
            ElMessageBox.alert(response.data.msg, '结果')
        }
    }).catch((error) => { //请求失败返回的数据
        ElMessageBox.alert("系统错误！", '结果')
    })
}
</script>

<template>
    <el-affix><Top/></el-affix>
    
    <el-row justify="center" style="margin-top:30px"> <!-- 查询条件 -->
        <el-col :span="12">
            <el-input v-model="data.articleCondition.title"
                placeholder="文章标题" title="文章标题" clearable />
        </el-col>
    </el-row>
    
    <el-row justify="center" style="margin-top:15px">
        <el-col :span="12">
            <el-space :size="40"> <!-- element-plus的日期组件 -->
                <el-date-picker value-format="YYYY-MM-DD" 
                    v-model="data.articleCondition.startDate"
                    type="date" placeholder="起始日期" title="起始日期"/>
                <el-date-picker value-format="YYYY-MM-DD"
                    v-model="data.articleCondition.endDate"
                    type="date" placeholder="结束日期" title="结束日期"/>
                <el-button type="primary" @click="search">开始查询</el-button>
            </el-space>
        </el-col>
    </el-row>

<el-row>
    <el-col :offset="1" :span="24"><h4 style="margin-left: 10px;">查询结果</h4></el-col>
</el-row>
<el-row>
</el-row>
<el-row>
    <el-col :span="1"></el-col>
    <el-col :span="22">
        <el-table :data="myData.articleVOs" stripe border style="width: 100%">
            <el-table-column prop="categories" label="所属分类" width="150" />
            <el-table-column label="文章标题" width="800">
                <template #default="scope">
                    <router-link :to="{path:'/article_comment/'+scope.row.id}"
                        style="text-decoration: none;" > <!-- 跳转到文章详情 -->
                        {{scope.row.title}}
                    </router-link>
                </template>
            </el-table-column>
            <el-table-column prop="created" label="发布时间" width="170" />
        </el-table>
    </el-col>
    <el-col :span="1"></el-col>
</el-row>
</template>