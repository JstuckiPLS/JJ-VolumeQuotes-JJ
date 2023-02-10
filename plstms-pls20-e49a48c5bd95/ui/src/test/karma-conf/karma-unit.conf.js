// Karma Unit configuration
// this configuration works with karma 0.10.1
// also need to install ng-html2js using following command:
// npm install -g karma-ng-html2js-preprocessor
module.exports = function (config) {
    config.set({
        frameworks: ['jasmine'],

        // base path, that will be used to resolve files and exclude
        basePath: '../../../src/main/webapp/',

        // list of files / patterns to load in the browser
        files: [
            //load library
            'resources/lib/jquery/jquery.js',
            'resources/lib/jquery-jasmine/jasmine-jquery.js',
            'resources/lib/jquery/jquery-ui.js',
            'resources/lib/lodash/3.10.1/lodash.min.js',
            'resources/lib/angular/angular.js',
            'resources/lib/angular/angular-route.js',
            'resources/lib/angular/angular-sanitize.js',
            'resources/lib/angular/angular-resource.js',
            'resources/lib/angular/angular-cookies.js',
            'resources/lib/angular/angular-mocks.js',
            'resources/lib/angular/date.js',
            'resources/lib/angular/angular-timezones.js',
            'resources/lib/angular-http-auth/angular-http-auth.js',
            'resources/lib/angular-grid/js/ng-grid-layout.js',
            'resources/lib/angular-grid/js/ng-grid.js',
            'resources/lib/ui-layout/js/ui-layout.min.js',
            'resources/lib/angular-ng-upload/js/ng-upload.js',
            'resources/lib/angular-ui/ui-map.js',
            'resources/lib/angular-ui/calendar.js',
            'resources/lib/angular-ui/ui-utils/alias.js',
            'resources/lib/angular-ui/ui-utils/event.js',
            'resources/lib/angular-ui/ui-utils/format.js',
            'resources/lib/angular-ui/ui-utils/highlight.js',
            'resources/lib/angular-ui/ui-utils/ie-shiv.js',
            'resources/lib/angular-ui/ui-utils/include.js',
            'resources/lib/angular-ui/ui-utils/indeterminate.js',
            'resources/lib/angular-ui/ui-utils/inflector.js',
            'resources/lib/angular-ui/ui-utils/jq.js',
            'resources/lib/angular-ui/ui-utils/keypress.js',
            'resources/lib/angular-ui/ui-utils/mask.js',
            'resources/lib/angular-ui/ui-utils/reset.js',
            'resources/lib/angular-ui/ui-utils/route.js',
            'resources/lib/angular-ui/ui-utils/scrollfix.js',
            'resources/lib/angular-ui/ui-utils/showhide.js',
            'resources/lib/angular-ui/ui-utils/ui-scroll.js',
            'resources/lib/angular-ui/ui-utils/ui-scroll-jqlite.js',
            'resources/lib/angular-ui/ui-utils/unique.js',
            'resources/lib/angular-ui/ui-utils/utils.js',
            'resources/lib/angular-ui/ui-utils/validate.js',
            'resources/lib/ui-bootstrap/ui-bootstrap-0.7.0.js',
            'resources/lib/ui-bootstrap/ui-bootstrap-tpls-0.7.0.js',
            'resources/lib/toastr/js/toastr.min.js',
            'resources/lib/bootstrap/js/bootstrap.js',
            'resources/lib/text-angular/textAngular.js',

            // timezones for angular-timezones.js
            {pattern: 'resources/lib/angular/tz/data/*', included: false},

            //load test libraries
            '../../../src/test/lib/chai.js',
            '../../../src/test/lib/chai-expect.js',
            '../../../src/test/lib/chai-jquery.js',
            '../../../src/test/lib/helpers.js',

            //load application
            'resources/js/app.js',
            'resources/js/commons/**/*.js',
            'resources/js/admin/admin.js',
            'resources/js/admin/controllers/*.js',
            'resources/js/quotes/quotes.js',
            'resources/js/quotes/utils/cost-details-utils.js',
            'resources/js/quotes/controllers/*.js',
            'resources/js/quotes/directives/*.js',
            'resources/js/products/products-config.js',
            'resources/js/address-book/address-book-config.js',
            'resources/js/trackingBoard/trackingBoard.js',
            'resources/js/trackingBoard/controllers/*.js',
            'resources/js/trackingBoard/services/*.js',
            'resources/js/financialBoard/financialBoard.js',
            'resources/js/financialBoard/services/*.js',
            'resources/js/financialBoard/controllers/*.js',
            'resources/js/customer-activity/customer-activity.js',
            'resources/js/customer/customer.js',
            'resources/js/customer/controllers/*.js',
            'resources/js/users/users-config.js',
            'resources/js/pricing/**/*.js',
            'resources/js/sales-order/sales-order.js',
            'resources/js/sales-order/controllers/*.js',
            'resources/js/profile/*.js',
            'resources/js/profile/**/*.js',
            'resources/js/kpi/**/*.js',
            'resources/js/dashboard/*.js',
            'resources/js/dashboard/**/*.js',
            'resources/js/account/controllers/account-history-controllers.js',
            'resources/js/vendor-bill/vendor-bill.js',
            'resources/js/vendor-bill/controllers/*.js',
            'resources/js/invoices/invoices.js',
            'resources/js/shipment-entry/shipment-entry.js',
            'resources/js/shipment-entry/controllers/*.js',
            'resources/js/shipment-entry/services/*.js',
            'resources/js/commons/directives/typeahead/pls-address-directive.js',
            'resources/js/commons/services/shipment-dictionary-service.js',
            'resources/js/users/services/*.js',
            'resources/js/users/controllers/*.js',
            'resources/js/admin/services/*.js',
            'resources/js/products/services/products-services.js',
            'resources/js/customer/services/customer-services.js',
            'resources/js/customer/services/billto-services.js',
            'resources/js/quotes/services/rate-quote-services.js',
            'resources/js/quotes/services/shipment-services.js',
            'resources/js/sales-order/services/sales-order-services.js',
            'resources/js/report/reports.js',
            'resources/js/quotes/services/terminal-info-services.js',
            'resources/js/quotes/services/quote-services.js',
            'resources/js/manual-bol/manual-bol.js',
            'resources/js/manual-bol/services/manual-bol-service.js',
            'resources/js/manual-bol/controllers/manual-bol-general-information-controller.js',
            'resources/js/manual-bol/controllers/manual-bol-addresses-controller.js',
            'resources/js/manual-bol/controllers/manual-bol-details-controller.js',
            'resources/js/manual-bol/controllers/manual-bol-docs-controller.js',
            'resources/js/terms-and-conditions/terms-and-conditions.js',
            'resources/js/terms-and-conditions/controllers/terms-and-conditions-controller.js',
            'resources/js/terms-and-conditions/services/terms-and-conditions-services.js',

            'pages/tpl/*.html',
            'pages/cellTemplate/*.html',
            { pattern: 'resources/img/*.*', watched: false, included: false, served: true, nocache: false },

            '../../../src/test/karma-conf/globalDefinitions.js',

            '../../../src/test/js/unit/**/*.js'
        ],

        proxies: {
            '/pages/cellTemplate/': '/base/pages/cellTemplate/',
            '/test.png': '/base/resources/img/logo.png',
            '/resources/img/': '/base/resources/img/'
        },

        // list of files to exclude
        exclude: [],

        browsers: ['PhantomJS'],

        preprocessors: {
            'pages/tpl/**/*.html': 'ng-html2js'
        },

        // test results reporter to use
        // possible values: dots || progress || growl
        reporters: ['progress'],

        browserNoActivityTimeout: 60000,

        //web server port
        port: 9000,

        autoWatch: true
    });
};