var ccda = angular.module('ttt.ccda', [
	// Modules
	'ttt.direct.ccdar2Validator',
	'ttt.direct.help'
]);

ccda.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('ccda', {
				url: '/ccda',
				params: {
					paramsObj: null
				},
				abstract: true,
				views: {
					"main": {
						controller: 'CCDAR2ValidatorCtrl',
						templateUrl: 'ccda/ccda.tpl.html'
					}
				},
				data: {
					pageTitle: 'CCDA'
				}
			})
			.state('ccda.home', {
				url: '',
				views: {
					"ccda": {
						controller: 'CCDAR2ValidatorCtrl',
						templateUrl: 'ccda/ccda-r2.tpl.html'
					}
				}
			})
			.state('ccda.documents', {
				url: '/documents',
				views: {
					"ccda": {
						controller: 'DocumentsCtrl',
						templateUrl: 'templates/documents.tpl.html'
					}
				}
			})
			.state('ccda.help', {
				url: '/help',
				views: {
					"ccda": {
						controller: 'DirectHelpCtrl',
						templateUrl: 'ccda/help/help.tpl.html'
					}
				}
			})
			.state('ccda.changePassword', {
				url: '/changePassword',
				views: {
					"ccda": {
						controller: 'ChangePasswordCtrl',
						templateUrl: 'templates/changePassword.tpl.html'
					}
				}
			})
            .state('ccda.faq', {
                url: '/faq',
                views: {
                    "ccda": {
                        controller: 'MarkDownCtrl',
                        templateUrl: 'templates/faq.tpl.html'
                    }
                },
                data: {
                    moduleInfo: "faq"
                }
            })
             .state('ccda.announcement', {
                url: '/announcement',
                views: {
                  "ccda": {
                        controller: 'MarkDownCtrl',
                        templateUrl: 'templates/announcement.tpl.html'
                    }
                },
                data: {
                    moduleInfo: "announcement"
                }
            })
			.state('ccda.releaseNotes', {
               url: '/releaseNotes',
                views: {
                    "ccda": {
                        controller: 'ReleaseNotesCtrl',
                        templateUrl: 'templates/releaseNotes.tpl.html'
                    }
                }
            })
            .state('ccda.accountInfo', {
				url: '/accountInfo',
				views: {
					"ccda": {
						controller: 'AccountInfoCtrl',
						templateUrl: 'templates/accountInfo.tpl.html'
					}
				}
			});
	}
]);

ccda.controller('DirectCtrl', ['$scope', '$stateParams', 'SettingsFactory', 'PropertiesFactory',
	function($scope, $stateParams, SettingsFactory, PropertiesFactory) {
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
