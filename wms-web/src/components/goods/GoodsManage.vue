<template>
    <div>
        <div style="margin-bottom: 5px;">
            <el-input v-model="name" placeholder="请输入物品名称" suffix-icon="el-icon-search" style="width: 200px;"
                      @keyup.enter.native="loadPost"></el-input>
            <el-select v-model="storage" placeholder="请选择所在仓库" style="margin-left: 5px">
              <el-option
                  v-for="item in storageData"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          <el-select v-model="goodstype" placeholder="请选择物品类别" style="margin-left: 5px">
            <el-option
                v-for="item in goodstypeData"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
          <el-button type="primary" style="margin-left: 5px;" @click="loadPost">查询</el-button>
          <el-button type="success" @click="resetParam">重置</el-button>

            <el-button type="primary" style="margin-left: 5px;" @click="add" v-if="user.roleId!=2">新增</el-button>
            <el-button type="primary" style="margin-left: 5px;" @click="inGoods" v-if="user.roleId!=2">入库</el-button>
            <el-button type="primary" style="margin-left: 5px;" @click="outGoods" v-if="user.roleId!=2">出库</el-button>
        </div>
        <el-table :data="tableData"
                  :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
                  border
                  highlight-current-row
                  @current-change="selectCurrentChange"
        >
            <el-table-column prop="id" label="ID" width="60" type="index">
            </el-table-column>

            <el-table-column prop="name" label="物品名称" width="180">
            </el-table-column>

            <el-table-column prop="storage" label="所在仓库" :formatter="formatStorage">
            </el-table-column>
          <el-table-column prop="goodstype" label="物品分类" :formatter="formatGoodstype">
<!--            <template slot-scope="scope">-->
<!--              <el-tag-->
<!--                    :type="scope.row.goodstype===1? 'primary' : 'success'"-->
<!--              disable-transitions>{{scope.row.goodstype===0 ? '计算机类' :-->
<!--                      (scope.row.goodstype===1 ? '会议类' :-->
<!--                          (scope.row.goodstype===2 ? '通讯类' :-->
<!--                              (scope.row.goodstype===3 ? '网络类' : '其他')))}}-->
<!--              </el-tag>-->
<!--            </template>-->
          </el-table-column>
          <el-table-column prop="materialcode" label="物料编码">
          </el-table-column>
          <el-table-column prop="brandmodel" label="品牌型号">
          </el-table-column>
          <el-table-column prop="count" label="数量">
          </el-table-column>
          <el-table-column prop="remark" label="备注">
          </el-table-column>

            <el-table-column prop="operate" label="操作" v-if="user.roleId!=2" >
                <template slot-scope="scope">
                    <el-button size="small" type="success" @click="mod(scope.row)">编辑</el-button>
                    <el-popconfirm
                            title="确定删除吗？"
                            @confirm="del(scope.row.id)"
                            style="margin-left: 5px;"
                    >
                        <el-button slot="reference" size="small" type="danger" >删除</el-button>
                    </el-popconfirm>
                </template>
            </el-table-column>

        </el-table>
        <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="pageNum"
                :page-sizes="[5, 10, 20,30]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total">
        </el-pagination>

        <el-dialog
                title="提示"
                :visible.sync="centerDialogVisible"
                width="30%"
                center>

            <el-form ref="form" :rules="rules" :model="form" label-width="80px">
                <el-form-item label="物品名称" prop="name">
                    <el-col :span="20">
                        <el-input v-model="form.name"></el-input>
                    </el-col>
                </el-form-item>

              <el-form-item label="所在仓库" prop="storage">
                <el-col :span="20">
                    <el-select v-model="form.storage" placeholder="请选择所在仓库" style="margin-left: 5px">
                      <el-option
                          v-for="item in storageData"
                          :key="item.id"
                          :label="item.name"
                          :value="item.id">
                      </el-option>
                    </el-select>
                </el-col>
              </el-form-item>

              <el-form-item label="物品分类" prop="goodstype">
                <el-col :span="20">
                  <el-select v-model="form.goodstype" placeholder="请选择物品类别" style="margin-left: 5px" >
                    <el-option
                      v-for="item in goodstypeData"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id">
                    </el-option>
                  </el-select>
                </el-col>
              </el-form-item>
              <el-form-item label="物料编码" prop="materialcode">
                <el-col :span="20">
                  <el-input v-model="form.materialcode"></el-input>
                </el-col>
              </el-form-item>
              <el-form-item label="品牌型号" prop="brandmodel">
                <el-col :span="20">
                  <el-input v-model="form.brandmodel"></el-input>
                </el-col>
              </el-form-item>
              <el-form-item label="数量" prop="count">
                <el-col :span="20">
                  <el-input v-model="form.count"></el-input>
                </el-col>
              </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-col :span="20">
                        <el-input type="textarea" v-model="form.remark"></el-input>
                    </el-col>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
    <el-button @click="centerDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="save">确 定</el-button>
  </span>
        </el-dialog>

<!--      入库的DialogVisible-->
      <el-dialog
          title="出入库"
          :visible.sync="inDialogVisible"
          width="30%"
          center>
        <el-dialog
            width="80%"
            title="用户选择"
            :visible.sync="innerVisible"
            append-to-body
        >
        <SelectUser @doSelectUser="doSelectUser"></SelectUser>
          <span slot="footer" class="dialog-f00ter"></span>
          <el-button @click="innerVisible=false">取 消</el-button>
          <el-button type="primary" @click="confirmUser">确 定</el-button>
        </el-dialog>

        <el-form ref="goodsForm" :rules="goodsRules" :model="goodsForm" label-width="80px">
<!--          因为入库需要选中，所以不需要form表单的物品名，所以去除prop，属性设置为readonly-->
          <el-form-item label="物品名称" >
            <el-col :span="20">
              <el-input v-model="goodsForm.goodsname" readonly></el-input>
            </el-col>
          </el-form-item>
          <el-form-item label="入库申请人" >
            <el-col :span="20">
              <el-input v-model="goodsForm.username" readonly @click.native="selectUser"></el-input>
            </el-col>
          </el-form-item>

<!--          <el-form-item label="物料编码" prop="materialcode">-->
<!--            <el-col :span="20">-->
<!--              <el-input v-model="goodsForm.materialcode"></el-input>-->
<!--            </el-col>-->
<!--          </el-form-item>-->
<!--          <el-form-item label="品牌型号" prop="brandmodel">-->
<!--            <el-col :span="20">-->
<!--              <el-input v-model="goodsForm.brandmodel"></el-input>-->
<!--            </el-col>-->
<!--          </el-form-item>-->
          <el-form-item label="数量" prop="count">
            <el-col :span="20">
              <el-input v-model="goodsForm.count"></el-input>
            </el-col>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-col :span="20">
              <el-input type="textarea" v-model="goodsForm.remark"></el-input>
            </el-col>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
    <el-button @click="inDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="doInGoods">确 定</el-button>
  </span>
      </el-dialog>
    </div>
</template>

<script>
import SelectUser from "@/components/user/SelectUser";
    export default {
        name: "GoodsManage",
      components: {SelectUser},
      data() {
            //在data里面加入规则
            let checkCount = (rule, value, callback) => {
              if(value>9999){
                callback(new Error('数量输⼊过⼤'));
              }else{
                callback();
              }
            };
            return {
                user:JSON.parse(sessionStorage.getItem('CurUser')),//获取登录用户信息
                storageData:[],
                goodstypeData:[],
                //name，storage，goodstype为输入框选中值
                name:'',
                storage: '',
                goodstype: '',
                tableData: [],
                pageSize:10,
                pageNum:1,
                total:0,
                centerDialogVisible:false,
                inDialogVisible:false,
                innerVisible:false,
                tempUser:{},
                currentRow:{},

                form:{
                    id:'',
                    name:'',
                    storage:'',
                    goodstype:'',
                    materialcode:'',
                    brandmodel:'',
                    count:'',
                    remark:'',

                },
                goodsForm:{
                  goods:'',
                  goodsname:'',
                  count:'',
                  username:'',
                  userid:'',
                  adminId:'',
                  remark:'',
                  action:'1',
                  },
              goodsRules:{

              },

                rules: {
                    name: [
                        {required: true, message: '请输入物品名称', trigger: 'blur'}
                    ],
                  goodstype: [
                    {required: true, message: '请选择物品类别', trigger: 'blur'}
                  ],
                  storage: [
                    {required: true, message: '请选择所属仓库', trigger: 'blur'}
                  ],
                    count: [
                      {required: true, message: '请输⼊数量', trigger: 'blur'},
                      {pattern: /^([1-9][0-9]*){1,4}$/,message: '数量必须为正整数字',trigger: "blur"},
                      {validator:checkCount,trigger: 'blur'}
                    ],
                },
            }

        },
        methods:{
          doSelectUser(val){
          //从selectUser中点击后selectCurrentChange方法回传的值
            console.log(val)
            //临时存储val，点击确认后执行confirmUser进行赋值，取消后不赋值
            this.tempUser=val
          },
          confirmUser(){//第二内层点击确认后执行

              this.goodsForm.username=this.tempUser.name
              this.goodsForm.userid=this.tempUser.id
              this.innerVisible=false
          },
          selectCurrentChange(val){
            this.currentRow=val;
          },
          formatStorage(row){
            let temp=this.storageData.find(item=>{
              //对storageData中的item遍历（预加载已经将storage的list保存，里面的item每项是一个storage表）
              //遍历后，拿到item的id，与row中的storage对应，对应成功则返回temp和temp的名字（即item中的name值）
              return item.id == row.storage
            })
            //如果有值，则取出值里面的name
            return temp && temp.name
          },
          formatGoodstype(row){
            let temp =  this.goodstypeData.find(item=>{
              return item.id == row.goodstype
            })

            return temp && temp.name
          },
            resetForm() {
                this.$refs.form.resetFields();
            },
          resetInForm() {
            this.$refs.goodsForm.resetFields();
          },
          selectUser(){
            this.innerVisible=true
          },
            del(id){
                console.log(id)
                this.$axios.post(this.$httpUrl+'/goods/del?id='+id).then(res=>res.data).then(res=>{
                    console.log(res)
                    if(res.code==200){

                        this.$message({
                            message: '操作成功！',
                            type: 'success'
                        });
                      this.centerDialogVisible=false
                        this.loadPost()
                    }else{
                        this.$message({
                            message: '操作失败！',
                            type: 'error'
                        });
                    }

                })
            },
            mod(row){
                this.centerDialogVisible = true
                this.$nextTick(()=>{
                    //赋值到表单
                    this.form.id = row.id
                    this.form.name = row.name
                    this.form.storage = row.storage
                    this.form.goodstype=row.goodstype
                    this.form.materialcode = row.materialcode
                    this.form.brandmodel = row.brandmodel
                    this.form.count = row.count
                    this.form.remark = row.remark
                })
            },
            add(){
                this.centerDialogVisible = true
                this.$nextTick(()=>{
                    this.resetForm()
                    //因为表单中没有prop为id的值，点击编辑后不进行修改而点击新增，
                    // 会导致id没有重置，使根据id判断的新增变为编辑
                    //需要手动将id转为空
                    this.form.id =''
                })

            },
          inGoods(){
            // 选择再点击入库，根据curentrow是否有id值判断是否选中
            if (!this.currentRow.id){
              alert("请选择记录")
              return;
            }
            this.inDialogVisible = true
            this.$nextTick(()=>{
              this.resetInForm()
              //因为表单中没有prop为id的值，点击编辑后不进行修改而点击新增，
              // 会导致id没有重置，使根据id判断的新增变为编辑
              //需要手动将id转为空
              //出入库不需要this.form.id =''
            })

            //获取goodsForm的值
            //currentRow中的name为展示出来的goodsForm表单中的goodsname
            this.goodsForm.goodsname=this.currentRow.name;
            ////currentRow中的id为展示出来的goodsForm表单中的goods
            this.goodsForm.goods=this.currentRow.id;
            //adminId可以从登录用户获取到
            //
            this.goodsForm.adminId=this.user.id;
            this.goodsForm.action='2'

          },
          outGoods(){
            // 选择再点击入库，根据curentrow是否有id值判断是否选中
            if (!this.currentRow.id){
              alert("请选择记录")
              return;
            }
            this.inDialogVisible = true
            this.$nextTick(()=>{
              this.resetInForm()
              //因为表单中没有prop为id的值，点击编辑后不进行修改而点击新增，
              // 会导致id没有重置，使根据id判断的新增变为编辑
              //需要手动将id转为空
              //出入库不需要this.form.id =''
            })

            //获取goodsForm的值
            //currentRow中的name为展示出来的goodsForm表单中的goodsname
            this.goodsForm.goodsname=this.currentRow.name;
            ////currentRow中的id为展示出来的goodsForm表单中的goods
            this.goodsForm.goods=this.currentRow.id;
            //adminId可以从登录用户获取到
            //
            this.goodsForm.adminId=this.user.id;
            this.goodsForm.action=2
          },
            doSave(){
                this.$axios.post(this.$httpUrl+'/goods/save',this.form).then(res=>res.data).then(res=>{
                    console.log(res)
                    if(res.code==200){

                        this.$message({
                            message: '操作成功！',
                            type: 'success'
                        });
                        this.centerDialogVisible = false
                        this.loadPost()
                        this. resetForm()
                    }else{
                        this.$message({
                            message: '操作失败！',
                            type: 'error'
                        });
                    }

                })
            },
            doInGoods(){
            this.$axios.post(this.$httpUrl+'/record/save',this.goodsForm).then(res=>res.data).then(res=>{
              console.log(res)
              if(res.code==200){

                this.$message({
                  message: '操作成功！',
                  type: 'success'
                });
                this.inDialogVisible= false
                this.loadPost()
                this.resetForm()
              }else{
                this.$message({
                  message: '操作失败！',
                  type: 'error'
                });
              }

            })
          },
            doMod(){
                this.$axios.post(this.$httpUrl+'/goods/update',this.form).then(res=>res.data).then(res=>{
                    console.log(res)
                    if(res.code==200){

                        this.$message({
                            message: '操作成功！',
                            type: 'success'
                        });
                        this.centerDialogVisible = false
                        this.loadPost()
                        this. resetForm()
                    }else{
                        this.$message({
                            message: '操作失败！',
                            type: 'error'
                        });
                    }

                })
            },
            save(){
                this.$refs.form.validate((valid) => {
                    if (valid) {
                        if(this.form.id){
                            this.doMod();
                        }else{
                            this.doSave();
                        }
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });

            },
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
            //重置，清除选择框并重新加载界面
            resetParam(){
                this.name='',
                this.storage='',
                this.goodstype='',
                this.loadPost()
            },
            //列表查询，根据选择框的内容查询列表，默认加载为空
            loadPost(){
                this.$axios.post(this.$httpUrl+'/goods/listPage',{
                    pageSize:this.pageSize,
                    pageNum:this.pageNum,
                    param:{
                        name:this.name,
                        //storage和goods设置为的是id，后端用param.get接收需要强转成string会报错
                        //需要传参数时加空字符串使赋值升为string
                        storage: this.storage+'',
                        goodstype: this.goodstype+''

                    }
                }).then(res=>res.data).then(res=>{
                    console.log(res)
                    if(res.code==200){
                        this.tableData=res.data
                        this.total=res.total
                    }else{
                        alert('获取数据失败')
                    }

                })
            },

            //获取所有的仓库名称以备使用,在预装载前执行，所以需要加在beforeMount中
            loadStorage(){
              this.$axios.get(this.$httpUrl+'/storage/list').then(res=>res.data).then(res=>{
                console.log(res)
                if(res.code==200){
                  //获取之后将获取到的数据res.data,赋值给this.storageDate,并在数据的return中设置storageData保存备用
                  //
                  this.storageData=res.data
                }else{
                  alert('获取数据失败')
                }

              })
            },
          loadGoodstype(){
            this.$axios.get(this.$httpUrl+'/goodstype/list').then(res=>res.data).then(res=>{
              console.log(res)
              if(res.code==200){
                this.goodstypeData=res.data
              }else{
                alert('获取数据失败')
              }

            })
          }
        },
        //装载，会在页面执行以前预装载数据
        beforeMount() {
          this.loadStorage()
          this.loadGoodstype()
          this.loadPost()
        }
    }
</script>

<style scoped>

</style>