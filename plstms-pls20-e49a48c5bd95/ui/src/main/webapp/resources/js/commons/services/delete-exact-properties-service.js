/**
 * Created by Vitaliy Gavrilyuk on 10/1/2015.
 */
angular.module('plsApp').factory('deleteExactProperties', [
    function () {
        return function (obj, props) {
            props.forEach(function (prop) {
                if (angular.isDefined(obj[prop])) {
                    delete obj[prop];
                }
            });

            return obj;
        };
    }
]);