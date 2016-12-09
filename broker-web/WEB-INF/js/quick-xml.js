/**
 * de.sb.util.QUICK_XML: XMl marshaler singleton.
 * Copyright (c) 2013-2015 Sascha Baumeister
 */
"use strict";

this.de = this.de || {};
this.de.sb = this.de.sb || {};
this.de.sb.util = this.de.sb.util || {};
(function () {

	/**
	 * Defines the QUICK_XML singleton which provides "quick and dirty" generic
	 * marshaling and unmarshaling of objects into XML and vice versa, similarly
	 * to the popular built-in JSON singleton.
	 */
	de.sb.util.QUICK_XML = new function () {

		/**
		* Recursively marshals the given object into an XML document. The result will contain
		* the given root element, except if the given object is null, an array, or a function.
		* Any primitive typed field (strings, numbers, booleans) is marshaled into an attribute.
		* Any array typed field is marshaled into multiple child elements, while any other
		* Object type is recursively marshaled into a single child element. Note that similarly
		* to JSON.stringify(), joint references to the same child object will be represented
		* as content equal but disjoint XML elements. Also, recursive object references are
		* not supported, and will cause infinite loops.
		* @param {String} rootElementName the root element name for the given object
		* @param {Object} object the object to be marshaled
		* @return {String} the marshaled XML document
		*/
		this.marshal = function (rootElementName, object) {
			var objectType = Object.prototype.toString.call(object); 
			if (objectType === "[object Array]" || objectType === "[object Function]") return "";

			var xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
			if (object === null) return xmlHeader;
			return xmlHeader + recursiveMarshal(rootElementName, object);
		}


		/**
		* Recursively unmarshals the given XML text into an object. If the XML root element
		* contains only text, the latter is returned. Otherwise, a generic object is
		* assembled that contains fields named after each element's attributes and child
		* elements. If an element contains multiple child elements sharing the same name,
		* their values are joined into an array. Note that this implies that field values
		* may be undefined (zero occurrences), object types (single occurrence), or array
		* types (multiple occurrences) depending on the XML content.
		* @param {String} xmlElement the XML element
		* @return {Object} a generic object
		*/
		this.unmarshal = function (xml) {
			var dom = new DOMParser().parseFromString(xml, "text/xml");
			var domRootElement = dom.childNodes[0];
			return recursiveUnmarshal(domRootElement);
		}


		/**
		* Private function recursively marshaling the given object into an XML element with the
		* given name. Any primitive typed field (strings, numbers, booleans) is marshaled into
		* an attribute. Any array typed field is marshaled into multiple child elements, while
		* any other object type is recursively marshaled into a single child element. Note that
		* similarly to JSON.stringify(), joint references to the same child object will be
		* represented as content equal but disjoint XML elements. Also, recursive object
		* references are not supported, and will cause infinite loops.
		* @param {String} elementName the element name for the given object
		* @param {Object} object the object to be marshaled
		* @return {String} the marshaled XML element
		*/
		function recursiveMarshal (elementName, object) {
			var attributeXml = "", bodyXml = "";

			for (var key in object) {
				var value = object[key], valueType = Object.prototype.toString.call(value);

				if (valueType === "[object Array]") {
					for (var valueIndex = 0; valueIndex < value.length; ++valueIndex) {
						bodyXml += recursiveMarshal(key, value[valueIndex])
					}
				} else if (valueType === "[object String]" || valueType === "[object Number]" || valueType === "[object Boolean]") {
					attributeXml += " " + key + "=\"" + value.toString().split("\"").join("&quot;") + "\"";
				} else if (value && valueType !== "[object Function]") {
					bodyXml += recursiveMarshal(key, value);
				}
			}

			return "<" + elementName + attributeXml + (bodyXml.length == 0 ? " />" : ">" + bodyXml + "</" + elementName + ">");
		}


		/**
		* Private function recursively unmarshaling the given DOM element into an object. If
		* the node is a text node, it's text value is returned. Otherwise a generic object is
		* returned that contains fields named after the node's attributes and child elements.
		* If a node contains multiple child elements sharing the same name, they are joined
		* into an array.
		* @param {Element} domElement the DOM element
		* @return {Object} a generic object
		*/
		function recursiveUnmarshal (domElement) {
			if(domElement.nodeName == "#text") return domElement.nodeValue.trim();

			var result = {};
			if ("attributes" in domElement) {
				for(var stop = domElement.attributes.length, index = 0; index < stop; ++index) {
					var domAttribute = domElement.attributes[index];
					result[domAttribute.nodeName] = domAttribute.nodeValue;
				}
			}

			for (var stop = domElement.childNodes.length, index = 0; index < stop; ++index) {
				var domChildElement = domElement.childNodes[index];
				var name = domChildElement.nodeName, value = recursiveUnmarshal(domChildElement);

				if (name in result) {
					var existingValue = result[name];
					if (Object.prototype.toString.call(existingValue) === "[object Array]") {
						existingValue.push(value);
					} else {
						result[name] = [existingValue, value];
					}
				} else {
					result[name] = value;
				}
			}

			return result;
		}
	}
} ());