/**
 * Service d'authentification
 * @param {?} $http - requête http
 * @param {?} $window - navigateur
 * @param {Object} socket - service socket
 */
function authService($http, $window)
{
	/**
	 * Connecte un utilisateur
	 * @param {String} login - Identifiant
	 * @param {String} password - Mot de passe
	 * @returns {Promise} Réponse serveur
	 */
	this.logIn = function(login, password)
	{
		return $http
		.post('http://192.168.43.15:3131/log/login', {'login': login, 'password': password})
		.then(function(response)
		{
			$window.location.reload();
			return response;
		});
	};

	/**
	 * Déconnecte un utilisateur
	 * @returns {Promise} Réponse serveur
	 */
	this.logOut = function()
	{
		return $http
		.post('http://192.168.43.15:3131/log/logout')
		.then(function()
		{
			$window.location.reload();
			return response;
		});
	};
};