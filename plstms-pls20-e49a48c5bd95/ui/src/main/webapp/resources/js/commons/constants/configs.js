angular.module('plsApp.utils').constant('plsJobIdRules', {
    regExp: '((^(BB|BI|BM|BN|BO|BP|BS|FB|FI|FM|FN|FO|FP|FS|W|WM|WME|WMP|WMS|WS)[0-9]{3,4}$)|(^[0-9]{5}[A-Z]{1}$))|(^R[0-9]{4}$)',
    warnText: 'Job# must be in the format of "Five digits + 1 alpha character (A to Z), or begin with \'BB\',\'BI\', \'BM\', \'BN\', \'BO\', ' +
    '\'BP\', \'BS\', \'FB\', \'FI\', \'FM\', \'FN\', \'FO\', \'FP\', \'FS\', \'W\', \'WM\', \'WME\', \'WMP\', \'WMS\', \'WS\'  + 3 or 4 digits, ' +
    'or begin with \'R\' + 4 digits'
});
angular.module('plsApp.utils').constant('plsGLRules', {
    regExp: '^([^-]+-){3}[^-]+$',
    warnText: ''
});

angular.module('plsApp.utils').constant('plsGLSafeworksRules', {
    regExp: '^[\\d]+\\.{1}[\\d]+$',
    warnText: ''
});

angular.module('plsApp.utils').constant('billToIdentifiers', {
    BOL : {
        permission : 'REQUIRE_SHIPMENT_BOL',
        field : 'bolNumber',
        label : 'BOL#',
        maxLength : 25
    },
    SO : {
        permission : 'REQUIRE_SHIPMENT_SO',
        field : 'finishOrder.soNumber',
        label : 'SO#',
        maxLength : 50
    },
    PO : {
        permission : 'REQUIRE_SHIPMENT_PO',
        field : 'finishOrder.poNumber',
        label : 'PO#',
        maxLength : 50
    },
    PU : {
        permission : 'REQUIRE_SHIPMENT_PU',
        field : 'finishOrder.puNumber',
        label : 'PU#',
        maxLength : 30
    },
    PRO : {
        permission : 'REQUIRE_SHIPMENT_PRO',
        field : 'proNumber',
        label : 'Pro#',
        maxLength : 30
    },
    SR : {
        permission : 'REQUIRE_SHIPMENT_REF',
        field : 'finishOrder.ref',
        label : 'Shipper Ref#',
        maxLength : 30
    },
    TR : {
        permission : 'REQUIRE_SHIPMENT_TRAILER',
        field : 'finishOrder.trailerNumber',
        label : 'Trailer#',
        maxLength : 30
    },
    CARGO : {
        permission : 'REQUIRE_SHIPMENT_CARGO',
        field : 'cargoValue',
        label : 'Cargo value',
        maxLength : 6
    },
    GL : {
        permission : 'REQUIRE_SHIPMENT_GL',
        field : 'finishOrder.glNumber',
        label : 'GL#',
        maxLength : 50
    },
    JOB : {
        permission : '',
        field : 'finishOrder.jobNumbers',
        label : 'Job#',
        maxLength : 30
    },
    RB : {
        permission : 'REQUIRE_SHIPMENT_REQUESTED_BY',
        field : 'finishOrder.requestedBy',
        label : 'Requested By',
        maxLength : 100
    }
});