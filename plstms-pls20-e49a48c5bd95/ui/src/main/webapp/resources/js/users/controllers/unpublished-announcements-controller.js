angular.module('announcements.controllers').controller('UnpublishedAnnouncementsCtrl', [ '$scope', 'NgGridPluginFactory', 'AnnouncementService',
    function ($scope, NgGridPluginFactory, AnnouncementService) {
        'use strict';

        $scope.today = $scope.$root.formatDate(new Date());
        $scope.announcement = undefined;
        $scope.selectedAnnouncement = [];

        $scope.showAddEditAnnouncementsPanel = false;
        $scope.showDeleteAnnouncementDialog = false;

        $scope.unpublishedAnnouncementsGrid = {
            data: 'unpublishedAnnouncements',
            selectedItems: $scope.selectedAnnouncement,
            columnDefs: [{
                field: 'createdBy',
                displayName: 'Created By',
                width: '15%'
            }, {
                field: 'modifiedBy',
                displayName: 'Last Modified',
                width: '15%'
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
                width: '10%'
            }, {
                field: 'text',
                displayName: 'Text',
                width: '40%'
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            multiSelect: false
        };

        function loadUnpublishedAnnouncements () {
            AnnouncementService.getUnpublished({}, function (data) {
                $scope.unpublishedAnnouncements = data;
            });
        }

        function clearAnnouncement() {
            $scope.selectedAnnouncement.length = 0;
            delete $scope.announcement;
        }

        loadUnpublishedAnnouncements();

        $scope.cancelManageAnnouncementDialog = function() {
            $scope.showAddEditAnnouncementsPanel = false;
            delete $scope.announcement;
        };

        $scope.editAnnouncement = function () {
            AnnouncementService.get({announcementId: $scope.selectedAnnouncement[0].id}, function (data) {
                $scope.announcement = data;
                $scope.showAddEditAnnouncementsPanel = true;
            });
        };

        $scope.saveAnnouncement = function () {
            AnnouncementService.save({}, $scope.announcement, function() {
                $scope.$emit('event:operation-success', "Success", "Announcement has been save successfully");
                loadUnpublishedAnnouncements();
                $scope.showAddEditAnnouncementsPanel = false;
                clearAnnouncement();
            }, function(error) {
                $scope.$emit('event:application-error', "Failure", "Announcement add failed!");
            });
        };

        $scope.deleteAnnouncement = function() {
            AnnouncementService.deleteById({announcementId: $scope.selectedAnnouncement[0].id}, function () {
                $scope.$emit('event:operation-success', "Success", "Announcement has been delete successfully");
                clearAnnouncement();
                loadUnpublishedAnnouncements();
                $scope.showDeleteAnnouncementDialog = false;
            });
        };

        $scope.publishAnnouncement = function() {
            AnnouncementService.publish({announcementId: $scope.selectedAnnouncement[0].id}, function () {
                $scope.$emit('event:operation-success', "Success", "Announcement has been published successfully");
                clearAnnouncement();
                loadUnpublishedAnnouncements();
            });
        };
    }
]);