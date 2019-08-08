var releaseNotes = angular.module('ttt.releaseNotes', []);

releaseNotes.controller('ReleaseNotesCtrl', ['$scope', 'ReleaseNotesFactory', '$location', '$anchorScroll',
    function($scope, ReleaseNotesFactory, $location, $anchorScroll) {
        ReleaseNotesFactory.get(function(response) {

            var data = response.data;

            var VERSION_DELIMITER = '==';
            var DATE_DELIMITER = '#';
            var TITLE_DELIMITER = '*';
            var LINE_DELIMITER = '-';

            $scope.versions = [];
            var versions = data.split(VERSION_DELIMITER);
            for (i = 1; i < versions.length; i++) {
                var lines = versions[i].split('\r\n');
                var splitVersion = lines[0].split(DATE_DELIMITER);
                var version = splitVersion[0];
                var date = '';
                if (splitVersion.length > 1) {
                    date = splitVersion[1];
                }
                lines.splice(0, 1);
                var versionForScope = {};
                versionForScope[version] = {
                    'date': Date.parse(date),
                    'notes': []
                };
                for (k = 0; k < lines.length; k++) {
                    var current = lines[k].replace(/^\s+|\s+$/g, '');
                    if (current.indexOf(TITLE_DELIMITER) === 0) {
                        var title = current.slice(1, current.length);
                        var linesFor = [];
                        k++;
                        var e = k;
                        while (e < lines.length && lines[e].replace(/^\s+|\s+$/g, '').indexOf(LINE_DELIMITER) === 0) {
                            var ecurrent = lines[e].replace(/^\s+|\s+$/g, '');
                            linesFor.push(ecurrent.slice(1, ecurrent.length));
                            e++;
                        }
                        versionForScope[version].notes.push({
                            'title': title,
                            'lines': linesFor
                        });
                    }
                }
                $scope.versions.push(versionForScope);
            }
        });

        $scope.goToVersion = function(anchor) {
            $location.hash(anchor);

            // call $anchorScroll()
            $anchorScroll();
        };
    }
]);
