angular.module('pls.controllers').controller('ShipmentEntrySaveDialogController', ['$scope', '$filter', '$window', '$route', '$location',
    function ($scope, $filter, $window, $route, $location) {
        'use strict';
        var okEvent;

        $scope.shipmentEntrySaveDialog = {
            show: false
        };

        $scope.closeShipmentEntrySaveDialog = function () {
            $scope.shipmentEntrySaveDialog = {
                show: false
            };
            setTimeout(function () {
                okEvent();
            }, 100);
        };

        function getInfoString(terminal, terminalContact) {
            var termString = "";

            if (terminal) {
                termString = terminal.address1;

                if (terminal.address2) {
                    termString += '\n' + terminal.address2;
                }

                termString += '\n' + terminal.city + ', ' + terminal.stateCode + ', ' + terminal.postalCode;
            }
            if (terminalContact) {
                if (terminalContact.name) {
                    termString += '\nName: ' + terminalContact.name;
                }

                if (terminalContact.phone) {
                    termString += '\nPhone: ' + $filter('phone')(terminalContact.phone);
                }

                if (terminalContact.contact) {
                    termString += '\nContact: ' + terminalContact.contact;
                }

                if (terminalContact.contactEmail) {
                    termString += '\nEmail: ' + terminalContact.contactEmail;
                }
            }

            return termString;
        }

        $scope.$on('event:openShipmentEntrySaveDialog', function (event, data) {
            if (data.event){
                okEvent = data.event;
            }
            if (_.isEmpty(data.name)) {
                $scope.$root.$emit('event:operation-success', 'Shipment has been updated successfully within the system.');
                $scope.closeShipmentEntrySaveDialog();
            } else {
                $scope.originInfo = getInfoString(data.originTerminal, data.originTerminalContact);
                $scope.destinationInfo = getInfoString(data.destinationTerminal, data.destTerminalContact);
                $scope.name = data.name;
                $scope.phone = data.phone;
                $scope.email = data.email;
                $scope.carrierName = data.carrierName;

                $scope.shipmentEntrySaveDialog = {
                    show: true
                };
            }
        });
    }
]);