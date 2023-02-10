describe('Unit test for breadcrumb directive', function() {
    var element, scope;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($compile, $rootScope) {
        $rootScope.allPages = [{title: 'General information', label: 'general-information'}, 
                               {title: 'Addresses', label: 'addresses'}, 
                               {title: 'Details', label: 'details'}, 
                               {title: 'Docs', label: 'docs'}];

        var html = '<data-pls-breadcrumb data-all-pages="allPages" data-current-page="general-information"></data-pls-breadcrumb>';
        element = $compile(html)($rootScope.$new());
        scope = element.isolateScope();
        scope.$digest();
    }));

    it('should check initial model state', function() {
        expect(scope.allPages.length).toBe(4);
        expect(scope.allPages[0].title).toEqual('General information');
        expect(scope.allPages[0].label).toEqual('general-information');
        expect(scope.allPages[1].title).toEqual('Addresses');
        expect(scope.allPages[1].label).toEqual('addresses');
        expect(scope.allPages[2].title).toEqual('Details');
        expect(scope.allPages[2].label).toEqual('details');
        expect(scope.allPages[3].title).toEqual('Docs');
        expect(scope.allPages[3].label).toEqual('docs');
        expect(scope.currentPage).toEqual('general-information');
    });

    it('should check there are totally four crumbs has been rendered', function() {
        var listItems = element.find('li');

        expect(listItems.length).toBe(4);

        expect(listItems.eq(0).find('strong').eq(0).text()).toEqual('General information');
        expect(listItems.eq(0).find('span').eq(1).text()).toEqual('/');
        expect(listItems.eq(0).find('span').hasClass('divider')).toBeTruthy();

        expect(listItems.eq(1).text()).toEqual('Addresses/');
        expect(listItems.eq(1).find('span').eq(1).text()).toEqual('/');
        expect(listItems.eq(1).find('span').hasClass('divider')).toBeTruthy();

        expect(listItems.eq(2).text()).toEqual('Details/');
        expect(listItems.eq(2).find('span').eq(1).text()).toEqual('/');
        expect(listItems.eq(2).find('span').hasClass('divider')).toBeTruthy();

        expect(listItems.eq(3).text()).toEqual('Docs');
        expect(listItems.eq(3).find('span').eq(1).text()).toEqual('');
        expect(listItems.eq(3).find('span').hasClass('divider')).toBeFalsy();
    });

    it('should prove only one page at the given point of time is active', function() {
        expect(scope.isActive({title: 'General information', label: 'general-information'})).toBeTruthy();
        expect(scope.isActive({title: 'Addresses', label: 'addresses'})).toBeFalsy();
        expect(scope.isActive({title: 'Details', label: 'details'})).toBeFalsy();
        expect(scope.isActive({title: 'Docs', label: 'docs'})).toBeFalsy();

        scope.currentPage = 'addresses';

        expect(scope.isActive({title: 'General information', label: 'general-information'})).toBeFalsy();
        expect(scope.isActive({title: 'Addresses', label: 'addresses'})).toBeTruthy();
        expect(scope.isActive({title: 'Details', label: 'details'})).toBeFalsy();
        expect(scope.isActive({title: 'Docs', label: 'docs'})).toBeFalsy();
    });
});