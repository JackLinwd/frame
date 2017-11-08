package org.lwd.frame.ctrl.validate;

import org.lwd.frame.util.DateTime;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Controller(Validators.DATE_TIME)
public class DateValidatorImpl extends ValidatorSupport {
    @Inject
    private DateTime dateTime;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return dateTime.toDate(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "date-time.illegal";
    }
}
