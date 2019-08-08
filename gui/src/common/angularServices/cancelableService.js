/*jshint es5: true */

/**
 * ResourceFactory creates cancelable resources.
 * Work based on: http://stackoverflow.com/a/25448672/1677187
 * which is based on: https://developer.rackspace.com/blog/cancelling-ajax-requests-in-angularjs-applications/
 */
/* global array */
var cancelable = angular.module('cancelableService', ['ngResource']);

cancelable.factory('CancelableResourceFactory', ['$q', '$resource',
	function($q, $resource) {

		function abortablePromiseWrap(promise, deferred, outstanding) {
			promise.then(function() {
				deferred.resolve.apply(deferred, arguments);
			});

			promise.catch(function() {
				deferred.reject.apply(deferred, arguments);
			});

			/**
			 * Remove from the outstanding array
			 * on abort when deferred is rejected
			 * and/or promise is resolved/rejected.
			 */
			deferred.promise.finally(function() {
				array.remove(outstanding, deferred);
			});
			outstanding.push(deferred);
		}

		function createResource(url, options, actions) {
			var resource;
			var outstanding = [];
			actions = actions || {};

			Object.keys(actions).forEach(function(action) {
				var canceller = $q.defer();
				actions[action].timeout = canceller.promise;
				actions[action].Canceller = canceller;
			});

			resource = $resource(url, options, actions);

			Object.keys(actions).forEach(function(action) {
				var method = resource[action];

				resource[action] = function() {
					var deferred = $q.defer(),
						promise = method.apply(null, arguments).$promise;

					abortablePromiseWrap(promise, deferred, outstanding);

					return {
						$promise: deferred.promise,

						abort: function() {
							deferred.reject('Aborted');
						},
						cancel: function() {
							actions[action].Canceller.resolve('Call cancelled');

							// Recreate canceler so that request can be executed again
							var canceller = $q.defer();
							actions[action].timeout = canceller.promise;
							actions[action].Canceller = canceller;
						}
					};
				};
			});

			/**
			 * Abort all the outstanding requests on
			 * this $resource. Calls promise.reject() on outstanding [].
			 */
			resource.abortAll = function() {
				for (var i = 0; i < outstanding.length; i++) {
					outstanding[i].reject('Aborted all');
				}
				outstanding = [];
			};

			return resource;
		}

		return {
			createResource: function(url, options, actions) {
				return createResource(url, options, actions);
			}
		};
	}
]);
