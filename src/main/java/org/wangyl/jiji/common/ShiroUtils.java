package org.wangyl.jiji.common;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.wangyl.jiji.entity.Employee;
import org.wangyl.jiji.entity.User;

public class ShiroUtils extends SecurityUtils {
    public static Session getSession(){
        return getSubject().getSession();
    }

    public static Long getEmployeeOrUserId(){

        Object o=getSubject().getPrincipal();
        if(o instanceof Employee){
            return ((Employee) o).getId();
        }
        if (o instanceof User){
            return ((User)o).getId();
        }
        return null;
    }
    public static Employee getEmployee(){
        Object o=getSubject().getPrincipal();
        if(o instanceof Employee){
            return (Employee) o;
        }
        return null;
    }
    public static User getUser(){
        Object o=getSubject().getPrincipal();
        if(o instanceof User){
            return (User) o;
        }
        return null;
    }
}
