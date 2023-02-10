angular.module('termsAndConditions').controller('TermsAndConditionsController',
        [ '$scope', 'TermsAndConditionsService', '$rootScope',  function($scope, TermsAndConditionsService, $rootScope) {
            'use strict';

            $scope.isTermsAndConditionsApplied = false;

            function isTermsAndConditionsApplied() {
                TermsAndConditionsService.isTermsAndConditionsApplied({}, function(data) {
                    if (data.result === 'true') {
                        $scope.isTermsAndConditionsApplied = true;
                    }
                });
            }

            $scope.termsAndConditionsOptions = {
                width : '100%',
                height : '600px',
                pdfLocation : 'resources/img/TermsAndConditions.pdf'
            };

            $scope.confirmTermsAndConditions = function() {
                TermsAndConditionsService.applyTermsAndConditions({}, function(data) {
                    $scope.$root.$emit('event:operation-success', 'You have accepted Terms & Agreements!', 'You can close your browser');
                    $scope.isTermsAndConditionsApplied = true;
                }, function() {
                    $scope.$root.$emit('event:application-error', 'You have already accepted Terms and Conditions!',
                            'You can close your browser');
                });
            };

            isTermsAndConditionsApplied();
}]);