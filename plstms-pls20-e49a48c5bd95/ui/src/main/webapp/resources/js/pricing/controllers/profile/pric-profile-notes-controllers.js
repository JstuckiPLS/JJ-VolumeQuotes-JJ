angular.module('plsApp').controller('ShowProfileNotesCtrl', ['$scope', '$route', 'ProfileNotesService', 'NgGridPluginFactory',
    function ($scope, $route, ProfileNotesService, NgGridPluginFactory) {
        'use strict';

        $scope.note = {};

        if (!$scope.profileDetails) {
            $scope.profileDetails = {
                id: $route.current.params.pricingId
            };
        }

        $scope.loadNotes = function () {
            ProfileNotesService.getNotes({id: $scope.profileDetails.id}, function (notes) {
                $scope.notesList = notes;
            });
        };

        $scope.loadNotes();

        $scope.gridOptions = {
            enableColumnResize: true,
            multiSelect: false,
            data: 'notesList',
            columnDefs: [{
                field: 'createdDate',
                displayName: 'Date & Time',
                width: '20%',
                cellFilter: 'date:appDateTimeFormat'
            }, {
                field: 'createdBy',
                displayName: 'Author',
                width: '20%'
            }, {
                field: 'note',
                displayName: 'Note',
                width: '59%',
                cellTemplate: '<div>{{COL_FIELD}}</div>'
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            refreshTable: $scope.loadNotes
        };

        $scope.submit = function () {
            $scope.note.profileId = $scope.profileDetails.id;

            ProfileNotesService.save({id: $scope.profileDetails.id}, $scope.note, function () {
                $scope.loadNotes();
                $scope.note = {};
                $scope.$root.$emit('event:operation-success', 'Profile notes successfully saved');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Profile notes save failed!');
            });
        };
    }
]);
