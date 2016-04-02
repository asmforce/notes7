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
                            callback.apply(self, [data.notes]);
                        }
                    });
                }
            });
        }

        function doDispose(nodes) {
            // Do nothing
        }

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
