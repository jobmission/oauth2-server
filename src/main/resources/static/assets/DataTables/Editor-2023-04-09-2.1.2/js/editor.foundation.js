/*! Foundation integration for DataTables' Editor
 * © SpryMedia Ltd - datatables.net/license
 */

(function( factory ){
	if ( typeof define === 'function' && define.amd ) {
		// AMD
		define( ['jquery', 'datatables.net-zf', 'datatables.net-editor'], function ( $ ) {
			return factory( $, window, document );
		} );
	}
	else if ( typeof exports === 'object' ) {
		// CommonJS
		var jq = require('jquery');
		var cjsRequires = function (root, $) {
			if ( ! $.fn.dataTable ) {
				require('datatables.net-zf')(root, $);
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
 * Set the default display controller to be our foundation control 
 */
DataTable.Editor.defaults.display = "foundation";


/*
 * Change the default classes from Editor to be classes for Foundation
 */
$.extend( true, $.fn.dataTable.Editor.classes, {
	field: {
		wrapper:         "DTE_Field row",
		label:           "small-4 columns inline",
		input:           "small-8 columns",
		error:           "error",
		multiValue:      "panel radius multi-value",
		multiInfo:       "small",
		multiRestore:    "panel radius multi-restore",
		"msg-labelInfo": "label secondary",
		"msg-info":      "label secondary",
		"msg-message":   "label secondary",
		"msg-error":     "label alert"
	},
	form: {
		button:  "button small",
		buttonInternal:  "button small"
	}
} );


/*
 * Foundation display controller - this is effectively a proxy to the Foundation
 * modal control.
 */
var shown = false;
var reveal;

const dom = {
	content: $(
		'<div class="reveal reveal-modal DTED" data-reveal></div>'
	),
	close: $('<button class="close close-button">&times;</div>')
};

DataTable.Editor.display.foundation = $.extend( true, {}, DataTable.Editor.models.displayController, {
	init: function ( dte ) {
		if (! reveal) {
			reveal = new window.Foundation.Reveal( dom.content, {
				closeOnClick: false
			} );
		}

		return DataTable.Editor.display.foundation;
	},

	open: function ( dte, append, callback ) {
		var content = dom.content;
		content.children().detach();
		content.append( append );
		content.prepend( dom.close );

		dom.close
			.attr('title', dte.i18n.close)
			.off('click.dte-zf')
			.on('click.dte-zf', function () {
				dte.close('icon');
			});

		$(document)
			.off('click.dte-zf')
			.on('click.dte-zf', 'div.reveal-modal-bg, div.reveal-overlay', function (e) {
				if ( $(e.target).closest(dom.content).length ) {
					return;
				}
				dte.background();
			} );

		if ( shown ) {
			if ( callback ) {
				callback();
			}
			return;
		}

		shown = true;

		$(dom.content)
			.one('open.zf.reveal', function () {
				if ( callback ) {
					callback();
				}
			});

		reveal.open();
	},

	close: function ( dte, callback ) {
		if (shown) {
			reveal.close();
			shown = false;
		}

		if ( callback ) {
			callback();
		}
	},

	node: function ( dte ) {
		return dom.content[0];
	}
} );


return Editor;
}));
