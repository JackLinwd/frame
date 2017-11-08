package org.lwd.frame.ctrl.validate;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * @author lwd
 */
@Controller("frame.ctrl.validate.wrapper")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ValidateWrapperImpl implements ValidateWrapper {
    private Validate validate;
    private String validator;
    private Validate.Scope scope;
    private String parameter;
    private String[] parameters;
    private boolean emptyable;
    private int[] number;
    private String[] string;
    private int failureCode;
    private String failureKey;
    private String[] failureArgKeys;

    @Override
    public ValidateWrapper setValidate(Validate validate) {
        this.validate = validate;

        return this;
    }

    @Override
    public ValidateWrapper setValidate(String validator, String parameter, int failureCode) {
        this.validator = validator;
        this.parameter = parameter;
        this.failureCode = failureCode;

        return this;
    }

    @Override
    public ValidateWrapper setValidate(String validator, Validate.Scope scope, String parameter, String[] parameters, boolean emptyable,
                                       int[] number, String[] string, int failureCode, String failureKey, String[] failureArgKeys) {
        this.validator = validator;
        this.scope = scope;
        this.parameter = parameter;
        this.parameters = parameters;
        this.emptyable = emptyable;
        this.number = number;
        this.string = string;
        this.failureCode = failureCode;
        this.failureKey = failureKey;
        this.failureArgKeys = failureArgKeys;

        return this;
    }

    @Override
    public String getValidator() {
        return validate == null ? validator : validate.validator();
    }

    @Override
    public Validate.Scope getScope() {
        return validate == null ? scope : validate.scope();
    }

    @Override
    public String getParameter() {
        return validate == null ? parameter : validate.parameter();
    }

    @Override
    public String[] getParameters() {
        return validate == null ? parameters : validate.parameters();
    }

    @Override
    public boolean isEmptyable() {
        return validate == null ? emptyable : validate.emptyable();
    }

    @Override
    public int[] getNumber() {
        return validate == null ? number : validate.number();
    }

    @Override
    public String[] getString() {
        return validate == null ? string : validate.string();
    }

    @Override
    public int getFailureCode() {
        return validate == null ? failureCode : validate.failureCode();
    }

    @Override
    public String getFailureKey() {
        return validate == null ? failureKey : validate.failureKey();
    }

    @Override
    public String[] getFailureArgKeys() {
        return validate == null ? failureArgKeys : validate.failureArgKeys();
    }
}
