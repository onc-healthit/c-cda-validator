var survb1 = angular.module('ttt.surveillance.surveilb1', []);

survb1.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('surveillance.surveilb1.main', {
				url: '',
				views:  {
					"surveilb1_xdr": {
						templateUrl: 'edge/xdr/xdrMain.tpl.html'
					},
					"surveilb1": {
						templateUrl: 'edge/smtp/smtpMain.tpl.html'
					}
				}
			})
			.state('surveillance.surveilb1.description', {
				url: '/description/:id',
				views: {
					"surveilb1": {
						templateUrl: 'edge/smtp/description/testDescription.tpl.html'
					}
				}
			})
			.state('surveillance.surveilb1.logs', {
				url: '/logs',
				views: {
					"surveilb1": {
						templateUrl: 'edge/smtp/logs/testLog.tpl.html'
					}
				}
			}
			)
			.state('surveillance.surveilb1.xdrdescription', {
				url: '/xdrdescription/',
				params: {
					id: null,
					testObj: null
				},
				views: {
					"surveilb1_xdr": {
						templateUrl: 'edge/xdr/description/xdrTestDescription.tpl.html'
					}
				}
			})
			.state('surveillance.surveilb1.xdrlogs', {
				url: '/xdrlogs',
				views: {
					"surveilb1_xdr": {
						templateUrl: 'edge/xdr/logs/xdrTestLog.tpl.html'
					}
				}
			}
			);
	}
]);