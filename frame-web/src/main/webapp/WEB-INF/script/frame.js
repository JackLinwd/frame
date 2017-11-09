function tephra() {
};

tephra.BeanFactory = Java.type("org.lpw.tephra.bean.BeanFactory");
tephra.cache = tephra.BeanFactory.getBean("tephra.cache");
tephra.message = tephra.BeanFactory.getBean("tephra.util.message");
tephra.logger = tephra.BeanFactory.getBean("tephra.util.logger");
tephra.sql = tephra.BeanFactory.getBean("tephra.dao.sql");
tephra.ctrl = {
    header: tephra.BeanFactory.getBean("tephra.ctrl.context.header"),
    session: tephra.BeanFactory.getBean("tephra.ctrl.context.session"),
    request: tephra.BeanFactory.getBean("tephra.ctrl.context.request")
};
tephra.args = tephra.BeanFactory.getBean("tephra.script.arguments");

tephra.ready = function (func) {
    tephra.ready.functions[tephra.ready.functions.length] = func;
};

tephra.ready.functions = [];

tephra.ready.execute = function () {
    if (tephra.ready.functions.length == 0)
        return;

    for (var i = 0; i < tephra.ready.functions.length; i++) {
        if (!tephra.ready.functions[i])
            continue;

        if (typeof (tephra.ready.functions[i]) == "function")
            tephra.ready.functions[i]();
        else if (typeof (tephra.ready.functions[i]) == "string")
            eval(tephra.ready.functions[i]);

        tephra.ready.functions[i] = null;
    }
};

tephra.existsMethod = function () {
    try {
        var method = tephra.arguments.get("method");
        if (!method)
            method = tephra.ctrl.request.get("method");

        return typeof (eval(method)) == "function";
    } catch (e) {
        return false;
    }
};
