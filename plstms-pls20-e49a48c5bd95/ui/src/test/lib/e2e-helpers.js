/**
 * Contains extended angular matcher.
 *
 * @author Sergey Kirichenko
 * Date: 8/8/13
 * Time: 3:28 PM
 */
angular.scenario.dsl('angularElement', function() {
    return function(selector) {
        return this.addFutureAction('set value for \'' + selector + '\'', function($window, $document, done) {
            var $ = $window.$; // jQuery inside the iframe
            var elem = $(selector);
            if(!elem.length) {
                elem = $document.elements('[ng\\:model="$1"]', selector);
            }
            if (!elem.length) {
                return done('No element matched \'' + selector + '\'.');
            }
            done(null, elem);
        });
    };
});
angular.scenario.dsl('typeahead', function() {
    var chain = {};

    chain.enter = function(value, event) {
        return this.addFutureAction("typeahead '" + this.name + "' enter '" + value + "'", function($window, $document, done) {
            var input = $document.elements('[data-pls-zip-search="$1"]', this.name).filter(':input');
            input.val(value);
            if (event) {
                input.trigger(event);
            } else {
                input.trigger('input');
                input.trigger('change');
            }
            done();
        });
    };

    chain.val = function() {
        return this.addFutureAction("return typeahead val", function($window, $document, done) {
            var input = $document.elements('[data-pls-zip-search="$1"]', this.name).filter(':input');
            done(null, input.val());
        });
    };

    chain.elem = function() {
        return this.addFutureAction("return typeahead val", function($window, $document, done) {
            var input = $document.elements('[data-pls-zip-search="$1"]', this.name).filter(':input');
            done(null, input);
        });
    };

    return function(name) {
        this.name = name;
        return chain;
    };
});

angular.scenario.dsl('progressiveSearch', function(modelName) {
    var chain = {};

    chain.enter = function(value, event) {
      return this.addFutureAction("Progressive search model '"  + this.name + "' enter '" + value + "'", function($window, $document, done) {
          var input = $document.elements(this.name);
          input.val(value);
          if (event) {
              input.trigger(event);
          } else {
              input.trigger('input');
              input.trigger('change');
          }
          done();
      });
    };

    chain.select = function() {
        return this.addFutureAction("Progressive search model '"  + this.name + "' select first element", function($window, $document, done) {
            var dropDiv = $document.elements('div.dropdown');
            dropDiv.find('ul.dropdown-menu > li > a').first().click();
            done();
        });
    };

    return function(name) {
        this.name = name;
        return chain;
    };
});

angular.scenario.dsl('productList', function() {
    var chain = {};

    chain.showList = function() {
        return this.addFutureAction("productList '" + this.name + "' show", function($window, $document, done) {
            var parentDiv = $document.elements('[data-pls-product-list="$1"]', this.name);
            parentDiv.find(':button').click();
            done();
        });
    };

    chain.selectProduct = function(productName) {
        return this.addFutureAction("productList '" + this.name + "' select '" + productName + "'", function($window, $document, done) {
            var parentDiv = $document.elements('[data-pls-product-list="$1"]', this.name);
            parentDiv.find('a:contains(' + productName + ')').click();
            done();
        });
    };

    return function(name) {
        this.name = name;
        return chain;
    };
});
angular.scenario.matcher('toHaveClass', function(expected) {
    return this.actual.hasClass(expected);
});
