var hispImap = angular.module('ttt.hisp.imap', []);

hispImap.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('hisp.imap.main', {
				url: '',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/smtpMain.tpl.html'
					}
				}
			})
			.state('hisp.imap.description', {
				url: '/description/:id',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/description/testDescription.tpl.html'
					}
				}
			})
			.state('hisp.imap.logs', {
				url: '/logs',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/logs/testLog.tpl.html'
					}
				}
			});

	}
]);