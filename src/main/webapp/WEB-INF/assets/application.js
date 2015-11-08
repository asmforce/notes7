var ASMX = {
    Doc: $(window.document),
    Location: window.location,
    Navigation: window.history,
    Fn: {},
    Ajax: {}
};

window.ASMX = ASMX;

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

(function() {
    if (!$.fn.q) {
        $.fn.q = function(query) {
            return this.filter(query).add(this.find(query));
        };
    }
})();

(function() {
    var STORAGE_KEY = 'com.asmx.STORAGE_KEY';
    var storage;
    var data;
    function Storage() {
        var d;
        try {
            storage = window.localStorage;
            d = storage.getItem(STORAGE_KEY);
        } catch (e) {
            storage = {
                setItem: function() {},
                clear: function() {}
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
    declaration('MESSAGE:DATA_PROCESSING_ERROR', {
        title: tr('error'),
        message: tr('error.data'),
        classes: declaration('MESSAGE_CLASS:ERROR'),
        id: 'client-server'
    });
    declaration('MESSAGE:UNEXPECTED_ERROR', {
        title: tr('error'),
        message: tr('error.unexpected'),
        classes: declaration('MESSAGE_CLASS:ERROR'),
        id: 'unexpected'
    });
    declaration('MESSAGE:FORGED_REQUEST_ERROR', {
        title: tr('error'),
        message: tr('error.forged_request'),
        classes: declaration('MESSAGE_CLASS:ERROR'),
        id: 'forged'
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
                handled = handler(data) === false;
            }
            var messages = data.messages || [];
            if (!handled) {
                switch (data.statusCode) {
                  case declaration('RESPONSE:UNAUTHORISED'):
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

(function() {
    var Binding = {}, extensions = {}, map = {};
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
                    handler.apply(handler, [ $scope ].concat(ids));
                };
            } else {
                throw new Error('Unknown binding extension `' + name + '` requested');
            }
        } else {
            if (name in map) {
                throw new Error('The binding `' + name + '` is already in use');
            }
            map[name] = function($scope) {
                handler.call(handler, $scope, name);
            };
        }
    };
    Binding.do = function($scope) {
        $scope = $scope || ASMX.Doc;
        $.each(Object.keys(map), function(index, name) {
            map[name]($scope);
        });
        $.each(Object.keys(extensions), function(index, name) {
            extensions[name].handler($scope);
        });
    };
    ASMX.Binding = Binding;
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

ASMX.Binding.extension('controller', function($scope, controller) {
    return $scope.q('[data-controller="' + controller + '"]');
});

(function() {
    function timestamp(time) {
        var pattern = ASMX.Storage.get('TIMESTAMP_PATTERN');
        return $.format.date(time, pattern);
    }
    ASMX.Fn.timestamp = timestamp;
})();

(function() {
    function ViewportRetention($container, $item) {
        this.$container = $container;
        this.$item = null;
        this.delta = null;
        this.save($item);
    }
    ViewportRetention.prototype.save = function($item) {
        if ($item) {
            this.$item = $item;
        }
        if (this.$item) {
            this.delta = $item.position().top - this.$container.scrollTop();
        } else {
            this.delta = null;
        }
    };
    ViewportRetention.prototype.restore = function() {
        if (this.delta !== null) {
            var deltaNew = this.$item.position().top - this.$container.scrollTop();
            this.$container.scrollTop(this.$container.scrollTop() - this.delta + deltaNew);
        }
    };
    ASMX.ViewportRetention = ViewportRetention;
})();

ASMX.Binding.new('controller:sign', function($form) {
    var $formUi = $form.find('ui.form');
    var $submitButton = $form.find('.submit.button');
    var $inputs = $form.find('[data-ajax]');
    function collectFormData() {
        var data = {};
        $inputs.each(function() {
            var $input = $(this);
            var name = $input.attr('name');
            if (name !== undefined) {
                data[name] = $input.val();
            }
        });
        return data;
    }
    function signIn() {
        var $inputsToLock = $inputs.filter(':not(:disabled)');
        var requestData = collectFormData();
        $.ajax(declaration('address:sign'), {
            type: 'POST',
            dataType: 'json',
            data: requestData,
            beforeSend: function() {
                $formUi.addClass('disabled');
                $submitButton.addClass('loading');
                $inputsToLock.prop('disabled', true);
            },
            complete: function() {
                $formUi.removeClass('disabled');
                $submitButton.removeClass('loading');
                $inputsToLock.prop('disabled', false);
            },
            success: function(data) {
                ASMX.Ajax.done(data, function(data) {
                    switch (data.statusCode) {
                      case declaration('RESPONSE:SUCCESS'):
                        ASMX.Storage.set('TIMESTAMP_PATTERN', data.timestampPattern);
                        location.assign(declaration('address:notes'));
                        break;

                      case declaration('RESPONSE:UNAUTHORISED'):
                        ASMX.Messages.show({
                            title: tr('sign.unauthorized.title'),
                            message: tr('sign.unauthorized'),
                            classes: declaration('MESSAGE_CLASS:ERROR'),
                            id: 'unauthorized'
                        });
                        $inputs.filter('[type="password"]').val('');
                        return false;
                    }
                });
            }
        });
    }
    $form.form({
        fields: {
            username: {
                identifier: 'username',
                rules: [ {
                    type: 'empty',
                    prompt: tr('error.form.field_required')
                } ]
            },
            password: {
                identifier: 'password',
                rules: [ {
                    type: 'empty',
                    prompt: tr('error.form.field_required')
                } ]
            }
        },
        inline: true,
        onSuccess: function() {
            signIn();
            return false;
        }
    });
});

$(document).ready(function() {
    ASMX.Binding.do();
});