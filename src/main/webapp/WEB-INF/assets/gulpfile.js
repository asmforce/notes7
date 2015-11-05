var gulp = require('gulp');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var uglify = require('gulp-uglify');

var sources = [
    './application/heads.js',
    './application/boot/*.js',
    './application/core/*.js',
    './application/core/binding/*.js',
    './application/controllers/tools/*.js',
    './application/controllers/*.js',
    './application/tails.js'
];

gulp.task('compile', function() {
    gulp.src(sources)
        .pipe(concat('application.js'))
        .pipe(uglify({
            mangle: false,
            compress: false,
            output: {
                beautify: true,
                quote_style: 1
            }
        }))
        .pipe(gulp.dest('./'));
});

gulp.task('default', ['compile']);
