angular.module('pricing', ['ngResource']);

angular.module('pricing').config(['$routeProvider', function ($routeProvider) {
    var routes = {
        "profile": {
            "pricingTab": "pages/content/pricing/profile/profile-tabs.html",
            "profileTab": "pages/content/pricing/profile/details/profile-details.html"
        },
        "profile.details": {
            "profileTab": "pages/content/pricing/profile/details/profile-details.html"
        },
        "profile.docs": {
            "profileTab": "pages/content/pricing/profile/docs.html"
        },
        "profile.prohibited-liability": {
            "profileTab": "pages/content/pricing/profile/profile-prohibited-liability.html"
        },
        "profile.notes": {
            "profileTab": "pages/content/pricing/profile/profile-notes.html"
        },
        "pricing": {
            "pricingTab": "pages/content/pricing/profile/pricing/pric-tabs.html",
            "detailsTab": "pages/content/pricing/profile/pricing/pric-details.html"
        },
        "pricing.details": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-details.html"
        },
        "pricing.accessorials": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-accessorials.html"
        },
        "pricing.guaranteed": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-guaranteed.html"
        },
        "pricing.fuel-table": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-fuel-table.html"
        },
        "pricing.fuel-triggers": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-fuel-triggers.html"
        },
        "pricing.freight-bill": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-freight-bill.html"
        },
        "pricing.terminal": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-terminal.html"
        },
        "pricing.block-carrier-zip": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-block-carrier-zip.html"
        },
        "pricing.zones": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-zones.html"
        },
        "pricing.pallet": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-pallet.html"
        },
        "buy": {
            "pricingTab": "pages/content/pricing/profile/pricing/pric-tabs.html",
            "detailsTab": "pages/content/pricing/profile/pricing/pric-details.html"
        },
        "buy.details": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-details.html"
        },
        "buy.accessorials": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-accessorials.html"
        },
        "buy.guaranteed": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-guaranteed.html"
        },
        "buy.fuel-table": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-fuel-table.html"
        },
        "buy.fuel-triggers": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-fuel-triggers.html"
        },
        "buy.freight-bill": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-freight-bill.html"
        },
        "buy.terminal": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-terminal.html"
        },
        "buy.block-carrier-zip": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-block-carrier-zip.html"
        },
        "buy.zones": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-zones.html"
        },
        "buy.pallet": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-pallet.html"
        },
        "sell": {
            "pricingTab": "pages/content/pricing/profile/pricing/pric-tabs.html",
            "detailsTab": "pages/content/pricing/profile/pricing/pric-details.html"
        },
        "sell.details": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-details.html"
        },
        "sell.accessorials": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-accessorials.html"
        },
        "sell.guaranteed": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-guaranteed.html"
        },
        "sell.fuel-table": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-fuel-table.html"
        },
        "sell.fuel-triggers": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-fuel-triggers.html"
        },
        "sell.freight-bill": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-freight-bill.html"
        },
        "sell.terminal": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-terminal.html"
        },
        "sell.block-carrier-zip": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-block-carrier-zip.html"
        },
        "sell.zones": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-zones.html"
        },
        "sell.pallet": {
            "detailsTab": "pages/content/pricing/profile/pricing/pric-pallet.html"
        }
    };

    $routeProvider
            .when('/pricing', {redirectTo: '/pricing/tariffs/active'})
            .when('/pricing/tariffs', {redirectTo: '/pricing/tariffs/active'})
            .when('/pricing/tariffs/active', {
                templateUrl: 'pages/content/pricing/pricing-tabs.html',
                pricingTab: 'pages/content/pricing/tariffs/pricing-tariffs.html',
                active: true
            })
            .when('/pricing/tariffs/:pricingId/:operationType', {
                templateUrl: 'pages/content/pricing/profile/profile.html',
                controller: 'PricingProfileCtrl',
                resolve: {
                    "routes": function () {
                        return routes;
                    }
                },
                reloadOnSearch: true
            })
            .when('/pricing/tariffs/archived', {
                templateUrl: 'pages/content/pricing/pricing-tabs.html',
                pricingTab: 'pages/content/pricing/tariffs/pricing-tariffs.html'
            })
            .when('/pricing/tariffs/analysis', {
                templateUrl: 'pages/content/pricing/pricing-tabs.html',
                pricingTab: 'pages/content/pricing/tariffs/pricing-analysis.html'
            })
            .when('/pricing/fuel', {
                templateUrl: 'pages/content/pricing/pricing-tabs.html',
                pricingTab: 'pages/content/pricing/fuel/pricing-fuel.html'
            })
            .when('/pricing/customer', {
                templateUrl: 'pages/content/pricing/pricing-tabs.html',
                pricingTab: 'pages/content/pricing/customer/pricing-customer.html'
            })
            .when('/pricing/customer/:customerId', {
                templateUrl: 'pages/content/pricing/pricing-tabs.html',
                pricingTab: 'pages/content/pricing/customer/pricing-customer-profile.html'
            })
            .when('/pricing/customer/:customerId/p44Config', {
                templateUrl: 'pages/content/pricing/pricing-tabs.html',
                pricingTab: 'pages/content/pricing/customer/p44-config.html'
            })
            .when('/pricing/customer/:customerId/blockLane', {
                templateUrl: 'pages/content/pricing/pricing-tabs.html',
                pricingTab: 'pages/content/pricing/customer/block-lane.html'
            })
            .when('/pricing/scac-codes', {
                templateUrl: 'pages/content/pricing/pricing-tabs.html',
                pricingTab: 'pages/content/pricing/scac/pricing-scac.html'
            })
            .when('/pricing/acc-types', {
                templateUrl: 'pages/content/pricing/pricing-tabs.html',
                pricingTab: 'pages/content/pricing/acc-types/pricing-acc-types.html'
            });
}]);