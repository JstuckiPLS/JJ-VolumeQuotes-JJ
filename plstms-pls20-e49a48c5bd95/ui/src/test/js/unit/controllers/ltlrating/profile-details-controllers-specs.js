/**
 * Tests ProfileDetailsCtrl controller.
 * 
 * @author Sergii Belodon
 */
describe('ProfileDetailsCtrl controller test', function() {
  // Angular scope
  var scope = undefined;
  
  // FuelTableCtrl controller
  var controller = undefined;
  beforeEach(module('plsApp'));
  
  var mockProfileDictionaryService = {
    get : function(sucess){
      
    }
  }
  
  beforeEach(inject(function($rootScope, $controller){
     scope = $rootScope.$new();
     controller = $controller('ProfileDetailsCtrl', {
       $scope : scope,
       $route: null, 
       ProfileDictionaryService: mockProfileDictionaryService, 
       SaveCopiedProfileService: null,
       ProfileDetailsService: null, 
       ProfileSelectedCarrierAPIService: null, 
       $routeParams: null, 
       $location: null
       });
     scope.$digest();
 }));
  it('should save properly', function() {
    c_expect(controller).to.be.an('object');
  });
});