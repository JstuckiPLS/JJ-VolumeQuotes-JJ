angular.module('plsApp').directive('plsBillToEdiSettings', function () {
    return {
        restrict: 'A',
        scope: {
            billTo: '=',
            ediSettings: '='
        },
        replace: true,
        templateUrl: 'pages/content/customer/billTo/templates/billto-edi-settings-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.ediSettingsFieldDisabled = !$scope.$root.isFieldRequired('VIEW_EDIT_EDI_SETTINGS');

            $scope.bolUniqueChanged = function () {
                if ($scope.billTo.ediSettings.bolUnique) {
                    if (!$scope.billTo.billToRequiredFields) {
                        $scope.billTo.billToRequiredFields = [];
                    }

                    if (!_.contains($scope.billTo.billToRequiredFields, 'BOL')) {
                        $scope.billTo.billToRequiredFields.push('BOL');
                    }
                }
            };
        }]
    };
});