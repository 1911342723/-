package com.java_project.reggie.controller;

import com.java_project.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.apache.tomcat.util.http.fileupload.util.mime.MimeUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/*文件上传下载的通用控制器*/
@RequestMapping("/common")
@RestController
@Slf4j
public class CommonController {
    //定义一个通用基本地址，交给Spring自动装配
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        log.info(file.toString());
        //上传后，文件会存为临时文件，如果不转存，那么就会自动删除，所以接下来来转存
        /*
        动态路径设置
        * */

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取后缀名
        String suffix =originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称冲突
        String fileName  = UUID.randomUUID().toString() + suffix;
        //创建一个目录名称
        File dir =new File(basePath);
        //判断当前目录存不存在
        if(!dir.exists()){
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    //下载函数，参数为文件名称，和响应信息
    //前端发送的参数是name文件名
    @GetMapping("/download")
    public void download(String name, HttpServletResponse  response){
        //输入流，读取服务端文件内容
        try {
            FileInputStream fileInputStream =new FileInputStream(new File(basePath+name));

            //输出流，通过网络写回浏览器，并在浏览器展示
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0 ;
            byte[] bytes =new byte[1024];
            //先用循环不断写到数组，并且在这个过程中不断刷新
            //知道len长度为-1为止，表述刷写完成
            while((len = fileInputStream.read(bytes))!= -1){
                outputStream.write(bytes,0,len);
                //刷写一下
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }



    }
}



