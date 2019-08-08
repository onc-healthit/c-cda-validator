var ttt = angular.module('ttt', [
    'templates-app',
    'templates-common',

    // Modules
    'ttt.home',
    'ttt.direct',
    'ttt.edge',
    'ttt.hisp',
    'ttt.certification',
    'ttt.ccda',
    'ttt.surveillance',
    'ttt.documents',
    'ttt.validators',
    'ttt.admin',

    // Commons
    'ttt.filters',
    'ttt.services',
    'ttt.directServices',
    'ttt.smtpServices',
    'ttt.xdrServices',
    'ttt.directives',
    'ttt.loginCtrl',
    'ttt.adminLoginCtrl',
    'ttt.headerCtrl',
    'ttt.changePassword',
    'ttt.accountInfo',
    'ttt.releaseNotes',
    'ttt.markdown',
    'cancelableService',

    // Libs
    'ngSanitize',
    'ngAnimate',
    'ui.router',
    'ui.bootstrap',
    'angular-growl',
    'ui.select',
    'flow',
    'ngIdle',
    'angular-loading-bar',
    'angular-ladda',
    'toggle-switch',
    'angularSpinner',
    'ng.shims.placeholder',
    'selectionModel',
    'nsPopover',
    '720kb.tooltips',
    'finderTree',
    'ngDropover',
    'angular-clipboard',
    'angulartics', 'angulartics.google.analytics'
]);

ttt.config(['$analyticsProvider',
    function($analyticsProvider) {
        $analyticsProvider.firstPageview(true);
        $analyticsProvider.withAutoBase(true);
    }
]);


ttt.config(['$stateProvider', '$urlRouterProvider',
    function($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/ccda');
    }
]);

ttt.config(['growlProvider',
    function(growlProvider) {
        growlProvider.globalTimeToLive(3000);
    }
]);

ttt.config(['KeepaliveProvider', 'IdleProvider',
    function(KeepaliveProvider, IdleProvider) {
        IdleProvider.idle(60 * 1440);
        IdleProvider.timeout(60 * 1440);
        // KeepaliveProvider.interval(10);
    }
]);

// Run idle as soon as the app starts
ttt.run(['Idle',
    function(Idle) {
        Idle.watch();
    }
]);

ttt.config(['$httpProvider',
    function($httpProvider) {
        $httpProvider.defaults.withCredentials = true;
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
]);

ttt.controller('AppCtrl', ['$scope', '$location',
    function($scope, $location) {
        $scope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams) {
            if (angular.isDefined(toState.data.pageTitle)) {
                $scope.pageTitle = toState.data.pageTitle + ' | ETT';
            }
        });
    }
]);

// Exception handler
ttt.config(['$provide',
    function($provide) {
        $provide.decorator('$exceptionHandler', ['$delegate', '$injector',
            function($delegate, $injector) {
                return function(exception, cause) {
                    $delegate(exception, cause);

                    var $uibModal = $injector.get("$uibModal");

                    var modalInstance = $uibModal.open({
                        templateUrl: 'templates/exception.tpl.html',
                        controller: ('ErrorHandlerCtrl', ['$scope', '$uibModalInstance',
                            function($scope, $uibModalInstance) {
                                $scope.modalTitle = "Error ";

                                $scope.excode = exception.code;

                                $scope.exceptionText = exception.message;
                                $scope.cause = cause;
                                $scope.url = exception.url;

                                $scope.close = function() {
                                    $uibModalInstance.dismiss("Ok");
                                };
                            }
                        ])
                    });
                };
            }
        ]);
    }
]);
