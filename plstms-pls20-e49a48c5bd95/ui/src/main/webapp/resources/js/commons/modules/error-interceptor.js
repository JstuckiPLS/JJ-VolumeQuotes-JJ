/**
 * HTTP Requests Interceptor Module for AngularJS.
 *
 * @author Eugene Borshch
 */

// Interceptor checks for http requests (calls either $http or $resource services) and shows progress indicator if necessary.
// Therefore, there is no need to control progress indicator in your code because it will be done automatically.
// If several http request call simultaneously, progress indicator will be hidden when last request either will be success or failure
angular.module('http-interceptor', []);

angular.module('http-interceptor').factory('PlsProgressIndicatorInterceptor', ['$timeout', '$rootScope', function ($timeout, $rootScope) {
    'use strict';

    //array contains regular expression of http request to skip from showing progress indicator
    //IMPORTANT: for url expression putted here progress indicator will not be shown at all
    var urlToSkip = [
        new RegExp('.html'), //skip all html requests
        new RegExp('/auth'), //skip all auth requests
        new RegExp('/product-list-sort'), //skip product list sort requests
        new RegExp('/invoice/financial/board/errors.*/count'), //skip invoices errors count request
        new RegExp('/shipment/tracking/board/alert/count'), //skip alerts count request
        new RegExp('/customer/\\d*/shipment/\\d*/tooltip'), //skip tooltip request
        new RegExp('/customer/\\d*/manualbol/\\d*/tooltip'), //skip manualbol tooltip request
        new RegExp('/country'), //skip country auto-complete request
        new RegExp('/zip'), //skip zip auto-complete request
        new RegExp('/customer/idNameTuples'), //skip customer auto-complete request
        new RegExp('/user/parentOrganizationsByName'), //skip parent organization auto-complete request
        new RegExp('/user/filterEmail'), //skip user email auto-complete request
        new RegExp('/carrier/list/byName'), //skip carrier auto-complete request
        new RegExp('/customer/\\d*/product/filter'), //skip product auto-complete request
        new RegExp('/profile/\\d*/applicableCustomers'), //skip loading applicable customers for tariff
        new RegExp('/customer/accountExecByFilter'), //skip account executive auto-complete request
        new RegExp('/customer/\\d*/product/\\d+\\b'), //skip product info request
        new RegExp('/customer/\\d*/address/list/by_zip'), //skip address book by zip request
        new RegExp('/customer/\\d*/shipment/\\d*/events'), //skip shipment events request
        new RegExp('/customer/\\d*/saved/\\d*/details'), //skip saved quote details request
        new RegExp('/profile/export.*'), //skip all request regarding export prices
        new RegExp('/profile/import.*'), //skip all request regarding import prices
        new RegExp('/customer/\\d*/address/freightBill.*'), //skip freight bill pay to auto-complete request
        new RegExp('/shipment/analysis/checkValidationStatus/\\d+'), //skip freight analysis validation status check
        new RegExp('/shipment/analysis/getAnalysisJobs'), //skip freight analysis jobs check
        new RegExp('/announcement/unread') //skip unread announcement check
    ];

    $rootScope.serviceRequests = {};
    $rootScope.serviceRequestsOptions = {};

    //Sets text for the progress indicator. Value will be applied only if it call before http request. Default value is 'Processing...'
    $rootScope.setProgressText = function (progressText) {
        $rootScope.serviceRequestsOptions.progressText = progressText;
    };

    //Sets css classes for the progress indicator. Value will be applied only if it call before http request.
    //Default value is 'fixed-block well well-large semiOpaqueLoader'
    $rootScope.setProgressCssClass = function (panelClass) {
        $rootScope.serviceRequestsOptions.panelClass = panelClass;
    };

    function getDefaultProgressText(method) {
        switch (method || 'POST') {
            case 'GET':
                return 'Loading...';
            case 'DELETE':
                return 'Deleting...';
            default :
                return 'Processing...';
        }
    }

    function generateUrlKey(config) {
        var key = config.url;

        if (config.params) {
            key += _.reduce(config.params, function (result, value, key) {
                if (result) {
                    result += '&';
                } else {
                    result = '?';
                }
                return result + key + '=' + value;
            }, '');
        }

        return key;
    }

    //DO NOT USE THIS METHOD (only for interceptor use).
    $rootScope._startRequest = function (config) {
        if (config) {
            var urlToStart = config.url;

            if (urlToStart && !_.find(urlToSkip, function (exp) {
                        return exp.test(urlToStart);
                    })) {

                $rootScope.serviceRequests[generateUrlKey(config)] = true;

                if (!$rootScope.progressPanelOptions.showPanel) {
                    $rootScope.progressPanelOptions.progressText = $rootScope.serviceRequestsOptions.progressText ||
                            getDefaultProgressText(config.method);
                    $rootScope.progressPanelOptions.panelClass = $rootScope.serviceRequestsOptions.panelClass;
                    $rootScope.progressPanelOptions.showPanel = true;

                    $timeout(function () {
                        //fix for account calendar progress indicator
                    });
                }
            }
        }
    };

    var requestsToFinish = [];
    var finishTask;

    //DO NOT USE THIS METHOD (only for interceptor use).
    $rootScope._finishRequest = function (response) {
        if (!response || !response.config) {
            return;
        }

        var urlKey = generateUrlKey(response.config);

        if ($rootScope.serviceRequests && $rootScope.serviceRequests[urlKey]) {
            var sleepTime = 100;

            if (finishTask && $timeout.cancel(finishTask)) {
                requestsToFinish.push(urlKey);
            } else {
                requestsToFinish = [urlKey];
            }

            finishTask = $timeout(function () {
                _.each(requestsToFinish, function (url) {
                    $rootScope.serviceRequests[url] = false;
                });

                if (!_.find(_.values($rootScope.serviceRequests), function (value) {
                            return value;
                        })) {
                    $rootScope.progressPanelOptions.showPanel = false;
                    $rootScope.serviceRequests = {};
                    $rootScope.serviceRequestsOptions = {};
                }

                finishTask = undefined;
            }, sleepTime);
        }
    };

    return {
        'request': function (config) {
            $rootScope._startRequest(config);
            return config;
        },
        'response': function (response) {
            $rootScope._finishRequest(response);
            return response;
        }
    };
}]);

angular.module('http-interceptor').factory('PlsApplicationVersionInterceptor', ['$rootScope', function ($rootScope) {
    'use strict';

    var urlToSkip = [
        new RegExp('/invoice/financial/board/errors.*/count'), //skip invoices errors count request
        new RegExp('/shipment/tracking/board/alert/count') //skip alerts count request
    ];

    var TIMEOUT = 30 * 60 * 1000; // 30 minutes
    var time;
    var timeoutId;

    return {
        request: function (config) {
            if (!timeoutId) {
                timeoutId = setTimeout(function () {
                    if (config) {
                        var url = config.url;

                        if (url && !_.find(urlToSkip, function (exp) {
                                    return exp.test(url);
                                })) {

                            if (!time || new Date() - time > TIMEOUT) {
                                $rootScope.$emit('GetPLSVersionEvent');
                            }

                            time = new Date();
                        }
                    }

                    timeoutId = undefined;
                });
            }

            return config;
        }
    };
}]);

angular.module('http-interceptor').config(['$httpProvider', function ($httpProvider) {
    'use strict';

    var interceptor = ['$rootScope', '$q', function ($rootScope, $q) {
        return function (promise) {
            return promise.then(function (response) {
                if (response.status === 200 && response.config.method === 'POST') {
                    if (response.config.url.indexOf("freightBillPayTo") > -1) {
                        toastr.success('Saved Successfully!');
                    }
                }

                return response;
            }, function (response) {
                var message = response.config.method + ': ' + response.config.url;

                if (response.status === 404) {
                    toastr.error(message, 'Resource Not Found!');
                } else if (response.status === 500) {
                    $rootScope.$broadcast('event:server-error', response.config, response.data);
                }

                $rootScope._finishRequest(response);
                return $q.reject(response);
            });
        };
    }];

    $httpProvider.interceptors.push('PlsProgressIndicatorInterceptor');
    $httpProvider.interceptors.push('PlsApplicationVersionInterceptor');
    $httpProvider.responseInterceptors.push(interceptor);
}]);

/**
 * This directive will listen to http errors broadcasted by 'http-interceptor'
 * module.
 *
 * @author Eugene Borshch
 */
angular.module('http-interceptor').directive('httpErrorHandler', function () {
    return {
        restrict: 'C',
        link: function ($scope) {
            'use strict';

            $scope.$on('event:server-error', function (msg, config) {
                $scope.$root.$emit('event:application-error', 'Request has been unsuccessful!', config.method + " " + config.url);
            });
        }
    };
});