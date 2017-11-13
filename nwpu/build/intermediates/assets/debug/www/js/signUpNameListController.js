angular.module('main',[]).controller('NameListCtrl', function($scope,$http,$location) {	
	$scope.url=$location.absUrl();
	$scope.len=$scope.url.length;
	$scope.offset=$scope.url.indexOf('?');
	$scope.info=$scope.url.substr($scope.offset,$scope.len);
	$scope.id=$scope.info.split('=');
	$scope.projectNum=$scope.id[1];
	
	$scope.zero;//zero为真，表示当前名单没有信息；
	$http({
		method:'POST',
		url:baseurl + "browse_alreadyjoin_info",
		data:{project_num:$scope.projectNum},
		headers: {
	    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	 	}
	}).success(function(rep){		
		$scope.projectName = rep.data.project_name;
		
		$scope.Namelist=rep.data.stu_list;
		$scope.backdata=rep.retCode;
		if($scope.backdata==0){
			$scope.zero = false;
		}
		else if($scope.backdata==1){
			$scope.zero = true;
		}
		
				
	}).error(function(rep){		
		BootstrapDialog.show({
        	title: '提示！',
        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
   		});
	})
	$scope.denglu = function(list){
		window.location.href="../html/signinAndup.html?backurl="+window.location.href;
	}
	
		
})




  	

		
			