var edgeImap = angular.module('ttt.edge.imap', []);

edgeImap.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('edge.imap.main', {
				url: '',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/smtpMain.tpl.html'
					}
				}
			})
			.state('edge.imap.description', {
				url: '/description/:id',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/description/testDescription.tpl.html'
					}
				}
			})
			.state('edge.imap.logs', {
				url: '/logs',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/logs/testLog.tpl.html'
					}
				}
			});

	}
]);