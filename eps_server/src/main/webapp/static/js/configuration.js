$(function () {
    var lineChoice = "";
    var $timeId1 = 0;    //时间设置值的id
    var idNum = 0;
    var sendArray = [];   //存放要传输的数据
    var flag = 0;    //用于判断是否执行
//存储同一线号的数据
    var $0 = [];
    var $1 = [];
    var $2 = [];
    var $3 = [];
    var $4 = [];
    var $5 = [];    
//    查询按钮
    $("#searchBtn").hover(function () {
        $(this).addClass("ui-state-hover");
    }, function () {
        $(this).removeClass("ui-state-hover");
    }).on("click", function () {
        for (var i = 0; i < $("#lineChoice option").length; i++) {
            $("#lineChoice option").eq(i).removeAttr("selected");
        }
        //$("#lineChoice option").eq(0).attr("selected",true);
        $("#lineChoice").val(100);
        $timeId1 = 0;
        $("#main").empty();
        $.ajax({
            url: "config/list",
            type: "post",
            dataType: "json",
            data: {},
            success: function (data) {
                $0 = [];
                $1 = [];
                $2 = [];
                $3 = [];
                $4 = [];
                $5 = [];
                


                var dataLength = data.length;
                var tempArray = [];
                for (var i = 0; i < dataLength; i++) {
                    var $value = data[i].line;
                    if (data[i].line === '1') {
                        $0.push(JSON.parse(JSON.stringify(data[i])));
                    }

                    switch ($value) {
                        case "1" :
                            $1.push(data[i]);
                            break;
                        case "2" :
                            $2.push(data[i]);
                            break;
                        case "3" :
                            $3.push(data[i]);
                            break;
                        case "4" :
                            $4.push(data[i]);
                            break;
                        case "5" :
                            $5.push(data[i]);
                            break;                        
                    }
                }
                for (var j = 0; j < $0.length; j++) {
                    $0[j].value = "";
                }
                //301线
                // for (var i = 1; i < 9; i++) {
                //     createTable(eval("$30" + i));
                // }

                //总配置框
                createAllTable($0, 4);

                //产线下拉框
                $("#lineChoice").on("change", function () {
                    flag = 0;
                    $("#main").empty();
                    lineChoice = $("#lineChoice option:selected").text();
                    select(lineChoice, flag);
                });
            },
            error: function () {
                console.log("数据传输错误！！");
            }
        });
    });
    //保存按钮
    $("#saveBtn").hover(function () {
        $(this).addClass("ui-state-hover");
    }, function () {
        $(this).removeClass("ui-state-hover");
    })
        .on("click", function () {
            lineChoice = $("#lineChoice option:selected").text();
            sendArray = [];  //每次清空数组
            idNum = 0;
            flag = 1;
            //select(lineChoice, flag);
            //将所有数组放到array中
            //console.log($301);
            if (select(lineChoice, flag) !== false) {
                for (var i = 1; i < 6; i++) {
                    getAndPush(eval("$" + i))
                }
                var $newJson = JSON.stringify(sendArray);
                $.ajax({
                    url: "config/set",
                    type: "post",
                    dataType: "json",
                    data: {
                        configs: $newJson
                    },
                    success: function (data) {
                        if (data.result == "succeed") {
                            alert("保存成功");
                        }
                    },
                    error: function () {
                        console.log("数据传输错误！！");
                    }
                });
            } else {
                alert("数据不能为空，请重新输入！")
            }
        });
    //create all setting
    function createAllTable(data, items) {
            var html = "";
            html += "<tr>";
            html += "<td rowspan='" + items + "'>" + "---" + "</td>";
            html += "<td>" + data[0].alias + "</td>";
            html += "<td>" + "<input id='" + $timeId1 + "' type='text' value='" + data[0].value + "' style='border: 1px solid black;padding: 5px;'>" + "</td>"
            html += "<td>" + data[0].description + "</td>"
            html += "</tr>";
            $("#main").append(html);
            $timeId1++;

        for (var count = 1; count < data.length; count ++) {
            var htmlLeft = "";
            htmlLeft += "<tr>";
            htmlLeft += "<td>" + data[count].alias + "</td>";
            htmlLeft += "<td>" + "<input id='" + $timeId1 + "' type='text' value='" + data[count].value + "' style='border: 1px solid black;padding: 5px;'>" + "</td>"
            htmlLeft += "<td>" + data[count].description + "</td>"
            htmlLeft += "</tr>";
            $("#main").append(htmlLeft);
            $timeId1++;
        }
    }
    //针对一个线号对应多个数据
    function createMulTable(data, w) {
        var html = "";
        var checkBox = data[0].enabled == true ? "<input id='0" + $timeId1 + "' type='checkbox' checked=" + false + ">" : "<input id='0" + $timeId1 + "' type='checkbox'>";
        html += "<tr>"
        html += "<td rowspan='" + w + "'>" + data[0].line + "</td>"
        html += "<td>" + data[0].alias + "</td>"
        html += "<td>" + "<input id='" + $timeId1 + "'  type='text' value='" + data[0].value + "' style='border: 1px solid black;padding: 5px;'>" + "</td>"
        // html +=     "<td>" + checkBox + "</td>"
        html += "<td>" + data[0].description + "</td>"
        html += "</tr>";
        $("#main").append(html);
        $timeId1++;
    }

//    生成缩进一格的一行table
    function createOneTable(data, k) {
        var checkBox = data[k].enabled == true ? "<input id='0" + $timeId1 + "' type='checkbox' checked=" + false + ">" : "<input id='0" + $timeId1 + "' type='checkbox'>";
        var html = "";
        html += "<tr>"
        html += "<td>" + data[k].alias + "</td>"
        html += "<td>" + "<input id='" + $timeId1 + "'  type='text' value='" + data[k].value + "' style='border: 1px solid black;padding: 5px'>" + "</td>"
        // html +=     "<td>" + checkBox + "</td>"
        html += "<td>" + data[k].description + "</td>"
        html += "</tr>";
        $("#main").append(html);
        $timeId1++;
    }

    //生成单独一行表格
    function createTwoTable(data, k) {
        var checkBox = data[k].enabled == true ? "<input id='0" + $timeId1 + "' type='checkbox' checked=" + false + ">" : "<input id='0" + $timeId1 + "' type='checkbox'>";
        var html = "";
        html += "<tr>"
        html += "<td>" + data[k].line + "</td>"
        html += "<td>" + data[k].alias + "</td>"
        html += "<td>" + "<input id='" + $timeId1 + "' type='text' value='" + data[k].value + "' style='border: 1px solid black;padding: 5px'>" + "</td>"
        // html +=     "<td>" + checkBox + "</td>"
        html += "<td>" + data[k].description + "</td>"
        html += "</tr>";
        $("#main").append(html);
        $timeId1++;
    }

    //生成表格
    function createTable(array) {
        if (array.length > 1) {
            createMulTable(array, array.length);
            for (var i = 1; i < array.length; i++) {
                createOneTable(array, i);
            }
        } else if (array.length == 1) {
            createTwoTable(array, 0);
        }
    }

//    获取配置内容
    function getConMain(array, num) {
        var index = 0;
        for (var i = num; i < num + array.length; i++) {
            if ($("#" + i).val() !== "") {
                array[index].value = $("#" + i).val();
                //array[index].enabled = $("#0" + i).prop("checked");
                index++;
            } else {
                return false;
            }
        }
        return true;
    }


//    产线筛选
    function select(a, t) {
        switch (a) {
            case "统一" :
                $timeId1 = 0;
                // if( t == 0){
                //     for( var i = 1 ; i < 9 ; i++){
                //         createTable(eval("$30"+i));
                //     }
                // }else if( t == 1){
                //     for( var i = 1 ; i < 9 ; i++){
                //             getConMain(eval("$30"+i),idNum);
                //             idNum += eval("$30"+i).length;
                //     }
                // }
                if (t === 0) {
                    createAllTable($0, 4);
                } else if (t === 1){
                    for( var i = 1 ; i < 6 ; i++){
                        if ( !getConMain(eval("$" + i), 0)) {
                            return false;
                        }
                    }
                }
                break;
            case "1" :
                $timeId1 = 0;   //保证ID一直一致
                if (t == 0) {
                    createTable($1);
                } else if (t == 1) {
                    if (!getConMain($1, $timeId1)) {
                        return false;
                    }

                }
                break;
            case "2" :
                $timeId1 = $1.length;
                if (t == 0) {
                    createTable($2);
                } else if (t == 1) {
                    if (!getConMain($2, $timeId1)) {
                        return false;
                    }

                }
                break;
            case "3" :
                $timeId1 = $1.length + $2.length;
                if (t == 0) {
                    createTable($3);
                } else if (t == 1) {
                    if (!getConMain($3, $timeId1)) {
                        return false;
                    }

                }
                break;
            case "4" :
                $timeId1 = $1.length + $2.length + $3.length;
                if (t == 0) {
                    createTable($4);
                } else if (t == 1) {
                    if (!getConMain($4, $timeId1)) {
                        return false;
                    }
                }
                break;
            case "5" :
                $timeId1 = $1.length + $2.length + $3.length + $4.length;
                if (t == 0) {
                    createTable($5);
                }
                else if (t == 1) {
                    if (!getConMain($5, $timeId1)) {
                        return false;
                    }
                }
                break;            
        }
    }

//    将数据从数组中拿出放到array中
    function getAndPush(array) {
        for (var i = 0; i < array.length; i++) {
            sendArray.push(array[i]);
        }
    }
});