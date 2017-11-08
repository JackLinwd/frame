package org.lwd.frame.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lwd
 */
@Controller(Validators.LATITUDE)
public class LatitudeValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isMatchRegex("^-?\\d{1,2}\\.?\\d*$", parameter) && Math.abs(numeric.toDouble(parameter)) <= 90;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-latitude";
    }
}
