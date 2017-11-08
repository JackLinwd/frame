package org.lwd.frame.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lwd
 */
@Controller(Validators.LESS_THAN)
public class LessThanValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return numeric.toInt(parameter) < validate.getNumber()[0];
    }

    @Override
    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        Object[] args = super.getFailureMessageArgs(validate);

        return new Object[]{args[0], validate.getNumber()[0]};
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "not-less-than";
    }
}
