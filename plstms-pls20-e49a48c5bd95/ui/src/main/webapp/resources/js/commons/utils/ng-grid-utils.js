angular.module('plsApp').service('NgGridService', function() {
    return {
        refreshGrid: function(gridOptions) {
            if (gridOptions && gridOptions.ngGrid) {
                gridOptions.ngGrid.buildColumns();
            }
        }
    };
});