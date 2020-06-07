let myChart = document.getElementById('myChart').getContext('2d');
Chart.defaults.global.defaultFontFamily = 'arial';
Chart.defaults.global.defaultFontSize = 12;
Chart.defaults.global.defaultFontColor = '#777';
var stateText = {'querySet': "",'page': 0,'rows': 10,'window': 10}
var stateImages = {'querySet': "",'page': 0,'rows': 20,'window': 10}

loadTrendsJson(0);

// console.log("ready?");  
// $(document).ready(function(){
//     console.log("ready?");  
//     $('#searchBox').keyup(function(){  
//          var query = $(this).val();  
//          if(query != '')  
//          {  
//              console.log(query);
//               $.ajax({  
//                    url:"autocomplete.php",  
//                    method:"POST",  
//                    data:{query:query},  
//                    success:function(data)  
//                    {  
//                         $('#searchList').fadeIn();  
//                         $('#searchList').html(data);  
//                    }  
//               });  
//          }  
//     });  
//     $(document).on('click', 'li', function(){  
//          $('#searchBox').val($(this).text());  
//          $('#searchList').fadeOut();  
//     });  
// });  

function redrawChart(mylabels,mydatasets)
{
new Chart(myChart,{
type:'bar', // bar, horizontalBar, pie, line, doughnut, radar, polarArea
data:{labels:mylabels,datasets:mydatasets},
options:{
layout:{ padding:{left:50,right:0,bottom:0,top:-10000},},
tooltips:{enabled:true}
}
});
}
    
if(document.getElementById('searchType').value=="text")
{
    loadTextJson();
    console.log("text");
}
else
{
    loadImagesJson();
    console.log("images");
}

function countryTrendChange()
{
    let val = document.getElementById('country').selectedIndex ;
    console.log(val);
    loadTrendsJson(val);
}

function loadTextJson()
{
    const xhr= new XMLHttpRequest();
    var jsonName=document.getElementById('searchBox').value;
    console.log(jsonName);
    var p=document.getElementById('phrase').value;
    var country=document.getElementById('geographicalLocation').value;
    console.log(country);
    xhr.open('get',jsonName+"_"+country+p+"_text.json",true);
    xhr.onload=function(){
   
        if(this.status==200)
        {
            try{
                var resObj= JSON.parse(this.responseText);
                stateText.querySet=resObj;
                stateText.page=1;
                rows=2;
                console.log(resObj)
                buildTableText(stateText.querySet,stateText.page,stateText.rows);
            }
            catch(e)
            {
                console.warn('parse error');
            }  
        }else console.warn('not 200');
    }
    xhr.send();
}

function loadImagesJson()
{
    const xhr= new XMLHttpRequest();
    var jsonName=document.getElementById('searchBox').value;
    var p=document.getElementById('phrase').value;
    var country=document.getElementById('geographicalLocation').value;
    xhr.open('get',jsonName+"_"+country+p+"_images.json",true);
    xhr.onload=function(){
        if(this.status==200)
        {
            try{
                var resObj= JSON.parse(this.responseText);
                stateImages.querySet=resObj;
                stateImages.page=1;
                rows=2;
                console.log(resObj)
                buildTableImages(stateImages.querySet,stateImages.page,stateImages.rows);
            }
            catch(e)
            {
                console.warn('parse error');
            }  
        }else console.warn('not 200');
    }
    xhr.send();
}

function loadTrendsJson(countryIndex)
{
    const xhr= new XMLHttpRequest();
    var country=document.getElementById('geographicalLocation').value;
    xhr.open('get',"trends.json",true);
    xhr.onload=function(){
        if(this.status==200)
        {
            try{
                var resObj= JSON.parse(this.responseText);
                var table = $('#rank');
                console.log(resObj[countryIndex].country);
                console.log(resObj[countryIndex].trend_0_text);
                console.log(resObj[countryIndex].trend_0_count);
                var row=`<h4> Rank # </h4>`
                table.empty();
                table.append(row)

                var mylabels=[];
                var mydatasets=[{label:'Searches',data:[],borderWidth:1,borderColor:'#777',hoverBorderWidth:3,hoverBorderColor:'#000'}]
                if(resObj!=null || resObj!=undefined)
                {
                mylabels.push(resObj[countryIndex].trend_0_text);
                console.log(resObj[countryIndex].trend_0_text);
                mydatasets[0].data.push(resObj[countryIndex].trend_0_count);
                row=`<h5> 1. ${resObj[countryIndex].trend_0_text} - ${resObj[countryIndex].trend_0_count} </h5>`;
                table.append(row);

                mylabels.push(resObj[countryIndex].trend_1_text);
                mydatasets[0].data.push(resObj[countryIndex].trend_1_count);
                row=`<h5> 2. ${resObj[countryIndex].trend_1_text} - ${resObj[countryIndex].trend_1_count} </h5>`;
                table.append(row);

                mylabels.push(resObj[countryIndex].trend_2_text);
                mydatasets[0].data.push(resObj[countryIndex].trend_2_count);
                row=`<h5> 3. ${resObj[countryIndex].trend_2_text} - ${resObj[countryIndex].trend_2_count} </h5>`;
                table.append(row);

                mylabels.push(resObj[countryIndex].trend_3_text);
                mydatasets[0].data.push(resObj[countryIndex].trend_3_count);
                row=`<h5> 4. ${resObj[countryIndex].trend_3_text} - ${resObj[countryIndex].trend_3_count} </h5>`;
                table.append(row);

                mylabels.push(resObj[countryIndex].trend_4_text);
                mydatasets[0].data.push(resObj[countryIndex].trend_4_count);
                row=`<h5> 5. ${resObj[countryIndex].trend_4_text} - ${resObj[countryIndex].trend_4_count} </h5>`;
                table.append(row);

                mylabels.push(resObj[countryIndex].trend_5_text);
                mydatasets[0].data.push(resObj[countryIndex].trend_5_count);
                row=`<h5> 6. ${resObj[countryIndex].trend_5_text} - ${resObj[countryIndex].trend_5_count} </h5>`;
                table.append(row);

                mylabels.push(resObj[countryIndex].trend_6_text);
                mydatasets[0].data.push(resObj[countryIndex].trend_6_count);
                row=`<h5> 7. ${resObj[countryIndex].trend_6_text} - ${resObj[countryIndex].trend_6_count} </h5>`;
                table.append(row);

                mylabels.push(resObj[countryIndex].trend_7_text);
                mydatasets[0].data.push(resObj[countryIndex].trend_7_count);
                row=`<h5> 8. ${resObj[countryIndex].trend_7_text} - ${resObj[countryIndex].trend_7_count} </h5>`;
                table.append(row);

                mylabels.push(resObj[countryIndex].trend_8_text);
                mydatasets[0].data.push(resObj[countryIndex].trend_8_count);
                row=`<h5> 9. ${resObj[countryIndex].trend_8_text} - ${resObj[countryIndex].trend_8_count} </h5>`;
                table.append(row);

                mylabels.push(resObj[countryIndex].trend_9_text);
                mydatasets[0].data.push(resObj[countryIndex].trend_9_count);
                row=`<h5>10. ${resObj[countryIndex].trend_9_text} - ${resObj[countryIndex].trend_9_count} </h5>`
                table.append(row);
                }
                console.log(mylabels);console.log( mydatasets[0].data);
                redrawChart(mylabels,mydatasets); 
            }
            catch(e)
            {
                console.log('parse error');
            }  
        }else console.log('not 200');
    }
    xhr.send();
}

function paginationText(querySet, page, rows) 
{

var trimStart = (page - 1) * rows
var trimEnd = trimStart + rows

var trimmedData = querySet.slice(trimStart, trimEnd)

var pages = Math.round(querySet.length / rows);

return {
    'querySet': trimmedData,
    'pages': pages,
}
}

function paginationImages(querySet, page, rows) 
{

var trimStart = (page - 1) * rows
var trimEnd = trimStart + rows

var trimmedData = querySet.slice(trimStart, trimEnd)

var pages = Math.round(querySet.length / rows);

return {
    'querySet': trimmedData,
    'pages': pages,
}
}

function pageButtonsText(pages) 
{
var wrapper = document.getElementById('pagination-wrapper-text')

wrapper.innerHTML = ``

var maxLeft = (stateText.page - Math.floor(stateText.window / 2))
var maxRight = (stateText.page + Math.floor(stateText.window / 2))

if (maxLeft < 1) {
    maxLeft = 1
    maxRight = stateText.window
}

if (maxRight > pages) {
    maxLeft = pages - (stateText.window - 1)
    
    if (maxLeft < 1){
        maxLeft = 1
    }
    maxRight = pages
}



for (var page = maxLeft; page <= maxRight; page++) {
    wrapper.innerHTML += `<button value=${page} class="page btn btn-sm btn-info ">${page}</button>`
}

if (stateText.page != 1) {
    wrapper.innerHTML = `<button value=${1} class="page btn-sm btn-info btn-danger">&#171; First</button>` + wrapper.innerHTML
}

if (stateText.page != pages) {
    wrapper.innerHTML += `<button value=${pages} class="page btn-sm btn-info btn-danger">Last &#187;</button>`
}

$('.page').on('click', function() {
    $('#result-table-text').empty()

    stateText.page = Number($(this).val())
    
    buildTableText(stateText.querySet,stateText.page,stateText.rows)
})

}

function pageButtonsImages(pages) 
{
var wrapper = document.getElementById('pagination-wrapper-images')

wrapper.innerHTML = ``

var maxLeft = (stateImages.page - Math.floor(stateImages.window / 2))
var maxRight = (stateImages.page + Math.floor(stateImages.window / 2))

if (maxLeft < 1) {
    maxLeft = 1
    maxRight = stateImages.window
}

if (maxRight > pages) {
    maxLeft = pages - (stateImages.window - 1)
    
    if (maxLeft < 1){
        maxLeft = 1
    }
    maxRight = pages
}



for (var page = maxLeft; page <= maxRight; page++) {
    wrapper.innerHTML += `<button value=${page} class="page btn btn-sm btn-info ">${page}</button>`
}

if (stateImages.page != 1) {
    wrapper.innerHTML = `<button value=${1} class="page btn-sm btn-info btn-danger">&#171; First</button>` + wrapper.innerHTML
}

if (stateImages.page != pages) {
    wrapper.innerHTML += `<button value=${pages} class="page btn-sm btn-info btn-danger">Last &#187;</button>`
}

$('.page').on('click', function() {
    $('#result-table-images').empty()

    stateImages.page = Number($(this).val())
    
    buildTableImages(stateImages.querySet,stateImages.page,stateImages.rows)
})

}

function buildTableText(querySet,page,rows) {
    
var table = $('#result-table-text')
var data = paginationText(querySet,page,rows)
var myList = data.querySet
table.empty();

for (var i = 1 in myList) {
    var row=`<div class="component">
    <h3 style="color:#3cba54;"> ${myList[i].url} </h3>
    <a href="${myList[i].url}"><h1> ${myList[i].mainsite}</h1></a>
    <h2>${myList[i].snippet}</h2>
    </div>`
    table.append(row)
}

pageButtonsText(data.pages)
}

function buildTableImages(querySet,page,rows) {
    
    var table = $('#result-table-images')
    var data = paginationImages(querySet,page,rows)
    var myList = data.querySet
    table.empty();
    for (var i = 1 in myList) {
        console.log(myList.src);
        var row=`<a href="${myList[i].url}"><img class="component-images" src="${myList[i].src}" alt="${myList[i].alt}"/></a>`
    
        table.append(row)
    }
    
    pageButtonsImages(data.pages)
    }

function toggleText() {
    loadTextJson();
    document.getElementById("result-images").style.display="none";
    document.getElementById("result-trends").style.display="none";
    document.getElementById("result-text").style.display="block";
    document.getElementById("searchType").value="text";
    document.getElementById("geographicalLocation").style.display="block";
    console.log("text")
    }

function toggleImages() {
    loadImagesJson();
    document.getElementById("result-text").style.display="none";
    document.getElementById("result-trends").style.display="none";
    document.getElementById("result-images").style.display="block";
    document.getElementById("searchType").value="images";
    document.getElementById("geographicalLocation").style.display="block";
    console.log("images")
    }

function toggleTrends() {
    document.getElementById("result-text").style.display="none";
    document.getElementById("result-images").style.display="none";
    document.getElementById("result-trends").style.display="block";
    document.getElementById("searchType").value="trends";
    document.getElementById("geographicalLocation").style.display="none";
    console.log("trends")
    }