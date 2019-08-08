/* Services */


var xdrService = angular.module('ttt.xdrServices', ['ngResource']);

xdrService.factory('XDRTestCases', ['RESOURCES', 'CancelableResourceFactory',
    function(RESOURCES, CancelableResourceFactory) {
        return CancelableResourceFactory.createResource(RESOURCES.USERS_API + 'xdr/tc/:id/configure', {
            id: '@id'
        }, {
            configure: {
                method: 'GET'
            }
        });
    }
]);

xdrService.factory('XDRRunTestCases', ['RESOURCES', 'CancelableResourceFactory',
    function(RESOURCES, CancelableResourceFactory) {
        return CancelableResourceFactory.createResource(RESOURCES.USERS_API + 'xdr/tc/:id/run', {
            id: '@id'
        }, {
            run: {
                method: 'POST'
            }
        });
    }
]);

xdrService.factory('XDRCheckStatus', ['RESOURCES', 'CancelableResourceFactory',
    function(RESOURCES, CancelableResourceFactory) {
        return CancelableResourceFactory.createResource(RESOURCES.USERS_API + 'xdr/tc/:id/status', {
            id: '@id'
        }, {
            checkStatus: {
                method: 'GET'
            }
        });
    }
]);


xdrService.factory('XDREndpoints', ['RESOURCES', 'CancelableResourceFactory',
    function(RESOURCES, CancelableResourceFactory) {
        return CancelableResourceFactory.createResource(RESOURCES.USERS_API + 'xdr/tc/:id/endpoint', {
            id: '@id'
        }, {});
    }
]);

xdrService.factory('XDRValidatorEndpoints', ['RESOURCES', 'CancelableResourceFactory',
    function(RESOURCES, CancelableResourceFactory) {
        return CancelableResourceFactory.createResource(RESOURCES.USERS_API + 'xdrvalidator/endpoints', {}, {});
    }
]);

xdrService.factory('XDRValidatorRun', ['RESOURCES', 'CancelableResourceFactory',
    function(RESOURCES, CancelableResourceFactory) {
        return CancelableResourceFactory.createResource(RESOURCES.USERS_API + 'xdrvalidator/run', {}, {});
    }
]);

xdrService.factory('XDRValidatorStatus', ['RESOURCES', 'CancelableResourceFactory',
    function(RESOURCES, CancelableResourceFactory) {
        return CancelableResourceFactory.createResource(RESOURCES.USERS_API + 'xdrvalidator/status', {}, {});
    }
]);