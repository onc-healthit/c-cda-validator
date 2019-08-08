var certCerth2 = angular.module('ttt.certification.certh2', []);

certCerth2.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('certification.certh2.main', {
				url: '',
				views: {
					"certh2_xdr": {
						templateUrl: 'edge/xdr/xdrMain.tpl.html'
					},
					"certh2": {
						templateUrl: 'edge/smtp/smtpMain.tpl.html'
					}
				}
			})
			.state('certification.certh2.description', {
				url: '/description/:id',
				views: {
					"certh2": {
						templateUrl: 'edge/smtp/description/testDescription.tpl.html'
					}
				}
			})
			.state('certification.certh2.logs', {
				url: '/logs',
				views: {
					"certh2": {
						templateUrl: 'edge/smtp/logs/testLog.tpl.html'
					}
				}
			}
			)
			.state('certification.certh2.xdrdescription', {
				url: '/xdrdescription/',
				params: {
					id: null,
					testObj: null
				},
				views: {
					"certh2_xdr": {
						templateUrl: 'edge/xdr/description/xdrTestDescription.tpl.html'
					}
				}
			})
			.state('certification.certh2.xdrlogs', {
				url: '/xdrlogs',
				views: {
					"certh2_xdr": {
						templateUrl: 'edge/xdr/logs/xdrTestLog.tpl.html'
					}
				}
			}
			);
	}
]);