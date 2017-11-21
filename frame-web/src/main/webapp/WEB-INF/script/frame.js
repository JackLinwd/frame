function frame() {
};

frame.BeanFactory = Java.type("org.lwd.frame.bean.BeanFactory");
frame.cache = frame.BeanFactory.getBean("frame.cache");
frame.message = frame.BeanFactory.getBean("frame.util.message");
frame.logger = frame.BeanFactory.getBean("frame.util.logger");
frame.sql = frame.BeanFactory.getBean("frame.dao.sql");
frame.ctrl = {
    header: frame.BeanFactory.getBean("frame.ctrl.context.header"),
    session: frame.BeanFactory.getBean("frame.ctrl.context.session"),
    request: frame.BeanFactory.getBean("frame.ctrl.context.request")
};
frame.args = frame.BeanFactory.getBean("frame.script.arguments");

frame.ready = function (func) {
    frame.ready.functions[frame.ready.functions.length] = func;
};

frame.ready.functions = [];

frame.ready.execute = function () {
    if (frame.ready.functions.length == 0)
        return;

    for (var i = 0; i < frame.ready.functions.length; i++) {
        if (!frame.ready.functions[i])
            continue;

        if (typeof (frame.ready.functions[i]) == "function")
            frame.ready.functions[i]();
        else if (typeof (frame.ready.functions[i]) == "string")
            eval(frame.ready.functions[i]);

        frame.ready.functions[i] = null;
    }
};

frame.existsMethod = function () {
    try {
        var method = frame.arguments.get("method");
        if (!method)
            method = frame.ctrl.request.get("method");

        return typeof (eval(method)) == "function";
    } catch (e) {
        return false;
    }
};
