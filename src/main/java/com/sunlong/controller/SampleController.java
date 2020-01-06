package com.sunlong.controller;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * kafkaDemo
 *
 * @Author 孙龙
 * @Date 2018/1/19
 */
@RestController
public class SampleController {

    @Autowired
    private PropertiesConfiguration commonConfig;

    @GetMapping("/getName")
    public String getName() throws Exception{

        return commonConfig.getString("name");
//        return "success";
    }

}
