var ASMX = {
    Doc: $(window.document),
    Location: window.location,
    Navigation: window.history,
    Fn: {},
    Ajax: {}
};

window.ASMX = ASMX;

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

(function() {
    if (!$.fn.q) {
        $.fn.q = function(query) {
            return this.filter(query).add(this.find(query));
        };
    }
    if (!$.fn.allDetached) {
        $.fn.allDetached = function() {
            var detached = true;
            this.each(function() {
                if (document == this || $.contains(document, this)) {
                    detached = false;
                    return false;
                }
            });
            return detached;
        };
    }
    if (!$.fn.anyDetached) {
        $.fn.anyDetached = function() {
            var detached = false;
            this.each(function() {
                if (document != this && !$.contains(document, this)) {
                    detached = true;
                    return false;
                }
            });
            return detached;
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

(function() {
    var Controllers = {}, map = {};
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
                    onBind: function() {},
                    onUnbind: function() {}
                }, binding.controller);
                try {
                    controller.onBind(controller.scope);
                    $root.data('_controller', controller);
                    binding.roots.push($root);
                } catch (e) {
                    console.debug(e);
                }
            } else {
                console.debug('Controller `' + name + '` is not defined');
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

(function() {
    var ViewportRetention = ASMX.ViewportRetention;
    function createNotesView($scope) {
        var $container = $scope.find('.l-notes-wrapper');
        var $template = $scope.find('script[type="application/x-template"]:first');
        var chunkSize = 25;
        var maxVisible = 2 * chunkSize;
        var maxCached = 4 * chunkSize;
        var begin = 0;
        var end = 0;
        var backwardCache = [];
        var nodes = [];
        var forwardCache = [];
        function retrieve(pagination, callback) {
            $.ajax(declaration('address:notes'), {
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify({
                    sorting: {
                        criterion: 'creation_time',
                        direction: 'desc'
                    },
                    pagination: pagination
                }),
                beforeSend: function() {
                    NProgress.start();
                },
                complete: function() {
                    NProgress.done();
                },
                success: function(data) {
                    ASMX.Ajax.done(data, function(data) {
                        if (data.statusCode == declaration('RESPONSE:SUCCESS')) {
                            callback.apply(self, [ data.notes ]);
                        }
                    });
                }
            });
        }
        function doDispose(nodes) {}
        function doShowBackward(nodes) {
            $.each(nodes.reverse(), function(index, node) {
                node.$el.prependTo($container);
            });
        }
        function doShowForward(nodes) {
            $.each(nodes, function(index, node) {
                node.$el.appendTo($container);
            });
        }
        function doShow(nodes) {
            $.each(nodes, function(index, node) {
                node.$el = $($template.jqote(node.note, '@'));
            });
        }
        function doHide(nodes) {
            $.each(nodes, function(index, node) {
                node.$el.remove();
                node.$el = null;
            });
        }
        function doCreateNodes(notes) {
            var nodes = [];
            $.each(notes, function(index, note) {
                nodes.push({
                    note: note,
                    $el: null
                });
            });
            return nodes;
        }
        function disposeBackward(count) {
            if (backwardCache.length > 0 && count > 0) {
                if (count > backwardCache.length) {
                    count = backwardCache.length;
                }
                var disposed = backwardCache.splice(0, count);
                begin += count;
                doDispose(disposed);
            }
        }
        function moveToCacheBackward(count) {
            if (nodes.length > 0 && count > 0) {
                if (count > nodes.length) {
                    count = nodes.length;
                }
                var moved = nodes.splice(0, count);
                Array.prototype.push.apply(backwardCache, moved);
                doHide(moved);
            }
        }
        function moveFromCacheBackward(count) {
            if (backwardCache.length > 0 && count > 0) {
                if (count > backwardCache.length) {
                    count = backwardCache.length;
                }
                var moved = backwardCache.splice(backwardCache.length - count, count);
                Array.prototype.unshift.apply(nodes, moved);
                doShow(moved.slice(0));
                doShowBackward(moved);
            }
        }
        function moveFromCacheForward(count) {
            if (forwardCache.length > 0 && count > 0) {
                if (count > forwardCache.length) {
                    count = forwardCache.length;
                }
                var moved = forwardCache.splice(0, count);
                Array.prototype.push.apply(nodes, moved);
                doShow(moved.slice(0));
                doShowForward(moved);
            }
        }
        function moveToCacheForward(count) {
            if (nodes.length > 0 && count > 0) {
                if (count > nodes.length) {
                    count = nodes.length;
                }
                var moved = nodes.splice(nodes.length - count, count);
                Array.prototype.unshift.apply(forwardCache, moved);
                doHide(moved);
            }
        }
        function disposeForward(count) {
            if (forwardCache.length > 0 && count > 0) {
                if (count > forwardCache.length) {
                    count = forwardCache.length;
                }
                var disposed = forwardCache.splice(forwardCache.length - count, count);
                end -= count;
                doDispose(disposed);
            }
        }
        function up(callback) {
            if (backwardCache.length > 0) {
                moveFromCacheBackward(chunkSize);
                var extraVisible = nodes.length - maxVisible;
                if (extraVisible > chunkSize / 2) {
                    moveToCacheForward(extraVisible);
                }
                if (callback) {
                    callback();
                }
            } else if (begin > 0) {
                retrieve({
                    begin: begin > chunkSize ? begin - chunkSize : 0,
                    size: begin > chunkSize ? chunkSize : begin
                }, function(newNotes) {
                    if (newNotes.length) {
                        var newNodes = doCreateNodes(newNotes);
                        doShow(newNodes);
                        doShowBackward(newNodes);
                        begin -= newNodes.length;
                        Array.prototype.unshift.apply(nodes, newNodes);
                        var extraVisible = nodes.length - maxVisible;
                        if (extraVisible > chunkSize / 2) {
                            moveToCacheForward(extraVisible);
                            var extraCached = backwardCache.length + forwardCache.length - maxCached;
                            if (extraCached > chunkSize / 2) {
                                disposeForward(extraCached);
                            }
                        }
                    }
                    if (callback) {
                        callback();
                    }
                });
            }
        }
        function down(callback) {
            if (forwardCache.length > 0) {
                moveFromCacheForward(chunkSize);
                var extraVisible = nodes.length - maxVisible;
                if (extraVisible > chunkSize / 2) {
                    moveToCacheBackward(extraVisible);
                }
                if (callback) {
                    callback();
                }
            } else {
                retrieve({
                    begin: end,
                    size: chunkSize
                }, function(newNotes) {
                    if (newNotes.length) {
                        var newNodes = doCreateNodes(newNotes);
                        doShow(newNodes);
                        doShowForward(newNodes);
                        end += newNodes.length;
                        Array.prototype.push.apply(nodes, newNodes);
                        var extraVisible = nodes.length - maxVisible;
                        if (extraVisible > chunkSize / 2) {
                            moveToCacheBackward(extraVisible);
                            var extraCached = backwardCache.length + forwardCache.length - maxCached;
                            if (extraCached > chunkSize / 2) {
                                disposeBackward(extraCached);
                            }
                        }
                    }
                    if (callback) {
                        callback();
                    }
                });
            }
        }
        function clear() {
            $container.empty();
            if (nodes.length > 0) {
                doHide(nodes);
                doDispose(nodes);
                nodes = [];
            }
            begin = 0;
            end = 0;
            if (backwardCache.length > 0) {
                doDispose(backwardCache);
                backwardCache = [];
            }
            if (forwardCache.length > 0) {
                doDispose(forwardCache);
                forwardCache = [];
            }
        }
        function initialize() {
            clear();
            retrieve({
                begin: 0,
                size: chunkSize
            }, function(newNotes) {
                if (newNotes.length > 0) {
                    var newNodes = doCreateNodes(newNotes);
                    doShow(newNodes);
                    doShowForward(newNodes);
                    end = newNodes.length;
                    nodes = newNodes;
                }
                $container.find('.segment[id]:first-child').addClass('js-pagination-up');
                $container.find('.segment[id]:last-child').addClass('js-pagination-down');
            });
        }
        return {
            up: up,
            down: down,
            clear: clear,
            initialize: initialize
        };
    }
    ASMX.Controllers.new('notes', {
        notesView: null,
        onBind: function($scope) {
            var notesView = createNotesView($scope);
            function isVisible($scroller, $child) {
                var pos = $child.position();
                var scrollTop = $scroller.scrollTop();
                return !!(pos.top + $child.outerHeight(true) >= scrollTop && pos.top <= scrollTop + $scroller.height());
            }
            var $scroller = $scope.q('.js-pagination-container');
            $scroller.on('scroll', function() {
                $scroller.find('.js-pagination-up, .js-pagination-down').each(function(index, element) {
                    var $element = $(element);
                    if (isVisible($scroller, $element)) {
                        var down = $element.hasClass('js-pagination-down');
                        $element.removeClass('js-pagination-down').removeClass('js-pagination-up');
                        var viewportRetention = new ViewportRetention($scroller, $element);
                        if (down) {
                            notesView.down(function() {
                                viewportRetention.restore();
                                $scroller.find('.segment[id]:first-child').addClass('js-pagination-up');
                                $scroller.find('.segment[id]:last-child').addClass('js-pagination-down');
                            });
                        } else {
                            notesView.up(function() {
                                viewportRetention.restore();
                                $scroller.find('.segment[id]:first-child').addClass('js-pagination-up');
                                $scroller.find('.segment[id]:last-child').addClass('js-pagination-down');
                            });
                        }
                        return false;
                    }
                    return true;
                });
            });
            notesView.initialize();
            this.notesView = notesView;
        },
        onUnbind: function($scope) {
            $scope.q('.js-pagination-container').unbind('scroll');
            this.notesView.clear();
        }
    });
})();

ASMX.Controllers.new('sign', {
    onBind: function($scope) {
        var $formUi = $scope.find('ui.form');
        var $submitButton = $scope.find('.submit.button');
        var $inputs = $scope.find('[data-ajax]');
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
                            ASMX.Location.assign(declaration('address:notes'));
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
        $scope.form({
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
    }
});

$(document).ready(function() {
    ASMX.Binding.do();
});