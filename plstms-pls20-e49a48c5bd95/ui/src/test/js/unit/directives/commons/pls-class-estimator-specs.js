/**
 * This unit test checks pls-class-estimator directive.
 * 
 * @author Aleksandr Leshchenko
 */
xdescribe('Class Estimator', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;

    var testData = [
        {weight: 1, length: 20, width: 20, height: 20, pcf: 0.216, estimatedClass: '500'},
        {weight: 1, length: 10, width: 10, height: 10, pcf: 1.728, estimatedClass: '400'},
        {weight: 1, length: 10, width: 10, height: 7, pcf: 2.469, estimatedClass: '300'},
        {weight: 1, length: 10, width: 10, height: 5, pcf: 3.456, estimatedClass: '250'},
        {weight: 1, length: 10, width: 7, height: 5, pcf: 4.937, estimatedClass: '200'},
        {weight: 1, length: 10, width: 6, height: 5, pcf: 5.76, estimatedClass: '175'},
        {weight: 1, length: 10, width: 5, height: 5, pcf: 6.912, estimatedClass: '150'},
        {weight: 1, length: 5, width: 6, height: 8, pcf: 7.2, estimatedClass: '125'},
        {weight: 1, length: 5, width: 6, height: 7, pcf: 8.229, estimatedClass: '110'},
        {weight: 1, length: 5, width: 6, height: 6, pcf: 9.6, estimatedClass: '100'},
        {weight: 1, length: 5, width: 5, height: 6, pcf: 11.52, estimatedClass: '92.5'},
        {weight: 1, length: 5, width: 5, height: 5.5, pcf: 12.567, estimatedClass: '85'},
        {weight: 1, length: 5, width: 5, height: 5, pcf: 13.824, estimatedClass: '77.5'},
        {weight: 1, length: 5, width: 5, height: 4, pcf: 17.28, estimatedClass: '70'},
        {weight: 1, length: 5, width: 5, height: 3, pcf: 23.04, estimatedClass: '65'},
        {weight: 1, length: 5, width: 5, height: 2, pcf: 34.56, estimatedClass: '60'},
        {weight: 1, length: 5, width: 4, height: 2, pcf: 43.2, estimatedClass: '55'},
        {weight: 1, length: 3, width: 3, height: 3, pcf: 64, estimatedClass: '50'}
    ];

    beforeEach(module('plsApp', 'pages/tpl/class-estimator-tpl.html'));

    beforeEach(inject(function($rootScope, $http, $compile, $templateCache, $httpBackend, NgGridPluginFactory, ClassEstimatorService, $timeout) {
        elm = angular.element(angular.copy('<div id="content" style="width: 800px;">'
                + '<button type="button" data-ng-click="open()" data-pls-class-estimator="{{parentDialog}}" '
                + 'data-ng-model="selectedCommodityClass"></button></div>'));

        $httpBackend.whenGET('pages/tpl/class-estimator-tpl.html')
                .respond($templateCache.get('pages/tpl/class-estimator-tpl.html'));

        scope = $rootScope.$new();
        scope.$apply(function() {
            scope.totals = {
                PCF: 0,
                estimatedClass: 'None',
                cubicFeet: 0,
                weight: 0
            };
            scope.model = {
                weightUnit: 'LBS',
                dimensionUnit: 'INCH',
                quantity: 1
            };
            scope.gridData = [];
        });

        $('body').append($compile(elm)(scope));
        scope.$digest();
    }));
    
    afterEach(function() {
        $('[data-pls-modal="shouldBeOpen"]').remove();
        $('#content').remove();
    });

    it('should open and close class estimator', function() {
        elm.find('button')[0].click();
        expect(scope.selectedCommodityClass).toBeUndefined();
        var modalDiv = elm.find('[data-pls-modal="shouldBeOpen"]');
        expect(modalDiv.css('display')).toBe('block');
        expect(modalDiv.find('[data-ng-model="model.weight"]')).toHaveClass('ng-invalid');
        expect(modalDiv.find('[data-ng-model="model.weight"]')).toHaveClass('ng-invalid-required');
        expect(modalDiv.find('[data-ng-model="model.weight"]').attr('data-forbid-zero')).toBe('true');
        expect(modalDiv.find('[data-ng-model="model.weight"]').is('[data-pls-number]')).toBeTruthy();
        expect(modalDiv.find('[data-ng-model="model.length"]')).toHaveClass('ng-invalid');
        expect(modalDiv.find('[data-ng-model="model.length"]')).toHaveClass('ng-invalid-required');
        expect(modalDiv.find('[data-ng-model="model.length"]').attr('data-forbid-zero')).toBe('true');
        expect(modalDiv.find('[data-ng-model="model.length"]').is('[data-pls-number]')).toBeTruthy();
        expect(modalDiv.find('[data-ng-model="model.width"]')).toHaveClass('ng-invalid');
        expect(modalDiv.find('[data-ng-model="model.width"]')).toHaveClass('ng-invalid-required');
        expect(modalDiv.find('[data-ng-model="model.width"]').attr('data-forbid-zero')).toBe('true');
        expect(modalDiv.find('[data-ng-model="model.width"]').is('[data-pls-number]')).toBeTruthy();
        expect(modalDiv.find('[data-ng-model="model.height"]')).toHaveClass('ng-invalid');
        expect(modalDiv.find('[data-ng-model="model.height"]')).toHaveClass('ng-invalid-required');
        expect(modalDiv.find('[data-ng-model="model.height"]').attr('data-forbid-zero')).toBe('true');
        expect(modalDiv.find('[data-ng-model="model.height"]').is('[data-pls-number]')).toBeTruthy();
        expect(modalDiv.find('[data-ng-model="model.weightUnit"]').val()).toBe('0');
        expect(modalDiv.find('[data-ng-model="model.weightUnit"]').find('option:selected').text()).toBe('Lbs');
        expect(modalDiv.find('[data-ng-model="model.dimensionUnit"]').val()).toBe('0');
        expect(modalDiv.find('[data-ng-model="model.dimensionUnit"]').find('option:selected').text()).toBe('Inch');

        scope.$apply(function() {
            scope.model.weight = '0';
            scope.model.length = '1';
            scope.model.width = '2';
            scope.model.height = '3';
        });

        expect(modalDiv.find('[data-ng-click="addRow()"]').is(':disabled')).toBeTruthy();
        modalDiv.find('[data-ng-click="addRow()"]').click();

        expect(scope.totals).toBeDefined();
        expect(scope.totals.PCF).toBe(0);
        expect(scope.totals.estimatedClass).toBe('None');
        expect(scope.totals.weight).toBe(0);
        expect(scope.totals.cubicFeet).toBe(0);

        modalDiv.find('.btn-primary').click();
        expect(modalDiv.css('display')).toBe('block');
        expect(scope.gridData.length).toBe(0);
        expect(scope.selectedCommodityClass).toBeUndefined();
        
        modalDiv.find('[data-ng-click="close()"]').click();
        expect(modalDiv.css('display')).toBe('none');
    });

    it('should add and delete few items', function() {
        elm.find('button')[0].click();
        var modalDiv = elm.find('[data-pls-modal="shouldBeOpen"]');
        expect(modalDiv.css('display')).toBe('block');

        expect(modalDiv.find('[data-ng-model="model.weight"]').is(':disabled')).toBeFalsy();
        expect(modalDiv.find('[data-ng-model="model.length"]').is(':disabled')).toBeFalsy();
        expect(modalDiv.find('[data-ng-model="model.width"]').is(':disabled')).toBeFalsy();
        expect(modalDiv.find('[data-ng-model="model.height"]').is(':disabled')).toBeFalsy();

        scope.$apply(function() {
            scope.model.weight = 10;
            scope.model.length = '1';
            scope.model.width = '2';
            scope.model.height = '3';
        });

        expect(modalDiv.find('[data-ng-model="model.weight"]').val()).toBe('10');
        expect(modalDiv.find('[data-ng-model="model.length"]').val()).toBe('1');
        expect(modalDiv.find('[data-ng-model="model.width"]').val()).toBe('2');
        expect(modalDiv.find('[data-ng-model="model.height"]').val()).toBe('3');

        modalDiv.find('[data-ng-click="addRow()"]').click();

        expect(modalDiv.find('[data-ng-model="model.weight"]').val()).toBe('');
        expect(modalDiv.find('[data-ng-model="model.length"]').val()).toBe('');
        expect(modalDiv.find('[data-ng-model="model.width"]').val()).toBe('');
        expect(modalDiv.find('[data-ng-model="model.height"]').val()).toBe('');

        expect(scope.totals.PCF).toBe(2880);
        expect(scope.totals.estimatedClass).toBe('50');

        expect(scope.gridData.length).toBe(1);
        expect(scope.selectedCommodityClass).toBeUndefined();

        scope.$apply(function() {
            scope.model.weight = '10';
            scope.model.length = '0.5';
            scope.model.width = '1';
            scope.model.height = '1.6';
            scope.model.weightUnit = 'KG';
            scope.model.dimensionUnit = 'M';
        });

        modalDiv.find('[data-ng-click="addRow()"]').click();

        expect(scope.totals.PCF).toBe(1.134);
        expect(scope.totals.estimatedClass).toBe('500');
        expect(scope.gridData.length).toBe(2);

        scope.selectedItems = [scope.gridData[1]];
        modalDiv.find('[data-ng-click="deleteSelectedRows()"]').click();

        expect(scope.totals.PCF).toBe(2880);
        expect(scope.totals.estimatedClass).toBe('50');
        expect(scope.gridData.length).toBe(1);

        scope.selectedItems = [scope.gridData[0]];
        modalDiv.find('[data-ng-click="deleteSelectedRows()"]').click();

        expect(scope.totals.PCF).toBe(0);
        expect(scope.totals.estimatedClass).toBe('None');
        expect(scope.gridData.length).toBe(0);
    });

    it('should calculate valid PCF and class', function() {
        elm.find('button')[0].click();
        var modalDiv = elm.find('[data-pls-modal="shouldBeOpen"]');
        expect(modalDiv.css('display')).toBe('block');

        var clearGridButton = modalDiv.find('[data-ng-click="clearGrid()"]');

        _.each(testData, function(data) {
            scope.$apply(function() {
                scope.model.weight = data.weight;
                scope.model.length = data.length;
                scope.model.width = data.width;
                scope.model.height = data.height;
            });

            modalDiv.find('[data-ng-click="addRow()"]').click();

            expect(scope.totals.PCF).toBe(data.pcf);
            expect(scope.totals.estimatedClass).toBe(data.estimatedClass);
            clearGridButton.click();
        });
    });

    it('should set valid class', function() {
        elm.find('button')[0].click();
        var modalDiv = elm.find('[data-pls-modal="shouldBeOpen"]');

        _.each(testData, function(data) {
            expect(modalDiv.css('display')).toBe('block');

            scope.$apply(function() {
                scope.model.weight = data.weight;
                scope.model.length = data.length;
                scope.model.width = data.width;
                scope.model.height = data.height;
            });

            modalDiv.find('[data-ng-click="addRow()"]').click();

            modalDiv.find('.btn-primary').click();

            expect(scope.selectedCommodityClass).toBe('CLASS_' + data.estimatedClass.replace(".", "_"));

            elm.find('button')[0].click();
        });
    });
});