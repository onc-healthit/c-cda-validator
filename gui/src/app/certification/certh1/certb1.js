var certCertb1 = angular.module('ttt.certification.certb1', []);

certCertb1.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('certification.certb1.main', {
				url: '',
				views:  {
					"certb1_xdr": {
						templateUrl: 'edge/xdr/xdrMain.tpl.html'
					},
					"certb1": {
						templateUrl: 'edge/smtp/smtpMain.tpl.html'
					}
				}
			})
			.state('certification.certb1.description', {
				url: '/description/:id',
				views: {
					"certb1": {
						templateUrl: 'edge/smtp/description/testDescription.tpl.html'
					}
				}
			})
			.state('certification.certb1.logs', {
				url: '/logs',
				views: {
					"certb1": {
						templateUrl: 'edge/smtp/logs/testLog.tpl.html'
					}
				}
			}
			)
			.state('certification.certb1.xdrdescription', {
				url: '/xdrdescription/',
				params: {
					id: null,
					testObj: null
				},
				views: {
					"certb1_xdr": {
						templateUrl: 'edge/xdr/description/xdrTestDescription.tpl.html'
					}
				}
			})
			.state('certification.certb1.xdrlogs', {
				url: '/xdrlogs',
				views: {
					"certb1_xdr": {
						templateUrl: 'edge/xdr/logs/xdrTestLog.tpl.html'
					}
				}
			}
			);
	}
]);