var directStatus = angular.module('ttt.direct.status', []);

directStatus.controller('DirectStatusCtrl', ['$scope', '$state', 'MessageStatusFactory', 'LogInfo', 'GetMdnForDirectFactory',
    function($scope, $state, MessageStatusFactory, LogInfo, GetMdnForDirectFactory) {

        // Outgoing/incoming variable
        $scope.messageTypes = "outgoing";

        // Get user credentials
        $scope.userInfo = LogInfo.getUsername();
        // Asynchronous call so do that when the back end answered with $promise
        $scope.userInfo.$promise.then(function(response) {
            // Get list of direct emails
            if (!$scope.userInfo.logged) {
                throw {
                    code: "0x0020",
                    url: "Message Status",
                    message: "You must be logged to access this feature"
                };
            } else {
                // Get the log list
                $scope.getLogs();
            }
        });

        // data
        $scope.head = {
            a_from: "From",
            b_messageID: "Message ID",
            c_time: "Time",
            d_status: "Status"
        };

        $scope.sort = {
            column: 'c_time',
            descending: true
        };

        $scope.$watch('from', function() {
            if ($scope.from) {
                $scope.head.a_from = "To";
                $scope.messageTypes = "incoming";
            } else {
                $scope.head.a_from = "From";
                $scope.messageTypes = "outgoing";
            }
            $scope.getLogs();
        });

        $scope.getLogs = function() {
            MessageStatusFactory.query({
                type: $scope.messageTypes
            }, function(result) {
                $scope.logList = result;

                // Convert to date so it can be sorted properly
                angular.forEach($scope.logList, function(directStep) {
                    angular.forEach(directStep.logList, function(logLine) {
                        logLine.c_time = Date.parse(logLine.c_time);
                    });
                });
            });
        };


        $scope.selectedCls = function(column) {
            return column == $scope.sort.column && 'sort-' + $scope.sort.descending;
        };

        $scope.changeSorting = function(column) {
            var sort = $scope.sort;
            if (sort.column == column) {
                sort.descending = !sort.descending;
            } else {
                sort.column = column;
                sort.descending = false;
            }
        };

        $scope.goToMDN = function(message) {
            var mdnLog = GetMdnForDirectFactory.save({}, message.b_messageID);
            mdnLog.$promise.then(function(data) {
                var mdnMessageId = data.messageId;
                $state.go('direct.report', {message_id: mdnMessageId});
            }, function(error) {
                throw {
                    code: error.data.code,
                    url: error.data.url,
                    message: error.data.message
                };
            });
        };

    }
]);
