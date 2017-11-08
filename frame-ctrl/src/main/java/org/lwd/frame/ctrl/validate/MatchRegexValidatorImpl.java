package org.lwd.frame.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lwd
 */
@Controller(Validators.MATCH_REGEX)
public class MatchRegexValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isMatchRegex(validate.getString()[0], parameter);
    }

    @Override
    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        Object[] args = super.getFailureMessageArgs(validate);

        return new Object[]{args[0], validate.getString()[0]};
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "not-match-regex";
    }
}
