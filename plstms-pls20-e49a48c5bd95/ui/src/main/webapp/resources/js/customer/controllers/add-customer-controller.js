angular.module('customer').controller('AddCustomerCtrl', ['$scope', '$q', 'CustomerService', 'CustomerResource',
    function ($scope, $q, CustomerService, CustomerResource) {
        'use strict';

        function addBreadCrumb(id, label) {
            var breadCrumb = new $scope.$root.BreadCrumb(id, label);

            if ($scope.addCustomerWizard.breadCrumbs.list.length !== 0) {
                var prevBreadCrumb = $scope.addCustomerWizard.breadCrumbs.list[$scope.addCustomerWizard.breadCrumbs.list.length - 1];
                breadCrumb.prev = prevBreadCrumb;
                prevBreadCrumb.next = breadCrumb;
            }

            $scope.addCustomerWizard.breadCrumbs.list.push(breadCrumb);
            $scope.addCustomerWizard.breadCrumbs.map[breadCrumb.id] = breadCrumb;
        }

        function isGoShipBusinessUnit() {
            var selectedNetwork = _.where($scope.addCustomerWizard.networks, {
                id: $scope.addCustomerWizard.customer.networkId
            });
            return selectedNetwork[0].name === 'GOSHIP';
        }

        function validateFederalTaxId() {
            if (!isGoShipBusinessUnit() && !$scope.addCustomerWizard.taxIdEmptinessConfirmed && !$scope.addCustomerWizard.customer.federalTaxId) {
                $scope.$root.$broadcast('event:showConfirmation', {
                    caption: 'Tax ID is Absent',
                    okFunction: function () {
                        $scope.addCustomerWizard.taxIdEmptinessConfirmed = true;
                        $scope.nextStep();
                    },
                    message: '<p>Tax Id is required for Active Customers.</p> <br/>' +
                    '<p>Customer will be created in Status "Hold" with reason "Tax ID is absent"</p><br/>Do you want to proceed?',
                    parentDlgId: 'addCustomer'
                });

                return false;
            }

            $scope.addCustomerWizard.taxIdEmptinessConfirmed = false;

            return true;
        }

        function validateForm() {
            return $scope.addCustomerForm.$valid;
        }

        $scope.init = function () {
            $scope.addCustomerWizard = {
                breadCrumbs: {
                    list: [],
                    map: {}
                },
                step: 'details',
                customer: {
                    billTo: {},
                    status: 'ACTIVE',
                    statusReason: 'ACTIVITY_REQUESTED'
                }
            };

            addBreadCrumb('details', 'Main Information');
            addBreadCrumb('address', 'Bill To');
            addBreadCrumb('invoice_preferences', 'Invoice Preferences');
            addBreadCrumb('edi_settings', 'EDI Settings');
            addBreadCrumb('req_fields', 'Req. Fields');
            addBreadCrumb('default_values', 'Default Values');
            addBreadCrumb('req_docs', 'Req. Docs');
            addBreadCrumb('audit_preferences', 'Auditing Preferences');

            $scope.addCustomerWizard.breadCrumbs.map.details.nextAction = validateFederalTaxId;
            $scope.addCustomerWizard.breadCrumbs.map.details.validNext = validateForm;
            $scope.addCustomerWizard.breadCrumbs.map.address.validNext = validateForm;
            $scope.addCustomerWizard.breadCrumbs.map.invoice_preferences.validNext = validateForm;
        };

        $scope.$on('event:showAddCustomer', function (event, billToId) {
            $scope.addCustomerWizard.customer.billTo = {
                billToRequiredFields: ['PRO']
            };

            $scope.addCustomerWizard.showDialog = true;
        });

        function checkCustomerName() {
            return CustomerService.checkCustomerName($scope.addCustomerWizard.customer.name);
        }

        function checkCustomerNameResult(result) {
            $scope.addCustomerWizard.customerNameUnique = !(angular.isString(result) ? result === 'true' : result);

            if ($scope.addCustomerWizard.customerNameUnique) {
                return true;
            } else {
                $scope.$root.$emit('event:application-error', 'Customer Name validation failed!',
                        $scope.addCustomerWizard.customer.name + ' already exists');
                $scope.addCustomerWizard.customer.name = '';
                return false;
            }
        }

        function checkTaxId() {
            return CustomerService.checkTaxId($scope.addCustomerWizard.customer.federalTaxId);
        }

        function checkTaxIdResult(result) {
            if (angular.isString(result) ? result === 'true' : result) {
                $scope.$root.$emit('event:application-error', 'Tax ID validation failed!',
                        $scope.addCustomerWizard.customer.federalTaxId + ' already exists');
                $scope.addCustomerWizard.customer.federalTaxId = '';
                return false;
            } else {
                return true;
            }
        }

        function checkEdiNum() {
            return CustomerService.checkEdiNum($scope.addCustomerWizard.customer.ediAccount);
        }

        function checkEdiNumResult(result) {
            if (angular.isString(result) ? result === 'true' : result) {
                $scope.$root.$emit('event:application-error', 'EDI# validation failed!',
                        $scope.addCustomerWizard.customer.ediAccount + ' already exists');
                $scope.addCustomerWizard.customer.ediAccount = '';
                return false;
            } else {
                return true;
            }
        }

        $scope.safeNextStep = function () {
            if ($scope.isFirstStep()) {
                if ($scope.addCustomerWizard.customer.name) {
                    if ($scope.addCustomerWizard.customer.federalTaxId || isGoShipBusinessUnit()) {
                        if ($scope.addCustomerWizard.customer.ediAccount) {
                            $q.all([checkCustomerName(), checkTaxId(), checkEdiNum()]).then(function (results) {
                                if (checkCustomerNameResult(results[0].data)
                                        && checkTaxIdResult(results[1].data)
                                        && checkEdiNumResult(results[2].data)) {
                                    $scope.nextStep();
                                }
                            });
                        } else {
                            $q.all([checkCustomerName(), checkTaxId()]).then(function (results) {
                                if (checkCustomerNameResult(results[0].data) && checkTaxIdResult(results[1].data)) {
                                    $scope.nextStep();
                                }
                            });
                        }
                    } else {
                        checkCustomerName().then(function (result) {
                            if (checkCustomerNameResult(result.data)) {
                                $scope.nextStep();
                            }
                        });
                    }
                }
            } else {
                $scope.nextStep();
            }
        };

        $scope.nextStep = function () {
            var stepObject = $scope.addCustomerWizard.breadCrumbs.map[$scope.addCustomerWizard.step];

            if (stepObject.nextAction && angular.isFunction(stepObject.nextAction)) {
                var result = stepObject.nextAction();

                if (angular.isObject(result) && result.then) {
                    //step returns promises as validation function
                    result.then(function () {
                        var next = stepObject.next;
                        $scope.addCustomerWizard.step = next.id;
                    });

                    return;
                } else if (!result) {
                    //step has next action function and it returns false that means validation failed
                    return;
                }
            }

            $scope.addCustomerWizard.step = stepObject.next.id;
        };

        $scope.prevStep = function () {
            var stepObject = $scope.addCustomerWizard.breadCrumbs.map[$scope.addCustomerWizard.step];
            var prev = stepObject.prev;
            $scope.addCustomerWizard.step = prev.id;
        };

        $scope.canPrevStep = function () {
            return $scope.addCustomerWizard.breadCrumbs.map[$scope.addCustomerWizard.step].prev !== undefined;
        };

        $scope.canNextStep = function () {
            var stepObject = $scope.addCustomerWizard.breadCrumbs.map[$scope.addCustomerWizard.step];
            return stepObject.next !== undefined && (!stepObject.validNext || (angular.isFunction(stepObject.validNext) && stepObject.validNext()));
        };

        $scope.isFirstStep = function () {
            if ($scope.addCustomerWizard.breadCrumbs.list && $scope.addCustomerWizard.breadCrumbs.list.length > 0) {
                return $scope.addCustomerWizard.step === $scope.addCustomerWizard.breadCrumbs.list.slice(0)[0].id;
            }

            return false;
        };

        $scope.isLastStep = function () {
            if ($scope.addCustomerWizard.breadCrumbs.list && $scope.addCustomerWizard.breadCrumbs.list.length > 0) {
                return $scope.addCustomerWizard.step === $scope.addCustomerWizard.breadCrumbs.list.slice(-1)[0].id;
            }

            return false;
        };

        $scope.nextStepKeyHandler = function () {
            if ($scope.canNextStep()) {
                $scope.safeNextStep();
            } else if ($scope.isLastStep()) {
                $scope.saveCustomer();
            }
        };

        function refineDocumentsReqTypes() {
            angular.forEach($scope.addCustomerWizard.customer.billTo.invoicePreferences.requiredDocuments, function (item) {
                if (item.carrierRequestType === '') {
                    delete item.carrierRequestType;
                }

                if (item.customerRequestType === '') {
                    delete item.customerRequestType;
                }
            });
        }

        $scope.saveCustomer = function () {
            if (!$scope.addCustomerWizard.customer.federalTaxId && !isGoShipBusinessUnit()) {
                $scope.addCustomerWizard.customer.status = 'HOLD';
                $scope.addCustomerWizard.customer.statusReason = 'TAX_ID_EMPTY';
            }

            if ($scope.addCustomerWizard.customer.billTo.address.fax && !$scope.addCustomerWizard.customer.billTo.address.fax.number) {
                delete $scope.addCustomerWizard.customer.billTo.address.fax;
            }

            if ($scope.addCustomerWizard.customer.contactFax && !$scope.addCustomerWizard.customer.contactFax.number) {
                delete $scope.addCustomerWizard.customer.contactFax;
            }

            var ediPhoneEntity = $scope.addCustomerWizard.customer.billTo.billToDefaultValues.ediCustomsBrokerPhone;
            if (!ediPhoneEntity.areaCode || ediPhoneEntity.areaCode.length === 0 || !ediPhoneEntity.number || ediPhoneEntity.number === 0) {
                delete $scope.addCustomerWizard.customer.billTo.billToDefaultValues.ediCustomsBrokerPhone;
            }

            refineDocumentsReqTypes();

            new CustomerResource($scope.addCustomerWizard.customer).$save(function () {
                $scope.$root.$emit('event:operation-success', 'Customer has been created!',
                        'Customer ' + $scope.addCustomerWizard.customer.name + ' has been created.');
                $scope.$emit('event:customerAdded');
                $scope.init();
            }, function () {
                $scope.$root.$emit('event:application-error', 'Customer creation failed!', 'Can\'t create customer.');
            });
        };
    }
]);