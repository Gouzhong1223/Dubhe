package org.dubhe.course.handler;


import org.dubhe.biz.base.constant.ResponseCode;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.course.common.exception.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description : 异常拦截器
 * @Date : create by QingSong in 2020-11-21 17:09
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.youcode.yxc.clockin.handler
 * @ProjectName : clockin-youcode
 * @Version : 1.0.0
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    //    @ExceptionHandler(ClockInException.class)
//    @ResponseBody
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseDto handlerBlogException(ClockInException ex) {
//        return new ResponseDto(ex.getCode(), ex.getMessage());
//    }
//
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DataResponseBody handlerAllException(Exception ex) {
        return new DataResponseBody(ResponseCode.ERROR, ex.getMessage());
    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseBody
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        return new ResponseDto(ResultCode.VALUE_NULL.getCode(), Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
//    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DataResponseBody handleMethodArgumentNotValidException(FileUploadException e) {
        return new DataResponseBody(ResponseCode.ERROR, e.getMessage());
    }

//    @ExceptionHandler(UnLoginException.class)
//    @ResponseBody
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResponseDto handleMethodArgumentNotValidException(UnLoginException e) {
//        return new ResponseDto(401, e.getMessage());
//    }


}
