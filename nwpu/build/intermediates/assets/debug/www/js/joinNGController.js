app.controller('nojoinCtrl', function($scope,$interval,$http,localStorageService){
	
	function getItem(key) {
				   return localStorageService.get(key);
				};				
	$scope.ID = getItem('user_code');
  	$http({
		method:'POST',
		url:baseurl + "absence_join_projectlist",
		data:{user_code:$scope.ID},
		headers: {
	    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	  	}
	}).success(function(rep) {
		$scope.nojoinProjectlist=rep.data;
		$scope.NoJoinFlag = false;
		
		$scope.noData = false;
		if (rep.data.length == 0){
		   	$scope.noData = true;
	  	}

		angular.forEach($scope.nojoinProjectlist,function(element, index){
			var start_time = element.start_time;			
			start_time = new Date(start_time);			
			
			if((start_time.valueOf()+30*24*60*60*1000) > new Date().valueOf()){
				$scope.NoJoinFlag = true;
			}
			  	
			//对时间格式进行调整
			trimtime(element);
			//获得项目的类型，活动还是讲座
			getProjectType(element);
			
			if(element.yqdrs == null){
				element.yqdrs = 0;
			}
			
			if(element.detail.length>40){
				element.detail = element.detail.substr(0,40)+"...";				
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