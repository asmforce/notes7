(function() {
    declaration('MESSAGE:DATA_PROCESSING_ERROR', function() {
        return {
            title: tr('error'),
            message: tr('error.data'),
            classes: declaration('MESSAGE_CLASS:ERROR'),
            id: 'client-server'
        };
    });

    declaration('MESSAGE:UNEXPECTED_ERROR', function() {
        return {
            title: tr('error'),
            message: tr('error.unexpected'),
            classes: declaration('MESSAGE_CLASS:ERROR'),
            id: 'unexpected'
        };
    });

    declaration('MESSAGE:FORGED_REQUEST_ERROR', function() {
        return {
            title: tr('error'),
            message: tr('error.forged_request'),
            classes: declaration('MESSAGE_CLASS:ERROR'),
            id: 'forged'
        };
    });

    function appendDetails(message, details) {
        if (details) {
            return message + ' (' + details + ')';
        }
        return message;
    }

    function buildErrorMessage(textStatus, errorThrown) {
        switch (textStatus) {
            case 'error':
            case 'timeout':
            case 'abort':
                return appendDetails(tr('error.network'), errorThrown);
                break;

            case 'parsererror':
                return tr('error.data');
                break;

            default:
                return appendDetails(tr('error.unknown'), errorThrown);
                break;
        }
    }

    ASMX.Ajax.error = function($xhr, textStatus, errorThrown) {
        var msg = declaration('MESSAGE:DATA_PROCESSING_ERROR');
        ASMX.Messages.show($.extend({}, msg, {
            message: buildErrorMessage(textStatus, errorThrown)
        }));
    };

    ASMX.Ajax.done = function(data, handler) {
        if (data && data.statusCode !== undefined) {
            var handled = false;
            if (handler instanceof Function) {
                handled = (handler(data) === false);
            }

            var messages = data.messages || [];

            if (!handled) {
                switch (data.statusCode) {
                    case declaration('RESPONSE:UNAUTHORISED'):
                        // TODO: show modal sign in dialog
                        break;

                    case declaration('RESPONSE:UNEXPECTED'):
                        messages.push(declaration('MESSAGE:UNEXPECTED_ERROR'));
                        break;

                    case declaration('RESPONSE:FORGED_REQUEST'):
                        messages.push(declaration('MESSAGE:FORGED_REQUEST_ERROR'));
                        break;
                }
            }

            if (messages.length > 0) {
                ASMX.Messages.showAll(messages);
            }
        } else {
            ASMX.Messages.show(declaration('MESSAGE:DATA_PROCESSING_ERROR'));
        }
    };

    $.ajaxSetup({
        error: ASMX.Ajax.error
    });
})();
