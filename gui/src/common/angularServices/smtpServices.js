/* Services */


var smtpService = angular.module('ttt.smtpServices', ['ngResource']);

smtpService.factory('SMTPTestCases', ['RESOURCES', 'CancelableResourceFactory',
    function(RESOURCES, CancelableResourceFactory) {
        return CancelableResourceFactory.createResource(RESOURCES.USERS_API + 'smtpTestCases', {}, {
            startTest: {
                method: 'POST',
                isArray: true
            }
        });
    }
]);

smtpService.factory('SMTPProfileFactory', ['RESOURCES', 'CancelableResourceFactory',
    function(RESOURCES, CancelableResourceFactory) {
        return CancelableResourceFactory.createResource(RESOURCES.USERS_API + 'smtpProfile/:profile', {}, {
            removeProf: {
                method: 'DELETE',
                params: {
                    profile: '@profile'
                }
            }
        });
    }
]);

smtpService.factory('SMTPLogFactory', ['RESOURCES', 'CancelableResourceFactory',
    function(RESOURCES, CancelableResourceFactory) {
        return CancelableResourceFactory.createResource(RESOURCES.USERS_API + 'smtpLog/:profile', {
            profile: '@profile'
        }, {});
    }
]);
