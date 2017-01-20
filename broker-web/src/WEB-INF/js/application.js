/**
 * de.sb.broker.APPLICATION: broker application singleton.
 * Copyright (c) 2013-2015 Sascha Baumeister
 */
"use strict";

this.de = this.de || {};
this.de.sb = this.de.sb || {};
this.de.sb.broker = this.de.sb.broker || {};
(function () {
	var SESSION_CONTEXT = new de.sb.broker.SessionContext();

	/**
	 * The broker application singleton maintaining the view controllers.
	 */
	de.sb.broker.APPLICATION = {
		welcomeController: "WelcomeController" in de.sb.broker
			? new de.sb.broker.WelcomeController(SESSION_CONTEXT)
			: new de.sb.broker.Controller(0),
		openAuctionsController: "OpenAuctionsController" in de.sb.broker
			? new de.sb.broker.OpenAuctionsController(SESSION_CONTEXT)
			: new de.sb.broker.Controller(1),
		closedAuctionsController: "ClosedAuctionsController" in de.sb.broker
			? new de.sb.broker.ClosedAuctionsController(SESSION_CONTEXT)
			: new de.sb.broker.Controller(2),
		preferencesController: "PreferencesController" in de.sb.broker
			? new de.sb.broker.PreferencesController(SESSION_CONTEXT)
			: new de.sb.broker.Controller(3)
	}
	var APPLICATION = de.sb.broker.APPLICATION;


	/**
	 * Register DOM menu callbacks, and display welcome view.
	 */
	window.addEventListener("load", function () {
		var menuAnchors = document.querySelectorAll("header > nav a");
		menuAnchors[0].addEventListener("click", APPLICATION.welcomeController.display.bind(APPLICATION.welcomeController));
		menuAnchors[1].addEventListener("click", APPLICATION.openAuctionsController.display.bind(APPLICATION.openAuctionsController));
		menuAnchors[2].addEventListener("click", APPLICATION.closedAuctionsController.display.bind(APPLICATION.closedAuctionsController));
		menuAnchors[3].addEventListener("click", APPLICATION.preferencesController.display.bind(APPLICATION.preferencesController));

		APPLICATION.welcomeController.display();
		APPLICATION.welcomeController.displayStatus(200, "OK");
	});
} ());