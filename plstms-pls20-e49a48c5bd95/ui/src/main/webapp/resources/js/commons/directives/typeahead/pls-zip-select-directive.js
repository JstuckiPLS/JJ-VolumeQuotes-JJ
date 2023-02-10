/**
 * AngularJS directive which displays type-ahead input for zip search country search and selected result.
 *
 * @author: Sergey Kirichenko
 * Date: 6/26/13
 * Time: 4:17 PM
 */
angular.module('plsApp.directives').directive('plsZipSelect', ['$compile', '$templateCache', '$http',
    function ($compile, $templateCache, $http) {
        return {
            restrict: 'A',
            scope: {
                selectedZip: '=plsZipSelect',
                zipLabel: '@',
                zipTabIndex: '@',
                countryLabel: '@',
                centerAlign: '@',
                validateWarning: '='
            },
            link: function (scope, element) {
                'use strict';

                scope.directiveModel = {
                    zipAutoComplete: true,
                    zip: scope.selectedZip
                };

                var zipAutoCompleteCountry = ['CAN', 'MEX', 'USA'];
                var previousCountry;

                if (scope.selectedZip && scope.selectedZip.country) {
                    scope.selectedCountry = scope.selectedZip.country;
                    previousCountry = scope.selectedCountry.id;
                    scope.directiveModel.zipAutoComplete = _.indexOf(zipAutoCompleteCountry, scope.selectedCountry.id) !== -1;
                }

                scope.$watch("selectedCountry", function (newVal) {
                    if (newVal && newVal.id !== previousCountry && !newVal.isChangedCountry) {
                        scope.directiveModel.zipAutoComplete = _.indexOf(zipAutoCompleteCountry, newVal.id) !== -1;
                        scope.directiveModel.zip = undefined;

                        if (scope.centerAlign === 'true' && !scope.directiveModel.zipAutoComplete) {
                            scope.directiveModel.zip = {
                                country: angular.copy(scope.selectedCountry)
                            };
                        }
                        previousCountry = newVal.id;
                    }
                    if (newVal && newVal.isChangedCountry) {
                        delete newVal.isChangedCountry;
                    }
                });

                if (scope.centerAlign === 'true') {
                    $http.get('pages/tpl/pls-zip-select-specific-tpl.html', {
                        cache: $templateCache
                    }).then(function (result) {
                        var newElement = $compile(result.data)(scope);
                        element.replaceWith(newElement);
                        element = newElement;
                    });
                } else {
                    $http.get('pages/tpl/pls-zip-select-default-tpl.html', {
                        cache: $templateCache
                    }).then(function (result) {
                        var newElement = $compile(result.data)(scope);
                        element.replaceWith(newElement);
                        element = newElement;
                    });
                }

                scope.$watch('selectedZip', function (newVal) {
                    if (scope.validateWarning && newVal && newVal.warning) {
                        scope.$root.$emit('event:application-warning',
                                'This zip code indicates a PO Box: ' + newVal.zip,
                                'For the most accurate quote, please change to a non-PO Box zip code if one is available.');
                    }
                    scope.directiveModel.zip = newVal;
                });

                scope.$watch('directiveModel.zip', function (newVal) {
                    if (scope.validateWarning && !newVal && scope.selectedZip && scope.selectedZip.warning) {
                        // show input value for user
                        setTimeout(function () {
                            $(element).find('[data-pls-zip-search="directiveModel.zip"]').val(scope.selectedZip.zip);
                        }, 1);
                        // do not clear model value when ZIP with warning is selected
                        return;
                    }
                    scope.selectedZip = newVal;
                });
            }
        };
    }
]);