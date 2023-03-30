/*! semantic ui integration for DataTables' SearchPanes
 * © SpryMedia Ltd - datatables.net/license
 */

(function( factory ){
	if ( typeof define === 'function' && define.amd ) {
		// AMD
		define( ['jquery', 'datatables.net-se', 'datatables.net-searchpanes'], function ( $ ) {
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

			if ( ! $.fn.dataTable.SearchPanes ) {
				require('datatables.net-searchpanes')(root, $);
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


$.extend(true, DataTable.SearchPane.classes, {
    buttonGroup: 'right floated ui buttons column',
    disabledButton: 'disabled',
    narrowSearch: 'dtsp-narrowSearch',
    narrowSub: 'dtsp-narrow',
    paneButton: 'basic ui',
    paneInputButton: 'circular search link icon',
    topRow: 'row dtsp-topRow'
});
$.extend(true, DataTable.SearchPanes.classes, {
    clearAll: 'dtsp-clearAll basic ui button',
    collapseAll: 'dtsp-collapseAll basic ui button',
    disabledButton: 'disabled',
    showAll: 'dtsp-showAll basic ui button'
});
// This override is required for the integrated search Icon in sematic ui
DataTable.SearchPane.prototype._searchContSetup = function () {
    $('<i class="' + this.classes.paneInputButton + '"></i>').appendTo(this.dom.searchCont);
};


return DataTable;
}));
