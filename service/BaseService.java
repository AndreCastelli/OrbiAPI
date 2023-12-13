package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class BaseService {
    private static final int STATUS_CODE_OK = 200;
    private static final String X_SOURCE = "X-Source";
    protected PropertiesUtil propertiesUtil;

    public Response doForceJobExecute(String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .when()
                .get(resource);
    }

    public Response doGetRequestOneAttribute(String resource, String param) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .when()
                .get(resource, param);
    }

    public Response doGetRequestWthPagination(String resource, long pageNumber) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .queryParam("page", pageNumber)
                .when()
                .get(resource)
                .then()
                .statusCode(STATUS_CODE_OK)
                .contentType(ContentType.JSON)
                .extract()
                .response();
    }

    public Response doGetRequestWithQueryParam(String resource, String param, String queryParamName, int queryParam) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .queryParam(queryParamName, queryParam)
                .when()
                .get(resource, param);
    }

    public Response doPostRequestMap(Map payloadMap, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .body(payloadMap)
                .when()
                .post(resource);
    }

    public Response doPatchRequestMap(Map payloadMap, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .body(payloadMap)
                .when()
                .patch(resource);
    }

    public Response doPatchRequestWithQueryParam(Map params, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .queryParams(params)
                .when()
                .patch(resource);
    }

    public Response doPostRequestMapWithQueryParams(String payloadMap, Map params, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.TEXT)
                .queryParams(params)
                .body(payloadMap)
                .when()
                .post(resource);
    }

    public Response doPostRequestMapAndQueryParamsTypeJson(String payloadMap, Map params, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .queryParams(params)
                .body(payloadMap)
                .when()
                .post(resource);
    }

    public Response doGetRequestWithListParams(Map params, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .params(params)
                .when()
                .get(resource);
    }

    public Response doGetRequestWithListParamsAndXsource(Map params, String resource, String xSource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .header(X_SOURCE, xSource)
                .params(params)
                .when()
                .get(resource);
    }

    public Response doGetRequestTwoAttributes(String resource, String param, String xSource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .header(X_SOURCE, xSource)
                .when()
                .get(resource, param);
    }

    public Response doGetRequestWithPathParams(Map<String, String> pathParams, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .pathParams(pathParams)
                .when()
                .get(resource);
    }

    public Response doPostRequestJson(String payloadJson, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .body(payloadJson)
                .when()
                .post(resource);
    }

    public Response doPostRequestJsonWithXsource(String payloadJson, String resource, String personId, String xSource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .header(X_SOURCE, xSource)
                .body(payloadJson)
                .when()
                .post(resource, personId);
    }

    public Response doPostRequestWithXsource(String resource, String personId, String xSource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .header(X_SOURCE, xSource)
                .when()
                .post(resource, personId);
    }

    public Response doPostRequestOneAttributeAndBody(String resource, String param, String body) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(resource, param);
    }

    public Response doPutRequestJson(String payloadJson, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .body(payloadJson)
                .when()
                .put(resource);
    }

    public Response doPutRequestOneAttribute(String resource, String param) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .when()
                .put(resource, param);
    }

    public Response doPutRequestOneAttributeAndBody(String resource, String param, String body) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(resource, param);
    }

    public Response doPostRequestQuery(Map<String, Object> queryParams, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .queryParams(queryParams)
                .when()
                .post(resource);
    }

    public Response doGetRequestQuery(Map<String, Object> payloadJson, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .queryParams(payloadJson)
                .when()
                .get(resource);
    }

    public Response doGetRequestQueryAndBody(Map<String, Object> payloadJson, String resource, String param) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .queryParams(payloadJson)
                .when()
                .put(resource, param);
    }

    public Response doGetRequestJson(String payloadJson, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .body(payloadJson)
                .when()
                .get(resource);
    }

    public Response doDeleteRequest(String resource, String param) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .when()
                .delete(resource, param);
    }

    public Response doDeleteRequestWithListParams(Map params, String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .params(params)
                .when()
                .delete(resource);
    }

    public String getAttributeFromPostRequest(String payloadJson, String resource, String attribute) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .body(payloadJson)
                .when()
                .post(resource)
                .then()
                .extract().path(attribute);
    }

    public String getAttributeNotAuthen(Map payloadMap, String attribute) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .formParams(payloadMap)
                .when()
                .post()
                .then()
                .extract().path(attribute);
    }

    public String getAttributeNotAuthen(Map payloadMap, String resource, String attribute) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .formParams(payloadMap)
                .when()
                .post(resource)
                .then()
                .extract().path(attribute);
    }

    public Response doPostRequestNoPayload(String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .when()
                .post(resource);
    }

    public Response doPostRequestOneAttribute(String payloadJson, String resource, String param) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .body(payloadJson)
                .when()
                .post(resource, param);
    }

    public Response doGetWithoutParameters(String resource) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .when()
                .get(resource);
    }

    public Response doPostRequestJsonWithHeaderParam(String payloadJson, String resource, Map params) {
        return given()
                .spec(RequestManager.shared().getRequest())
                .contentType(ContentType.JSON)
                .headers(params)
                .body(payloadJson)
                .when()
                .post(resource);
    }
}