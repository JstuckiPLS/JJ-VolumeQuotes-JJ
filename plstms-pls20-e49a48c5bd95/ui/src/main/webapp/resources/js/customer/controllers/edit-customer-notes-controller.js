angular.module('editCustomer').controller('EditCustomerNotesCtrl', ['$scope', '$q', 'CustomerNotesService',
    function ($scope, $q, CustomerNotesService) {
        'use strict';

        $scope.notesInitialized = false;

        function loadNotes() {
            var deferred = $q.defer();
            CustomerNotesService.getCustomerNotes({customerId: $scope.editCustomerModel.customerId}, function (data) {
                deferred.resolve(data);
                $scope.notesInitialized = true;
            }, function () {
                $scope.$root.$emit('event:application-error', 'Customer load failed!',
                        'Can\'t load notes for customer with ID ' + $scope.editCustomerModel.customerId);
                deferred.reject('Can\'t load notes for customer with ID ' + $scope.editCustomerModel.customerId);
            });

            return deferred.promise;
        }

        loadNotes();

        $scope.notesFunctions = {
            loadNotes: loadNotes,
            addNewNote: function (note) {
                var newNote = {
                    createdDate: new Date(),
                    customerId: $scope.editCustomerModel.customerId,
                    username: $scope.authData.fullName,
                    text: note
                };

                var deferred = $q.defer();

                CustomerNotesService.saveCustomerNote({}, newNote, function () {
                    deferred.resolve('Note was successfully added for customer ' + $scope.editCustomerModel.customerName);
                }, function () {
                    deferred.reject('Can\'t add note for customer ' + $scope.editCustomerModel.customerName);
                });

                return deferred.promise;
            }
        };
    }
]);