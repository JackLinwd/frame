package org.lwd.frame.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lwd
 */
@Controller(Validators.NOT_EMPTY)
public class NotEmptyValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return !validator.isEmpty(parameter);
    }

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        for (String parameter : parameters)
            if (!validator.isEmpty(parameter))
                return true;

        return false;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "empty";
    }
}
