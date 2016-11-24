var gulp = require('gulp');
var pug = require('gulp-pug');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var uglify = require('gulp-uglify');
var merge = require('merge2');

gulp.task('pug', function () {
    var pugs = gulp.src('src/main/assets/pug/**/*.pug')
        .pipe(pug({pretty: false}))
        .pipe(rename({extname: '.ftl'}))
        .pipe(gulp.dest('src/main/webapp/WEB-INF/jftl'));

    var tmpls = gulp.src(['!src/main/assets/pug/**/*.pug', 'src/main/assets/**/*.pug'])
        .pipe(pug({pretty: false}))
        .pipe(rename({extname: '.ftl'}))
        .pipe(gulp.dest('src/main/webapp/WEB-INF/jftl/tmpl'));

    return merge(pugs, tmpls);
});

gulp.task('copy', function () {
    return gulp.src('src/main/assets/img/**/*')
        .pipe(gulp.dest('src/main/webapp/assets/img'))
});

gulp.task('js', function () {
    var dest = 'src/main/webapp/assets/js/';

    gulp.src('src/main/assets/js/**/*.js')
        .pipe(gulp.dest(dest))
        .pipe(rename(function (path) {
            path.extname = ".min.js"
        }))
        .pipe(uglify())
        .pipe(gulp.dest(dest))
});

gulp.task('default', ['pug', 'js', 'copy']);
