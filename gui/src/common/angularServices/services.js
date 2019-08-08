/* Services */


var tttService = angular.module('ttt.services', ['ngResource']);

// Constants for services
tttService.constant('RESOURCES', (function() {
    // Define your variable
    var resource = '';
    // Use the variable in your constants
    return {
        USERS_DOMAIN: resource,
        USERS_API: resource + 'api/'
    };
})());

tttService.service('ApiUrl', ['RESOURCES', function(RESOURCES) {
    return {
        get: function() {
            return RESOURCES.USERS_API;
        }
    };
}]);


/**
 *  ASSETS
 */

tttService.factory('SettingsFactory', function($http) {
    return {
        getSettings: function(callback, error) {
            $http.get('assets/directSettings.json').then(callback, error);
        }
    };
});

tttService.factory('PropFactory', function($http) {
    return {
        get: function(callback, error) {
            $http.get('api/propfile').then(callback, error);
        }
    };
});

tttService.factory('ReleaseNotesFactory', function($http) {
    return {
        get: function(callback, error) {
            $http.get('api/releasenotes').then(callback, error);
        }
    };
});

tttService.factory('SMTPTestCasesDescription', function($http) {
    return {
        getTestCasesDescription: function(callback, error) {
            $http.get('api/smtpcasesjson').then(callback, error);
        }
    };
});

tttService.factory('XDRTestCasesDescription', function($http) {
    return {
        getTestCasesDescription: function(callback, error) {
            $http.get('assets/xdrtestCases.json').then(callback, error);
        }
    };
});

tttService.factory('XDRTestCasesTemplate', function($http) {
    return {
        getTestCasesDescription: function(callback, error) {
            $http.get('assets/xdrtestCases.json').then(callback, error);
        }
    };
});

tttService.factory('ReplaceDomain', function($http) {
	return {
		getReplacedDomain: function(test,ettDomain) {
			var replaceJsonTest = JSON.stringify(test);
			var jsonReplacedObjTest = replaceJsonTest.split("ttpedge.sitenv.org").join(ettDomain.ettEdgeDomain);
			var jsonReplacedObjTest2 = jsonReplacedObjTest.split("ttpds.sitenv.org").join(ettDomain.ettDsDomain);
			var jsonReplacedObjTest3 = jsonReplacedObjTest2.split("ttpds2.sitenv.org").join(ettDomain.ettDs2Domain);
			var jsonReplacedObjTest4 = jsonReplacedObjTest3.split("direct2.sitenv.org").join(ettDomain.ettDirect2Domain);
			var jsonReplacedObjTest5 = jsonReplacedObjTest4.split("dnsops.ttpedge.sitenv.org").join("dnsops."+ettDomain.ettEdgeDomain);
			//console.log(" jsonReplacedObjTest5 ::"+angular.toJson(jsonReplacedObjTest5, true));
			return JSON.parse(jsonReplacedObjTest5);
	}
};
});


tttService.factory('CriteriaDescription', function($http) {
    return {
        getCriteriaOptions: function(callback, error) {
            $http.get('assets/Criteria.json').then(callback, error);
        }
    };
});

tttService.factory('CCDAR21Documents', function($http) {
    return {
        getCcdaDocuments: function(callback, error) {
            $http.get('assets/ccdar2list.json').then(callback, error);
        }
    };
});

tttService.factory('PropertiesFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'properties', {}, {});
    }
]);

tttService.factory('AllLogFilesFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'logview', {}, {});
    }
]);

tttService.factory('LogViewFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'logview/:file', {
            file: '@file'
        }, {});
    }
]);

tttService.factory('LogViewLevelFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'logview/level', {}, {});
    }
]);

/**
 *   LOGIN SERVICES
 */

tttService.factory('CreateUser', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'login/register', {}, {
            createUser: {
                method: 'POST'
            }
        });
    }
]);

tttService.factory('LogInfo', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {

        return $resource(RESOURCES.USERS_API + 'login', {}, {
            'getUsername': {
                method: 'GET',
                isArray: false
            }
        });
    }
]);

tttService.service('Login', ['$http', 'RESOURCES',
    function($http, RESOURCES) {

        this.login = function(credentials) {
            var data = "username=" + credentials.username + "&password=" + credentials.password + "&submit=Login";

            return $http.post(RESOURCES.USERS_DOMAIN + 'login', data, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });
        };
    }
]);

tttService.service('AdminLogin', ['$http', 'RESOURCES',
    function($http, RESOURCES) {

        this.adminlogin = function(credentials) {
            var data = "username=" + credentials.username + "&password=" + credentials.password + "&AdminLogin=Yes&submit=Login";

            return $http.post(RESOURCES.USERS_DOMAIN + 'login', data, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });
        };
    }
]);

tttService.service('Logout', ['$http', 'RESOURCES',
    function($http, RESOURCES) {

        this.logout = function() {
            return $http.get(RESOURCES.USERS_DOMAIN + 'logout');
        };
    }
]);

tttService.service('LoginModal', function ($uibModal, $rootScope) {
  function assignCurrentUser (user) {
    $rootScope.currentUser = user;
    return user;
  }
  return function() {
    var instance = $uibModal.open({
      templateUrl: 'templates/loginModalTemplate.tpl.html',
      controller: 'AdminLoginCtrl',
      controllerAs: 'AdminLoginCtrl'
    });
    return instance.result.then(assignCurrentUser);
  };

});

/**
 *   PASSWORD SERVICES
 */
tttService.factory('ChangePassword', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {

        return $resource(RESOURCES.USERS_API + 'passwordManager/change', {}, {});
    }
]);

tttService.factory('ForgotPassword', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {

        return $resource(RESOURCES.USERS_API + 'passwordManager/forgot', {}, {});
    }
]);


/**
 *   CCDA SERVICES
 */

tttService.factory('CCDADocumentsFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'ccdadocuments', {testCaseType: ''}, {});
    }
]);

/**
 *   CCDA SERVICES XDR
 */

tttService.factory('CCDAXdrDocumentsFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'ccdadocuments', {testCaseType: 'xdr'}, {});
    }
]);

/**
 *  DIRECT RI Cert Upload
 */
tttService.factory('DirectRICertFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'directricert', {}, {});
    }
]);