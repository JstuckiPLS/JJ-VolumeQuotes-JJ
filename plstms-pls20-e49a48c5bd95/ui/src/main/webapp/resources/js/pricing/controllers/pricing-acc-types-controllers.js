angular.module('plsApp').controller('AccessorialTypesCtrl', ['$scope', 'AccTypesServices', 'CheckAccTypeCodeExists', 'NgGridPluginFactory',
    function ($scope, AccTypesServices, CheckAccTypeCodeExists, NgGridPluginFactory) {
        'use strict';

        $scope.accessorialTypeModel = {
            accessorialType: {
                status: 'ACTIVE'
            },
            changeStatusBtnCaption: 'Archive',
            editMode: false,
            selectedItems: [],
            currentTab: 'Active',
            accessorialGroups: [{group: '', value: undefined},
                {group: 'Pickup', value: 'PICKUP'},
                {group: 'Delivery', value: 'DELIVERY'}],
            isAccessorialTypeUnique: true
        };

        $scope.accTypeValidation = function () {
            $scope.errorMessage = "";

            CheckAccTypeCodeExists.validate({
                code: $scope.accessorialTypeModel.accessorialType.id
            }, function (data) {
                if (data.result === "true") {
                    $scope.errorMessage = "Accessorial Type with the same Code already exists.";
                    $scope.$root.$emit('event:application-error', $scope.errorMessage);
                }
            });
        };

        $scope.showActiveList = function () {
            $scope.accessorialTypeModel.changeStatusBtnCaption = "Archive";
            $scope.accessorialTypeModel.currentTab = "Active";

            AccTypesServices.active({}, function (response) {
                $scope.listItems = response;
                $scope.clear();
            });
        };

        $scope.showInactiveList = function () {
            $scope.accessorialTypeModel.changeStatusBtnCaption = "Activate";
            $scope.accessorialTypeModel.currentTab = "Inactive";

            AccTypesServices.inactive({}, function (response) {
                $scope.listItems = response;
                $scope.clear();
            });
        };

        function getID() {
            var arr = [];

            _.each($scope.accessorialTypeModel.selectedItems, function (item) {
                arr.push(item.id);
            });

            return arr;
        }

        $scope.editDetails = function () {
            if ($scope.accessorialTypeModel.selectedItems.length !== 1) {
                return;
            }

            $scope.accessorialTypeModel.editMode = true;
            $scope.accessorialTypeModel.accessorialType = {
                id: $scope.accessorialTypeModel.selectedItems[0].id,
                description: $scope.accessorialTypeModel.selectedItems[0].description,
                applicableTo: $scope.accessorialTypeModel.selectedItems[0].applicableTo,
                accessorialGroup: $scope.accessorialTypeModel.selectedItems[0].accessorialGroup,
                status: $scope.accessorialTypeModel.selectedItems[0].status,
                version: $scope.accessorialTypeModel.selectedItems[0].version
            };

            AccTypesServices.isUnique({accessorialTypeCode: $scope.accessorialTypeModel.accessorialType.id}, function (isUnique) {
                $scope.accessorialTypeModel.isAccessorialTypeUnique = isUnique.result === "true" ? true : false;
            });
        };

        $scope.clear = function () {
            $scope.accessorialTypeModel.accessorialType = {
                status: 'INACTIVE'
            };

            $scope.accessorialTypeModel.editMode = false;
            $scope.accessorialTypeModel.selectedItems.length = 0;
            $scope.errorMessage = "";
        };

        function loadAllType() {
            AccTypesServices.get({}, function (data) {
                $scope.$root.accessorialTypes = data;

                if ($scope.accessorialTypeModel.currentTab === "Active") {
                    $scope.accessorialTypeModel.changeStatusBtnCaption = "Archive";
                    $scope.listItems = _.filter(data, function (item) {
                        return item && item.status === 'ACTIVE';
                    });
                } else {
                    $scope.listItems = _.filter(data, function (item) {
                        $scope.accessorialTypeModel.changeStatusBtnCaption = "Activate";
                        return item && item.status === 'INACTIVE';
                    });
                }

                $scope.clear();
            });
        }

        $scope.save = function () {
            AccTypesServices.save({}, $scope.accessorialTypeModel.accessorialType, function () {
                loadAllType();
                $scope.$root.$emit('event:operation-success', 'Accessorial Type was successfully saved');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Accessorial Type save failed!');
            });
            $scope.clear();
        };

        function activate() {
            AccTypesServices.activate({}, getID(), function (response) {
                $scope.$root.accessorialTypes = response;

                $scope.listItems = _.filter(response, function (item) {
                    return item && item.status === 'INACTIVE';
                });

                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Accessorial Type was successfully activated');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Accessorial Type activation failed!');
            });
        }

        function inactivate() {
            AccTypesServices.inactivate({}, getID(), function (response) {
                $scope.$root.accessorialTypes = response;

                $scope.listItems = _.filter(response, function (item) {
                    return item && item.status === 'ACTIVE';
                });

                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Accessorial Type was successfully inactivated');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Accessorial Type inactivation failed!');
            });
        }

        $scope.changeStatusEvent = function () {
            switch ($scope.accessorialTypeModel.changeStatusBtnCaption) {
                case 'Activate' :
                    activate();
                    break;
                case 'Archive' :
                    inactivate();
                    break;
            }
        };

        function loadUnits() {
            AccTypesServices.applicableunits({}, function (response) {
                $scope.applicableToUnits = response;
            });
        }

        loadUnits();
        $scope.showActiveList();

        $scope.gridOptions = {
            enableColumnResize: true,
            multiSelect: false,
            selectedItems: $scope.accessorialTypeModel.selectedItems,
            data: 'listItems',
            columnDefs: [{
                field: 'id',
                displayName: 'Type',
                width: '10%'
            }, {
                field: 'description',
                displayName: 'Description',
                width: '58%'
            }, {
                field: 'accessorialGroup',
                displayName: 'Group',
                width: '15%'
            }, {
                field: 'applicableTo',
                displayName: 'Applicable To',
                width: '15%'
            }],
            plugins: [
                NgGridPluginFactory.plsGrid(),
                NgGridPluginFactory.progressiveSearchPlugin(),
                NgGridPluginFactory.actionPlugin()
            ],
            useExternalSorting: false,
            progressiveSearch: true
        };

        $scope.$watch('accessorialTypeModel.accessorialType.id', function (newValue) {
            /* skip undefined value during initialization of model in digest*/
            if (_.isUndefined($scope.accessorialTypeModel.accessorialType.id)) {
                return;
            }

            if (newValue.length > 3) {
                $scope.accessorialTypeModel.accessorialType.id = newValue.substring(0, 3);
            }
        });

        $scope.$watch('accessorialTypeModel.accessorialType.description', function (newValue) {
            /* skip undefined value during initialization of model in digest*/
            if (_.isUndefined($scope.accessorialTypeModel.accessorialType.description)) {
                return;
            }

            if (newValue.length > 50) {
                $scope.accessorialTypeModel.accessorialType.description = newValue.substring(0, 50);
            }
        });
    }
]);