(function () {
    geturl = function () {
        var protocol, context, host, port, strurl;
        protocol = window.location.protocol;
        host = window.location.host;
        port = window.location.port;
        context = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
        strurl = protocol + "//" + host + context;
        return strurl;
    }
}).call(this);