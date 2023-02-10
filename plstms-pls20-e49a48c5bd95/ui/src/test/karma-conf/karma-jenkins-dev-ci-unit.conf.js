// Karma E2E configuration for Jenkins environment.
// It extends main karma-junit.conf.js config and overrides specific properties like 'proxies'.
// Also this file may contains expression like ${port} that will be processed by maven. Result file will stored into target sub directory.

var shared = require('./karma-unit.conf');

module.exports = function(config) {
    shared(config);

    config.set({

        // test results reporter to use
        // possible values: dots || progress || growl
        reporters : [ 'progress', 'junit' ],

        junitReporter : {
            // Name of test result file should to have 'TEST-' prefix (for Jenkins reports).
            outputDir : '${project.build.directory}/surefire-reports'
        },

        // web server port
        port : '${UITest.unit.port}'
    });

};