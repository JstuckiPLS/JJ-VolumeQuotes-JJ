/**
 * Service for saving export data.
 *
 * @author Mykola Teslenko
 */
angular.module('plsApp.directives.services').factory('ExportService', ['$resource', '$window', '$rootScope',
    function ($resource, $window, $rootScope) {
        return {
            exportData: function (exportData) {
                $resource('/restful/export/exportData', {}, {
                            saveExportData: {
                                method: 'POST',
                                transformResponse: function (data) {
                                    return {
                                        result: data
                                    };
                                }
                            }
                        }
                ).saveExportData({}, exportData, function (data) {
                    $window.open('/restful/export/report?uuid=' + data.result, '_blank');
                }, function () {
                    $rootScope.$emit('event:application-error', 'Export failed!');
                });
            }
        };
    }
]);