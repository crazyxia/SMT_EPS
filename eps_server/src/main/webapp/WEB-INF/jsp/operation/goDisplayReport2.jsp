<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<base href="/eps_server/">
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <title>实时显示报表展示页面</title>
    <link rel="stylesheet" href="static/css/myCss.css">
    <script src="static/js/jquery-1.11.3.min.js"></script>
    <script src="static/js/myJs.js"></script>
    <script src="static/js/konva.min.js"></script>
</head>
<body>
<div class="nav">
    <section id="nav-left">
        <em>当前产线:</em>
        <span>
        	<select id="line">
        		<option value=0>1</option>
        		<option value=1>2</option>
        		<option value=2>3</option>
        		<option value=3>4</option>
        		<option value=4 selected="selected">5</option>        		
        	</select>
        </span>
    </section>
    <%--<section id="click-left">--%>
        <%--<em class="out"></em>--%>
    <%--</section>--%>
    <section id="main">
        <div>上料情况</div>
        <div>换料情况</div>
        <div>抽检情况</div>
        <div>全检情况</div>
    </section>
    <section id="click-right">
        <em class="out"></em>
    </section>
    <section id="nav-right" >
        <div class="start"></div>
    </section>
</div>
<div class="mainText" >
    <span class="clock"></span>
        <section id="sec1">
        </section>
        <section id="sec2">
        </section>
        <section id="sec3">
        </section>
        <section id="sec4">
        </section>
    </div>
</body>
</html>