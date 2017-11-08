package org.lwd.frame.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lwd
 */
@Controller(Validators.ID)
public class IdValidatorImpl extends IdValidatorSupport {
    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-id";
    }
}
