/**
 * Vendor bills list page objects.
 * @author Alexander Kirichenko
 */
angular.module('PageObjectsModule').factory('VendorBillsPageObject', [function () {
    return {
        list: {
            vendorBillControllerSelector: '[data-ng-controller="VendorBillController"]',
            unmatchedTabSelector: 'li:has(a:contains("Unmatched Vendor Bills"))',
            unmatchedLinkSelector: 'a:contains("Unmatched Vendor Bills")',
            archiveTabSelector: 'li:has(a:contains("Archived Vendor Bills"))',
            archiveLinkSelector: 'a:contains("Archived Vendor Bills")',
            unmatchedButtonsBlockSelector: 'a[href="#/vendorBill/unmatched"]',
            archivedButtonsBlockSelector: 'a[href="#/vendorBill/archived"]',
            searchSalesOrderBtn: '[data-ng-click="searchSalesOrder()"]',
            closeSalesOrderBtn: '[data-ng-click="closeSearchDialog()"]',
            archiveVendorBillBtn: '[data-ng-click="vendorBillsModel.confirmArchiveDialog.showDialog()"]',
            unArchiveVendorBillBtn: '[data-ng-click="changeState(\'unArchive\')"]',
            viewVendorBillBtn: '[data-ng-click="viewVendorBill()"]',
            createSalesOrderButton: '[data-ng-click="checkingMatchLoads()"]',
            viewVendorBillDialog: '[data-pls-modal="vendorBillsModel.vendorBillView.show"]',
            viewEditSalesOrderDialog: '[data-pls-modal="editSalesOrderModel.showEditOrderDialog"]',
            inputSearchByOrigin: 'input[ng-model="col.searchValue"]:eq(4)',
            customer: 'input[data-ng-model="selectedCustomer"]',
            origin: 'input[data-ng-model="plsZipSearch"]:visible:first',
            destination: 'input[data-ng-model="plsZipSearch"]:visible:last',
            bol: '#BOL',
            po: '#PO',
            pu: '#PU',
            shipperRef: '#SR',
            trailer: '#TR',
            so: '#SO',
            gl: '#GL',
            cargo: '#CARGO',
            requestedBy: '#RB',
            actualDeliveryDate: 'input[id="actualDeliveryDate"]',
            actualPickupDate: 'input[id="actualPickupDate"]',
            addCostDetailsBtn: '[data-ng-click="addCostDetails()"]',
            costDetailDescription: 'editedCostDetail.refType',
            revenue: 'input[id="revenue"]',
            cost: 'input[id="addCostDetailsCost"]',
            saveCostDetailsBtn: '[data-ng-click="saveCostDetails()"]',
            selectAddresses: '[data-ng-click="selectTab(\'addresses\')"]',
            selectDetails: '[data-ng-click="selectTab(\'details\')"]',
            locationInput: '#location :input',
            originAddressName: 'input[data-ng-model="selectedAddressName"]:visible:first',
            destinationAddressName: 'input[data-ng-model="selectedAddressName"]:visible:last',
            deliveryFromWindow: 'input[id="deliveryFromTime"]',
            deliveryToWindow: 'input[id="deliveryToTime"]',
            updateOrderBtn: '[data-ng-click="updateOrder()"]',
            weight: 'material.weight',
            gridColShipper: '[class="ngHeaderSortColumn  ngSorted"]div :contains("Shipper")',
            gridColConsignee: '[class="ngHeaderSortColumn  ngSorted"]div :contains("Consignee")',
            gridRow: '[ng-row]',
            gridFirstRow: '[ng-row]:first',
            gridLastRow: '[ng-row]:last',
            gridSecondRow: '[ng-row]:eq(2)',
            selectUnmatchedTab: function () {
                element(this.unmatchedLinkSelector).click();
            },
            selectArchiveTab: function () {
                element(this.archiveLinkSelector).click();
            },
            selectFirstRow: function () {
                element(this.gridFirstRow).click();
            },
            selectSecondRow: function () {
                element(this.gridSecondRow).click();
            },
            getSearchSalesOrderDisplay: function () {
                return element(this.searchSalesOrderBtn).attr("disabled");
            },
            closeSalesOrderDialog: function () {
                return element(this.closeSalesOrderBtn).click();
            },
            getArchiveVendorBillDisplay: function () {
                return element(this.archiveVendorBillBtn).attr("disabled");
            },
            getUnArchiveVendorBillDisplay: function () {
                return element(this.unArchiveVendorBillBtn).attr("disabled");
            },
            getViewVendorBillDisplay: function () {
                return element(this.viewVendorBillBtn).attr("disabled");
            },
            createSalesOrder: function () {
                element(this.createSalesOrderButton).click();
            },
            archiveSelectedVendorBill: function () {
                element(this.archiveVendorBillBtn).click();
            },
            viewSelectedVendorBill: function () {
                element(this.viewVendorBillBtn).click();
            },
            unArchiveSelectedVendorBill: function () {
                element(this.unArchiveVendorBillBtn).click();
            },
            showSearchSalesOrderTab: function () {
                return element(this.searchSalesOrderBtn).click();
            },
            getDialogDisplay: function () {
                return element(this.viewVendorBillDialog).css('display');
            },
            getEditSalesOrderDialogDisplay: function () {
                return element(this.viewEditSalesOrderDialog).css('display');
            },
            searchByOrigin: function (value) {
                setValue(this.inputSearchByOrigin, value);
            },
            setCustomer: function (value) {
                progressiveSearch(this.customer).enter(value);
                progressiveSearch(this.customer).select();
            },
            setOrigin: function (value) {
                progressiveSearch(this.origin).enter(value);
                progressiveSearch(this.origin).select();
            },
            setDestination: function (value) {
                progressiveSearch(this.destination).enter(value);
                progressiveSearch(this.destination).select();
            },
            setBOL: function (value) {
                setValue(this.bol, value);
            },
            setPo: function (value) {
                setValue(this.po, value);
            },
            setPu: function (value) {
                setValue(this.pu, value);
            },
            setShipperRef: function (value) {
                setValue(this.shipperRef, value);
            },
            setSO: function (value) {
                setValue(this.so, value);
            },
            setTrailer: function (value) {
                setValue(this.trailer, value);
            },
            setGL: function (value) {
                setValue(this.gl, value);
            },
            setCargo: function (value) {
                setValue(this.cargo, value);
            },
            setRequestedBy: function (value) {
                setValue(this.requestedBy, value);
            },
            setActualDeliveryDate: function (value) {
                setValue(this.actualDeliveryDate, value);
            },
            setActualPickupDate: function (value) {
                setValue(this.actualPickupDate, value);
            },
            selectProduct: function (productLabel) {
                productList('products.product').showList();
                productList('products.product').selectProduct(productLabel);
            },
            setWeight: function (value) {
                input(this.weight).enter(value);
            },
            addCostDetails: function () {
                element(this.addCostDetailsBtn).click();
            },
            setCostDetailDescription: function (value) {
                select(this.costDetailDescription).option(value);
            },
            setRevenue: function (value) {
                setValue(this.revenue, value);
            },
            setCost: function (value) {
                setValue(this.cost, value);
            },
            saveCostDetails: function () {
                element(this.saveCostDetailsBtn).click();
            },
            selectAddressesTab: function () {
                element(this.selectAddresses).click();
            },
            selectDetailsTab: function () {
                element(this.selectDetails).click();
            },
            setLocation: function (value) {
                setValue(this.locationInput, value);
            },
            setOriginAddressName: function (value) {
                setValue(this.originAddressName, value);
            },
            setDestinationAddressName: function (value) {
                setValue(this.destinationAddressName, value);
            },
            setDeliveryFromWindow: function (fromTime) {
                setValue(this.deliveryFromWindow, fromTime);
            },
            setDeliveryToWindow: function (toTime) {
                setValue(this.deliveryToWindow, toTime);
            },
            updateOrder: function () {
                element(this.updateOrderBtn).click();
            }
        },
        addEditDialog: {
            dialogSelector: '.a_add_edit_vendor_bill_dialog',
            saveVendorBillBtnSelector: '[data-ng-click="saveVendorBill()"]',
            amountField: 'addEditVendorBillModel.vendorBill.amount',
            originZipField: 'addEditVendorBillModel.vendorBill.originAddress.zip',
            originAddressField: 'addEditVendorBillModel.vendorBill.originAddress.address1',
            destinationZipField: 'addEditVendorBillModel.vendorBill.destinationAddress.zip',
            destinationAddressField: 'addEditVendorBillModel.vendorBill.destinationAddress.address1',
            proField: 'addEditVendorBillModel.vendorBill.pro',
            poField: 'addEditVendorBillModel.vendorBill.po',
            puField: 'addEditVendorBillModel.vendorBill.pu',
            quoteIdField: 'addEditVendorBillModel.vendorBill.quoteId',
            getSaveDisplay: function () {
                return element(this.saveVendorBillBtnSelector).attr("disabled");
            },
            saveVendorBill: function () {
                element(this.saveVendorBillBtnSelector).click();
            },
            getDialogDisplay: function () {
                return element(this.dialogSelector).css('display');
            },
            lineItemGroup: {
                descriptionField: 'lineItem.productDescription',
                quantityField: 'lineItem.quantity',
                packageTypeFiled: 'lineItem.packageType',
                addLineItemBtn: '[data-ng-click="addLineItem()"]',
                getAddLineItemDisplay: function () {
                    return element(this.addLineItemBtn).attr("disabled");
                }
            }
        },
        confirmArchiveDialog: {
            dialogSelector: '[data-pls-modal="vendorBillsModel.confirmArchiveDialog.show"]',
            getDialogDisplay: function () {
                return element(this.dialogSelector).css('display');
            },
            selectReason: function() {
                select('vendorBillsModel.confirmArchiveDialog.reason').option('Already Invoiced');
            },
            confirmArchiveVendorBill: function () {
                element('[data-ng-click="vendorBillsModel.confirmArchiveDialog.ok()"]').click();
            }
        },
        salesOrdersPage: {
            controller: 'div[data-ng-controller="SalesOrderForVendorBillController"]',
            clearSalesOrderBtn: '[data-ng-click="clearSalesOrders()"]',
            closeSalesOrderBtn: '[data-ng-click="closeSearchDialog()"]',
            closeEditSalesOrderBtn: '.a_cancelButton',
            origin : 'input[data-ng-model="plsZipSearch"]:visible:first',
            destination : 'input[data-ng-model="plsZipSearch"]:visible:last',
            bol: '#bolZip',
            pro: '#proZip',
            inputBol: 'searchSalesOrderModel.search.bol',
            inputPro: 'searchSalesOrderModel.search.pro',
            clearSalesOrderField: function () {
                return element(this.clearSalesOrderBtn).click();
            },
            setBol: function (value) {
                input(this.inputBol).enter(value);
            },
            setPro: function (value) {
                input(this.inputPro).enter(value);
            },
            setOrigin: function (value) {
                progressiveSearch(this.origin).enter(value);
                progressiveSearch(this.origin).select();
            },
            setDestination: function (value) {
                progressiveSearch(this.destination).enter(value);
                progressiveSearch(this.destination).select();
            },
            closeSalesOrderPage: function () {
                return element(this.closeSalesOrderBtn).click();
            },
            closeEditSalesOrderPage: function () {
                return element(this.closeEditSalesOrderBtn).click();
            }
        }
    };
}]);
