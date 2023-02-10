angular.module('plsApp.directives').directive('plsAttachmentsList', function () {
    return {
        restrict: 'A',
        scope: {
            attachments: '@'
        },
        replace: true,
        link: function (scope, element) {
            'use strict';

            var i;

            if (!scope.attachments) {
                return;
            }

            var attachments = JSON.parse(scope.attachments);
            var result = '';

            for (i = 0; i < attachments.length; i = i + 1) {
                var current = attachments[i];
                result = result + '<a target="_blank" href="/restful/customer/shipmentdocs/' + current.attachmentId + '">' + current.attachmentName +
                        '</a>';

                if (attachments.indexOf(current) !== attachments.length - 1) {
                    result = result + ' , ';
                }
            }

            element.html("<div>" + result + "</div>");
        }
    };
});
