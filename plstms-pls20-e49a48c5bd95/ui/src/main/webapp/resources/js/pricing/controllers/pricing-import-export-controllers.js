angular.module('plsApp').controller('ImportExportCtrl', ['$scope', '$q', '$timeout', 'urlConfig', 'ExportPricesService', 'ImportPricesService',
    function ($scope, $q, $timeout, urlConfig, ExportPricesService, ImportPricesService) {
        'use strict';

        $scope.importPrices = function () {
            $scope.importPricesOptions.showDialog = true;
        };

        $scope.jobUUID = undefined;

        $scope.exportPricesProgressPanelOptions = {
            showPanel: false,
            progressText: 'Exporting prices ...'
        };

        var callTimer;

        var validateExportStatus = function () {
            ExportPricesService.isExportPricesDone($scope.jobUUID).then(function (data) {
                        if (angular.isString(data.data) ? data.data === 'true' : data.data) {
                            $scope.$root.$emit('event:operation-success', 'Export prices', 'Export prices job has been finished. Job UUID:'
                                    + $scope.jobUUID);

                            ExportPricesService.saveExportPrices($scope.jobUUID);
                            $scope.exportPricesProgressPanelOptions.showPanel = false;
                        } else {
                            callTimer();
                        }
                    }, function (e) {
                        $scope.exportPricesProgressPanelOptions.showPanel = false;
                        $scope.$root.$emit('event:application-error', 'Export prices status validation failed!', e);
                    }
            );
        };

        callTimer = function () {
            $timeout(validateExportStatus, 3000);
        };

        $scope.exportPrices = function () {
            $scope.exportPricesProgressPanelOptions.showPanel = true;

            ExportPricesService.startExport().then(function (data) {
                if (angular.isString(data)) {
                    $scope.jobUUID = data;
                } else {
                    $scope.jobUUID = data.data;
                }

                $scope.$root.$emit('event:operation-success', 'Export prices', 'Export prices job has been started. Job UUID:' + $scope.jobUUID);
                callTimer();
            }, function (data) {
                $scope.$root.$emit('event:application-error', 'Export prices failed!', data);
                $scope.exportPricesProgressPanelOptions.showPanel = false;
            });
        };

        $scope.importPricesOptions = {
            label: 'Import Prices',
            isAddress: false,
            showDialog: false,
            async: true,
            asyncImport: function (importJobUUID) {
                var deferred = $q.defer();

                var callTimer;

                var validateImportStatus = function () {
                    ImportPricesService.isImportPricesDone(importJobUUID).then(function (data) {
                                if (angular.isString(data.data) ? data.data === 'true' : data.data) {
                                    ImportPricesService.getImportPriceResult(importJobUUID).then(function (result) {
                                        deferred.resolve(result);
                                    }, function (error) {
                                        deferred.reject(error);
                                    });
                                } else {
                                    callTimer();
                                }
                            }, function (error) {
                                deferred.reject(error);
                            }
                    );
                };

                callTimer = function () {
                    $timeout(validateImportStatus, 3000);
                };

                callTimer();

                return deferred.promise;
            },
            importUrl: function () {
                return urlConfig.pricing + '/profile/import';
            },
            fixUrl: function () {
                return urlConfig.shipment + '/profile/import_fix_now_doc/';
            },
            removeFixDoc: function (docId) {
                ExportPricesService.removeFixedDoc(docId);
            },
            closeCallback: function () {
                $scope.$root.$emit('event:operation-success', "Success", "Prices were imported successfully");
            }
        };
    }
]);
