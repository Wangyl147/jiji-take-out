package org.wangyl.jiji.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;


@Component
@Aspect
@Slf4j
public class OrderAdvice {

    @Pointcut(value = "execution(* org.wangyl.jiji.controller.OrderController.*age(..))")
    private void pointCut(){
    }

    public void advice(){

    }

}
