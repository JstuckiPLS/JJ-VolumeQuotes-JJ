/**
 * Future action to check .
 * Selector - any jQuery selector.
 */
angular.scenario.dsl('isElementPresent', function() {
  return function (selector) {
    return this.addFutureAction('element presence \'' + selector + '\'', function ($window, $document, done) {
      var $ = $window.$; // jQuery inside the iframe
      var isPresent = $(selector).length ? true : false;
      done(null, isPresent);
    });
  };
});