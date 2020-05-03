console.log("Imported")
function buttonHandler(){
    console.log("Click");
    //alert("Hello " + $('#name').val());
    $.post(
        "main",
        {
            "name": $('#name').val()
        },
        function (data, status) {
            var node = document.createElement("p");
            node.appendChild(document.createTextNode(data));
            $('#ajax-response').append(node);
        }
    );
}