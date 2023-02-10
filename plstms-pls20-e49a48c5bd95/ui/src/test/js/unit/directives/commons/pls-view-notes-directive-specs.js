/**
 * Tests zip search directive.
 *
 * @author Sergey Kirichenko
 */
describe('PLS View Notes (pls-view-notes-directive) Directive Test.', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;
    var timeout;
    var angularQ;
    //date filter
    var filter;
    //note date
    var noteDate = Date.UTC(2013, 7, 15, 12, 45, 15);
    //controls
    var noteLabel, grid, addButon, detailsPanel, dateLabel, userLabel, noteText;
    //add note controls
    var addDialog, newNoteText, textCounterLabel, cancelButton, addNoteButton;

    var originalNotes = [
        {createdDate: noteDate, username: 'User 1', text: 'Note 1'},
        {createdDate: noteDate, username: 'User 2', text: 'Note 2'},
        {createdDate: noteDate, username: 'User 3', text: 'Note 3'}
    ];

    var notes = [];

    var mockService = {
        loadNotes : function() {
            return angularQ.when(notes);
        },
        addNewNote : function(newNote) {
            return angularQ.when(notes.push({createdDate: noteDate, username: 'User New', text: newNote}));
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/view-notes-tpl.html', 'pages/tpl/pls-add-note-tpl.html'));

    describe('Default behaviour.', function() {

        beforeEach(inject(function($rootScope, $compile, $document, dateFilter, $timeout, $q) {
            filter = dateFilter;
            timeout = $timeout;
            angularQ = $q;
            elm = angular.element('<div id="directiveHolder" style="width: 800px;">'
                + '                 <div data-pls-view-notes="testObject.notesFunctions" '
                + '                 data-element-for-dialog="addNotesDialogHolder" '
                + '                 data-hide-label="{{testObject.hideLabel}}" '
                + '                 data-hide-button-add="testObject.hideButtonAdd">'
                + '                 </div><div id="addNotesDialogHolder"></div></div>');
            scope = $rootScope.$new();
            notes = originalNotes.slice(0);
            scope.$apply(function() {
                scope.testObject = {
                    notesFunctions: mockService,
                    hideLabel: undefined,
                    hideButtonAdd: undefined
                };
            });
            $document.find('body').append($compile(elm)(scope));
            scope.$digest();
            noteLabel = elm.find('div[data-ng-hide="hideLabel"]');
            grid = elm.find('div.gridStyle.gridHeight4.span12');
            addButon = elm.find('button[data-ng-click="openAddNewNoteDialog()"]');
            detailsPanel = elm.find('div[data-ng-show="pageNoteModel.selectedNotes.length == 1"]');
            dateLabel = detailsPanel.find('div.row-fluid div.span3 label.control-label');
            userLabel = detailsPanel.find('div.row-fluid div.span9 label.control-label');
            noteText = detailsPanel.find('textarea');
            addDialog = elm.find('div[data-pls-modal="pageNoteModel.showAddNoteDialog"]');
            newNoteText = addDialog.find('textarea');
            textCounterLabel = addDialog.find('div.text-right.span1.offset11.muted');
            cancelButton = addDialog.find('button.btn.cancel');
            addNoteButton = addDialog.find('button.btn.btn-primary');
        }));

        afterEach(inject(function($document) {
            $document.find('div#directiveHolder').remove();
        }));

        it('should show notes', function() {
            timeout.flush();
            c_expect(noteLabel).to.exist;
            c_expect(noteLabel).not.to.have.css('display', 'none');
            c_expect(grid).to.exist;
            c_expect(grid).to.have('div[ng-row]');
            var rows = grid.find('div[ng-row]');
            c_expect(rows.length).to.equal(3);
            //check first row
            c_expect(rows.eq(0)).to.have('div[ng-cell] span');
            var columns = rows.eq(0).find('div[ng-cell] span');
            c_expect(columns.length).to.equal(3);
            c_expect(columns.eq(0)).to.have.text(filter(noteDate, scope.$root.appDateTimeFormat));
            c_expect(columns.eq(1)).to.have.text('User 1');
            c_expect(columns.eq(2)).to.have.text('Note 1');
            c_expect(addButon).to.exist;
            c_expect(addButon).not.to.have.css('display', 'none');
            c_expect(detailsPanel).to.exist;
            c_expect(detailsPanel).not.to.have.css('display', 'none');
            c_expect(dateLabel).to.exist;
            c_expect(dateLabel).to.have.text(filter(noteDate, scope.$root.appDateTimeFormat));
            c_expect(userLabel).to.exist;
            c_expect(userLabel).to.have.text('User 1');
            c_expect(noteText).to.exist;
            c_expect(noteText).to.have.value('Note 1');
            c_expect(addDialog).to.exist;
            c_expect(addDialog).not.to.have.class('in');
        });

        it('should select second note', function() {
            timeout.flush();
            //check selection first row
            c_expect(detailsPanel).not.to.have.css('display', 'none');
            c_expect(dateLabel).to.have.text(filter(noteDate, scope.$root.appDateTimeFormat));
            c_expect(userLabel).to.have.text('User 1');
            c_expect(noteText).to.have.value('Note 1');
            //check second row value
            c_expect(grid).to.have('div[ng-row]');
            var rows = grid.find('div[ng-row]');
            c_expect(rows.length).to.equal(3);
            c_expect(rows.eq(1)).to.have('div[ng-cell] span');
            var columns = rows.eq(1).find('div[ng-cell] span');
            c_expect(columns.length).to.equal(3);
            c_expect(columns.eq(0)).to.have.text(filter(noteDate, scope.$root.appDateTimeFormat));
            c_expect(columns.eq(1)).to.have.text('User 2');
            c_expect(columns.eq(2)).to.have.text('Note 2');
            rows.eq(1).click();
            //check second row was selected
            c_expect(detailsPanel).not.to.have.css('display', 'none');
            c_expect(dateLabel).to.have.text(filter(noteDate, scope.$root.appDateTimeFormat));
            c_expect(userLabel).to.have.text('User 2');
            c_expect(noteText).to.have.value('Note 2');
        });

        it('should show add dialog', function() {
            timeout.flush();
            c_expect(addDialog).not.to.have.class('in');
            addButon.click();
            c_expect(addDialog).to.have.class('in');
            c_expect(newNoteText).to.exist;
            c_expect(newNoteText).not.to.have.text();
            c_expect(newNoteText).to.have.class('ng-invalid');
            c_expect(textCounterLabel).to.exist;
            c_expect(textCounterLabel).to.contain('1500');
            c_expect(cancelButton).to.exist;
            c_expect(addNoteButton).to.exist;
            c_expect(addNoteButton).to.be.disabled;
            cancelButton.click();
            c_expect(addDialog).not.to.have.class('in');
        });

        it('should add new note', function() {
            spyOn(mockService, 'addNewNote').and.callThrough();
            timeout.flush();
            c_expect(addDialog).not.to.have.class('in');
            addButon.click();
            c_expect(addDialog).to.have.class('in');
            input(newNoteText).enter('Test new note.');
            c_expect(newNoteText).not.to.have.class('ng-invalid');
            c_expect(textCounterLabel).to.contain('1486');
            c_expect(addNoteButton).not.to.be.disabled;
            addNoteButton.click();
            c_expect(addDialog).not.to.have.class('in');
            c_expect(grid).to.have('div[ng-row]');
            var rows = grid.find('div[ng-row]');
            c_expect(rows.length).to.equal(4);
            c_expect(rows.eq(3)).to.have('div[ng-cell] span');
            var columns = rows.eq(3).find('div[ng-cell] span');
            c_expect(columns.length).to.equal(3);
            c_expect(columns.eq(0)).to.have.text(filter(noteDate, scope.$root.appDateTimeFormat));
            c_expect(columns.eq(1)).to.have.text('User New');
            c_expect(columns.eq(2)).to.have.text('Test new note.');
            c_expect(mockService.addNewNote.calls.count()).to.equal(1);
            c_expect(mockService.addNewNote.calls.mostRecent().args).to.eql(['Test new note.']);
        });

        it('should hide note label', function() {
            timeout.flush();
            scope.$apply(function() {
                scope.testObject.hideLabel = true;
            });
            c_expect(noteLabel).to.have.css('display', 'none');
            scope.$apply(function() {
                scope.testObject.hideLabel = false;
            });
            c_expect(noteLabel).not.to.have.css('display', 'none');
        });

        it('should hide add button', function() {
            timeout.flush();
            scope.$apply(function() {
                scope.testObject.hideButtonAdd = true;
            });
            c_expect(addButon).to.have.css('display', 'none');
            scope.$apply(function() {
                scope.testObject.hideButtonAdd = false;
            });
            c_expect(addButon).not.to.have.css('display', 'none');
        });
    });

    describe('Length limitation.', function() {
        beforeEach(inject(function($rootScope, $compile, $document, $q) {
            angularQ = $q;
            elm = angular.element('<div id="directiveHolder" style="width: 800px;">'
                        + '                 <div data-pls-view-notes="testObject.notesFunctions" '
                        + '                 data-element-for-dialog="addNotesDialogHolder" data-max-length="10">'
                        + '                 </div><div id="addNotesDialogHolder"></div></div>');
            scope = $rootScope.$new();
            scope.$apply(function() {
                scope.testObject = {
                    notesFunctions: mockService,
                    hideLabel: undefined,
                    hideButtonAdd: undefined
                };
            });
            $document.find('body').append($compile(elm)(scope));
            scope.$digest();
            addButon = elm.find('button[data-ng-click="openAddNewNoteDialog()"]');
            addDialog = elm.find('div[data-pls-modal="pageNoteModel.showAddNoteDialog"]');
            newNoteText = addDialog.find('textarea');
            textCounterLabel = addDialog.find('div.text-right.span1.offset11.muted');
            addNoteButton = addDialog.find('button.btn.btn-primary');
        }));

        afterEach(inject(function($document) {
            $document.find('div#directiveHolder').remove();
        }));

        it('should validate max length', function() {
            c_expect(addDialog).not.to.have.class('in');
            addButon.click();
            c_expect(addDialog).to.have.class('in');
            c_expect(addNoteButton).to.be.disabled;
            c_expect(newNoteText).to.have.class('ng-invalid');
            c_expect(textCounterLabel).to.contain('10');
            c_expect(textCounterLabel).not.to.have.class('text-error');
            input(newNoteText).enter('Note');
            c_expect(addNoteButton).not.to.be.disabled;
            c_expect(newNoteText).not.to.have.class('ng-invalid');
            c_expect(textCounterLabel).to.contain('6');
            c_expect(textCounterLabel).not.to.have.class('text-error');
            input(newNoteText).enter('Very big note.');
            c_expect(addNoteButton).to.be.disabled;
            c_expect(newNoteText).not.to.have.class('ng-invalid');
            c_expect(textCounterLabel).to.contain('-4');
            c_expect(textCounterLabel).to.have.class('text-error');
        });
    });

});