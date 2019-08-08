var certMod = angular.module('ttt.certification', [
    // Modules
    'ttt.certification.certh1',
    'ttt.certification.certh2',
    'ttt.certification.certb1'
]);

certMod.config(['$stateProvider',
    function($stateProvider) {
        $stateProvider.state('certification', {
            url: '/certification',
				params: {
					paramCri: null
				},
            abstract: true,
            views: {
                "main": {
                    controller: 'CertificationCriteriaCtrl',
                    templateUrl: 'certification/certificationCriteria.tpl.html'
                }
            },
            data: {
                pageTitle: 'Certification'
            }
        })
            .state('certification.home', {
                url: '',
                views: {
                    "certification": {
                        controller: 'EdgeHomeCtrl',
                        templateUrl: 'certification/certification-home/certification-home.tpl.html'
                    }
                }
            })
            .state('certification.certb1', {
                url: '/certb1',
                abstract: true,
                views: {
                    "certification": {
                        controller: 'Certh1Ctrl',
                        templateUrl: 'certification/certificationCriteriah1.tpl.html'
                    }
                },
                data: {
                    filterCrit: 'b1',
                    pageTitle:' 170.315(b)(1)'
                }
            })
            .state('certification.certh1', {
                url: '/certh1',
                abstract: true,
                views: {
                    "certification": {
                        controller: 'Certh1Ctrl',
                        templateUrl: 'certification/certificationCriteriah1.tpl.html'
                    }
                },
                data: {
                    filterCrit: 'h1',
                    pageTitle:' 170.315(h)(1)'
                }
            })
            .state('certification.certh2', {
                url: '/certh2',
                abstract: true,
                views: {
                    "certification": {
                        controller: 'Certh1Ctrl',
                        templateUrl: 'certification/certificationCriteriah1.tpl.html'
                    }
                },
                data: {
                    filterCrit: 'h2',
                    pageTitle:' 170.315(h)(2)'
                }
            })
            .state('certification.documents', {
                url: '/documents',
                views: {
                    "certification": {
                        controller: 'DocumentsCtrl',
                        templateUrl: 'templates/documents.tpl.html'
                    }
                }
            })
            .state('certification.help', {
                url: '/help',
                views: {
                    "certification": {
                        controller: 'EdgeHelpCtrl',
                        templateUrl: 'edge/help/help.tpl.html'
                    }
                }
            });

    }
]);

certMod.controller('CertificationCriteriaCtrl', ['$scope', 'PropertiesFactory',
    function($scope, PropertiesFactory) {
        PropertiesFactory.get(function(result) {
            $scope.properties = result;
        });
    }
]);