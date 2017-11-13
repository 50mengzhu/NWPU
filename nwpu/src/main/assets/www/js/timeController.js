angular.module('myApp',[]).controller('timeCtrl',function($scope,$timeout){
	
	$scope.cutdown = 8;	
	$scope.imgName=[];
	$scope.imgName[0] = 'A.jpg';
	$scope.imgName[1] = 'B.jpg';
	$scope.imgName[2] = 'C.jpg';
	$scope.imgName[3] = 'D.jpg';
	$scope.imgName[4] = 'E.jpg';	
	$scope.Imgindex = Math.round(Math.random()*4);
	$scope.Image = $scope.imgName[$scope.Imgindex];
	
	var updateClock = function(){
	$timeout(function(){
			$scope.cutdown = $scope.cutdown-1;			
			updateClock();			
		},1000);
		if($scope.cutdown<=1){
			window.location.href="../index.html"
		}	
	}	
	updateClock();
	
	$scope.jumpTo=function(url){
		window.location.href="../index.html";
	}
})