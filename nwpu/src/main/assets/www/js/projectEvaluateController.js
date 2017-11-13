app.controller('ProjectevaluateCtrl',function($scope,$http,$location,localStorageService){
	
	function getItem(key) {
				   return localStorageService.get(key);
				};				
	$scope.ID = getItem('user_code');
	$scope.url=$location.absUrl();
	$scope.len=$scope.url.length;
	$scope.offset=$scope.url.indexOf('?');
	$scope.info=$scope.url.substr($scope.offset,$scope.len);
	$scope.id=$scope.info.split('=');
	$scope.projectNumOffset=$scope.id[1];
	$scope.eva=$scope.id[2];
	$scope.projectNumfen=$scope.projectNumOffset.split('&');
	$scope.projectNum=$scope.projectNumfen[0];
	
	$http({
		method:'POST',
		url:baseurl + "select_project_info_bynum",
		data:{project_num:$scope.projectNum},
		headers: {
	    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	 	}
	}).success(function(rep){				
		$scope.ProjectData=rep.data.lecture_info;
		
		angular.forEach($scope.ProjectData,function(element, index){
	  	//对时间格式进行调整
			trimtime(element);
			
//			if (element.whether_to_add_subprojects == '2'){
//	   		element.IsSub = true;
//		}else{
//	   		element.IsSub = false;
//		}	
		});
				
	}).error(function(rep){
		BootstrapDialog.show({
        	title: '提示！',
        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
   		});
   
	});
		
	$scope.max = 5;
	$scope.maxContent = 5;
	$scope.maxGot = 5;
	$scope.maxForm = 5;
	
	$scope.EvaInfo={
		user_code:$scope.ID,
		project_num:$scope.projectNum,		
		ratingVal : 4,
		ratingValContent : 4,
		ratingValGot : 4,
		ratingValForm : 4,
		Evaluate:""
	};	

	
	$scope.onHover = function(val){
		$scope.hoverVal = val;
	  };
	$scope.onLeave = function(){
	    $scope.hoverVal = null;
	  }
	$scope.onChange = function(val){
	    $scope.EvaInfo.ratingVal = val;
	  }
	
	//content
	$scope.onHoverContent = function(val){
		$scope.hoverValContent = val;
	  };
	$scope.onLeaveContent = function(){
	    $scope.hoverValContent = null;
	  }
	$scope.onChangeContent = function(val){
	    $scope.EvaInfo.ratingValContent = val;
	  }
	//Got
	$scope.onHoverGot = function(val){
		$scope.hoverValGot = val;
	  };
	$scope.onLeaveGot = function(){
	    $scope.hoverValGot = null;
	  }
	$scope.onChangeGot = function(val){
	    $scope.EvaInfo.ratingValGot = val;
	  }
	//Form
	$scope.onHoverForm = function(val){
		$scope.hoverValForm = val;
	  };
	$scope.onLeaveForm = function(){
	    $scope.hoverValForm = null;
	  }
	$scope.onChangeForm = function(val){
	    $scope.EvaInfo.ratingValForm = val;
	  }
	
	
	
	$scope.submitEvaluation = function(EvaInfo){
		BootstrapDialog.show({
				    title: '提示！',
				    message: '每个人只有一次机会评论哦，有理有据的评价会为您增分不少！\n点击“确定”提交评论 \n否则点击“关闭”继续评论',            
				    buttons: [{
			        label: '确定',
			        action: function(dialogItself) {
			            dialogItself.close();
			            $http({
								method:'POST',
								url:baseurl + "save_evaluate_opinion",
								data:{
									  user_code:EvaInfo.user_code,
									  project_num:EvaInfo.project_num,
									  satisficing:EvaInfo.ratingVal,
									  content:EvaInfo.ratingValContent,
									  reap:EvaInfo.ratingValGot,
									  modality:EvaInfo.ratingValForm,
									  content_info:EvaInfo.Evaluate				  
								},
								headers: {
							    	'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
							 	}
							}).success(function(rep) {
								$scope.backData=rep.retCode;
								switch(rep.retCode){
									case 0:
									BootstrapDialog.show({
									    title: '提示！',
									    message: '评价成功，谢谢！',  
									    buttons:[{
									    	label:'确定',
									    	action:function(dialogItselfCtr){
									    		dialogItselfCtr.close();
									    		window.location.href="../html/joinAlready.html?backurl="+window.location.href;
									    	}
									    }]
									    
								    }); 
								    break;
								    case 1:
									BootstrapDialog.show({
									    title: '提示！',
									    message: '已评价，不能再次提交！',
									    buttons:[{
									    	label:'确定',
									    	action:function(dialogItselfCtr){
									    		dialogItselfCtr.close();
									    		history.go(-1);
									    	}
									    }]
									    
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
			        label: '关闭',                
			        action: function(dialogItself) {                	
			        	dialogItself.close();                    
			        }
				    }]
			    }); 	
	}
	$scope.evaluate = function(list){
		switch($scope.eva){
			case '0':
			{window.location.href="../html/signinAndup.html";};
			break;
			case '1':
			{window.location.href="../html/joinAlready.html"};
			break;
		}
		
	}
	
	
})
.directive('evaluateOne', function() { 
 return { 
 restrict: 'E', 
 template: '<ul class="rating" ng-mouseleave="leave()">' +
        '<li ng-repeat="star in stars" ng-class="star"  style="font-size: 22px;" ng-click="click($index + 1)" ng-mouseover="over($index + 1)">' +
        '\u2605' +
        '</li>' +
        '</ul>',
 scope: {
      ratingValue: '=',
      max: '=',
      onHover: '=',
      onLeave: '='
    },
 controller: function($scope){
      $scope.ratingValue = $scope.ratingValue || 0 ;
      $scope.max = $scope.max || 5 ;
      $scope.click = function(val){        
        $scope.ratingValue = val;
      };
      $scope.over = function(val){
        $scope.onHover(val);
      };
      $scope.leave = function(){
        $scope.onLeave();
      }
     
    },
 link: function (scope, elem, attrs) {
      elem.css("text-align", "center");
      var updateStars = function () {
        scope.stars = [];
        for (var i = 0; i < scope.max; i++) {
          scope.stars.push({
            filled: i < scope.ratingValue
          });
        }
      };
      updateStars();
 scope.$watch('ratingValue', function (oldVal, newVal) {
        if (newVal) {
          updateStars();
        }
      });
      scope.$watch('max', function (oldVal, newVal) {
        if (newVal) {
          updateStars();
        }
         });
    }
 
 }; 
}) 
.directive('evaluateTwo', function() { 
   return { 
   restrict: 'E', 
   template: '<ul class="rating" ng-mouseleave="leave()">' +
        '<li ng-repeat="star in stars" ng-class="star"  style="font-size: 22px;" ng-click="click($index + 1)" ng-mouseover="over($index + 1)">' +
        '\u2605' +
        '</li>' +
        '</ul>',
   scope: {
      ratingValueContent: '=',
      maxContent: '=',
      onHoverContent: '=',
      onLeaveContent: '='
    },
   controller: function($scope){
      $scope.ratingValueContent = $scope.ratingValueContent || 0 ;
      $scope.maxContent = $scope.maxContent || 5 ;
      $scope.click = function(val){        
        $scope.ratingValueContent = val;
      };
      $scope.over = function(val){
        $scope.onHoverContent(val);
      };
      $scope.leave = function(){
        $scope.onLeaveContent();
      }
    },
   link: function (scope, elem, attrs) {
      elem.css("text-align", "center");
      var updateStars = function () {
        scope.stars = [];
        for (var i = 0; i < scope.maxContent; i++) {
          scope.stars.push({
            filled: i < scope.ratingValueContent
          });
        }
      };
      updateStars();
   scope.$watch('ratingValueContent', function (oldVal, newVal) {
        if (newVal) {
          updateStars();
        }
      });
      scope.$watch('maxContent', function (oldVal, newVal) {
        if (newVal) {
          updateStars();
        }
         });
    }
   
   }; 
}) 
.directive('evaluateThree', function() { 
   return { 
   restrict: 'E', 
   template: '<ul class="rating" ng-mouseleave="leave()">' +
        '<li ng-repeat="star in stars" ng-class="star"  style="font-size: 22px;" ng-click="click($index + 1)" ng-mouseover="over($index + 1)">' +
        '\u2605' +
        '</li>' +
        '</ul>',
   scope: {
      ratingValueGot: '=',
      maxGot: '=',
      onHoverGot: '=',
      onLeaveGot: '='
    },
   controller: function($scope){
      $scope.ratingValueGot = $scope.ratingValueGot || 0 ;
      $scope.maxGot = $scope.maxGot || 5 ;
      $scope.click = function(val){        
        $scope.ratingValueGot = val;
      };
      $scope.over = function(val){
        $scope.onHoverGot(val);
      };
      $scope.leave = function(){
        $scope.onLeaveGot();
      }
    },
   link: function (scope, elem, attrs) {
      elem.css("text-align", "center");
      var updateStars = function () {
        scope.stars = [];
        for (var i = 0; i < scope.maxGot; i++) {
          scope.stars.push({
            filled: i < scope.ratingValueGot
          });
        }
      };
      updateStars();
   scope.$watch('ratingValueGot', function (oldVal, newVal) {
        if (newVal) {
          updateStars();
        }
      });
      scope.$watch('maxGot', function (oldVal, newVal) {
        if (newVal) {
          updateStars();
        }
         });
    }
   
   }; 
})
.directive('evaluateFour', function() { 
   return { 
   restrict: 'E', 
   template: '<ul class="rating" ng-mouseleave="leave()">' +
        '<li ng-repeat="star in stars" ng-class="star"  style="font-size: 22px;" ng-click="click($index + 1)" ng-mouseover="over($index + 1)">' +
        '\u2605' +
        '</li>' +
        '</ul>',
   scope: {
      ratingValueForm: '=',
      maxForm: '=',
      onHoverForm: '=',
      onLeaveForm: '='
    },
   controller: function($scope){
      $scope.ratingValueForm = $scope.ratingValueForm || 0 ;
      $scope.maxForm = $scope.maxForm || 5 ;
      $scope.click = function(val){        
        $scope.ratingValueForm = val;
      };
      $scope.over = function(val){
        $scope.onHoverForm(val);
      };
      $scope.leave = function(){
        $scope.onLeaveForm();
      }
    },
   link: function (scope, elem, attrs) {
      elem.css("text-align", "center");
      var updateStars = function () {
        scope.stars = [];
        for (var i = 0; i < scope.maxForm; i++) {
          scope.stars.push({
            filled: i < scope.ratingValueForm
          });
        }
      };
      updateStars();
   scope.$watch('ratingValueForm', function (oldVal, newVal) {
        if (newVal) {
          updateStars();
        }
      });
      scope.$watch('maxForm', function (oldVal, newVal) {
        if (newVal) {
          updateStars();
        }
         });
    }
   
   }; 
}) 