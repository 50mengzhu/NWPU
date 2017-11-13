app.controller('angularCtrl', function($scope,$interval,$http,localStorageService) {
	
	function getItem(key) {
				   return localStorageService.get(key);
				};				
	$scope.ID = getItem('user_code');
	$interval(function () {
			$scope.thetime = new Date();
	}, 1000);
	
	$http({
		method:'POST',
		url:baseurl + "stu_sign_up_project",
		data:{user_code:$scope.ID},
		headers: {
	    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	 	}
	}).success(function(rep) {
		$scope.projectdatasignUp=rep.data;
		
		$scope.todayRGFlag = false;
		if (rep.data.length == 0){
		   	$scope.todayRGFlag = true;
	  	}
		
	  	angular.forEach($scope.projectdatasignUp,function(element, index){
	      	//对时间格式进行调整
			trimtime(element);
			//获得项目的类型，活动还是讲座
			getProjectType(element);
			
			if(element.yqdrs == null){
				element.yqdrs = 0;
			}			
		});  
	}).error(function(rep){
		BootstrapDialog.show({
        	title: '提示！',
        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
   		});
	})
			
	$http({
		method:'POST',
		url:baseurl + "go_to_the_registration",
		data:{user_code:$scope.ID},
		headers: {
	    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	  }
	}).success(function(rep) {		
		$scope.signUpQuickly=rep.data;
		
		$scope.noData = false;
		if (rep.data.length == 0){
		   	$scope.noData = true;
	  	}
		
		angular.forEach($scope.signUpQuickly,function(element, index){
			//每个报名列表元素的按钮文本
			Dealbutton(element);
						
			//对时间格式进行调整
			trimtime(element);
			//获得项目的类型，活动还是讲座
			getProjectType(element);			
			
			if(element.ybmrs == null){
				element.ybmrs = 0;
			} 
			
			element.signlist = true;
			if(element.ybmrs == 0){
				element.signlist = false;
			}
    });
    			
	}).error(function(rep){
		BootstrapDialog.show({
        	title: '提示！',
        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
   		});
	})
	
//	查询个人信息的接口
	$scope.myinfo={
		userName:"-",
		stuID:"-",
		school:"-",
		phone:"-",//手机号初始值定为"-"，若库里的个人信息手机号为空，则没有进行赋值；手机号采用初始值"-"
		
	};	
	/*报名或取消报名的方法
    @param list 当前所点击的list
//*/
//立即报名

//取消报名
function buttonUnsign(list,$http)
{
	$http({
		method:'POST',
		url:baseurl + "del_sign_up_bystu",
		data:{user_code:$scope.ID,
		project_num:list.project_num,
		},
		headers: {
	    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	  	}
	}).success(function(rep) {
		switch (rep.retCode)
		{	
			case 0:
		    case 2:
			    BootstrapDialog.show({
			        title: '提示！',
			        message: '取消报名成功！', 
				});
				list.buttonText = '立即报名';
			  	list.buttoncomment = "";
			  	list.status = 'btnOk';
		    break;
		    case 1:
			    BootstrapDialog.show({
	    			title: '警告！',
	    			message: '已被进行审核操作，不可取消报名。',
				});
		    break;		    	    
		    default:
		    	BootstrapDialog.show({
			        title: '警告！',
			        message: '取消报名失败。', 					
				});
		}	
					
   	}).error(function(rep){
		BootstrapDialog.show({
        	title: '提示！',
        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
   		});
	})
}
	$scope.toJoinOrQuit = function(list){ 
		if(list.status=='btnOk'){  //立即报名; 抓紧报名
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
				
		 });
		 if($scope.myinfo.phone=="-"){
		 	BootstrapDialog.show({
	        	title: '提示！',
	        	message: '报名需要您的手机号！\n请到“我的”信息里编辑并提交您的联系方式，谢谢！',            
	   		});
		 }
		 if($scope.myinfo.phone!='-'){
		 BootstrapDialog.show({
				    title: '报名信息确认！',
				    message: '姓名：'+$scope.myinfo.userName+'\n手机：'+$scope.myinfo.phone+'\n'+list.projectname+'\n提示：若当前手机号有误，可到“我的”信息里编辑并提交最新联系方式。然后返回该页面重新报名即可!\n点击“确认”提交报名，否则点击“取消”',            
				    buttons: [{
			        label: '确定',
			        action: function(dialogItself) {
//			            dialogItself.close();
					$http({
							method:'POST',
							url:baseurl + "save_sign_up_project",
							data:{user_code:$scope.ID,
								  project_num:list.project_num,
								  telephone:$scope.myinfo.phone
								  },
							headers: {
						    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
						  	}
						}).success(function(rep) {
							dialogItself.close();
							switch (rep.retCode)
							{	
					
							    case 2:
								    BootstrapDialog.show({
								        title: '警告！',
								        message: '当前账户错误。', 
									});
							    break;
							    case 5:
								    BootstrapDialog.show({
								        title: '提示！',
							        	message: '该项活动已经报过名了，无需再次报名。', 
									});
							    break;
							    case 0:
							    case 6:
								    BootstrapDialog.show({
								        title: '提示！',
								        message: '报名成功，等待活动举办方审查！\n\n注意：审查通过后就不能取消了！',					
									});	
									list.buttonText = '取消报名';
						  			list.buttoncomment = "报名待审查";
									list.status = 'btnCheck';
							    break;		    
							    default:
							    	BootstrapDialog.show({
								        title: '警告！',
								        message: '报名未能成功。', 					
									});
							}	
						}).error(function(rep){
							BootstrapDialog.show({
					        	title: '提示！',
					        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
					   		});
						})
			        }
				    }, {
			        label: '取消',                
			        action: function(dialogItself) {                	
			        	dialogItself.close();                    
			        }
				    }]
			    });

		 }	
		})	
	
		.error(function(rep){
			BootstrapDialog.show({
	        	title: '提示！',
	        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
	   		});
		})
   	}else if(list.status=='btnCheck'){ //取消报名：审核中
   		buttonUnsign(list,$http);
   	}
  	
    if (list.status == 'btnJudgeNG'){
    	//需要给出评价界面
    	window.location.href="project_evaluate.html?list.num="+list.project_num+"&"+"eva="+0;	
    }
    if (list.status == 'btnJudgeOk'){
    	//显示评价信息    		
		window.location.href="project_evaluate_already.html?list.num="+list.project_num+"&"+"eva="+0; 
    }
    	
   	if (list.status == 'btnPass'){    		    		
    	BootstrapDialog.show({
        title: '提示！',
        message: '报名审查已通过，不能取消了！\n请注意按时参加活动！',            
      });
    }
   	if(list.status == 'btnNG'){    		    		
    	BootstrapDialog.show({
        title: '提示！',
        message: '报名时间未到或已结束！\n请注意查看报名时间！',            
      });
    }
    
  };	//toJoinOrQuit	
		
  //签到扫码
 	$scope.CheckIn = function(list){    	
		window.plugins.barcodeScanner.scan( 
	                    BarcodeScanner.Type.QR_CODE, 
	                    function(result) {
	                        //$("#barcodediv").html(""+result);
							$scope.result2=result;
							var reg = /\:+/;
							var reg1 = /\s+/;
														
							var result1=$scope.result2.split(reg);
							$scope.erweima=result1[1];
							$scope.jingdu=result1[4];
							$scope.weidudu=result1[5];
							$scope.loca=result1[6];//位置信息
							var erweima=$scope.erweima.split(reg1);
							var jingdu=$scope.jingdu.split('l');
							var weidu=$scope.weidudu.split('A');							
							$scope.twobarCode = erweima[0];
							$scope.JingDu = jingdu[0];
							$scope.WeiDu = weidu[0];//清无用文字符
							var weidu = $scope.JingDu.split('\n');
							var jingdu = $scope.WeiDu.split('\n');
							$scope.JingDu = jingdu[0];//
							$scope.WeiDu = weidu[0];//清换行符
							var jingdud = $scope.JingDu.split(' ');
							var weidud = $scope.WeiDu.split(' ');
							$scope.JingDu = jingdud[1];//
							$scope.WeiDu = weidud[1];//清空格符
							BootstrapDialog.show({
							    title: '提示！',
							    message: list.projectname+'\n经度：'+$scope.JingDu+'\n纬度：'+$scope.WeiDu+'\n位置：'+$scope.loca+'\n点击“确定”进行签到！\n 若当前不想签到，点击“取消”退出扫码过程。',            
							    buttons: [{
						        label: '确定',
						        action: function(dialogItself) {
//						            dialogItself.close();
									$http({
										method:'POST',
										url:baseurl + "analysis_qr",
										data:{user_code:$scope.ID,
											  project_num:list.project_num,
											  qr_info:$scope.twobarCode,
											  longitude:$scope.WeiDu,
											  latitude:$scope.JingDu},
										headers: {
									    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
									  }
									}).success(function(rep){
										dialogItself.close();										
										switch(rep.retCode){
											case 0:
												BootstrapDialog.show({
										        title: '提示！',
										        message: '签到成功！', 
													});
											    break;
											case 2:
												BootstrapDialog.show({
										        title: '提示！',
										        message: '报名审核未通过，不可签到！', 
													});
											    break;
											case 3:
												BootstrapDialog.show({
										        title: '提示！',
										        message: '已经签到，无需重复签到！', 
													});
											    break;
											case 4:
												BootstrapDialog.show({
										        title: '提示！',
										        message: '你没有报名该项目，不可签到！', 
													});
											    break;
											case 5:
												BootstrapDialog.show({
										        title: '提示！',
										        message: '当前二维码已失效，请获取最新的签到二维码！', 
													});
											    break;
											case -1:
												BootstrapDialog.show({
										        title: '提示！',
										        message: '参数传递错误！', 
													});
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
											case -100:
												BootstrapDialog.show({
										        title: '提示！',
										        message: '数据库错误！', 
													});
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
						        },{
						        label:'取消',
						        action: function(dialogItself) {
						            dialogItself.close();}
							    }]
						    }); 
	                    }, 
	                    function(error) {
	                       // $("#barcodediv").html("扫描失败："+result);
						   $scope.result2=result;
	            		}, 
	            		{
	            		    installTitle: "安装提示",
	            		    installMessage:"是否安装开源免费的ZXing二维码扫描器",
	            		    yesString:"确定",
	            		    noString:"取消"
	            		}
	        );    	    
  };
  //	返回到index页面
	$scope.returnIndex = function(back){
		window.location.href="../index.html?backurl="+window.location.href;
	 }
});
		



