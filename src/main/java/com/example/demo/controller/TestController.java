package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/first_project")
    public String firstProject(){

        System.out.println("shx");
        return "shx";
    }

}
