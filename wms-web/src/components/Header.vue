<template>
  <!--改签-->
  <div style="display: flex;line-height: 60px; font-size: 20px">
    <div style="margin-top: 8px">
      <!--cursor: pointer"是点击样式，移到此处会出现点击图标-->
      <i :class="icon" style="font-size: 20px;cursor: pointer" @click="collapse" ></i>
    </div>
    <div style="flex:1;text-align: center;font-size: 34px">
      <span>欢迎来到仓库管理系统</span>
    </div>
    <el-dropdown>
      <span>{{user.name}}</span>
      <i class="el-icon-arrow-down" style="margin-left: 15px "></i>
        <el-dropdown-menu slot="dropdown">
        <el-dropdown-item @click.native="toUser">个人中心</el-dropdown-item>
        <el-dropdown-item @click.native="logOut">退出登陆</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>

</template>

<script>
export default {
  name: "Header",
  data(){
    return{
      user:JSON.parse(sessionStorage.getItem('CurUser'))
    }
  },

  props:{
    icon:String
  },
  methods:{
    toUser(){
      console.log('to_user'),
      this.$router.push("/Home")
    },
    logOut(){
      console.log('logout')

      this.$confirm('您确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',  //确认按钮的文字显示
        type: 'warning',
        center: true, //文字居中显示

      })
          //点击确定
          .then(() => {
            this.$message({
              type:'success',
              message:'退出登录成功'
            })
            //跳转回登录界面，登录界面路径为斜杠
            this.$router.push("/")
            //清除session的数据
            sessionStorage.clear()
          })
          //取消退出
          .catch(() => {
            this.$message({
              type:'info',
              message:'已取消退出登录'
            })
          })

    },
    collapse(){
      //调用父组件中的doCollapse方法,后面可传参
      this.$emit('doCollapse')//
    }
  },
  created() {
    this.$router.push("/Home")
  }
}
</script>

<style scoped>

</style>