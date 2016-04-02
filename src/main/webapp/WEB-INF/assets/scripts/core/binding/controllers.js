(function() {
    var Controllers = {},
        map = {};

    Controllers.new = function(name, controller) {
        var key = '@' + name;
        if (key in map) {
            throw new Error('The controller `' + name + '` is already defined');
        }

        map[key] = {
            roots: [],
            controller: controller
        };
    };

    ASMX.Binding.new('controllers', function($scope) {
        if ($scope.anyDetached()) {
            return;
        }

        $scope.q('[data-controller]').each(function() {
            var $root = $(this);
            var name = $root.data('controller');
            var key = '@' + name;

            if (key in map) {
                var binding = map[key];

                var controller = $.extend({
                    get name() {
                        return name;
                    },
                    get scope() {
                        return $root;
                    },
                    onBind: function() {
                        // Do nothing
                    },
                    onUnbind: function() {
                        // Do nothing
                    }
                }, binding.controller);

                try {
                    controller.onBind(controller.scope);
                    $root.data('_controller', controller);
                    binding.roots.push($root);
                } catch (e) {
                    console.debug(e);
                }
            } else {
                console.debug('Controller `' + name + "` is not defined");
            }
        });

        $.each(Object.keys(map), function(index, name) {
            var roots = [];
            $.each(map[name].roots, function(index, $root) {
                if ($root.anyDetached()) {
                    var controller = $root.data('_controller');
                    try {
                        controller.onUnbind(controller.scope);
                    } catch (e) {
                        console.debug(e);
                    }
                } else {
                    roots.push($root);
                }
            });
            map[name].roots = roots;
        });
    });

    ASMX.Controllers = Controllers;
})();
