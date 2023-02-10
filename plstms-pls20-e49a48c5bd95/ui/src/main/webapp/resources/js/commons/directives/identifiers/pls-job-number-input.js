angular.module('plsApp.directives').directive('plsJobNumberInput', ['plsJobIdRules', '_', function (plsJobIdRules, _) {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            model: '=',
            regexp: '@',
            startWith: '@',
            endWith: '@',
            customerId: '@'
        },
        link: function (scope, elm, attrs, ctrl) {
            var prevValue = '';

            /* Save old value, before edit */
            elm[0].onfocus = function () {
                if (ctrl.$viewValue) {
                    prevValue = _.result(_.find(scope.model, 'jobNumber', ctrl.$viewValue), 'jobNumber');
                }
            };

            /* Trigger blur (saving) on pressing "Enter" */
            elm[0].onkeyup = function (event) {
                if (event.which === 13 || event.keyCode === 13 || event.charCode === 13) {
                    elm[0].blur();
                }
            };

            elm[0].onblur = function () {
                /* Force saving the Alpha characters in all caps */
                var valueUpper = ctrl.$viewValue ? ctrl.$viewValue.toUpperCase() : undefined;
                var valueLower = ctrl.$viewValue ? ctrl.$viewValue.toLowerCase() : undefined;

                /* Validate input data via regexp */
                if (ctrl.$viewValue && ctrl.$viewValue !== '' && (new RegExp(scope.regexp).test(ctrl.$viewValue)
                        || new RegExp(scope.regexp).test(valueUpper) || new RegExp(scope.regexp).test(valueLower))) {

                    ctrl.$setViewValue(valueUpper);
                } else {
                    if (ctrl.$viewValue && ctrl.$viewValue !== '' && !new RegExp(scope.regexp).test(valueUpper)) {
                        if (scope.$root.isSafway(scope.customerId)) {
                            toastr.error(plsJobIdRules.warnText, 'Error!');
                        } else {
                            toastr.warning('Incorrect format of Job#. It should Start with "' + scope.startWith
                                    + '", End with "' + scope.endWith + '"', 'Warning!');
                        }
                    }

                    /* Set previous value */
                    ctrl.$setViewValue(prevValue);

                    /* Remove last row if previous value was empty */
                    if (!prevValue) {
                        var validJobs = [];

                        scope.model.forEach(function (value) {
                            if (value.jobNumber) {
                                validJobs.push(value);
                            }
                        });

                        scope.model = validJobs;
                        scope.$apply();
                    }
                }
            };
        }
    };
}]);