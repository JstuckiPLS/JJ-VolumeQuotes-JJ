/**
 * Directive that represents dialog window for sending email.
 * It has user email search field with progressive search functionality.
 *
 * Usage:
 * In page code:
 *     <div data-pls-send-mail="emailOptions"
 *          data-dialog-show-option="showSendMailDialog"
 *          data-modal-options="shipmentDetailsModel.shipmentSendMailModalOptions"
 *        />
 *
 * In controller:
 *     $scope.emailOptions = {
 *         subject : 'Email Subject',
 *         attachedFileName : 'name of attached file',
 *         sendMailFunction : function(recipients, subject) {
 *            // implementations
 *         }
 *     };
 *     $scope.showSendMailDialog = true; // indicates if dialog is shown
 *     $scope.shipmentSendMailModalOptions : {
 *          parentDialog : 'detailsDialogDiv'
 *     };
 *
 *     As for scope.emailOptions.sendMailFunction
 *     - recipients - string with emails separated with semicolon
 *
 *     Events:
 *         'event:close-email-dialog' makes send mail dialog not visible
 */
angular.module('plsApp.directives').directive('plsSendMail', ['$q', 'EmailRecipientsService', function ($q, EmailRecipientsService) {
    return {
        restrict: 'A',
        scope: {
            options: '=plsSendMail',
            dialogShowOption: '=',
            modalOptions: '='
        },
        replace: true,
        templateUrl: 'pages/tpl/pls-send-mail-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.onAddClick = function () {
                if ($scope.selectedRecipient) {
                    if (!$scope.editEmailRecipientsList || $.trim($scope.editEmailRecipientsList) === '') {
                        $scope.editEmailRecipientsList = $scope.selectedRecipient.email;
                    } else {
                        $scope.editEmailRecipientsList = $scope.editEmailRecipientsList + ';' + $scope.selectedRecipient.email;
                    }

                    $scope.selectedRecipient = undefined;
                }
            };

            $scope.showUserHtml = function (user) {
                if (user) {
                    return user.fullName + ' &lt;' + user.email + '&gt;';
                }
            };

            $scope.showUser = function (user) {
                if (user) {
                    return user.fullName + ' <' + user.email + '>';
                }
            };

            $scope.$on('event:showSendMailDialog', function (event, options) {
                $scope.options = options;
            });

            $scope.$watch('dialogShowOption', function (newValue, oldValue) {
                if (newValue !== oldValue && newValue) {
                    if (_.isFunction($scope.options.getTemplate)) {
                        var templateResult = $scope.options.getTemplate();
                        if (templateResult.$promise) {
                            templateResult.$promise.then(function (data) {
                                $scope.emailContent = data.result;
                            });
                        } else {
                            $scope.emailContent = templateResult;
                        }
                    }
                }
            });

            $scope.onSendClick = function () {
                $scope.options.sendMailFunction($scope.editEmailRecipientsList, $scope.editEmailSubject, $scope.emailContent);
            };

            $scope.$watch('options', function (newValue, oldValue) {
                if (newValue) {
                    $scope.editEmailSubject = newValue.subject;
                    $scope.editEmailRecipientsList = newValue.editEmailRecipientsList;
                    $scope.emailContent = newValue.emailContent;
                }
            }, true);

            $scope.findUser = function (criteria) {
                $scope.criteria = criteria;
                var deferred = $q.defer();

                EmailRecipientsService.findMachingUsers({
                    filterValue: criteria // filtering value
                }, function (result) {
                    deferred.resolve(result);
                }, function () {
                    deferred.reject([]);
                });

                return deferred.promise;
            };

            $scope.closeSendMailDialog = function () {
                $scope.options.showSendEmailDialog = false;

                if (angular.isFunction($scope.options.closeSendMailDialogHandler)) {
                    $scope.options.closeSendMailDialogHandler();
                    delete $scope.options.closeSendMailDialogHandler;
                }
            };

            $scope.$on('event:close-email-dialog', function () {
                $scope.closeSendMailDialog();
            });
        }]
    };
}]);