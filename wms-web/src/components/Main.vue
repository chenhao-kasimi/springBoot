<template>
  <!--改签-->
  <div>
    <div style="margin-bottom: 5px">
      <el-input v-model="name" placeholder="请输入名字" suffix-icon="el-icon-search" style="width: 200px; "
      @keyup.enter.native="loadPost">

      </el-input>
        <!--下拉选择框-->
        <el-select v-model="sex" filterable placeholder="请选择性别"  style="margin-left: 5px">
          <el-option
              v-for="item in sexs"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>

      <el-button type="primary" style="margin-left: 5px" @click="loadPost">查询</el-button>
      <el-button type="success" @click="resetParam">重置</el-button>
      <el-button type="primary" style="margin-left: 5px" @click="add">新增</el-button>
    </div>
  <el-table :data="tableData"
  :header-cell-style="{background:'#f2f5fc',color:'#555555'}"
            border
  >
    <el-table-column prop="id" label="ID" width="80" >
    </el-table-column>
    <el-table-column prop="username" label="用户名" width="180">
    </el-table-column>
    <el-table-column prop="name" label="姓名" width="180">
    </el-table-column>
    <el-table-column prop="age" label="年龄" width="80">
    </el-table-column>
    <el-table-column prop="sex" label="性别" width="80">
      <template slot-scope="scope">
        <el-tag
            :type="scope.row.sex===1? 'primary' : 'success'"
            disable-transitions>{{scope.row.sex=== 1 ? '男' : '女'}}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="roleId" label="权限" width="180">
      <template slot-scope="scope">
        <el-tag
            :type="scope.row.roleId===0? 'danger':(scope.row.roleId===1?'primary':'success')"
            disable-transitions>{{scope.row.roleId===0?'超级管理员':(scope.row.roleId===1?'管理员':'用户')}}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="phone" label="电话" width="180">
    </el-table-column>
    <el-table-column prop="operate" label="操作" >
      <template slot-scope="scope">
        <!--根据scope获取到需要编辑的对象的属性并显示到面板-->
      <el-button size="small" type="success" @click="edit(scope.row)" >编辑</el-button>
        <!-- 此时用的点击事件是confirm ，相比于click是点击后，点击弹出框确定才会进行-->
        <el-popconfirm
            title="这是一段内容确定删除吗？"
            @confirm="del(scope.row.id)"
            style="margin-left: 10px"
        >
          <el-button slot="reference" size="small" type="danger" >删除</el-button>
        </el-popconfirm>
      </template>
    </el-table-column>
  </el-table>
    <!--分页-->
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[2, 5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>

    <!--添加-->
    <el-dialog
        title="提示"
        :visible.sync="centerDialogVisible"
        width="30%"
        center>

      <!-- 表单 rulse设置规则，需要每个item标签都有prop属性-->
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-col :span="20">
            <el-input v-model="form.username"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="名字" prop="name">
          <el-col :span="20">
              <el-input v-model="form.name"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-col :span="20">
            <el-input v-model="form.password"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-col :span="20">
            <el-input v-model="form.age"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-col :span="20">
            <el-input v-model="form.phone"></el-input>
          </el-col>
        </el-form-item>

      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Main",
  data() {
    //提交前判断，value是否符合rule的值
    let checkAge = (rule, value, callback) => {
      if(value>150){
        callback(new Error('年龄输⼊过⼤'));
      }else{
        callback();
      }
    };
    //账号的唯一性验证
    let checkDuplicate =(rule,value,callback)=>{
      if(this.form.id){
        return callback();
      }
      this.$axios.get(this.$httpUrl+"/user/findByUsername?username="+this.form.username).then(res=>res.data).then(res=>{
        if(res.code!=200){
          //查询到会触发suc，返回值为200，则添加应该为！200；

          callback()
        }else{
          callback(new Error('账号已经存在'));
        }
      })
    }
    return {
      tableData: [],
      pageSize:10,
      pageNum:1,
      total:0,
      name:'',//获取输入框中名为name的数据
      sex:'',
      sexs:[
        {
          value:'1',
          label:'男'
        },{
          value: '0',
          label: '女'
        }
      ],
      centerDialogVisible:false, //提示框默认不打开,
      form:{
        id:'',
        username:'',
        name:'',
        password:'',
        age:'',
        sex:'',
        phone:'',
        roleId:'2'
      },
      rules:{
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 8, message: '长度在 3 到 8 个字符', trigger: 'blur' },
          {validator:checkDuplicate,trigger: 'blur'}
        ],
        name: [
          { required: true, message: '请输入名字', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 3, max: 8, message: '长度在 3 到 8 个字符', trigger: 'blur' }
        ],
        age: [
          {required: true, message: '请输⼊年龄', trigger: 'blur'},
          {min: 1, max: 3, message: '⻓度在 1 到 3 个位', trigger: 'blur'},
          {pattern: /^([1-9][0-9]*){1,3}$/,message: '年龄必须为正整数字',trigger: "blur"},
          {validator:checkAge,trigger: 'blur'}
        ],
        phone: [
          {required: true,message: "⼿机号不能为空",trigger: "blur"},
          {pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输⼊正确的⼿机号码", trigger:
                "blur"}
        ]
      },


    }
  },
  methods:{
    //************************************ methods ****************************************8//
    loadGet(){
      this.$axios.get(this.$httpUrl+'/user/list').then(res=>res.data).then(res=>{
        console.log(res)
      });
    },
    //加载页面获取列表数据
    loadPost(){
      this.$axios.post(this.$httpUrl+'/user/listPageC1',{
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          name:this.name,//获取数据中name的属性赋值给名为name的变量
          sex:this.sex
        }
      }).then(res=>res.data).then(res=>{
        console.log(res)
        if (res.code==200){
          this.tableData=res.data
          this.total=res.total
        }else {
          alert('获取失败')
        }
      });
    },

    //重置
    resetParam(){
        this.name=''
        this.sex=''
        this.loadPost()

    },
    //新增
    add(){
        this.centerDialogVisible=true
        this.$nextTick(()=>{
          this.resetForm()
      })
    },
    //删除
    del(id){
      console.log(id)
        this.$axios.post(this.$httpUrl+'/user/del?id='+id).then(res=>res.data).then(res=>{
          console.log(res)
          if (res.code==200){
            this.$notify({
              message: '操作成功',
              type: 'success'
            });
            this.centerDialogVisible=false
            this.loadPost()
          }else {
            this.$notify({
              message: '操作失败',
              type: 'error'
            });
          }
        });
      },
    //编辑
    edit(row){
      console.log(row)

      //展示模态窗口
      this.centerDialogVisible=true
      this.$nextTick(()=>{
        //对表单内容赋值
        //this.form=row
        this.form.id=row.id
        this.form.username=row.username
        this.form.name=row.name
        this.form.password=''
        this.form.age=row.age+''
        //页面展示的sex属性为string，而sex获取的为int，需要加上空字符转换成string
        this.form.sex=row.sex+''
        this.form.phone=row.phone
        this.form.roleId=row.roleId
      })
    },

    //分页
    handleSizeChange(val) {
        console.log(`每页 ${val} 条`);
        this.pageNum=1
      this.pageSize=val
      this.loadPost()
      },
    handleCurrentChange(val) {
        console.log(`当前页: ${val}`);
        this.pageNum=val
      this.loadPost()
      },

    //表单保存

    save(){
      //表单save前进行判断
      //编辑与修改的弹出框是一样，所以根据id判断是否是新的对象，新增对象不含id
      this.$refs.form.validate((valid) => {
        if (valid) {
          if(this.form.id){
            this.doEdit();
          }else{
            this.doSave();
          }

        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    doSave(){
      this.$axios.post(this.$httpUrl+'/user/save',this.form).then(res=>res.data).then(res=>{
        console.log(res)
        if (res.code==200){
          this.$notify({
            message: '操作成功',
            type: 'success'
          });
          this.centerDialogVisible=false
          this.loadPost()
        }else {
          this.$notify({
            message: '操作失败',
            type: 'error'
          });
        }
      });
    },
    doEdit(){
      this.$axios.post(this.$httpUrl+'/user/update',this.form).then(res=>res.data).then(res=>{
        console.log(res)
        if (res.code==200){
          this.$notify({
            message: '操作成功',
            type: 'success'
          });
          this.centerDialogVisible=false
          this.loadPost()
        }else {
          this.$notify({
            message: '操作失败',
            type: 'error'
          });
        }
      });
    },


    resetForm(){
      this.$refs.form.resetFields();
    },


  //********************************  methods   ***********************************8//
  },
  beforeMount() {
    //this.loadGet();
    this.loadPost();
  }
}
</script>

<style scoped>

</style>