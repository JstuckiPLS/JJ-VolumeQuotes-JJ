angular.module('plsApp.directives').directive('plsPaymentDialog', ['$http', 'urlConfig', '$timeout', 'ShipmentDocumentEmailService',
    'DictionaryService', function ( $http, urlConfig, $timeout, ShipmentDocumentEmailService, DictionaryService) {

    return {
        restrict: 'A',
        replace: true,
        scope: true,
        templateUrl: 'pages/tpl/pls-payment-dialog-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.paymentModel = {
                    showDialog : false,
                    phone : {},
                    showSendMailDialog:false
            };

            $scope.$on('event:showPaymentDialog', function (event, transferObject) {
               $scope.paymentModel.showSendMailDialog = false;

               DictionaryService.getPLSPayURL({}, function(data) {
                   $scope.paymentModel.plsPayUrl = data.result;
               });

               var url = urlConfig.shipment + '/customer/' + transferObject.data.customerId
                                                + '/location/' + transferObject.data.locationId + '/accountexecutive';

               $http.get(url).then(function(response) {
                    $scope.paymentModel.contactName = response.data.contactName;
                    $scope.paymentModel.phone.number = response.data.phone.number;
                    $scope.paymentModel.phone.countryCode = response.data.phone.countryCode;
                    $scope.paymentModel.phone.areaCode = response.data.phone.areaCode;
                    $scope.paymentModel.phone.extension = response.data.phone.extension;
                    $scope.paymentModel.email = response.data.email;

                    $scope.paymentModel.showDialog = true;
                }, function() {
                    $scope.paymentModel.showDialog = true;
                });

                $scope.paymentModel.loadId = transferObject.data.loadId;
                $scope.paymentModel.customerId = transferObject.data.customerId;
                $scope.paymentModel.locationId = transferObject.data.locationId;

                $scope.paymentModel.bolNumber = transferObject.data.bolNumber;
                $scope.paymentModel.creditCardEmail = transferObject.data.creditCardEmail;
                $scope.paymentModel.isPlsUser = $scope.$root.authData.plsUser;
                $scope.paymentModel.closeHandler = transferObject.closeHandler;

                $scope.paymentModel.emailOptions = {
                        sendMailFunction : function (recipients, subject, content) {
                            ShipmentDocumentEmailService.emailDoc({
                                loadId : $scope.paymentModel.loadId,
                                recipients : recipients,
                                subject : subject,
                                content : content,
                                emailType :"PEN_PAY"
                            }, function() {
                                $scope.$root.$emit('event:operation-success', 'Email send status',
                                        'Email sent successfully');
                                $scope.closePaymentDialog();
                            }, function(data) {
                                $scope.$root
                                        .$emit('event:application-error', 'Email send status', data);
                            });
                        },
                        closeSendMailDialogHandler : function() {
                            $scope.closePaymentDialog();
                        },
                        isDisabledSubject :true,
                        editEmailRecipientsList : "",
                        subject : "Pending Payment - BOL " + $scope.paymentModel.bolNumber,
                        getTemplate: function() {
                            return ShipmentDocumentEmailService.getTemplateForPLSPay({
                                locationId : $scope.paymentModel.locationId
                            });
                        },
                        showSendMailDialog : false
                    };
            });

            $scope.openEmailDialog = function(email) {
                $scope.paymentModel.emailOptions.editEmailRecipientsList = email;
                $scope.paymentModel.emailOptions.showSendMailDialog = true;
            };

            $scope.closePaymentDialog = function() {
                $scope.paymentModel.showDialog = false;
                $timeout(function() {
                    $scope.paymentModel.closeHandler();
                });
            };
        }]
    };
}]);