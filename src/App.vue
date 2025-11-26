<script setup>
import { RouterView, useRouter } from 'vue-router'
import {provide} from "vue"
const router = useRouter()
router.push({name: 'home'})

const toAdminMain = () => {
  router.push( { name: 'adminMain'})
}
function toArticle(){
  router.push({name: 'articleAndComment', params: {articleId: 13}})
}
provide("toAdminMain", toAdminMain)
provide("toArticle", toArticle)
</script>

<template>
  <RouterView />
</template>

<style scoped>

</style>


