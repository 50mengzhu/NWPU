app.controller('LoginCtrl',function($http,$scope,localStorageService){
	function submit(key, val) {
				   return localStorageService.set(key, val);
				 };
	
	$scope.login = function(userInfo){	
		$http({
			method:'POST',
			url:baseurl + "app_login",
			data:{user_code:userInfo.userName,
				  pass_word:userInfo.passWord
			},
			headers: {
		    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
		 	}
		}).success(function(rep) {						
			switch(rep.retCode){
				case -4 :
					BootstrapDialog.show({
					        title: '提示！',
				        	message: '用户密码错误。', 
						});
				break;
				case -2 :
					BootstrapDialog.show({
					        title: '提示！',
				        	message: '账号错误。', 
						});
				break;
				case -3 :
					BootstrapDialog.show({
					        title: '提示！',
				        	message: '账号格式错误。', 
						});
				break;
				case -1 :
					BootstrapDialog.show({
					        title: '提示！',
				        	message: '参数传递错误。', 
						});
				break;
				case -100 :
					BootstrapDialog.show({
					        title: '提示！',
				        	message: '数据库操作异常。', 
						});
				break;
				case 0 :				
					window.location.href="signinAndup.html?backurl="+window.location.href;
					submit('user_code',$scope.userInfo.userName);					
				break;	
			}
		})
		.error(function(rep){
			BootstrapDialog.show({
	        	title: '提示！',
	        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
	   		});
		})
	
	}
//	$scope.returnbackIndex = function(back){
//		window.location.href="../index.html?backurl="+window.location.href;
//	 }
	$scope.returnbackIndex2 = function(back){
		window.location.href="../index.html?backurl="+window.location.href;
	 }
	//传递全局用户名变量
	
});

		







