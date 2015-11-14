(function() {
    function createHashMap(m) {
        var map = m || {};

        return function(k, v) {
            var res;

            if (arguments.length == 1) {
                res = map[k];
                if (res instanceof Function) {
                    res = res(k);
                    map[k] = res;
                }
            } else if (arguments.length > 1) {
                res = map[k];
                map[k] = v;
                if (res instanceof Function) {
                    res = res(k);
                }
            }

            return res;
        };
    }

    var declarations = {};
    window.declaration = createHashMap(declarations);

    var translations = {};
    window.tr = createHashMap(translations);

    ASMX.Fn.createHashMap = createHashMap;
})();
