app.controller('joinCtrl', function($scope,$interval,$http,localStorageService){ 
	function getItem(key) {
				   return localStorageService.get(key);
				};				
	$scope.ID = getItem('user_code');
//	alert($scope.ID);
  	$http({
		method:'POST',
		url:baseurl + "already_join_projectlist",
		data:{user_code:$scope.ID},
		headers: {
	    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	  	}
	}).success(function(rep) {
		$scope.joinProjectlist=rep.data;
		
		$scope.noData = false;
		if (rep.data.length == 0){
		   	$scope.noData = true;
	  	}

		angular.forEach($scope.joinProjectlist,function(element, index){
			//对时间格式进行调整
			trimtime(element);
			//获得项目的类型，活动还是讲座
			getProjectType(element);
			
			if(element.yqdrs == null){
				element.yqdrs = 0;
			}
			
			if(element.evaluate_project_num == null){
				element.buttonText = '待评价'; 
	  			element.buttoncomment = "";
	  			element.status = 'btnJudgeNG';
	  		}else{
	  			element.buttonText = '已评价'; 
	  			element.buttoncomment = "";
	  			element.status = 'btnJudgeOk';
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
	
	/*进行评价
	  @param list 当前所点击的list
	*/
	$scope.toJudge = function(list){ 		
	    if (list.status == 'btnJudgeNG'){
	    	//跳转到评价界面
			window.location.href="project_evaluate.html?list.num="+list.project_num+"&"+"eva="+1;
	    }
	    if (list.status == 'btnJudgeOk'){	    	
	    	//显示评价信息    
			window.location.href="project_evaluate_already.html?list.num="+list.project_num+"&"+"eva="+1; 
	    }   	
	   	
	    
	};//toJudge
	
    //返回到index页面
	$scope.returnIndex = function(back){
		window.location.href="../index.html"
	}
})