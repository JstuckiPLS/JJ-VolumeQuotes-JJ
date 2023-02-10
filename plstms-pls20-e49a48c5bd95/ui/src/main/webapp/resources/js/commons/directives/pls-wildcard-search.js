/**
 *  AngularJS directive for filtering enter data for wildcard search.
 */
angular.module('plsApp.directives').directive('plsWildcardSearch', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            'use strict';

            var regExp = new RegExp('(^[*]?[^*]{3,}[*]?$)|(^[^*]{1,3}$)');

            function validator(val) {
                //if input is empty then it's valid
                //input can be marked as mandatory using 'required' attribute
                if (!val) {
                    return undefined;
                }

                var valid = regExp.test(val);

                if (valid) {
                    ctrl.$setValidity('plsWildcardSearch', true);
                    return val;
                }

                ctrl.$setValidity('plsWildcardSearch', false);
                return undefined;
            }

            ctrl.$parsers.push(validator);
            ctrl.$formatters.push(validator);
        }
    };
});