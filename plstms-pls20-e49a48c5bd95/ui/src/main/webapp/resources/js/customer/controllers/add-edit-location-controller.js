angular.module('editCustomer').controller('AddEditLocationCtrl', [
    '$scope', '$filter', 'CustomerLocationsService', 'CustomerService', 'BillToService',
    function ($scope, $filter, CustomerLocationsService, CustomerService, BillToService) {
        'use strict';

        $scope.editLocationModel = {
            accountExecutives: [],
            billTos: []
        };

        CustomerService.getAccountExecutives().success(function (data) {
            $scope.editLocationModel.accountExecutivesBackup = data;
        });

        BillToService.getIdValueByOrgId({customerId: $scope.editCustomerModel.customerId, userId: -1}, function (data) {
            $scope.editLocationModel.billTos = data;
        });

        function getLocationAndAE(location) {
            CustomerLocationsService.getLocation({
                customerId: $scope.editCustomerModel.customerId,
                path: location.id
            }, function (data) {
                $scope.editLocationModel.location = data;
                if (data.accExecPersonId && !_.findWhere($scope.editLocationModel.accountExecutives, {id: data.accExecPersonId})) {
                    $scope.editLocationModel.accountExecutives.push({
                        id: data.accExecPersonId,
                        name: location.accountExecutive,
                        invalid: true
                    });

                    $scope.editLocationModel.accountExecutives = _.sortBy($scope.editLocationModel.accountExecutives, 'name');
                }

                $scope.editLocationModel.showLocationDialog = true;
            });
        }

        $scope.$on('event:showAddEditLocationDialog', function (events, location) {
            $scope.editLocationModel.accountExecutives = angular.copy($scope.editLocationModel.accountExecutivesBackup);

            if (location) {
                getLocationAndAE(location);
            } else {
                $scope.editLocationModel.showLocationDialog = true;
                $scope.editLocationModel.location = {};
            }
        });

        $scope.resetAccExecStartDate = function () {
            $scope.editLocationModel.location.accExecStartDate = $filter('date')(new Date(), 'yyyy-MM-dd');
        };

        $scope.checkAccExecSelected = function () {
            return _.isUndefined($scope.editLocationModel.location) || _.isUndefined($scope.editLocationModel.location.accExecPersonId)
                    || _.isNull($scope.editLocationModel.location.accExecPersonId);
        };

        $scope.closeLocationDialog = function () {
            $scope.editLocationModel.showLocationDialog = false;
            delete $scope.editLocationModel.location;
        };

        $scope.saveLocation = function () {
            CustomerLocationsService.saveLocation({customerId: $scope.editCustomerModel.customerId}, $scope.editLocationModel.location, function () {
                $scope.$root.$emit('event:operation-success', 'Location was successfully saved');
                $scope.$emit('event:locationSaved');
                $scope.closeLocationDialog();
            }, function () {
                $scope.$root.$emit('event:application-error', 'Location save failed!');
            });
        };
    }
]);