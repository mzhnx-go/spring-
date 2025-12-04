import { defineStore } from 'pinia'
import { reactive, ref } from 'vue'

export const useStore = defineStore('my', () => {
  const articleId = ref(0)
  const page = reactive({ "pageParams": null })

  const home = reactive({"page": 1})
  return { articleId, page, home }
})