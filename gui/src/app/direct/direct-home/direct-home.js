var directHome = angular.module('ttt.direct.home', []);

directHome.controller('DirectHomeCtrl', ['$scope', 'growl', 'SettingsFactory', 'PropertiesFactory', 'CCDADocumentsFactory', 'DirectCertsLinkFactory',
	function($scope, growl, SettingsFactory, PropertiesFactory, CCDADocumentsFactory, DirectCertsLinkFactory) {


		$scope.properties = PropertiesFactory.get(function(data) {

			var sfLink = "https://sourceforge.net/projects/mutransporttesting/files/certificates/";

			SettingsFactory.getSettings(function(result) {

				// angular.forEach(result.data.certificates, function(certificate) {
				//     if (certificate.name === "") {
				//         certificate.name = data.domainName + "/" + data.domainName + ".der";
				//     }
				//     if (!certificate.link) {
				//         certificate.sourceforgeLink = sfLink + certificate.name;
				//     } else {
				//         certificate.sourceforgeLink = certificate.link;
				//     }
				// });

				$scope.certificatesLink = DirectCertsLinkFactory.query(function(resData) {
					angular.forEach(resData, function(cert) {
						if(cert.link === "") {
							$scope.certNotConfigured = true;
						}
					});
				});

				$scope.settings = result.data;
			});

		});

		$scope.copyCcdaEmail = function(ccda, domain) {
			return ccda + "@" + domain;
		};

		$scope.displayGrowl = function(text) {
			growl.success(text);
		};

		$scope.sutRole = "sender";
		$scope.ccdaData = {};

		CCDADocumentsFactory.get(function(data) {
			$scope.ccdaDocuments = data;
			if (data !== null) {
				$scope.sutRole = Object.keys(data)[0];
				$scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
			}
		}, function(error) {
			console.log(error);
		});

		$scope.switchDocType = function(type) {
			$scope.sutRole = type;
			$scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
		};
	}

]);
