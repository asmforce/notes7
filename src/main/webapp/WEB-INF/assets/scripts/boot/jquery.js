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
        }
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
        }
    }
})();
