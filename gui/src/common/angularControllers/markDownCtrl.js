var markDownCtrl = angular.module('ttt.markdown', []);

markDownCtrl.controller('MarkDownCtrl', ['$scope','$http', '$sce','$state',
    function($scope,$http, $sce,$state) {
             $scope.accouncments = "";
             $scope.moduleinfo  = $state.current.data.moduleInfo;
            $http({
                    method: 'GET',
                    url: 'api/markdown?moduleInfo=' + $scope.moduleinfo,
                    data: {},
                    transformResponse: function (data, headersGetter, status) {
                                       $scope.accouncments = data;
                                       return {data: data};
                     }
                     }).success(function () {
                             //Some success function
                     }).error(function () {
                             //Some error function
            });
         $scope.renderHtml = function (htmlCode) {
              return $sce.trustAsHtml(markdown.toHTML(htmlCode));
         };
    }
]);
