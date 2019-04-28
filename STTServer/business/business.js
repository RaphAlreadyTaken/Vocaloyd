/******** Application
 *
 ********/
var business = angular.module('business', ["ngRoute"]);
business.controller('loginController', loginController);
business.controller('transcriptController', transcriptController);
business.service('auth', authService);
business.service('transcript', transcriptService);