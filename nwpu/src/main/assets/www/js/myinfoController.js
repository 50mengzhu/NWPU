app.controller('myinfoCtrl', function($scope,$interval,$http,localStorageService){
		
	function getItem(key) {
				   return localStorageService.get(key);
				};				
	$scope.ID = getItem('user_code');	
	$scope.myinfo={
		userName:"-",
		stuID:"-",
		school:"-",
		phone:"编辑并提交最新手机号",
		phoneNum:''
	};
	
  	$http({
		method:'POST',
		url:baseurl + "select_stu_info",
		data:{user_code:$scope.ID},
		headers: {
	    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	  	}
	}).success(function(rep) {
		$scope.stuinfo=rep.data;
		
		
		angular.forEach($scope.stuinfo,function(element, index){
			if(element.XM!=null){
				$scope.myinfo.userName = element.XM;
			}
			if(element.XH!=null){
				$scope.myinfo.stuID = element.XH;
			}
			
			if(element.DWMC!=null){
				$scope.myinfo.school = element.DWMC;
			}
			if(element.LXDH!=null){
				$scope.myinfo.phone = element.LXDH;
			}	
			
			$scope.btnText = "编辑";
			
	  	});  
	  	
	}).error(function(rep){
		BootstrapDialog.show({
        	title: '提示！',
        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
   		});
	})

		
	/*编辑电话号码*/
	$scope.myinfo.phoneNum = null;
	$scope.phonebool=true;
	$scope.editphone = function(list){ 
		if($scope.btnText == "编辑"){
			$scope.phonebool=!$scope.phonebool; 
			$scope.btnText = "提交";
		} else{			
			if($scope.myinfo.phoneNum==null){
				BootstrapDialog.show({
		        	title: '提示！',
		        	message: '手机号不能为空！',            
		   			});
	   			return;
			}
			$http({
				method:'POST',
				url:baseurl + "update_stu_telephone",
				data:
					{user_code:$scope.ID,
					telephone:$scope.myinfo.phoneNum							 
					},
				headers: {
			    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
			  	}
				}).success(function(rep){
					switch(rep.retCode){
						case 0:										
							BootstrapDialog.show({
					        	title: '提示！',
					        	message: '手机号修改成功！',            
					   		});	
					   		$scope.myinfo.phone = $scope.myinfo.phoneNum;
					   		break;
				   		case -2:
					   		BootstrapDialog.show({
					        	title: '提示！',
					        	message: '账号错误！',            
					   		});
					   		break;
				   		case -3:
					   		BootstrapDialog.show({
					        	title: '提示！',
					        	message: '账号格式错误！',            
					   			});
					   		break;
				   		case -4:
					   		BootstrapDialog.show({
					        	title: '提示！',
					        	message: '联系电话只能为数字！',            
					   			});	
					   		break;
				   		case -100:
					   		BootstrapDialog.show({
					        	title: '提示！',
					        	message: '数据库操作异常！',            
					   			});	
					   		break; 						
					}
					
				}).error(function(rep){
					BootstrapDialog.show({
		        	title: '提示！',
		        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
		   			});
				})
				
			$scope.phonebool=!$scope.phonebool; 
			$scope.btnText = "编辑";
			
		}
  	};	//editphonebtn
  	
  	//	返回到index页面
	$scope.returnIndex = function(back){
		window.location.href="../index.html?backurl="+window.location.href;
	}
  	  	
})