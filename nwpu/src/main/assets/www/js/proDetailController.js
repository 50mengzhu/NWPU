app.controller('ProDetailangularCtrl', function($scope,$http,$location) {	
	$scope.url=$location.absUrl();
	$scope.len=$scope.url.length;
	$scope.offset=$scope.url.indexOf('?');
	$scope.info=$scope.url.substr($scope.offset,$scope.len);
	$scope.id=$scope.info.split('=');
	$scope.projectNumOffset=$scope.id[1];
	$scope.can=$scope.id[2];
	$scope.projectNumfen=$scope.projectNumOffset.split('&');
	$scope.projectNum=$scope.projectNumfen[0];
	$scope.mark = $scope.projectNumfen[1];	
	
	//活动详情页面显示签到人数还是报名人数；或者都不显示
	$scope.mark_qian="";
	$scope.mark_bao="";
	$scope.index="";
	$scope.expect="";
	if($scope.mark=='qian'){
		$scope.mark_bao=false;
		$scope.mark_qian=false;//显示签到人数
		$scope.expect=true;
	}else if($scope.mark=='can'){
		$scope.mark_bao=true;
		$scope.mark_qian=true;//显示报名人数
		$scope.expect=true;
	}else if($scope.mark=='index'){
		if($scope.can=='0'){
			$scope.mark_bao=false;
			$scope.mark_qian=true;//两者都不显示
			$scope.expect=true;
		}else if($scope.can=='1'){
			$scope.mark_bao=false;
			$scope.mark_qian=true;//两者都不显示
			$scope.expect=false;
		}
		
	}
	
	$scope.gender="";//讲座嘉宾性别；
	
	$http({
		method:'POST',
		url:baseurl + "select_project_info_bynum",
		data:{project_num:$scope.projectNum},
		headers: {
	    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	 	}
	}).success(function(rep){		
		$scope.ProjectData=rep.data.lecture_info;
		$scope.baogaoren = rep.data.reporter_list;		
		$scope.Huodong="";
	    angular.forEach($scope.ProjectData,function(element, index){
	  	//对时间格式进行调整
			trimtime(element);
		//控制活动还是讲座的详情显示
//		alert(element.education_type)
			if(element.education_type == "1"){
				$scope.Huodong = true;//显示活动项
			}else if(element.education_type == "2"){
				$scope.Huodong = false;
			}
			if(element.ybmrs == null){
				element.ybmrs = 0;
			}
			
			if (element.whether_to_add_subprojects == '2'){
		   		element.IsSub = true;
	  		}else{
		   		element.IsSub = false;
	  		}	
		});
		angular.forEach($scope.baogaoren,function(element, index){
			if(element.gender=="0"){
				$scope.gender="男";
			}else if(element.gender=="1"){
				$scope.gender="女";
			}
		});
				
	}).error(function(rep){		
		BootstrapDialog.show({
        	title: '提示！',
        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
   		});
	})
	
//返回页面
    $scope.backurl = function(list){    	
      	switch($scope.can){
      		case '0'://index
    		{window.location.href="../index.html"};
      		break;
      		case '1'://expect
      		{window.location.href="../expectProject.html"};
      		break;
      		case '2'://签到报名
      		{window.location.href="../html/signinAndup.html"};
      		break;
      		case '3'://已报名-待参加
      		{window.location.href="../html/signupAlready.html"};
      		break;
      		case '4'://已报名-审核未通过
      		{window.location.href="../html/signupNG.html"};
      		break;
      		case '5'://已参加-评价
      		{window.location.href="../html/joinAlready.html"};
      		break;
      		case '6'://报名未参加
	   		{window.location.href="../html/joinNG.html"};			
      		break;     		
      	}
      	
    	
    		
    
}
	
		
})




  	

		
			