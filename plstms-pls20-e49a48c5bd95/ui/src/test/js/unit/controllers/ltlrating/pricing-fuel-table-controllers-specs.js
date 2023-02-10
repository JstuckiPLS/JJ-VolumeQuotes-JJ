/**
 * Tests FuelTableCtrl controller.
 * 
 * @author Sergii Belodon
 */
 
 describe('FuelTableCtrl controller test', function() {
   // Angular scope
   var scope = undefined;
   
   // FuelTableCtrl controller
   var controller = undefined;
   
   var fuelList = [
                   {id:1, minRate:1, maxRate:2, surcharge:10},
                   {id:2, minRate:2.01, maxRate:3, surcharge:20},
                   {id:3, minRate:3.01, maxRate:4, surcharge:30}
                  ];
   
   var mockGetLtlFuelSurchargeService = {
       getActive: function(profileDetailId,success){
         success(fuelList);
       }
   };
   
   var mockLtlFuelSurchargeSaveService = {
       saveAll: function(fuelTableList,f){
         
       }
   };
 
     beforeEach(module('plsApp'));
     
     beforeEach(inject(function($rootScope, $controller){
        scope = $rootScope.$new();
        controller = $controller('FuelTableCtrl', {
          $scope : scope,
          GetLtlFuelSurchargeService : mockGetLtlFuelSurchargeService,
          LtlFuelSurchargeCopyFromService : null, 
          LtlFuelSurchargeSaveService : mockLtlFuelSurchargeSaveService,
          LtlFuelSurchargeUpdateStatusService : null,
          ExportService : null,
          ExportDataBuilder : null
          });
        scope.$digest();
    }));
     
     it('should save properly', function() {
       c_expect(controller).to.be.an('object');
       scope.saveFuelSurchargeList();
       c_expect(scope.validationPassed).to.equal(true);
     });
     
     it('should add new record properly', function() {
       c_expect(controller).to.be.an('object');
       c_expect(scope.fuelTableList.length).to.equal(3);
       scope.onNewRecord();
       c_expect(scope.fuelTableList.length).to.equal(4);
     });
     
     
 });