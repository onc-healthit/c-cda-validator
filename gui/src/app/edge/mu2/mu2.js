var edgeMU2 = angular.module('ttt.edge.mu2', []);

edgeMU2.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('edge.mu2.main', {
				url: '',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/smtpMain.tpl.html'
					}
				}
			})
			.state('edge.mu2.description', {
				url: '/description/:id',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/description/testDescription.tpl.html'
					}
				}
			})
			.state('edge.mu2.logs', {
				url: '/logs',
				views: {
					"smtp": {
						templateUrl: 'edge/smtp/logs/testLog.tpl.html'
					}
				}
			});

	}
]);