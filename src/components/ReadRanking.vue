<script setup>
import { computed } from 'vue'

const props = defineProps({
  articles: {
    type: Array,
    required: true,
    default: () => []
  }
})

const rankings = computed(() => {
  if (!props.articles || props.articles.length === 0) return [];
  
  return props.articles.map((article, index) => 
    `${index + 1}、${article.title}(${article.hits})`
  )
})
</script>

<template>
    <div class="read-ranking">
        <h3>阅读排行榜</h3>
        <div v-if="rankings.length === 0" class="empty">暂无数据</div>
        <el-row v-else v-for="ranking in rankings">
            <el-col :span="24">
                {{ ranking }} <!-- 添加双大括号 -->
                <el-divider class="divider" />
            </el-col>
        </el-row>
    </div>
</template>

<style scoped>
.divider {
    margin-top: 8px;
    margin-bottom: 8px;
}

a:hover {
    color: #10D07A;
}

.title {
    color: black;
    font-size: 15px;
}
</style>