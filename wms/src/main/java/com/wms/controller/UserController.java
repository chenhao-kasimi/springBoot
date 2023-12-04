package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Menu;
import com.wms.entity.User;
import com.wms.service.MenuService;
import com.wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wms
 * @since 2023-02-09
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    //登录
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        //eq方法判断User的getUsername方法，和username参数是否相同，以列表的方式传回来
        List list = userService.lambdaQuery().eq(User::getUsername,user.getUsername()).eq(User::getPassword,user.getPassword()).list();

        //list不为空时代表查询到用户的对象
        if (list.size()>0){
            //将获取到的列表强转为user对象以备使用
            User user1 = (User)list.get(0);

            //通过user对象获取到该用户可操作的菜单menu
            List MenuList = menuService.lambdaQuery().like(Menu::getMenuright, user1.getRoleId()).list();

            //返回数据时需要同时传参user1，与menuList，所以用hashmap更好
            HashMap res=new HashMap();
            res.put("user",user1);
            res.put("menu",MenuList);

            return Result.suc(res);

        }
        //执行到此处时list为空，直接执行fail
        return Result.fail();
    }
    //列表
    @GetMapping("/list")
    public List<User> list(){
        return userService.list();
    }

    //账号检验
    @GetMapping("/findByUsername")
    public Result findByUsername(@RequestParam String username){
        //eq方法判断User的getUsername方法，和username参数是否相同，以列表的方式传回来
        List<User> list = userService.lambdaQuery().eq(User::getUsername, username).list();
        //查询到会触发suc
        return list.size()>0?Result.suc(list):Result.fail();
    }

    //新增
    @PostMapping("/save")
    public Result save(@RequestBody User user){
        return userService.save(user)?Result.suc():Result.fail();
    }

    //删除
    @PostMapping("/del")
    public Result del(@RequestParam String id){
        return userService.removeById(id)?Result.suc():Result.fail();
    }
    //更新
    @PostMapping("/update")
    public Result update(@RequestBody User user){
        return userService.updateById(user)?Result.suc():Result.fail();
    }

    //修改
    @PostMapping("/mod")
    public boolean mod(@RequestBody User user){
        return userService.updateById(user);
    }

    //新增或修改
    @PostMapping("/saveOrMod")
    public boolean saveOrMod(@RequestBody User user){
        return userService.saveOrUpdate(user);
    }

    //新增或修改
    @GetMapping("/delete")
    public boolean delete(Integer id){
        return userService.removeById(id);
    }

    //查询（模糊查询，完全匹配）
    @PostMapping("/listP")
    public Result listP(@RequestBody User user){
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        // lambdaQueryWrapper.eq();为完全匹配
        // lambdaQueryWrapper.like();为模糊查询
        if(StringUtils.isNotBlank(user.getName())){
            lambdaQueryWrapper.like(User::getName,user.getName());
        }
        return Result.suc(userService.list(lambdaQueryWrapper));
    }

    @PostMapping("/listPage")
    public List<User> listPage(@RequestBody QueryPageParam Query){
//        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
//        // lambdaQueryWrapper.eq();为完全匹配
//        // lambdaQueryWrapper.like();为模糊查询
//        lambdaQueryWrapper.eq(User::getName,user.getName());
//        return userService.list(lambdaQueryWrapper);
        //首先new一个page类，两个参数分别是当前页数，和当前页所展现的个数
        Page<User> page = new Page(Query.getPageNum(), Query.getPageSize());
        String name = (String)Query.getParam().get("name");
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
//        // lambdaQueryWrapper.eq();为完全匹配
//        // lambdaQueryWrapper.like();为模糊查询
        lambdaQueryWrapper.like(User::getName,name);
        IPage result = userService.page(page, lambdaQueryWrapper);
        System.out.println("result"+result.getTotal());

        return result.getRecords();
    }

    @PostMapping("/listPageC")//没用lambdaQueryWrapper进行模糊查询
    public List<User> listPageC(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String name = (String)param.get("name");
//        String sex = (String)param.get("sex");
//        String roleId = (String)param.get("roleId");

        Page<User> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        //if(StringUtils.isNotBlank(name) && !"null".equals(name)){
        lambdaQueryWrapper.like(User::getName,name);


        IPage result = userService.pageCC(page,lambdaQueryWrapper);

        System.out.println("listPageC的result="+result.getTotal());

        return result.getRecords();
    }

    //返回前端数据封装的自定义page
    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String name = (String)param.get("name");
        String sex = (String)param.get("sex");
        String roleId = (String)param.get("roleId");

        Page<User> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(name) && !"null".equals(name)) {
            lambdaQueryWrapper.like(User::getName, name);
        }
        if(StringUtils.isNotBlank(sex) ) {
            lambdaQueryWrapper.like(User::getSex, sex);
        }
        if(StringUtils.isNotBlank(roleId) ) {
            lambdaQueryWrapper.like(User::getRoleId, roleId);
        }

        System.out.println(name);
        System.out.println("lambdaQueryWrapper为"+lambdaQueryWrapper);

        IPage result = userService.pageCC(page,lambdaQueryWrapper);

        System.out.println("listPageC的result="+result.getTotal());

        return Result.suc(result.getRecords(),result.getTotal());
    }


}
