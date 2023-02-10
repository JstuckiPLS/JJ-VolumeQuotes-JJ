angular.module('customer').controller('AddCustomerDetailsCtrl', [
    '$scope', 'CustomerService', 'ShipmentDictionaryService', 'DictionaryService', 'ShipmentUtils',
    function ($scope, CustomerService, ShipmentDictionaryService, DictionaryService, ShipmentUtils) {
        'use strict';

        var zipAutoCompleteCountry = ['CAN', 'MEX', 'USA'];

        if (!$scope.addCustomerWizard.customer.accountExecutiveStartDate) {
            $scope.addCustomerWizard.customer.accountExecutiveStartDate = $scope.$root.formatDate(new Date());
        }

        function getNewPhone() {
            return {countryCode: Number($scope.addCustomerWizard.customer.address.country.dialingCode)};
        }

        $scope.$watch('addCustomerWizard.customer.address.country', function (newVal, oldVal) {
            if (newVal) {
                $scope.addCustomerWizard.zipAutoComplete = _.indexOf(zipAutoCompleteCountry, newVal.id) !== -1;

                if (!$scope.addCustomerWizard.previousCustomerCountry || newVal.id !== $scope.addCustomerWizard.previousCustomerCountry.id) {
                    delete $scope.addCustomerWizard.customer.address.zip;
                    $scope.addCustomerWizard.customer.contactPhone = getNewPhone();
                    $scope.addCustomerWizard.customer.contactFax = getNewPhone();
                }

                $scope.addCustomerWizard.previousCustomerCountry = angular.copy(newVal);
            }
        });

        $scope.$watch('addCustomerWizard.customer.networkId', function (newVal) {
            if (newVal) {
                $scope.addCustomerWizard.customer.billTo.emailAccountExecutive =
                ShipmentUtils.isEmailAccountExecutive(newVal);
            }
        });

        $scope.customerContractChanged = function() {
            if (!$scope.addCustomerWizard.customer.contract) {
                angular.element('[data-ng-model="addCustomerWizard.customer.startDate"]').controller('ngModel').$setViewValue('');
                angular.element('[data-ng-model="addCustomerWizard.customer.startDate"]').controller('ngModel').$render();
                angular.element('[data-ng-model="addCustomerWizard.customer.endDate"]').controller('ngModel').$setViewValue('');
                angular.element('[data-ng-model="addCustomerWizard.customer.endDate"]').controller('ngModel').$render();
            }
        };

        $scope.isFreightSolutionsSelected = function () {
            return $scope.addCustomerWizard.customer.networkId === 4;
        };

        $scope.networkSelected = function () {
            if (!_.isEmpty($scope.addCustomerWizard.allNetworks)) {
                var selectedNetworks = _.where($scope.addCustomerWizard.allNetworks, {id: $scope.addCustomerWizard.customer.networkId});

                $scope.addCustomerWizard.companyCodes = _.sortBy(selectedNetworks, function (item) {
                    return item.description;
                });

                if (!$scope.isFreightSolutionsSelected()) {
                    $scope.addCustomerWizard.customer.companyCode = $scope.addCustomerWizard.companyCodes[0].companyCode;
                } else {
                    delete $scope.addCustomerWizard.customer.companyCode;
                }
            }
        };

        $scope.getCostCenter = function () {
            if (!$scope.addCustomerWizard.customer.companyCode || !$scope.addCustomerWizard.companyCodes.length) {
                return undefined;
            }

            return _.findWhere($scope.addCustomerWizard.companyCodes, {companyCode: $scope.addCustomerWizard.customer.companyCode}).costCenterCode;
        };

        if (_.isEmpty($scope.addCustomerWizard.allNetworks)) {
            ShipmentDictionaryService.getAllNetworks({}, function (data) {
                $scope.addCustomerWizard.allNetworks = _.sortBy(data, function (network) {
                    return network.name;
                });

                $scope.addCustomerWizard.networks = _.uniq($scope.addCustomerWizard.allNetworks, function (item) {
                    return item.id;
                });

                if (!$scope.$root.isPlsPermissions('CAN_ADD_CUSTOMER_FOR_GOSHIP')) {
                    $scope.addCustomerWizard.networks = _.reject($scope.addCustomerWizard.networks, function(network) {
                        return network.name === 'GOSHIP';
                    });
                }
            });
        }

        if (_.isEmpty($scope.addCustomerWizard.accountExecutives)) {
            CustomerService.getAccountExecutives().success(function (data) {
                $scope.addCustomerWizard.accountExecutives = data;
            });
        }

        //init all other required fields
        if (_.isEmpty($scope.addCustomerWizard.payTerms)) {
            DictionaryService.getCustomerPayTerms({}, function (data) {
                if (data && data.length) {
                    $scope.addCustomerWizard.payTerms = data;
                }
            });
        }

        if (_.isEmpty($scope.addCustomerWizard.payMethods)) {
            DictionaryService.getCustomerPayMethods({}, function (data) {
                if (data && data.length) {
                    $scope.addCustomerWizard.payMethods = data;
                    $scope.addCustomerWizard.payMethods.unshift({label: '', value: ""});
                }
            });
        }

        if (_.isEmpty($scope.addCustomerWizard.sortTypes)) {
            $scope.addCustomerWizard.sortTypes = [
                {id: 'LOAD_ID', value: 'Load ID'},
                {id: 'GL_NUM', value: 'GL#'},
                {id: 'DELIV_DATE', value: 'Delivery Date'},
                {id: 'BOL', value: 'BOL'}
            ];
        }

        if (_.isEmpty($scope.addCustomerWizard.xlsDocuments)) {
            $scope.addCustomerWizard.xlsDocuments = [
                {label: 'None', value: 'NONE'},
                {label: 'Standard Excel', value: 'STANDARD_EXCEL'},
                {label: 'Excel Grouped by GL#', value: 'GROUPED_BY_GL_NUMBER_EXCEL'},
                {label: 'Flex Version', value: 'FLEX_VERSION_EXCEL'},
                {label: 'Customized Inbound/Outbound', value: 'CUSTOMIZED_EXCEL'}
            ];
        }

        if (_.isEmpty($scope.addCustomerWizard.pdfDocuments)) {
            $scope.addCustomerWizard.pdfDocuments = [
                {label: 'None', value: 'NONE'},
                {label: 'Standard Multi Transactional', value: 'PDF'}
            ];
        }

        if (_.isEmpty($scope.addCustomerWizard.ediSettings)) {
            $scope.addCustomerWizard.ediSettings = {};

            $scope.$watch(function () {
                return ShipmentUtils.getDictionaryValues().shipmentStatusEnum;
            }, function (newValue) {
                if (!_.isUndefined(newValue)) {
                    $scope.addCustomerWizard.ediSettings.ediStatuses = _.pairs(angular.copy(newValue));
                }
            });

            $scope.$watch(function () {
                return ShipmentUtils.getDictionaryValues().ediType;
            }, function (newValue) {
                if (!_.isUndefined(newValue)) {
                    $scope.addCustomerWizard.ediSettings.ediTypes = _.pairs(angular.copy(newValue));
                }
            });
        }

        if (_.isEmpty($scope.addCustomerWizard.requiredFields)) {
            $scope.$watch(function () {
                return ShipmentUtils.getDictionaryValues().billToRequiredField;
            }, function (newValue) {
                if (!_.isUndefined(newValue)) {
                    var requiredFields = angular.copy(newValue);

                    $scope.addCustomerWizard.requiredFields = _.map(requiredFields, function (field) {
                        return [field.value, field.label];
                    });
                }
            });
        }
    }
]);