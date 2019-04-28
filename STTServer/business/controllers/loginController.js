/**
 * Contrôleur de connexion
 * @param {?} $scope - Variable de contexte
 * @param {*} auth - Service authentification
 */
function loginController($scope, auth)
{
    $scope.login = null;
    $scope.password = null;

    $scope.formLogin = function()
    {
        auth.logIn($scope.login, $scope.password);
    };

    $scope.logOut = function()
    {
        auth.logOut();
    };
};