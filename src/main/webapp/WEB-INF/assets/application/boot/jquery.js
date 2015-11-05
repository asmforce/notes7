(function() {
    if (!$.fn.q) {
        $.fn.q = function(query) {
            return this.filter(query).add(this.find(query));
        };
    }
})();
