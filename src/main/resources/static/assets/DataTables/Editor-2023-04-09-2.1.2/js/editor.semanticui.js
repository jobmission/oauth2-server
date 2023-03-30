/*! Semantic UI integration for DataTables' Editor
 * © SpryMedia Ltd - datatables.net/license
 */

(function( factory ){
	if ( typeof define === 'function' && define.amd ) {
		// AMD
		define( ['jquery', 'datatables.net-se', 'datatables.net-editor'], function ( $ ) {
			return factory( $, window, document );
		} );
	}
	else if ( typeof exports === 'object' ) {
		// CommonJS
		var jq = require('jquery');
		var cjsRequires = function (root, $) {
			if ( ! $.fn.dataTable ) {
				require('datatables.net-se')(root, $);
			}

			if ( ! $.fn.dataTable.Editor ) {
				require('datatables.net-editor')(root, $);
			}
		};

		if (typeof window !== 'undefined') {
			module.exports = function (root, $) {
				if ( ! root ) {
					// CommonJS environments without a window global must pass a
					// root. This will give an error otherwise
					root = window;
				}

				if ( ! $ ) {
					$ = jq( root );
				}

				cjsRequires( root, $ );
				return factory( $, root, root.document );
			};
		}
		else {
			cjsRequires( window, jq );
			module.exports = factory( jq, window, window.document );
		}
	}
	else {
		// Browser
		factory( jQuery, window, document );
	}
}(function( $, window, document, undefined ) {
'use strict';
var DataTable = $.fn.dataTable;



var Editor = DataTable.Editor;

/*
 * Set the default display controller to be Semantic UI modal
 */
DataTable.Editor.defaults.display = "semanticui";

/*
 * Change the default classes from Editor to be classes for Bootstrap
 */
$.extend( true, $.fn.dataTable.Editor.classes, {
	"header": {
		"wrapper": "DTE_Header header"
	},
	"body": {
		"wrapper": "DTE_Body content"
	},
	"footer": {
		"wrapper": "DTE_Footer actions"
	},
	"form": {
		"tag": "ui form",
		"button": "ui button",
		"buttonInternal": "ui button",
		"content": 'DTE_Form_Content'
	},
	"field": {
		"wrapper": "DTE_Field inline fields",
		"label":   "right aligned five wide field",
		"input":   "eight wide field DTE_Field_Input",

		"error":   "error has-error",
		"msg-labelInfo": "ui small",
		"msg-info":      "ui small",
		"msg-message":   "ui message small",
		"msg-error":     "ui error message small",
		"multiValue":    "ui message multi-value",
		"multiInfo":     "small",
		"multiRestore":  "ui message multi-restore"
	},
	inline: {
		wrapper: "DTE DTE_Inline ui form"
	},
	bubble: {
		table: "DTE_Bubble_Table ui form",
		bg: "ui dimmer modals page transition visible active"
	}
} );


$.extend( true, DataTable.ext.buttons, {
	create: {
		formButtons: {
			className: 'primary'
		}
	},
	edit: {
		formButtons: {
			className: 'primary'
		}
	},
	remove: {
		formButtons: {
			className: 'negative'
		}
	}
} );

DataTable.Editor.fieldTypes.datatable.tableClass = 'ui table';

/*
 * Bootstrap display controller - this is effectively a proxy to the Bootstrap
 * modal control.
 */

// Single shared model for all Editor instances
const dom = {
	modal: $('<div class="ui modal DTED"></div>'),
	close: $('<i class="close icon"/>')
}
let shown = false;
let lastAppend;

DataTable.Editor.display.semanticui = $.extend( true, {}, DataTable.Editor.models.displayController, {
	/*
	 * API methods
	 */
	init: function ( dte ) {
		// Make select lists semantic ui dropdowns if possible
		if ($.fn.dropdown) {
			dte.on( 'displayOrder.dtesu open.dtesu', function ( e, display, action, form ) {
				$.each( dte.s.fields, function ( key, field ) {
					$('select', field.node())
						.addClass('fluid')
						.dropdown();
				} );
			} );
		}

		return DataTable.Editor.display.semanticui;
	},

	open: function ( dte, append, callback ) {
		var modal = dom.modal;
		var appendChildren = $(append).children();

		// Because we can't use a single element, we need to insert the existing
		// children back into their previous host so that can be reused later
		if (lastAppend) {
			modal.children().appendTo(lastAppend);
		}

		lastAppend = append;

		// Clean up any existing elements and then insert the elements to
		// display. In Semantic UI we need to have the header, content and
		// actions at the top level of the modal rather than as children of a
		// wrapper.
		modal
			.children()
			.detach();

		modal
			.append( appendChildren )
			.prepend( modal.children('.header') ) // order is important
			.addClass( append.className )
			.prepend( dom.close );

		dom.close
			.attr('title', dte.i18n.close)
			.off( 'click.dte-se' )
			.on( 'click.dte-se', function (e) {
				dte.close('icon');
				return false;
			} );

		$(document)
			.off('click.dte-se')
			.on('click.dte-se', 'div.ui.dimmer.modals', function (e) {
				if ( $(e.target).hasClass('dimmer') ) {
					dte.background();
				}
			} );

		if ( shown ) {
			if ( callback ) {
				callback();
			}
			return;
		}

		shown = true;

		$(modal)
			.modal( 'setting', {
				autofocus: false,
				closable: false,
				onVisible: function () {
					// Can only give elements focus when shown
					if ( dte.s.setFocus ) {
						dte.s.setFocus.focus();
					}

					if ( callback ) {
						callback();
					}
				},
				onHidden: function () {
					$(append).append( appendChildren );
					shown = false;
				}
			} )
			.modal( 'show' );
	},

	close: function ( dte, callback ) {
		if ( ! shown ) {
			if ( callback ) {
				callback();
			}
			return;
		}

		dom.modal.modal('hide');

		lastAppend = null;
		shown = false;

		if ( callback ) {
			callback();
		}
	},

	node: function ( dte ) {
		return dom.modal[0];
	}
} );


return Editor;
}));
