/**
 * Future action to emulate setting checked/isChecked to HTML radio buttons and radio group buttons.
 * Selector - any jQuery selector.
 * Index - index to be selected.
 */
angular.scenario.dsl('checkedValue', function() {
    return function(selector) {
        return this.addFutureAction('set value for \'' + selector + '\'', function($window, $document, done) {
            var $ = $window.$; // jQuery inside the iframe
            var elem = $(selector);

            if (!elem.length) {
                return done('No element matched \'' + selector + '\'.');
            }

            elem.attr("checked", "checked");
            elem.click();

            done();
        });
    };
});

angular.scenario.dsl('isChecked', function() {
    return function(selector) {
        return this.addFutureAction('check value for \'' + selector + '\'', function($window, $document, done) {
            var $ = $window.$;
            var elem = $(selector);
            if (!elem.length) {
                return done('No element matched \'' + selector + '\'.');
            }
            var isChecked = elem.prop('checked') ? true : false;
            done(null, isChecked);
        });
    };
});

angular.scenario.dsl('selectRadioGroupButtonByIndex', function() {
    return function(selector, index) {
        return this.addFutureAction('check value for \'' + selector + '\'', function($window, $document, done) {
            var $ = $window.$;
            var elem = $(selector);
            if (!elem.length) {
                return done('No element matched \'' + selector + '\'.');
            }
            elem.get(index).click();
            done();
        });
    };
});

angular.scenario.dsl('getSelectIndexRadioGroupButton', function() {
    return function(selector) {
        return this.addFutureAction('check value for \'' + selector + '\'', function($window, $document, done) {
            var $ = $window.$;
            var elem = $(selector);
            if (!elem.length) {
                return done('No element matched \'' + selector + '\'.');
            }
            var checkedRadioButton = elem.filter(':checked');
            var selectedIndex = elem.index(checkedRadioButton);
            done(null, selectedIndex);
        });
    };
});