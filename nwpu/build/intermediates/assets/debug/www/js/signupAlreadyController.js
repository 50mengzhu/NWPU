app.controller('signupCtrl', function($scope,$interval,$http,localStorageService){ 
	
	function getItem(key) {
				   return localStorageService.get(key);
				};				
	$scope.ID = getItem('user_code');
  	$http({
		method:'POST',
		url:baseurl + "have_to_sign_up_to_attend",
		data:{user_code:$scope.ID},
		headers: {
	    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	  	}
	}).success(function(rep) {
		$scope.signupProjectlist=rep.data;
		
		$scope.noData = false;
		if (rep.data.length == 0){
		   	$scope.noData = true;
	  	}

		angular.forEach($scope.signupProjectlist,function(element, index){
			//对时间格式进行调整
			trimtime(element);
			//获得项目的类型，活动还是讲座
			getProjectType(element);
			
			if(element.ybmrs == null){
				element.ybmrs = 0;
			}
	  	});    			
	}).error(function(rep){
		BootstrapDialog.show({
        	title: '提示！',
        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
   		});
	})
	//	返回到index页面
	$scope.returnIndex = function(back){
		window.location.href="../index.html?backurl="+window.location.href;
	}
})