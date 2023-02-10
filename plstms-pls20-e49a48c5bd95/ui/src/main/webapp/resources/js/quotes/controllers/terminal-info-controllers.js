angular.module('pls.controllers').controller('TerminalInfoController', ['$scope', '$filter', 'TerminalInfoService',
    function ($scope, $filter, TerminalInfoService) {
        'use strict';

        $scope.showTerminalInfo = false;
        $scope.terminalInfoModel = {};

        $scope.closeTerminalInfoDialog = function () {
            $scope.showTerminalInfo = false;
            $scope.$emit('event:closeTerminalInfoDialog');
        };

        function getTerminalInfoString(terminal, terminalContact) {
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

        function getTerminalInfo(data) {
            if (data && (data.destinationTerminal || data.originTerminal || data.mileageBtwOrigTermDestTerm
                    || data.mileageFromDestTerminal || data.mileageToOrigTerminal)) {
                $scope.terminalInfo = data;

                $scope.originTerminalInfo = getTerminalInfoString(data.originTerminal, data.originTerminalContact);
                $scope.destinationTerminalInfo = getTerminalInfoString(data.destinationTerminal, data.destTerminalContact);

                $scope.showTerminalInfo = true;
            } else {
                $scope.$root.$emit('event:application-error', 'No Terminal Information!',
                        'No Terminal Information found for selected Carrier, Origin and Destination.');
            }
        }

        $scope.$on('event:openTerminalInfoDialog', function (event, id) {
            TerminalInfoService.getTerminalInfo({shipmentId: id}, getTerminalInfo);
        });

        $scope.$on('event:openTerminalInfoDialogFoManualBol', function (event, id) {
            TerminalInfoService.getTerminalInfoForManualBol({manualBol: id}, getTerminalInfo);
        });

        $scope.$on('event:openTerminalInfoForPreparedCriteria', function (event, data) {
            $scope.terminalInfo = data;
            $scope.originTerminalInfo = getTerminalInfoString(data.originTerminal, data.originTerminalContact);
            $scope.destinationTerminalInfo = getTerminalInfoString(data.destinationTerminal, data.destTerminalContact);
            $scope.terminalInfoModel.parentDialog = data.parentDialog;
            $scope.showTerminalInfo = true;
        });
    }
]);