var directRegister = angular.module('ttt.direct.register', []);

directRegister.controller('RegisterCtrl', ['$scope', 'DirectEmailAddress', 'ContactEmailAddress', 'growl', 'LogInfo',
	function($scope, DirectEmailAddress, ContactEmailAddress, growl, LogInfo) {

		$scope.directList = [];
		$scope.contactList = [];

		// Get username
		$scope.userInfo = LogInfo.getUsername();
		// Asynchronous call so do that when the back end answered with $promise
		$scope.userInfo.$promise.then(function(response) {
			// Get list of direct emails
			if ($scope.userInfo.logged) {
				DirectEmailAddress.query(function(data) {
					$scope.directList = data;
				});
			}
		});



		// Create Direct address and bind it to logged user
		$scope.createDirect = function(newDirect) {
			if (newDirect) {
				DirectEmailAddress.save(newDirect, function(data) {
					growl.success("Direct Email successfully created!");
					$scope.directList.push(newDirect);
					$scope.currentDirect = newDirect;
					$scope.getContactForDirect(newDirect);
				}, function(data) {
					throw {
						code: data.data.code,
						url: data.data.url,
						message: data.data.message
					};
				});
			}
		};

		// Delete the direct address
		$scope.deleteDirect = function(direct) {
			DirectEmailAddress.remove({
				'direct': direct
			}, function(data) {
				growl.success("Direct address successfully deleted!");
				$scope.directList = $scope.directList.filter(function(item) {
					return item !== direct;
				});
				$scope.currentDirect = undefined;
			}, function(data) {
				throw {
					code: data.data.code,
					url: data.data.url,
					message: data.data.message
				};
			});
		};

		// Get the Direct addresses for th clicked contact
		$scope.getContactForDirect = function(direct) {
			$scope.currentDirect = direct;
			$scope.contactList = ContactEmailAddress.query({
				'direct': direct
			});
		};

		// Add Contact for Direct address
		$scope.addContactToDirect = function(newContact) {
			ContactEmailAddress.save({
				'direct': $scope.currentDirect
			}, newContact, function(data) {
				growl.success("Contact Email successfully added!");
				$scope.contactList = ContactEmailAddress.query({
					'direct': $scope.currentDirect
				});
			}, function(data) {
				throw {
					code: data.data.code,
					url: data.data.url,
					message: data.data.message
				};
			});
		};

		$scope.deleteContactForDirect = function(contactToDelete) {
			ContactEmailAddress.remove({
				'direct': $scope.currentDirect,
				'contact': contactToDelete
			}, function(data) {
				growl.success("Contact successfully deleted!");
				$scope.contactList = ContactEmailAddress.query({
					'direct': $scope.currentDirect
				});
			}, function(data) {
				throw {
					code: data.data.code,
					url: data.data.url,
					message: data.data.message
				};
			});
		};

	}
]);
