/**
 * Directive that updates GL # of shipment for Sid Harvey customer.
 *
 * @author Aleksandr Leshchenko
 */
angular.module('plsApp.directives').directive('plsGlGenerator', function () {
    return {
        restrict: 'A',
        scope: {
            shipment: '=',
            customer: '='
        },
        controller: ['$scope', function ($scope) {
            'use strict';

            var SID_HARVEY_ID = 193362;

            var unRegisterWatch;

            function isSidHarveyOrMitco(address, code) {
                return (address.indexOf("SID HARVEY") !== -1 || address === "MITCO MANUFACTURING" || address === "MITCO") && /\d{3}/.exec(code);
                // code contains at least 3 digits in a row
            }

            /**
             * Returns last three digits in a row from @val text.
             */
            function getLastThreeDigits(val) {
                var temp = val.split("").reverse().join("");                // reverse string
                temp = /\d{3}/.exec(temp);                                  // find first match
                return temp ? temp[0].split("").reverse().join("") : '';    // reverse result if match found
            }

            //generate gl #
            function updateGLNumber(originAddressName, originAddressCode, destinationAddressName, destinationAddressCode) {
                var originAddressCodeSuffix = getLastThreeDigits(originAddressCode);
                var destinationAddressCodeSuffix = getLastThreeDigits(destinationAddressCode);
                var result;

                if (originAddressName === "SID HARVEY DISTRIBUTION CENTER 0085"
                        || destinationAddressName === "SID HARVEY DISTRIBUTION CENTER 0085") {
                    result = "085-5095.0030";
                } else if (isSidHarveyOrMitco(originAddressName, originAddressCode)
                        && isSidHarveyOrMitco(destinationAddressName, destinationAddressCode)) {
                    result = destinationAddressCodeSuffix + "-5095.0004";
                } else if (!isSidHarveyOrMitco(originAddressName, originAddressCode)
                        && isSidHarveyOrMitco(destinationAddressName, destinationAddressCode)) {
                    result = destinationAddressCodeSuffix + "-5095.0010";
                } else if (isSidHarveyOrMitco(originAddressName, originAddressCode)
                        && !isSidHarveyOrMitco(destinationAddressName, destinationAddressCode)) {
                    result = originAddressCodeSuffix + "-5050.0010";
                }

                $scope.shipment.finishOrder.glNumber = result;
            }

            /**
             * When both origin and destination addresses change at one digest cycle, it means that address was not changed manually
             * (For example when shipment was loaded for editing or copied). GL # shouldn't be regenerated in this case.
             * Only if user manually changed one of addresses, then we should regenerate GL #.
             */
            function isOnlyOneFieldChanged(newVal, oldVal) {
                return (_.isEqual(newVal[0], oldVal[0]) && _.isEqual(newVal[1], oldVal[1]))
                        || (_.isEqual(newVal[2], oldVal[2]) && _.isEqual(newVal[3], oldVal[3]));
            }

            function watch() {
                if (_.isFunction(unRegisterWatch)) {
                    return;
                }

                unRegisterWatch = $scope.$watch('[shipment.originDetails.address.addressName,' +
                        'shipment.originDetails.address.addressCode,' +
                        'shipment.destinationDetails.address.addressName,' +
                        'shipment.destinationDetails.address.addressCode]',
                        function (newVal, oldVal) {
                            if (!_.isEqual(newVal, oldVal)
                                    && isOnlyOneFieldChanged(newVal, oldVal)
                                    && !_.isEmpty(newVal[0])
                                    && !_.isEmpty(newVal[1])
                                    && !_.isEmpty(newVal[2])
                                    && !_.isEmpty(newVal[3])) {
                                updateGLNumber(newVal[0].toUpperCase().trim(),
                                        newVal[1].toUpperCase().trim(),
                                        newVal[2].toUpperCase().trim(),
                                        newVal[3].toUpperCase().trim());
                            }
                        }, true);
            }

            $scope.$watch('customer.id', function (newVal) {
                if (newVal === SID_HARVEY_ID) {
                    watch();
                } else if (_.isFunction(unRegisterWatch)) {
                    unRegisterWatch();
                    unRegisterWatch = undefined;
                }
            });
        }]
    };
});