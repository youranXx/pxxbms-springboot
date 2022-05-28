/**
 * @Name: pxxbms-springboot
 * @Author: SaarChaffee
 * @Code: UTF-8
 * @Date: Created in 2022 2022/5/1
 */
package com.chaffee.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaffee.entity.dto.LoginDTO;
import com.chaffee.entity.dto.UserCodeDTO;
import com.chaffee.entity.pojo.User;
import com.chaffee.entity.pojo.UserRole;
import com.chaffee.entity.vo.UserVO;
import com.chaffee.service.UserRoleService;
import com.chaffee.service.UserService;
import com.chaffee.util.JwtTokenUtil;
import com.chaffee.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping( "/user" )
public class UserController {
  @Autowired
  UserService userService;
  @Autowired
  UserRoleService userRoleService;
  
  @GetMapping( "/login" )
  public R login( @RequestParam( value = "token" ) String token ) {
    String name = JwtTokenUtil.getUserNameFromToken( token );
    LoginDTO loginDTO = userService.queryLogin( name );
    return R.ok().datas( "user", loginDTO );
  }
  
  @PostMapping("/logout")
  public R logout( HttpServletResponse response ){
    response.setHeader( "Access-Control-Allow-Origin","*" );
    return R.ok();
  }
  
  @GetMapping( "/list" )
  public R list( @RequestParam( value = "queryUserName", required = false ) String userName,
                 @RequestParam( value = "queryUserRole", required = false ) String role,
                 @RequestParam( value = "pageIndex", required = false ) String pageIndex ) {
    userName = StringUtils.isEmpty( userName ) ? "" : userName;
    int userRole = StringUtils.isNumber( role ) && !role.contains( "-" ) ? Integer.parseInt( role ) : 0;
    int index = StringUtils.isNumber( pageIndex ) && !pageIndex.contains( "-" ) ? Integer.parseInt( pageIndex ) : 1;
    
    Page<UserVO> page = new Page<>( index, 15 );
    List<UserVO> userList = userService.queryUserList( page, userName, userRole );
    List<UserRole> roleList = userRoleService.list();
    return R.ok()
        .datas( "users", userList )
        .datas( "pageParam", page )
        .datas( "roleList", roleList )
        .datas( "queryUserName", userName )
        .datas( "queryUserRole", userRole );
  }
  
  @GetMapping( "/list/{id}" )
  public R list( @PathVariable( "id" ) String userId ) {
    long id = StringUtils.isNumber( userId ) ? Long.parseLong( userId ) : 0L;
    UserVO userVO = userService.queryUser( id );
    return R.ok().datas( "user", userVO );
  }
  
  @GetMapping( "/toUpd/{id}" )
  public R toUpd( @PathVariable( "id" ) String userId ) {
    long id = StringUtils.isNumber( userId ) ? Long.parseLong( userId ) : 0L;
    User user = userService.getById( id );
    List<UserRole> roleList = userRoleService.list();
    return R.ok()
        .datas( "user", user )
        .datas( "roleList", roleList );
  }
  
  @PostMapping( "/upd" )
  public R upd( User user, String currentId ) {
    boolean result = false;
    Long curId = StringUtils.isNumber( currentId ) ? Long.parseLong( currentId ) : 0L;
    user.setModifyBy( curId );
    try{
      result = userService.updateById( user );
    }catch( Exception e ){
      e.printStackTrace();
    }
    return result ? R.ok().message( "更新成功" ) : R.error().message( "更新失败" );
  }
  
  @GetMapping( "/toAdd" )
  public R toAdd() {
    return R.ok()
        .datas( "roleList", userRoleService.list() );
  }
  
  @PostMapping( "/add" )
  public R add( User user, String currentId ) {
    boolean result = false;
    Long curId = StringUtils.isNumber( currentId ) ? Long.parseLong( currentId ) : 0L;
    user.setModifyBy( curId );
    user.setCreatedBy( curId );
    
    try{
      result = userService.save( user );
    }catch( Exception e ){
      e.printStackTrace();
    }
    return result ? R.ok().message( "添加成功" ) : R.error().message( "添加失败" );
  }
  
  @GetMapping( "/exist" )
  @ResponseBody
  public R exist( @RequestParam String userCode ) {
    UserCodeDTO user = userService.queryUserByCode( userCode );
    if( user != null ){
      return R.ok().data( user );
      
    }
    else return R.error();
  }
  
  @GetMapping( "/delUser/{id}" )
  public R del( @PathVariable( "id" ) String userId ) {
    long id = StringUtils.isNumber( userId ) ? Long.parseLong( userId ) : 0L;
    boolean result = userService.removeById( id );
    return result ? R.ok().message( "删除成功" ) : R.error().message( "删除失败" );
  }
  
  @GetMapping( "/get/{id}" )
  @ResponseBody
  public R getUserById( @PathVariable( "id" ) String userId ) {
    long id = StringUtils.isNumber( userId ) ? Long.parseLong( userId ) : 0L;
    User user = userService.getById( id );
    if( user != null ){
      return R.ok().data( user );
    }
    else return R.error();
  }
}
