/**
 * Future action to get text from selected option.
 * text - inner text of the element.
 */
angular.scenario.dsl('clickElementByText', function() {
  return function (tagName, text) {
    return this.addFutureAction('get first \'' + tagName + '\' element by inner text \'' + text + '\'', function ($window, $document, done) {
      var $ = $window.$; // jQuery inside the iframe

      var tags = document.getElementsByTagName(tagName);

      for (var i = 0; i < tags.length; i++) {
        if (tags[i].textContent == text) {
          tags[i].click();
          break;
        }
      }

      done();
    });
  };
});