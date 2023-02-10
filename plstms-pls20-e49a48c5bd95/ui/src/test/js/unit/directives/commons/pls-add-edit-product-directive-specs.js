/**
 * Tests for pls-add-edit-product-directive.
 * @author: Alexander Kirichenko
 */
describe('Test scenarios for pls-add-edit-product directive.', function() {
    var ADD_EDIT_PRODUCT_DLG_SELECTOR = 'div#addEditProductDialog';
    var INVALID_ELEMENTS_SELECTOR = '.ng-invalid.ng-invalid-required';
    var DISABLED_SELECTOR = '[disabled="disabled"]';
    var ADD_EDIT_BTN_SELECTOR = 'button.a_add_product_ok';
    var DISABLED_ADD_BTN_SELECTOR = ADD_EDIT_BTN_SELECTOR + DISABLED_SELECTOR;
    var COMMODITY_CLASS_SEL_SELECTOR = 'select#commodity-class-select';
    var DISABLED_COMMODITY_CLASS_SEL_SELECTOR = COMMODITY_CLASS_SEL_SELECTOR + DISABLED_SELECTOR;
    var PRODUCT_DESCRIPTION_INPUT_SELECTOR = 'input#product-description';
    var NMFC_INPUT_SELECTOR = 'input#nmfc-class';
    var PRODUCT_CODE_INPUT_SELECTOR = 'input#product-code';
    var NMFC_SUB_NUM_INPUT_SELECTOR = 'input[data-ng-model="editProductModel.product.nmfcSubNum"]';
    var HAZMAT_UN_NUM_INPUT_SELECTOR = 'input#hazmat-un-num';
    var HAZMAT_CLASS_SEL_SELECTOR = 'select#hazmat-class-select';
    var HAZMAT_EMRG_COMPANY_INPUT_SELECTOR = 'input#emergencyCompanyInp';
    var HAZMAT_PHONE_CNTRY_CODE_INPUT_SELECTOR = 'input#phoneCountryCodeInp';
    var HAZMAT_PHONE_AREA_CODE_INPUT_SELECTOR = 'input[data-ng-model="editProductModel.product.hazmatEmergencyPhone.areaCode"]';
    var HAZMAT_PHONE_NUM_INPUT_SELECTOR = 'input[data-ng-model="editProductModel.product.hazmatEmergencyPhone.number"]';
    var HAZMAT_EMRG_CONTRACT_NUM_INPUT_SELECTOR = 'input#emergencyContractInp';
    var HAZMAT_EMRG_INSTRUCTION_TEXTAREA_SELECTOR = 'textarea#emergencyInstructionTxt';
    var HAZMAT_CHECKBOX_SELECTOR = 'input#prod-hazmat-checkbox';

    var SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT = 'event:showAddEditProduct';

    var scope, elm, savedProduct, savedProductId;

    var mockProduct = {
        id: 1,
        description: 'Test product',
        commodityClass: 'CLASS_50',
        nmfc: '1234',
        nmfcSubNum: '5678',
        productCode: 'TPr1',
        hazmat: false,
        sharedProduct: true
    };

    var mockHazmatProduct = {
        id: 2,
        description: 'Test hazmat product',
        commodityClass: 'CLASS_50',
        nmfc: '987',
        nmfcSubNum: '654',  
        productCode: 'TprH1',
        hazmat: true,
        hazmatUnNumber: '789',
        hazmatPackingGroup: '',
        hazmatClass: '3',
        hazmatEmergencyCompany: 'EmrgCmp',
        hazmatEmergencyPhone: {
            countryCode: '1',
            areaCode: '123',
            number: '1234567'
        },
        hazmatEmergencyContract: 'John Doe',
        hazmatInstructions: 'Be carefully',
        sharedProduct: true
    };

    var prepareMockProductToCompare = function(product, clearHazmat) {
        var shouldClearHazmat = clearHazmat || false;
        var processedProduct = angular.copy(product);
        delete processedProduct.nmfc;
        delete processedProduct.nmfcSubNum;
        delete processedProduct.productCode;
        if (shouldClearHazmat) {
            delete processedProduct.hazmatInstructions;
        }
        return processedProduct;
    };

    var mockCloseHandlrr = {
        closeHandler: function(savedProductId) {

        }
    };

    var mockService = {
        save: function(data, product, success) {
            savedProduct = product;
            savedProduct.hazmat = savedProduct.hazmat || false;
            if (success && angular.isFunction(success)) {
                success({data: product.hazmat? 2 : 1});
            }
        },
        get: function(data, success) {
            if (data && data.productId && success && angular.isFunction(success)) {
                var product;
                if (data.productId === 1) {
                    product = mockProduct;
                } else {
                    product = mockHazmatProduct;
                }
                success(product);
            }
        },
        isUnique: function (data, success) {
            success({result: "true"});
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/add-edit-product-tpl.html',
            'pages/tpl/class-estimator-tpl.html', function($provide) {
        $provide.factory('ProductService', function() {
            return mockService;
        });
    }));

    beforeEach(inject(function($rootScope, $compile, $document) {
        $rootScope.authData.privilegies.push('ADD_EDIT_HAZMAT_PRODUCT');

        savedProduct = savedProductId = undefined;
        scope = $rootScope.$new();
        scope.selectedCustomer = {
            id : 1,
            name: 'WORTHINGTON INDUSTRIES'
        };
        elm = angular.element('<div id="plsAddEditProductDirectiveContainer">' +
            '<div data-pls-add-edit-product data-selected-customer="selectedCustomer"></div></div>');
        $document.find('body').append($compile(elm)(scope));
        scope.$digest();
        scope.$on('event:newProductAdded', function(event, newProductId) {
            savedProductId = newProductId;
        });
        spyOn(mockCloseHandlrr, 'closeHandler').and.callThrough();
    }));

    afterEach(inject(function($document, $rootScope) {
        var index = $rootScope.authData.privilegies.indexOf('ADD_EDIT_HAZMAT_PRODUCT');
        if (index > -1) {
            $rootScope.authData.privilegies.splice(index, 1);
        }
        $document.find('div#plsAddEditProductDirectiveContainer').remove();
        $document.find('div.modal-backdrop').remove();
    }));

    describe('Test adding new product', function() {

        it('Should open add/edit product dialog by sending event.', function() {
            c_expect(elm.find(ADD_EDIT_PRODUCT_DLG_SELECTOR).css('display')).to.be.eql('none');
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {});
            });
            c_expect(elm.find(ADD_EDIT_PRODUCT_DLG_SELECTOR).css('display')).to.be.eql('block');
            c_expect(elm.find(INVALID_ELEMENTS_SELECTOR).length).to.be.equal(3);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            c_expect(elm.find(DISABLED_COMMODITY_CLASS_SEL_SELECTOR)).to.not.exist;
            c_expect(elm.find('div[data-ng-bind="editProductModel.selectedCustomerName"]').text()).
                    to.be.equal(scope.selectedCustomer.name);
        });

        it('Should open add/edit product dialog with predefined commodity class.', function() {
            c_expect(elm.find(ADD_EDIT_PRODUCT_DLG_SELECTOR).css('display')).to.be.eql('none');
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {commodityClass: 'CLASS_77_5'});
            });
            c_expect(elm.find(ADD_EDIT_PRODUCT_DLG_SELECTOR).css('display')).to.be.eql('block');
            c_expect(elm.find(INVALID_ELEMENTS_SELECTOR).length).to.be.equal(2);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            c_expect(elm.find(DISABLED_COMMODITY_CLASS_SEL_SELECTOR)).to.exist;
            c_expect(elm.find(DISABLED_COMMODITY_CLASS_SEL_SELECTOR + ' > option:selected').text()).to.be.equal('77.5');
        });

        it('Should save new non hazmat product with required fields set only.', function() {
            c_expect(savedProduct).to.not.exist;
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {});
            });
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(PRODUCT_DESCRIPTION_INPUT_SELECTOR)).enter(mockProduct.description);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            select(elm.find(COMMODITY_CLASS_SEL_SELECTOR)).optionByLabel('50');
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.not.exist;
            elm.find(ADD_EDIT_BTN_SELECTOR).click();
            c_expect(savedProduct).to.exist;
            var toCompare = prepareMockProductToCompare(mockProduct);
            c_expect(savedProduct).to.be.eql(toCompare);
            c_expect(savedProductId).to.be.equal(1);
        });

        it('Should call close handler on save new non hazmat product with required fields set only.', function() {
            c_expect(savedProduct).to.not.exist;
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {closeHandler: mockCloseHandlrr.closeHandler});
            });
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(PRODUCT_DESCRIPTION_INPUT_SELECTOR)).enter(mockProduct.description);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            select(elm.find(COMMODITY_CLASS_SEL_SELECTOR)).optionByLabel('50');
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.not.exist;
            elm.find(ADD_EDIT_BTN_SELECTOR).click();
            c_expect(savedProduct).to.exist;
            var toCompare = prepareMockProductToCompare(mockProduct);
            c_expect(savedProduct).to.be.eql(toCompare);
            c_expect(savedProductId).to.be.equal(1);
            expect(mockCloseHandlrr.closeHandler).toHaveBeenCalledWith(toCompare);
        });

        it('Should save new non hazmat product with all fields.', function() {
            c_expect(savedProduct).to.not.exist;
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {});
            });
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(PRODUCT_DESCRIPTION_INPUT_SELECTOR)).enter(mockProduct.description);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            select(elm.find(COMMODITY_CLASS_SEL_SELECTOR)).optionByLabel('50');
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.not.exist;
            input(elm.find(NMFC_INPUT_SELECTOR)).enter(mockProduct.nmfc);
            input(elm.find(PRODUCT_CODE_INPUT_SELECTOR)).enter(mockProduct.productCode);
            input(elm.find(NMFC_SUB_NUM_INPUT_SELECTOR)).enter(mockProduct.nmfcSubNum);
            elm.find(ADD_EDIT_BTN_SELECTOR).click();
            c_expect(savedProduct).to.exist;
            c_expect(savedProduct).to.be.eql(mockProduct);
            c_expect(savedProductId).to.be.equal(1);
        });

        it('Should save new hazmat product with required fields set only.', function() {
            c_expect(savedProduct).to.not.exist;
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {});
            });
            elm.find(HAZMAT_CHECKBOX_SELECTOR).click();
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            c_expect(elm.find(INVALID_ELEMENTS_SELECTOR).length).to.be.equal(9);
            input(elm.find(PRODUCT_DESCRIPTION_INPUT_SELECTOR)).enter(mockHazmatProduct.description);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            select(elm.find(COMMODITY_CLASS_SEL_SELECTOR)).optionByLabel('50');
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(HAZMAT_UN_NUM_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatUnNumber);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            select(elm.find(HAZMAT_CLASS_SEL_SELECTOR)).option(3);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(HAZMAT_EMRG_COMPANY_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatEmergencyCompany);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(HAZMAT_PHONE_CNTRY_CODE_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatEmergencyPhone.countryCode);
            input(elm.find(HAZMAT_PHONE_AREA_CODE_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatEmergencyPhone.areaCode);
            input(elm.find(HAZMAT_PHONE_NUM_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatEmergencyPhone.number);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(HAZMAT_EMRG_CONTRACT_NUM_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatEmergencyContract);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.not.exist;
            elm.find(ADD_EDIT_BTN_SELECTOR).click();
            c_expect(savedProduct).to.exist;
            c_expect(savedProduct).to.be.eql(prepareMockProductToCompare(mockHazmatProduct, true));
            c_expect(savedProductId).to.be.equal(2);
        });

        it('Should save new hazmat product with all fields.', function() {
            c_expect(savedProduct).to.not.exist;
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {});
            });
            elm.find(HAZMAT_CHECKBOX_SELECTOR).click();
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            c_expect(elm.find(INVALID_ELEMENTS_SELECTOR).length).to.be.equal(9);
            input(elm.find(PRODUCT_DESCRIPTION_INPUT_SELECTOR)).enter(mockHazmatProduct.description);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            select(elm.find(COMMODITY_CLASS_SEL_SELECTOR)).optionByLabel('50');
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(HAZMAT_UN_NUM_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatUnNumber);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            select(elm.find(HAZMAT_CLASS_SEL_SELECTOR)).option(3);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(HAZMAT_EMRG_COMPANY_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatEmergencyCompany);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(HAZMAT_PHONE_CNTRY_CODE_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatEmergencyPhone.countryCode);
            input(elm.find(HAZMAT_PHONE_AREA_CODE_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatEmergencyPhone.areaCode);
            input(elm.find(HAZMAT_PHONE_NUM_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatEmergencyPhone.number);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.exist;
            input(elm.find(HAZMAT_EMRG_CONTRACT_NUM_INPUT_SELECTOR)).enter(mockHazmatProduct.hazmatEmergencyContract);
            c_expect(elm.find(DISABLED_ADD_BTN_SELECTOR)).to.not.exist;
            input(elm.find(NMFC_INPUT_SELECTOR)).enter(mockHazmatProduct.nmfc);
            input(elm.find(PRODUCT_CODE_INPUT_SELECTOR)).enter(mockHazmatProduct.productCode);
            input(elm.find(NMFC_SUB_NUM_INPUT_SELECTOR)).enter(mockHazmatProduct.nmfcSubNum);
            input(elm.find(HAZMAT_EMRG_INSTRUCTION_TEXTAREA_SELECTOR)).enter(mockHazmatProduct.hazmatInstructions);
            elm.find(ADD_EDIT_BTN_SELECTOR).click();
            c_expect(savedProduct).to.exist;
            c_expect(savedProduct).to.be.eql(mockHazmatProduct);
            c_expect(savedProductId).to.be.equal(2);
        });
    });

    describe('Test editing existing product', function() {

        it('Should open edit product dialog with preloaded product without hazmat', function() {
            spyOn(mockService, 'get').and.callThrough();
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {productId: 1});
            });
            c_expect(elm.find(ADD_EDIT_PRODUCT_DLG_SELECTOR).css('display')).to.be.eql('block');
            c_expect(elm.find(HAZMAT_CHECKBOX_SELECTOR + ':checked')).to.not.exist;
            expect(mockService.get).toHaveBeenCalled();
            c_expect(mockService.get.calls.argsFor(0)[0]).to.be.eql({customerId: 1, productId: 1});
            c_expect(elm.find(PRODUCT_DESCRIPTION_INPUT_SELECTOR).val()).to.be.equal(mockProduct.description);
            c_expect(elm.find(NMFC_INPUT_SELECTOR).val()).to.be.equal(mockProduct.nmfc);
            c_expect(elm.find(NMFC_SUB_NUM_INPUT_SELECTOR).val()).to.be.equal(mockProduct.nmfcSubNum);
            c_expect(elm.find(COMMODITY_CLASS_SEL_SELECTOR + ' > option:selected').text()).to.be.equal('50');
            c_expect(elm.find(PRODUCT_CODE_INPUT_SELECTOR).val()).to.be.equal(mockProduct.productCode);
            c_expect(elm.find(HAZMAT_CHECKBOX_SELECTOR + ':not(:checked)')).to.exist;
        });

        it('Should open edit product dialog with preloaded product with hazmat', function() {
            spyOn(mockService, 'get').and.callThrough();
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {productId: 2});
            });
            c_expect(elm.find(ADD_EDIT_PRODUCT_DLG_SELECTOR).css('display')).to.be.eql('block');
            expect(mockService.get).toHaveBeenCalled();
            c_expect(mockService.get.calls.argsFor(0)[0]).to.be.eql({customerId: 1, productId: 2});
            c_expect(elm.find(PRODUCT_DESCRIPTION_INPUT_SELECTOR).val()).to.be.equal(mockHazmatProduct.description);
            c_expect(elm.find(NMFC_INPUT_SELECTOR).val()).to.be.equal(mockHazmatProduct.nmfc);
            c_expect(elm.find(NMFC_SUB_NUM_INPUT_SELECTOR).val()).to.be.equal(mockHazmatProduct.nmfcSubNum);
            c_expect(elm.find(COMMODITY_CLASS_SEL_SELECTOR + ' > option:selected').text()).to.be.equal('50');
            c_expect(elm.find(PRODUCT_CODE_INPUT_SELECTOR).val()).to.be.equal(mockHazmatProduct.productCode);
            c_expect(elm.find(HAZMAT_CHECKBOX_SELECTOR + ':checked')).to.exist;
            c_expect(elm.find(HAZMAT_UN_NUM_INPUT_SELECTOR).val()).to.be.equal(mockHazmatProduct.hazmatUnNumber);
            c_expect(elm.find(HAZMAT_CLASS_SEL_SELECTOR + ' > option:selected').val()).to.be.equal(mockHazmatProduct.hazmatClass);
            c_expect(elm.find(HAZMAT_EMRG_COMPANY_INPUT_SELECTOR).val()).to.be.equal(mockHazmatProduct.hazmatEmergencyCompany);
            c_expect(elm.find(HAZMAT_PHONE_CNTRY_CODE_INPUT_SELECTOR).val()).to.be.equal(mockHazmatProduct.hazmatEmergencyPhone.countryCode);
            c_expect(elm.find(HAZMAT_PHONE_AREA_CODE_INPUT_SELECTOR).val()).to.be.equal(mockHazmatProduct.hazmatEmergencyPhone.areaCode);
            c_expect(elm.find(HAZMAT_PHONE_NUM_INPUT_SELECTOR).val()).to.be.equal(mockHazmatProduct.hazmatEmergencyPhone.number);
            c_expect(elm.find(HAZMAT_EMRG_CONTRACT_NUM_INPUT_SELECTOR).val()).to.be.equal(mockHazmatProduct.hazmatEmergencyContract);
            c_expect(elm.find(HAZMAT_EMRG_INSTRUCTION_TEXTAREA_SELECTOR).val()).to.be.equal(mockHazmatProduct.hazmatInstructions);
        });

        it('Should open edit product dialog with preloaded product without hazmat and then save it.', function() {
            spyOn(mockService, 'get').and.callThrough();
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {productId: 1, closeHandler: mockCloseHandlrr.closeHandler});
            });
            c_expect(elm.find(ADD_EDIT_PRODUCT_DLG_SELECTOR).css('display')).to.be.eql('block');
            expect(mockService.get).toHaveBeenCalled();
            c_expect(mockService.get.calls.argsFor(0)[0]).to.be.eql({customerId: 1, productId: 1});
            var modifiedProduct = angular.extend(angular.copy(mockProduct), {
                description: mockProduct.description + '*',
                nmfc: mockProduct.nmfc + '*',
                nmfcSubNum: mockProduct.nmfcSubNum + '*',
                productCode: mockProduct.productCode + '*'
            });
            delete modifiedProduct.hazmatEmergencyPhone;
            delete modifiedProduct.hazmatPackingGroup;
            input(elm.find(PRODUCT_DESCRIPTION_INPUT_SELECTOR)).enter(modifiedProduct.description);
            select(elm.find(COMMODITY_CLASS_SEL_SELECTOR)).optionByLabel('50');
            input(elm.find(NMFC_INPUT_SELECTOR)).enter(modifiedProduct.nmfc);
            input(elm.find(NMFC_SUB_NUM_INPUT_SELECTOR)).enter(modifiedProduct.nmfcSubNum);
            input(elm.find(PRODUCT_CODE_INPUT_SELECTOR)).enter(modifiedProduct.productCode);
            elm.find(ADD_EDIT_BTN_SELECTOR).click();
            c_expect(savedProduct).to.exist;
            c_expect(savedProduct).to.be.eql(modifiedProduct);
            c_expect(savedProductId).to.be.equal(1);
            expect(mockCloseHandlrr.closeHandler).toHaveBeenCalledWith(modifiedProduct);
        });

        it('Should open edit product dialog with preloaded product with hazmat and then save it.', function() {
            spyOn(mockService, 'get').and.callThrough();
            scope.$apply(function() {
                scope.$broadcast(SHOW_ADD_EDIT_PRODUCT_DIALOG_EVENT, {productId: 2, closeHandler: mockCloseHandlrr.closeHandler});
            });
            c_expect(elm.find(ADD_EDIT_PRODUCT_DLG_SELECTOR).css('display')).to.be.eql('block');
            c_expect(elm.find(HAZMAT_CHECKBOX_SELECTOR + ':checked')).to.exist;
            expect(mockService.get).toHaveBeenCalled();
            c_expect(mockService.get.calls.argsFor(0)[0]).to.be.eql({customerId: 1, productId: 2});
            var modifiedProduct = angular.extend(angular.copy(mockHazmatProduct), {
                description: mockHazmatProduct.description + '*',
                nmfc: mockHazmatProduct.nmfc + '*',
                nmfcSubNum: mockHazmatProduct.nmfcSubNum + '*',
                productCode: mockHazmatProduct.productCode + '*',
                hazmatUnNumber: mockHazmatProduct.hazmatUnNumber + '*',
                hazmatEmergencyCompany: mockHazmatProduct.hazmatEmergencyCompany + '*',
                hazmatEmergencyContract: mockHazmatProduct.hazmatEmergencyContract + '*',
                hazmatInstructions: mockHazmatProduct.hazmatInstructions + '*',
                hazmatPackingGroup: '',
                hazmatEmergencyPhone: {
                    countryCode: '1',
                    areaCode: '321',
                    number: '7654321'
                }
            });
            input(elm.find(PRODUCT_DESCRIPTION_INPUT_SELECTOR)).enter(modifiedProduct.description);
            select(elm.find(COMMODITY_CLASS_SEL_SELECTOR)).optionByLabel('50');
            input(elm.find(NMFC_INPUT_SELECTOR)).enter(modifiedProduct.nmfc);
            input(elm.find(NMFC_SUB_NUM_INPUT_SELECTOR)).enter(modifiedProduct.nmfcSubNum);
            input(elm.find(PRODUCT_CODE_INPUT_SELECTOR)).enter(modifiedProduct.productCode);
            input(elm.find(HAZMAT_UN_NUM_INPUT_SELECTOR)).enter(modifiedProduct.hazmatUnNumber);
            input(elm.find(HAZMAT_EMRG_COMPANY_INPUT_SELECTOR)).enter(modifiedProduct.hazmatEmergencyCompany);
            input(elm.find(HAZMAT_PHONE_CNTRY_CODE_INPUT_SELECTOR)).enter(modifiedProduct.hazmatEmergencyPhone.countryCode);
            input(elm.find(HAZMAT_PHONE_AREA_CODE_INPUT_SELECTOR)).enter(modifiedProduct.hazmatEmergencyPhone.areaCode);
            input(elm.find(HAZMAT_PHONE_NUM_INPUT_SELECTOR)).enter(modifiedProduct.hazmatEmergencyPhone.number);
            input(elm.find(HAZMAT_EMRG_CONTRACT_NUM_INPUT_SELECTOR)).enter(modifiedProduct.hazmatEmergencyContract);
            input(elm.find(HAZMAT_EMRG_INSTRUCTION_TEXTAREA_SELECTOR)).enter(modifiedProduct.hazmatInstructions);
            elm.find(ADD_EDIT_BTN_SELECTOR).click();
            c_expect(savedProduct).to.exist;
            c_expect(savedProduct).to.be.eql(modifiedProduct);
            c_expect(savedProductId).to.be.equal(2);
            expect(mockCloseHandlrr.closeHandler).toHaveBeenCalledWith(modifiedProduct);
        });
    });
});