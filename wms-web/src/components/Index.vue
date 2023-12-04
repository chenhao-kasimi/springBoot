<!--改签-->
<template>
  <el-container style="height: 100%; border: 1px solid #eee">
    <el-aside :width="aside_width" style="height:100vh; background-color: rgb(238, 241, 246);margin-left: -1px" >
      <Aside :isCollapse="isCollapse"></Aside>
    </el-aside>

    <el-container style="height: 100%">
      <el-header style="text-align: right; font-size: 12px;height: 100%;border-bottom: #1e0000 1px solid">
        <!-- 定义一个doCollapse函数名，等号后面表示的是调用的函数体-->
        <Header @doCollapse="doCollapse" :icon="icon"></Header>
      </el-header>

      <el-main style="height: 100% ;padding: 1px" >
<!--        <Main></Main>-->
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import Aside from "@/components/Aside";
import Header from "@/components/Header";
// import Main from "@/components/Main";
export default {
  name: "Index",
  components: {Header, Aside},
  data(){
    return {
      isCollapse:false,
      aside_width:'200px',
      icon:'el-icon-s-fold'
    }
  },
  methods:{
    doCollapse(){
      this.isCollapse=!this.isCollapse
      if(!this.isCollapse){
        this.aside_width='200px'
        this.icon='el-icon-s-fold'
      }else{
        this.aside_width='64px'
        this.icon='el-icon-s-unfold'
      }
    }
  }
}
</script>

<style scoped>
.el-header {
  #background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}
.el-main{
  margin: 5px;
}
.el-aside {
  color: #333;
}
</style>