angular.module('pricing').factory('ExportPricesService', ['$http', 'urlConfig', '$window',
    function ($http, urlConfig, $window) {
        return {
            exportSync: function () {
                return $window.open(urlConfig.pricing + '/profile/export/sync', '_blank');
            },
            startExport: function () {
                return $http.get(urlConfig.pricing + '/profile/export');
            },
            isExportPricesDone: function (jobUUID) {
                return $http.get(urlConfig.pricing + '/profile/export/' + jobUUID + "/finished");
            },
            saveExportPrices: function (jobUUID) {
                $window.open(urlConfig.pricing + '/profile/export/' + jobUUID + "/result", '_blank');
            },
            removeFixedDoc: function (docId) {
                return $http['delete'](urlConfig.pricing + '/profile/import_fix_now_doc/' + docId);
            }
        };
    }
]);

angular.module('pricing').factory('ImportPricesService', ['$http', 'urlConfig', '$window',
    function ($http, urlConfig, $window) {
        return {
            isImportPricesDone: function (jobUUID) {
                return $http.get(urlConfig.pricing + '/profile/import/' + jobUUID + "/finished");
            },
            getImportPriceResult: function (jobUUID) {
                return $http.get(urlConfig.pricing + '/profile/import/' + jobUUID + "/result");
            }
        };
    }
]);
