package com.increff.pos.controller;


import java.util.List;

import com.increff.pos.controller.AbstractUiController;
import com.increff.pos.dto.AdminApiDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.InfoData;
import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.ApiOperation;

@Controller
public class SignUpApiController extends AbstractUiController {

    @Autowired
    private AdminApiDto adminApiDto;
    @Autowired
    private InfoData info;

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/signup", method = RequestMethod.GET)
    public ModelAndView showPage() {
        if(info.getEmail() != "") {
            return new ModelAndView("redirect:/ui/home");
        }
        info.setMessage("");
        return mav("signup.html");
    }

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/signup", method = RequestMethod.POST)
    public ModelAndView initSite(UserForm form) throws ApiException{
        if(info.getEmail() != "") {
            return new ModelAndView("redirect:/ui/home");
        }
        try {
            adminApiDto.add(form);
        } catch (ApiException ex) {
            info.setMessage("User with email: " + form.getEmail() + " already exists!");
        }
        return mav("login.html");
    }
}