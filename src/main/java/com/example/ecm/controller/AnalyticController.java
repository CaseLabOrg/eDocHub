package com.example.ecm.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytic")
public class AnalyticController {

    @GetMapping("/{id}")
    public void getAnalyticByUserId() {

    }

}
