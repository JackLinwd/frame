frame.validator = function (name, func) {
    frame.validators[name] = func;
};

frame.validators = {};

frame.validate = function () {
    try {
        console.log(frame.args.get("parameter"));
        var json = JSON.parse(frame.args.get("parameter"));
        var names = frame.args.get("names");
        for (var i = 0; i < names.length; i++) {
            if (!frame.validators[names[i]])
                return "{\"code\":9996,\"name\":" + names[i] + "}";

            var result = frame.validators[names[i]](json);
            if (!result || result.code != 0)
                return JSON.stringify(result);
        }

        return "{\"code\":0}";
    } catch (e) {
        return "{\"code\":9999}";
    }
};
