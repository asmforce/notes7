(function() {
    function timestamp(time) {
        var pattern = ASMX.Storage.get('TIMESTAMP_PATTERN');
        return $.format.date(time, pattern);
    }

    ASMX.Fn.timestamp = timestamp;
})();
