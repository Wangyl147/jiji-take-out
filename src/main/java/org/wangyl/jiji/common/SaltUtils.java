package org.wangyl.jiji.common;


import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class SaltUtils {
    public synchronized static String generateSalt(){
        StringBuilder salt= new StringBuilder();
        for(int i=0;i<4;i++){
            int a=new Random().nextInt(15);
            if(a>=10)salt.append(a-10+'a');
            else salt.append(a+'0');
        }
        log.info("salt:"+salt);
        return salt.toString();
    }
}
