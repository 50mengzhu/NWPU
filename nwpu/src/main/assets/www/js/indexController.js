angular.module('main',[]).controller('beforeLoginCtrl', function($scope,$interval,$http){ 
	
  	$http({
		method:'POST',
		url:baseurl + "notlogin_projectlist",
		data:{},
		headers: {
	    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	  	}
	}).success(function(rep) {
		$scope.indexprojectlist=rep.data;
		
		$scope.noData = false;
		$scope.HaveData = false;
		
		if (rep.data.length == 0){
		   	$scope.noData = true;
		} else {
		  	$scope.HaveData = true;
		}

		angular.forEach($scope.indexprojectlist,function(element, index){
			//对时间格式进行调整
			trimtime(element);
			//获得项目的类型，活动还是讲座
			getProjectType(element);
			
			if(element.ybmrs == null){
				element.ybmrs = 0;
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
	$scope.closeEve = function(){
		alert("关闭窗口")

	}
})
		



