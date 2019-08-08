var edgePop3 = angular.module('ttt.hisp.pop', []);

edgePop3.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('hisp.pop.main', {
				url: '',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/smtpMain.tpl.html'
					}
				}
			})
			.state('hisp.pop.description', {
				url: '/description/:id',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/description/testDescription.tpl.html'
					}
				}
			})
			.state('hisp.pop.logs', {
				url: '/logs',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/logs/testLog.tpl.html'
					}
				}
			});

	}
]);