package com.sunlong.listener;

import com.google.common.base.Charsets;
import com.sunlong.config.SpringBootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author sunlong
 * @since 2020/2/1
 */
@Slf4j
public class ReloadListener {

    public void onChange(SpringBootContext context, Path filePath) throws IOException {

        //构造完整路径
//        Path fullPath = Paths.get(baseDir, path.toString());
        //获取文件
        File resourceFile = filePath.toFile();
        //读取文件内容
        String content = new String(Files.readAllBytes(filePath), Charsets.UTF_8);
        log.info("变更后文件内容：{}", content);
        //按行读取文件内容
//                List<String> lineList = Files.readAllLines(filePath);

        /**
         * 取出传递过来的参数构造本地资源文件
         */
//        File resourceFile = new File(String.valueOf(data[0]));
        FileSystemResource resource = new FileSystemResource(resourceFile);
        try {
            /**
             * 使用spring工具类加载资源，spring真是个好东西，你能想到的基本都有了
             */
            Properties prop = PropertiesLoaderUtils.loadProperties(new EncodedResource(resource, StandardCharsets.UTF_8));
            /**
             * 调用SpringBootContext刷新配置
             */
            context.refreshConfig(prop2Map(prop));
        } catch (InvocationTargetException | IllegalAccessException e1) {
            log.error("refresh config error", e1);
        } catch (Exception e) {
            log.error("load config error", e);
        }

    }

    public static Map<String, Object> prop2Map(Properties prop) {
        Map<String, Object> props = new HashMap<>();
        prop.forEach((key, value) -> {
            props.put(String.valueOf(key), value);
        });
        return props;
    }


}
