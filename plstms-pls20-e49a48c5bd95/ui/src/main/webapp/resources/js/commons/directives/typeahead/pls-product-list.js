/**
 * AngularJS directive which displays editable combo-box for product.
 */
angular.module('plsApp.directives').directive('plsProductList', ['ProductFilterService', function (ProductFilterService) {
    return {
        restrict: 'A',
        scope: {
            selectedProduct: '=plsProductList',
            mandatory: "=",
            commodityClass: '=',
            hazmatOnly: '=',
            customerId: '='
        },
        replace: true,
        templateUrl: 'pages/tpl/product-list-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.findProducts = function (selectedCustomer, searchString, commodityClass, hazmatOnly) {
                searchString = searchString === ' ' ? '' : searchString;
                return ProductFilterService.filter(selectedCustomer, searchString, commodityClass, hazmatOnly);
            };

            $scope.getProductListIndex = function() {
                return document.getElementById('productList') ? document.getElementById('productList').tabIndex : 0;
            };
        }]
    };
}]);
