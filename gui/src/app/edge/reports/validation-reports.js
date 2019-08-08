var edgeReports = angular.module('ttt.edge.reports', []);

edgeReports.controller('EdgeReportsCtrl', ['$scope', 'SMTPProfileFactory', 'SMTPLogFactory', 
    function($scope, SMTPProfileFactory, SMTPLogFactory) {
        $scope.profileList = SMTPProfileFactory.query();

        $scope.selectProfile = function(profile) {
            $scope.selectedProfile = profile;
            $scope.validation_report = SMTPLogFactory.query({
                'profile': profile.profileName
            });
        };

        $scope.resetProfile = function() {
            $scope.selectedProfile = undefined;
        };
    }

]);
