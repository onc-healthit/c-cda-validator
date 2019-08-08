var edge = angular.module('ttt.edge', [
    // Modules
    'ttt.edge.home',
    'ttt.edge.smtp',
    'ttt.edge.imap',
    'ttt.edge.pop',
    'ttt.edge.mu2',
    'ttt.edge.smtp.description',
    'ttt.edge.reports',
    'ttt.edge.xdr',
    'ttt.edge.xdr.description',
    'ttt.edge.help'

]);

edge.config(['$stateProvider',
    function($stateProvider) {
        $stateProvider.state('edge', {
            url: '/edge',
            abstract: true,
            views: {
                "main": {
                    controller: 'EdgeCtrl',
                    templateUrl: 'edge/edge.tpl.html'
                }
            },
            data: {
                pageTitle: 'Edge'
            }
        })
            .state('edge.home', {
                url: '',
                views: {
                    "edge": {
                        controller: 'EdgeHomeCtrl',
                        templateUrl: 'edge/edge-home/edge-home.tpl.html'
                    }
                }
            })
            .state('edge.smtp', {
                url: '/smtp',
                abstract: true,
                views: {
                    "edge": {
                        controller: 'SmtpCtrl',
                        templateUrl: 'edge/smtp/smtp.tpl.html'
                    }
                },
                data: {
                    sutEge: true,
                    protocol: "smtp"
                }
            })
            .state('edge.imap', {
                url: '/imap',
                abstract: true,
                views: {
                    "edge": {
                        controller: 'SmtpCtrl',
                        templateUrl: 'edge/smtp/smtp.tpl.html'
                    }
                },
                data: {
                    sutEge: true,
                    protocol: "imap"
                }
            })
            .state('edge.pop', {
                url: '/pop',
                abstract: true,
                views: {
                    "edge": {
                        controller: 'SmtpCtrl',
                        templateUrl: 'edge/smtp/smtp.tpl.html'
                    }
                },
                data: {
                    sutEge: true,
                    protocol: "pop"
                }
            })
            .state('edge.mu2', {
                url: '/mu2',
                abstract: true,
                views: {
                    "edge": {
                        controller: 'SmtpCtrl',
                        templateUrl: 'edge/smtp/smtp.tpl.html'
                    }
                },
                data: {
                    sutEge: true,
                    protocol: "mu2"
                }
            })
            .state('edge.xdr', {
                url: '/xdr',
                abstract: true,
                views: {
                    "edge": {
                        controller: 'XdrCtrl',
                        templateUrl: 'edge/xdr/xdr.tpl.html'
                    }
                },
                data: {
                    sutEge: true,
                    protocol: "xdr"
                }
            })
            .state('edge.reports', {
                url: '/reports',
                views: {
                    "edge": {
                        controller: 'EdgeReportsCtrl',
                        templateUrl: 'edge/reports/validation-reports.tpl.html'
                    }
                },
                data: {
                    sutEge: true
                }
            })
            .state('edge.documents', {
                url: '/documents',
                views: {
                    "edge": {
                        controller: 'DocumentsCtrl',
                        templateUrl: 'templates/documents.tpl.html'
                    }
                }
            })
            .state('edge.help', {
                url: '/help',
                views: {
                    "edge": {
                        controller: 'EdgeHelpCtrl',
                        templateUrl: 'edge/help/help.tpl.html'
                    }
                }
            })
            .state('edge.changePassword', {
                url: '/changePassword',
                views: {
                    "edge": {
                        controller: 'ChangePasswordCtrl',
                        templateUrl: 'templates/changePassword.tpl.html'
                    }
                }
            })
            .state('edge.accountInfo', {
                url: '/accountInfo',
                views: {
                    "edge": {
                        controller: 'AccountInfoCtrl',
                        templateUrl: 'templates/accountInfo.tpl.html'
                    }
                }
            })
            .state('edge.announcement', {
                url: '/announcement',
                views: {
                    "edge": {
                        controller: 'MarkDownCtrl',
                        templateUrl: 'templates/announcement.tpl.html'
                    }
                },
                data: {
                    moduleInfo: "announcement"
                }
            }).state('edge.faq', {
                url: '/faq',
                views: {
                    "edge": {
                        controller: 'MarkDownCtrl',
                        templateUrl: 'templates/faq.tpl.html'
                    }
                },
                data: {
                    moduleInfo: "faq"
                }
            }).state('edge.localinstall', {
                url: '/localinstall',
                views: {
                    "edge": {
                        controller: 'MarkDownCtrl',
                        templateUrl: 'templates/localinstall.tpl.html'
                    }
                },
                data: {
                    moduleInfo: "localinstall"
                }
            }).state('edge.releaseNotes', {
                url: '/releaseNotes',
                views: {
                    "edge": {
                        controller: 'ReleaseNotesCtrl',
                        templateUrl: 'templates/releaseNotes.tpl.html'
                    }
                }
            });

    }
]);

edge.controller('EdgeCtrl', ['$scope', 'PropertiesFactory',
    function($scope, PropertiesFactory) {
        PropertiesFactory.get(function(result) {
            $scope.properties = result;
        });
    }
]);