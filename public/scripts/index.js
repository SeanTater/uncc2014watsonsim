/*$(function() {
    $("#search").ajaxForm({
    beforeSubmit: function() {
        $("#note").text("Asking learned grand-masters for insight.");
        return true; 
    },
    success: function(response) {
        $("#note").empty();
        $("#results").empty();
        response.answers.forEach(function(item) {
            var x = $("<li>"+item.title+"</li>");
            x[0].style.background = "linear-gradient(#4FA5C2 " + 100 * item.score + ", #C8DAE0 " + 100 * item.score + ")";
            $("#results").append(x);
        });
    }});
});*/
