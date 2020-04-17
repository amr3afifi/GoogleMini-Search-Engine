$(function(){
    //caching
    var sName        = $('#sName'),
        sAge         = $('#sAge'),
        sButt        = $('#sButt'),
        showBox      = $('#showBox'),
        updateBox    = $('#updateBox');
    // vars 
    var _url = 'http://rest.learncode.academy/api/:amit/:students';
    // create DOM
    function createDOM(d){
        showBox.append(`
            <li class='cards'>
                <h2 class='name'> <span class='on-text'>Name : </span>`+ d.name +`</h2>
                <h3> <span class='on-text'>Age : </span>`+ d.age +`</h3>
                <button data-click='UPDATE' id='`+d.id+`'>UPDATE</button>
                <button data-click='X' id='`+ d.id +`'>X</button>                
            </li>
        `)
    }
    // create Update DOM 
    function updateDOM(id){
        updateBox.html(`
            <input id='uSName' type='text'>
            <input id='uSAge' type='text'>
            <button id='`+id+`' data-click='DONE'>DONE</button>
        `)
    }
    //Ajax GET data
    function getData(){
        $.ajax({
            type    : 'GET',
            url     : _url,
            success : function(data){
                showBox.html('');
                for(var d of data){
                    createDOM(d)
                }
            },
            error   : function(err){
                console.error(err);
            }
        })
    }
    //Ajax POST data
    function postData(){
        var newStudent = {
            name    : sName.val(),
            age     : sAge.val()
        }
        $.ajax({
            type    : 'POST',
            url     : _url,
            data    : newStudent,
            success : function(d){
                createDOM(d)
            },
            error   : function(err){
                console.log(err)
            }
        })
    }
    //Ajax UPDATE data
    function updateData(id){
        var studentUpdate = {
            name    : $('#uSName').val(),
            age     : $('#uSAge').val(),
        }
        $.ajax({
            type    : 'PUT',
            url     : _url + '/' + id,
            data    : studentUpdate,
            success : function(){
                getData();
            },
            error   : function(err){
                console.log(err)
            }
        })
    }
    //Ajax DELETE data
    function deleteData(id){
        $.ajax({
            type    : 'DELETE',
            url     : _url + '/' + id,
            success : function(d){
                getData();
            },
            error   : function(err){
                console.log(err)
            }
        })
    }
    // events 
    $(document).click(function(e){
        var btt     = $(e.target),
            bttAtt  = btt.attr('data-click'),
            bId     = btt.attr('id');
        // updateClick
        switch (bttAtt){
            case 'UPDATE':
                updateDOM(bId);
                break;
            case 'DONE':
                updateData(bId);
                updateBox.html('');
                break;
            case 'X':
                deleteData(bId);
                break;
            default : 
                return false
        }
    })
    getData()
    sButt.click(postData);

})