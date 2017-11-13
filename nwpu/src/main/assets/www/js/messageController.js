app.controller('messageCtrl', function($scope,$interval,$http,localStorageService){
	function getItem(key) {
				   return localStorageService.get(key);
				};				
	$scope.ID = getItem('user_code');
		
  	/*$http({
		method:'POST',
		url:baseurl + "get_message",
		data:{user_code:$scope.ID},
		headers: {
	    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	  	}
	}).success(function(rep) {
		$scope.messagelist=rep.data;
		
		$scope.noData = false;
		if (rep.data.length == 0){
		   	$scope.noData = true;
	  	}

		angular.forEach($scope.messagelist,function(element, index){
			
	  	});    			
	}).error(function(rep){
		BootstrapDialog.show({
        	title: '提示！',
        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
   		});
	})*/
	//	返回到index页面
	$scope.returnIndex = function(back){
		window.location.href="../index.html?backurl="+window.location.href;
	}
})