package org.lwd.frame.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lwd
 */
@Controller(Validators.BETWEEN)
public class BetweenValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        int n = numeric.toInt(parameter);

        return n >= validate.getNumber()[0] && n <= validate.getNumber()[1];
    }

    @Override
    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        Object[] args = super.getFailureMessageArgs(validate);

        return new Object[]{args[0], validate.getNumber()[0], validate.getNumber()[1]};
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "not-between";
    }
}
