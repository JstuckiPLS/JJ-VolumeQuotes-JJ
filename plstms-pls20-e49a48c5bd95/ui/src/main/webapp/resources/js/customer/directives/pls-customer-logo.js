/**
 *  AngularJS directive which provides displaying/uploading customer logo.
 */
angular.module('plsApp').directive('plsCustomerLogo', ['$http', '$compile', '$templateCache', 'CustomerService',
    function ($http, $compile, $templateCache, CustomerService) {
        return {
            restrict: 'A',
            scope: {
                plsCustomerLogo: '=',
                displayOnBol: '=',
                displayOnShipLabel: '=',
                customerId: '=',
                parentDialog: '@'
            },
            templateUrl: 'pages/tpl/customer-logo-tpl.html',
            controller: ['$scope', function ($scope) {
                'use strict';

                $http.get('pages/tpl/upload-logo-dialog-tpl.html', {cache: $templateCache}).then(function (result) {
                    var dialog = $compile(result.data)($scope);
                    angular.element('#content').append(dialog);

                    $scope.$on('$destroy', function () {
                        dialog.remove();
                    });
                });

                $scope.dialogOptions = {};
                $scope.dialogOptions.parentDialog = $scope.parentDialog;

                $scope.showUploadDialog = function () {
                    $scope.showDialog = true;
                };

                $scope.closeUploadDialog = function () {
                    $scope.showDialog = false;
                };

                $scope.updateCustomerLogo = function (logoId) {
                    if (logoId) {
                        $scope.plsCustomerLogo = logoId;
                        $scope.customerLogoUrl = '/restful/organization/logo/' + logoId + '?' + new Date().getTime();
                    }
                };

                if ($scope.customerId) {
                    CustomerService.isCustomerLogoAvailable($scope.customerId).success(function (data) {
                        if (angular.isString(data) ? data === 'true' : data) {
                            $scope.customerLogoUrl = '/restful/organization/' + $scope.customerId + '/logo?' + new Date().getTime();
                        }
                    });
                } else if ($scope.plsCustomerLogo) {
                    $scope.updateCustomerLogo($scope.plsCustomerLogo);
                }

                $scope.$watch('plsCustomerLogo', function () {
                    if ($scope.plsCustomerLogo === null) {
                        //Clear previous logo preview. Only exact 'null' value means that logo should be cleared
                        $('#logoSmallImg').removeAttr('data-ng-src').removeAttr('src');
                        $('#logoLargeImg').removeAttr('data-ng-src').removeAttr('src');
                    }
                });

                $scope.customerLogoFile = undefined;

                $scope.onUpdateLogoButtonClick = function () {
                    if ($scope.customerLogoFile === undefined ||
                            $scope.customerLogoFile.files === undefined ||
                            $scope.customerLogoFile.files.length === 0) {
                        return;
                    }

                    $scope.fileSentOk = false;
                    $scope.uploadProgressMsg = null;

                    var formData = new FormData();
                    formData.append('logo', $scope.customerLogoFile.files[0]);
                    var xhr = new XMLHttpRequest();

                    if (xhr.upload) {
                        xhr.upload.onprogress = function (e) {
                            var done = e.position || e.loaded, total = e.totalSize || e.total;
                            $scope.uploadProgressMsg = 'Progress: ' + done + ' / ' + total + ' = ' + (Math.floor(done / total * 1000) / 10) + '%';
                        };
                    }

                    xhr.onreadystatechange = function (e) {
                        // request finished, response is ready
                        if (xhr.readyState === 4) {
                            if (xhr.status === 200) {
                                $scope.$apply(function () {
                                    $scope.fileSentOk = true;
                                    $scope.customerLogoFile.value = '';
                                    var logoId;

                                    if (xhr.response) {
                                        logoId = JSON.parse(xhr.response).data;
                                    }

                                    $scope.updateCustomerLogo(logoId);
                                });
                            } else if (xhr.status === 401) {
                                $scope.$root.$broadcast('event:auth-loginRequired');
                            } else {
                                $scope.$root.$emit('event:application-error', 'Logo updated failed!', 'Can\'t update logo');
                            }
                        }
                    };

                    xhr.open('post', '/restful/organization/logo', true);
                    xhr.send(formData);
                };
            }]
        };
    }
]);