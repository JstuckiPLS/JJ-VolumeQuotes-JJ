// Karma E2E configuration for Jenkins environment.
// It extends main karma-e2e.conf.js config and overrides specific properties like 'proxies'.
// Also this file may contains expression like ${port} that will be processed by maven. Result file will stored into target sub directory.

var shared = require('./karma-e2e.conf');

module.exports = function(config) {
    shared(config);

    config.set({

        // test results reporter to use
        // possible values: dots || progress || growl
        reporters : [ 'progress', 'junit' ],

        junitReporter : {
            // Name of test result file should to have 'TEST-' prefix (for Jenkins reports).
            outputDir : '../../..'
        },

        browsers : [ 'Chrome', 'Firefox' ],

        proxies : {
            '/' : 'http://10.180.196.109:8080/'
        },

        // web server port
        port : 9002,

        // cli runner port
        runnerPort : 9102,
    });
};