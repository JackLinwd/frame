package org.lwd.frame.ctrl.validate;

import org.lwd.frame.crypto.Sign;
import org.lwd.frame.ctrl.context.Header;
import org.lwd.frame.ctrl.security.TrustfulIp;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Controller(Validators.SIGN)
public class SignValidatorImpl extends ValidatorSupport implements SignValidator {
    @Inject
    private Sign sign;
    @Inject
    private Header header;
    @Inject
    private TrustfulIp trustfulIp;
    private ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    @Override
    public void setSignEnable(boolean enable) {
        threadLocal.set(enable);
    }

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return !enable() || trustfulIp.contains(header.getIp())
                || sign.verify(request.getMap(), validator.isEmpty(validate.getString()) ? null : validate.getString()[0]);
    }

    private boolean enable() {
        return threadLocal.get() == null || threadLocal.get();
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return 9995;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-sign";
    }
}
