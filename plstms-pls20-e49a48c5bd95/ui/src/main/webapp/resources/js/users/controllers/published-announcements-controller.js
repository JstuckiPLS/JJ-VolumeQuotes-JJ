angular.module('announcements.controllers').controller('PublishedAnnouncementsCtrl', [ '$scope', 'NgGridPluginFactory', 'AnnouncementService',
    function ($scope, NgGridPluginFactory, AnnouncementService) {
        'use strict';

        $scope.fromDate = $scope.$root.formatDate(new Date(new Date().setMonth(new Date().getMonth() -1)));
        $scope.toDate = $scope.$root.formatDate(new Date());

        $scope.searchAnnouncement = function(){
            AnnouncementService.getAllAnnouncements({from:$scope.fromDate, to:$scope.toDate, status:['PUBLISHED','CANCEL']}, function (data) {
                $scope.announcements = data;
            });
        };
        $scope.searchAnnouncement();

        $scope.announcements =[];
        $scope.selectedAnnouncements = [];
        $scope.publishedGrid = {
                data: 'announcements',
                selectedItems: $scope.selectedAnnouncements,
                columnDefs: [{
                    cellTemplate: '<div class="ngCellText" data-ng-if="isAnnouncementActual(row.entity)"><i '
                        + 'class="fa fa-bell-o">'
                        + '</i></div>'
                        + '<div class="ngCellText" style="color:red" data-ng-if="row.entity.status === \'CANCEL\'"><i '
                        + 'class="fa fa-minus-circle">'
                        + '</i></div>',
                    searchable: false,
                    width: '5%'
                }, {
                    field: 'startDate',
                    displayName: 'Start Date',
                    width: '10%'
                }, {
                    field: 'endDate',
                    displayName: 'End Date',
                    width: '10%'
                }, {
                    field: 'theme',
                    displayName: 'Theme',
                    width: '15%'
                }, {
                    field: 'createdBy',
                    displayName: 'Created By',
                    width: '15%'
                }, {
                    field: 'announcer',
                    displayName: 'Announcer',
                    width: '15%'
                }, {
                    field: 'text',
                    displayName: 'Text',
                    width: '29%',
                    searchable: false
                }],
                plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
                multiSelect: false,
                progressiveSearch: true,
                afterSelectionChange: function (row) {
                    if(new Date(row.entity.endDate) < new Date() || row.entity.status === 'CANCEL'){
                        $scope.hideDelete = false;
                    } else{
                        $scope.hideDelete = true;
                    }
                },
                sortInfo: {
                    fields: ['startDate'],
                    directions: ['desc']
                }
            };

            $scope.isAnnouncementActual = function(entity) {
                if(entity.status === 'CANCEL'){
                    return false;
                }
                var currentDate = new Date();
                var endDate = new Date(entity.endDate);
                var startDate = new Date(entity.startDate);
                return currentDate < endDate && currentDate > startDate ;
             };


            $scope.copyAnnouncement = function () {
                AnnouncementService.copy({announcementId:$scope.selectedAnnouncements[0].id}, function () {
                        $scope.$emit('event:operation-success', "Success", "Announcement has been copied successfully");
                    }, function(error) {
                        $scope.$emit('event:application-error', "Failure", "Announcement copy failed!");
                    });
            };

            $scope.cancelAnnouncement = function () {
                AnnouncementService.cancel({announcementId:$scope.selectedAnnouncements[0].id}, function () {
                    $scope.searchAnnouncement();
                    $scope.selectedAnnouncements.length = 0;
                    $scope.$emit('event:operation-success', "Success", "Announcement has been canceled successfully");
                }, function(error) {
                    $scope.$emit('event:application-error', "Failure", "Announcement cancel failed!");
                });
            };

            $scope.deleteAnnouncement = function() {
                AnnouncementService.deleteById({announcementId:$scope.selectedAnnouncements[0].id}, function () {
                    $scope.searchAnnouncement();
                    $scope.selectedAnnouncements.length = 0;
                    $scope.$emit('event:operation-success', "Success", "Announcement has been deleted successfully");
                }, function(error) {
                    $scope.$emit('event:application-error', "Failure", "Announcement delete failed!");
                });
            };

    }
]);