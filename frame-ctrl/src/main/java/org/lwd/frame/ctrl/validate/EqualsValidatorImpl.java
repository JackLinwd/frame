package org.lwd.frame.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lwd
 */
@Controller(Validators.EQUALS)
public class EqualsValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        if (parameters[0] == null)
            return parameters[1] == null;

        return parameters[0].equals(parameters[1]);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "not-equals";
    }
}
