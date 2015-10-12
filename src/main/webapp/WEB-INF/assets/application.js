var ASMX = {};

window.ASMX = ASMX;


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


(function() {
    var CONTAINER = 'div.messages-wrapper';
    var TEMPLATE = 'script[type="application/x-template"]:first';

    function showMessage($container, $template, message) {
        var $popup = $($template.jqote(message, '@'));

        $popup.find('.close[data-dismiss]').click(function() {
            $popup.transition({
                animation: 'fade down',
                onComplete: function() {
                    $popup.remove();
                }
            });
        });

        if (message.id !== undefined) {
            $popup.attr('data-message', message.id);

            var $previous = $container.children('[data-message="' + message.id + '"]');
            if ($previous.length > 0) {
                $previous.replaceWith($popup);
                $popup.transition('shake');
                return;
            }
        }

        $popup.appendTo($container);
        $popup.transition('pulse');
    }

    function show(message) {
        var $container = $(CONTAINER);
        var $template = $container.children(TEMPLATE);

        showMessage($container, $template, message);
    }

    function showAll(messages) {
        var $container = $(CONTAINER);
        var $template = $container.children(TEMPLATE);

        $.each(messages, function(index, message) {
            showMessage($container, $template, message);
        });
    }

    ASMX.Messages = {
        show: show,
        showAll: showAll
    };
})();
