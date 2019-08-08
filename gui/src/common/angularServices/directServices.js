/* Services */


var directService = angular.module('ttt.directServices', ['ngResource']);

directService.factory('MessageStatusFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'directMessageStatus/:type', {
            type: '@type'
        }, {});
    }
]);

directService.factory('DirectEmailAddress', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'registration/direct/:direct', {}, {
            remove: {
                method: 'DELETE',
                params: {
                    direct: '@direct'
                }
            }
        });
    }
]);

directService.factory('ContactEmailAddress', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'registration/contact/:direct/:contact', {
            direct: '@direct'
        }, {
            remove: {
                method: 'DELETE',
                params: {
                    direct: '@direct',
                    contact: '@contact'
                }
            }
        });
    }
]);

directService.factory('SendDirect', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'sendDirect', {}, {});
    }
]);

directService.factory('GetMdnForDirectFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'findmdn', {}, {});
    }
]);

directService.factory('MessageValidatorFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'directValidator', {}, {});
    }
]);

directService.factory('ValidationReportFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'validationReport/:messageId', {
            messageId: '@messageId'
        }, {});
    }
]);

directService.factory('ReportMessageContentFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'validationReport/rawContent/:messageId', {
            messageId: '@messageId'
        }, {});
    }
]);

directService.factory('CCDAValidatorFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'ccdaValidator', {}, {});
    }
]);

directService.factory('CCDAReportFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'ccdaReport/:messageId', {
            messageId: '@messageId'
        }, {});
    }
]);

directService.factory('CCDAR2ValidatorFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'ccdar2', {}, {
        });
    }
]);

directService.factory('DCDTValidatorFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'dcdt', {}, {
        });
    }
]);

directService.factory('DirectCertsLinkFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'directcertlinks', {}, {
        });
    }
]);

directService.factory('XDMValidatorFactory', ['$resource', 'RESOURCES',
    function($resource, RESOURCES) {
        return $resource(RESOURCES.USERS_API + 'xdm', {}, {
        });
    }
]);
