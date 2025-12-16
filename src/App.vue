<script setup>
import { RouterView, useRouter } from 'vue-router'
import {provide} from "vue"
const router = useRouter()
router.push({name: 'home'})

const toHome = () => {
  router.push( { name: 'home'})
}

const toAdminMain = () => {
  router.push( { name: 'adminMain'})
}
function toArticle(articleId) {
  router.push({ 
    name: 'articleAndComment', 
    params: { articleId: articleId } 
  })
}

function toLogin() {
  router.push({ name: 'login' })
}

provide("toAdminMain", toAdminMain)
provide("toArticle", toArticle)
provide("toLogin", toLogin)
provide("tohome", toHome)
</script>

<template>
  <RouterView />
</template>

<style scoped>

</style>


