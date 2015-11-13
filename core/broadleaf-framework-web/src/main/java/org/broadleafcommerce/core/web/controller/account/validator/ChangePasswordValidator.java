/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.controller.account.validator;

import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import java.util.Arrays;

@Component("blChangePasswordValidator")
public class ChangePasswordValidator implements Validator {

    public static final String DEFAULT_VALID_PASSWORD_REGEX = "[0-9A-Za-z]";
    public static final Integer DEFAULT_MAX_PASSWORD_LENGTH = 15;
    public static final Integer DEFAULT_MIN_PASSWORD_LENGTH = 4;

    private String validPasswordRegex = DEFAULT_VALID_PASSWORD_REGEX;
    private Integer maxPasswordLength = DEFAULT_MAX_PASSWORD_LENGTH;
    private Integer minPasswordLength = DEFAULT_MIN_PASSWORD_LENGTH;

    @Resource(name = "blCustomerService")
    protected CustomerService customerService;

    public void validate(PasswordChange passwordChange, Errors errors) {

        char[] currentPassword = passwordChange.getCurrentPassword();
        char[] password = passwordChange.getNewPassword();
        char[] passwordConfirm = passwordChange.getNewPasswordConfirm();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", "currentPassword.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "newPassword.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPasswordConfirm", "newPasswordConfirm.required");

        if (!errors.hasErrors()) {
            //validate current password
            if (!customerService.isPasswordValid(currentPassword, CustomerState.getCustomer().getPassword(), CustomerState.getCustomer())) {
                errors.rejectValue("currentPassword", "currentPassword.invalid");
            }
            //password and confirm password fields must be equal
            if(!Arrays.equals(passwordConfirm,password)){
                errors.rejectValue("newPasswordConfirm", "newPasswordConfirm.invalid");
            }
            //restrict password characteristics
            if (!isPasswordValid(password)) {
                errors.rejectValue("newPassword", "newPassword.invalid");
            }
        }

        Arrays.fill(currentPassword,'\0');
        Arrays.fill(password,'\0');
        Arrays.fill(passwordConfirm,'\0');

    }

    public String getValidPasswordRegex() {
        return validPasswordRegex;
    }

    private boolean isPasswordValid(char[] password){
        if(minPasswordLength !=null && password.length < minPasswordLength ){
            return false;
        }
        if(maxPasswordLength != null && password.length > maxPasswordLength){
            return false;
        }

        for (char c: password) {
            String temp = String.valueOf(c);
            if(!temp.matches(validPasswordRegex)){

                return false;
            }
            temp = null;
        }
        return true;
    }

    public void setValidPasswordRegex(String validPasswordRegex) {
        this.validPasswordRegex = validPasswordRegex;
    }

    public void setMaxPasswordLength(Integer maxLength){
        this.maxPasswordLength = maxLength;
    }

    public void setMinPasswordLength(Integer minPasswordLength){
        this.minPasswordLength = minPasswordLength;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

}
