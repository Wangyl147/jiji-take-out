package org.wangyl.jiji.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wangyl.jiji.common.R;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传：页面的form表单必须满足以下条件：
 * method="post"
 * enctype="multipart/form-data"
 * type="file"
 * 不满足无法上传
 * 页面上的各种上传组件底层还是基于form表单
 * 服务端接收上传的文件，一般都会使用Apache的commons-fileupload和commons-io两个组件
 * Spring已对此进行了封装，只需在Controller的方法中声明一个Multipart类型的参数即可接收
 */

/**
 * 文件下载：两种形式，1、附件下载；2、直接在浏览器中打开
 * 本质上是服务端以流的形式把文件写回浏览器的过程
 * 文件下载，页面可以使用<img>标签显示下载的图片
 */

//文件上传和下载
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${jiji.path}")
    private String basePath;

    //上传
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){//必须跟表单中的name保持一致
        //file是一个临时文件，需要尽快转存，否则就会消失
        log.info("上传文件{}",file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));

        //使用uuid重新命名，防止文件重名导致覆盖
        String fileName = UUID.randomUUID().toString()+suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try{
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath+fileName));
        }catch (IOException e){
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    //下载
    //通过流的方式写二进制数据，不需要返回值
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try{
            // 通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            // 通过输出流在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
