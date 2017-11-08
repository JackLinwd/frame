package org.lwd.frame.ctrl.validate;

import org.lwd.frame.ctrl.context.Header;
import org.lwd.frame.ctrl.security.TrustfulIp;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Controller(Validators.TRUSTFUL_IP)
public class TrustfulIpValidatorImpl extends ValidatorSupport {
    @Inject
    private Header header;
    @Inject
    private TrustfulIp trustfulIp;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return trustfulIp.contains(header.getIp());
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return 9996;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "distrust-ip";
    }
}
