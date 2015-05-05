package com.asmx.controllers;

import com.asmx.data.daos.GreetingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GreetingController {
    @Autowired
    private GreetingDao greetingDao;

    @RequestMapping("/greeting/{id}")
    public String greeting(@PathVariable("id") int id, Model model) {
        model.addAttribute("greeting", greetingDao.getGreeting(id));
        return "greeting";
    }

    @RequestMapping("/greetings")
    public String greetings(Model model) {
        model.addAttribute("greetings", greetingDao.getGreetings());
        return "greetings";
    }
}
