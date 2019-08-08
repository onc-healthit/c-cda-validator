var surveilh2 = angular.module('ttt.surveillance.surveilh2', []);

surveilh2.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('surveillance.surveilh2.main', {
				url: '',
				views: {
					"surveilh2_xdr": {
						templateUrl: 'edge/xdr/xdrMain.tpl.html'
					},
					"surveilh2": {
						templateUrl: 'edge/smtp/smtpMain.tpl.html'
					}
				}
			})
			.state('surveillance.surveilh2.description', {
				url: '/description/:id',
				views: {
					"surveilh2": {
						templateUrl: 'edge/smtp/description/testDescription.tpl.html'
					}
				}
			})
			.state('surveillance.surveilh2.logs', {
				url: '/logs',
				views: {
					"surveilh2": {
						templateUrl: 'edge/smtp/logs/testLog.tpl.html'
					}
				}
			}
			)
			.state('surveillance.surveilh2.xdrdescription', {
				url: '/xdrdescription/',
				params: {
					id: null,
					testObj: null
				},
				views: {
					"surveilh2_xdr": {
						templateUrl: 'edge/xdr/description/xdrTestDescription.tpl.html'
					}
				}
			})
			.state('surveillance.surveilh2.xdrlogs', {
				url: '/xdrlogs',
				views: {
					"surveilh2_xdr": {
						templateUrl: 'edge/xdr/logs/xdrTestLog.tpl.html'
					}
				}
			}
			);
	}
]);