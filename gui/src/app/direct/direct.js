var direct = angular.module('ttt.direct', [
	// Modules
	'ttt.direct.home',
	'ttt.direct.register',
	'ttt.direct.send',
	'ttt.direct.validator',
	'ttt.direct.ccdaValidator',
	'ttt.direct.ccdar2Validator',
	'ttt.direct.dcdtValidator',
	'ttt.direct.status',
	'ttt.direct.report',
	'ttt.direct.help'
]);

direct.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('direct', {
				url: '/direct',
				params: {
					paramsObj: null
				},
				abstract: true,
				views: {
					"main": {
						controller: 'DirectCtrl',
						templateUrl: 'direct/direct.tpl.html'
					}
				},
				data: {
					pageTitle: 'Direct'
				}
			})
			.state('direct.home', {
				url: '',
				views: {
					"direct": {
						controller: 'DirectHomeCtrl',
						templateUrl: 'direct/direct-home/direct-home.tpl.html'
					}
				}
			})
			.state('direct.register', {
				url: '/register',
				views: {
					"direct": {
						controller: 'RegisterCtrl',
						templateUrl: 'direct/register/register.tpl.html'
					}
				}
			})
			.state('direct.send', {
				url: '/send',
				views: {
					"direct": {
						controller: 'DirectSendCtrl',
						templateUrl: 'direct/send/send.tpl.html'
					}
				}
			})
			.state('direct.validator', {
				url: '/validator/direct',
				views: {
					"direct": {
						controller: 'DirectValidatorCtrl',
						templateUrl: 'direct/message-validator/direct/message-validator.tpl.html'
					}
				}
			})
			.state('direct.ccda', {
				url: '/validator/ccda',
				views: {
					"direct": {
						controller: 'CCDAValidatorCtrl',
						templateUrl: 'direct/message-validator/ccda/ccda-validator.tpl.html'
					}
				}
			})
			.state('direct.ccdar2', {
				url: '/validator/ccda/r2',
				views: {
					"direct": {
						controller: 'CCDAR2ValidatorCtrl',
						templateUrl: 'direct/message-validator/ccda-r2/ccda-r2.tpl.html'
					}
				}
			})
			.state('direct.dcdt1', {
				url: '/certdiscovery/dcdt1',
				views: {
					"direct": {
						controller: 'DCDTValidatorCtrl',
						templateUrl: 'direct/cert-discovery/dcdt.tpl.html'
					}
				},
                data: {
                    pageTitle:'2014'
                }
			})
			.state('direct.dcdt2', {
				url: '/certdiscovery/dcdt2',
				views: {
					"direct": {
						controller: 'DCDTValidatorCtrl',
						templateUrl: 'direct/cert-discovery/dcdt.tpl.html'
					}
				},
                data: {
                    pageTitle:'2015'
                }
			})
			.state('direct.status', {
				url: '/status',
				views: {
					"direct": {
						controller: 'DirectStatusCtrl',
						templateUrl: 'direct/status/status.tpl.html'
					}
				}
			})
			.state('direct.report', {
				url: '/report/:message_id',
				views: {
					"direct": {
						controller: 'DirectReportCtrl',
						templateUrl: 'direct/validation-report/validation-report.tpl.html'
					}
				}
			})
			.state('direct.documents', {
				url: '/documents',
				views: {
					"direct": {
						controller: 'DocumentsCtrl',
						templateUrl: 'templates/documents.tpl.html'
					}
				}
			})
			.state('direct.help', {
				url: '/help',
				views: {
					"direct": {
						controller: 'DirectHelpCtrl',
						templateUrl: 'direct/help/help.tpl.html'
					}
				}
			})
			.state('direct.changePassword', {
				url: '/changePassword',
				views: {
					"direct": {
						controller: 'ChangePasswordCtrl',
						templateUrl: 'templates/changePassword.tpl.html'
					}
				}
			})
            .state('direct.faq', {
                url: '/faq',
                views: {
                    "direct": {
                        controller: 'MarkDownCtrl',
                        templateUrl: 'templates/faq.tpl.html'
                    }
                },
                data: {
                    moduleInfo: "faq"
                }
            })
             .state('direct.announcement', {
                url: '/announcement',
                views: {
                  "direct": {
                        controller: 'MarkDownCtrl',
                        templateUrl: 'templates/announcement.tpl.html'
                    }
                },
                data: {
                    moduleInfo: "announcement"
                }
            })
			.state('direct.releaseNotes', {
               url: '/releaseNotes',
                views: {
                    "direct": {
                        controller: 'ReleaseNotesCtrl',
                        templateUrl: 'templates/releaseNotes.tpl.html'
                    }
                }
            })
            .state('direct.accountInfo', {
				url: '/accountInfo',
				views: {
					"direct": {
						controller: 'AccountInfoCtrl',
						templateUrl: 'templates/accountInfo.tpl.html'
					}
				}
			});
	}
]);

direct.controller('DirectCtrl', ['$scope', '$stateParams', 'SettingsFactory', 'PropertiesFactory',
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
