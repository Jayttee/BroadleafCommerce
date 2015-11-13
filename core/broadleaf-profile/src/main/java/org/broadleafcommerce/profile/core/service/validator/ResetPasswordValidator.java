/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.service.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("blResetPasswordValidator")
public class ResetPasswordValidator implements Validator {
    
    private String validPasswordRegex = RegistrationValidator.DEFAULT_VALID_PASSWORD_REGEX;
    public static final Integer DEFAULT_MAX_PASSWORD_LENGTH = 15;
    public static final Integer DEFAULT_MIN_PASSWORD_LENGTH = 4;

    private Integer maxPasswordLength = DEFAULT_MAX_PASSWORD_LENGTH;
    private Integer minPasswordLength = DEFAULT_MIN_PASSWORD_LENGTH;

    public void validate(String username, char[] password, char[] confirmPassword, Errors errors) {
        if (password.length <= 0) {
            errors.reject("password", "password.required");
        }
        
        if (StringUtils.isEmpty(username)) {
            errors.reject("username", "username.required");
        }
        
        if (! errors.hasErrors()) {
            if (!isPasswordValid(password)) {
                errors.rejectValue("password", "password.invalid", null, null);
            } else {
                if (!password.equals(confirmPassword)) {
                    errors.rejectValue("password", "passwordConfirm.invalid", null, null);
                }
            }        
        }
    }

    private boolean isPasswordValid(char[] password){
        if(getMinPasswordLength() !=null && password.length < getMinPasswordLength() ){
            return false;
        }
        if(getMaxPasswordLength() != null && password.length > getMaxPasswordLength()){
            return false;
        }

        for (char c: password) {
            String temp = String.valueOf(c);
            if(!temp.matches(getValidPasswordRegex())){

                return false;
            }
            temp = null;
        }
        return true;
    }

    public void setMaxPasswordLength(Integer maxLength){
        this.maxPasswordLength = maxLength;
    }

    public Integer getMaxPasswordLength(){
        return this.maxPasswordLength;
    }

    public void setMinPasswordLength(Integer minPasswordLength){
        this.minPasswordLength = minPasswordLength;
    }

    public Integer getMinPasswordLength(){
        return this.minPasswordLength;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
    }
    
    public String getValidPasswordRegex() {
        return validPasswordRegex;
    }

    public void setValidPasswordRegex(String validPasswordRegex) {
        this.validPasswordRegex = validPasswordRegex;
    }
}
