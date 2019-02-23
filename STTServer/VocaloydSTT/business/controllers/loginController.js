/**
 * Contrôleur de connexion
 * @param {?} $scope - Variable de contexte
 * @param {*} auth - Service authentification
 */
function loginController($scope, auth)
{
    var login = null;
    var password = null;

    $scope.formLogin = function()
    {
        auth.logIn(login, password)
        .then(function()
        {
            $scope.logged = auth.isLoggedIn();
        })
    };

    $scope.logOut = function()
    {
        auth.logOut($scope);
        $scope.logged = auth.isLoggedIn();
    };
};