angular.module('plsApp.directives').directive('plsEmailsHistory', ['ShipmentDocumentEmailService', 'NgGridPluginFactory', 'urlConfig', '$timeout',
    function (ShipmentDocumentEmailService, NgGridPluginFactory, urlConfig, $timeout) {
        return {
            restrict: 'A',
            scope: {
                parentDialog: '@',
                shipmentId: '='
            },
            replace: true,
            templateUrl: 'pages/tpl/add-emails-history-tpl.html',
            compile: function () {
                return {
                    pre: function (scope) {
                        'use strict';

                        scope.selectedItems = [];
                        scope.gridData = [];

                        scope.getEmailHistory = function () {
                            if(scope.shipmentId){
                                ShipmentDocumentEmailService.getEmailHistory({subParamId: scope.shipmentId}, function (data) {
                                    scope.gridData = data;
                                });
                            }
                        };

                        scope.emailOptions = {
                            showSendEmailDialog: false,
                            docSendMailModalOptions: {
                                parentDialog: 'detailsDialogDiv'
                            },
                            sendMailFunction: function (recipients, subject, content) {
                                ShipmentDocumentEmailService.emailDoc({
                                    recipients: recipients,
                                    subject: subject,
                                    content: content,
                                    documents: scope.emailOptions.documents,
                                    loadId: scope.shipmentId,
                                    emailType: scope.emailOptions.emailType,
                                    notificationType: scope.emailOptions.notificationType
                                }, function () {
                                    scope.$root.$emit('event:operation-success', 'Email send status', 'Email sent successfully');
                                    scope.$broadcast('event:close-email-dialog');
                                    scope.emailOptions.showSendEmailDialog = false;

                                    $timeout(function () {
                                        scope.getEmailHistory();
                                    }, 5000);
                                }, function (data) {
                                    scope.$root.$emit('event:application-error', 'Email send status', data);
                                });
                            }
                        };

                        scope.getEmailHistory();

                        scope.gridOptions = {
                            enableColumnResize: true,
                            data: 'gridData',
                            selectedItems: scope.selectedItems,
                            primaryKey: 'id',
                            columnDefs: [{
                                field: 'emailType',
                                cellFilter: 'emailStatus',
                                displayName: 'Email Type',
                                width: '10%'
                            }, {
                                field: 'subject',
                                displayName: 'Email Subject',
                                width: '10%'
                            }, {
                                field: 'text',
                                displayName: 'Text',
                                cellFilter: 'htmlStringFilter',
                                width: '18%'
                            }, {
                                field: 'notificationType',
                                cellFilter: 'notificationType',
                                displayName: 'Notification Type',
                                width: '12%'
                            }, {
                                field: 'sendTo',
                                displayName: 'Sent To',
                                width: '15%'
                            }, {
                                field: 'attachmentId',
                                sortable: false,
                                displayName: 'Attachments',
                                enableCellEdit: false,
                                cellTemplate: '<div class="ngCellText text-center" ng-class="col.colIndex()">'
                                + '<div pls-attachments-list attachments="{{row.entity.attachments}}"></div></div>',
                                width: '15%'
                            }, {
                                field: 'sendBy',
                                displayName: 'Sent By',
                                width: '10%'
                            }, {
                                field: 'sendTime',
                                displayName: 'Date/Time',
                                width: '10%',
                                cellFilter: 'date:$root.appDateTimeFormat'
                            }],
                            multiSelect: false,
                            plugins: [NgGridPluginFactory.plsGrid()]
                        };

                        scope.isSelectedEmail = function () {
                            return !_.isEmpty(scope.selectedItems);
                        };

                        scope.viewEmail = function () {
                            if (!_.isEmpty(scope.selectedItems)) {
                                scope.emailOptions.documents = [];
                                scope.documents = [];
                                _.each(scope.selectedItems[0].attachments, function (attachment) {
                                    scope.emailOptions.documents.push({
                                        imageMetadataId: attachment.attachmentId,
                                        attachmentFileName: attachment.attachmentName
                                    });
                                    scope.documents.push({
                                        id: attachment.attachmentId,
                                        fileName: attachment.attachmentName
                                    });
                                });

                                scope.emailOptions.editEmailRecipientsList = scope.selectedItems[0].sendTo;
                                scope.emailOptions.subject = scope.selectedItems[0].subject;
                                scope.emailOptions.attachedFileName = "";
                                if (scope.emailOptions.documents) {
                                    _.each(scope.emailOptions.documents, function (document) {
                                        scope.emailOptions.attachedFileName = scope.emailOptions.attachedFileName +
                                                '<a target="_blank" href="/restful/customer/shipmentdocs/' + document.imageMetadataId + '">' +
                                                document.attachmentFileName + '</a>';
                                        if (scope.emailOptions.documents.indexOf(document) !== scope.emailOptions.documents.length - 1) {
                                            scope.emailOptions.attachedFileName = scope.emailOptions.attachedFileName + ", ";
                                        }
                                    });
                                }
                                scope.emailOptions.emailContent = scope.selectedItems[0].text;
                                scope.emailOptions.emailType = scope.selectedItems[0].emailType;
                                scope.emailOptions.notificationType = scope.selectedItems[0].notificationType;
                                scope.emailOptions.showSendEmailDialog = true;
                                scope.emailOptions.isViewMode = true;
                                scope.emailOptions.getTemplate = function () {
                                    return scope.emailOptions.emailContent;
                                };
                                scope.$root.$broadcast('event:showSendMailDialog', scope.emailOptions);
                            }
                        };

                        scope.sendCopy = function () {
                            if (!_.isEmpty(scope.selectedItems)) {
                                scope.emailOptions.documents = [];
                                scope.documents = [];
                                _.each(scope.selectedItems[0].attachments, function (attachment) {
                                    scope.emailOptions.documents.push({
                                        imageMetadataId: attachment.attachmentId,
                                        attachmentFileName: attachment.attachmentName
                                    });
                                    scope.documents.push({
                                        id: attachment.attachmentId,
                                        fileName: attachment.attachmentName
                                    });
                                });

                                scope.emailOptions.editEmailRecipientsList = scope.selectedItems[0].sendTo;
                                scope.emailOptions.subject = scope.selectedItems[0].subject;
                                scope.emailOptions.attachedFileName = "";
                                if (scope.emailOptions.documents) {
                                    _.each(scope.emailOptions.documents, function (document) {
                                        scope.emailOptions.attachedFileName = scope.emailOptions.attachedFileName +
                                                '<a target="_blank" href="/restful/customer/shipmentdocs/' + document.imageMetadataId + '">' +
                                                document.attachmentFileName + '</a>';
                                        if (scope.emailOptions.documents.indexOf(document) !== scope.emailOptions.documents.length - 1) {
                                            scope.emailOptions.attachedFileName = scope.emailOptions.attachedFileName + ", ";
                                        }
                                    });
                                }
                                scope.emailOptions.emailContent = scope.selectedItems[0].text;
                                scope.emailOptions.emailType = scope.selectedItems[0].emailType;
                                scope.emailOptions.notificationType = scope.selectedItems[0].notificationType;
                                scope.emailOptions.showSendEmailDialog = true;
                                scope.emailOptions.isViewMode = false;
                                scope.emailOptions.getTemplate = function () {
                                    return scope.emailOptions.emailContent;
                                };
                                scope.$root.$broadcast('event:showSendMailDialog', scope.emailOptions);
                            }
                        };
                    }
                };
            }
        };
    }
]);