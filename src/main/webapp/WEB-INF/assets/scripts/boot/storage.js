(function() {
    var STORAGE_KEY = "com.asmx.STORAGE_KEY";
    var storage;
    var data;

    function Storage() {
        var d;
        try {
            storage = window.localStorage;
            d = storage.getItem(STORAGE_KEY);
        } catch(e) {
            storage = {
                setItem: function() {
                    // stub
                },
                clear: function() {
                    // stub
                }
            };
        }
        data = JSON.parse(d) || {};
    }

    Storage.prototype.set = function(name, value) {
        var ex = data[name];

        if (value === undefined) {
            delete data[name];
        } else {
            data[name] = value;
        }

        if (ex !== value) {
            storage.setItem(STORAGE_KEY, JSON.stringify(data));
        }
    };

    Storage.prototype.get = function(name) {
        return data[name];
    };

    Storage.prototype.clear = function() {
        data = {};
        storage.clear();
    };

    ASMX.Storage = new Storage();
})();
