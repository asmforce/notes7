package com.asmx.controllers;

import com.asmx.data.daos.GreetingDao;
import com.asmx.data.entities.Greeting;
import com.asmx.data.entities.GreetingFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GreetingController {
    @Autowired
    private GreetingDao greetingDao;
    @Autowired
    private GreetingFactory greetingFactory;

    @RequestMapping("/greetings")
    public String greetings(Model model) {
        model.addAttribute("greetings", greetingDao.getGreetings());
        return "greetings";
    }

    @RequestMapping("/greeting/{id:\\d+}")
    public String greeting(@PathVariable("id") int id, Model model) {
        Greeting greeting = greetingDao.getGreeting(id);
        if (greeting == null) {
            return "redirect:/greetings";
        }

        model.addAttribute("greeting", greeting);
        return "greeting";
    }

    @RequestMapping(value = "/greeting/new", consumes = "application/json", method = RequestMethod.POST)
    public String create1(@RequestBody Greeting greeting) {
        return create(greeting);
    }

    @RequestMapping(value = "/greeting/new", method = RequestMethod.GET)
    public String create2(@RequestParam String name, @RequestParam(required = false) Integer value) {
        Greeting greeting = greetingFactory.create();
        greeting.setName(name);
        if (value != null) {
            greeting.setValue(value);
        }

        return create(greeting);
    }

    private String create(Greeting greeting) {
        // Ensure that we don't touch an existing entries
        greeting.setId(0);

        if (StringUtils.isNotBlank(greeting.getName())) {
            if (greetingDao.putGreeting(greeting)) {
                return "redirect:/greeting/" + greeting.getId();
            }
        }
        return "redirect:/greetings";
    }
}
