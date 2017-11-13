app.controller('EvaluateAlreadyCtrl',function($scope,$http,$location,localStorageService){
	
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
	$http({
		method:'POST',
		url:baseurl + "select_project_evaluate_bystu",
		data:{
			user_code:$scope.ID,
			project_num:$scope.projectNum
			},
		headers: {
	    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
	 	}
	}).success(function(rep){				
		$scope.ProjectData=rep.data;		
		
		angular.forEach($scope.ProjectData,function(element, index){
			$scope.EvaInfo.ratingVal=element.satisficing;
			$scope.EvaInfo.ratingValContent=element.content_satisficing;
			$scope.EvaInfo.ratingValForm=element.modality_satisficing;
			$scope.EvaInfo.ratingValGot=element.reap_satisficing;			
			
	  		if (element.content	== "" || element.content == null){
	  			element.content	= "-";
	  		}
		});
				
	}).error(function(rep){
		BootstrapDialog.show({
        	title: '提示！',
        	message: '网络或服务器问题！\n请连接网络后重新操作！',            
   		});
   
	});
		
	

	
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
	
	$scope.evaluate = function(list){
		switch($scope.eva){
			case '0':
			{window.location.href="../html/signinAndup.html";};
			break;
			case '1':
			{window.location.href="../html/joinAlready.html";};
		}
		
	}
	
	
})
.directive('evaluateOne', function() { 
 return { 
 restrict: 'E', 
 template: '<ul class="rating" ng-mouseleave="leave()">' +
        '<li ng-repeat="star in stars" ng-class="star"  style="font-size: 22px;"  ng-mouseover="over($index + 1)">' +
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
        '<li ng-repeat="star in stars" ng-class="star"  style="font-size: 22px;"  ng-mouseover="over($index + 1)">' +
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
        '<li ng-repeat="star in stars" ng-class="star"  style="font-size: 22px;"  ng-mouseover="over($index + 1)">' +
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
        '<li ng-repeat="star in stars" ng-class="star"  style="font-size: 22px;"  ng-mouseover="over($index + 1)">' +
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