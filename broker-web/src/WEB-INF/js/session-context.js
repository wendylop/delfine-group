/**
 * de.sb.broker.SessionContext: broker session context.
 * Copyright (c) 2013-2015 Sascha Baumeister
 */
"use strict";

this.de = this.de || {};
this.de.sb = this.de.sb || {};
this.de.sb.broker = this.de.sb.broker || {};
(function () {

	/**
	 * Creates an empty session context.
	 */
	de.sb.broker.SessionContext = function () {
		var self = this;

		Object.defineProperty(this, "user", {
			enumerable: true,
			configurable: false,
			writable: true,
			value: null
		});

		Object.defineProperty(this, "userAlias", {
			enumerable: true,
			configurable: false,
			get: function () { return self.user == null ? null : self.user.alias; }
		});

		Object.defineProperty(this, "userPassword", {
			enumerable: true,
			configurable: false,
			writable: true,
			value: null
		});
	}


	/**
	 * Clears this context.
	 */
	de.sb.broker.SessionContext.prototype.clear = function () {
		this.user = null;
		this.userPassword = null;
	}
} ());