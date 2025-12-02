<script setup>
// 引入工具函数（@ 表示 src 目录，确保路径正确）
import dateFormat from "@/js/tools.js";
import { ref } from 'vue';

// 定义组件属性（建议指定类型，增强类型提示）
const props = defineProps({
  article: {
    type: Object,
    required: true, // 标记为必填属性
    default: () => ({}) // 默认值避免报错
  }
});

// el-space 间距（已绑定到组件）
const size = ref(40);
</script>

<template>
  <el-row>
    <!-- 响应式图片列 -->
    <el-col :sm="24" :md="11">
      <el-image :src="props.article.thumbnail" fit="cover"></el-image> <!-- 补充 fit 属性优化图片显示 -->
    </el-col>

    <!-- 间距列（sm 屏幕隐藏） -->
    <el-col :sm="0" :md="12"></el-col>

    <!-- 分类和发布时间 -->
    <el-row align="middle">
      <el-col>
        <span id="categories" class="category-height" v-html="props.article.categories"></span>
        <span class="category-height" v-html="'发布于' + dateFormat(props.article.created, 'yyyy-MM-dd')"></span>
      </el-col>
    </el-row>

    <!-- 标题和摘要（已绑定间距） -->
    <el-space :size="size" direction="vertical"> <!-- 明确方向为垂直排列 -->
      <el-row align="middle">
        <el-col>
          <span class="title" v-html="props.article.title"></span>
        </el-col>
      </el-row>
      <el-row align="middle">
        <el-col>
          <!-- 截取摘要，空值处理避免报错 -->
          <span v-html="props.article.content?.substring(0, 90) || '暂无摘要'"></span>
        </el-col>
      </el-row>
    </el-space>

    <!-- 疑似笔误的 "1"，建议替换为实际功能（如链接） -->
    <el-row>
      <span>1</span> <!-- 若为链接，建议改为 <el-link> 组件 -->
    </el-row>
  </el-row>

  <!-- 分割线 -->
  <el-divider />
</template>

<style scoped>
/* 分类 hover 效果 */
#categories:hover {
  color: #10D07A;
  cursor: pointer; /* 补充鼠标指针，提升交互体验 */
}

/* 行高样式 */
.category-height {
  line-height: 40px;
  margin-right: 16px; /* 补充间距，避免文字挤在一起 */
}

/* 标题样式 */
.title {
  color: #0f9ae0;
  font-size: 20px;
  font-weight: 600; /* 补充加粗，突出标题 */
  line-height: 40px;
}
</style>