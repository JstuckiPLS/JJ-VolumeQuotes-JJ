angular.module('editCustomer').controller('EditCustomerProfileCtrl', [
    '$scope', 'CustomerService', 'CustomerResource', 'BillToService', 'CustomerLocationsService',
    function ($scope, CustomerService, CustomerResource, BillToService, CustomerLocationsService) {
        'use strict';

        var activeStatusReasons = ['ACTIVITY_REQUESTED', 'ENROLLMENT_ACCEPTED'];
        var inactiveStatusReasons = ['CUSTOMER_REQUEST', 'NO_ACTIVITY', 'OUT_OF_BUSINESS'];
        var holdStatusReasons = ['CREDIT_HOLD', 'TAX_ID_EMPTY'];

        $scope.profileModel = {
            customerNameUnique: true,
            billTos: [],
            customerStatuses: ['ACTIVE', 'INACTIVE', 'HOLD'],
            statusReasons: [],
            previousCountry: undefined,
            ediNumUnique: true
        };

        function getEmptyPhone() {
            return {
                countryCode: Number($scope.profileModel.customer.address.country.dialingCode)
            };
        }

        function fixPhone(phone) {
            var fixedPhone = phone;

            if (_.isEmpty(fixedPhone)) {
                fixedPhone = getEmptyPhone();
            }

            if (!fixedPhone.countryCode) {
                fixedPhone.countryCode = Number($scope.profileModel.customer.address.country.dialingCode);
            }

            return fixedPhone;
        }

        function initLocations() {
            CustomerLocationsService.getListForCustomer({customerId: $scope.profileModel.customer.id}, function (locations) {
                $scope.profileModel.customerLocations = _.sortBy(locations, 'location');
                $scope.profileModel.customerLocations.unshift({});
            });
        }

        function setUpBillTo() {
            if ($scope.profileModel.customer.billTo) {
                angular.forEach($scope.profileModel.billTos, function (item) {
                    if ($scope.profileModel.customer.billTo.id === item.id) {
                        $scope.profileModel.customer.billTo = item;
                    }
                });
            }
        }

        function initBillTo() {
            BillToService.list({customerId: $scope.profileModel.customer.id, userId: -1}, function (data) {
                if (data) {
                    $scope.profileModel.billTos = _.sortBy(data, function (billTo) {
                        return billTo.address.addressName;
                    });

                    if (!$scope.profileModel.customer.createOrdersFromVendorBills) {
                        $scope.profileModel.billTos.unshift({});
                    }

                    setUpBillTo();
                }
            });
        }

        function initCustomer() {
            if ($scope.profileModel.customer.address && $scope.profileModel.customer.address.country
                    && $scope.profileModel.customer.address.country.dialingCode) {

                $scope.profileModel.customer.contactPhone = fixPhone($scope.profileModel.customer.contactPhone);
                $scope.profileModel.customer.contactFax = fixPhone($scope.profileModel.customer.contactFax);
            }

            initLocations();
            initBillTo();

            $scope.profileModel.originalCustomerName = angular.copy($scope.profileModel.customer.name);
            $scope.profileModel.originalEdiNum = angular.copy($scope.profileModel.customer.ediAccount);
            $scope.profileModel.oldStatus = angular.copy($scope.profileModel.customer.status);
            $scope.profileModel.oldStatusReason = angular.copy($scope.profileModel.customer.statusReason);
            if ($scope.profileModel.customer.address) {
                $scope.profileModel.previousCountry = angular.copy($scope.profileModel.customer.address.country);
            }

            setTimeout(function () {
                $scope.customerProfileForm.$setPristine();
            }, 1);
        }

        $scope.init = function () {
            new CustomerResource().$get({customerId: $scope.editCustomerModel.customerId}, function (data) {
                $scope.profileModel.customer = data;
                initCustomer();
            }, function () {
                $scope.$root.$emit('event:application-error', 'Customer load failed!', 'Can\'t load customer with ID '
                        + $scope.editCustomerModel.customerId);
            });
        };

        $scope.createOrdersFromVendorBillsChanged = function () {
            if (!$scope.profileModel.customer.createOrdersFromVendorBills) {
                $scope.profileModel.billTos.unshift({});
            } else {
                //remove empty item
                var emptyItem = $scope.profileModel.billTos.shift();

                if (!_.isEmpty(emptyItem)) {
                    $scope.profileModel.billTos.unshift(emptyItem);
                }
            }

            if (_.isEmpty($scope.profileModel.customer.billTo)) {
                $scope.profileModel.customer.billTo = $scope.profileModel.billTos[0];
            }
        };

        $scope.$watch('profileModel.customer.address.country', function () {
            if ($scope.profileModel.customer && $scope.profileModel.customer.address && $scope.profileModel.customer.address.country) {
                $scope.profileModel.zipAutoComplete = _.contains(['USA', 'CAN', 'MEX'], $scope.profileModel.customer.address.country.id);

                if ($scope.profileModel.customer.address.zip &&
                        $scope.profileModel.customer.address.zip.country &&
                        $scope.profileModel.customer.address.zip.country.id !== $scope.profileModel.customer.address.country.id) {

                    if ($scope.profileModel.zipAutoComplete) {
                        delete $scope.profileModel.customer.address.zip;
                    } else {
                        $scope.profileModel.customer.address.zip = {country: $scope.profileModel.customer.address.country};
                    }
                }

                if ($scope.profileModel.previousCountry
                        && $scope.profileModel.customer.address.country.id !== $scope.profileModel.previousCountry.id) {
                    $scope.profileModel.customer.contactPhone = getEmptyPhone();
                    $scope.profileModel.customer.contactFax = getEmptyPhone();
                }

                if (!$scope.profileModel.customer.contactFax) {
                    $scope.profileModel.customer.contactFax = getEmptyPhone();
                }

                if (!$scope.profileModel.customer.contactPhone) {
                    $scope.profileModel.customer.contactPhone = getEmptyPhone();
                }

                $scope.profileModel.previousCountry = angular.copy($scope.profileModel.customer.address.country);

                if ($scope.profileModel.customer.address.country.id === 'USA') {
                    $scope.taxIdFieldLength = 10;
                    $scope.taxIdFieldPlaceholder = '##-#######';
                } else {
                    $scope.taxIdFieldLength = 35;
                    $scope.taxIdFieldPlaceholder = '';
                }
            }
        });

        $scope.checkCustomerName = function () {
            if ($scope.profileModel.customer.name
                    && $scope.profileModel.customer.name !== $scope.profileModel.originalCustomerName) {
                CustomerService.checkCustomerName($scope.profileModel.customer.name).success(function (data) {
                    $scope.profileModel.customerNameUnique = !(angular.isString(data) ? data === 'true' : data);

                    if (!$scope.profileModel.customerNameUnique) {
                        $scope.$root.$emit('event:application-error', 'Customer Name validation failed!',
                                $scope.profileModel.customer.name + ' already exists');
                        $scope.profileModel.customer.name = angular.copy($scope.profileModel.originalCustomerName);
                    }
                });
            }
        };

        $scope.checkEdiNumber = function () {
            if ($scope.profileModel.customer.ediAccount
                    && $scope.profileModel.customer.ediAccount !== $scope.profileModel.originalEdiNum) {
                CustomerService.checkEdiNum($scope.profileModel.customer.ediAccount).success(function (data) {
                    $scope.profileModel.ediNumUnique = !(angular.isString(data) ? data === 'true' : data);

                    if (!$scope.profileModel.ediNumUnique) {
                        $scope.$root.$emit('event:application-error', 'EDI# validation failed!',
                                $scope.profileModel.customer.ediAccount + ' already exists');
                        $scope.profileModel.customer.ediAccount = angular.copy($scope.profileModel.originalEdiNum);
                    }
                });
            }
        };

        $scope.getStatusReasons = function () {
            if ($scope.profileModel.customer) {
                switch ($scope.profileModel.customer.status) {
                    case 'ACTIVE':
                        return activeStatusReasons;
                    case 'INACTIVE':
                        return inactiveStatusReasons;
                    case 'HOLD':
                        return $scope.profileModel.oldStatus === 'HOLD' && $scope.profileModel.oldStatusReason === 'TAX_ID_EMPTY'
                                ? holdStatusReasons : holdStatusReasons.slice(0, 1);
                }
            }

            return [];
        };

        $scope.isStatusEditable = function () {
            if ($scope.profileModel.customer.address && $scope.profileModel.customer.address.country &&
                    $scope.profileModel.customer.address.country.id === 'USA') {
                return !($scope.profileModel.oldStatus === 'HOLD' && $scope.profileModel.oldStatusReason === 'TAX_ID_EMPTY');
            }

            return true;
        };

        $scope.isStatusReasonEditable = function () {
            return $scope.profileModel.customer && (($scope.profileModel.customer.status !== $scope.profileModel.oldStatus
                    && !($scope.profileModel.oldStatus === 'HOLD' && $scope.profileModel.oldStatusReason === 'TAX_ID_EMPTY')) ||
                    (!$scope.profileModel.oldStatusReason && $scope.profileModel.oldStatus !== 'ACTIVE'));
        };

        $scope.statusChanged = function () {
            if ($scope.profileModel.customer.status !== $scope.profileModel.oldStatus) {
                $scope.profileModel.customer.statusReason = undefined;
            } else {
                $scope.profileModel.customer.statusReason = $scope.profileModel.oldStatusReason;
            }
        };

        $scope.editCustomerModel.isSaveDisabled = function () {
            return $scope.customerProfileForm.$pristine || $scope.customerProfileForm.$invalid
                    || !$scope.profileModel.customer || !$scope.profileModel.customer.id;
        };

        $scope.editCustomerModel.saveChanges = function () {
            var clonedCustomer = angular.copy($scope.profileModel.customer);

            if (clonedCustomer.accountExecutiveStartDate && clonedCustomer.accountExecutiveEndDate
                    && new Date(clonedCustomer.accountExecutiveStartDate) >= new Date(clonedCustomer.accountExecutiveEndDate)) {
                $scope.$root.$emit('event:application-error', 'Customer save failed!', 'Account expiration start date should be less then end date.');
                return;
            }

            if (!_.isEmpty(clonedCustomer.billTo)) {
                clonedCustomer.billTo.defaultNode = true;
            } else {
                clonedCustomer.billTo = undefined;
            }

            if (clonedCustomer.contactFax && !clonedCustomer.contactFax.number) {
                clonedCustomer.contactFax = undefined;
            }

            new CustomerResource(clonedCustomer).$save(function (data) {
                $scope.profileModel.customer = data;
                $scope.$root.$emit('event:operation-success', 'Customer save succeed!', 'Customer with ID ' + $scope.profileModel.customer.id +
                        ' has been saved.');
                $scope.profileModel.oldStatus = angular.copy($scope.profileModel.customer.status);
                $scope.profileModel.oldStatusReason = angular.copy($scope.profileModel.customer.statusReason);
                $scope.profileModel.originalCustomerName = angular.copy($scope.profileModel.customer.name);
                if (!$scope.profileModel.customer.contactFax) {
                    $scope.profileModel.customer.contactFax = getEmptyPhone();
                }
                setUpBillTo();
                setTimeout($scope.customerProfileForm.$setPristine);
            }, function (response) {
                var errorMessage = 'Error during saving customer with ID ' + $scope.profileModel.customer.id;

                if (response.status === 426) {
                    errorMessage = 'Customer has been updated by another user. Please refresh the page.';
                } else if (response.data && response.data.message) {
                    errorMessage = 'Error during saving customer due to ' + response.data.message
                            + '<br/>Probably it has been updated by another user.';
                }

                $scope.$root.$emit('event:application-error', 'Customer save failed!', errorMessage);
            });
        };

        $scope.isTaxIdEditable = function () {
            return $scope.profileModel.oldStatus === 'HOLD' && $scope.profileModel.oldStatusReason === 'TAX_ID_EMPTY';
        };

        $scope.checkTaxId = function () {
            if ($scope.profileModel.customer.federalTaxId) {
                CustomerService.checkTaxId($scope.profileModel.customer.federalTaxId).success(function (data) {
                    if (angular.isString(data) ? data === 'true' : data) {
                        $scope.profileModel.customer.federalTaxId = '';
                        $scope.$root.$emit('event:application-error', 'Tax ID validation failed!',
                                $scope.profileModel.customer.federalTaxId + ' already exists');
                    } else {
                        $scope.profileModel.customer.status = 'ACTIVE';
                        $scope.profileModel.customer.statusReason = 'ACTIVITY_REQUESTED';
                    }
                });
            } else {
                if ($scope.profileModel.oldStatus === 'HOLD' && $scope.profileModel.oldStatusReason === 'TAX_ID_EMPTY') {
                    $scope.profileModel.customer.status = 'HOLD';
                    $scope.profileModel.customer.statusReason = 'TAX_ID_EMPTY';
                }
            }
        };

        $scope.$watch('profileModel.customer.logoId', function () {
            $scope.customerProfileForm.$setDirty();
        });
    }
]);