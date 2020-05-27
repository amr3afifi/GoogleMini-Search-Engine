
var stateText = {
    'querySet': "",
    'page': 0,
    'rows': 10,
    'window': 10
    }

var stateImages = {
    'querySet': "",
    'page': 0,
    'rows': 20,
    'window': 10
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
console.log(document.getElementById('searchType').value);
function loadTextJson()
{
    const xhr= new XMLHttpRequest();
    var jsonName=document.getElementById('searchBox').value;
    xhr.open('get',jsonName+"_text.json",true);
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
    xhr.open('get',jsonName+"_images.json",true);
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
    <h3> ${myList[i].url} </h3>
    <a href="${myList[i].url}"><h1> ${myList[i].mainsite}</h1></a>
    <h2> website info..bio..description..</h2>
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