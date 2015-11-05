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
