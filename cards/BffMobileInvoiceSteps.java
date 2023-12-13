package steps.cards;

import base.RequestManager;
import base.util.MathUtil;
import base.util.StringUtil;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;
import service.OrbiBffMobileCardService;
import java.io.IOException;
import java.util.List;

public class BffMobileInvoiceSteps {
    private static final String LAST_CLOSED_INVOICE_DUE_DATE = "lastClosedInvoice.invoiceDueDate";
    private static final String INVOICE_DUE_DATE = "2030-09-20";
    private static final String INVOICE_INSTALLMENT_ACTIVE_DUE_DATE = "2022-06-03";
    private static final String BILLET_ID_INVALID = "99787141";
    private static final String PROCESSING_SITUATION_INVOICES = "invoices.2022.processingSituation";

    OrbiBffMobileCardService orbiBffCardService;
    String lastClosedInvoiceDueDate;
    String openedInvoiceDueDate;
    String billetId;

    public BffMobileInvoiceSteps() throws IOException {
        orbiBffCardService = new OrbiBffMobileCardService();
    }

    @Quando("executo a requisicao para consultar a data da fatura fechada")
    public void executoARequisicaoParaConsultarADataDaFaturaFechada() {
        RequestManager.shared().setResponse(orbiBffCardService.getCardsHome());
        lastClosedInvoiceDueDate = RequestManager.shared().getResponse()
                .jsonPath().get(LAST_CLOSED_INVOICE_DUE_DATE);
    }

    @Quando("executo a requisicao para consultar a data da fatura aberta")
    public void executoARequisicaoParaConsultarADataDaFaturaAberta() {
        RequestManager.shared().setResponse(orbiBffCardService.getCardsHome());
        openedInvoiceDueDate = RequestManager.shared().getResponse()
                .jsonPath().get("openedInvoice.invoiceDueDate");
    }

    @Quando("executo a requisicao para consultar a fatura fechada informando a data de vencimento")
    public void executoARequisicaoParaConsultarAFaturaFechadaInformandoADataDeVencimento() {
        RequestManager.shared().setResponse(orbiBffCardService.getInvoiceByDueDate(lastClosedInvoiceDueDate));
    }

    @Quando("executo a requisicao para consultar a fatura aberta informando a data de vencimento")
    public void executoARequisicaoParaConsultarAFaturaAbertaInformandoADataDeVencimento() {
        RequestManager.shared().setResponse(orbiBffCardService.getInvoiceByDueDate(openedInvoiceDueDate));
    }

    @Quando("com a data da fatura fechada consulto os tipos de pagamento")
    public void comADataDaFaturaFechadaConsultoOsTiposDePag() {
        RequestManager.shared().setResponse(orbiBffCardService
                .getInvoiceSummaryAndPaymentTypes(lastClosedInvoiceDueDate));
    }

    @Quando("com a data da fatura fechada inexistente consulto os tipos de pagamento")
    public void comADataDaFaturaFechadaInexistenteConsultoOsTiposDePagamento() {
        RequestManager.shared().setResponse(orbiBffCardService.getInvoiceSummaryAndPaymentTypes("2050-01-05"));
    }

    @Quando("executo a requisicao para consultar a data da fatura fechada e id do boleto")
    public void executoARequisicaoParaConsultarADataDaFaturaFechadaEIdDoBoleto() {
        RequestManager.shared().setResponse(orbiBffCardService.getCardsHome());
        lastClosedInvoiceDueDate = RequestManager.shared().getResponse()
                .jsonPath().get(LAST_CLOSED_INVOICE_DUE_DATE);
        billetId = RequestManager.shared().getResponse()
                .jsonPath().getString("lastClosedInvoice.billetId");
    }

    @Quando("com a data da fatura fechada e id do boleto consulto o resumo da fatura")
    public void comADataDaFaturaFechadaEIdDoBoletoConsultoOResumoDaFatura() {
        RequestManager.shared().setResponse(orbiBffCardService
                .getInvoiceSummaryByBilletId(lastClosedInvoiceDueDate, billetId));
    }

    @Quando("com a data da fatura fechada inexistente e id do boleto inexistente consulto o resumo da fatura")
    public void comADataDaFaturaFechadaInexistenteEIdDoBoletoInexistenteConsultoOResumoDaFatura() {
        RequestManager.shared().setResponse(orbiBffCardService
                .getInvoiceSummaryByBilletId(INVOICE_DUE_DATE, BILLET_ID_INVALID));
    }

    @Quando("com a data da fatura fechada inexistente e id do boleto consulto o resumo da fatura")
    public void comADataDaFaturaFechadaInexistenteEIdDoBoletoConsultoOResumoDaFatura() {
        RequestManager.shared().setResponse(orbiBffCardService.getInvoiceSummaryByBilletId(INVOICE_DUE_DATE, billetId));
    }

    @Quando("com a data da fatura fechada e id do boleto inexistente consulto o resumo da fatura")
    public void comADataDaFaturaFechadaEIdDoBoletoInexi() {
        RequestManager.shared().setResponse(orbiBffCardService
                .getInvoiceSummaryByBilletId(lastClosedInvoiceDueDate, BILLET_ID_INVALID));
    }

    @Quando("executo a requisicao para consultar fatura futura")
    public void executoARequisicaoParaConsultarFaturaFutura() {
        RequestManager.shared().setResponse(orbiBffCardService.getFutureInvoice());
    }

    @Quando("executo a requisicao para consultar os planos de parcelamento do tipo campanha "
            + "para uma fatura que possua parcelamento ativo")
    public void executoRequisicaoConsultarOsPlanosDeParcelamentoDTipoCampanhaParaUmaFaturaQPossuaParcelamentoAtivo() {
        String invoiceDueDate = INVOICE_INSTALLMENT_ACTIVE_DUE_DATE;
        RequestManager.shared().setResponse(orbiBffCardService.getIinstallmentPlans(invoiceDueDate));
    }

    @Entao("valido que retornou a lista de parcelas da fatura")
    public void validoQueRetornouAListaDeParcelasDaFatura() {
        Assert.assertTrue(RequestManager.shared().getResponse().body().jsonPath().getList("installments").size() > 0);
    }

    @Quando("na api bff-mobile executo a requisicao para consultar o historico de faturas de uma conta valida")
    public void naApiBffMobileExecutoARequisicaoParaConsultarOHistoricoDeFaturasDeUmaContaValida() {
        RequestManager.shared().setResponse(orbiBffCardService.getHistoryInvoice());
        RequestManager.shared().getResponse().body()
                .jsonPath().getString(PROCESSING_SITUATION_INVOICES + MathUtil.ZERO);
    }

    @Entao("no bff-mobile valido que retornou todas as faturas na ordem correta")
    public void noBffMobileValidoQueRetornouTodasAsFaturasNaOrdemCorreta() {
        RequestManager.shared().setResponse(orbiBffCardService.getHistoryInvoice());
        List<String> processingSituation = RequestManager.shared().getResponse()
                .jsonPath().getList(PROCESSING_SITUATION_INVOICES);
        Assert.assertEquals("OPENED", processingSituation.get(MathUtil.ZERO));
        Assert.assertEquals("CLOSED", processingSituation.get(MathUtil.ONE));
    }

    @Quando("na api bff-mobile executo a requisicao para consultar o historico de faturas de uma conta valida "
            + "utilizando o filtro de data {string} e {string}")
    public void naApiBffMobileExecutoARequisicaoPConsultarHistoricoDFaturasDUmaContaValidaUtilizandoOFiltroDDtE(
                                                                                    String fromDate, String toDate) {
        RequestManager.shared().setResponse(orbiBffCardService.getHistoryInvoiceWithFilters(fromDate, toDate));
        RequestManager.shared().getResponse().body()
                .jsonPath().getString(PROCESSING_SITUATION_INVOICES + MathUtil.ZERO);
    }

    @Entao("no bff-mobile valido que as faturas que retornaram estao dentro do periodo "
            + "de data {string} e {string} pesquisado")
    public void noBffMobileValidoQFaturasQRetornaramEstaoDentroDPeriodoDDtEPesquisado(String fromDate, String toDate) {
        List<String> toDateFromResponseBody = RequestManager.shared().getResponse()
                .jsonPath().getList("invoices.2022.invoiceDueDate");
        StringUtil stringUtil = new StringUtil();
        //Passando indexStart=7 e indexEnd=10 para remover a '-13' do valor do campo invoiceDueDate, Ex: 2022-01-13
        String toDateFormatedWithoutDay = stringUtil
                .deletePartOfString(toDateFromResponseBody.get(MathUtil.ZERO), MathUtil.SEVEN, MathUtil.TEN);
        Assert.assertEquals(toDateFormatedWithoutDay, toDate);
        String fromDateWithoutDay = stringUtil.deletePartOfString(toDateFromResponseBody
                .get(toDateFromResponseBody.size() - MathUtil.ONE), MathUtil.SEVEN, MathUtil.TEN);
        Assert.assertEquals(fromDateWithoutDay, fromDate);
    }

    @Quando("na api bff-mobile executo a requisicao para consultar o pdf da ultima fatura fechada")
    public void naApiBffMobileExecutoARequisicaoParaConsultarOPdfDaUltimaFaturaFechada() {
//        RequestManager.shared().setResponse(orbiBffCardService.getCardsHome());
//        lastClosedInvoiceDueDate = RequestManager.shared().getResponse()
//                .jsonPath().get(LAST_CLOSED_INVOICE_DUE_DATE);

//      Necessario passar id fixo pois o cliente possui somente essa fatura parcelada ativa
        billetId = "93755015";
        RequestManager.shared().setResponse(orbiBffCardService.getBilletPdfById(billetId));
    }
}
