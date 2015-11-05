ASMX.Binding.extension('controller', function($scope, controller) {
    return $scope.q('[data-controller="' + controller + '"]');
});
