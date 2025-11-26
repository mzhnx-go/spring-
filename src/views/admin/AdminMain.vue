<script setup>
import { RouterView, useRouter } from 'vue-router';
import {reactive,provide } from 'vue';
import {View, DArrowRight} from '@element-plus/icons-vue'
const aside_state = reactive({collapse:false, width:"200px"})
function toggleCollapse() {
    aside_state.collapse = !aside_state.collapse
    if(aside_state.collapse) {
        aside_state.width="70px"
    }else{
        aside_state.width="200px"
    }

}

const router = useRouter()
// 跳转至编辑文章
function editArticle() {
  router.push({ name: 'publicArticle' })
}
provide("editArticle", editArticle)

// 跳转至管理文章（文章列表）
function gotoArticleManage() {
  router.push({ name: 'manageArticle' })
}
provide("gotoArticleManage", gotoArticleManage)
</script>

<template>
    <el-container>
      <el-aside width="aside_state.width">
        <el-row>
            <el-col class="left-top">
                <img src="@/assets/bloglogo.jpg" width="50" height="50" />
                <span v-if="!aside_state.collapse" class="big-text">MyBlog</span>
            </el-col>
        </el-row>
        <el-row>
            <el-col>
            <el-menu active-text-color="white" background-color="#545c64" text-color="#a7b1c2" router
                :collapse="aside_state.collapse"
                :collapse-transition="false">
                <el-menu-item>
                    <el-icon>
                        <odometer />
                    </el-icon>
                    <span>仪表盘</span>
                </el-menu-item>
                <el-menu-item index="/admin_Main/publish_article">
                    <el-icon>
                        <Edit />
                    </el-icon>
                    <span>发布文章</span>
                </el-menu-item>
                <el-menu-item index="/admin_Main/manage_article">
                    <el-icon>
                        <Memo />
                    </el-icon>
                    <span>文章管理</span>
                </el-menu-item>
                <el-menu-item>
                    <el-icon>
                        <Comment />
                    </el-icon>
                    <span>评论管理</span>
                </el-menu-item>
                <el-menu-item>
                    <el-icon>
                        <Filter />
                    </el-icon>
                    <span>分类/标签</span>
                </el-menu-item>
                <el-menu-item>
                    <el-icon>
                        <Setting />
                    </el-icon>
                    <span>系统设置</span>
                </el-menu-item>
            </el-menu>
            </el-col>
        </el-row>
      </el-aside>
      <el-main style="padding:0;">
        <el-row align="middle" style="height: 70px;" justify="space-between">
            <el-col :span="'4'">
                <span @click="toggleCollapse" class="toggle_menu">
                    <el-icon :size="30">
                        <Fold v-if="!aside_state.collapse" />
                        <Expand v-if="aside_state.collapse" />
                    </el-icon>
                    </span>
            </el-col>
            <el-col :span="1">
                <el-dropdown trigger="click" style="cursor: pointer;">
                    <span class="el-dropdown-link">
                        <img src="@/assets/me.jpg" class="img-circle"/>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item :icon="View" style="color:black">查看网站</el-dropdown-item>
                            <el-dropdown-item :icon="DArrowRight" style="color:black">用户注销</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </el-col>
        </el-row>
        <el-row>
            <el-col>
              <RouterView />
            </el-col>
        </el-row>
      </el-main>
    </el-container>
</template>

<style scoped>
.img-circle {
    border-radius: 50%;
    height: 36px;
    width: 36px;
}

.toggle-menu{
    margin-left: 5px;
    color: darkgray;
}
.toggle-menu:hover{
    color:black;
    cursor:pointer;
}

.big-text{
    margin-left: 10px;
    font-size: 28px;
    color: #2f353f;
}
.left-top {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 70px;
    background-color: #eee;
}

/* 在 <style scoped> 中增加以下样式 */
.el-menu--collapse .el-menu-item span {
  display: none;
}

.el-menu--collapse .el-menu-item .el-icon {
  margin: 0; /* 移除可能的左右边距影响居中 */
  display: flex;
  justify-content: center;
}


.el-menu-item {
  display: flex;
  align-items: center;
  height: 50px;
  line-height: 50px;
}
</style>


