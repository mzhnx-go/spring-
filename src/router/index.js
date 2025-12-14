import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import('../views/index.vue')
        },
        {
            path: "/admin_Main",
            name: "adminMain",
            component: () => import("../views/admin/AdminMain.vue"),
            children: [
                {
                    path: "publish_article",
                    name: "publicArticle",
                    component: () => import("../views/admin/PublishArticle.vue"),
                },
                {
                    path: "edit_article",
                    name: "editArticle",
                    component: () => import("../views/admin/EditArticle.vue")
                },
                {
                    path: "manage_article",
                    name: "manageArticle",
                    component: () => import("../views/admin/ManageArticle.vue")
                }
            ]
        },
        {
            path: '/article_comment/:articleId',
            name: 'articleAndComment',
            component: () => import('../views/ArticleAndComment.vue')
        },
        {
            path: '/search',
            name: 'search',
            component: () => import('../views/Search.vue')
        }
    ]
})
export default router