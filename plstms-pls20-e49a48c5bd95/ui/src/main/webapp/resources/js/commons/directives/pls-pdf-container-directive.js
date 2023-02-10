/**
 * AngularJS directive which displays PDF document based on URL.
 *
 * @author: Sergey Kirichenko
 * Date: 5/10/13
 * Time: 3:19 PM
 */
angular.module('plsApp.directives').directive('plsPdfContainer', ['$compile', 'urlConfig', 'ShipmentDocumentService',
                                                                  function ($compile, urlConfig, ShipmentDocumentService) {
    return {
        restrict: 'A',
        scope: {
            options: '=plsPdfContainer'
        },
        link: function (scope, element) {
            'use strict';

            scope.hidePdf = false;
            var setLocation = null;
            var pdfDocument;
            var width = scope.options.width || '100%';
            var height = scope.options.height || '600px';

            function setupPdfObject() {
                if ((scope.options.imageContent)) {
                    pdfDocument = $compile('<img src="' + scope.options.pdfLocation
                            + '" style="max-height: {{options.height || \'600px\'}}" alt="" width="{{options.width}}" /> ')(scope);
                    element.html(pdfDocument);
                } else {
                    pdfDocument = $compile('<div class="pdf-wrapper"><object id="ttt" width="' + width + '" height="' + height + '" ' +
                            'type="application/pdf" data="' + scope.options.pdfLocation + '"></object> ' +
                            '<div class="msg-pdf"><span>PDF Reader is not installed on your computer. Please </span><a target="_blank" ' +
                            'href="https://get.adobe.com/reader/">download Adobe Acrobat Reader</a></div></div>')(scope);
                    element.html(pdfDocument);
                }
            }

            var emptyElement = $compile('<div class="pdf-wrapper"><div  style="width:' + width + '; height:' + height + '" ' +
                    'class="pls-pdf-container-emty"><div><b>None of the Documents are selected.</b></div></div></div>')(scope);
            var multipleDocumentsElement = $compile('<div class="pdf-wrapper"><div  style="width:' + width + '; height:' + height + '" ' +
            'class="pls-pdf-container-emty"><div><b>Multiple Documents selected.</b></div></div></div>')(scope);

            element.html(emptyElement);

            scope.$watch('[options.hidePdf, hidePdf, options.pdfLocation]', function () {
                if (scope.options.hidePdf || scope.hidePdf) {
                    setLocation = null;
                    if (scope.options.docsNumber > 1) {
                        element.html(multipleDocumentsElement);
                    } else {
                        element.html(emptyElement);
                    }
                } else if (scope.options.pdfLocation && setLocation !== scope.options.pdfLocation) {
                    setLocation = scope.options.pdfLocation;
                    setupPdfObject();
                }
            }, true);

            scope.$root.$on('event:openContactUs', function () {
                scope.hidePdf = true;
            });

            scope.$root.$on('event:contactUsClosed', function () {
                scope.hidePdf = false;
            });

            function openDocument () {
                var doc;

                if (scope.options.url) {
                    doc = window.open(scope.options.url, "_blank");
                } else {
                    if (!_.isEmpty(pdfDocument[0].childNodes[0]) && pdfDocument[0].childNodes[0].data) {
                        doc = window.open(pdfDocument[0].childNodes[0].data, "_blank");
                    } else {
                        doc = window.open(pdfDocument[0].currentSrc, "_blank");
                    }
                }

                if (doc.addEventListener) {
                    doc.addEventListener('load', function () {
                        doc.focus();
                        doc.print();
                    }, false);
                }
            }

            scope.options.printDocument = function (data) {
                if (data) {
                    ShipmentDocumentService.prepareDocsForShipment(data.requestData, data.shipment, function (dataArr) {
                        if (dataArr) {
                            scope.options.url = urlConfig.shipment + '/customer/shipmentdocs/' + dataArr[0].value;
                            openDocument();
                        }
                    });
                } else {
                    openDocument();
                }
            };
        }
    };
}]);