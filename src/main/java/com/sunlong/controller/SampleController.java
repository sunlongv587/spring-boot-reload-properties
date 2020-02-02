package com.sunlong.controller;

import com.sunlong.annotation.RefreshScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * kafkaDemo
 *
 * @author 孙龙
 * @Date 2018/1/19
 */
@RefreshScope
@RestController
public class SampleController {

    @Value("${name}")
    private String name;

    @Value("${address}")
    private String address;

    @Value("${test}")
    private boolean test;

    @Value("${age}")
    private int age;

    @GetMapping("/fileSystem")
    public String getConfig() {

        return "name: " + name + "\n address: " + address + "\n age: " + age + "\n test: " + test;
    }
}
