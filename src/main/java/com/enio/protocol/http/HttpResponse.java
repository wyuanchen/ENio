package com.enio.protocol.http;

import java.util.Map;

/**
 * Created by yuan on 11/15/16.
 */
public class HttpResponse extends HttpProtocol{
    enum HttpStatusCode{
        // 1xx Informational
        HttpStatusContinue(100),
        HttpStatusSwichingProtocols(101),

        // 2xx Successful
        HttpStatusOk(200),
        HttpStatsuCreated(201),
        HttpStatusAccepted(202),
        HttpStatusNonAuthorizedInformation(203),
        HttpStatusNoContent(204),
        HttpStatusResetContent(205),
        HttpStatusPartialContent(206),

        //4xx Client Error
        HttpStatusBadRequest(400),
        HttpStatusUnauthorized(401),
        HttpStatusPaymentRequired(402),
        HttpStatusForbidden(403),
        HttpStatusNotFound(404),


        //5xx Server Error
        HttpStatusInternalServerError(500),
        HttpStatusNotImplemented(501),
        HttpStatusBadGateway(502),
        HttpStatusServiceUnavaliable(503),
        HttpStatusGatewayTimeOut(504),
        HttpStatusHttpVersionNotSupported(505);

        HttpStatusCode(int value){
            this.value=String.valueOf(value);
        }
        private String value;
        private String getValue(){
            return value;
        }
    }


    private HttpStatusCode statusCode;
    private String serverName;
    private String contentType;
    private boolean closeConnection;


    public boolean isCloseConnection() {
        return closeConnection;
    }

    public void setCloseConnection(boolean closeConnection) {
        this.closeConnection = closeConnection;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public void setVersion(HttpVersion version) {
        this.version = version;
    }

    public HttpResponse(){}



}
