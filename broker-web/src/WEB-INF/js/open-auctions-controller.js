// open-auctions-template

/**
 * de.sb.broker.OpenAuctionsController: broker auctions controller.
 * Copyright (c) 2013-2015 Sascha Baumeister
 */
"use strict";

this.de = this.de || {};
this.de.sb = this.de.sb || {};
this.de.sb.broker = this.de.sb.broker || {};
(function () {
	var SUPER = de.sb.broker.Controller;
	var TIMESTAMP_OPTIONS = {
		year: 'numeric', month: 'numeric', day: 'numeric',
		hour: 'numeric', minute: 'numeric', second: 'numeric',
		hour12: false
	};


	/**
	 * Creates a new auctions controller that is derived from an abstract controller.
	 * @param sessionContext {de.sb.broker.SessionContext} a session context
	 */
	de.sb.broker.OpenAuctionsController = function (sessionContext) {
		SUPER.call(this, 1, sessionContext);
	}
	de.sb.broker.OpenAuctionsController.prototype = Object.create(SUPER.prototype);
	de.sb.broker.OpenAuctionsController.prototype.constructor = de.sb.broker.OpenAuctionsController;


	/**
	 * Displays the associated view.
	 */
	de.sb.broker.OpenAuctionsController.prototype.display = function () {
		if (!this.sessionContext.user) return;
		SUPER.prototype.display.call(this);

		var sectionElement = document.querySelector("#open-auctions-template").content.cloneNode(true).firstElementChild;
		document.querySelector("main").appendChild(sectionElement);
		//sectionElement = document.querySelector("#open-auctions-template").content.cloneNode(true).firstElementChild;
		//document.querySelector("main").appendChild(sectionElement);

		//registrierung event send button
		
		var indebtedSemaphore = new de.sb.util.Semaphore(1 - 2);
		var statusAccumulator = new de.sb.util.StatusAccumulator();
		var self = this;

		//offene auctionen, eigene gebote
		//GET services/auctions?closed=false
		//GET services/people/id/bids?closed=false
		//gebote in map ablegen, value gebot und key auktionsreferenz von gebot, abgebildet nach gebot
		
		var resource = "/services/people/" + this.sessionContext.user.identity + "/auctions?seller=false&open=true";
		de.sb.util.AJAX.invoke(resource, "GET", {"Accept": "application/json"}, null, this.sessionContext, function (request) {
			if (request.status === 200) {
				var auctions = JSON.parse(request.responseText);
				self.displayBidderAuctions(auctions);
			}
			statusAccumulator.offer(request.status, request.statusText);
			indebtedSemaphore.release();
		});

		indebtedSemaphore.acquire(function () {
			self.displayStatus(statusAccumulator.status, statusAccumulator.statusText);
		});
		//
	}


	/**
	 * Displays the given auctions that feature the requester as seller.
	 * @param auctions {Array} the seller auctions
	 */
	/*de.sb.broker.OpenAuctionsController.prototype.displaySellerAuctions = function (auctions) {
		var tableBodyElement = document.querySelector("section.open-seller-auctions tbody");
		var rowTemplate = document.createElement("tr");
		for (var index = 0; index < 7; ++index) {
			var cellElement = document.createElement("td");
			cellElement.appendChild(document.createElement("output"));
			rowTemplate.appendChild(cellElement);
		}

		var self = this;
		auctions.forEach(function (auction) {
			var rowElement = rowTemplate.cloneNode(true);
			tableBodyElement.appendChild(rowElement);

			var activeElements = rowElement.querySelectorAll("output");

			activeElements[1].value = new Date(auction.creationTimestamp).toLocaleString(TIMESTAMP_OPTIONS);
			activeElements[2].value = new Date(auction.closureTimestamp).toLocaleString(TIMESTAMP_OPTIONS);
			activeElements[3].title = auction.description;
			activeElements[3].value = auction.title;
			activeElements[4].value = auction.unitCount;
			activeElements[5].value = (auction.askingPrice * 0.01).toFixed(2);
		});
	}*/


	/**
	 * Displays the given auctions that feature the requester as bidder.
	 * @param auctions {Array} the bidder auctions
	 */
	de.sb.broker.OpenAuctionsController.prototype.displayBidderAuctions = function (auctions) {
		var tableBodyElement = document.querySelector("section.open-auctions tbody");
		var rowTemplate = document.createElement("tr");
		for (var index = 0; index < 9; ++index) {
			var cellElement = document.createElement("td");
			cellElement.appendChild(document.createElement("output"));
			rowTemplate.appendChild(cellElement);
		}

		var self = this;
		auctions.forEach(function (auction) {
			var rowElement = rowTemplate.cloneNode(true);
			tableBodyElement.appendChild(rowElement);

			var userBid = selectBidByBidder(auction.bids, self.sessionContext.user.identity);
			var activeElements = rowElement.querySelectorAll("output");
			activeElements[0].innerHTML = "<img class=\" " + auction.seller.alias + "\" style=\"min-height: 10px; min-width: 10px\">";
			//auction.seller.alias;
			activeElements[0].title = createDisplayTitle(auction.seller);
			//activeElements[1].value = maxBid.bidder.alias;
			//activeElements[1].title = createDisplayTitle(maxBid.bidder);
			activeElements[1].value = new Date(auction.creationTimestamp).toLocaleString(TIMESTAMP_OPTIONS);
			activeElements[2].value = new Date(auction.closureTimestamp).toLocaleString(TIMESTAMP_OPTIONS);
			activeElements[3].value = auction.title;
			activeElements[3].title = auction.description;
			activeElements[4].value = auction.unitCount;
			activeElements[5].value = (auction.askingPrice * 0.01).toFixed(2);
			


			//settings for avatar thumbnail
			var userID = auction.seller.identity;
			var avatar = document.querySelector("."+auction.seller.alias);		
			avatar.src = "/services/people/" + userID + "/avatar";
		});
	}


	/**
	 * Returns the bid with the highest price offer.
	 * @param bids {Array} the bids
	 * @return the maximum bid, or null for none
	 */
	/*function selectBidByMaximumPrice (bids) {
		var maxBid = null;
		bids.forEach(function (bid) {
			if (!maxBid || bid.price > maxBid.price) maxBid = bid;
		});
		return maxBid;
	}*/


	/**
	 * Returns the bid featuring the given bidder.
	 * @param bids {Array} the bids
	 * @param bidderIdentity the bidder identity
	 * @return the bidder's bid, or null for none
	 */
	function selectBidByBidder (bids, bidderIdentity) {
		for(var index = 0; index < bids.length; ++index) {
			var bid = bids[index];
			if (bid.bidder.identity == bidderIdentity) return bid;
		}
		return null;
	}


	/**
	 * Creates a display title for the given person.
	 * @param person {Object} the person
	 */
	function createDisplayTitle (person) {
		if (!person) return "";
		if (!person.name) return person.alias;
		return person.name.given + " " + person.name.family + " (" + person.contact.email + ")";
	}
} ());