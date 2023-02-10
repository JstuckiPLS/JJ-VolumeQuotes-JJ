/**
 * AngularJS directive which displays type-ahead input for country search.
 */
angular.module('plsApp.directives').directive('plsCountrySearch', ['CountryService', function (CountryService) {
    return {
        restrict: 'A',
        priority: 10,
        scope: {
            plsCountrySearch: '=',
            countryDisabled: '=',
            count: '@',
            plsCountryIndex: "="
        },
        replace: true,
        templateUrl: 'pages/tpl/pls-country-search-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            function setDefaultCountrySearch() {
                $scope.plsCountrySearch = {id: 'USA', name: 'United States of America', dialingCode: '1'};
            }

            if (!$scope.plsCountrySearch) {
                setDefaultCountrySearch();
            }
            $scope.$on('event:cleaning-input', setDefaultCountrySearch);
            $scope.$on('event:pls-clear-form-data', setDefaultCountrySearch);

            $scope.$root.$on('event:isChangedCountry', function (event, dialogOptions) {
                if (dialogOptions
                        && dialogOptions.countryIndex
                        && $scope.plsCountryIndex
                        && $scope.plsCountryIndex.indexOf(dialogOptions.countryIndex) !== -1
                        && dialogOptions.selectedCountry.id !== $scope.plsCountrySearch.id) {
                    $scope.plsCountrySearch = dialogOptions.selectedCountry;
                    $scope.plsCountrySearch.isChangedCountry = true;
                }
            });

            $scope.findCountry = function (criteria) {
                return CountryService.searchCountries(criteria, $scope.count);
            };
        }]
    };
}]);