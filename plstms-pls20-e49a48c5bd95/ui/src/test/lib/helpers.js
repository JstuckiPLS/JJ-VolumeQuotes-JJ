/**
 * Contains extended jasmine matcher.
 *
 * @author Sergey Kirichenko
 * Date: 8/8/13
 * Time: 3:28 PM
 */
beforeEach(function() {
    // matcher checks whether element contains defined class
    if (jasmine && jasmine.addMatchers) {
        jasmine.addMatchers({
            toHaveClass: function() {
                return {
                    compare: function(actual, expected) {
                        return {
                            pass: actual.hasClass(expected)
                        };
                    }
                }
            }
        });
    }
});
var google = {load: function(){}};
function input(elem) {
    return new InputModel(elem);
}

function InputModel(elem) {
    this.inp = elem;
}

InputModel.prototype.enter = function(value) {
    if (this.inp) {
        this.inp.val(value);
        this.inp.trigger('input');
        this.inp.trigger('change');
    }
    return this;
};

function element(elem) {
    return new ElementModel(elem);
}

function ElementModel(elem) {
    this.element = elem;
}

ElementModel.prototype.pressEnterKey = function() {
    var enterKeyEvent = jQuery.Event('keydown');
    enterKeyEvent.ctrlKey = false;
    enterKeyEvent.which = 13;

    this.element.trigger(enterKeyEvent);
    return this;
};

function select(elem) {
    return new SelectModel(elem);
}

function SelectModel(elem) {
    this.select = elem;
}

SelectModel.prototype.option = function(value) {
    var selectedOptions = this.select.find('option:selected');
    if (selectedOptions.length) {
        _.each(selectedOptions, function(item) {
            var option = $(item);
            if (option.val() !== value) {
                option.attr('selected', null);
            }
        });
    }
    var options = this.select.find('option[value="' + value + '"]');
    if (options.length) {
        _.each(options, function(item) {
            $(item).attr('selected', 'selected');
        });
        this.select.val(value);
    }
    this.select.trigger('change');
    return this;
};

SelectModel.prototype.optionByLabel = function(label) {
    var item = _.find(this.select.find('option'), function(option) {
        return $(option).text() == label;
    });
    if (item) {
        this.option($(item).val());
    }
};