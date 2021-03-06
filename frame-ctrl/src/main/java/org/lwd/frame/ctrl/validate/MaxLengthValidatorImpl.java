package org.lwd.frame.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lwd
 */
@Controller(Validators.MAX_LENGTH)
public class MaxLengthValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isEmpty(parameter) || parameter.length() <= validate.getNumber()[0];
    }

    @Override
    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        Object[] args = super.getFailureMessageArgs(validate);

        return new Object[]{args[0], validate.getNumber()[0]};
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "over-max-length";
    }
}
