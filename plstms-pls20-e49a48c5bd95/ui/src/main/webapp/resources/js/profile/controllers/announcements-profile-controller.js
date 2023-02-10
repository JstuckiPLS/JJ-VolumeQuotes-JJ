angular.module('myProfile.controllers')
    .controller('AnnouncementsProfileCtrl', ['$scope', 'NgGridPluginFactory', 'AnnouncementService',
                                   function ($scope, NgGridPluginFactory, AnnouncementService) {
     $scope.fromDate = $scope.$root.formatDate(new Date(new Date().setMonth(new Date().getMonth() -1)));
     $scope.toDate = $scope.$root.formatDate(new Date());
     $scope.maxToDate = $scope.$root.formatDate(new Date());
     $scope.selectedEntries = [];
     $scope.announcementsEntries =[];

     $scope.searchAllAnnouncementsEntries = function() {
         AnnouncementService.getAllAnnouncements({
                 from : $scope.fromDate,
                 to : $scope.toDate,
                 status:['PUBLISHED']
             }, function(data) {
                 $scope.announcementsEntries = data;
             });
         };

     function markAnnouncementAsRead(entity) {
         AnnouncementService.markAsRead({
             announcementId : entity.id
         }, function() {
             $scope.$emit('event:updateAnnouncementsBell');
             entity.isAnnouncementRead = true;
         });
     }

     $scope.isAnnouncementActual = function(entity) {
         var currentDate = new Date();
         var endDate = new Date(entity.endDate);
         var startDate = new Date(entity.startDate);
         return currentDate < endDate && currentDate > startDate ;
      };


     $scope.announcementsGrid = {
         enableColumnResize : true,
         data : 'announcementsEntries',
         multiSelect : false,
         selectedItems : $scope.selectedEntries,
         columnDefs : [ {
             cellTemplate: '<div class="ngCellText" data-ng-if="isAnnouncementActual(row.entity)"><i '
                 + 'class="fa fa-bell-o">'
                 + '</i></div>',
             searchable: false
         }, {
             field : 'startDate',
             displayName : 'Publishing Date'
         }, {
             field : 'announcer',
             displayName : 'Announcer',
             cellText: 'red'
         }, {
             field : 'theme',
             displayName : 'Theme'
         }, {
             field : 'text',
             displayName : 'Text'
         } ],
         rowTemplate: '<div style="height: 100%", ngRow: true}">' +
         '<div ng-style="{ \'cursor\': row.cursor }" ng-repeat="col in renderedColumns" ng-class="col.colIndex()" class="ngCell">' +
         '<div class="ngVerticalBar" ng-style="{height: rowHeight}" ng-class="{ ngVerticalBarVisible: !$last }"> </div>' +
         '<div ng-cell ng-style=\'{ "color": row.entity.isAnnouncementRead ? "black" : "red" }\'></div></div></div>',
         plugins : [ NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin() ],
         afterSelectionChange: function (row) {
             if (!row.entity.isAnnouncementRead) {
                 markAnnouncementAsRead(row.entity);
             }
         },
         sortInfo: {
             fields: ['startDate'],
             directions: ['desc']
         },
         progressiveSearch : true
     };

     $scope.searchAllAnnouncementsEntries();
}]);