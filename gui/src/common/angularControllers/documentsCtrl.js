var documentsCtrl = angular.module('ttt.documents', []);

edgeHelp.controller('DocumentsCtrl', ['$scope',
    function($scope) {
        $scope.pdfUrl = 'https://github.com/siteadmin/ett/raw/resources/documentation/guides/userguide.pdf';
        $scope.pdfName = 'Edge Testing Tool User Guide';
        $scope.scroll = 0;

        $scope.documentsLink = ["170 314(b)(8)_ATLreview_20150313.pdf", "170.314(e)(1)_20150123.pdf"];

        $scope.getNavStyle = function(scroll) {
            if (scroll > 100) {
                return 'pdf-controls fixed';
            } else {
                return 'pdf-controls';
            }
        };
    }
]);
