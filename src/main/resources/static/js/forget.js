$(function () {
    $("#verifycodeBtn").click(getVerificationCode);
});

function getVerificationCode() {
    var email = $("#your-email").val();

    $.get(
        CONTEXT_PATH + "/verificationCode",
        {
            "email":email
        },
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                alert("验证码已发送至您的邮箱,请及时查看!");
            } else {
                alert(data.msg);
            }
        }
    );
}