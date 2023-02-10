/**
 * Linked list implementation.
 *
 * @author: Alexander Kirichenko
 */

angular.module('plsApp.utils').factory('LinkedListUtils', function () {
    return {
        getLinkedList: function () {
            function LinkedList() {
                this._first = null;
                this._last = null;
                this._iter = null;
                this._size = 0;
            }

            LinkedList.prototype.Node = function (data) {
                this._data = data;
                this._next = null;
                this._prev = null;
            };

            LinkedList.prototype.isEmpty = function () {
                return this._size === 0;
            };

            LinkedList.prototype.add = function (data) {
                var newNode = new this.Node(data);

                if (this._first === null) {
                    this._first = newNode;
                    this._last = newNode;
                } else {
                    this._last._next = newNode;
                    newNode._prev = this._last;
                    this._last = newNode;
                }

                this._size += 1;
            };

            LinkedList.prototype.remove = function (data) {
                if (!this.isEmpty()) {
                    var curNode = this._first;

                    do {
                        if (curNode._data === data) {
                            if (this._size === 1) {
                                this._first = null;
                                this._last = null;
                                this._iter = null;
                                this._size = 0;
                            } else {
                                if (curNode === this._first) {
                                    this._first = curNode._next;
                                    this._first._prev = null;
                                } else if (curNode === this._last) {
                                    this._last = curNode._prev;
                                    this._last._next = null;
                                } else {
                                    curNode._prev._next = curNode._next;
                                    curNode._next._prev = curNode._prev;
                                }

                                curNode._next = null;
                                curNode._prev = null;
                                this._size -= 1;
                            }
                            break;
                        } else {
                            curNode = curNode._next;
                        }
                    } while (curNode !== null);
                }
            };

            LinkedList.prototype.find = function (data) {
                this._iter = null;

                if (!this.isEmpty()) {
                    var curNode = this._first;

                    do {
                        if (curNode._data === data) {
                            this._iter = curNode;
                            break;
                        } else {
                            curNode = curNode._next;
                        }
                    } while (curNode !== null);
                }

                return this._iter !== null ? this._iter._data : null;
            };

            LinkedList.prototype.first = function () {
                this._iter = this._first;
                return this._first !== null ? this._first._data : null;
            };

            LinkedList.prototype.last = function () {
                this._iter = this._last;
                return this._last !== null ? this._last._data : null;
            };

            LinkedList.prototype.next = function () {
                if (this._iter === null) {
                    this._iter = this._first;
                } else {
                    this._iter = this._iter._next;
                }

                return this._iter !== null ? this._iter._data : null;
            };

            LinkedList.prototype.prev = function () {
                if (this._iter === null) {
                    this._iter = this._last;
                } else {
                    this._iter = this._iter._prev;
                }

                return this._iter !== null ? this._iter._data : null;
            };

            return new LinkedList();
        }
    };
});