/**
 * AngularJS directive that provides import functionality.
 *
 * It's necessary to define model for this directive. It must contain following parameters:
 *  label - is the dialog's label
 *  showDialog - is the flag whether import dialog should be opened or not
 *  importUrl - is the URL to the REST service for import data
 *  fixUrl - is the URL to the REST service that provides access to the fix document
 *  removeFixDoc - is the JS function that will remove fix document. It takes documentId as a parameter
 *  closeCallback - is the optional JS function that will be called when import dialog will be closed
 *
 * @author: Sergey Kirichenko
 * Date: 6/19/13
 * Time: 11:33 AM
 */
angular.module('plsApp.directives').directive('plsImport', ['$log', function ($log) {
    var JOB_UUID_LENGTH = 36;

    return {
        restrict: 'A',
        scope: {
            importDialogOptions: '=plsImport'
        },
        replace: true,
        templateUrl: 'pages/tpl/import-data-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            var clearUploadElement = function () {
                var fileElem = angular.element('#upload');
                fileElem.wrap('<form>').parent('form').trigger('reset');
                fileElem.unwrap();
                fileElem.replaceWith(angular.element('#upload').clone(true));
                var fileForm = angular.element('#uploadForm');
                fileForm.trigger('reset');
            };

            $scope.importItemsModel = {
                importUrl: $scope.importDialogOptions.importUrl(),
                importFile: null,
                progressPanelOptions: {
                    progressText: 'Loading...',
                    showPanel: false
                },
                importResults: {
                    showResults: false,
                    modalOptions: {
                        parentDialog: 'dataImportDialog'
                    },
                    closeResults: function () {
                        if ($scope.importItemsModel.importResults.fixNowDocId && angular.isFunction($scope.importDialogOptions.removeFixDoc)) {
                            $scope.importDialogOptions.removeFixDoc($scope.importItemsModel.importResults.fixNowDocId);
                        }

                        $scope.importItemsModel.importResults.showResults = false;
                        $scope.importItemsModel.close();
                    },
                    fixNow: function () {
                        var url = $scope.importDialogOptions.fixUrl() + $scope.importItemsModel.importResults.fixNowDocId;
                        window.open(encodeURI(url), '_blank');
                    }
                },
                close: function () {
                    if (angular.isFunction($scope.importDialogOptions.closeCallback)) {
                        $scope.importDialogOptions.closeCallback();
                    }

                    $scope.importDialogOptions.showDialog = false;
                },
                setFile: function (element) {
                    if ((element.files && element.files.length) || element.value) {
                        var fileName = element.files && element.files.length ? element.files[0].name : element.value;

                        if (fileName.match(/\.xls$|\.xlsx$|\.csv$/i)) {
                            $scope.$apply(function ($scope) {
                                $scope.importItemsModel.importFile = element.value;
                            });
                            return;
                        } else {
                            $scope.$root.$broadcast('event:application-error', 'Data import failed!',
                                    'The file format is incorrect. Please select the correct file.');
                        }
                    }

                    $scope.importItemsModel.importFile = undefined;
                    clearUploadElement();
                }
            };

            $scope.uploadCallback = function (content, completed) {
                var processImportResult = function(responseObj) {
                    $scope.importItemsModel.importResults.success = responseObj.success;
                    $scope.importItemsModel.importResults.recordsNumberExceeded = responseObj.errorType === 'RECORDS_NUMBER_EXCEEDED';
                    $scope.importItemsModel.importResults.failedCount = parseInt(responseObj.failedCount, 10);
                    $scope.importItemsModel.importResults.succeedCount = parseInt(responseObj.succeedCount, 10);
                    $scope.importItemsModel.importResults.hasFailedRecords = $scope.importItemsModel.importResults.failedCount > 0;
                    $scope.importItemsModel.importResults.rowsCount = $scope.importItemsModel.importResults.succeedCount
                        + $scope.importItemsModel.importResults.failedCount;
                    $scope.importItemsModel.importResults.hasNoRecords = $scope.importItemsModel.importResults.rowsCount <= 0;
                    $scope.importItemsModel.importResults.fixNowDocId = responseObj.fixNowDocId;

                    $scope.importItemsModel.importResults.showResults = true;
                    clearUploadElement();
                    $scope.importItemsModel.importFile = undefined;
                };

                if ($scope.importDialogOptions.async) {
                    if (completed && content) {
                        if (content.length !== JOB_UUID_LENGTH) {
                            if (content.indexOf('HTTP Status 401') !== -1) {
                                $scope.$root.$broadcast('event:auth-loginRequired');
                            } else {
                                $scope.$root.$broadcast('event:application-error', 'Data import failed!', 'Can\'t import items async. Not a job UUID['
                                    + content + ']');
                            }
                        }

                        $scope.importDialogOptions.asyncImport(content).then(function (data) {
                            $scope.importItemsModel.progressPanelOptions.showPanel = false;
                            processImportResult(data.data);
                        }, function (error) {
                            $scope.importItemsModel.progressPanelOptions.showPanel = false;
                            $log.error('Can not import data', error);
                            $scope.$root.$broadcast('event:application-error', 'Data import failed!', 'Can\'t import items async. ' + error);
                        });
                    }
                } else {
                    if (completed) {
                        $scope.importItemsModel.progressPanelOptions.showPanel = false;
                    }

                    if (completed && content) {
                        var responseObj;

                        try {
                            responseObj = JSON.parse(content);
                        } catch (e) {
                            if (content.indexOf('HTTP Status 401') !== -1) {
                                $scope.$root.$broadcast('event:auth-loginRequired');
                            } else {
                                $log.error('Can not import data', e);
                                $scope.$root.$broadcast('event:application-error', 'Data import failed!', 'Can\'t import items');
                            }

                            return;
                        }
                        processImportResult(responseObj);
                    }
                }
            };

            $scope.getImportResultsWindowClass = function () {
                if (!$scope.importItemsModel.importResults.success && !$scope.importItemsModel.importResults.recordsNumberExceeded) {
                    return 'modalWidth3';
                } else {
                    return 'modalWidth4';
                }
            };

            $scope.importSubmit = function () {
                $scope.importItemsModel.importUrl = $scope.importDialogOptions.importUrl();
                $scope.importItemsModel.progressPanelOptions.showPanel = true;
            };
        }]
    };
}]);
