var hisp = angular.module('ttt.hisp', [
    // Modules
    'ttt.hisp.home',
    'ttt.hisp.smtp',
    'ttt.hisp.imap',
    'ttt.hisp.pop',
    'ttt.hisp.mu2',
    'ttt.hisp.smtp.description',
    'ttt.hisp.reports',
    'ttt.hisp.xdr',
    'ttt.hisp.help'

]);

hisp.config(['$stateProvider',
    function($stateProvider) {
        $stateProvider.state('hisp', {
                url: '/hisp',
                abstract: true,
                views: {
                    "main": {
                        controller: 'HispCtrl',
                        templateUrl: 'hisp/hisp.tpl.html'
                    }
                },
                data: {
                    pageTitle: 'Hisp'
                }
            })
            .state('hisp.home', {
                url: '',
                views: {
                    "hisp": {
                        controller: 'EdgeHomeCtrl',
                        templateUrl: 'hisp/hisp-home/hisp-home.tpl.html'
                    }
                }
            })
            .state('hisp.smtp', {
                url: '/smtp',
                abstract: true,
                views: {
                    "hisp": {
                        controller: 'SmtpCtrl',
                        templateUrl: 'edge/smtp/smtp.tpl.html'
                    }
                },
                data: {
                    sutEge: false,
                    protocol: "smtp"
                }
            })
            .state('hisp.imap', {
                url: '/imap',
                abstract: true,
                views: {
                    "hisp": {
                        controller: 'SmtpCtrl',
                        templateUrl: 'edge/smtp/smtp.tpl.html'
                    }
                },
                data: {
                    sutEge: false,
                    protocol: "imap"
                }
            })
            .state('hisp.pop', {
                url: '/pop',
                abstract: true,
                views: {
                    "hisp": {
                        controller: 'SmtpCtrl',
                        templateUrl: 'edge/smtp/smtp.tpl.html'
                    }
                },
                data: {
                    sutEge: false,
                    protocol: "pop"
                }
            })
            .state('hisp.mu2', {
                url: '/mu2',
                abstract: true,
                views: {
                    "hisp": {
                        controller: 'SmtpCtrl',
                        templateUrl: 'edge/smtp/smtp.tpl.html'
                    }
                },
                data: {
                    sutEge: false,
                    protocol: "mu2"
                }
            })
            .state('hisp.xdr', {
                url: '/xdr',
                abstract: true,
                views: {
                    "hisp": {
                        controller: 'XdrCtrl',
                        templateUrl: 'edge/xdr/xdr.tpl.html'
                    }
                },
                data: {
                    sutEge: false,
                    protocol: "xdr"
                }
            })
            .state('hisp.reports', {
                url: '/reports',
                views: {
                    "hisp": {
                        controller: 'HispReportsCtrl',
                        templateUrl: 'hisp/reports/validation-reports.tpl.html'
                    }
                }
            })
            .state('hisp.documents', {
                url: '/documents',
                views: {
                    "hisp": {
						controller: 'DocumentsCtrl',
                        templateUrl: 'templates/documents.tpl.html'
                    }
                }
            })
            .state('hisp.help', {
                url: '/help',
                views: {
                    "hisp": {
                        controller: 'EdgeHelpCtrl',
                        templateUrl: 'edge/help/help.tpl.html'
                    }
                }
            })
            .state('hisp.changePassword', {
                url: '/changePassword',
                views: {
                    "hisp": {
                        controller: 'ChangePasswordCtrl',
                        templateUrl: 'templates/changePassword.tpl.html'
                    }
                }
            })
            .state('hisp.accountInfo', {
                url: '/accountInfo',
                views: {
                    "hisp": {
                        controller: 'AccountInfoCtrl',
                        templateUrl: 'templates/accountInfo.tpl.html'
                    }
                }
            })
            .state('hisp.announcement', {
                url: '/announcement',
                views: {
                    "hisp": {
                        controller: 'MarkDownCtrl',
                        templateUrl: 'templates/announcement.tpl.html'
                    }
                }
            }).state('hisp.releaseNotes', {
                url: '/releaseNotes',
                views: {
                    "hisp": {
                        controller: 'ReleaseNotesCtrl',
                        templateUrl: 'templates/releaseNotes.tpl.html'
                    }
                }
            });
    }
]);

hisp.controller('HispCtrl', ['$scope', 'PropertiesFactory',
    function($scope, PropertiesFactory) {
        PropertiesFactory.get(function(result) {
            $scope.properties = result;
        });
    }
]);
