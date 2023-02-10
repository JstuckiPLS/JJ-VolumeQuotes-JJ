//describe('Dashboard tests for different reports go here', function() {
//    beforeEach(function() {
//        $injector = angular.injector(['PageObjectsModule']);
//        freightSpendAnalysisReportObject = $injector.get('FreightSpendAnalysisReportObject');
//        weightAnalysisReportObject = $injector.get('WeightAnalysisReportObject');
//        geographicSummaryReportObject = $injector.get('GeographicReportObject');
//        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
//    });
//
//    it('checks freight spend analysis page objects', function() {
//        browser().navigateTo('#/dashboard/analysis/freight-spend-analysis');
//
//        expect(element(freightSpendAnalysisReportObject.classFilterSelector, 'check class filter select exists').count()).toBe(1);
//        expect(element(freightSpendAnalysisReportObject.refreshButtonSelector, 'check refresh button exists').count()).toBe(1);
//        expect(element(freightSpendAnalysisReportObject.pieChartSelector, 'check pie freight chart exists').count()).toBe(1);
//        expect(element(freightSpendAnalysisReportObject.barChartSelector, 'check bar freight chart exists').count()).toBe(1);
//        expect(element(freightSpendAnalysisReportObject.freightGridSelector, 'check freight grid exists').count()).toBe(1);
//        expect(element(freightSpendAnalysisReportObject.freightExportButtonSelector, 'check export button exists').count()).toBe(1);
//    });
//
//    it('checks weight analysis page objects', function() {
//        browser().navigateTo('#/dashboard/analysis/weight-analysis');
//
//        expect(element(weightAnalysisReportObject.weightFilterSelector, 'check weight filter selector exists').count()).toBe(1);
//        expect(element(weightAnalysisReportObject.refreshButtonSelector, 'check refresh button existance').count()).toBe(1);
//        expect(element(weightAnalysisReportObject.graphChartSelector, 'check weight chart existance').count()).toBe(1);
//        expect(element(weightAnalysisReportObject.weightGridSelector, 'check weight grid existance').count()).toBe(1);
//        expect(element(weightAnalysisReportObject.weightExportButtonSelector, 'check export button existance').count()).toBe(1);
//    });
//
//    it('checks geographic summary page objects', function() {
//        browser().navigateTo('#/dashboard/summaries/geographic-summary');
//
//        expect(element(geographicSummaryReportObject.ioFilterSelector, 'check inbound/outbound filter selector exists').count()).toBe(1);
//        expect(element(geographicSummaryReportObject.destinationFilterSelector, 'check destination filter selector exists').count()).toBe(1);
//        expect(element(geographicSummaryReportObject.originFilterSelector, 'check origin filter selector exists').count()).toBe(1);
//        expect(element(geographicSummaryReportObject.classFilterSelector, 'check class filter selector exists').count()).toBe(1);
//        expect(element(geographicSummaryReportObject.refreshButtonSelector, 'check refresh button existance').count()).toBe(1);
//        expect(element(geographicSummaryReportObject.geographicGridSelector, 'check geographic grid existance').count()).toBe(1);
//        expect(element(geographicSummaryReportObject.geographicExportButtonSelector, 'check export button existance').count()).toBe(1);
//    });
//});