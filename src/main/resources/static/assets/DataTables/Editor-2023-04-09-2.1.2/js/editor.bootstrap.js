/*! Bootstrap integration for DataTables' Editor
 * © SpryMedia Ltd - datatables.net/license
 */

(function( factory ){
	if ( typeof define === 'function' && define.amd ) {
		// AMD
		define( ['jquery', 'datatables.net-bs', 'datatables.net-editor'], function ( $ ) {
			return factory( $, window, document );
		} );
	}
	else if ( typeof exports === 'object' ) {
		// CommonJS
		var jq = require('jquery');
		var cjsRequires = function (root, $) {
			if ( ! $.fn.dataTable ) {
				require('datatables.net-bs')(root, $);
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
 * Set the default display controller to be our bootstrap control 
 */
DataTable.Editor.defaults.display = "bootstrap";


/*
 * Change the default classes from Editor to be classes for Bootstrap
 */
$.extend( true, $.fn.dataTable.Editor.classes, {
	"header": {
		"wrapper": "DTE_Header modal-header",
		title: {
			tag: 'h4',
			class: 'modal-title'
		}
	},
	"body": {
		"wrapper": "DTE_Body modal-body"
	},
	"footer": {
		"wrapper": "DTE_Footer modal-footer"
	},
	"form": {
		"tag": "form-horizontal",
		"button": "btn btn-default",
		"buttonInternal": "btn btn-default"
	},
	"field": {
		"wrapper": "DTE_Field",
		"label":   "col-lg-4 control-label",
		"input":   "col-lg-8 controls",
		"error":   "error has-error",
		"msg-labelInfo": "help-block",
		"msg-info":      "help-block",
		"msg-message":   "help-block",
		"msg-error":     "help-block",
		"multiValue":    "well well-sm multi-value",
		"multiInfo":     "small",
		"multiRestore":  "well well-sm multi-restore"
	}
} );

$.extend( true, DataTable.ext.buttons, {
	create: {
		formButtons: {
			className: 'btn-primary'
		}
	},
	edit: {
		formButtons: {
			className: 'btn-primary'
		}
	},
	remove: {
		formButtons: {
			className: 'btn-danger'
		}
	}
} );

DataTable.Editor.fieldTypes.datatable.tableClass = 'table';

let shown = false;
let fullyShown = false;

const dom = {
	// Note that `modal-dialog-scrollable` is BS4.3+ only. It has no effect on 4.0-4.2
	content: $(
		'<div class="modal fade DTED">'+
			'<div class="modal-dialog">'+
				'<div class="modal-content"></div>'+
			'</div>'+
		'</div>'
	),
	close: $('<button class="close">&times;</div>')
};

/*
 * Bootstrap display controller - this is effectively a proxy to the Bootstrap
 * modal control.
 */
DataTable.Editor.display.bootstrap = $.extend( true, {}, DataTable.Editor.models.displayController, {
	init: function ( dte ) {
		// Add `form-control` to required elements
		dte.on( 'displayOrder.dtebs open.dtebs', function ( e, display, action, form ) {
			$.each( dte.s.fields, function ( key, field ) {
				$('input:not([type=checkbox]):not([type=radio]), select, textarea', field.node() )
					.addClass( 'form-control' );
			} );
		} );

		return DataTable.Editor.display.bootstrap;
	},

	open: function ( dte, append, callback ) {
		$(append).addClass('modal-content');

		// Special class for DataTable buttons in the form
		$(append).find('div.DTE_Field_Type_datatable div.dt-buttons')
			.removeClass('btn-group')
			.addClass('btn-group-vertical');

		var content = dom.content.find('div.modal-dialog');
		content.children().detach();
		content.append( append );

		// Setup events on each show
		dom.close
			.attr('title', dte.i18n.close)
			.off('click.dte-bs3')
			.on('click.dte-bs3', function () {
				dte.close('icon');
			})
			.prependTo($('div.modal-header', dom.content));

		// This is a bit horrible, but if you mousedown and then drag out of the modal container, we don't
		// want to trigger a background action.
		var allowBackgroundClick = false;
		$(document)
			.off('mousedown.dte-bs3')
			.on('mousedown.dte-bs3', 'div.modal', function (e) {
				if ( ! shown ) {
					return;
				}

				allowBackgroundClick = $(e.target).hasClass('modal') && shown
					? true
					: false;
			} );

		$(document)
			.off('click.dte-bs3')
			.on('click.dte-bs3', 'div.modal', function (e) {
				if ( $(e.target).hasClass('modal') && allowBackgroundClick ) {
					dte.background();
				}

				if ( ! $(e.target).hasClass('modal') ) {
					return;	
				}

				// If scrollbar shown
				if ($('div.modal-dialog').height() > $(e.target).height()) {
					// And if clicking inside it - do nothing
					if (e.pageX >= document.body.offsetWidth - DataTable.__browser.barWidth) {
						return;
					}
				}

				// All checks pass - do background action
				dte.background();
			} );

		if ( shown ) {
			if ( callback ) {
				callback();
			}
			return;
		}

		shown = true;
		fullyShown = false;

		$(dom.content)
			.one('shown.bs.modal', function () {
				// Can only give elements focus when shown
				if ( dte.s.setFocus ) {
					dte.s.setFocus.focus();
				}

				if ( callback ) {
					callback();
				}
			})
			.one('hidden', function () {
				shown = false;
			})
			.appendTo( 'body' )
			.modal( {
				backdrop: "static",
				keyboard: false
			} );

		dom.close.prependTo($('div.modal-header', dom.content));
	},

	close: function ( dte, callback ) {
		if ( ! shown ) {
			if ( callback ) {
				callback();
			}
			return;
		}

		$(dom.content)
			.one( 'hidden.bs.modal', function () {
				$(this).detach();
			} )
			.modal('hide');

		shown = false;

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
