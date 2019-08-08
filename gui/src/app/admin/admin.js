var admin = angular.module('ttt.admin', [
    // Modules
]);

admin.config(['$stateProvider',
    function($stateProvider) {
        $stateProvider.state('admin', {
                url: '/admin',
                abstract: true,
                views: {
                    "main": {
                        controller: 'AdminCtrl',
                        templateUrl: 'admin/admin.tpl.html'
                    }
                },
                data: {
                    pageTitle: 'Admin',
                     requireLogin: true
                }
            })
            .state('admin.logs', {
                url: '',
                views: {
                    "admin": {
                        controller: 'LogsViewCtrl',
                        templateUrl: 'admin/logs/logs.tpl.html'
                    }
                }
            }).state('admin.settings', {
                url: '',
                views: {
                    "admin": {
                        controller: 'LogsViewCtrl',
                        templateUrl: 'admin/logs/logs.tpl.html'
                    }
                }
            });
    }
]);

admin.run(function ($rootScope,$state, LoginModal) {
  $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
    var requireLogin = toState.data.requireLogin;
    if (requireLogin && typeof $rootScope.currentUser === 'undefined') {
      event.preventDefault();
      new LoginModal()
        .then(function () {
           return $state.go(toState.name, toParams);
        });
    }
  });

});
admin.controller('AdminCtrl', ['$scope', 'SettingsFactory', 'PropertiesFactory', 'LogViewFactory', 'LogViewLevelFactory',
    function($scope, SettingsFactory, PropertiesFactory, LogViewFactory, LogViewLevelFactory) {

        SettingsFactory.getSettings(function(result) {
            $scope.settings = result.data;
        }, function(error) {
            $scope.error = true;
        });

        PropertiesFactory.get(function(result) {
            $scope.properties = result;
        });
    }
]);

admin.controller('LoginModalCtrl', function ($scope, UsersApi) {
  this.cancel = $scope.$dismiss;
  this.submit = function (email, password) {
    UsersApi.login(email, password).then(function (user) {
      $scope.$close(user);
    });
  };

});

admin.controller('LogsViewCtrl', ['$scope', 'LogViewFactory', 'LogViewLevelFactory', 'AllLogFilesFactory',
    function($scope, LogViewFactory, LogViewLevelFactory, AllLogFilesFactory) {

        $scope.allLogFiles = AllLogFilesFactory.query();

        $scope.allLogFiles.$promise.then(function(data) {

            // Remove the non files from array
            $scope.allLogFiles = $scope.allLogFiles.filter(function(item) {
                return (/^.*\.[a-zA-Z]+$/i).test(item);
            }).reverse();

        });

        $scope.selectedFile = 'catalina.out';

        $scope.Alllogs = LogViewFactory.get({
            file: $scope.selectedFile
        }, function(data) {
            $scope.Alllogs = data;
            $scope.Alllogs.file = $scope.escapeHtml(data.file);
            $scope.logs = {
                'file': $scope.Alllogs.file
            };
        }, function(error) {
            throw {
                code: error.data.code,
                url: error.data.url,
                message: error.data.message
            };
        });

        $scope.logLevel = ['ALL'];

        $scope.searchOn = false;

        // Log level: add/remove level
        $scope.toggleLevel = function(level) {
            if (level === 'ALL') {
                $scope.logLevel = ['ALL'];
                $scope.logs = {
                    'file': $scope.Alllogs.file
                };
            } else {
                // Level that was alrady in the array has been clicked: Remove it from array
                if ($scope.logLevel.indexOf(level) !== -1) {
                    $scope.logLevel = $scope.logLevel.filter(function(item) {
                        return item !== level;
                    });
                } else {
                    // Remove ALL from array and add new level
                    var arrayWithoutAll = $scope.logLevel.filter(function(item) {
                        return item !== 'ALL';
                    });
                    arrayWithoutAll.push(level);
                    $scope.logLevel = arrayWithoutAll;
                }
                $scope.logs = LogViewLevelFactory.save({
                    "levels": $scope.logLevel,
                    "logs": $scope.searchOn ? $scope.logs.file : $scope.Alllogs.file,
                    "noStacktrace": $scope.noStacktrace
                });
            }
            if ($scope.logLevel.length === 0) {
                $scope.logLevel = ['ALL'];
                $scope.logs = $scope.Alllogs;
            }
        };

        $scope.shouldBeActive = function(level) {
            return ($scope.logLevel.indexOf(level) !== -1);
        };

        $scope.escapeHtml = function(str) {
            // Escape HTMl first
            var entityMap = {
                "&": "&amp;",
                "<": "&lt;",
                ">": "&gt;",
                '"': '&quot;',
                "'": '&#39;',
                "/": '&#x2F;'
            };
            text = String(str).replace(/[&<>"'\/]/g, function(s) {
                return entityMap[s];
            });
            return text;
        };

        $scope.changeLogFile = function(file) {
            $scope.selectedFile = file;
            $scope.Alllogs = LogViewFactory.get({
                file: $scope.selectedFile
            }, function(data) {
                $scope.Alllogs = data;
                $scope.Alllogs.file = $scope.escapeHtml(data.file);
                $scope.logs = {
                    'file': $scope.Alllogs.file
                };
            }, function(error) {
                throw {
                    code: error.data.code,
                    url: error.data.url,
                    message: error.data.message
                };
            });
        };

        // Monitor search
        $scope.$watch('search', function(newV, oldV) {
            if (newV === undefined || newV === "") {
                $scope.logs = {
                    'file': $scope.Alllogs.file
                };
            } else {
                $scope.logs = LogViewFactory.save({
                    "logs": $scope.Alllogs.file,
                    "grep": newV,
                    "grepAContext": 0,
                    "grepBContext": 0
                }, function(data) {
                    $scope.logs.file = data.file.replace(new RegExp('(' + newV + ')', 'gi'), '<span class="highlighting">$1</span>');
                });
            }
        });
    }
]);
