import VueRouter from 'vue-router';

const routes =[
    {
        path:'/',
        name:'login',
        component:()=>import('../components/Login')
    },
    {
    //账号密码成功时跳转到index
        path: '/Index',
        name: 'index',
        component: () => import('../components/Index'),
        children:[
                {
                //个人中心界面是在index界面中，属于index的子路由
                path: '/Home',
                name: 'Home',
                meta:{
                    //之后会用到
                    title:'首页'
                },
                component: () => import('../components/Home')
            },
            // {
            //     //个人中心界面是在index界面中，属于index的子路由
            //     path: '/Admin',
            //     name: 'admin',
            //     meta:{
            //         //之后会用到
            //         title:'管理员管理'
            //     },
            //     component: () => import('../components/Admin/AdminManage.vue')
            // },
            // {
            //     //个人中心界面是在index界面中，属于index的子路由
            //     path: '/User',
            //     name: 'user',
            //     meta:{
            //         //之后会用到
            //         title:'用户管理'
            //     },
            //     component: () => import('../components/User/UserManage.vue')
            // },
        ]
    }
]
const router=new VueRouter({
    mode:'history',//去除路由路径的#，更美观
    routes
})
//由于最开始设置了index,user,home三条路由，由因为动态路由增加这三条路由，会出现路由重复的警告
export function resetRouter() {
    router.matcher = new VueRouter({
        mode:'history',
        routes: []
    }).matcher
}

//路由错误清除
const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (to) {
    return VueRouterPush.call(this, to).catch(err => err)
}

export default router;//设置完毕后去main.js去注册