/* Filters */

var tttFilter = angular.module('ttt.filters', []);

tttFilter.filter('contentTypeFilter', function() {
	return function(contentType) {
		if (contentType !== undefined) {
			if (contentType.indexOf(';') > -1) {
				return contentType.split(';')[0];
			}
			return contentType;
		}
		return "";
	};
});

tttFilter.filter('statusIconFilter', function() {
	return function(status) {
		if (status) {
			return "fa-check";
		} else {
			return "fa-times";
		}
	};
});

tttFilter.filter('statusFilter', function() {
	return function(status) {
		if (status) {
			return "success";
		} else {
			return "error";
		}
	};
});

tttFilter.filter('logStatusFilter', function() {
	return function(status) {
		var logStatus = status.toLowerCase();
		if (logStatus === "success" || logStatus === "mdn_received") {
			return "success";
		} else if (logStatus === "waiting") {
			return "warning";
		} else {
			return "danger";
		}
	};
});

tttFilter.filter('detailStatusFilter', function() {
	return function(status) {
		if (status === "ERROR") {
			return "danger";
		} else {
			return angular.lowercase(status);
		}
	};
});

tttFilter.filter('rfcFilter', function() {
	return function(rfc) {
		if (rfc !== undefined) {
			return rfc.split(';');
		}
		return "";
	};
});

tttFilter.filter('reverseArrayOnly', function() {
	return function(items) {
		if (!angular.isArray(items)) {
			return items;
		}
		return items.slice().reverse();
	};
});

tttFilter.filter('smtpStatusFilter', function() {
	return function(status) {
		if (status) {
			return "success";
		} else {
			return "danger";
		}
	};
});

tttFilter.filter('cutLongWord', function() {
	return function(value, wordwise, max, tail) {
		if (!value) {
			return '';
		}

		max = parseInt(max, 10);
		if (!max) {
			return value;
		}
		if (value.length <= max) {
			return value;
		}

		value = value.substr(0, max);
		if (wordwise) {
			var lastspace = value.lastIndexOf(' ');
			if (lastspace != -1) {
				value = value.substr(0, lastspace);
			}
		}

		return value + (tail || ' …');
	};
});

tttFilter.filter('inputFilter', function() {
	return function(datatype) {
		if (datatype.toLowerCase().indexOf('boolean') >= 0) {
			return "checkbox";
		} else {
			return "text";
		}
	};
});

tttFilter.filter('ccdaClassFilter', function() {
	return function(datatype) {
		var type = datatype.toLowerCase();
		if (type.indexOf('error') >= 0) {
			return "btn-danger";
		} else if (type.indexOf('war') >= 0) {
			return "btn-warning";
		} else if (type.indexOf('info') >= 0) {
			return "btn-info";
		} else {
			return "btn-primary";
		}
	};
});

tttFilter.filter('isEmpty', [function() {
	return function(object) {
		return angular.equals({}, object);
	};
}]);
