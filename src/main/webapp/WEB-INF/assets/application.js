var ASMX = {};

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

    function showAll(messsages) {
        var $container = $(CONTAINER);
        var $template = $container.children(TEMPLATE);

        $.each(messsages, function(index, message) {
            showMessage($container, $template, message);
        });
    }

    ASMX.Messages = {
        show: show,
        showAll: showAll
    };
})();
