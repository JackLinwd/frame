package org.lwd.frame.ctrl.execute;

import org.lwd.frame.ctrl.Interceptor;
import org.lwd.frame.ctrl.Invocation;
import org.lwd.frame.ctrl.validate.Validators;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lwd
 */
public class ExecuteInvocation implements Invocation, Interceptor {
    protected List<Interceptor> interceptors;
    protected Validators validators;
    protected Executor executor;
    protected int index;

    public ExecuteInvocation(List<Interceptor> interceptors, Validators validators, Executor executor) {
        this.interceptors = new ArrayList<>(interceptors);
        this.interceptors.add(this);
        this.validators = validators;
        this.executor = executor;
        index = 0;
    }

    @Override
    public Object invoke() throws Exception {
        if (index < interceptors.size())
            return interceptors.get(index++).execute(this);

        return null;
    }

    @Override
    public int getSort() {
        return 0;
    }

    @Override
    public Object execute(Invocation invocation) throws Exception {
        Object object;
        if ((object = validators.validate(executor.getValidates(), executor.getTemplate())) == null)
            object = executor.getMethod().invoke(executor.getBean());

        return object;
    }
}
