// Karma E2E configuration
// this configuration works with karma 0.10.1
// also need to install scenario using following command:
// npm install -g karma-ng-scenario
module.exports = function (config) {
    config.set({
        frameworks: ['ng-scenario'],

        // base path, that will be used to resolve files and exclude
        basePath: '../',

        // list of files / patterns to load in the browser
        files: [
            '../../src/main/webapp/resources/lib/lodash/3.10.1/lodash.min.js',

            //load test utils
            'lib/e2e-helpers.js',

            //load page objects
            'js/e2e/pages-module.js',
            'js/e2e/pages/*.js',
            'js/e2e/pages/pricing/*.js',
            'js/e2e/dsls/*.js',

            //load specs.
            //open-application-scenarios.js should be executed first.
            'js/e2e/open-application-scenarios.js',
            'js/e2e/specs/**/*.js'
        ],

        proxies: {
            '/': 'http://localhost:8080/'
        },

        browsers: ['Chrome'],

        // list of files to exclude
        exclude: [],

        specReporter: {
            showSpecTiming: true
        },

        // test results reporter to use
        // possible values: dots || progress || growl
        reporters: ['progress', 'spec'],

        browserNoActivityTimeout: 120000,

        // web server port
        port: 9000,

        // cli runner port
        runnerPort: 9100,

        autoWatch: true
    });
};