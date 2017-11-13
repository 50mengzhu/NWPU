/* global define */

//var baseurl="http://172.16.0.42:8082/index.php/api/api/"; //http://222.24.192.176   http://172.16.0.42:8082
var baseurl="http://222.24.192.176/index.php/api/api/"; //http://222.24.192.176   http://172.16.0.42:8082

var app=angular.module('main',['LocalStorageModule']);
app.config(function (localStorageServiceProvider) {
  localStorageServiceProvider
    .setPrefix('main')
    .setStorageType('sessionStorage')
    .setNotify(true, true)
});
function add0(m){return m<10?'0'+m:m }
function formattime(timeint)
{
	//timeint是整数，否则要parseInt转换
	var time = new Date(timeint);
	var y = time.getFullYear();
	var m = time.getMonth()+1;
	var d = time.getDate();
	var h = time.getHours();
	var mm = time.getMinutes();
	var s = time.getSeconds();
	return add0(m)+'月'+add0(d)+'日 '+add0(h)+':'+add0(mm);
}

//对时间格式进行调整
function trimtime(element)
{	
	var signUp_start_time = element.signup_starttime;
	var signUp_end_time= element.signup_endtime;
	var start_time = element.start_time;
	var end_time = element.end_time;
	signUp_start_time=new Date(signUp_start_time);
	signUp_end_time=new Date(signUp_end_time);
	start_time = new Date(start_time);
	end_time = new Date(end_time);
	
	element.signup_starttime = formattime(signUp_start_time);
	element.signup_endtime = formattime(signUp_end_time);
	element.start_time_list = formattime(start_time);
	element.end_time_list = formattime(end_time);	
}

//项目类型：活动、讲座
function getProjectType(element)
{
	element.projectType = "";	
	
	if(element.education_type == "1"){ //活动 项目		
		element.projectType = "glyphicon glyphicon-bookmark";
		element.projectname = "活动："+element.name;
	}
	if(element.education_type == "2"){ //讲座 项目
		element.projectType = "glyphicon glyphicon-th-list";
		element.projectname = "讲座："+element.name;
	}	
}

//每个报名列表元素的按钮文本
function Dealbutton(element)
{
	var signUp_start_time = element.signup_starttime;
	var signUp_end_time= element.signup_endtime;
	var start_time = element.start_time;
	var end_time = element.end_time;
	signUp_start_time=new Date(signUp_start_time);	
	signUp_end_time=new Date(signUp_end_time);
	start_time = new Date(start_time);
	end_time = new Date(end_time);

	if (element.signup_projectnum == null){
		if(signUp_start_time.valueOf() > new Date().valueOf()){
			element.buttonText = '报名未开始';
    		element.status = 'btnNG';
		}
		
		if(signUp_end_time.valueOf() <= new Date().valueOf()){
			element.buttonText = '报名时间已过';
			element.status = 'btnNG';
		}
		
		if((signUp_start_time.valueOf() < new Date().valueOf())&&(new Date().valueOf() <= signUp_end_time.valueOf() ))
		{
			if((start_time.valueOf() < new Date().valueOf())&&(end_time.valueOf()>new Date().valueOf())){
			  	element.buttonText = '抓紧报名';
				element.buttoncomment = "活动已开始";
				element.status = 'btnOk';
			}
			if(start_time.valueOf() > new Date().valueOf()){
				element.buttonText = '立即报名';
	    		element.buttoncomment = "";
	    		element.status = 'btnOk';
			}    			
		}
	}
	if(element.signup_projectnum != null){
		if(element.apply_result == null){
			element.buttonText = '取消报名';
	  		element.buttoncomment = "报名待审查";
	  		element.status = 'btnCheck';
		}
		if(element.apply_result =="1"){
			if (element.join_result =="1"){
				if(element.evaluate_project_num == null){
					element.buttonText = '待评价'; 
		  			element.buttoncomment = "";
		  			element.status = 'btnJudgeNG';
		  		}else{
		  			element.buttonText = '已评价'; 
		  			element.buttoncomment = "";
		  			element.status = 'btnJudgeOk'; 
		  		}
	  		}else{
		  		element.buttonText = '报名成功';
		  		element.buttoncomment = "";
		  		element.status = 'btnPass';    	  	
	  		}
		}
		if(element.apply_result =="0"){
			element.buttonText = '审核未通过';
	  		element.buttoncomment = "";
	  		element.status = 'btnNoPass';
		}		
	}
}




//报名取消按钮的响应




//显示评价信息  TODO

