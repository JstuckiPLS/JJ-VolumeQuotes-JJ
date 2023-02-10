angular.module('pls.controllers').controller('SONotesCtrl', ['$scope', 'ShipmentNotesService', 'NgGridPluginFactory',
    function ($scope, ShipmentNotesService, NgGridPluginFactory) {
        'use strict';

        $scope.notesModel = {
            selectedNotes: [],
            currentNote: '',
            appendMode: false,
            editMode: false
        };

        function fetchNotesList() {
            if (_.isUndefined($scope.wizardData.shipment.id)) {
                return;
            }
            $scope.notesModel.editMode = false;
            $scope.notesModel.appendMode = false;

            ShipmentNotesService.findShipmentNotes({
                customerId: $scope.authData.organization.orgId,
                shipmentId: $scope.wizardData.shipment.id
            }, function (data) {
                $scope.wizardData.shipment.notes = data;
            }, function (error) {
                $scope.$emit('event:application-error', "Failed to load shipment notes", error.toString());
            });
        }

        function initNotesModel() {
            if ($scope.wizardData.breadCrumbs && $scope.wizardData.breadCrumbs.map) {
                var stepObject = $scope.wizardData.breadCrumbs.map.notes;
                stepObject.validNext = function() {
                    return !$scope.notesModel.appendMode;
                };
            }
            fetchNotesList();
        }

        initNotesModel();

        $scope.notesGridOptions = {
            enableColumnResize: true,
            data: 'wizardData.shipment.notes',
            columnDefs: 'notesGridColumnDefinition',
            refreshTable: fetchNotesList,
            sortInfo: {fields: ['createdDate'], directions: ['desc']},
            multiSelect: false,
            selectedItems: $scope.notesModel.selectedNotes,
            plugins: [NgGridPluginFactory.plsGrid()],
            progressiveSearch: false,

            afterSelectionChange: function (rowItem) {
                if (!_.isUndefined($scope.notesModel.currentNote.text)) {
                    $scope.notesModel.currentNote = rowItem.entity;
                } else {
                    $scope.notesModel.currentNote.text = $scope.notesModel.tempNoteText;
                }

                $scope.notesModel.editMode = false;
                $scope.notesModel.appendMode = false;
            }
        };

        $scope.notesGridColumnDefinition = [{
            field: 'createdDate',
            displayName: 'Date & Time',
            width: '24%',
            cellFilter: 'date:$root.appDateTimeFormat'
        }, {
            field: 'username',
            displayName: 'Author',
            width: '20%'
        }, {
            field: 'text',
            displayName: 'Note',
            width: '55%'
        }];

        $scope.$on('event:note-tab-fetchNotesList', fetchNotesList);

        $scope.addNote = function () {
            if (!_.isUndefined($scope.notesModel.currentNote.text) && $scope.notesModel.appendMode) {
                var note = {
                    createdDate: new Date(),
                    username: $scope.authData.fullName,
                    text : $scope.notesModel.holdMode ? "Financial Pause Note: "
                            + $scope.notesModel.currentNote.text : $scope.notesModel.currentNote.text
                };

                $scope.wizardData.shipment.notes.push(note);
            }

            $scope.notesModel.appendMode = false;
        };

        $scope.saveNote = function () {
            if ($scope.notesModel.appendMode) {
                $scope.addNote();
            } else if ($scope.notesModel.editMode) {
                $scope.notesModel.editMode = false;
            }

            $scope.notesModel.currentNote = {};
            $scope.notesGridOptions.selectAll(false);
        };

        $scope.newNote = function () {
            $scope.notesModel.currentNote = {};
            $scope.notesModel.appendMode = true;
            $scope.notesModel.holdMode = false;
        };

        $scope.cancelNote = function() {
            $scope.notesModel.currentNote.text = $scope.notesModel.tempNoteText;
            $scope.notesModel.editMode = false;
            $scope.notesModel.appendMode = false;
            if($scope.notesModel.holdMode) {
                $scope.wizardData.shipment.holdFinalizationStatus = false;
            }
        };

        $scope.pauseFinancials = function () {
            $scope.$root.$emit('event:application-warning', 'Pause Financials',
                    'You are about to withhold this shipment from the Finance processes that kickoff after delivery. '+
                    'This should only be used if there is a severe issue with the shipment. '+
                    '<br> Please leave a note detailing why you paused this shipment');
            $scope.wizardData.shipment.holdFinalizationStatus = true;
            $scope.notesModel.holdMode = true;
            $scope.notesModel.currentNote = {};
            $scope.notesModel.appendMode = true;
        };

        $scope.allowFinancials = function () {
            $scope.wizardData.shipment.holdFinalizationStatus = false;
        };

        $scope.editNote = function () {
            $scope.notesModel.currentNote = $scope.notesModel.selectedNotes[0];
            $scope.notesModel.tempNoteText = angular.copy($scope.notesModel.selectedNotes[0].text);
            $scope.notesModel.editMode = true;
        };

        $scope.enableTextArea = function () {
            return $scope.notesModel.editMode || $scope.notesModel.appendMode;
        };

        $scope.$on('event:operation-success', function (event, data) {
            if (data === 'Save sales order') {
                fetchNotesList();
            }
        });
        $scope.$on('event:edit-sales-order-tab-close', fetchNotesList);
    }
]);