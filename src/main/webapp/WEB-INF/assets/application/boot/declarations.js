(function() {
    function HashMap(m) {
        var map = m || {};

        return function(k, v) {
            if (arguments.length == 1) {
                return map[k];
            } else if (arguments.length > 1) {
                var ex = map[k];
                map[k] = v;
                return ex;
            }
        };
    }

    var declarations = {};
    window.declaration = new HashMap(declarations);

    var translations = {};
    window.tr = new HashMap(translations);

    ASMX.HashMap = HashMap;
})();
