var gulp = require('gulp');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var uglify = require('gulp-uglify');
var sass = require('gulp-sass');

var scripts = [
    './scripts/heads.js',
    './scripts/boot/*.js',
    './scripts/core/*.js',
    './scripts/core/binding/*.js',
    './scripts/controllers/tools/*.js',
    './scripts/controllers/*.js',
    './scripts/tails.js'
];

var styles = [
    './styles/application.scss'
];

gulp.task('compile-js', function() {
    gulp.src(scripts)
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

gulp.task('compile-css', function() {
    gulp.src(styles)
        .pipe(concat('application.css'))
        .pipe(sass({
            errLogToConsole: true,
            outputStyle: 'expanded'
        }).on('error', sass.logError))
        .pipe(gulp.dest('./'));
});

gulp.task('compile', ['compile-js', 'compile-css']);
gulp.task('default', ['compile']);
