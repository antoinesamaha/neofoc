package com.neofoc.app.controller;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    @GetMapping("/users")
    public String getUsers() {
        FocDesc focDesc = Globals.getApp().getFocDescByName("FUSER");
        FocList list = focDesc.getFocList();
        list.loadIfNotLoadedFromDB();
        list.size();
        return "Hello, World!";
    }
}