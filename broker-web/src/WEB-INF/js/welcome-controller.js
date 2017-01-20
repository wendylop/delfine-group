/**
 * de.sb.broker.WelcomeController: broker welcome controller.
 * Copyright (c) 2013-2015 Sascha Baumeister
 */
"use strict";

this.de = this.de || {};
this.de.sb = this.de.sb || {};
this.de.sb.broker = this.de.sb.broker || {};
(function () {
	var SUPER = de.sb.broker.Controller;

	/**
	 * Creates a new welcome controller that is derived from an abstract controller.
	 * @param sessionContext {de.sb.broker.SessionContext} a session context
	 */
	de.sb.broker.WelcomeController = function (sessionContext) {
		SUPER.call(this, 0, sessionContext);
	}
	de.sb.broker.WelcomeController.prototype = Object.create(SUPER.prototype);
	de.sb.broker.WelcomeController.prototype.constructor = de.sb.broker.WelcomeController;


	/**
	 * Displays the associated view.
	 */
	de.sb.broker.WelcomeController.prototype.display = function () {
		this.sessionContext.clear();
		SUPER.prototype.display.call(this);

		var sectionElement = document.querySelector("#login-template").content.cloneNode(true).firstElementChild;
		sectionElement.querySelector("button").addEventListener("click", this.login.bind(this));
		document.querySelector("main").appendChild(sectionElement);
	}


	/**
	 * Performs a login check on the given user data, initializes the controller's
	 * session context if the login was successful, and initiates rendering of the
	 * preferences view.
	 */
	de.sb.broker.WelcomeController.prototype.login = function () {
		var inputElements = document.querySelectorAll("section.login input");
		var credentials = {
			userAlias: inputElements[0].value.trim(),
			userPassword: inputElements[1].value.trim()
		};

		if (!credentials.userAlias | !credentials.userPassword) {
			this.displayStatus(401, "Unauthorized");
			return;
		}

		var self = this;
		de.sb.util.AJAX.invoke("/services/people/requester", "GET", {"Accept": "application/json"}, null, credentials, function (request) {
			self.displayStatus(request.status, request.statusText);
			if (request.status === 200) {
				self.sessionContext.user = JSON.parse(request.responseText);
				self.sessionContext.userPassword = credentials.userPassword;
				de.sb.broker.APPLICATION.preferencesController.display();
			}
		});
	}
} ());