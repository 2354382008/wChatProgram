package com.wChartProgram.buss.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import javax.servlet.http.HttpServletRequest;


/**
 * aop切面
 */
@Aspect
@Component
public class AopAspect {
    /**
     * execution(modifier? ret-type declaring-type?name-pattern(param-pattern) throws-pattern?)
     * modifier：匹配修饰符，public, private 等，省略时匹配任意修饰符
     * ret-type：匹配返回类型，使用 * 匹配任意类型
     * declaring-type：匹配目标类，省略时匹配任意类型
     * .. 匹配任意的包及其子包
     * name-pattern：匹配方法名称，使用 * 表示通配符
     * * 表示所有的方法
     * set* 匹配名称以 set 开头的方法
     * param-pattern：匹配参数类型和数量
     * () 匹配没有参数的方法
     * (..) 匹配有任意数量参数的方法
     * (*) 匹配有一个任意类型参数的方法
     * (*,int) 匹配有两个参数的方法，并且第一个为任意类型，第二个为 int 类型
     * throws-pattern：匹配抛出异常类型，省略时匹配任意类型
     */
    @Before("execution(* com.wChartProgram.buss.controller.*..*(..))") // 定义切入点表达式，这里匹配com.wChartProgram包下的所有方法
    public void beforeAdvice() {
        // 方法执行前的增强处理
        System.out.println("Before method execution");

    }

    @After("execution(* com.wChartProgram.buss.controller.*..*(..))") // 方法执行后的增强处理，无论方法是否抛出异常都会执行
    public void afterAdvice() {
        System.out.println("After method execution");
    }

    @Around("execution(* com.wChartProgram.buss.controller.*..*(..))")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        String url = request.getRequestURL().toString();
        //测试环境暂不拦截
        if (url.contains("test")){
            System.out.println("AOP拦截-query该路径不允许访问！");
            return null;
        }
        System.out.println("AOP-开始访问");
        Object proceed = proceedingJoinPoint.proceed();
        System.out.println("AOP-访问结束");
        return proceed;
    }

    /**
     * 定义切入点
     */
    @Pointcut(value = "execution(* com.wChartProgram.buss.controller.*..*(..))")
    public void pointCut(){}


    @Around(value = "pointCut()")
    public Object around2(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        String url = request.getRequestURL().toString();
        //测试环境暂不拦截
//        if (url.contains("add")){
//            System.out.println("AOP拦截-add该路径不允许访问！");
//            return null;
//        }
        System.out.println("AOP-开始访问");
        Object proceed = proceedingJoinPoint.proceed();
        System.out.println("AOP-访问结束");
        return proceed;
    }
}
