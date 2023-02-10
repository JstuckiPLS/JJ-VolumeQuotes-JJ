angular.module('plsApp.directives').directive('plsBell',['AnnouncementService', '$interval', '$location', 
    function (AnnouncementService, $interval, $location) {
    return {
        restrict: 'A',
        templateUrl: 'pages/tpl/bell.html',
        controller: ['$scope', function ($scope) {
            'use strict';
            $scope.number = 0;
            $scope.active = true;
            var interval = 1000 * 60 * 60 * 2; // every two hours 
            $scope.open = function(){
                $location.path('/my-profile/announcement');
            };

            $scope.isTree = function() {
                var month = new Date().getMonth();
                var day = new Date().getDate();
                return (month === 0 && day <= 1) || (month === 11 && day >= 19);
            };

            function activeBell(number){
                if(number > 0){
                    $scope.number = number;
                    $scope.active = true;
                } else {
                    $scope.active = false;
                }
            }

            function updateAnnouncementsBell(){
                if ($scope.$root.authData.plsUser || $scope.$root.authData.customerUser) {
                    AnnouncementService.getUnread({}, function (data) {
                        activeBell(data[0]);
                    }, function(){
                        activeBell(0);
                    });
                }
            }

            updateAnnouncementsBell();

            $interval(updateAnnouncementsBell, interval);

            $scope.$on('event:updateAnnouncementsBell', updateAnnouncementsBell);
        }]
    };
}]);
