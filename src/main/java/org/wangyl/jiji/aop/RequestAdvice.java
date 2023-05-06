package org.wangyl.jiji.aop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wangyl.jiji.common.R;
import org.wangyl.jiji.entity.Orders;

import java.lang.annotation.Annotation;

@Component
@Aspect
@Slf4j
public class RequestAdvice {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    private void postCut(){
    }
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    private void getCut(){
    }
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    private void putCut(){
    }
    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    private void deleteCut(){
    }

    @Before("postCut()")
    public void postAdvice(JoinPoint jp) throws Throwable {
        log.info("连接{}进行post操作",((MethodSignature)jp.getSignature()).getMethod());
    }
    @Before("getCut()")
    public void getAdvice(JoinPoint jp) throws Throwable {
        log.info("连接{}进行get操作",((MethodSignature)jp.getSignature()).getMethod());
    }
    @Before("putCut()")
    public void putAdvice(JoinPoint jp) throws Throwable {
        log.info("连接{}进行put操作",((MethodSignature)jp.getSignature()).getMethod());
    }
    @Before("deleteCut()")
    public void deleteAdvice(JoinPoint jp) throws Throwable {
        log.info("连接{}进行delete操作",((MethodSignature)jp.getSignature()).getMethod());
    }
}
