/**
 * Created by Vitaliy Gavrilyuk on 10/1/2015.
 */
angular.module('plsApp').factory('copyExactProperties', [
    function () {
        return function (objFrom, props, objTo) {
            props.forEach(function (prop) {
                if (angular.isDefined(objFrom[prop])) {
                    objTo[prop] = objFrom[prop];
                }
            });

            return objTo;
        };
    }
]);