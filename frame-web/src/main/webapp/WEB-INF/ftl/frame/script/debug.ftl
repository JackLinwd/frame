<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>frame Script Validator Debug</title>
    <script type="text/javascript" src="debug/debug.js"></script>
<#list data.names as name>
    <script type="text/javascript" src="debug${name}"></script>
</#list>
    <style type="text/css">
        input, textarea {
            width: 95%;
        }
    </style>
</head>
<body onload="javascript:frame.ready.execute();">
<div>
    <div>验证器名称，多个间以逗号分割。</div>
    <input id="names"/>
    <div>验证参数，JSON格式数据。</div>
    <textarea id="parameter" rows="10"></textarea>
    <div>
        <button onclick="javascript:debug();">执行</button>
    </div>
    <div id="result"></div>
</div>
</body>
</html>