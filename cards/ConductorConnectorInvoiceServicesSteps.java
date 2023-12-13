package steps.cards;

import base.RequestManager;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;
import service.ConductorConectorService;

import java.io.IOException;
import java.util.List;

public class ConductorConnectorInvoiceServicesSteps extends ConductorConectorService {

    private static final String INVOICE_DUE_DATE_WITH_INSTALLMENT_PLAN = "2021-06-10";
    private static final String NONEXISTENT_EXTERNAL_ACCOUNT_ID = "9878787878";
    String jsonFirstPropertyName = "first";
    String externalAccountId;
    private final ConductorConectorService conductorConectorService;

    public ConductorConnectorInvoiceServicesSteps() throws IOException {
        conductorConectorService = new ConductorConectorService();
        externalAccountId = propertiesUtil.getPropertyByName("conductor.external.account.id");
    }

    @Quando("executo a requisicao para consultar a fatura com status de "
             + "{string}, {string}, {string} e {string} para a conta valida existente")
    public void executoARequis(String processingSituation, String limit, String page, String sort) {
        RequestManager.shared().setResponse(conductorConectorService.getInvoice(
                externalAccountId, limit, page, processingSituation, sort));
    }

    @Quando("executo a requisicao para consultar a fatura com status de "
            + "{string}, {string}, {string} e {string} para a conta valida inexistente")
    public void executoARequisicaoPar(String processingSituation, String limit, String page, String sort) {
        RequestManager.shared().setResponse(conductorConectorService.getInvoice(
                NONEXISTENT_EXTERNAL_ACCOUNT_ID, limit, page, processingSituation, sort));
    }

    @Quando("executo a requisicao para consultar a fatura futura com {string}, "
            + "{string} e {string} para a conta valida existente")
    public void executoARequisicaoPaValidaExistente(String limit, String page, String sort) {
        RequestManager.shared().setResponse(conductorConectorService
                .getFutureIvoiceByAccountId(externalAccountId, limit, page, sort));
    }

    @Quando("executo a requisicao para consultar a fatura futura com {string}, "
            + "{string} e {string} para a conta valida inexistente")
    public void executoARequisicaoParaConsulnte(String limit, String page, String sort) {
        RequestManager.shared().setResponse(conductorConectorService
                .getFutureIvoiceByAccountId(NONEXISTENT_EXTERNAL_ACCOUNT_ID, limit, page, sort));
    }

    @Entao("valido que retornou os resultados da pagina {string} correto")
    public void validoQueRetornouOsResultadosDaPaginaCorreto(String page) {
        String responsePage = RequestManager.shared().getResponse().jsonPath().getString("number");
        Assert.assertEquals(page, responsePage);
    }

    @Quando("executo a requisicao para consultar fatura fechada para a conta valida")
    public void executoARequisicaoParaConsultarFaturaFechadaParaAConta() {
        RequestManager.shared().setResponse(conductorConectorService.getInvoiceClosedByAccountId(externalAccountId));
    }

    @Quando("executo a requisicao para consultar fatura fechada para a conta {string} inexistente")
    @Quando("executo a requisicao para consultar fatura fechada para a conta {string} invalida")
    public void executoARequisicaoParaConsultarFaturaFechadaParaAContaInexistente(String externalAccountId) {
        RequestManager.shared().setResponse(conductorConectorService.getInvoiceClosedByAccountId(externalAccountId));
    }

    @Quando("executo a requisicao para consultar a primeira pagina de fatura fechada para a conta valida")
    public void executoARequisicaoParaConsultarAPrimeiraPaginaDeFaturaFechadaParaAConta() {
        RequestManager.shared().setResponse(conductorConectorService.getInvoiceClosedByPage(externalAccountId, 0));
    }

    @Quando("executo a requisicao para consultar a segunda pagina de fatura fechada para a conta valida")
    public void executoARequisicaoParaConsultarASegundaPaginaDeFaturaFechadaParaAConta() {
        RequestManager.shared().setResponse(conductorConectorService.getInvoiceClosedByPage(externalAccountId, 1));
    }

    @Quando("executo a requisicao para consultar {int} registro de fatura fechada para a conta valida")
    public void executoARequisicaoParaConsultarRegistroDeFaturaFechadaParaAConta(int limite) {
        RequestManager.shared().setResponse(conductorConectorService
                .getInvoiceClosedUsingQueryParam(externalAccountId, limite));
    }

    @Quando("executo a requisicao para consultar fatura aberta para a conta valida")
    public void executoARequisicaoParaConsultarFaturaAbertaParaAConta() {
        RequestManager.shared().setResponse(conductorConectorService.getInvoiceOpenedByAccountId(externalAccountId));
    }

    @Quando("executo a requisicao para consultar fatura aberta para a conta {string} sem lancamentos")
    @Quando("executo a requisicao para consultar fatura aberta para a conta {string} inexistente")
    public void executoARequisicaoParaConsultarFaturaAbertaParaAContaSemLancamentos(String externalAccountId) {
        RequestManager.shared().setResponse(conductorConectorService.getInvoiceOpenedByAccountId(externalAccountId));
    }

    @Entao("deve retornar resultados da primeira página")
    public void deveRetornarResultadosDaPrimeiraPagina() {
        Assert.assertEquals("true", conductorConectorService.getJsonProperty(jsonFirstPropertyName));
    }

    @Entao("deve retornar resultados da segunda página")
    public void deveRetornarResultadosDaSegundaPagina() {
        Assert.assertEquals("false", conductorConectorService.getJsonProperty(jsonFirstPropertyName));
    }

    @Entao("deve retornar apenas {int} registro de fatura fechada")
    public void deveRetornarApenasRegistroDeFaturaFechada(int limite) {
        Assert.assertEquals(conductorConectorService.getJsonProperty("size"), String.valueOf(limite));
        Assert.assertEquals(conductorConectorService.getSizePropertyByName("content.accountId"), limite);
    }

    @Quando("executo a requisicao para consultar o parcelamento ativo para uma fatura")
    public void executoARequisicaoParaConsultarOParcelamentoAtivoParaUmaFatura() {
        RequestManager.shared().setResponse(conductorConectorService
                .getInvoiceInstallmentePlans(externalAccountId, INVOICE_DUE_DATE_WITH_INSTALLMENT_PLAN));
    }

    @Entao("valido que retornou a lista de parcelamento da fatura")
    public void validoQueRetornouAListaDeParcelamentoDaFatura() {
        List<String> listInstallmentPlans = RequestManager.shared().getResponse().body().jsonPath().getList("content");
        Assert.assertTrue(listInstallmentPlans.size() > 0);
    }

    @Quando("executo a requisicao para consultar o parcelamento ativo para uma fatura com conta inexistente")
    public void executoARequisicaoParaConsultarOParcelamentoAtivoParaUmaFaturaComContaInexistente() {
        RequestManager.shared().setResponse(conductorConectorService
                .getInvoiceInstallmentePlans(NONEXISTENT_EXTERNAL_ACCOUNT_ID, INVOICE_DUE_DATE_WITH_INSTALLMENT_PLAN));
    }

    @Quando("executo a requisicao para consultar o pdf da ultima fatura fechada")
    public void executoARequisicaoParaConsultarOPdfDaFatura() {
        RequestManager.shared().setResponse(conductorConectorService.getInvoiceClosedByAccountId(externalAccountId));
        String invoiceClosedDueDate = RequestManager.shared().getResponse().body().jsonPath().getString("content.invoiceDueDate[0]");
        RequestManager.shared().setResponse(conductorConectorService.getInvoicePDF(invoiceClosedDueDate, externalAccountId));
    }
}
