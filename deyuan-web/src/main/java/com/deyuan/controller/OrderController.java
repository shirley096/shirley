package com.deyuan.controller;

import com.deyuan.pojo.Orders;
import com.deyuan.service.IOrdersService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private IOrdersService ordersService;
    //查询订单列表
    @RequestMapping("/findAll")
    @Secured("ROLE_ADMIN")
    public ModelAndView findAll(@RequestParam(name = "page",required = true,defaultValue = "1")Integer page,@RequestParam(name = "size",required = true,defaultValue = "4")Integer size ) throws Exception {
        ModelAndView modelAndView= new ModelAndView();
        List<Orders> list = ordersService.findAll(page,size);
        PageInfo pageInfo = new PageInfo(list);
        modelAndView.addObject("pageInfo",pageInfo);
        modelAndView.setViewName("orders-list");
        return  modelAndView;
    }

    //查询订单详情
    @RequestMapping("/findById")
    public ModelAndView findById(@RequestParam(name = "id",required = true)String orderId) throws Exception {
        ModelAndView mav= new ModelAndView();
        Orders orders= ordersService.findById(orderId);
        mav.addObject("orders",orders);
        mav.setViewName("orders-show");
        return mav;
    }

}
