/**
 * de.sb.util:
 * - AJAX singleton:	XmlHttpRequest invocation
 * - StatusAccumulator:	Chose the maximum of multiple request status replies 
 * - Semaphore:      	Asynchronous execution block synchronization
 * Copyright (c) 2013-2015 Sascha Baumeister
 */
"use strict";

this.de = this.de || {};
this.de.sb = this.de.sb || {};
this.de.sb.util = this.de.sb.util || {};
(function () {

	/**
	 * Creates the AJAX singleton for simplified XmlHttpRequest processing.
	 */
	de.sb.util.AJAX = new function () {

		/**
		 * Sends an XmlHttpRequest with the given arguments. If a callback function is specified,
		 * the HTTP request is executed asynchronously, and the given callback method is invoked
		 * once the HTTP response is received. Otherwise, the request is executed synchronously.
		 * @param resource {String} the HTTP request URI
		 * @param method {String} the HTTP request method
		 * @param header {Object} a map associating HTTP request header keys with values,
		          or null for none
		 * @param body {Object} the HTTP request body, or null for none
		 * @param credentials an object that provides userAlias and userPassword fields
		          (for HTTP basic authentication), or null for unauthenticated access
		 * @param callback {Function} a function that takes an XmlHttpRequest as an argument,
		          and is executed once the corresponding response becomes available, or
		          null for synchronized request processing
		 * @return the XmlHttpRequest
		 */
		this.invoke = function (resource, method, header, body, credentials, callback) {
			var request = new XMLHttpRequest();
			request.overrideMimeType("text/plain");

			var asynchronous = typeof callback == "function";
			if (asynchronous) {
				request.addEventListener("readystatechange", function () {
					if (this.readyState === 4) callback.call(null, this);
				});
			}

			if (credentials) {
				request.open(method, resource, asynchronous, credentials.userAlias, credentials.userPassword);
			} else {
				request.open(method, resource, asynchronous);
			}

			for (var key in (header || {})) {
				request.setRequestHeader(key, header[key]);
			}
			request.send(body || "");

			return request;
		}
	}

	
	/**
	 * Creates a new request status accumulator. the accumulator stores the
	 * highest request status accumulated, and it's associated status text.
	 */
	de.sb.util.StatusAccumulator = function () {
		var worstStatus = -1;
		var worstStatusText = null;

		/**
		 * The (read-only) accumulated status.
		 */
		Object.defineProperty(this, "status", {
			configurable: false,
			enumerable: true,
			get: function () {
				return worstStatus;
			}
		});


		/**
		 * The (read-only) accumulated status text.
		 */
		Object.defineProperty(this, "statusText", {
			configurable: false,
			enumerable: true,
			get: function () {
				return worstStatusText;
			}
		});


		/**
		 * Overrides the digest's status and status text if the given status
		 * exceeds it.
		 * @param status {Number} the request status
		 * @param statusText {String} the associated status text
		 */
		Object.defineProperty(this, "offer", {
			configurable: false,
			enumerable: false,
			value: function (status, statusText) {
				if (worstStatus === 0) return;
				if (status === 0 | worstStatus < status) {
					worstStatus = status;
					worstStatusText = statusText;
				}
			}
		});
	}



	/**
	 * Creates a new semaphore with the floor of the given ticket count. Note
	 * that the ticket count may be negative, which constitutes an indebted
	 * semaphore. Also note that JavaScript's single threaded nature implies
	 * that this semaphore implementation is safe without compare-and-swap or
	 * similar hardware support.
	 * @param initialTicketCount {Number} the initial ticket count
	 * @throws TypeError if the given ticket count is not a number
	 */
	de.sb.util.Semaphore = function (initialTicketCount) {
		var ticketCount = Math.floor(initialTicketCount);
		var actionQueue = [];
		var self = this;

		/**
		 * The (read-only) number of tickets currently available for acquisition.
		 */
		Object.defineProperty(this, "ticketCount", {
			configurable: false,
			enumerable: true,
			get: function () {
				return ticketCount;
			}
		});


		/**
		 * The (read-only) number of actions currently queued for execution.
		 */
		Object.defineProperty(this, "actionCount", {
			configurable: false,
			enumerable: true,
			get: function () {
				return actionQueue.length;
			}
		});


		/**
		 * Acquires a single ticket. If the current number of available tickets
		 * is strictly positive, then this count is decremented by one, and the
		 * given action is executed immediately. Otherwise, the given action is
		 * queued for execution once a matching ticket is released, thereby
		 * postponing the ticket count decrement.
		 * @param action {Function} the action to be performed once a ticket has
		 *                          been acquired
		 * @throws TypeError if the given action is not a function
		 */
		Object.defineProperty(this, "acquire", {
			configurable: false,
			enumerable: false,
			value: function (action) {
				if (typeof action !== "function") throw new TypeError();

				if (ticketCount <= 0) {
					actionQueue.push(action);
				} else {
					ticketCount -= 1;
					action.call();
				}
			}
		});


		/**
		 * Releases a single ticket. If there no queued actions, the number
		 * of available tickets is increased by one. Otherwise, a single
		 * action is removed from the queue and executed, immediately
		 * re-acquiring the ticket released.
		 */
		Object.defineProperty(this, "release", {
			configurable: false,
			enumerable: false,
			value: function () {
				if (ticketCount == 0 && actionQueue.length > 0) {
					var action = actionQueue.shift();
					action.call();
				} else {
					ticketCount += 1;
				}
			}
		});
	}
} ());