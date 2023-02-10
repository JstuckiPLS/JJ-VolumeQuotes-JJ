angular.module('plsApp').controller('BlockLaneDtlCtrl', [
     '$scope', '$route', 'NgGridPluginFactory', 'GetCustomerBlockLaneService', 'GetBlanketCarrListService', 'ChangeStatusBlockLaneService',
     '$location', '$filter', 'CustomerBlockLaneService', function ($scope, $route, NgGridPluginFactory, GetCustomerBlockLaneService,
     GetBlanketCarrListService, ChangeStatusBlockLaneService, $location, $filter, CustomerBlockLaneService) {

         $scope.setMinExpiryDate = function () {
             if($scope.blockLane.effDate instanceof Date) {
                  $scope.minExpiryDate = new Date($scope.blockLane.effDate);
             } else if(!_.isEmpty($scope.blockLane.effDate)){
                  var parts = $scope.blockLane.effDate.match(/(\d+)/g);
                  $scope.minExpiryDate = new Date(parts[0], parts[1]-1, parts[2]);
             }
              $scope.minExpiryDate.setDate($scope.minExpiryDate.getDate()+1);
          };

          $scope.blockLane = {
               shipperId: $route.current.params.customerId
          };

          $scope.blockLane.effDate = new Date();
          $scope.minEffectiveDate = new Date();
          $scope.minExpiryDate = $scope.setMinExpiryDate();
          $scope.selectedItems = [];
          $scope.isEditCarrierZip = false;

          GetBlanketCarrListService.query({id: $scope.blockLane.shipperId}, function (response) {
               $scope.blanketCarrProfiles = response;
               $scope.blanketCarrProfiles.unshift({id:-1, name:'ALL'});
          });

         function removeInvalidCarrProfFromList() {
              var indexOfItemToBeRemoved = _.findIndex($scope.blanketCarrProfiles, function(blkCarrProf) {
                                                 return blkCarrProf.invalid; });
              if (indexOfItemToBeRemoved !== -1) {
                  $scope.blanketCarrProfiles.splice(indexOfItemToBeRemoved,1);
              }
          }

          $scope.clear = function () {
              $scope.selectedCarrProfile = undefined;
              removeInvalidCarrProfFromList();
              $scope.selectedItems.length = 0;
              $scope.blockLane = {
                   shipperId: $route.current.params.customerId
              };
              $scope.blockLane.effDate = new Date();
              $scope.isEditCarrierZip = false;
          };

          $scope.loadBlockLane = function (status) {
              $scope.currentTabName = status;
              $scope.clear();

              switch (status) {
                  case 'Active':
                      GetCustomerBlockLaneService.active({id: $scope.blockLane.shipperId}, function (response) {
                          $scope.blockLaneList = response;
                      });
                      break;
                  case 'Expired':
                      GetCustomerBlockLaneService.expired({id: $scope.blockLane.shipperId}, function (response) {
                          $scope.blockLaneList = response;
                      });
                      break;
                  case 'Archived':
                      GetCustomerBlockLaneService.inactive({id: $scope.blockLane.shipperId}, function (response) {
                          $scope.blockLaneList = response;
                      });
                      break;
              }
          };

          $scope.loadBlockLane('Active');

          $scope.gridOptions = {
                  enableColumnResize: true,
                  selectedItems: $scope.selectedItems,
                  multiSelect: false,
                  data: 'blockLaneList',
                  columnDefs: [{
                      field: 'name',
                      displayName: 'Blanket Carrier'
                  }, {
                      field: 'origin',
                      displayName: 'Origin'
                  }, {
                      field: 'destination',
                      displayName: 'Destination'
                  }, {
                      field: 'effDate',
                      displayName: 'Effective Date'
                  }, {
                      field: 'expDate',
                      displayName: 'Expiry Date'
                  }, {
                      field: 'notes',
                      displayName: 'Notes'
                  }],
                  plugins: [NgGridPluginFactory.plsGrid()]
              };

          $scope.setZips = function () {
              if (!_.isEmpty($scope.blockLane.origin)) {
                  $scope.blockLane.origin = $scope.blockLane.origin.toUpperCase();
              }

              if (!_.isEmpty($scope.blockLane.destination)) {
                  $scope.blockLane.destination = $scope.blockLane.destination.toUpperCase();
              }

              $scope.blockLane.carrierId = $scope.selectedCarrProfile.id;
              $scope.blockLane.status = 'ACTIVE';

              CustomerBlockLaneService.save($scope.blockLane, function () {
                  $scope.loadBlockLane($scope.currentTabName);
                  $scope.$root.$emit('event:operation-success', 'Block Carrier Lane is successfully saved');
              }, function () {
                  $scope.$root.$emit('event:application-error', 'Block Carrier Lane save failed!');
              });
          };

          $scope.edit = function () {
              if ($scope.currentTabName !== "Archived") {
                  removeInvalidCarrProfFromList();
                  CustomerBlockLaneService.get({
                      id: $scope.selectedItems[0].id
                  }, function (response) {
                        $scope.blockLane =  response;
                        $scope.blockLane.shipperId = $route.current.params.customerId;
                        $scope.selectedCarrProfile = _.find($scope.blanketCarrProfiles, function(blkCarrProf) {
                                                              return blkCarrProf.id === $scope.blockLane.carrierId; });
                        if (!$scope.selectedCarrProfile) {
                             $scope.blanketCarrProfiles.push({id:$scope.blockLane.carrierId,
                                 name:$scope.blockLane.name, invalid:true});
                             $scope.selectedCarrProfile = _.find($scope.blanketCarrProfiles, function(blkCarrProf) {
                                 return blkCarrProf.id === $scope.blockLane.carrierId; });
                        }
                        $scope.isEditCarrierZip = true;
                  });
              }
          };

          $scope.saveAsNew = function () {
              $scope.blockLane.id = undefined;
              $scope.blockLane.version = undefined;
              $scope.setZips();
          };

          $scope.expire = function () {
              ChangeStatusBlockLaneService.expire({
                  profileId: $scope.blockLane.shipperId
              }, _.pluck($scope.selectedItems, 'id'), function (response) {
                  $scope.clear();
                  $scope.blockLaneList = response;
                  $scope.$root.$emit('event:operation-success', 'Blocked lane is Expired');
              });
          };

          $scope.inactivate = function (active) {
              ChangeStatusBlockLaneService.inactivate({
                  profileId: $scope.blockLane.shipperId,
                  isActive: active
              }, _.pluck($scope.selectedItems, 'id'), function (response) {
                  $scope.clear();
                  $scope.blockLaneList = response;
                  $scope.$root.$emit('event:operation-success', 'Blocked lane is archived');
              });
          };

          $scope.reactivate = function () {
              ChangeStatusBlockLaneService.reactivate({
                   profileId: $scope.blockLane.shipperId
              }, _.pluck($scope.selectedItems, 'id'), function (response) {
                  $scope.clear();
                  $scope.blockLaneList = response;
                  $scope.$root.$emit('event:operation-success', 'Blocked lane is activated');
              });
          };
     }
]);