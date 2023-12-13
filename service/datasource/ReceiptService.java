package service.datasource;

import base.RequestManager;
import base.util.DockClientUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import service.BaseService;

import java.io.IOException;

public class ReceiptService extends BaseService {
    private DockClientUtil dockClientUtil;
    private int idAccount;
    private int idAdjustment;
    private String merchantId;
    private String orderId;
    private int amount;
    private String voucherCode;
    private String description;
    private String transactionDate;
    private String transactionStatus;
    private String transactionCode;

    public ReceiptService(String transStatus) throws IOException {
        propertiesUtil = new PropertiesUtil();
        dockClientUtil = new DockClientUtil();
        RequestManager.shared().setRequest(dockClientUtil.getAuthenticateToTheDockAPI());
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("dock.voucher.client.api.baseURI"));
        setReceipt(transStatus);
    }

    public int getIdAccount() {
        return idAccount;
    }

    public int getIdAdjustment() {
        return idAdjustment;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getAmount() {
        return amount;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public String getDescription() {
        return description;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setReceipt(String transStatus) {
        int i = 0;
        int pageNumber = 0;
        boolean flag = true;

        do {
            Response response = doGetRequestWthPagination("/v1/receipts", i);
            JSONObject jsonBody = new JSONObject(response.getBody().asString());
            pageNumber = (int) jsonBody.get("totalPages");
            JSONArray userArray = jsonBody.getJSONArray("items");

            for (int j = 0; j < userArray.length(); j++) {
                JSONObject jsonObject = userArray.getJSONObject(j);
                String status = jsonObject.getString("transactionStatus");

                if (status.equalsIgnoreCase(transStatus)) {
                    this.idAccount = jsonObject.getInt("idAccount");
                    this.idAdjustment = jsonObject.getInt("idAdjustment");
                    this.merchantId = jsonObject.getString("merchantId");
                    this.orderId = jsonObject.getString("orderId");
                    this.amount = jsonObject.getInt("amount");
                    this.voucherCode = jsonObject.getString("voucherCode");
                    this.description = jsonObject.getString("description");
                    this.transactionDate = jsonObject.getString("transactionDate");
                    this.transactionStatus = status;
                    this.transactionCode = jsonObject.getString("transactionCode");
                    flag = false;
                    break;
                }
            }

            i++;
        } while (i < pageNumber && flag);

        if (flag) {
            throw new RuntimeException("Erro na Captura de dados para o teste!"
                    + "Não foi encontrado  as informações do recibo pesquisado na 'API v1/receipts'");
        }
    }
}
