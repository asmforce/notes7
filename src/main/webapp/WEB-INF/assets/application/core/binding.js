(function() {
    var Binding = {},
        extensions = {},
        map = {};

    Binding.extension = function(name, handler) {
        if (!handler instanceof Function) {
            throw new TypeError('The binding extension handler must be a function');
        }

        if (name in extensions) {
            throw new Error('The binding extension `' + name + '` is already in use');
        }

        var map = {};

        extensions[name] = {
            handler: function($scope) {
                $.each(Object.keys(map), function(index, param) {
                    var $elements = handler.call(handler, $scope, param);
                    if ($elements instanceof $) {
                        $elements.each(function() {
                            map[param]($(this));
                        });
                    }
                });
            },
            map: map
        };
    };

    Binding.new = function(name, handler) {
        var ids = (name || '').split(':');

        if (!handler instanceof Function) {
            throw new TypeError('The binding handler must be a function');
        }

        name = ids.shift();

        if (ids.length > 0) {
            if (name in extensions) {
                var extension = extensions[name];
                var param = ids[0];

                if (param in extension.map) {
                    throw new Error('The binding `' + param + '` is already in use');
                }

                extension.map[param] = function($scope) {
                    handler.apply(handler, [$scope].concat(ids));
                };
            } else {
                throw new Error('Unknown binding extension `' + name + '` requested');
            }
        } else {
            if (name in map) {
                throw new Error('The binding `' + name + '` is already in use');
            }

            map[name] = function ($scope) {
                handler.call(handler, $scope, name);
            };
        }
    };

    Binding.do = function($scope) {
        $scope = $scope || $(document);
        $.each(Object.keys(map), function(index, name) {
            map[name]($scope);
        });
        $.each(Object.keys(extensions), function(index, name) {
            extensions[name].handler($scope);
        });
    };

    ASMX.Binding = Binding
})();
