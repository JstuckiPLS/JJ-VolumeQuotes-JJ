/**
 * This file is ignored because it causes unit tests to fail with error: ERROR [launcher]: PhantomJS crashed.
 * Problem might be related to following line: scope = element.isolateScope();
 * FIXME: issue should be resolved by Dmitriy Nefedchenko
 */
xdescribe('Unit test for quote products directive', function() {
    var element;
    var scope;
    var productService;
    var customerOrderService;
    var templateCache;
    var pluginFactory;
    var filter;
    var httpBackend;
    var compile;

    var shipment = {
        originDetails: {
            accessorials: []
        },
        destinationDetails: {
            accessorials: []
        },
        finishOrder:{
            quoteMaterials:[],
            pickupDate : ''
        },
        status : 'OPEN'
    };

    var dictionary = {
        classes: [],
        dimensions: [],
        notificationTypes: [],
        packageTypes: [],
        paymentTerms: [],
        weights: []
    };

    var selectedCustomer = {
        id: 1,
        name: 'Mr-Too-Good-To-Call-Or-Write-His-Fans'
    };

    var emptyMaterial = { hazmat:false, stackable:false, weightUnit:'LBS', dimensionUnit:'INCH',
            weight:'', length:'', width:'', height:'', quantity:'1' };

    var findFormInputs = function() {
        return element.find('input');
    };

    var findFormButtons = function() {
        return element.find('button.btn');
    };

    var formInputs;
    var formButtons;

    beforeEach(module('plsApp'));
    beforeEach(module('pages/tpl/products-data-tpl.html', 
            'pages/tpl/product-list-tpl.html',
            'pages/tpl/class-estimator-tpl.html',
            'pages/tpl/pls-typeahead-tpl.html'
            ));

    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();

        productService = $injector.get('ProductService');
        customerOrderService = $injector.get('CustomerOrderService');
        httpBackend = $injector.get('$httpBackend');
        httpBackend.when('GET', '/restful/customer/1/product-list-sort').respond('');
        httpBackend.when('GET', '/restful/customer/3/product-list-sort').respond('');
        httpBackend.when('GET', '/restful/shipment/dictionary/packageType').respond('');
        httpBackend.when('GET', '/restful/dictionary/notificationTypes').respond('');
        httpBackend.when('GET', '/restful/shipment/dictionary/billToReqField').respond('');

        var compile = $injector.get('$compile');

        var htmlSnippet = '<div data-pls-quote-products="shipment" data-rate-quote-dictionary="rateQuoteDictionary" data-selected-customer="selectedCustomer" data-quote-processing="quoteProcessing"></div>';
        element = compile(htmlSnippet)(rootScope.$new());
        scope = element.isolateScope();
        scope.shipment = shipment;
        scope.rateQuoteDictionary = dictionary;
        scope.selectedCustomer = selectedCustomer;
        scope.$digest();

        formInputs = findFormInputs();
        formButtons = findFormButtons();
    }));

    it('should check out initial model state', function() {
        expect(scope.shipment).toEqual(shipment);
        expect(scope.rateQuoteDictionary).toEqual(dictionary);
        expect(scope.selectedCustomer).toEqual(selectedCustomer);
        expect(scope.quoteProcessing).toBeDefined();
        expect(scope.parentDialog).toBeUndefined();
        expect(scope.hideControls).toBeFalsy();
        expect(scope.qtyRequired).toBeFalsy();
        expect(scope.dimensionsRequired).toBeFalsy();
        expect(scope.showLargeLoadMessage).toBeFalsy();
        expect(scope.renderGrid).toBeUndefined();

        expect(scope.emptyMaterial).toEqual(emptyMaterial);
        expect(scope.products).toEqual({});
        expect(scope.materialColumnModel).toBeDefined();
        expect(scope.hazmatInfo).toEqual({'exist': false});
        expect(scope.maxCountOfProducts).toBe(100);
    });

    it('should check materials grid', function() {
        expect(scope.materialsGrid).toBeDefined();
        expect(scope.materialsGrid.enableColumnResize).toBeTruthy();
        expect(scope.materialsGrid.data).toEqual('shipment.finishOrder.quoteMaterials');
        expect(scope.materialsGrid.multiSelect).toBeFalsy();
        expect(scope.materialsGrid.selectedItems).toEqual([]);
        expect(scope.materialsGrid.columnDefs).toEqual('materialColumnModel');
        expect(scope.materialsGrid.plugins.length).toBe(2);
        expect(scope.materialsGrid.tooltipOptions.showIf).toEqual(jasmine.any(Function));
        expect(scope.materialsGrid.tooltipOptions.onShow).toEqual(jasmine.any(Function));
        expect(scope.materialsGrid.enableSorting).toBeFalsy();
        expect(scope.materialsGrid.progressiveSearch).toBeFalsy();
        expect(scope.materialsGrid.tabIndex).toBe(-10);
    });

    it('should check material column model', function() {
        expect(scope.materialColumnModel).toBeDefined();
        expect(scope.materialColumnModel.length).toBe(12);

        expect(scope.materialColumnModel[0].field).toEqual('self');
        expect(scope.materialColumnModel[0].referenceId).toEqual('weightColumn');
        expect(scope.materialColumnModel[0].displayName).toEqual('Weight');
        expect(scope.materialColumnModel[0].width).toEqual('7%');
        expect(scope.materialColumnModel[0].cellFilter).toEqual('materialWeight');

        expect(scope.materialColumnModel[1].field).toEqual('commodityClass');
        expect(scope.materialColumnModel[1].referenceId).toEqual('classColumn');
        expect(scope.materialColumnModel[1].displayName).toEqual('Class');
        expect(scope.materialColumnModel[1].width).toEqual('6%');
        expect(scope.materialColumnModel[1].cellFilter).toEqual('commodityClass');

        expect(scope.materialColumnModel[2].field).toEqual('productDescription');
        expect(scope.materialColumnModel[2].referenceId).toEqual('productColumn');
        expect(scope.materialColumnModel[2].displayName).toEqual('Product Description');
        expect(scope.materialColumnModel[2].width).toEqual('12%');

        expect(scope.materialColumnModel[3].field).toEqual('productCode');
        expect(scope.materialColumnModel[3].referenceId).toEqual('productCodeColumn');
        expect(scope.materialColumnModel[3].displayName).toEqual('SKU/Product Code');
        expect(scope.materialColumnModel[3].width).toEqual('14%');

        expect(scope.materialColumnModel[4].field).toEqual('nmfc');
        expect(scope.materialColumnModel[4].referenceId).toEqual('nmfcColumn');
        expect(scope.materialColumnModel[4].displayName).toEqual('NMFC');
        expect(scope.materialColumnModel[4].width).toEqual('7%');

        expect(scope.materialColumnModel[5].field).toEqual('self');
        expect(scope.materialColumnModel[5].referenceId).toEqual('dimensionsColumn');
        expect(scope.materialColumnModel[5].displayName).toEqual('Dimensions');
        expect(scope.materialColumnModel[5].width).toEqual('10%');

        expect(scope.materialColumnModel[6].field).toEqual('quantity');
        expect(scope.materialColumnModel[6].referenceId).toEqual('quantityColumn');
        expect(scope.materialColumnModel[6].displayName).toEqual('Qty');
        expect(scope.materialColumnModel[6].width).toEqual('4%');

        expect(scope.materialColumnModel[7].field).toEqual('packageType');
        expect(scope.materialColumnModel[7].referenceId).toEqual('packageTypeColumn');
        expect(scope.materialColumnModel[7].displayName).toEqual('Packaging Type');
        expect(scope.materialColumnModel[7].width).toEqual('10%');

        expect(scope.materialColumnModel[8].field).toEqual('pieces');
        expect(scope.materialColumnModel[8].referenceId).toEqual('piecesColumn');
        expect(scope.materialColumnModel[8].displayName).toEqual('Pieces');
        expect(scope.materialColumnModel[8].width).toEqual('6%');

        expect(scope.materialColumnModel[9].field).toEqual('stackable');
        expect(scope.materialColumnModel[9].referenceId).toEqual('stackableColumn');
        expect(scope.materialColumnModel[9].displayName).toEqual('Stackable');
        expect(scope.materialColumnModel[9].width).toEqual('8%');
        expect(scope.materialColumnModel[9].cellTemplate)
            .toEqual('<div class="ngSelectionCell text-center"><input tabindex="-1" class="ngSelectionCheckbox" ' +
                    'type="checkbox" data-ng-disabled="true" data-ng-checked="row.entity.stackable"/></div>');

        expect(scope.materialColumnModel[11].field).toEqual('hazmatClass');
        expect(scope.materialColumnModel[11].referenceId).toEqual('hazmatClassColumn');
        expect(scope.materialColumnModel[11].displayName).toEqual('Hazmat Class');
        expect(scope.materialColumnModel[11].cellClass).toEqual('text-center');
        expect(scope.materialColumnModel[11].width).toEqual('8%');

        expect(scope.materialColumnModel[11].field).toEqual('hazmatClass');
        expect(scope.materialColumnModel[10].referenceId).toEqual('hazmatColumn');
        expect(scope.materialColumnModel[10].displayName).toEqual('Hazmat');
        expect(scope.materialColumnModel[10].showTooltip).toBeTruthy();
        expect(scope.materialColumnModel[10].width).toEqual('7%');
    });

    it('should check all buttons are in place', function() {
        expect(formButtons.length).toBe(7);

        angular.forEach(formButtons, function(button) {
            expect(angular.element(button).hasClass('btn')).toBeTruthy();
        });

        expect(formButtons.eq(0)).toHaveClass('span3');
        expect(formButtons.eq(0)).toHaveClass('nowrap');
        expect(formButtons.eq(0)).toHaveClass('ng-pristine');
        expect(formButtons.eq(0)).toHaveClass('ng-valid');
        expect(formButtons.eq(0).find('i.icon-list')).toBeDefined();
        expect(formButtons.eq(0)).toHaveAttr('data-ng-click', 'open()');
        expect(formButtons.eq(0)).toHaveAttr('data-pls-class-estimator');
        expect(formButtons.eq(0)).toHaveAttr('data-ng-model', 'material.commodityClass');

        expect(formButtons.eq(1)).toHaveClass('span12');
        expect(formButtons.eq(1).find('i.icon-arrow-down')).toBeDefined();
        expect(formButtons.eq(1)).toHaveAttr('data-ng-click', 'showTypeaheadList();');
        expect(formButtons.eq(1)).toHaveAttr('data-ng-disabled', '!customerId');

        expect(formButtons.eq(2)).toHaveClass('a_addProductButton');
        expect(formButtons.eq(2)).toHaveAttr('data-ng-disabled', '!selectedCustomer.id');
        expect(formButtons.eq(2)).toHaveAttr('data-ng-click', 'openAddProductDialog()');
        expect(formButtons.eq(2)).toHaveText('+');

        expect(formButtons.eq(3)).toHaveClass('span2');
        expect(formButtons.eq(3)).toHaveClass('pull-left');
        expect(formButtons.eq(3)).toHaveClass('a_addItemButton');
        expect(formButtons.eq(3).find('i.icon-arrow-down')).toBeDefined();
        expect(formButtons.eq(3)).toHaveAttr('data-ng-disabled', 
                'addProductForm.$invalid || shipment.finishOrder.quoteMaterials.length >= maxCountOfProducts');
        expect(formButtons.eq(3)).toHaveAttr('data-ng-click', 'addProduct()');
        expect(formButtons.eq(3)).toHaveAttr('disabled', 'disabled');

        expect(formButtons.eq(4)).toHaveClass('span2');
        expect(formButtons.eq(4)).toHaveClass('pull-left');
        expect(formButtons.eq(4)).toHaveClass('a_editButton');
        expect(formButtons.eq(4)).toHaveAttr('data-ng-disabled', 'getSelectedMaterialRowNum() < 0');
        expect(formButtons.eq(4)).toHaveAttr('data-ng-click', 'editProduct()');
        expect(formButtons.eq(4)).toHaveAttr('disabled', 'disabled');
        expect(formButtons.eq(4)).toHaveText('Edit');

        expect(formButtons.eq(5)).toHaveClass('span2');
        expect(formButtons.eq(5)).toHaveClass('pull-left');
        expect(formButtons.eq(5)).toHaveClass('a_removeButton');
        expect(formButtons.eq(5)).toHaveAttr('data-ng-disabled', 'getSelectedMaterialRowNum() < 0');
        expect(formButtons.eq(5)).toHaveAttr('data-ng-click', 'removeProduct()');
        expect(formButtons.eq(5)).toHaveAttr('disabled', 'disabled');
        expect(formButtons.eq(5)).toHaveText('Remove');

        expect(formButtons.eq(6)).toHaveClass('span2');
        expect(formButtons.eq(6)).toHaveClass('btn-primary');
        expect(formButtons.eq(6)).toHaveClass('pull-right');
        expect(formButtons.eq(6)).toHaveClass('a_getQuoteButton');
        expect(formButtons.eq(6)).toHaveAttr('data-ng-show', 'isProcessingRequired()');
        expect(formButtons.eq(6)).toHaveAttr('data-ng-click', 'getQuote()');
        expect(formButtons.eq(6)).toHaveText('Get Quote');
    });

    it('should check all labels are in place', function() {
        var labels = element.find('label.control-label');
        expect(labels.length).toBe(10);

        expect(labels.eq(0)).toHaveText('Weight:');
        expect(labels.eq(1)).toHaveText('Class:');
        expect(labels.eq(2)).toHaveText('Product:');
        expect(labels.eq(3)).toHaveText('Hazardous');
        expect(labels.eq(4)).toHaveText('');
        expect(labels.eq(5)).toHaveText('Dimensions:');
        expect(labels.eq(6)).toHaveText('Qty:');
        expect(labels.eq(7)).toHaveText('Pack. Type:');
        expect(labels.eq(8)).toHaveText('Stackable');
        expect(labels.eq(9)).toHaveText('Pcs:');
    });

    it('should check material grid is in place', function() {
        var materialGrid = element.find('div.gridStyle');

        expect(materialGrid).toBeDefined();
        expect(materialGrid).toHaveClass('span12');
        expect(materialGrid).toHaveClass('gridHeight1_8');
        expect(materialGrid).toHaveAttr('data-ng-grid', 'materialsGrid');
        expect(materialGrid).toHaveAttr('data-ng-if', 'renderGrid !== false');
        expect(materialGrid).toHaveAttr('data-ng-model', 'shipment.finishOrder.quoteMaterials');
    });

    it('should check all drop-downs are in place', function() {
        var dropdowns = element.find('select');

        expect(dropdowns.eq(0)).toHaveClass('span6 a_weightMeasure ng-pristine ng-valid');
        expect(dropdowns.eq(0)).toHaveAttr('data-ng-model', 'material.weightUnit');
        expect(dropdowns.eq(0)).toHaveAttr('data-ng-model', 'material.weightUnit');
        expect(dropdowns.eq(0)).toHaveAttr('data-ng-disabled', 'true');
        expect(dropdowns.eq(0)).toHaveAttr('disabled', 'disabled');

        expect(dropdowns.eq(1)).toHaveClass('span12 a_commodityClass ng-pristine ng-invalid ng-invalid-required');
        expect(dropdowns.eq(1)).toHaveAttr('data-ng-model', 'material.commodityClass');
        expect(dropdowns.eq(1)).toHaveAttr('tabindex', '4');
        expect(dropdowns.eq(1)).toHaveAttr('data-ng-init', 'material.commodityClass');
        expect(dropdowns.eq(1)).toHaveAttr('data-ng-change', 'cleanProduct()');

        expect(dropdowns.eq(2)).toHaveClass('span3 a_dimensionsMeasure ng-pristine ng-valid');
        expect(dropdowns.eq(2)).toHaveAttr('data-ng-disabled', 'true');
        expect(dropdowns.eq(2)).toHaveAttr('data-ng-model', 'material.dimensionUnit');
        expect(dropdowns.eq(2)).toHaveAttr('disabled', 'disabled');

        expect(dropdowns.eq(3)).toHaveClass('span11 a_packageType ng-pristine ng-valid ng-valid-required');
        expect(dropdowns.eq(3)).toHaveAttr('data-ng-required', 'material.productId || qtyRequired');
        expect(dropdowns.eq(3)).toHaveAttr('data-ng-model', 'material.packageType');
        expect(dropdowns.eq(3)).toHaveAttr('data-ng-init', 'material.packageType');
    });

    it('should check "weight" input attributes and css styling', function() {
        expect(formInputs.eq(0)).toHaveAttr('type', 'text');
        expect(formInputs.eq(0)).toHaveAttr('data-pls-number');
        expect(formInputs.eq(0)).toHaveAttr('data-integral', '8');
        expect(formInputs.eq(0)).toHaveAttr('data-fractional', '2');
        expect(formInputs.eq(0)).toHaveAttr('data-forbid-zero', 'true');
        expect(formInputs.eq(0)).toHaveAttr('data-ng-model', 'material.weight');
        expect(formInputs.eq(0)).toHaveAttr('maxLength', '11');
        expect(formInputs.eq(0)).toHaveAttr('tabIndex', '3');
        expect(formInputs.eq(0)).toHaveClass('span6 a_weight ng-pristine ng-invalid ng-invalid-required ng-valid-format');
    });

    it('should check "hazmat" input attributes and css styling', function() {
        expect(formInputs.eq(1)).toHaveAttr('type', 'checkbox');
        expect(formInputs.eq(1)).toHaveAttr('data-ng-model', 'products.hazmatOnly');
        expect(formInputs.eq(1)).toHaveAttr('data-ng-init', 'products.hazmatOnly');
        expect(formInputs.eq(1)).toHaveAttr('data-ng-change', 'cleanProduct()');
        expect(formInputs.eq(1)).toHaveClass('a_onlyHazmat ng-pristine ng-valid');
        
    });

    it('should check "product list" input attributes and css styling', function() {
        expect(formInputs.eq(2)).toHaveAttr('type', 'text');
        expect(formInputs.eq(2)).toHaveAttr('data-ng-required', 'mandatory');
        expect(formInputs.eq(2)).toHaveAttr('data-ng-model', 'selectedProduct');
        expect(formInputs.eq(2)).toHaveAttr('data-ng-disabled', '!customerId');
        expect(formInputs.eq(2)).toHaveAttr('data-typeahead-min-length', '2');
        expect(formInputs.eq(2)).toHaveAttr('data-pls-typeahead', 'product as product.label for product in findProducts(customerId, $viewValue, commodityClass, hazmatOnly)');
        expect(formInputs.eq(2)).toHaveClass('span10 ng-pristine ng-valid ng-valid-required');
    });

    it('should check "length" input attributes and css styling', function() {
        expect(formInputs.eq(3)).toHaveAttr('type', 'text');
        expect(formInputs.eq(3)).toHaveAttr('data-ng-required', 'dimensionsRequired');
        expect(formInputs.eq(3)).toHaveAttr('data-ng-model', 'material.length');
        expect(formInputs.eq(3)).toHaveAttr('data-ng-init', 'material.length');
        expect(formInputs.eq(3)).toHaveAttr('placeholder', 'LLL');
        expect(formInputs.eq(3)).toHaveAttr('data-pls-number');
        expect(formInputs.eq(3)).toHaveAttr('data-integral', '8');
        expect(formInputs.eq(3)).toHaveAttr('data-fractional', '2');
        expect(formInputs.eq(3)).toHaveAttr('data-forbid-zero', 'true');
        expect(formInputs.eq(3)).toHaveAttr('maxLength', '11');
        expect(formInputs.eq(3)).toHaveClass('span3 a_length ng-pristine ng-valid-format ng-valid ng-valid-required');
    });

    it('should check "width" input attributes and css styling', function() {
        expect(formInputs.eq(4)).toHaveAttr('type', 'text');
        expect(formInputs.eq(4)).toHaveAttr('data-ng-required', 'dimensionsRequired');
        expect(formInputs.eq(4)).toHaveAttr('data-ng-model', 'material.width');
        expect(formInputs.eq(4)).toHaveAttr('data-ng-init', 'material.width');
        expect(formInputs.eq(4)).toHaveAttr('placeholder', 'WWW');
        expect(formInputs.eq(4)).toHaveAttr('data-pls-number');
        expect(formInputs.eq(4)).toHaveAttr('data-integral', '8');
        expect(formInputs.eq(4)).toHaveAttr('data-fractional', '2');
        expect(formInputs.eq(4)).toHaveAttr('data-forbid-zero', 'true');
        expect(formInputs.eq(4)).toHaveAttr('maxLength', '11');
        expect(formInputs.eq(4)).toHaveClass('span3 a_width ng-pristine ng-valid-format ng-valid ng-valid-required');
    });

    it('should check "heigth" input attributes and css styling', function() {
        expect(formInputs.eq(5)).toHaveAttr('type', 'text');
        expect(formInputs.eq(5)).toHaveAttr('data-ng-required', 'dimensionsRequired');
        expect(formInputs.eq(5)).toHaveAttr('data-ng-model', 'material.height');
        expect(formInputs.eq(5)).toHaveAttr('data-ng-init', 'material.height');
        expect(formInputs.eq(5)).toHaveAttr('placeholder', 'HHH');
        expect(formInputs.eq(5)).toHaveAttr('data-pls-number');
        expect(formInputs.eq(5)).toHaveAttr('data-integral', '8');
        expect(formInputs.eq(5)).toHaveAttr('data-fractional', '2');
        expect(formInputs.eq(5)).toHaveAttr('data-forbid-zero', 'true');
        expect(formInputs.eq(5)).toHaveAttr('maxLength', '11');
        expect(formInputs.eq(5)).toHaveClass('span3 a_height ng-pristine ng-valid-format ng-valid ng-valid-required');
    });

    it('should check "quantity" input attributes and css styling', function() {
        expect(formInputs.eq(6)).toHaveAttr('type', 'text');
        expect(formInputs.eq(6)).toHaveAttr('data-pls-number');
        expect(formInputs.eq(6)).toHaveAttr('data-forbid-zero', 'true');
        expect(formInputs.eq(6)).toHaveAttr('data-ng-model', 'material.quantity');
        expect(formInputs.eq(6)).toHaveAttr('required');
        expect(formInputs.eq(6)).toHaveAttr('data-ng-pattern');
        expect(formInputs.eq(6)).toHaveAttr('maxLength', '6');
        expect(formInputs.eq(6)).toHaveClass('span8 a_qty ng-pristine ng-valid-pattern ng-valid-format ng-valid ng-valid-required');
    });

    it('should check "stackable" input attributes and css styling', function() {
        expect(formInputs.eq(7)).toHaveAttr('type', 'checkbox');
        expect(formInputs.eq(7)).toHaveAttr('data-ng-model', 'material.stackable');
        expect(formInputs.eq(7)).toHaveAttr('data-ng-init', 'material.stackable');
        expect(formInputs.eq(7)).toHaveClass('a_stackable ng-pristine ng-valid');
    });

    it('should check "pieces" input attributes and css styling', function() {
        expect(formInputs.eq(8)).toHaveAttr('type', 'text');
        expect(formInputs.eq(8)).toHaveAttr('data-ng-model', 'material.pieces');
        expect(formInputs.eq(8)).toHaveClass('span8 ng-pristine ng-valid ng-valid-format');
    });

    it('should check "search" placeholder attributes and css styling', function() {
        expect(formInputs.eq(9)).toHaveAttr('type', 'text');
        expect(formInputs.eq(9)).toHaveAttr('placeholder', 'Search...');
        expect(formInputs.eq(9)).toHaveAttr('ng-model', 'filterText');
        expect(formInputs.eq(8)).toHaveClass('ng-pristine ng-valid');
    });

    it('should check "pinned" checkbox attributes and css styling', function() {
        expect(formInputs.eq(10)).toHaveAttr('type', 'checkbox');
        expect(formInputs.eq(10)).toHaveAttr('ng-model', 'col.visible');
        expect(formInputs.eq(10)).toHaveClass('ngColListCheckbox ng-pristine ng-valid');
    });

    it('should not add product if grid contains more than seven items', function() {
        scope.addProductForm = {
            $valid: true
        };
        for (var i = 0; i < 105; i++) {
            scope.shipment.finishOrder.quoteMaterials.push({});
        }
        spyOn(scope.$root, '$emit').and.callThrough();

        var added = scope.addProduct();

        expect(scope.$root.$emit).toHaveBeenCalledWith('event:application-error', 'Product Grid size exceeded!',
                'Product Grid should contain not more than 100 products.');
        expect(added).toBeFalsy();
    });

    it('should add product', function() {
        scope.shipment.finishOrder.quoteMaterials = [];
        scope.addProductForm = {
            $valid: true
        };
        var material = {
            hazmat : false,
            stackable : true,
            weightUnit : 'LBS',
            dimensionUnit : 'INCH',
            weight : 123,
            length : 1,
            width : 2,
            height : 3,
            quantity : 5,
            product : {id: 1},
            productId: 321
        };
        scope.material = angular.copy(material);
        spyOn(scope.$root, '$broadcast').and.callThrough();
        expect(scope.shipment.finishOrder.quoteMaterials.length).toBe(0);

        var added = scope.addProduct();

        expect(scope.material.hazmat).toBeFalsy();
        expect(scope.material.quantity).toEqual('1');
        expect(scope.material.self).toBeUndefined();
        expect(scope.shipment.finishOrder.quoteMaterials.length).toBe(1);
        delete material.product; // should delete product field
        expect(scope.shipment.finishOrder.quoteMaterials[0]).toEqual(material);
        expect(scope.material).toEqual(emptyMaterial);
        expect(scope.products).toEqual({});
        expect(added).toBeTruthy();
        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:productChanged');
    });

    it('should edit a product', function() {
        scope.materialsGrid.selectedItems.push(scope.shipment.finishOrder.quoteMaterials[0]);
        spyOn(scope, 'removeProduct');

        scope.editProduct();

        expect(scope.material.weightUnit).toEqual('LBS');
        expect(scope.material.dimensionUnit).toEqual('INCH');
        expect(scope.products.hazmatOnly).toBeFalsy();
        expect(scope.removeProduct).toHaveBeenCalled();
    });

    it('should remove product', function() {
        scope.materialsGrid.selectedItems.push(scope.shipment.finishOrder.quoteMaterials[0]);
        spyOn(scope.$root, '$broadcast');

        expect(scope.shipment.finishOrder.quoteMaterials.length).toBe(1);

        scope.removeProduct();

        expect(scope.shipment.finishOrder.quoteMaterials.length).toBe(0);
        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:productChanged');
    });

    it('should get selected material row number', function() {
        var selectedRow = scope.getSelectedMaterialRowNum();

        expect(selectedRow).toBe(-1);

        scope.shipment.finishOrder.quoteMaterials.push({});
        scope.materialsGrid.selectedItems.push(scope.shipment.finishOrder.quoteMaterials[0]);
        selectedRow = scope.getSelectedMaterialRowNum();

        expect(selectedRow).toBe(0);
    });

    it('should open add product dialog', function() {
        var buttons = element.find('button.btn');
        spyOn(scope.$root, '$broadcast').and.callThrough();

        buttons.eq(2).click();

        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:showAddEditProduct', jasmine.any(Object));
    });

    it('should get quote', function() {
        spyOn(scope, 'addProduct').and.callThrough();

        scope.getQuote();

        expect(scope.addProduct).toHaveBeenCalled();
    });

    it('should prove that processing required', function() {
        expect(scope.isProcessingRequired()).toBeFalsy();
        scope.quoteProcessing = function() {
            return function() {};
        };
        scope.$digest();
        expect(scope.isProcessingRequired()).toBeTruthy();
    });

    it('should add new product', function() {
        spyOn(productService, 'info');
        scope.$broadcast('event:newProductAdded', 1);

        expect(productService.info).toHaveBeenCalledWith({productId: 1, customerId: 1}, jasmine.any(Function));
    });

    it('should trigger products watch and set up a product', function() {
        scope.products = {
            product: {
                value: 3
            }
        };
        httpBackend.when('GET', '/restful/customer/1/product/3').respond('');
        spyOn(productService, 'info');
        scope.$digest();

        expect(productService.info).toHaveBeenCalledWith({productId: 3, customerId: 1}, jasmine.any(Function));
    });

    it('should trigger products watch and clean up a product', function() {
        scope.products.product = undefined;
        spyOn(productService, 'info');
        scope.$digest();

        expect(productService.info).not.toHaveBeenCalled();
        expect(scope.material.productId).toBeUndefined();
        expect(scope.material.productCode).toBeUndefined();
        expect(scope.material.productDescription).toBeUndefined();
        expect(scope.material.hazmat).toBeFalsy();
        expect(scope.material.hazmatClass).toBeUndefined();
        expect(scope.material.unNum).toBeUndefined();
        expect(scope.material.nmfc).toBeUndefined();
        expect(scope.material.emergencyResponseCompany).toBeUndefined();
        expect(scope.material.emergencyContractNumber).toBeUndefined();
        expect(scope.material.emergencyResponseInstructions).toBeUndefined();
        expect(scope.material.emergencyResponsePhoneCountryCode).toBeUndefined();
        expect(scope.material.emergencyResponsePhoneAreaCode).toBeUndefined();
        expect(scope.material.emergencyResponsePhone).toBeUndefined();
        expect(scope.hazmatInfo).toBeDefined();
        expect(scope.hazmatInfo.exist).toBeFalsy();
    });

    it('shoud clear product', function() {
        scope.products = {
            product: {
                value: 3
            }
        };
        httpBackend.when('GET', '/restful/customer/1/product/3').respond('');
        scope.$digest();

        scope.cleanProduct();

        expect(scope.products.product).toBeUndefined();
    });

    it('should trigger customer watch and fetch list of products from the server', function() {
        scope.selectedCustomer.id = 3;
        httpBackend.when('GET', '/restful/customer/3/product-list-sort').respond({id: 123});
        spyOn(customerOrderService, 'getProductListPrimarySort');
        scope.$digest();

        expect(customerOrderService.getProductListPrimarySort).toHaveBeenCalledWith({customerId: 3}, jasmine.any(Function));
    });

    it('should trigger customer watch assign default product sort order', function() {
        var customerId = 3;
        scope.shipment.productListPrimarySort = {items: []};
        scope.shipment.organizationId = customerId;
        scope.selectedCustomer.id = customerId;
        spyOn(customerOrderService, 'getProductListPrimarySort');
        scope.$digest();

        expect(customerOrderService.getProductListPrimarySort).not.toHaveBeenCalledWith();
    });

    it('should reset material quantity to 1 when origin accessorials are changed and does not contains ODM', function() {
        scope.material.quantity = '123';
        scope.shipment.originDetails.accessorials = ['FSC'];
        scope.$digest();

        expect(scope.material.quantity).toEqual('1');
    });

    it('should set material quantity to blank value when origin accessorials are changed and contains ODM', function() {
        scope.material.quantity = '123';
        scope.shipment.originDetails.accessorials = ['ODM'];
        scope.$digest();

        expect(scope.material.quantity).toEqual('');
    });

    it('should reset material quantity to 1 when destination accessorials are changed and does not contains ODM', function() {
        scope.material.quantity = '123';
        scope.shipment.destinationDetails.accessorials = ['FSC'];
        scope.$digest();

        expect(scope.material.quantity).toEqual('1');
    });

    it('should set material quantity to blank value when destination accessorials are changed and contains ODM', function() {
        scope.material.quantity = '123';
        scope.shipment.destinationDetails.accessorials = ['ODM'];
        scope.$digest();

        expect(scope.material.quantity).toEqual('');
    });

    it('should clear form data', function() {
        scope.$broadcast('event:pls-clear-form-data');

        expect(scope.material).toEqual(emptyMaterial);
        expect(scope.products.product).toEqual('');
        expect(scope.products.productList).toBeUndefined();
        expect(scope.products.hazmatOnly).toBeUndefined();
    });

    it('should add quote item', function() {
        spyOn(scope, 'addProduct');
        spyOn(scope, '$emit');
        scope.$broadcast('event:pls-add-quote-item');

        expect(scope.addProduct).toHaveBeenCalled();
        expect(scope.$emit).toHaveBeenCalledWith('event:pls-added-quote-item', 1, undefined);
    });

    it('should open dialog for adding of a product', function() {
        spyOn(scope.$root, '$broadcast').and.callThrough();
        formButtons[2].click();

        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:showAddEditProduct', jasmine.any(Object));
    });

    it('should add product on "Add" button click', function() {
        scope.addProductForm = {
            $valid: true
        };
        scope.shipment.finishOrder.quoteMaterials.push({});
        scope.$digest();

        spyOn(scope.$root, '$broadcast').and.callThrough();
        formButtons[3].click();

        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:productChanged');
    });

    it('should edit product on "Edit" button click', function() {
        scope.defaultProductSortOrder = undefined;
        scope.materialsGrid.selectedItems.push(scope.shipment.finishOrder.quoteMaterials[0]);
        scope.$digest();
        spyOn(scope, 'removeProduct');

        formButtons[4].click();

        expect(scope.removeProduct).toHaveBeenCalled();
    });

    it('should edit product on "Edit" button click', function() {
        scope.materialsGrid.selectedItems.push(scope.shipment.finishOrder.quoteMaterials[0]);
        scope.$digest();
        spyOn(scope.$root, '$broadcast').and.callThrough();

        formButtons[5].click();

        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:productChanged');
    });

    it('should get quote on "Get Quote" button click', function() {
        scope.$digest();
        spyOn(scope, 'addProduct');

        formButtons[6].click();

        expect(scope.addProduct).toHaveBeenCalled();
    });
});