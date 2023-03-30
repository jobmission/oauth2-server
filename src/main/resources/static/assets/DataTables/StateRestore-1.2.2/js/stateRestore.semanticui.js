/*! Bootstrap integration for DataTables' StateRestore
 * © SpryMedia Ltd - datatables.net/license
 */

(function( factory ){
	if ( typeof define === 'function' && define.amd ) {
		// AMD
		define( ['jquery', 'datatables.net-se', 'datatables.net-staterestore'], function ( $ ) {
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

			if ( ! $.fn.dataTable.StateRestore ) {
				require('datatables.net-staterestore')(root, $);
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


$.extend(true, DataTable.StateRestoreCollection.classes, {
    checkBox: 'dtsr-check-box form-check-input',
    checkLabel: 'dtsr-check-label form-check-label',
    checkRow: 'dtsr-check-row form',
    creationButton: 'dtsr-creation-button ui button primary',
    creationForm: 'dtsr-creation-form modal-body',
    creationText: 'dtsr-creation-text modal-header',
    creationTitle: 'dtsr-creation-title modal-title',
    nameInput: 'dtsr-name-input form-control',
    nameLabel: 'dtsr-name-label form-label',
    nameRow: 'dtsr-name-row ui input'
});
$.extend(true, DataTable.StateRestore.classes, {
    confirmation: 'dtsr-confirmation modal',
    confirmationButton: 'dtsr-confirmation-button ui button primary',
    confirmationText: 'dtsr-confirmation-text modal-body',
    renameModal: 'dtsr-rename-modal ui input'
});


return DataTable;
}));
