<template>
  <div id="app">
    <!--路由跳转去掉index-->
    <!--<index></index>-->
    <router-view/>
  </div>
</template>

<script>

//import Index from "./components/Index";


export default {
  name: 'App',
  components: {
    //Index

  },
  data(){
    return{
      user:JSON.parse(sessionStorage.getItem('CurUser')),
    }
  },
  watch:{
    '$store.state.menu':{
      handler(val,old){
        if (!old&&this.user&&this.user.username){
          this.$store.commit("setRouter",val)
        }
      },
      immediate:true
    }
  }
}
</script>

<style>
#app {
  height: 100%;
}
</style>
