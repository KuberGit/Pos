package com.increff.pos.dto;

import com.increff.pos.model.UserData;
import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminApiDto {
    @Autowired
    private UserService userService;

    public void add(UserForm userForm) throws ApiException {
        validateForm(userForm);
        UserPojo user = ConvertUtil.convertFormToPojo(userForm);
        userService.add(user);
    }

    public List<UserData> getAll() {
        return userService
                .getAll()
                .stream()
                .map(ConvertUtil::convertPojoToData)
                .collect(Collectors.toList());
    }

    private static final String EMAIL_PATTERN = "[a-z\\d]+@[a-z]+\\.[a-z]{2,3}";

    public static boolean isValidEmail(String email) {
        return email.matches(EMAIL_PATTERN);
    }

    public static void validateForm (UserForm userForm) throws ApiException {
        System.out.println(userForm.getEmail());
        System.out.println(userForm.getPassword());
        if (!isValidEmail(userForm.getEmail())) {
            throw new ApiException("Invalid email!");
        }

        if (StringUtil.isEmpty(userForm.getPassword())) {
            throw new ApiException("Password must not be blank!");
        }
    }
}