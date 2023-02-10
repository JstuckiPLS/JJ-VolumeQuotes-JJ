angular.module('editCustomer').controller('AddEditBillToCtrl', ['$scope', '$q', 'BillToService', 'BillToValidationService',
    function ($scope, $q, BillToService, BillToValidationService) {
        'use strict';

        $scope.billToModel = {
            selectedTab: 'address',
            breadCrumbs: {
                list: [],
                map: {}
            }
        };

        function addBreadCrumb(id, label) {
            var breadCrumb = new $scope.$root.BreadCrumb(id, label);

            if ($scope.billToModel.breadCrumbs.list.length !== 0) {
                var prevBreadCrumb = $scope.billToModel.breadCrumbs.list[$scope.billToModel.breadCrumbs.list.length - 1];
                breadCrumb.prev = prevBreadCrumb;
                prevBreadCrumb.next = breadCrumb;
            }

            $scope.billToModel.breadCrumbs.list.push(breadCrumb);
            $scope.billToModel.breadCrumbs.map[breadCrumb.id] = breadCrumb;
        }

        addBreadCrumb('address', 'Bill To Address');
        addBreadCrumb('invoice_preferences', 'Invoice Preferences');
        addBreadCrumb('edi_settings', 'EDI Settings');
        addBreadCrumb('req_fields', 'Req. Fields');
        addBreadCrumb('default_values', 'Default Values');
        addBreadCrumb('req_docs', 'Req. Docs');
        addBreadCrumb('audit_preferences', 'Audit Preferences');

        $scope.nextStep = function () {
            var stepObject = $scope.billToModel.breadCrumbs.map[$scope.billToModel.selectedTab];
            $scope.billToModel.selectedTab = stepObject.next.id;
        };

        $scope.prevStep = function () {
            var stepObject = $scope.billToModel.breadCrumbs.map[$scope.billToModel.selectedTab];
            var prev = stepObject.prev;
            $scope.billToModel.selectedTab = prev.id;
        };

        $scope.$on('event:showAddBillToDialog', function () {
            $scope.billToModel.billTo = {
                billToRequiredFields: ['PRO'],
                emailAccountExecutive: $scope.editCustomerModel.emailAccountExecutive
            };

            $scope.showAddBillToDialog = true;
        });

        $scope.$on('event:showEditBillToDialog', function (event, billToId, openInvoicePreferencesTab) {
            BillToService.getBillToById({
                customerId: $scope.editCustomerModel.customerId,
                userId: -1,
                billToId: billToId
            }, function (data) {
                $scope.billToModel.billTo = data;
                $scope.billToModel.billToNameBeforeEditing = $scope.billToModel.billTo.address.addressName;

                if (openInvoicePreferencesTab) {
                    $scope.billToModel.selectedTab = 'invoicePreferences';
                }

                $scope.showEditBillToDialog = true;
            }, function () {
                $scope.$root.$emit('event:application-error', 'Bill To load failed!', 'Can\'t load Bill To with ID ' + billToId);
            });
        });

        $scope.closeBillToDialog = function () {
            $scope.billToModel.selectedTab = 'address';
            $scope.showEditBillToDialog = false;
            $scope.showAddBillToDialog = false;
        };

        function refineDocumentsReqTypes() {
            angular.forEach($scope.billToModel.billTo.invoicePreferences.requiredDocuments, function (item) {
                if (item.carrierRequestType === '') {
                    delete item.carrierRequestType;
                }

                if (item.customerRequestType === '') {
                    delete item.customerRequestType;
                }
            });
        }

        function saveBillTo() {
            if ($scope.billToModel.billTo.address.fax && !$scope.billToModel.billTo.address.fax.number) {
                delete $scope.billToModel.billTo.address.fax;
            }

            var ediPhoneEntity = $scope.billToModel.billTo.billToDefaultValues.ediCustomsBrokerPhone;
            if (!ediPhoneEntity.areaCode || ediPhoneEntity.areaCode.length === 0 || !ediPhoneEntity.number || ediPhoneEntity.number === 0) {
                delete $scope.billToModel.billTo.billToDefaultValues.ediCustomsBrokerPhone;
            }

            refineDocumentsReqTypes();

            BillToService.saveUpdateBillTo({
                customerId: $scope.editCustomerModel.customerId,
                userId: -1
            }, $scope.billToModel.billTo, function () {
                $scope.$root.$emit('event:operation-success', 'Bill To save succeed!', 'Bill To changes have been saved successfully.');
                $scope.$emit('event:billToSavedOrUpdated');
                $scope.closeBillToDialog();
            }, function (data) {
                $scope.$root.$emit('event:application-error', 'Bill To save failed!', data.data.message);

                if (!$scope.billToModel.billTo.address.fax) {
                    $scope.billToModel.billTo.address.fax = {countryCode: Number($scope.billToModel.billTo.address.country.dialingCode)};
                }
            });
        }

        $scope.saveBillTo = function () {
            if ($scope.billToModel.billToNameBeforeEditing !== $scope.billToModel.billTo.address.addressName) {
                var billToNamePromise = $q.defer();

                BillToValidationService.validateNameDuplication({
                    customerId: $scope.editCustomerModel.customerId,
                    userId: -1,
                    nameToBeValidated: $scope.billToModel.billTo.address.addressName
                }, function (data) {
                    if (data.result === "true") {
                        $scope.$root.$emit('event:application-error', 'Bill to save failed!', 'Bill to with the same name already exists');
                        billToNamePromise.reject('BillTo name failure');
                    } else {
                        billToNamePromise.resolve('BillTo name success');
                    }
                }, function () {
                    billToNamePromise.reject('BillTo name failure');
                });

                billToNamePromise.promise.then(function () {
                    saveBillTo();
                });
            } else {
                saveBillTo();
            }
        };
    }
]);