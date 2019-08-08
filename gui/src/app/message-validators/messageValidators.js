var validators = angular.module('ttt.validators', [
    // Modules
    'ttt.direct.ccdar2Validator',
    'ttt.direct.ccdaValidator',
    'ttt.direct.xdmValidator',
    'ttt.validator.home'
]);

validators.config(['$stateProvider',
    function($stateProvider) {
        $stateProvider.state('validators', {
                url: '/validators',
				params: {
					paramsObj: null
				},
                abstract: true,
                views: {
                    "main": {
                        controller: 'MessageValidatorsCtrl',
                        templateUrl: 'message-validators/messageValidators.tpl.html'
                    }
                },
                data: {
                    pageTitle: 'Message Validators'
                }
            })
            .state('validators.ccdar2', {
                url: '/ccdar2',
                views: {
                    "validators": {
                        controller: 'CCDAR2ValidatorCtrl',
                        templateUrl: 'direct/message-validator/ccda-r2/ccda-r2.tpl.html'
                    }
                }
            })
            .state('validators.home', {
                url: '',
                views: {
                    "validators": {
                        controller: 'EdgeHomeCtrl',
                        templateUrl: 'message-validators/validator-home/validator-home.tpl.html'
                    }
                }
            })
            .state('validators.ccdar1', {
                url: '/ccdar1',
                views: {
                    "validators": {
                        controller: 'CCDAValidatorCtrl',
                        templateUrl: 'direct/message-validator/ccda/ccda-validator.tpl.html'
                    }
                }
            }).state('validators.xdm', {
                url: '/xdm',
                views: {
                    "validators": {
                        controller: 'XDMValidatorCtrl',
                        templateUrl: 'message-validators/xdm-validator/xdm-validator.tpl.html'
                    }
                }
            }).state('validators.xdr', {
                url: '/xdr',
                views: {
                    "validators": {
                        controller: 'XDRValidatorCtrl',
                        templateUrl: 'message-validators/xdr-validator/xdr-validator.tpl.html'
                    }
                }
            }).state('validators.direct', {
                url: '/direct',
                views: {
                    "validators": {
                        controller: 'DirectValidatorCtrl',
                        templateUrl: 'direct/message-validator/direct/message-validator.tpl.html'
                    }
                }
            }).state('validators.documents', {
                url: '/documents',
                views: {
                    "validators": {
                        controller: 'DocumentsCtrl',
                        templateUrl: 'templates/documents.tpl.html'
                    }
                }
            })
            .state('validators.help', {
                url: '/help',
                views: {
                    "validators": {
                        controller: 'EdgeHelpCtrl',
                        templateUrl: 'edge/help/help.tpl.html'
                    }
                }
            })
            .state('validators.changePassword', {
                url: '/changePassword',
                views: {
                    "validators": {
                        controller: 'ChangePasswordCtrl',
                        templateUrl: 'templates/changePassword.tpl.html'
                    }
                }
            })
            .state('validators.accountInfo', {
                url: '/accountInfo',
                views: {
                    "validators": {
                        controller: 'AccountInfoCtrl',
                        templateUrl: 'templates/accountInfo.tpl.html'
                    }
                }
            })
            .state('validators.announcement', {
                url: '/announcement',
                views: {
                    "edge": {
                        controller: 'MarkDownCtrl',
                        templateUrl: 'templates/announcement.tpl.html'
                    }
                }
            }).state('validators.releaseNotes', {
                url: '/releaseNotes',
                views: {
                    "validators": {
                        controller: 'ReleaseNotesCtrl',
                        templateUrl: 'templates/releaseNotes.tpl.html'
                    }
                }
            });
    }
]);

validators.controller('MessageValidatorsCtrl', ['$scope', '$stateParams', 'SettingsFactory', 'PropertiesFactory',
    function($scope,$stateParams, SettingsFactory, PropertiesFactory) {
    $scope.paramsObj =  $stateParams.paramsObj;

		$scope.backTo = null;
		if ($stateParams.paramsObj !=null){
			$scope.parmobj = "{paramCri:{'backToCriteria':"+$scope.paramsObj.backToCriteria+",'backToOption':"+$scope.paramsObj.backToOption+"}}";
			$scope.backTo = $scope.paramsObj.goBackTo+"("+$scope.parmobj +")";
		}

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
