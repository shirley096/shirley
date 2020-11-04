package com.deyuan.controller;

import com.deyuan.pojo.SysLog;
import com.deyuan.service.ISyslogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Component
@Aspect
public class LogAop {
    private Date visitTime;
    private Class claszz;//访问类
    private Method method;  //访问的方法


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ISyslogService syslogService;

    //前置通知
    @Before("execution(* com.deyuan.controller.*.*(..))")
    public void doBefore(JoinPoint jp) throws NoSuchMethodException {
        visitTime = new Date();
        claszz =  jp.getTarget().getClass();   //具体访问类
        //获取执行的所有方法名称
        String  methodName = jp.getSignature().getName();
        //获取所有访问的方法参数
        Object[] args = jp.getArgs();

        if (args==null || args.length==0){
            method = claszz.getMethod(methodName); //只能获取到无参的方法
        }else {
            Class[] classArgs = new Class[args.length];
            for (int i=0;i<args.length;i++){
                classArgs[i] = args[i].getClass();
            }
            //封装参数
            claszz.getMethod(methodName,classArgs);
        }
    }
    //后置通知
    @After("execution(* com.deyuan.controller.*.*(..))")
    public void doAfter(){
        long time =    new Date().getTime()-visitTime.getTime(); //访问的时长
        //获取操作的URL  通过java反射的方式获取
        String  url ="";

        if (claszz !=null && method!=null && claszz!=LogAop.class){
            //获取类上的注解和注解里的内容
            RequestMapping classAnnotation = (RequestMapping) claszz.getAnnotation(RequestMapping.class);
            if (classAnnotation !=null){
                String[] classValue = classAnnotation.value(); //获取到类上requestmapping里的value值
                //获取方法上的requestmapping注解
                RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                //取出里面的value值
                String[] methodValue = methodAnnotation.value();
                url = classValue[0]+methodValue[0];

                // 获取的请求的ip地址.
                String ip = request.getRemoteAddr();
                //获取当前操作的用户
                SecurityContext context = SecurityContextHolder.getContext();
                //获取到当前操作用户对象
                User principal = (User) context.getAuthentication().getPrincipal();
                //获取用户名
                String userName = principal.getUsername();
                SysLog  sysLog = new SysLog();
                sysLog.setIp(ip);// ip地址
                sysLog.setExecutionTime(time);//执行时长
                sysLog.setMethod("[类名] "+claszz.getName() +"[方法名] "+method.getName());//访问的方法
                sysLog.setUrl(url);//请求的url
                sysLog.setUsername(userName);//当前访问的用户
                sysLog.setVisitTime(visitTime);//访问的时间
                syslogService.save(sysLog);




            }

        }
    }
}
