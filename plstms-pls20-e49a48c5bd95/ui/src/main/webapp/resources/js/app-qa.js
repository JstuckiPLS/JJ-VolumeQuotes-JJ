angular.module('plsApp.directives.services', []);
angular.module('plsApp.directives', ['plsApp.directives.services']);
angular.module('plsApp.common.services', []);
angular.module('plsApp.utils', ['plsApp.common.services']);
angular.module('plsApp.customFilters', ['plsApp.utils']);

angular.module('pls.controllers', []);

// Declare app level module which depends on filters, and services
angular.module('plsApp', [
    'ngRoute',
    'ngSanitize',
    'http-auth-interceptor',
    'ui.layout',
    'ui.bootstrap',
    'ui.bootstrap.tpls',
    'ui.calendar',
    'ui.event',
    'ui.map'/* <- used by google maps*/,
    'ngGrid',
    'ngUpload',
    'ngCookies',
    'textAngular',

    'ng-google-chart',
    'http-interceptor',
    'plsApp.customFilters',
    'plsApp.utils',
    'plsApp.directives',
    'users',

    'quotesServices',
    'productsServices',
    'addressBookServices',
    'financialBoardServices',
    'trackingBoardServices',
    'customerActivityServices',
    'customer',
    'pricing',
    'salesOrderServices',
    'myProfile',
    'kpiServices',
    'dashboard',
    'vendorBillServices',
    'invoicesServices',
    'shipmentEntry',
    'reports',
    'pls.controllers',
    'manualBol',
    'admin',
    'termsAndConditions'
]);

/**
 * URLs available without permissions.
 */
var AVAILABLE_URLS = ['#/document'];

/**
 * Contains mapping URL to required privileges.
 * Privileges separated by comma means at least one privilege  must exist to access URL.
 * If none privileges are defined than URL is accessible for everyone
 */
var URLPrivilegesMapping = {
    '#/dashboard': 'CAN_ACCESS_CUSTOMER_DASHBOARD,CAN_ACCESS_SALES_REP_DASHBOARD,CAN_ACCESS_MANAGER_DASHBOARD',
    '#/shipment-entry': 'ADD_SHIPMENT_ENTRY',
    '#/manual-bol': 'CREATE_MANUAL_BOL,EDIT_MANUAL_BOL',
    '#/manual-bol/general-information': 'CREATE_MANUAL_BOL,EDIT_MANUAL_BOL',
    '#/manual-bol/addresses': 'CREATE_MANUAL_BOL,EDIT_MANUAL_BOL',
    '#/manual-bol/details': 'CREATE_MANUAL_BOL,EDIT_MANUAL_BOL',
    '#/manual-bol/docs': 'CREATE_MANUAL_BOL,EDIT_MANUAL_BOL',
    '#/quotes': 'QUOTES_VIEW,ACTIVE_SHIPMENTS_PAGE_VIEW',
    '#/quotes/quot': 'QUOTES_VIEW', // dirty hack, because users can't click F5 to refresh screen
    '#/quotes/quote': 'QUOTES_VIEW',
    '#/quotes/saved': 'QUOTES_VIEW',
    '#/quotes/active': 'ACTIVE_SHIPMENTS_PAGE_VIEW',
    '#/account-history': 'ACCOUNT_HISTORY_PAGE_VIEW,ACCOUNT_HISTORY_CALENDAR_VIEW',
    '#/account-history/search': 'ACCOUNT_HISTORY_PAGE_VIEW',
    '#/account-history/calendar': 'ACCOUNT_HISTORY_CALENDAR_VIEW',
    '#/address-book': 'ADD_EDIT_ADDRESS_BOOK_PAGE,DELETE_ADDRESS,IMPORT_ADDRESS,VIEW_ADDRESS_ONLY',
    '#/address-book/address-book-list': 'ADD_EDIT_ADDRESS_BOOK_PAGE,DELETE_ADDRESS,IMPORT_ADDRESS,VIEW_ADDRESS_ONLY',
    '#/products': 'ADD_EDIT_PRODUCT,DELETE_PRODUCT,IMPORT_PRODUCT,VIEW_PRODUCTS_ONLY',
    '#/products/products-list': 'ADD_EDIT_PRODUCT,DELETE_PRODUCT,IMPORT_PRODUCT,VIEW_PRODUCTS_ONLY',
    '#/invoices': 'INVOICES_REPORTS_PAGE_VIEW,INVOICES_CREDIT_BILLING_PAGE_VIEW',
    '#/invoices/invoices': 'INVOICES_REPORTS_PAGE_VIEW',
    '#/invoices/credit-billing': 'INVOICES_CREDIT_BILLING_PAGE_VIEW',
    '#/user-mgmt': 'USERS_PAGE_VIEW,ADD_EDIT_ROLE,DELETE_ROLE',
    '#/user-mgmt/users': 'USERS_PAGE_VIEW',
    '#/user-mgmt/roles': 'ADD_EDIT_ROLE,DELETE_ROLE',
    '#/user-mgmt/announcements': 'CAN_MANAGE_ANNOUNCEMENTS',
    '#/sales-order': 'SALES_ORDER_VIEW',
    '#/sales-order/create': 'SALES_ORDER_VIEW',
    '#/customer': 'CUSTOMER_PROFILE_VIEW,VIEW_ACTIVE_CUSTOMER_PROFILE',
    '#/pricing': 'PRICING_PAGE_VIEW,UPDATE_DEFAULT_MARGIN,UPDATE_CARRIER_BLOCK,UPDATE_BLOCK_LANE,PERFORM_FREIGHT_ANALYSIS',
    '#/pricing/tariffs': 'PRICING_PAGE_VIEW,PERFORM_FREIGHT_ANALYSIS',
    '#/pricing/tariffs/active': 'PRICING_PAGE_VIEW',
    '#/pricing/tariffs/archived': 'PRICING_PAGE_VIEW',
    '#/pricing/tariffs/analysis': 'PERFORM_FREIGHT_ANALYSIS',
    '#/pricing/fuel': 'PRICING_PAGE_VIEW',
    '#/pricing/scac-codes': 'PRICING_PAGE_VIEW,SCAC_CODES_PAGE_VIEW',
    '#/pricing/acc-types': 'PRICING_PAGE_VIEW',
    '#/pricing/customer': 'PRICING_PAGE_VIEW,UPDATE_DEFAULT_MARGIN,UPDATE_CARRIER_BLOCK,UPDATE_BLOCK_LANE',
    '#/trackingBoard': 'BOARD_ALERT_PAGE_VIEW,BOARD_BOOKED_PAGE_VIEW,VIEW_ACTIVE_SHIPMENTS,VIEW_ACTIVE_SHIPMENTS_REVENUE_ONLY,' +
            'VIEW_ACTIVE_SHIPMENTS_COST_DETAILS,BOARD_DELIVERED_PAGE_VIEW,BOARD_OPEN_PAGE_VIEW,VIEW_ALL_SHIPMENTS,VIEW_ALL_SHIPMENTS_REVENUE_ONLY,' +
            'VIEW_ALL_SHIPMENTS_COST_DETAILS,VIEW_MANUAL_BOL',
    '#/trackingBoard/alerts': 'BOARD_ALERT_PAGE_VIEW',
    '#/trackingBoard/booked': 'BOARD_BOOKED_PAGE_VIEW',
    '#/trackingBoard/undelivered': 'VIEW_ACTIVE_SHIPMENTS,VIEW_ACTIVE_SHIPMENTS_REVENUE_ONLY,VIEW_ACTIVE_SHIPMENTS_COST_DETAILS',
    '#/trackingBoard/unbilled': 'BOARD_DELIVERED_PAGE_VIEW',
    '#/trackingBoard/open': 'BOARD_OPEN_PAGE_VIEW',
    '#/trackingBoard/all': 'VIEW_ALL_SHIPMENTS,VIEW_ALL_SHIPMENTS_REVENUE_ONLY,VIEW_ALL_SHIPMENTS_COST_DETAILS',
    '#/trackingBoard/hold': 'CAN_PUT_LOADS_ON_HOLD',
    '#/trackingBoard/manualBol': 'VIEW_MANUAL_BOL',
    '#/financialBoard': 'FIN_BOARD_TRANSACTIONAL_PAGE_VIEW,FIN_BOARD_CONSOLIDATED_PAGE_VIEW,FIN_BOARD_AUDIT_PAGE_VIEW,FIN_BOARD_ERRORS_VIEW,' +
            'FIN_BOARD_HISTORY_VIEW',
    '#/financialBoard/transactional': 'FIN_BOARD_TRANSACTIONAL_PAGE_VIEW',
    '#/financialBoard/consolidated': 'FIN_BOARD_CONSOLIDATED_PAGE_VIEW',
    '#/financialBoard/audit': 'FIN_BOARD_AUDIT_PAGE_VIEW',
    '#/financialBoard/price': 'VIEW_PRICE_AUDIT',
    '#/financialBoard/errors': 'FIN_BOARD_ERRORS_VIEW',
    '#/financialBoard/history': 'FIN_BOARD_HISTORY_VIEW',
    '#/vendorBill': 'VENDOR_BILL_PAGE_VIEW,VEND_BILL_ARCHIVED_VIEW',
    '#/vendorBill/unmatched': 'VENDOR_BILL_PAGE_VIEW',
    '#/vendorBill/archived': 'VEND_BILL_ARCHIVED_VIEW',
    '#/my-profile': 'EDIT_MY_PROFILE,CHANGE_PASSWORD,VIEW_MY_PROFILE',
    '#/reports': 'EXECUTE_UNBILLED_REPORT,EXECUTE_SAVINGS_REPORT,EXECUTE_ACTIVITY_REPORT,EXECUTE_LOST_SAVINGS_OPPORTUNITY_REPORT,' +
            'EXECUTE_SHIPMENT_CREATION_REPORT,BATCHED_INVOICE_REPORT',
    '#/kpi': 'DASHBOARD_SUMMARIES_VIEW,DASHBOARD_ACTIVITIES_VIEW,DASHBOARD_ANALISYS_VIEW',
    '#/admin': 'ADMIN_LOG_VIEW',
    '#/admin/logs': 'ADMIN_LOG_VIEW',
    '#/termsAndConditions': 'ACCOUNT_EXECUTIVE'
};

// ORDER OF THESE ITEMS IS IMPORTANT !!! IT SHOULD BE THE SAME AS ORDER OF MENU ITEMS
var PLSUserMenu = [
    '#/dashboard',
    '#/shipment-entry',
    '#/manual-bol',
    '#/quotes',
    '#/sales-order',
    '#/vendorBill',
    '#/trackingBoard',
    '#/financialBoard',
    '#/pricing',
    '#/customer',
    '#/products',
    '#/address-book',
    '#/reports',
    '#/kpi',
    '#/user-mgmt',
    '#/my-profile',
    '#/admin'
];

var CustomerUserMenu = [
    '#/dashboard',
    '#/shipment-entry',
    '#/manual-bol',
    '#/quotes',
    '#/invoices',
    '#/trackingBoard',
    '#/account-history',
    '#/customer',
    '#/products',
    '#/address-book',
    '#/reports',
    '#/kpi',
    '#/user-mgmt',
    '#/my-profile'
];

/**
 * Remove #/ signs from the beginning of URL and query parameter starting with ? sign
 */
function cleanupUrl(url) {
    'use strict';

    if (!url) {
        return url;
    }

    if (url.charAt(0) === '#') {
        url = url.substring(1);
    }

    if (url.charAt(0) === '/') {
        url = url.substring(1);
    }

    return url.split('?')[0];
}

/**
 * Constructor for authData object that stored in root scope
 * ($rootScope.authData).<br />
 * <p>
 * Normally this object should be created only in login controller. In other
 * places you should only use this object using root scope
 * ($rootScope.authData).
 * </p>
 *
 * @param currentUserData
 *            JSON data that returns '/auth/current_user' server method. Put null if login was filed.
 * @class
 */
function AuthData(currentUserData) {
    'use strict';

    var self = this;
    /**
     * Person ID of current user.
     */
    self.personId = null;

    /**
     * Full name of current user.
     */
    self.firstName = "";

    /**
     * Full name of current user.
     */
    self.fullName = "";

    /**
     * true if it is PLS user. False if user was not logged in or he/she customer user.
     */
    self.plsUser = false;

    /**
     * true if it is customer user. False if user was not logged in or he/she PLS user.
     */
    self.customerUser = false;

    /**
     * Array of strings that contains all privileges for current user.
     */
    self.privilegies = [];

    /**
     * Information about primary user organization.
     */
    self.organization = {

        /**
         * Name of organization.
         */
        name: "",

        /**
         * ID of the organization.
         */
        orgId: null
    };

    /**
     * Information about assigned organization. Applicable when only one organization is assigned to current user.
     */
    self.assignedOrganization = undefined;

    /**
     * true if login was successes.
     */
    self.loginSuccess = function () {
        return self.plsUser || self.customerUser;
    };

    /**
     * true if login was failed.
     */
    self.loginFailed = function () {
        return !(self.loginSuccess());
    };

    if (currentUserData) {
        self.personId = currentUserData.personId;
        self.firstName = currentUserData.firstName;
        self.fullName = currentUserData.fullName;
        self.plsUser = currentUserData.plsUser !== undefined && (currentUserData.plsUser === 'true' || currentUserData.plsUser === true);
        self.customerUser = !self.plsUser;

        if (currentUserData.privilegies) {
            self.privilegies = currentUserData.privilegies;
        }

        if (currentUserData.organization) {
            self.organization = currentUserData.organization;
        }

        self.assignedOrganization = currentUserData.assignedOrganization;
    }

    /**
     * Check whether it possible or not to access url with defined privileges.
     */
    self.canAccessURL = function (url) {
        var urlToCheck = url;

        if (!urlToCheck || self.loginFailed()) {
            return true;
        }

        urlToCheck = cleanupUrl(urlToCheck);

        var urlParts = urlToCheck.split('/');

        // check if URL is available for any user
        if (_.contains(AVAILABLE_URLS, '#/' + urlParts[0])) {
            return true;
        }

        // check if URL is available for specified user role from menu
        var menuItems;

        if (self.plsUser) {
            menuItems = PLSUserMenu;
        } else {
            menuItems = CustomerUserMenu;
        }

        if (_.find(menuItems, function (item) {
                    var contains = item.indexOf(urlParts[0]) === -1;
                })) {
            return false;
        }

        // check if URL is available according to permissions
        var roles, i;

        for (i = urlParts.length; i > 0 && !roles; i -= 1) {
            roles = URLPrivilegesMapping['#/' + urlParts.slice(0, i).join('/')];
        }

        if (!roles) {
            return false;
        }

        return !_.isEmpty(_.intersection(self.privilegies, roles.split(',')));
    };
}

angular.module('plsApp').config(['$tooltipProvider', '$provide', function ($tooltipProvider, $provide) {
    'use strict';

    $tooltipProvider.setTriggers({
        'mouseenter': 'mouseleave',
        'click': 'click',
        'focus': 'blur',
        'never': 'mouseleave' // <- This line ensures the tooltip will go away on mouseleave (Otherwise tooltip is working incorrectly in FF)
    });

    $provide.service('PlsUtils', function () {
        this.generateUUID = function guid() {
            function _p8(s) {
                var p = (Math.random().toString(16) + "000000000").substr(2, 8);
                return s ? "-" + p.substr(0, 4) + "-" + p.substr(4, 4) : p;
            }

            return _p8() + _p8(true) + _p8(true) + _p8();
        };
    });
}]);

// REST configuration. Maybe it would be good idea to move it somewhere that it
// could be initialized during build time
//
// - $http -> http://127.0.0.1:8080/restful
// - $resource -> http://107.21.228.221\\:8080/restful  (we need to escape port!)
//

angular.module('ngRoute').value("urlConfig", {
    // The basic idea is to have separate configurations for
    // each feature(functional block/module or whatever).
    // So, if they(RESTs) would be deployed separately we could easily switch.
    shipment: "/restful",
    pricing: "/restful",
    login: "/restful",
    core: '/restful',
    userMgmt: "/restful",
    indexPage: "/my-freight/index.html#",
    appContext: "/my-freight/#",
    termsAndConditions:"/restful/termsAndConditions" 
});

// Sorry, so brutal, but we need to support IE.
// This object holds specific browser info (its certain type and version)
// to align progressive search input margins in getWidth() function below
var browserDetect = {
    init: function () {
        this.browser = this.searchString(this.dataBrowser) || "Other";
        this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || "Unknown";
    },

    searchString: function (data) {
        var i;
        for (i = 0; i < data.length; i += 1) {
            var dataString = data[i].string;
            this.versionSearchString = data[i].subString;

            if (dataString.indexOf(data[i].subString) !== -1) {
                return data[i].identity;
            }
        }
    },

    searchVersion: function (dataString) {
        var index = dataString.indexOf(this.versionSearchString);

        if (index === -1) {
            return;
        }

        if (this.versionSearchString === "Trident") {
            return parseFloat(dataString.substring(dataString.indexOf('rv:') + 3));
        }

        return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
    },

    dataBrowser: [
        {string: navigator.userAgent, subString: "Chrome", identity: "Chrome"},
        {string: navigator.userAgent, subString: "MSIE", identity: "Explorer"},
        {string: navigator.userAgent, subString: "Trident", identity: "Explorer"},
        {string: navigator.userAgent, subString: "Firefox", identity: "Firefox"},
        {string: navigator.userAgent, subString: "Safari", identity: "Safari"},
        {string: navigator.userAgent, subString: "Opera", identity: "Opera"}
    ]
};

angular.module('plsApp').run([
    '$rootScope', '$route', '$filter', '$location', '$window', '$templateCache', '$sortService', '$resource', 'DateTimeUtils', 'UserSettingsService',
    function ($rootScope, $route, $filter, $location, $window, $templateCache, $sortService, $resource, DateTimeUtils, UserSettingsService) {
        'use strict';

        // inject LoDash to $rootScope
        $rootScope._ = _;

        $rootScope.route = $route;

        $rootScope.browserDetect = browserDetect;

        /**
         * Information about current user.
         */
        $rootScope.authData = new AuthData(null);

        window.ngGrid.config = {headerRowHeight: 24};

        $rootScope.SAFWAY_ID = 206962;
        $rootScope.SID_HARVEY = 193362;
        $rootScope.SAFEWORKS_ID = 331702;
        $rootScope.BRAND_INDUSTRIAL_SERVICES_ID = 349324;
        $rootScope.ALUMA_SYSTEMS_ID = 358411;

        $rootScope.isSafway = function(customerId) {
            return parseInt(customerId, 10) === $rootScope.SAFWAY_ID;
        };

        $rootScope.isSafeworks = function(customerId) {
            return parseInt(customerId, 10) === $rootScope.SAFEWORKS_ID;
        };

        $rootScope.isBrandIndustrialServices = function(customerId) {
            return parseInt(customerId, 10) === $rootScope.BRAND_INDUSTRIAL_SERVICES_ID;
        };

        $rootScope.isBrandOrAluma = function(customerId) {
            var id = parseInt(customerId, 10);
            return id === $rootScope.BRAND_INDUSTRIAL_SERVICES_ID || id === $rootScope.ALUMA_SYSTEMS_ID;
        };

        $rootScope.isSidHarvey = function(customerId) {
            return parseInt(customerId, 10) === $rootScope.SID_HARVEY;
        };

        /**
         * Check whether you have certain permission.
         * TODO find a better place for this.
         */
        $rootScope.isFieldRequired = function (roleName) {
            return _.indexOf($rootScope.authData.privilegies, roleName) !== -1;
        };

        /*
         * Check whether the user has permissions.
         * example of use : isPlsPermission("PERMISSION_NAME || PERMISSION_NAME")
         * return boolean
         */
        $rootScope.isPlsPermissions = function (permissions) {
            // TODO find best way.
            var parsedValue = permissions.replace(new RegExp('\\w*', 'g'), function (subStr) {
                return subStr !== "" ? $rootScope.isFieldRequired(subStr) : "";
            });

            return $rootScope.$eval(parsedValue);
        };

        $rootScope.formatDate = function (date, format) {
            if (!format) {
                format = $rootScope.transferDateFormat;
            }

            return $filter('date')(date, format);
        };

        $rootScope.parseISODate = DateTimeUtils.parseISODate;

        // fixing ng-grid template for aggregated row
        $templateCache.put("aggregateTemplate.html", $templateCache.get("aggregateTemplate.html").replace(
                '{{row.totalChildren()}} {{AggItemsLabel}}', '{{row.totalChildren()}}{{AggItemsLabel}}'
        ));

        //  customize upload file form
        $rootScope.$watch(function () {
            if ($('.filestyle').length) {
                $('.filestyle').each(function () {
                    var $this = $(this), options = {
                        'buttonText': $this.attr('data-buttonText'),
                        'input': $this.attr('data-input') === 'false' ? false : true,
                        'icon': $this.attr('data-icon') === 'false' ? false : true,
                        'classButton': $this.attr('data-classButton'),
                        'classInput': $this.attr('data-classInput'),
                        'classIcon': $this.attr('data-classIcon')
                    };

                    $this.filestyle(options);
                });
            }
        });

        /**
         * NgGrid caches sorting functions for columns based on field name.
         * Instance of $sortService is a singleton in scope of user session.
         * So we must always clear it's cache.
         */
        $rootScope.$watch(function () {
            return Object.keys($sortService.colSortFnCache).length;
        }, function (newVal) {
            if (newVal) {
                // remove all keys from object
                angular.copy({}, $sortService.colSortFnCache);
            }
        });

        // common format for date transfering
        $rootScope.transferDateFormat = "yyyy-MM-dd";
        $rootScope.appDateFormat = "MM/dd/yy";
        $rootScope.appDateTimeFormat = "MM/dd/yy hh:mm a";
        $rootScope.wideAppDateFormat = "EEE MM/dd/yy";
        $rootScope.monthYearDateFormat = "MMM yyyy";
        $rootScope.exportFileNameDateFormat = 'MM.dd.yyyy';
        $rootScope.appDateFullFormat = "MM/dd/yyyy";
        $rootScope.fullAppDateTimeFormat = "MM/dd/yyyy hh:mm a";

        toastr.options = {
            positionClass: "toast-top-right",
            timeOut: 10000,
            extendedTimeOut: 20000,
            closeButton: true,
            closeDuration: 0,
            progressBar: true,
            tapToDismiss: false
        };

        function removeToasters() {
            if (toastr.getContainer().children().length >= 2) {
                toastr.clear($(toastr.getContainer().children()[1]));
            }
        }

        $rootScope.$on('event:application-error', function (event, header, message) {
            removeToasters();
            toastr.error(message, header);
        });
        $rootScope.$on('event:application-warning', function (event, header, message) {
            removeToasters();
            toastr.warning(message, header);
        });
        $rootScope.$on('event:operation-success', function (event, header, message) {
            removeToasters();
            toastr.success(message, header);
        });
        $rootScope.$on('event:application-warning-nopacity', function (event, header, message) {
            removeToasters();
            toastr.warning(message, header, {toastClass: 'toast-nopacity'});
        });

        $rootScope.progressPanelOptions = {
            showPanel: false,
            progressText: 'Processing...'
        };

        $rootScope.jasperReportsObj = {
            URL: '/PLSpro/',
            name: "pls20dashboard",
            password: "S00p3rior1"
        };

        browserDetect.init();

        $rootScope.BreadCrumb = function (id, label) {
            this.id = id;
            this.label = label;

            this.prev = undefined;
            this.next = undefined;
            this.validNext = undefined;
            this.nextAction = undefined;
            this.validDone = undefined;
            this.doneAction = undefined;
        };

        $rootScope.ignoreLocationChangeFlag = false;
        var urlRegEx = new RegExp('#/.+');

        $rootScope.$on('$locationChangeStart', function (event, newUrl, oldUrl) {
            if (newUrl && newUrl !== oldUrl && event.currentScope.authData.personId) {
                if ($window.blockLocationChange($rootScope.ignoreLocationChangeFlag)) {
                    if (!confirm('The current page contains unsaved information that will be lost if you leave this page.')) {
                        event.preventDefault();
                    }
                } else {
                    $rootScope.ignoreLocationChangeFlag = false;
                }

                var urlToCheck = urlRegEx.exec(newUrl);

                if (urlToCheck && urlToCheck.length && !event.currentScope.authData.canAccessURL(urlToCheck[0])) {
                    $rootScope.$emit('event:application-error', 'URL Access Error',
                            'URL \'' + newUrl + '\' is not accessible due to lack of privileges or invalid.');
                    event.preventDefault();
                }
            }
        });

        $rootScope.ignoreLocationChange = function () {
            $rootScope.ignoreLocationChangeFlag = true;
        };

        function navigateToURL(url, params) {
            // find first available sub-menu (or tab) item link
            var urlToNavigate = _.find(_.keys(URLPrivilegesMapping), function (searchingUrl) {
                        return searchingUrl.indexOf(url) === 0 && searchingUrl !== url && $rootScope.authData.canAccessURL(searchingUrl);
                    }) || url;

            if (urlToNavigate.charAt(0) === '#') {
                urlToNavigate = urlToNavigate.slice(1);
            }

            if (params && !_.isEmpty(params)) {
                $location.path(urlToNavigate).search(params);
            } else {
                $location.path(urlToNavigate);
            }
        }

        $rootScope.redirectToUrl = function (url, params) {
            url = cleanupUrl(url);

            if (url) {
                if ($rootScope.authData.canAccessURL(url)) {
                    navigateToURL(url, params);
                    return;
                }

                var urlParts = url.split('/');
                if (urlParts.length > 2) {
                    // this code allows to access third level url covered with specific permission e.g. #/pricing/tariffs/analysis
                    var urlPart = urlParts[0] + '/'+ urlParts[1];
                    if ($rootScope.authData.canAccessURL(urlPart)) {
                        navigateToURL('#/' + urlPart, params);
                        return;
                    }
                }
                var firstUrlPart = urlParts[0];

                if ($rootScope.authData.canAccessURL(firstUrlPart)) {
                    navigateToURL('#/' + firstUrlPart, params);
                    return;
                }
            }

            if ($rootScope.authData.loginFailed() || (!url && cleanupUrl($location.path()) && $rootScope.authData.canAccessURL($location.path()))) {
                return;
            }

            var urls;

            if ($rootScope.authData.plsUser) {
                urls = PLSUserMenu;
            } else {
                urls = CustomerUserMenu;
            }

            // find first available url
            navigateToURL(_.find(urls, function (searchingUrl) {
                return $rootScope.authData.canAccessURL(searchingUrl);
            }), params);
        };

        $rootScope.$on('$locationChangeSuccess', function () {
            $rootScope.redirectToUrl($location.path());
        });

        $rootScope.$on('$routeChangeSuccess', function () {
            ga('send', 'pageview', $location.path());
        });

        $rootScope.$on('GetPLSVersionEvent', function () {
            if (typeof ignoreVersionChange === 'undefined') {
                $resource('resources/version.json').get(function (response) {
                    if (response && response.build && response.build.version && $rootScope.PLS_UI_VERSION !== response.build.version) {
                        if ($rootScope.PLS_UI_VERSION) {
                            $rootScope.applicationMustBeRestarted = true;
                        } else {
                            $rootScope.PLS_UI_VERSION = response.build.version;
                        }
                    }
                });
            }
        });

        $rootScope.reloadApplication = function () {
            $window.location.reload();
        };
        
        $rootScope.$watch('authData', function (authData) {
            if(authData && authData.personId){
                UserSettingsService.getUserSettings({}, function (data) {
                    var userSettings = {};
                    var i;
                    for(i=0; i<data.length; i=i+1){
                        userSettings[data[i].key] = data[i].value;
                    }
                    $rootScope.userSettings = userSettings;
                });
            }
        });
    }
]);

// init
google.load('visualization', '1.0', {'packages': ['corechart']});

function blockLocationChange(ignoreLocationChange) {
    'use strict';

    return !ignoreLocationChange
            && $(':input:visible:enabled:not(:button):not(.search-query):not(.search-field):not([readonly])').filter(function () {
                return $(this).parentsUntil('div[data-pls-modal]:visible', 'div[data-pls-ignore-location-change-check="true"]').length === 0;
            }).length !== 0;
}

var ignoreUnload = false;

$(window).bind('beforeunload', function () {
    'use strict';

    var isNotLoginPage = $("#content").length > 0; //Do not warn user about leaving the login page
    var isDocumentPage = window.location.href.indexOf('/document/') !== -1; //Do not warn user about redirecting to document

    if (isNotLoginPage && !isDocumentPage && !ignoreUnload && blockLocationChange(false)) {
        return 'The current page contains unsaved information that will be lost if you leave this page.';
    } else {
        ignoreUnload = false;
    }
});
