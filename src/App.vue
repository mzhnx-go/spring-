<script setup>
import { RouterView, useRouter } from 'vue-router'
import {provide} from "vue"
const router = useRouter()
router.push({name: 'home'})

const toAdminMain = () => {
  router.push( { name: 'adminMain'})
}
function toArticle(articleId) {
  router.push({ 
    name: 'articleAndComment', 
    params: { articleId: articleId } 
  })
}

provide("toAdminMain", toAdminMain)
provide("toArticle", toArticle)
</script>

<template>
  <RouterView />
</template>

<style scoped>

</style>


