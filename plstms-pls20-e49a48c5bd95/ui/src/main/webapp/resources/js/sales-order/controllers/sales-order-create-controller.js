angular.module('pls.controllers').controller('CreateSalesOrderCtrl', ['$scope', '$routeParams', 'MatchedLoadsService', 'ShipmentUtils',
    function ($scope, $routeParams, MatchedLoadsService, ShipmentUtils) {
        'use strict';

        function addBreadCrumb(id, label) {
            var breadCrumb = new $scope.$root.BreadCrumb(id, label);

            if ($scope.wizardData.breadCrumbs.list.length !== 0) {
                var prevBreadCrumb = $scope.wizardData.breadCrumbs.list[$scope.wizardData.breadCrumbs.list.length - 1];
                breadCrumb.prev = prevBreadCrumb;
                prevBreadCrumb.next = breadCrumb;
            }

            $scope.wizardData.breadCrumbs.list.push(breadCrumb);
            $scope.wizardData.breadCrumbs.map[breadCrumb.id] = breadCrumb;
        }

        $scope.init = function () {
            $scope.initialize();

            $scope.wizardData.breadCrumbs = {};
            $scope.wizardData.breadCrumbs.list = [];
            $scope.wizardData.breadCrumbs.map = {};

            addBreadCrumb('general_information', 'General Information');
            addBreadCrumb('addresses', 'Addresses');
            addBreadCrumb('details', 'Details');
            addBreadCrumb('docs', 'Docs');

            if ($routeParams.step) {
                $scope.wizardData.step = $routeParams.step;
            } else {
                $scope.wizardData.step = 'general_information';
            }

            $scope.displayWarning = true;
            $scope.isEstDeliveryRequired = true;
        };

        $scope.nextStep = function () {
            if (!_.isEmpty($scope.wizardData.shipment.proNumber) && $scope.wizardData.step === "general_information" &&
                    $scope.wizardData.shipment.selectedProposition.carrier !== undefined) {
                MatchedLoadsService.get($scope.wizardData.shipment.proNumber,
                        $scope.wizardData.shipment.selectedProposition.carrier.id).then(function (response) {
                    if (response.data.length > 0) {
                        $scope.$root.$broadcast('event:showConfirmation', {
                            caption: 'Duplicate Shipment',
                            message: "The PRO # and Carrier on this shipment is the same as Load ID " + response.data.join(', '),
                            okFunction: $scope.nextStepConfirm,
                            confirmButtonLabel: "Ok",
                            closeButtonLabel: "Cancel"
                        });
                    } else {
                        $scope.nextStepConfirm();
                    }
                }, function () {
                    $scope.$root.$emit('event:application-warning', 'Duplicate check was not done');
                    $scope.nextStepConfirm();
                });
            } else {
                $scope.nextStepConfirm();
            }
        };

        $scope.nextStepConfirm = function () {
            if ($scope.wizardData.step === 'addresses' && !ShipmentUtils.isCreditLimitValid($scope.wizardData.shipment)) {
                return;
            }

            var stepObject = $scope.wizardData.breadCrumbs.map[$scope.wizardData.step];

            if (stepObject.nextAction && angular.isFunction(stepObject.nextAction)) {
                var result = stepObject.nextAction();

                if (angular.isObject(result) && result.then) {
                    //step returns promises as validation function
                    result.then(function () {
                        var next = stepObject.next;
                        $scope.wizardData.step = next.id;
                    });

                    return;
                } else if (!result) {
                    //step has next action function and it returns false that means validation failed
                    return;
                }
            }

            $scope.wizardData.step = stepObject.next.id;
        };

        $scope.prevStep = function () {
            var stepObject = $scope.wizardData.breadCrumbs.map[$scope.wizardData.step];
            var prev = stepObject.prev;
            $scope.wizardData.step = prev.id;
        };

        $scope.done = function () {
            var stepObject = $scope.wizardData.breadCrumbs.map[$scope.wizardData.step];

            if (stepObject.doneAction && angular.isFunction(stepObject.doneAction)) {
                var result = stepObject.doneAction();

                if (angular.isObject(result) && result.then) {
                    //step returns promises as validation function
                    result.then(function () {
                        $scope.init();
                    });

                    return;
                } else if (!result) {
                    //step has next action function and it returns false that means validation failed
                    return;
                }
            }
            $scope.init();
        };

        $scope.canPrevStep = function () {
            return $scope.wizardData.breadCrumbs.map[$scope.wizardData.step].prev !== undefined;
        };

        $scope.canNextStep = function () {
            var stepObject = $scope.wizardData.breadCrumbs.map[$scope.wizardData.step];
            return stepObject.next !== undefined && (!stepObject.validNext || (angular.isFunction(stepObject.validNext) && stepObject.validNext()));
        };

        $scope.canDone = function () {
            var stepObject = $scope.wizardData.breadCrumbs.map[$scope.wizardData.step];
            return stepObject.next === undefined && (!stepObject.validDone || (angular.isFunction(stepObject.validDone) && stepObject.validDone()));
        };

        $scope.cancel = function () {
            var selectedCustomer = angular.copy($scope.wizardData.selectedCustomer);
            $scope.initialize();
            $scope.wizardData.selectedCustomer = selectedCustomer;
            $scope.init();
        };

        $scope.isLastStep = function () {
            if ($scope.wizardData.breadCrumbs.list && $scope.wizardData.breadCrumbs.list.length > 0) {
                return $scope.wizardData.step === $scope.wizardData.breadCrumbs.list.slice(-1)[0].id;
            }

            return false;
        };

        $scope.isFormClean = function () {
            // ignore freightBillPayTo
            var shipment = _.omit($scope.wizardData.shipment, 'freightBillPayTo');
            delete shipment.selectedProposition.totalCarrierAmt;
            delete shipment.selectedProposition.totalShipperAmt;

            if ($scope.$root.authData.assignedOrganization !== undefined) {
                // ignore organization id for customer user
                shipment = _.omit(shipment, 'organizationId');
            }

            return _.isEqual($scope.wizardData.emptyShipment, shipment) && $scope.wizardData.selectedCustomer
                    && (!$scope.wizardData.selectedCustomer.id || $scope.$root.authData.assignedOrganization !== undefined);
        };

        $scope.isPageOpen = function (pageName) {
            return $scope.wizardData.step === pageName;
        };
    }
]);