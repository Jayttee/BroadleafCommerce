/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.controller.validator;

import org.apache.commons.validator.GenericValidator;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

/**
 * @author bpolster
 */
@Component("blRegisterCustomerValidator")
public class RegisterCustomerValidator implements Validator {

    public static final Integer DEFAULT_MAX_PASSWORD_LENGTH = 15;
    public static final Integer DEFAULT_MIN_PASSWORD_LENGTH = 4;

    private Integer maxPasswordLength = DEFAULT_MAX_PASSWORD_LENGTH;
    private Integer minPasswordLength = DEFAULT_MIN_PASSWORD_LENGTH;

    private String validatePasswordExpression = "[0-9A-Za-z]";

    @Resource(name="blCustomerService")
    private CustomerService customerService;

    public RegisterCustomerValidator() {}

    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return clazz.equals(RegisterCustomerForm.class);
    }
    
    public void validate(Object obj, Errors errors) {
        validate(obj, errors, false);
    }
    
    /*
     *         errors.pushNestedPath("customer");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "emailAddress.required");
        errors.popNestedPath();

        if (errors.hasErrors()){
            if (!passwordConfirm.equals(password)) {
                errors.rejectValue("passwordConfirm", "invalid"); 
            }
            if (!customer.getFirstName().matches(validNameRegex)) {
                errors.rejectValue("firstName", "firstName.invalid", null, null);
            }

            if (!customer.getLastName().matches(validNameRegex)) {
                errors.rejectValue("lastName", "lastName.invalid", null, null);
            }

            if (!customer.getPassword().matches(validPasswordRegex)) {
                errors.rejectValue("password", "password.invalid", null, null);
            }

            if (!password.equals(passwordConfirm)) {
                errors.rejectValue("password", "passwordConfirm.invalid", null, null);
            }

            if (!GenericValidator.isEmail(customer.getEmailAddress())) {
                errors.rejectValue("emailAddress", "emailAddress.invalid", null, null);
            }
        }
     */

    public void validate(Object obj, Errors errors, boolean useEmailForUsername) {
        RegisterCustomerForm form = (RegisterCustomerForm) obj;

        Customer customerFromDb = customerService.readCustomerByUsername(form.getCustomer().getUsername());

        if (customerFromDb != null) {
            if (useEmailForUsername) {
                errors.rejectValue("customer.emailAddress", "emailAddress.used", null, null);
            } else {
                errors.rejectValue("customer.username", "username.used", null, null);
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "passwordConfirm.required");
        
        errors.pushNestedPath("customer");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "emailAddress.required");
        errors.popNestedPath();


        if (!errors.hasErrors()) {

            if (!isPasswordValid(form.getPassword())) {
                errors.rejectValue("password", "password.invalid", null, null);
            }

            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                errors.rejectValue("password", "passwordConfirm.invalid", null, null);
            }

            if (!GenericValidator.isEmail(form.getCustomer().getEmailAddress())) {
                errors.rejectValue("customer.emailAddress", "emailAddress.invalid", null, null);
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
            if(!temp.matches(getValidatePasswordExpression())){

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

    public String getValidatePasswordExpression() {
        return validatePasswordExpression;
    }

    public void setValidatePasswordExpression(String validatePasswordExpression) {
        this.validatePasswordExpression = validatePasswordExpression;
    }        
}
