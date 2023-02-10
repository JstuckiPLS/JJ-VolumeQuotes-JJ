/**
 * Future action to get text from selected option.
 * Selector - any jQuery selector.
 */
angular.scenario.dsl('getOptionText', function() {
  return function (selector) {
    return this.addFutureAction('get selected option text from \'' + selector + '\'', function ($window, $document, done) {
      var $ = $window.$; // jQuery inside the iframe
      var elem = $(selector);
      if (!elem.length) {
        return done('No element matched \'' + selector + '\'.');
      }
      done(null, $(selector + ' option:selected').text());
    });
  };
});