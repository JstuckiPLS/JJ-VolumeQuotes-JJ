angular.module('plsApp').controller('CustomerP44ConfigController', [
    '$scope', '$location', '$route', 'CustomerP44ConfigService', 'NgGridPluginFactory',
    function ($scope, $location, $route, CustomerP44ConfigService, NgGridPluginFactory) {
        'use strict';

        var customerId = $route.current.params.customerId;
        
        $scope.availableAccountGroups = null;
        var savedP44Config = {
                customerName: "", 
                accountGroupCode: "",
                accounts: [] // stores array of accounts ({accountCode; carrierCode})
        };
        
        $scope.custP44Config = {
                customerName: "",
                accountGroupCode: "",
                accounts: [] // stores array of accounts ({accountCode; carrierCode})
        };
        
        $scope.selectedAccountGroupAccounts = []; // stores array of objects {scac : string, accounts : [{accountCode;carrierCode}] }
        
        function setAvailableAccountGroups(data) {
           $scope.availableAccountGroups = data;
           $scope.accountGroupChanged();
        }
        
        function setCustomerP44Config(data) {
            // store saved values in a separate var for later reuse (when switching back to that group)
            savedP44Config = data;
        
            // switch to that account group based on stored data
            $scope.custP44Config.customerName = savedP44Config.customerName;
            $scope.custP44Config.accountGroupCode = savedP44Config.accountGroupCode;
        
            $scope.accountGroupChanged();
        }
        
        CustomerP44ConfigService.list({
        }, setAvailableAccountGroups);
        
        CustomerP44ConfigService.get({
            id: customerId
        }, setCustomerP44Config);
        
        $scope.accountGroupChanged = function () {
            var scacToAccountsList = [];
            var selectedAccountGroup = null;
            var i;
            
            if($scope.availableAccountGroups===null) {
                return;
            }
            
            // create scac to accounts mapping
            selectedAccountGroup = $scope.availableAccountGroups.find(
                    function(item) { 
                        return item.accountGroupCode === $scope.custP44Config.accountGroupCode; 
                        }
                    );
            
            function findScacToAccountsMapping(carrierCode){
                return function(item){return item.scac === carrierCode;};
            }
            
            if(!!selectedAccountGroup){
                for(i in selectedAccountGroup.accounts){
                    var accGrpAccount = selectedAccountGroup.accounts[i];
                    var mapping = scacToAccountsList.find(findScacToAccountsMapping(accGrpAccount.carrierCode));
                    
                    if(!mapping) {
                        mapping = {scac: accGrpAccount.carrierCode, accounts: []};
                        scacToAccountsList.push(mapping);
                    }
                    mapping.accounts.push(accGrpAccount);
                }
            }
            
            $scope.selectedAccountGroupAccounts = scacToAccountsList;
            
            function findMappingIncludesAccount(accounts){
                return function(item) {
                    var i;
                    for(i in accounts){
                        var acc = accounts[i];
                        if(acc.accountCode === item.accountCode){
                            return true;
                        }
                    }
                    return false;
               };
            }
            
            // preselect saved values (if on the same group as the saved one)
            if($scope.custP44Config.accountGroupCode === savedP44Config.accountGroupCode){
                var accountMapping = [];
                for(i in scacToAccountsList){
                    var accounts = scacToAccountsList[i].accounts;
                    var account = accounts.find(findMappingIncludesAccount(savedP44Config.accounts));
                    accountMapping.push(!!account ? account : '');
                }
                $scope.custP44Config.accounts = accountMapping;
            } else {
                $scope.custP44Config.accounts = new Array(scacToAccountsList.length); // empty config if switching to different account group
            }

        };
        
        $scope.backToCustomerScreen = function () {
            $location.url('/pricing/customer/');
        };
        
        $scope.submit = function() {
        
            savedP44Config.accountGroupCode = $scope.custP44Config.accountGroupCode;
            // filter out empty strings
            savedP44Config.accounts = $scope.custP44Config.accounts.filter(function (item){
                    return !!item;
                }
            ); 
        
            var success = function (response) {
                $scope.$root.$emit('event:operation-success', 'Success saving customer P44 Account configuration');
                //$scope.backToCustomerScreen();
            };

            var failure = function (response) {
                $scope.$root.$emit('event:application-error', 'Failed saving customer P44 Account configuration!');
            };
            
            savedP44Config.$save({
                id: customerId
            }, success, failure);
        };
    }
]);
