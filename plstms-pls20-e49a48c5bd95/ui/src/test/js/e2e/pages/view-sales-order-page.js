/**
 * @author Sergii Belodon
 */
angular.module('PageObjectsModule').factory('AuditTabObject', [function() {
    return {
        auditTabLink : '[data-ng-click="selectTab(\'audit\')"]',
        active: {
            costDetailsGrid:'[data-ng-grid = "costDetailsGrid"]',
            vendorBillGrid:'[data-ng-grid = "vendorBillGrid"]',
            infoTableGrid:'[data-ng-grid = "infoTableGrid"]',
            accessorialsGrid:'[data-ng-grid = "accessorialsGrid"]'
        }
    };
}]);

angular.module('PageObjectsModule').factory('DocsTabObject', [function() {
    return {
        docsTabLink: '[data-ng-click="selectTab(\'docs\')"]',
        regenerateConsigneeInvoiceButton: '[data-ng-click="regenerateConsigneeInvoice()"]',

        clickTab: function() {
            element(this.docsTabLink).click();
        }
    }
}]);