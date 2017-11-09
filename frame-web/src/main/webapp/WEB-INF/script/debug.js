function Java() {
}

Java.type = function () {
    return BeanFactory;
};

function BeanFactory() {
}

BeanFactory.getBean = function (name) {
    if (name == "frame.util.message")
        return Message;

    if (name == "frame.script.arguments")
        return Arguments;

    return null;
};

function Message() {
}

Message.get = function (key) {
    return key;
};

function Arguments() {
}

Arguments.get = function (name) {
    if (name == "names") {
        var names = document.querySelector("#names").value;

        return names.indexOf(",") > -1 ? names.split(",") : [names];
    }

    return document.querySelector("#parameter").value;
};

function debug() {
    document.querySelector("#result").innerHTML = frame.validate();
}
