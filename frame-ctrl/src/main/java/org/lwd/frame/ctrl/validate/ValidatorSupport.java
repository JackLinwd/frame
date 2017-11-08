package org.lwd.frame.ctrl.validate;

import org.lwd.frame.ctrl.context.Request;
import org.lwd.frame.ctrl.execute.ExecutorHelper;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Message;
import org.lwd.frame.util.Numeric;

import javax.inject.Inject;

/**
 * @author lwd
 */
public abstract class ValidatorSupport implements Validator {
    @Inject
    protected org.lwd.frame.util.Validator validator;
    @Inject
    protected Converter converter;
    @Inject
    protected Numeric numeric;
    @Inject
    protected Message message;
    @Inject
    protected Request request;
    @Inject
    protected ExecutorHelper executorHelper;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return false;
    }

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        return false;
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return 0;
    }

    @Override
    public String getFailureMessage(ValidateWrapper validate) {
        return message.get(validator.isEmpty(validate.getFailureKey()) ? getDefaultFailureMessageKey() : validate.getFailureKey(),
                getFailureMessageArgs(validate));
    }

    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        if (validator.isEmpty(validate.getFailureArgKeys()))
            return new Object[]{message.get(executorHelper.get().getKey() + "." + validate.getParameter())};

        Object[] args = new Object[validate.getFailureArgKeys().length];
        for (int i = 0; i < args.length; i++)
            args[i] = message.get(validate.getFailureArgKeys()[i]);

        return args;
    }

    protected abstract String getDefaultFailureMessageKey();
}
