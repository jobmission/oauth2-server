/*! Bootstrap integration for DataTables' Editor
 * © SpryMedia Ltd - datatables.net/license
 */

(function( factory ){
	if ( typeof define === 'function' && define.amd ) {
		// AMD
		define( ['jquery', 'datatables.net-bs5', 'datatables.net-editor'], function ( $ ) {
			return factory( $, window, document );
		} );
	}
	else if ( typeof exports === 'object' ) {
		// CommonJS
		var jq = require('jquery');
		var cjsRequires = function (root, $) {
			if ( ! $.fn.dataTable ) {
				require('datatables.net-bs5')(root, $);
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
		"wrapper": "DTE_Header",
		title: {
			tag: 'h5',
			class: 'modal-title'
		}
	},
	"body": {
		"wrapper": "DTE_Body"
	},
	"footer": {
		"wrapper": "DTE_Footer"
	},
	"form": {
		"tag": "form-horizontal",
		"button": "btn",
		"buttonInternal": "btn btn-outline-secondary"
	},
	"field": {
		"wrapper": "DTE_Field form-group row",
		"label":   "col-lg-4 col-form-label",
		"input":   "col-lg-8",
		"error":   "error is-invalid",
		"msg-labelInfo": "form-text text-secondary small",
		"msg-info":      "form-text text-secondary small",
		"msg-message":   "form-text text-secondary small",
		"msg-error":     "form-text text-danger small",
		"multiValue":    "card multi-value",
		"multiInfo":     "small",
		"multiRestore":  "multi-restore"
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

/*
 * Bootstrap display controller - this is effectively a proxy to the Bootstrap
 * modal control.
 */
let shown = false;
let fullyShown = false;

const dom = {
	content: $(
		'<div class="modal fade DTED">'+
			'<div class="modal-dialog modal-dialog-scrollable modal-dialog-centered"></div>'+
		'</div>'
	),
	close: $('<button class="btn-close"></div>')
};
let modal;
let _bs = window.bootstrap;

DataTable.Editor.bootstrap = function (bs) {
	_bs = bs;
}

DataTable.Editor.display.bootstrap = $.extend( true, {}, DataTable.Editor.models.displayController, {
	/*
	 * API methods
	 */
	init: function ( dte ) {
		// Add `form-control` to required elements
		dte.on( 'displayOrder.dtebs open.dtebs', function ( e, display, action, form ) {
			$.each( dte.s.fields, function ( key, field ) {
				$('input:not([type=checkbox]):not([type=radio]), select, textarea', field.node() )
					.addClass( 'form-control' );

				$('input[type=checkbox], input[type=radio]', field.node() )
					.addClass( 'form-check-input' );

				$('select', field.node() )
					.addClass( 'form-select' );
			} );
		} );

		return DataTable.Editor.display.bootstrap;
	},

	open: function ( dte, append, callback ) {
		if (! modal) {
			modal = new _bs.Modal(dom.content[0], {
				backdrop: "static",
				keyboard: false
			});
		}

		$(append).addClass('modal-content');
		$('.DTE_Header', append).addClass('modal-header');
		$('.DTE_Body', append).addClass('modal-body');
		$('.DTE_Footer', append).addClass('modal-footer');

		// Special class for DataTable buttons in the form
		$(append).find('div.DTE_Field_Type_datatable div.dt-buttons')
			.removeClass('btn-group')
			.addClass('btn-group-vertical');

		// Setup events on each show
		dom.close
			.attr('title', dte.i18n.close)
			.off('click.dte-bs5')
			.on('click.dte-bs5', function () {
				dte.close('icon');
			})
			.appendTo($('div.modal-header', append));

		// This is a bit horrible, but if you mousedown and then drag out of the modal container, we don't
		// want to trigger a background action.
		let allowBackgroundClick = false;
		$(document)
			.off('mousedown.dte-bs5')
			.on('mousedown.dte-bs5', 'div.modal', function (e) {
				allowBackgroundClick = $(e.target).hasClass('modal') && shown
					? true
					: false;
			} );

		$(document)
			.off('click.dte-bs5')
			.on('click.dte-bs5', 'div.modal', function (e) {
				if ( $(e.target).hasClass('modal') && allowBackgroundClick ) {
					dte.background();
				}
			} );

		var content = dom.content.find('div.modal-dialog');
		content.children().detach();
		content.append( append );

		// Floating label support - rearrange the DOM for the inputs
		if (dte.c.bootstrap && dte.c.bootstrap.floatingLabels) {
			var floating_label_types = ['readonly', 'text', 'textarea', 'select', 'datetime']
			var fields = dte.order();

			fields
				.filter(function (f) {
					var type = dte.field(f).s.opts.type;

					return floating_label_types.includes(type);
				})
				.forEach(function(f) {
					var node = $(dte.field(f).node());
					var wrapper = node.find('.DTE_Field_InputControl');
					var control = wrapper.children(':first-child');
					var label = node.find('label');

					wrapper.parent().removeClass('col-lg-8').addClass('col-lg-12');
					wrapper.addClass('form-floating');
					control.addClass('form-control').attr("placeholder", f);
					label.appendTo(wrapper);
				});
		}

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

				fullyShown = true;

				if ( callback ) {
					callback();
				}
			})
			.one('hidden', function () {
				shown = false;
			})
			.appendTo( 'body' );
		
		modal.show();
	},

	close: function ( dte, callback ) {
		if ( ! shown ) {
			if ( callback ) {
				callback();
			}
			return;
		}

		// Check if actually displayed or not before hiding. BS4 doesn't like `hide`
		// before it has been fully displayed
		if ( ! fullyShown ) {
			$(dom.content)
				.one('shown.bs.modal', function () {
					DataTable.Editor.display.bootstrap.close( dte, callback );
				} );

			return;
		}

		$(dom.content)
			.one( 'hidden.bs.modal', function () {
				$(this).detach();
			} );

		modal.hide();

		shown = false;
		fullyShown = false;

		if ( callback ) {
			callback();
		}
	},

	node: function ( dte ) {
		return dom.content[0];
	},
} );


return Editor;
}));
