package com.deyuan.service.impl;

import com.deyuan.dao.UserDao;
import com.deyuan.pojo.Role;
import com.deyuan.pojo.UserInfo;
import com.deyuan.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncode;
    @Autowired
    private UserDao userDao;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo=userDao.findByUsername(username);
        User user=new User(userInfo.getUsername(),userInfo.getPassword(),userInfo.getStatus()==0?false:true,true,true,true,getAuthority(userInfo.getRoles()));
        return user;
    }

    private List<SimpleGrantedAuthority> getAuthority(List<Role> roleslist) {

        List<SimpleGrantedAuthority> list=new ArrayList<>();
        for (Role role : roleslist) {
            list.add(new SimpleGrantedAuthority("ROLE_"+role.getRoleName()));
        }

        return  list;
    }

    //查询全部
    @Override
    public List<UserInfo> findAll() {
        return userDao.findAll();
    }

    //添加用户
    @Override
    public void save(UserInfo userInfo) {
        userInfo.setPassword(passwordEncode.encode(userInfo.getPassword()));
        userDao.save(userInfo);
    }

    //查询用户详情
    @Override
    public UserInfo findById(String id) {
        return userDao.findById(id);
    }


    //根据用户id查询出 没有的角色
    @Override
    public List<Role> findOtherRoles(String userId) {
        return userDao.findOtherRoles(userId);
    }

    //添加没有的角色
    @Override
    public void addRoleToUser(String userId, String[] roles) {
        for (String roleId : roles) {
            userDao.addRoleToUser(userId,roleId);
        }
    }


}
