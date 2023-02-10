module.exports = function ( grunt ) {
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-htmlcompressor');
    grunt.loadNpmTasks('grunt-contrib-cssmin');

    var userConfig = require( './build.config.js' );

    var today = grunt.template.process('<%= grunt.template.today("yyyymmddHHMMss") %>');

    var taskConfig = {
        pkg: grunt.file.readJSON("package.json"),
        compiledScript: '<%= compile_dir %>/assets/<%= pkg.name%>.concat.js',
        compiledMinScript: '<%= compile_dir %>/assets/<%= pkg.name%>-<%= pkg.version%>-' + today + '.min.js',
        compiledMinUnsubscribeScript: '<%= compile_dir %>/assets/unsubscribe-<%= pkg.version%>-' + today + '.min.js',
        resourceMinScript: '<%= resources_dir%>/assets/<%= pkg.name%>-<%= pkg.version%>-' + today + '.min.js',
        resourceMinUnsubscribeScript: '<%= resources_dir%>/assets/unsubscribe-<%= pkg.version%>-' + today + '.min.js',
        buildSouces: '<%= build_dir %>/src/',
        buildJs: '<%= buildSouces %>js/',
        sourcesWebApp: '<%= src_dir %>src/main/webapp/',
        meta: {
            banner: 
                '/**\n' +
                ' * <%= pkg.name %> - v<%= pkg.version %> - <%= grunt.template.today("yyyy-mm-dd") %>\n' +
                ' * <%= pkg.homepage %>\n' +
                ' *\n' +
                ' * Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author %>\n' +
                ' * Licensed <%= pkg.licenses.type %> <<%= pkg.licenses.url %>>\n' +
                ' */\n'
        },
        clean: [ 
            '<%= build_dir %>', 
            '<%= compile_dir %>'
        ],
        copy: {
            copy_js : {
                src : [ 'js/**' ],
                dest : '<%= buildSouces %>',
                cwd : '<%= sourcesWebApp %>resources',
                expand : true
            },
            copy_html : {
                src : [ 'pages/**', 'index.html', 'unsubscribe.html' ],
                dest : '<%= buildSouces %>',
                cwd : '<%= sourcesWebApp %>',
                expand : true,
                options : {
                    process : function(contents, path) {
                        if (path.indexOf('index.html') !== -1) {
                            var templRE = /<\!--\s?start_min_replacement\s?-->[\s\S]*<\!--\s?end_min_replacement\s?-->/gm;
                            contents = contents.replace(templRE, '<script type="text/javascript" src="' + grunt.config.get('resourceMinScript')
                                    + '"></script>');
                        } else if (path.indexOf('unsubscribe.html') !== -1) {
                            var templRE = '<script src="resources/js/unsubscribe.js" type="text/javascript"></script>';
                            contents = contents.replace(templRE, '<script type="text/javascript" src="'
                                     + grunt.config.get('resourceMinUnsubscribeScript') + '"></script>');
                        }
                        contents = contents.replace(/\sclass="([^"]*a_[^"]+)"/gm, function(str, classes){
                            // remove all instances of a_{{something}} CSS classes
                            classes = classes.replace(/^a_[^\s]*{{[^}]*}}[^\s]*/g, '').replace(/\sa_[^\s]*{{[^}]*}}[^\s]*/g, '');
                            // remove all instances of a_something CSS classes
                            classes = classes.replace(/^a_[^\s]+/g, '').replace(/\sa_[^\s]+/g, '');
                            // remove all duplicated spaces between CSS classes
                            classes = classes.replace(/\s+/g, ' ');
                            return classes.length ? ' class="' + classes + '"' : '';
                        });
                        return contents;
                    }
                }
            }
        },
        concat: {
            concat_js: {
                options: {
                    banner: '<%= meta.banner %>'
                },
                src: [
                    '<%= buildJs %>app.js',
                    '<%= buildJs %>*/*.js',
                    '<%= buildJs %>**/constants/*.js',
                    '<%= buildJs %>**/services/*.js',
                    '<%= buildJs %>**/modules/*.js',
                    '<%= buildJs %>**/utils/*.js',
                    '<%= buildJs %>**/directives/**/*.js',
                    '<%= buildJs %>**/directives/*.js',
                    '<%= buildJs %>**/controllers/**/*.js',
                    '<%= buildJs %>**/controllers/*.js'
                ],
                dest: '<%= compiledScript %>'
            }
        },
        uglify: {
            uglify_js: {
                options: {
                    banner: '<%= meta.banner %>',
                    preserveComments: false
                },
                files: { // destination : source
                    '<%= compiledMinScript%>' : '<%= compiledScript %>',
                    '<%= compiledMinUnsubscribeScript%>' : '<%= buildJs %>unsubscribe.js'
                }
            }
        },
        htmlcompressor : {
            compress_html : {
                src : '<%= buildSouces %>',
                dest : 'htmlCompressor',
                options : {
                    type : 'html',
                    recursive: true,
                    mask: '*.html',
                    output: '<%= compile_dir %>/'
                }
            }
        },
        cssmin : {
            compress_css : {
                files : {
                    '<%= compile_dir %>/app.css' : '<%= sourcesWebApp %>resources/css/app.css',
                    '<%= compile_dir %>/jasper.css' : '<%= sourcesWebApp %>resources/css/jasper.css'
                }
            }
        }
    };

    grunt.initConfig( grunt.util._.extend( taskConfig, userConfig ) );
    grunt.registerTask( 'default', [ 'build', 'compile' ] );

    grunt.registerTask( 'build', ['clean', 'copy'] );
    grunt.registerTask( 'compile', ['concat', 'uglify', 'htmlcompressor', 'cssmin'] );
};