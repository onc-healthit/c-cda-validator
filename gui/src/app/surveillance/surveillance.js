var surveilMod = angular.module('ttt.surveillance', [
    // Modules
    'ttt.surveillance.surveilh1',
    'ttt.surveillance.surveilh2',
    'ttt.surveillance.surveilb1',
    'ttt.certification.certh1'
]);

surveilMod.config(['$stateProvider',
    function($stateProvider) {
        $stateProvider.state('surveillance', {
            url: '/surveillance',
				params: {
					paramCri: null
				},
            abstract: true,
            views: {
                "main": {
                    controller: 'SurveillanceCriteriaCtrl',
                    templateUrl: 'surveillance/surveillanceCriteria.tpl.html'
                }
            },
            data: {
                pageTitle: 'Surveillance'
            }
        })
            .state('surveillance.home', {
                url: '',
                views: {
                    "surveillance": {
                        controller: 'EdgeHomeCtrl',
                        templateUrl: 'surveillance/surveil-home.tpl.html'
                    }
                }
            })
            .state('surveillance.surveilb1', {
                url: '/surveilb1',
                abstract: true,
                views: {
                    "surveillance": {
                        controller: 'Certh1Ctrl',
                        templateUrl: 'certification/certificationCriteriah1.tpl.html'
                    }
                },
                data: {
                    filterCrit: 'su1',
                    pageTitle:' 170.315(b)(1)'
                }
            })
            .state('surveillance.surveilh1', {
                url: '/surveilh1',
                abstract: true,
                views: {
                    "surveillance": {
                        controller: 'Certh1Ctrl',
                        templateUrl: 'certification/certificationCriteriah1.tpl.html'
                    }
                },
                data: {
                    filterCrit: 'sa1',
                    pageTitle:' 170.315(h)(1)'
                }
            })
            .state('surveillance.surveilh2', {
                url: '/surveilh2',
                abstract: true,
                views: {
                    "surveillance": {
                        controller: 'Certh1Ctrl',
                        templateUrl: 'certification/certificationCriteriah1.tpl.html'
                    }
                },
                data: {
                    filterCrit: 'sc2',
                    pageTitle:' 170.315(h)(2)'
                }
            })
            .state('surveillance.documents', {
                url: '/documents',
                views: {
                    "surveillance": {
                        controller: 'DocumentsCtrl',
                        templateUrl: 'templates/documents.tpl.html'
                    }
                }
            })
            .state('surveillance.help', {
                url: '/help',
                views: {
                    "surveillance": {
                        controller: 'EdgeHelpCtrl',
                        templateUrl: 'edge/help/help.tpl.html'
                    }
                }
            });

    }
]);

surveilMod.controller('SurveillanceCriteriaCtrl', ['$scope', 'PropertiesFactory',
    function($scope, PropertiesFactory) {
        PropertiesFactory.get(function(result) {
            $scope.properties = result;
        });
    }
]);