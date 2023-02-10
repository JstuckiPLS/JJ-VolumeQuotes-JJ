/**
 * AngularJS directive that provides view notes functionality.
 *
 * @author: Sergey Kirichenko
 * Date: 7/9/13
 * Time: 2:34 PM
 */
angular.module('plsApp.directives').directive('plsViewNotes', ['$templateCache', '$compile', '$timeout', '$http', 'NgGridPluginFactory',
    function ($templateCache, $compile, $timeout, $http, NgGridPluginFactory) {
        return {
            restrict: 'A',
            scope: {
                notesFunctions: '=plsViewNotes',
                elementForDialog: '@',
                parentDialog: '@',
                maxLength: '@',
                hideLabel: '@',
                hideButtonAdd: '='
            },
            replace: true,
            templateUrl: 'pages/tpl/view-notes-tpl.html',
            compile: function () {
                return {
                    pre: function (scope, element, attrs) {
                        'use strict';

                        var MAX_LENGTH = 1500;

                        scope.pageNoteModel = {
                            selectedNotes: [],
                            lengthCounter: attrs.maxLength || MAX_LENGTH,
                            maxLength: attrs.maxLength || MAX_LENGTH
                        };

                        scope.pageNoteModel.loadData = function () {
                            if (scope.notesFunctions.loadNotes && angular.isFunction(scope.notesFunctions.loadNotes)) {
                                scope.notesFunctions.loadNotes().then(function (data) {
                                    scope.pageNoteModel.notesList = data;

                                    $timeout(function () {
                                        scope.notesGridOptions.selectRow(0, true);
                                    }, 50);
                                }, function (reason) {
                                    scope.$root.$emit('event:application-error', 'Notes load failed!', reason || 'Unknown error.');
                                });
                            }
                        };

                        scope.pageNoteModel.showAddNoteDialog = false;

                        scope.pageNoteModel.addNoteModalOptions = {
                            parentDialog: attrs.parentDialog
                        };

                        scope.notesGridColumnDefinition = [];

                        scope.notesGridOptions = {
                            enableColumnResize: true,
                            data: 'pageNoteModel.notesList',
                            columnDefs: 'notesGridColumnDefinition',
                            refreshTable: scope.pageNoteModel.loadData,
                            sortInfo: {fields: ['createdDate'], directions: ['desc']},
                            multiSelect: false,
                            selectedItems: scope.pageNoteModel.selectedNotes,
                            plugins: [NgGridPluginFactory.plsGrid()],
                            progressiveSearch: false
                        };

                        scope.openAddNewNoteDialog = function () {
                            scope.pageNoteModel.newNote = undefined;
                            scope.pageNoteModel.showAddNoteDialog = true;
                        };

                        scope.closeAddNoteDialog = function () {
                            scope.pageNoteModel.showAddNoteDialog = false;
                        };

                        scope.addNewNote = function () {
                            if (scope.notesFunctions.addNewNote && angular.isFunction(scope.notesFunctions.addNewNote)) {
                                scope.notesFunctions.addNewNote(scope.pageNoteModel.newNote).then(function () {
                                    scope.closeAddNoteDialog();
                                    scope.notesGridOptions.refreshTable();
                                }, function (reason) {
                                    scope.$root.$emit('event:application-error', 'Note add failed!', reason || 'Unknown error.');
                                });
                            }
                        };

                        scope.$watch('pageNoteModel.newNote', function () {
                            scope.pageNoteModel.lengthCounter = scope.pageNoteModel.newNote ?
                                    (scope.pageNoteModel.maxLength - scope.pageNoteModel.newNote.length) : scope.pageNoteModel.maxLength;
                        });

                        scope.$on('event:force-refresh-notes', scope.pageNoteModel.loadData);

                        $http.get('pages/tpl/pls-add-note-tpl.html', {cache: $templateCache}).then(function (result) {
                            var dialog = $compile(result.data)(scope);
                            var elementSelector = attrs.elementForDialog ? '#' + attrs.elementForDialog : '#content';
                            angular.element(elementSelector).append(dialog);

                            scope.$on('$destroy', function () {
                                dialog.remove();
                            });
                        });
                    },
                    post: function (scope) {
                        'use strict';

                        scope.notesGridColumnDefinition = [{
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

                        scope.pageNoteModel.notesList = [];
                        scope.pageNoteModel.addNoteModalOptions.parentDialog = scope.parentDialog;
                        scope.pageNoteModel.loadData();
                    }
                };
            }
        };
    }
]);